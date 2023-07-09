package com.cheq.retail.ui.splash

import android.Manifest
import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.cheq.navigation.IntentKey
import com.cheq.navigation.IntentProvider
import com.cheq.retail.R
import com.cheq.retail.application.MainApplication
import com.cheq.retail.application.MainApplication.Companion.finartProcessInstance
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.base.FirebaseRemoteConfigUtils
import com.cheq.retail.constants.AFConstants
import com.cheq.retail.constants.AFConstants.AF_CUST_FIRST_OPEN
import com.cheq.retail.constants.AFConstants.AF_SPLASH_EVENT
import com.cheq.retail.constants.AFConstants.AF_USER_OPENS
import com.cheq.retail.constants.AFConstants.CHEQ_USER
import com.cheq.retail.constants.AFConstants.FBEvent_SPLASH_EVENT
import com.cheq.retail.constants.AFConstants.FBEvent_USER_OPENS
import com.cheq.retail.constants.AFConstants.FB_CUST_FIRST_OPEN
import com.cheq.retail.constants.AFConstants.FINART_INVOKE
import com.cheq.retail.constants.AFConstants.FINART_SUCCESS_SPLASH
import com.cheq.retail.constants.AFConstants.FINART_TRIGGER_SPLASH
import com.cheq.retail.constants.AFConstants.SCREEN_SPLASH
import com.cheq.retail.constants.AFConstants.SOMETHING_WENT_WRONG
import com.cheq.retail.constants.AppsFlyEvents.logEventFly
import com.cheq.retail.constants.Constant
import com.cheq.retail.constants.FirebaseLog.FireBaseLogEvent
import com.cheq.retail.constants.TraceConstants
import com.cheq.retail.databinding.ActivitySplashBinding
import com.cheq.retail.sharePreferences.*
import com.cheq.retail.sharePreferences.SharePrefsKeys.Companion.BASE_BANK_MASTER
import com.cheq.retail.sharePreferences.SharePrefsKeys.Companion.GET_USER_ID_FIRST_TRY
import com.cheq.retail.sharePreferences.SharePrefsKeys.Companion.GET_USER_ID_RETRY
import com.cheq.retail.sharePreferences.SharePrefsKeys.Companion.GET_USER_ID_SUCCESS
import com.cheq.retail.sharePreferences.SharePrefsKeys.Companion.IS_BASE_URLS_AVAIL
import com.cheq.retail.ui.downTime.DownTimeActvity
import com.cheq.retail.ui.login.ExistingUserActivity
import com.cheq.retail.ui.login.LoginActivity
import com.cheq.retail.ui.onboarding.OnBoardingActivity
import com.cheq.retail.ui.splash.viewModel.SplashViewModel
import com.cheq.retail.utils.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.perf.ktx.performance
import com.google.firebase.perf.metrics.Trace
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.moengage.core.internal.utils.showToast
import com.smsapi.ProcessSMS
import com.smsapi.ResponseListener
import dagger.hilt.android.AndroidEntryPoint
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

@AndroidEntryPoint
class SplashActivity : BaseActivity(), ResponseListener {
    private var viewModel: SplashViewModel? = null
    lateinit var mBinding: ActivitySplashBinding
    val animationObserver: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>()
    private var MY_REQUEST_CODE = 1
    lateinit var appUpdateManager: AppUpdateManager
    var startupTraceLoggedIn: Trace? = null
    var startupTraceFirstTime: Trace? = null
    var splashCount = 0

