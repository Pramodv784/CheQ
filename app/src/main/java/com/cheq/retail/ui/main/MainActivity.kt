package com.cheq.retail.ui.main


import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.cheq.cache.sharedprefs.SharedPrefs
import com.cheq.cache.sharedprefs.SharedPrefsCheQ
import com.cheq.cache.sharedprefs.SharedPrefsCheQUser
import com.cheq.cache.sharedprefs.SharedPrefsConstants
import com.cheq.common.extension.orFalse
import com.cheq.common.firebase.CheqFirebase
import com.cheq.common.utils.FirebaseConstants
import com.cheq.navigation.IntentKey
import com.cheq.navigation.IntentProvider
import com.cheq.retail.R
import com.cheq.retail.api.EncryptionProvider
import com.cheq.retail.api.RestClient
import com.cheq.retail.application.MainApplication
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.constants.AFConstants
import com.cheq.retail.constants.AppsFlyEvents
import com.cheq.retail.constants.Constant.Companion.FIREBASE_DATA
import com.cheq.retail.constants.Constant.Companion.FIREBASE_DOWN_TIME_REF
import com.cheq.retail.databinding.ActivityMainBinding
import com.cheq.retail.sharePreferences.SharePrefCheqUserId
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.ui.billPayments.PaymentMethodsActivity.SupportedUPIApps.Companion.PAYMENT_METHOD
import com.cheq.retail.ui.dashboard.view.fragment.HomeFragment.Companion.HOME
import com.cheq.retail.ui.deeplinkHandler.DeepLinkHandler
import com.cheq.retail.ui.deeplinkHandler.Pages
import com.cheq.retail.ui.downTime.DownTimeActvity
import com.cheq.retail.ui.loans.LoanSuccessActivity.Companion.LOAN_ADD
import com.cheq.retail.ui.main.fragment.BottomVisibility
import com.cheq.retail.ui.main.fragment.ProductFragment
import com.cheq.retail.ui.main.model.SelectOfferResponseItem
import com.cheq.retail.ui.main.viewModel.ProductViewModel
import com.cheq.retail.ui.referral.viewmodel.ReferralViewModel
import com.cheq.retail.ui.referral.viewmodel.ReferralVmFactory
import com.cheq.retail.ui.rewards.view.RewardsFragment
import com.cheq.retail.utils.*
import com.freshchat.consumer.sdk.Freshchat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.*
import com.instabug.library.Instabug
import com.smsapi.ProcessSMS
import com.smsapi.ResponseListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.Executor
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity(), BottomVisibility {
    lateinit var mBinding: ActivityMainBinding
    var navController: NavController? = null
    lateinit var referalViewModel: ReferralViewModel
    val cheqUserId = SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
        ?.getString(SharePrefsKeys.CHEQ_USER_ID)
    private val navHostFragmentDelegate: NavHostFragment
        get() = supportFragmentManager.findFragmentById(
            R.id.main_nav_host
        ) as NavHostFragment
    lateinit var viewModel: ProductViewModel
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var executor: Executor
    private lateinit var callBack: BiometricPrompt.AuthenticationCallback
    private var keyguardManager: KeyguardManager? = null
    var comingFrom = ""
    private var doubleBackToExitPressedOnce = false
    val TAG = MainActivity::class.java.simpleName


    @Inject
    lateinit var firebase: CheqFirebase

    @Inject
    @SharedPrefsCheQUser
    lateinit var sharePrefsUser: SharedPrefs

    @Inject
    @SharedPrefsCheQ
    lateinit var sharePrefs: SharedPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ProductViewModel::class.java]
        setupUI()

        // setupBiometricAuth()
        catchIntent()

        // createFreshChatUser()

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            initialFinart()
        }
        checkForDeeplink()
        initFirebaseData()
        getCheqSafe()

        checkShortLink()
    }

    private fun getCheqSafe() {
        lifecycleScope.launch(Dispatchers.IO) {
            val response = firebase.getValue(FirebaseConstants.CHEQ_SAFE_REFERENCE, CheqSafeBannerResponse::class.java)
            response.onSuccess {
                val isVisible = it?.cheqsafe_visibility.orFalse
                        && it?.feature_config_setting?.rule?.platform?.android == true
                        && checkInWhiteList(it.feature_config_setting.rule.whitelist)
                sharePrefs.putBoolean(
                    SharedPrefsConstants.IS_CHEQ_SAFE_VISIBLE,
                    isVisible
                )
            }.onFailure {
                FirebaseCrashlytics.getInstance().log("Error in getting firebase data about cheq safe: $it")
            }
        }
    }

    private fun checkInWhiteList(whitelist: WhitelistUser?): Boolean {
        return if (whitelist?.enabled.orFalse) {
            val cheqUserId = sharePrefsUser.getString(SharedPrefsConstants.CHEQ_USER_ID)
            whitelist?.whitelist_users?.contains(cheqUserId).orFalse
        }else {
            true
        }
    }

    private fun initFirebaseData() {
        var _data: ConfigDataResponse? = null
        val mFirebaseDatabase: DatabaseReference?
        val mFirebaseInstance: FirebaseDatabase?
        mFirebaseInstance =
            FirebaseDatabase.getInstance(com.cheq.retail.BuildConfig.firebaseDataBaseURL)
        mFirebaseDatabase = mFirebaseInstance.getReference(FIREBASE_DOWN_TIME_REF)
        mFirebaseDatabase.keepSynced(true)
        val rateListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _data = snapshot.getValue(ConfigDataResponse::class.java)
                if (_data?.isenabled == true) {
                    startActivity(
                        Intent(
                            MainApplication.getApplicationContext(),
                            DownTimeActvity::class.java
                        ).putExtra(FIREBASE_DATA, _data)
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        }
        mFirebaseDatabase.addValueEventListener(rateListener)
    }


    private fun checkForDeeplink() {

        val deeplinkValue = DeepLinkHandler.getDeeplinkIntentValue(this)
        if (deeplinkValue == AFConstants.SUPPORT) {
            Freshchat.showConversations(applicationContext)

        } else {
            var intent = DeepLinkHandler.getDeeplinkIntent(this, deeplinkValue ?: "")

            val className = intent?.component?.className

            if (this.javaClass.name == className) {
                // this is the current activity
                val deeplinkTab = intent?.getIntExtra(TAB_EXTRA, -1) ?: -1
                if (deeplinkTab > 0) {
                    (supportFragmentManager.findFragmentById(R.id.main_nav_host) as? NavHostFragment)
                        ?.navController
                        ?.navigate(
                            deeplinkTab
                        )
                }

            } else {
                intent?.let {
                    startActivity(it)
                }

            }
        }

    }


    private fun initialFinart() {
        val cheqUserId =
            SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                ?.getString(SharePrefsKeys.CHEQ_USER_ID)
        cheqUserId?.let { chequser ->
            AppsFlyEvents.setCurrentUserForAF(MainApplication.getApplicationContext(), chequser)
            MparticleUtils.setCurrentUser(MainApplication.getApplicationContext(), chequser)
        }
    }


    private fun catchIntent() {

        if (intent != null && intent.getStringExtra("COMING_FROM") != null) {
            println("COMING_FROM")
            navController?.navigate(R.id.nav_rewards_fragment)
        }

        if (intent != null && intent.getStringExtra("COMING_FROM_BILL") != null) {
            println("COMING_FROM")
            navController?.navigate(R.id.nav_rewards_fragment)
        }

        if (intent != null && intent.getStringExtra(PAYMENT_METHOD) != null) {
            //println("COMING_FROM")
            navController?.navigate(R.id.nav_pay_fragment)
        }

        if (intent != null && intent.getStringExtra(LOAN_ADD) != null) {
            navController?.navigate(R.id.nav_products_fragment)
        }


    }

    private var fbRewardsRateList: List<SelectOfferResponseItem>? = null

    //private var fbRewardsRateList: Array<SelectOfferResponseItem>?-> Unit)? = null
    fun getRewardsList(): List<SelectOfferResponseItem>? {
        return fbRewardsRateList
    }

    private fun setupUI() {
        //  Utils.initFirebase(this)
        /* Utils.itemList = {
             fbRewardsRateList = it
         }*/
        Utils.initFirebase(this)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        Instabug.setUserAttribute(
            "Mobile",
            SharePrefs.getInstance(MainApplication.getApplicationContext())
                ?.getString("MOBILE_NUMBER").toString()
        )
        Instabug.setUserAttribute(
            "cheq_user_id ",
            SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                ?.getString(SharePrefsKeys.CHEQ_USER_ID).toString()
        )
        println(
            "cheeeee ${
                SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                    ?.getString(SharePrefsKeys.CHEQ_USER_ID).toString()
            }"
        )
        Instabug.setUserAttribute(
            "user_name ",
            SharePrefs.getInstance(MainApplication.getApplicationContext())
                ?.getString(SharePrefsKeys.FIRST_NAME)
                .toString() + " " + SharePrefs.getInstance(
                MainApplication.getApplicationContext()
            )?.getString(SharePrefsKeys.LAST_NAME).toString()
        )


        Utils.setLightStatusBar(this)
        navController =
            (supportFragmentManager.findFragmentById(R.id.main_nav_host) as NavHostFragment?)?.navController

        mBinding.llPay.setOnClickListener {
            navController?.navigate(R.id.nav_pay_fragment)
        }

        mBinding.llProduct.setOnClickListener {
            navController?.navigate(R.id.nav_products_fragment)
        }
        mBinding.llRewards.setOnClickListener {
            navController?.navigate(R.id.nav_rewards_fragment)
        }

        mBinding.llRewards.setOnClickListener {
            navController?.navigate(R.id.nav_rewards_fragment)
        }

        mBinding.llSettings.setOnClickListener {
            // startActivity(Intent(this, TransactionHistoryActivity::class.java))
            navController?.navigate(R.id.nav_menu_fragment)
        }
        viewModel.responsComeFromeObserver.observeForever {
            if (it == true) {
                navController?.navigate(R.id.nav_rewards_fragment)
            }
        }

        viewModel.finartObserver.observe(this) {

        }


    }

    fun setBottomTab(type: Int) {

        mBinding.tvRewards.setTextColor(ContextCompat.getColor(this, R.color.colorTextGrey))

        mBinding.tvHome.setTextColor(ContextCompat.getColor(this, R.color.colorTextGrey))

        mBinding.tvProduct.setTextColor(ContextCompat.getColor(this, R.color.colorTextGrey))
        mBinding.tvSettings.setTextColor(ContextCompat.getColor(this, R.color.colorTextGrey))
        mBinding.ivSettings.setColorFilter(ContextCompat.getColor(this, R.color.colorTextGrey))
        if (type == 1) {
            mBinding.ivRewards.setAnimation(R.raw.reward_active)
            mBinding.ivRewards.playAnimation()
            mBinding.ivHome.setAnimation(R.raw.home_deactive)
            mBinding.ivProduct.setAnimation(R.raw.product_deactive)
            mBinding.tvRewards.setTextColor(
                ContextCompat.getColor(
                    this, R.color.black
                )
            )

            ////                mBinding.tvRewards.setTextColor(
////                    ContextCompat.getColor(
////                        this,
////                        R.color.colorPrimaryGreen
////                    )
////                )
        }
        if (type == 2) {
            mBinding.ivRewards.setAnimation(R.raw.reward_deactive)
            mBinding.ivHome.setAnimation(R.raw.home_active)
            mBinding.ivHome.playAnimation()

            mBinding.ivProduct.setAnimation(R.raw.product_deactive)
            mBinding.tvHome.setTextColor(
                ContextCompat.getColor(
                    this, R.color.black
                )
            )
        }
        if (type == 3) {
            mBinding.ivRewards.setAnimation(R.raw.reward_deactive)
            mBinding.ivHome.setAnimation(R.raw.home_deactive)
            mBinding.ivProduct.setAnimation(R.raw.product_active)
            mBinding.ivProduct.playAnimation()
            mBinding.tvProduct.setTextColor(
                ContextCompat.getColor(
                    this, R.color.black
                )
            )

        }

//        when (type) {
//            1 -> {
////                mBinding.ivRewards.setColorFilter(
////                    ContextCompat.getColor(
////                        this,
////                        R.color.colorPrimaryGreen
////                    )
////                )
////                mBinding.tvRewards.setTextColor(
////                    ContextCompat.getColor(
////                        this,
////                        R.color.colorPrimaryGreen
////                    )
////                )
//                mBinding.ivRewards.setAnimation(R.raw.reward_active)
//                mBinding.ivHome.setAnimation(R.raw.home_deactive)
//                mBinding.ivProduct.setAnimation(R.raw.product_deactive)
//
//            }
//            2 -> {
////                mBinding.ivHome.setColorFilter(
////                    ContextCompat.getColor(
////                        this,
////                        R.color.colorPrimaryGreen
////                    )
////                )
////                mBinding.tvHome.setTextColor(
////                    ContextCompat.getColor(
////                        this,
////                        R.color.colorPrimaryGreen
////                    )
////                )
//                mBinding.ivRewards.setAnimation(R.raw.reward_deactive)
//                mBinding.ivHome.setAnimation(R.raw.home_active)
//                mBinding.ivProduct.setAnimation(R.raw.product_deactive)
//
//            }
//            3 -> {
////                mBinding.ivProduct.setColorFilter(
////                    ContextCompat.getColor(
////                        this,
////                        R.color.colorPrimaryGreen
////                    )
////                )
////                mBinding.tvProduct.setTextColor(
////                    ContextCompat.getColor(
////                        this,
////                        R.color.colorPrimaryGreen
////                    )
////                )
//                mBinding.ivRewards.setAnimation(R.raw.reward_deactive)
//                mBinding.ivHome.setAnimation(R.raw.home_deactive)
//                mBinding.ivProduct.setAnimation(R.raw.product_active)
//
//            }
//            4 -> {
//                mBinding.ivSettings.setColorFilter(
//                    ContextCompat.getColor(
//                        this,
//                        R.color.colorPrimaryGreen
//                    )
//                )
//                mBinding.tvSettings.setTextColor(
//                    ContextCompat.getColor(
//                        this,
//                        R.color.colorPrimaryGreen
//                    )
//                )
//            }
//        }

    }

    fun setBtnVisibility(amount: Double, isShowFull: Boolean, errorMessage: String) {
        if (isShowFull) {
            mBinding.flPayTogether.visibility = View.VISIBLE
            mBinding.llError.visibility = View.GONE
            mBinding.btnPayTogether.visibility = View.VISIBLE
        } else {
            mBinding.flPayTogether.visibility = View.VISIBLE
            mBinding.llError.visibility = View.VISIBLE
            mBinding.tvErrorText.text = errorMessage
            mBinding.btnPayTogether.visibility = View.GONE
        }

    }

    fun setBtnVisibilityFull(isFullGone: Boolean) {
        if (isFullGone) {
            mBinding.flPayTogether.visibility = View.GONE
        } else {
            mBinding.flPayTogether.visibility = View.VISIBLE
        }
    }


    override fun onBackPressed() {
        val fragment = navHostFragmentDelegate.childFragmentManager.fragments[0] as Fragment

        val isOtherFragment = fragment is ProductFragment || fragment is RewardsFragment
        if (isOtherFragment) {
            navController?.navigate(R.id.nav_pay_fragment)
        } else {
            if (doubleBackToExitPressedOnce) {

                finishAffinity()

            }else{
                this.doubleBackToExitPressedOnce = true
                Toast.makeText(this, "Press BACK again to close the app", Toast.LENGTH_SHORT).show()

            }


        }

    }

    private fun setupBiometricAuth() {
        executor = ContextCompat.getMainExecutor(applicationContext)

        callBack = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                SharePrefs.getInstance(MainApplication.getApplicationContext())!!
                    .putBoolean(SharePrefsKeys.IS_UNLOCKED, true)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                finishAffinity()
                Toast.makeText(this@MainActivity, errString, Toast.LENGTH_SHORT).show()
            }
        }

        biometricPrompt = BiometricPrompt(this, executor, callBack)

        if (SharePrefs.instance?.getBoolean(SharePrefsKeys.IS_UNLOCKED) == false && checkDeviceCanAuthenticateWithBiometrics()) {
            authenticateWithBiometrics()
        }
    }

    private fun checkDeviceCanAuthenticateWithBiometrics(): Boolean {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                return true
            }
//            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
//                Toast.makeText(
//                    this,
//                    getString(R.string.message_no_support_biometrics),
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
//                Toast.makeText(
//                    this,
//                    getString(R.string.message_no_hardware_available),
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
//                checkAPILevelAndProceed()
//            }
//            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
//                Toast.makeText(
//                    this,
//                    getString(R.string.error_security_update_required),
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
//                Toast.makeText(this, getString(R.string.error_unknown), Toast.LENGTH_LONG).show()
//            }
//            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
//                Toast.makeText(this, getString(R.string.error_unknown), Toast.LENGTH_LONG).show()
//            }
        }
        return false
    }

    fun setPayScreen(index: Int, payToggle: Boolean) {
        if (index == 0) {
            viewModel.updatePayToGatherToggleStatus(payToggle)
            var bundle = Bundle()
            bundle.putBoolean("pay", payToggle)
            navController!!.navigate(R.id.nav_products_fragment, bundle)

        } else if (index == 1) {
            navController!!.navigate(R.id.nav_products_fragment)

        } else {
            navController!!.navigate(R.id.nav_rewards_fragment)
        }

        setBottomTab(index)
    }

    private fun biometricsEnrollIntent(): Intent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                putExtra(
                    Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                    BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL
                )
            }
        } else {
            TODO("VERSION.SDK_INT < R")
        }
    }

    private fun setUpDeviceLockInAPIBelow23Intent(): Intent {
        return Intent(Settings.ACTION_SECURITY_SETTINGS)
    }

    private fun checkAPILevelAndProceed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            startActivityForResult(
                setUpDeviceLockInAPIBelow23Intent(),
                RC_DEVICE_CREDENTIAL_ENROLL
            )
        } else {
            startActivityForResult(biometricsEnrollIntent(), RC_BIOMETRICS_ENROLL)
        }
    }

    private fun getErrorMessage(errorCode: Int): String {
        return when (errorCode) {
            BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                getString(R.string.message_user_app_authentication)
            }
            BiometricPrompt.ERROR_HW_UNAVAILABLE -> {
                getString(R.string.error_hw_unavailable)
            }
            BiometricPrompt.ERROR_UNABLE_TO_PROCESS -> {
                getString(R.string.error_unable_to_process)
            }
            BiometricPrompt.ERROR_TIMEOUT -> {
                getString(R.string.error_time_out)
            }
            BiometricPrompt.ERROR_NO_SPACE -> {
                getString(R.string.error_no_space)
            }
            BiometricPrompt.ERROR_CANCELED -> {
                getString(R.string.error_canceled)
            }
            BiometricPrompt.ERROR_LOCKOUT -> {
                getString(R.string.error_lockout)
            }
            BiometricPrompt.ERROR_VENDOR -> {
                getString(R.string.error_vendor)
            }
            BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> {
                getString(R.string.error_lockout_permanent)
            }
            BiometricPrompt.ERROR_USER_CANCELED -> {
                getString(R.string.error_user_canceled)
            }
            BiometricPrompt.ERROR_NO_BIOMETRICS -> {
                checkAPILevelAndProceed()
                getString(R.string.error_no_biometrics)
            }
            BiometricPrompt.ERROR_HW_NOT_PRESENT -> {
                getString(R.string.error_hw_not_present)
            }
            BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL -> {
                startActivityForResult(biometricsEnrollIntent(), RC_BIOMETRICS_ENROLL)
                getString(R.string.error_no_device_credentials)
            }
            BiometricPrompt.ERROR_SECURITY_UPDATE_REQUIRED -> {
                getString(R.string.error_security_update_required)
            }
            else -> {
                getString(R.string.error_unknown)
            }
        }
    }

    private fun checkShortLink() {
        val refUrl = SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
            ?.getString(SharePrefsKeys.REF_SHORT_URL)
        if (Utils.isNetworkAvailable(applicationContext) && refUrl.isNullOrEmpty()) {
            try {
                MainScope().launch {
                    val response =
                        RestClient.getInstance().referralApi.getShortReferralUrl(EncryptionProvider("abc"))
                    if (response.isSuccessful && response.body() != null) {
                        val referralUrl: String? = response.body()?.referralUrl;
                        if (!referralUrl.isNullOrEmpty()) {
                            SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                                ?.putString(SharePrefsKeys.REF_SHORT_URL, referralUrl)
                        } else {
                            AppsFlyerLib.getInstance().setAppInviteOneLink(AFConstants.TEMPLATE_ID)
                            startAppsFlyer()
                        }

                    }
                }

            } catch (e: Exception) {
            }
        }
        initializeReferalObservables()
    }
    private fun startAppsFlyer() {

        AppsFlyerLib.getInstance()
            .start(this, AFConstants.AF_DEV_KEY, object : AppsFlyerRequestListener {
                override fun onSuccess() {
                    referalViewModel.generateRefLink(applicationContext)

                }

                override fun onError(errorCode: Int, errorDesc: String) {
                }
            })
    }
    private fun initializeReferalObservables() {
        val repository = (application as MainApplication).referralRepository
        referalViewModel = ViewModelProvider(
            this,
            ReferralVmFactory(repository, cheqUserId, application)
        )[ReferralViewModel::class.java]

        referalViewModel.genLinkLiveData.observe(this) {
            SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                ?.putString(SharePrefsKeys.REF_SHORT_URL, it)

        }
        referalViewModel.refStaticdata.observe(this) {
            when (it) {
                is NetworkResult.Loading -> {
                    Utils.showProgressDialog(this)
                }
                is NetworkResult.Success -> {
                    it.data?.let { it1 ->
                        SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                            ?.putString(SharePrefsKeys.REF_BE_URL, it1.whatsappMessage)
                    }
                    Utils.hideProgressDialog()
                }
                is NetworkResult.Error -> {
                    Utils.hideProgressDialog()
                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    private fun authenticateWithBiometrics() {

        val manufacturer = Build.MANUFACTURER.lowercase()

        val promptInfo = BiometricPrompt.PromptInfo.Builder().apply {
            setTitle("Confirm your phone screen lock pattern, PIN or password")
            setDescription("Unlock CheQ")
            if (manufacturer == "samsung" && Build.VERSION.SDK_INT < 30) {
                setNegativeButtonText("Cancel")
            } else {
                setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            }
        }.build()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager?.let { manager ->
                if (manager.isKeyguardSecure) {
                    biometricPrompt.authenticate(promptInfo)
                } else {
                    startActivityForResult(
                        setUpDeviceLockInAPIBelow23Intent(), RC_DEVICE_CREDENTIAL_ENROLL
                    )
                }
            }
        } else {
            biometricPrompt.authenticate(promptInfo)
        }
    }

    companion object {
        const val RC_BIOMETRICS_ENROLL = 10
        const val RC_DEVICE_CREDENTIAL_ENROLL = 18
        const val TAB_EXTRA = "TAB_EXTRA"

        fun startActivityForDeepLink(context: Context, tab: Int): Intent {
            return Intent(context, MainActivity::class.java).apply {
                putExtra(TAB_EXTRA, tab)
            }
        }

        val intentHelper = object: IntentProvider<IntentKey.MainActivityKey> {
            override fun createIntent(context: Context, key: IntentKey.MainActivityKey): Intent {
                return Intent(
                    context,
                    MainActivity::class.java
                )
            }
        }
    }

    override fun setVisibility(bottomVisiblity: Boolean) {

    }


    override fun onResume() {
        super.onResume()
        if (intent.getStringExtra("from") != null && intent.getStringExtra("from") == "credit") {
            setPayScreen(3, false)
            setBottomTab(3)
        }
    }
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val deeplinkTab = intent?.getIntExtra(TAB_EXTRA, -1) ?: -1
        if (deeplinkTab > 0) {
            (supportFragmentManager.findFragmentById(R.id.main_nav_host) as? NavHostFragment)
                ?.navController
                ?.navigate(
                    deeplinkTab
                )
        }
    }
}