    companion object {
        val intentHelper = object: IntentProvider<IntentKey.SplashActivityKey> {
            override fun createIntent(context: Context, key: IntentKey.SplashActivityKey): Intent {
                return Intent(
                    context,
                    SplashActivity::class.java
                ).apply {
                    if (key.newTask) {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        animationObserver.postValue(false)
        initCustomTraceForAppLaunchTime()
        initFirebaseDowntTime()
        setupViewModel()
        checkForEncryptionkeyToProceed()
        setupUi()


    }

    private fun checkForEncryptionkeyToProceed() {
       EncryptionPass.getDecryptedText()?.let {
            initData()
        }?: SharePrefsReferral.getInstance(this)?.getSslDelay(SharePrefsKeys.SSL_DELAY)
            ?.let { delay ->
                Handler(Looper.getMainLooper()).postDelayed({
                    initData()
                }, delay)
            }
    }

    private fun initCustomTraceForAppLaunchTime() {
        if (SharePrefs.getInstance(this)?.getBoolean(SharePrefsKeys.IS_LOGGED_IN) == true) {
            startupTraceLoggedIn =
                Firebase.performance.newTrace(TraceConstants.SPLASH_LOAD_TIME_LOGGED_IN)
            startupTraceLoggedIn?.start()
        } else {
            startupTraceFirstTime =
                Firebase.performance.newTrace(TraceConstants.SPLASH_LOAD_TIME_FIRST_TIME)
            startupTraceFirstTime?.start()
        }
    }


    private fun initData() {
        checkUpdate()
        logEventFly(this, AF_USER_OPENS, AF_SPLASH_EVENT, AF_CUST_FIRST_OPEN)
        FireBaseLogEvent(this, FBEvent_USER_OPENS, FBEvent_SPLASH_EVENT, FB_CUST_FIRST_OPEN)
    }

    private fun initFirebaseDowntTime() {
        DownTime.getDownTime {
            if (it.isenabled == true) {
                startActivity(
                    Intent(
                        this@SplashActivity,
                        DownTimeActvity::class.java
                    ).putExtra(Constant.FIREBASE_DATA, it)
                )


            }
        }
    }

    private fun setupUi() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        Utils.setTransparentStatusBar(this)

        if (SharePrefs.getInstance(this)?.getBoolean(SharePrefsKeys.IS_LOGGED_IN) == true) {
            mBinding.ivCheq.visibility = View.VISIBLE
            mBinding.clMain.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            mBinding.animationView.visibility = View.GONE
            mBinding.ivIcons.visibility = View.VISIBLE
            animationObserver.postValue(true)
            val cheqUserId =
                SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                    ?.getString(SharePrefsKeys.CHEQ_USER_ID)
            cheqUserId?.let { chequserid ->
                triggerFinartSdk(chequserid)
            }
        } else {
            mBinding.ivIcons.visibility = View.GONE
            mBinding.ivCheq.visibility = View.GONE
            mBinding.animationView.visibility = View.VISIBLE
            mBinding.clMain.background = null
            mBinding.animationView.setAnimation(R.raw.splash_screen_new)
            mBinding.animationView.playAnimation()
        }


        SharePrefs.getInstance(MainApplication.getApplicationContext())!!
            .putBoolean(SharePrefsKeys.IS_UNLOCKED, false)
        mBinding.animationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                if(splashCount >= FirebaseRemoteConfigUtils.getSplashRetryCount()){
                    errorSnackBar(FirebaseRemoteConfigUtils.getSplashExhaustText())
                    splashCount = 0
                    mBinding.animationView.cancelAnimation()
                    mBinding.ivCheq.visibility = View.VISIBLE
                    mBinding.clMain.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                    mBinding.animationView.visibility = View.GONE
                    mBinding.ivIcons.visibility = View.VISIBLE
                    animationObserver.postValue(false)
                }else {
                    checkIfcheqUserIdExists()
                }
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationStart(animation: Animator) {

            }

        })
        //  printHashKey(this)

    }

    private fun checkIfcheqUserIdExists() {
        if (!SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                ?.getString(SharePrefsKeys.CHEQ_USER_ID).isNullOrEmpty()
        ) {
            viewModel?.fetchUserProfileDetails()
            animationObserver.postValue(true)
            MparticleUtils.logEvent(
                eventName = GET_USER_ID_SUCCESS,
                eventDescription = "Get user id fetch is successful")
        } else {
            splashCount++
            viewModel?.checkCheqUser()
            mBinding.animationView.playAnimation()
            MparticleUtils.logEvent(
                eventName = GET_USER_ID_RETRY,
                eventDescription = "Get user id failed and retry is triggered")
        }
    }

    private fun setupObserver() {
        if (SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                ?.getString(SharePrefsKeys.CHEQ_USER_ID).isNullOrEmpty()
        ) {
            MparticleUtils.logEvent(
                eventName = GET_USER_ID_FIRST_TRY,
                eventDescription = "First time get cheQ user id is triggered")
            viewModel?.checkCheqUser()
        }

        viewModel?.responseObserver?.observe(this) {
            if (it == true) navigateToApp()
        }

        viewModel?.statusObserver?.observe(this) {
            errorSnackBar(it)
        }

        animationObserver.observe(this@SplashActivity) {
            if (it == true) {
                if (!SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                        ?.getString(SharePrefsKeys.CHEQ_USER_ID).isNullOrEmpty()
                ) {
                    navigateToApp()
                } else {
                    Utils.showToast(this@SplashActivity, SOMETHING_WENT_WRONG)
                }
            }
        }


        viewModel?.companyConstantObserver?.observe(this) {
            if (it?.assetsBaseUrl != null) {
                SharePrefs.getInstance(MainApplication.getApplicationContext())!!.apply {
                    putBoolean(IS_BASE_URLS_AVAIL, true)
                    it.assetsBaseUrl.apply {
                        putString(BASE_BANK_MASTER, "${bankMaster}/")
                        putString(SharePrefsKeys.BASE_LOAN_MASTER, "${loanMaster}/")
                        putString(SharePrefsKeys.BASE_BANNER, "${banner}/")
                        putString(SharePrefsKeys.BASE_HTML, "${html}/")
                        putString(SharePrefsKeys.BASE_POLICIES, "${policies}/")
                        putString(SharePrefsKeys.BASE_FAQS, "${faqs}/")
                        putString(SharePrefsKeys.BASE_VOUCHER, "${voucher}")
                        putString(SharePrefsKeys.BASE_WAITLIST, "${waitlist}/")

                    }


                }
            }
        }

    }

    private fun navigateToApp() {
        when {
            SharePrefs.getInstance(this)?.getBoolean(SharePrefsKeys.IS_LOGGED_IN) == true -> {
                MparticleUtils.logCurrentScreen(
                    "/onboarding/splashscreen",
                    "The screen displays a small animation with the brand name and logo of CheQ",
                    "user-type",
                    "existing",
                    "",
                    "",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.SPLASH_SCREEN),
                    this
                )
                startActivity(NavigationUtils.getNavigation(this, startupTraceLoggedIn))
            }

            SharePrefs.getInstance(this)
                ?.getBoolean(SharePrefsKeys.QUICK_LOGIN_AVAILABLE) != true -> {
                MparticleUtils.logCurrentScreen(
                    "/onboarding/splashscreen",
                    "The screen displays a small animation with the brand name and logo of CheQ",
                    "user-type",
                    "new",
                    "",
                    "",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.SPLASH_SCREEN),
                    this
                )
                startupTraceFirstTime?.stop()
                startActivity(Intent(this, OnBoardingActivity::class.java))
            }

            /*!PermissionUtils.checkRequiredPermission(this) -> {
                startActivity(Intent(this, PermissionActivity::class.java))

            }*/
            SharePrefs.getInstance(this)
                ?.getBoolean(SharePrefsKeys.QUICK_LOGIN_AVAILABLE) == true && !SharePrefs.getInstance(
                this
            )?.getString(SharePrefsKeys.FIRST_NAME).isNullOrEmpty() -> {
                MparticleUtils.logCurrentScreen(
                    "/onboarding/splashscreen",
                    "The screen displays a small animation with the brand name and logo of CheQ",
                    "user-type",
                    "existing",
                    "",
                    "",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.SPLASH_SCREEN),
                    this
                )
                startActivity(Intent(this, ExistingUserActivity::class.java))
            }

            SharePrefs.getInstance(this)
                ?.getBoolean(SharePrefsKeys.IS_USER_AUTHENTICATE) != true -> {
                MparticleUtils.logCurrentScreen(
                    "/onboarding/splashscreen",
                    "The screen displays a small animation with the brand name and logo of CheQ",
                    "user-type",
                    "existing",
                    "",
                    "",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.SPLASH_SCREEN),
                    this
                )
                startActivity(Intent(this, LoginActivity::class.java))

            }

            else -> {
                MparticleUtils.logCurrentScreen(
                    "/onboarding/splashscreen",
                    "The screen displays a small animation with the brand name and logo of CheQ",
                    "user-type",
                    "existing",
                    "",
                    "",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.SPLASH_SCREEN),
                    this
                )
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
        finish()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[SplashViewModel::class.java]
    }

    fun requestPermissionInSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 200)
    }

    private fun checkpermission() {
        TedPermission.create().setPermissionListener(permissionListener).setPermissions(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        ).check()
    }


    private fun errorSnackBar(message: String) {
        val snackBar = Snackbar.make(mBinding.clMain, message, Snackbar.LENGTH_INDEFINITE)
            .setAction("Refresh", object : View.OnClickListener {
                override fun onClick(v: View?) {
                    if (Utils.isNetworkAvailable(MainApplication.getApplicationContext()) && EncryptionPass.getDecryptedText()!=null) {
                        viewModel?.checkCheqUser()
                        if (SharePrefs.getInstance(MainApplication.getApplicationContext())
                                ?.getBoolean(SharePrefsKeys.IS_LOGGED_IN) == true
                        ) {
                            viewModel?.checkUserProfile()
                        } else {
                            viewModel?.checkUserByDeviceId()
                        }
                        viewModel?.responseObserver?.observe(this@SplashActivity) {
                            if (it == true) {
                                viewModel?.fetchUserProfileDetails()
                                navigateToApp()

                            }
                        }
                    }else{
                        errorSnackBar(message)
                    }

                }

            })

        snackBar.show()

    }

    private var permissionListener: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {

            animationObserver.postValue(true)

        }

        override fun onPermissionDenied(deniedPermissions: List<String>) {

            val snackBar = Snackbar.make(
                mBinding.clMain,
                "You need to allow storage permission",
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction("Setting", object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        requestPermissionInSetting()


                    }

                })

            snackBar.show()
            //checkpermission()
            /*  println("deniedPermissions $deniedPermissions")
            for (i in deniedPermissions) {
            if (i == "android.permission.WRITE") {
            println("permission*********** notificationPermission1deniedFor")
            //  updatePermissionStatus("notificationPermission", false)

            }
            if (i == "android.permission.READ_SMS") {
            println("permission*********** sMSPermission1deniedFor")
            // updatePermissionStatus("sMSPermission", false)
            }

            }*/

        }


    }


    /*override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            animationObserver.postValue(true)
            storagePermission = true
            navigateToApp()
        } else {
            storagePermission = false
            checkpermission()
        }
    }*/
    /*val intent = intent
    finish()
    startActivity(intent)*/

    private fun checkUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager!!.appUpdateInfo
        appUpdateInfoTask.addOnFailureListener() { appUpdateInfo ->
            setupObserver()
        }
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            handleAppUpdateInfo(appUpdateInfo)
        }
    }

    private fun handleAppUpdateInfo(appUpdateInfo: AppUpdateInfo?) {
        when (appUpdateInfo?.updateAvailability()) {
            UpdateAvailability.UPDATE_AVAILABLE -> {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    MY_REQUEST_CODE
                )
            }

            UpdateAvailability.UPDATE_NOT_AVAILABLE -> {
                setupObserver()
            }

            UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {
            }

            else -> {
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                checkUpdate()
            }
        }
    }

    private fun triggerFinartSdk(cheqUserId: String) {
        finartProcessInstance = ProcessSMS(applicationContext, this, cheqUserId)
        finartProcessInstance.enableProcessing()
        viewModel?.finartTriggerLog(cheqUserId, SCREEN_SPLASH)
        MparticleUtils.logFinartEvent(FINART_INVOKE, SCREEN_SPLASH)
        val bundle = Bundle().apply { putString(CHEQ_USER, cheqUserId) }
        FirebaseAnalytics.getInstance(MainApplication.getApplicationContext())
            .logEvent(FINART_TRIGGER_SPLASH, bundle)
    }

    override fun onSuccess(p0: Int, p1: Int) {
        MparticleUtils.logFinartEvent(FINART_SUCCESS_SPLASH, SCREEN_SPLASH)
        FirebaseAnalytics.getInstance(MainApplication.getApplicationContext())
            .logEvent(FINART_SUCCESS_SPLASH, null)
    }

    override fun onError(p0: Int, p1: String?) {
        FirebaseCrashlytics.getInstance().recordException(Throwable("Finart_SMS_Error $p0 $p1"))
    }

}