package com.cheq.retail.ui.main.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Nullable
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cheq.retail.BuildConfig
import com.cheq.retail.R
import com.cheq.retail.application.MainApplication
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.databinding.FragmentAppMenuBinding
import com.cheq.retail.sharePreferences.*
import com.cheq.retail.ui.accountSettings.*
import com.cheq.retail.ui.accountSettings.model.ActiveEmailsItem
import com.cheq.retail.ui.accountSettings.webView.adapter.EmailCheqSafeAdapter
import com.cheq.retail.ui.cheqsafe.CheqSafeParentFragment
import com.cheq.retail.ui.login.ExistingUserActivity
import com.cheq.retail.ui.main.MainActivity
import com.cheq.retail.ui.main.adapter.MenuItemAdapter
import com.cheq.retail.ui.main.model.listData
import com.cheq.retail.ui.main.viewModel.AppMenuViewModel
import com.cheq.retail.ui.referral.ReferralActivity
import com.cheq.retail.ui.socialLogin.MSGraphRequestWrapper
import com.cheq.retail.ui.socialLogin.model.CheqSafeScreens
import com.cheq.retail.ui.socialLogin.model.CheqSafeStatus
import com.cheq.retail.ui.socialLogin.model.EmailLinkingStatus
import com.cheq.retail.ui.socialLogin.viewModel.SocialLoginViewModel
import com.cheq.retail.utils.*
import com.cheq.retail.utils.Utils.Companion.isValidData
import com.freshchat.consumer.sdk.Freshchat
import com.google.android.gms.auth.GoogleAuthException
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.callback.OnCompleteListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.microsoft.identity.client.*
import com.microsoft.identity.client.exception.MsalClientException
import com.microsoft.identity.client.exception.MsalException
import com.microsoft.identity.client.exception.MsalServiceException
import com.moengage.core.MoECoreHelper
import java.io.IOException
import java.util.*
import kotlin.math.roundToInt


class AppMenuActivity : BaseActivity() {
    companion object {
        const val FIRST_NAME = "firstName"
        const val LAST_NAME = "lastName"
        const val FULL_NAME = "fullName"
        const val DATE_OF_BIRTH = "dob"
        const val PAN_DETAILS = "panDetails"
        const val MOBILE_NUMBER = "mobileNumber"
        const val EMAIL_ID = "emailId"
        const val LINKING = "Linking..."
        const val SUCCESS = "Success"

        private val TAG = AppMenuActivity::class.java.simpleName
        private const val EXTRA_SHOULD_OPEN_CHEQ_SAFE = "CHEQ_SAFE"

        fun getStartIntentForCheqSafe(context: Context): Intent {
            return Intent(context, AppMenuActivity::class.java)
                .putExtra(EXTRA_SHOULD_OPEN_CHEQ_SAFE, true)
        }
    }

    private var activity: MainActivity? = null
    private var mBinding: FragmentAppMenuBinding? = null
    private val binding get() = mBinding!!
    private var viewModel: AppMenuViewModel? = null
    private var socialViewModel: SocialLoginViewModel? = null
    private var googleSignInClient: GoogleSignInClient? = null
    private var mSingleAccountApp: ISingleAccountPublicClientApplication? = null
    private var mAccount: IAccount? = null
    val MS_GRAPH_ROOT_ENDPOINT = "https://graph.microsoft.com/"
    var googleName: String = ""
    var googleFamilyName: String = ""
    var googleEmail: String = ""
    var googleId: String = ""
    var googleIdToken: String = ""
    var googleAuthCode: String = ""
    var accessToken: String = ""
    var accessTokenSource: String = ""
    var scope: String = "user.read mail.read"
    private final val SIGN_IN_CODE = 100
    private var linkingDialog: BottomSheetDialog? = null
    private var selectEmailTypeDialog: BottomSheetDialog? = null
    var globalEmail = ""
    var firstName: String = ""
    var lastName: String = ""
    var name: String = ""
    var dob: String = ""
    var pancard: String = ""
    var mobile: String = ""
    var email: String = ""
    var outlookEmail: String = ""
    var isEMailTypeSelected = true
    var isEmailFilled = false
    private var emailType: String = ""
    var etEmail: EditText? = null
    var btnNext: AppCompatButton? = null
    private lateinit var _adaptor: MenuItemAdapter
    var emailCount: Int = 0
    private lateinit var emailCheqSafeAdapter: EmailCheqSafeAdapter
    lateinit var emailList: List<ActiveEmailsItem?>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.setContentView(this, R.layout.fragment_app_menu)

        setupViewModel()
        setupUI()

        setupObserver()
        bindListener()
        initGoogleLogin()
        createMicrosoft()


    }

    private fun getSettingData() {
        viewModel?.getUserLinkedEmail()
    }

    private fun createMicrosoft() {
        PublicClientApplication.createSingleAccountPublicClientApplication(this,
            R.raw.auth_config_single_account,
            object : IPublicClientApplication.ISingleAccountApplicationCreatedListener {
                override fun onCreated(application: ISingleAccountPublicClientApplication) {

                    mSingleAccountApp = application
                    loadAccount()
                }

                override fun onError(exception: MsalException) {
                    displayError(exception)
                }
            })
    }

    private fun loadAccount() {
        if (mSingleAccountApp == null) {
            return
        }
        mSingleAccountApp!!.getCurrentAccountAsync(object :
            ISingleAccountPublicClientApplication.CurrentAccountCallback {
            override fun onAccountLoaded(@Nullable activeAccount: IAccount?) {
                // You can use the account data to update your UI or your app database.
                mAccount = activeAccount
                // updateUI(activeAccount)
            }

            override fun onAccountChanged(
                @Nullable priorAccount: IAccount?,
                @Nullable currentAccount: IAccount?
            ) {
                if (currentAccount == null) {
                    // Perform a cleanup task as the signed-in account changed.
                    // performOperationOnSignOut()
                }
            }

            override fun onError(exception: MsalException) {
                displayError(exception)
            }
        })
    }

    private fun bindListener() {
        binding.linearLogout.setOnClickListener {

            MparticleUtils.logCurrentScreen(
                "/accounts-and-settings/logout",
                "The customer views the bottomsheet for logout confirmation",
                "",
                "",
                "",
                "",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.ACCOUNT_SETTING_LOGOUT),
                this
            )

            MparticleUtils.logEvent(
                "Accounts_Logout_Clicked",
                "User opts to logout",
                "Unique",
                "Continue",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Accounts_Logout_Clicked),
                this
            )
            //  logOutPopUp()
            logoutBottomSheetDialog()
        }
        binding.txtTermsPolicies.setOnClickListener {

            MparticleUtils.logEvent(
                "Accounts_TermsPolicies_Clicked",
                "User opts to click on terms & policies",
                "Unique",
                "Continue",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Accounts_TermsPolicies_Clicked),
                this
            )
            //  logOutPopUp()
            startActivity(Intent(this, TermsPoliciesActivity::class.java))
        }
    }

    private fun logoutBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_logout)
        val btnConfirm = bottomSheetDialog.findViewById<AppCompatButton>(R.id.btnConfirm)
        val btnCancel = bottomSheetDialog.findViewById<AppCompatButton>(R.id.btnCancel)
        bottomSheetDialog.show()
        btnConfirm!!.setOnClickListener {
            this?.let { it1 -> MoECoreHelper.logoutUser(it1) }

            SharePrefs.getInstance(MainApplication.getApplicationContext())?.let {
                it.putBoolean(SharePrefsKeys.IS_LOGGED_IN, false)
                it.putBoolean(SharePrefsKeys.QUICK_LOGIN_AVAILABLE, true)
            }
            SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())?.clearSharePrefs()
            SharePrefsReferral.getInstance(this)?.setCardShown(SharePrefsKeys.IS_CARD_SHOWN, false)
            SharePrefsReferral.getInstance(applicationContext)?.putBoolean(SharePrefsKeys.SSL_SAVED, false)
            MoECoreHelper.logoutUser(MainApplication.getApplicationContext())
            bottomSheetDialog.dismiss()
            MainApplication.finartLogout()
            startActivity(Intent(this, ExistingUserActivity::class.java))
            finish()

            MparticleUtils.logEvent(
                "Accounts_LogoutAccept_Clicked",
                "User confirms that they want to logout of their account",
                "Unique",
                "Continue",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Accounts_LogoutAccept_Clicked),
                this
            )
        }
        btnCancel!!.setOnClickListener {
            MparticleUtils.logEvent(
                "Accounts_LogoutCancel_Clicked",
                "User cancels the logout operation",
                "Unique",
                "Continue",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Accounts_LogoutCancel_Clicked),
                this
            )
            bottomSheetDialog.dismiss()
        }

    }

    private fun setupObserver() {
        viewModel?.userEmailResponseObserver?.observe(this) { userSettingsResponse ->
            userSettingsResponse?.let {
                firstName = it.firstName ?: ""
                lastName = it.lastName ?: ""
                name = "$firstName $lastName"
                email = it.email ?: ""
                mobile = it.mobile ?: ""
                pancard = it.panCard ?: ""
                dob = it.dateOfBirth ?: ""

                binding.txtName.text = name
                binding.txtNameInitials.text = getInitialsFnLn(firstName, lastName)
                binding.txtMobile.text = "+91 $mobile"


                listData[2].emailCount = it.activeEmails?.size?:0
                emailCount = it.activeEmails?.size?:0

                binding.rvMenu.adapter?.notifyDataSetChanged()
                emailList = it.activeEmails?: emptyList()

                emailCheqSafeAdapter = EmailCheqSafeAdapter(
                    this,
                    emailList,
                )

                if (intent.getBooleanExtra("CHEQ_SAFE", false)) {
                    if (emailList.isNotEmpty()) {
                        openManageCheqSafeDialog()
                    } else {
                        openCheqSafeBottomSheet()

                    }
                }
            }


        }

        viewModel!!.responseObserver.observe(this) {
//            openSuccessfullyLinkedDialog()

        }

        viewModel!!.statusObserver.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }


        viewModel!!.progressObserver.observe(this) {
            if (it) {
                Utils.showProgressDialog(activity)
            } else {
                Utils.hideProgressDialog()
            }
        }

        socialViewModel!!.progressObserver.observe(this) {
            if (it) {
                showLinkingBottomSheet()
            } else {
                linkingDialog!!.dismiss()
            }
        }
        socialViewModel!!.responseObserver.observe(this) {
            when (it) {
                EmailLinkingStatus.IN_PROGRESS -> {
                    openSuccessfullyLinkedDialog(LINKING)
                }
                EmailLinkingStatus.SUCCESS -> {
                    openSuccessfullyLinkedDialog(SUCCESS)
                }
                EmailLinkingStatus.FAILED -> {
                    openFailedDialog()
                }
            }

        }
        /*socialViewModel!!.emailcrawlingObserver.observe(this) {
            println("crawlingValueObserver $it")

            if (it != null) {

                    openSuccessfullyLinkedDialog()
               *//* } else {
                    openStatementDialog()
                }*//*
            } else {
                openFailedDialog()
            }

        }*/
    }

    private fun getInitialsFnLn(firstName: String, lastName: String): String? {
        return ((if (isValidData(firstName)) firstName[0].toString() else "")
                + if (isValidData(lastName)) lastName[0].toString() else "")
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[AppMenuViewModel::class.java]
        socialViewModel = ViewModelProvider(this)[SocialLoginViewModel::class.java]
    }


    private fun setupUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = window.decorView.systemUiVisibility // get current flag
            flags =
                flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // add LIGHT_STATUS_BAR to flag
            window.decorView.systemUiVisibility = flags
            window.statusBarColor = ContextCompat.getColor(this, R.color.ref_earned_bg) // optional
        }

        binding.rvMenu.layoutManager = LinearLayoutManager(activity)
        binding.rvMenu.setHasFixedSize(true)
        //  binding!!.llLogOut.setOnClickListener { logOutPopUp() }
        binding.rvMenu.layoutManager = LinearLayoutManager(this)


        viewModel!!.cheqSafeStatusObserver.observe(this, androidx.lifecycle.Observer {
            checkOnFirebase()
        })

        /* mBinding!!.llCheqSafe.setOnClickListener {
             openCheqSafeBottomSheet()
         }
         binding.llTransactionHistory.setOnClickListener {
             startActivity(Intent(getActivity(), TransactionHistoryActivity::class.java))
         }*/

        binding.linearHelp.setOnClickListener {

            MparticleUtils.logEvent(
                "Accounts_NeedHelp_Clicked",
                "User clicks on Need Help on the accounts & settings page",
                "Unique",
                "Continue",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.ACCOUNT_NEED_HELP_CLICKED),
                this
            )

            //Freshchat.resetUser(getApplicationContext());
            createFreshChatUser()
//            val tags: MutableList<String> = ArrayList()
//            tags.add("waitlist")
//            val options = ConversationOptions()
//                .filterByTags(tags, "waitlist")
//            Freshchat.showConversations(this, options)
            Freshchat.showConversations(applicationContext)
            //  composeEmail(arrayOf(getString(R.string.support_cheq_email)),"")
        }
        mBinding?.ivBack?.setOnClickListener { onBackPressed() }


        mBinding?.tvAppVersion?.text = "Build V${BuildConfig.VERSION_NAME}"

        MparticleUtils.logCurrentScreen(
            "/accounts-and-settings",
            "The customer views the accounts & settings screen",
            "",
            "",
            "",
            "",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.ACCOUNT_SETTING),
            this
        )
    }

    fun checkOnFirebase() {
        CheqSafeRealtimeDatabase.checkSafeFromFb(from = CheqSafeScreens.SETTING, onValueFetched = {
            binding.rvMenu.adapter = if (it != null && it.isVisible) {
                MenuItemAdapter(this, listData, ::onItemClicked, true)
            } else {
                val newList = listData.filterNot {
                    it.id == 3
                }
                MenuItemAdapter(this, newList, ::onItemClicked, true)
            }
        })
    }

    private fun createFreshChatUser() {


        SharePrefs.getInstance(MainApplication.getApplicationContext())?.getString("FIRST_NAME")
            ?.let {
                SharePrefs.getInstance(MainApplication.getApplicationContext())
                    ?.getString("LAST_NAME")?.let { it1 ->
                        SharePrefs.getInstance(MainApplication.getApplicationContext())
                            ?.getString("USER_EMAIL")?.let { it2 ->
                                SharePrefs.getInstance(MainApplication.getApplicationContext())
                                    ?.getString("MOBILE_NUMBER")?.let { it3 ->
                                        SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                                            ?.getString("CHEQ_USER_ID")?.let { it4 ->
                                                FreshChatUtil.createFreshChatUser(
                                                    this, it, it1, it2, it3, it4

                                                )
                                            }
                                    }
                            }
                    }
            }
    }

    val body = "User id: ${
        SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
            ?.getString(SharePrefsKeys.CHEQ_USER_ID)
    }" +
            "\n" +
            "Device Name: ${getDeviceName()}" +
            "\n" +
            "System Version: Android ${Build.VERSION.RELEASE}" +
            "\n" +
            "App Version: ${BuildConfig.VERSION_NAME}\n" +
            "\n" +
            "-Please don’t edit anything above this line, to help us serve you better-\n" +
            "\n" +
            "Add your query below:\n"

    private fun composeEmail(addresses: Array<String?>?, subject: String?) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:") // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, body)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    fun getDeviceName(): String? {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.lowercase(Locale.getDefault())
                .startsWith(manufacturer.lowercase(Locale.getDefault()))
        ) {
            capitalize(model)
        } else {
            capitalize(manufacturer) + " " + model
        }
    }

    private fun capitalize(s: String?): String? {
        if (s == null || s.length == 0) {
            return ""
        }
        val first = s[0]
        return if (Character.isUpperCase(first)) {
            s
        } else {
            first.uppercaseChar().toString() + s.substring(1)
        }
    }

    private fun startStartCheqSafeFlow() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.cheqSafeContainer, CheqSafeParentFragment())
        fragmentTransaction.commit()
    }

    @SuppressLint("InflateParams")
    private fun openCheqSafeBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog_enable_cheq_safe, null)
        dialog.setContentView(view)

        dialog.dismissWithAnimation = true
        dialog.behavior.setPeekHeight(
            (resources.displayMetrics.heightPixels * 0.90).roundToInt(),
            true
        )
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        val btnLinkEmail = dialog.findViewById<CardView>(R.id.btnLinkEmail)
        btnLinkEmail!!.setOnClickListener {
            dialog.dismiss()
//            openSelectEmailBottomSheet()
            googleSignOut()
            doGoogleLogin()
        }
        val ivCancel = dialog.findViewById<AppCompatImageView>(R.id.ivCancel)
        ivCancel!!.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


    @SuppressLint("InflateParams")
    private fun openSelectEmailBottomSheet() {
        initGoogleLogin()
        selectEmailTypeDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog_select_email, null)
        selectEmailTypeDialog!!.setContentView(view)
        selectEmailTypeDialog!!.dismissWithAnimation = true

        selectEmailTypeDialog!!.behavior.setPeekHeight(
            (resources.displayMetrics.heightPixels * 0.90).roundToInt(),
            true
        )
        selectEmailTypeDialog!!.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        // val flEtEmail = selectEmailTypeDialog!!.findViewById<FrameLayout>(R.id.iv_google)
        val ivGoogle = selectEmailTypeDialog!!.findViewById<ImageView>(R.id.iv_google)
        val ivOutlook = selectEmailTypeDialog!!.findViewById<ImageView>(R.id.iv_outlook)


        ivGoogle!!.setOnClickListener {
            /* etEmail!!.setText("")
             if (emailType == "Gmail") {
                 googleSignOut()
                 doGoogleLogin()
             } else if (emailType == "Outlook") {
                 signOut()
                 if (mSingleAccountApp == null) {
                     return@setOnClickListener
                 }
                 Utils.showProgressDialog(activity)
                 mSingleAccountApp!!.signIn(
                     this,
                     "",
                     getScopes()!!,
                     getAuthInteractiveCallback()!!
                 )
             }*/
            googleSignOut()
            doGoogleLogin()
        }

        ivOutlook?.setOnClickListener {
            signOut()
            if (mSingleAccountApp == null) {
                return@setOnClickListener
            }
            Utils.showProgressDialog(activity)
            mSingleAccountApp!!.signIn(
                this,
                "",
                getScopes()!!,
                getAuthInteractiveCallback()!!
            )
        }
        /*  val llGmail = selectEmailTypeDialog!!.findViewById<LinearLayoutCompat>(R.id.llGmail)
          val llOutlook = selectEmailTypeDialog!!.findViewById<LinearLayoutCompat>(R.id.llOutlook)
          val llLive = selectEmailTypeDialog!!.findViewById<LinearLayoutCompat>(R.id.llLive)
          val llHotmail = selectEmailTypeDialog!!.findViewById<LinearLayoutCompat>(R.id.llHotmail)
          val rbGmail = selectEmailTypeDialog!!.findViewById<AppCompatRadioButton>(R.id.rbGmail)
          val rbOutLook = selectEmailTypeDialog!!.findViewById<AppCompatRadioButton>(R.id.rbOutLook)
          val rbLive = selectEmailTypeDialog!!.findViewById<AppCompatRadioButton>(R.id.rbLive)
          val rbHotmail = selectEmailTypeDialog!!.findViewById<AppCompatRadioButton>(R.id.rbHotmail)
          val ivEmailType = selectEmailTypeDialog!!.findViewById<AppCompatImageView>(R.id.ivEmailType)*/

        /*  llGmail!!.setOnClickListener {
              etEmail!!.setText("")
              rbGmail!!.isChecked = true
              rbOutLook!!.isChecked = false
              rbLive!!.isChecked = false
              rbHotmail!!.isChecked = false
              emailType = "Gmail"
              ivEmailType!!.setImageResource(R.drawable.ic_gmail)
          }
          llOutlook!!.setOnClickListener {
              etEmail!!.setText("")
              rbGmail!!.isChecked = false
              rbOutLook!!.isChecked = true
              rbLive!!.isChecked = false
              rbHotmail!!.isChecked = false
              emailType = "Outlook"
              ivEmailType!!.setImageResource(R.drawable.ic_outlook)
          }
          llLive!!.setOnClickListener {
              etEmail!!.setText("")
              rbGmail!!.isChecked = false
              rbOutLook!!.isChecked = false
              rbLive!!.isChecked = true
              rbHotmail!!.isChecked = false
              emailType = "Live"
              ivEmailType!!.setImageResource(R.drawable.ic_outlook)
          }
          llHotmail!!.setOnClickListener {
              etEmail!!.setText("")
              rbGmail!!.isChecked = false
              rbOutLook!!.isChecked = false
              rbLive!!.isChecked = false
              rbHotmail!!.isChecked = true
              emailType = "Hotmail"
              ivEmailType!!.setImageResource(R.drawable.ic_outlook)
          }


          rbGmail!!.setOnCheckedChangeListener { _, p1 ->
              if (p1) {
                  llGmail.setBackgroundResource(R.drawable.ic_green_bg)
                  llOutlook.setBackgroundResource(R.drawable.ic_grey_bg)
                  llLive.setBackgroundResource(R.drawable.ic_grey_bg)
                  llHotmail.setBackgroundResource(R.drawable.ic_grey_bg)
                  rbGmail.isChecked = true
                  rbOutLook!!.isChecked = false
                  rbLive!!.isChecked = false
                  rbHotmail!!.isChecked = false
                  emailType = "Gmail"
                  ivEmailType!!.setImageResource(R.drawable.ic_gmail)
                  etEmail!!.setText("")
                 googleSignOut()
                  doGoogleLogin()
              }
          }
          rbOutLook!!.setOnCheckedChangeListener { _, p1 ->
              if (p1) {
                  llOutlook.setBackgroundResource(R.drawable.ic_green_bg)
                  llGmail.setBackgroundResource(R.drawable.ic_grey_bg)
                  llLive.setBackgroundResource(R.drawable.ic_grey_bg)
                  llHotmail.setBackgroundResource(R.drawable.ic_grey_bg)
                  rbGmail.isChecked = false
                  rbOutLook.isChecked = true
                  rbLive!!.isChecked = false
                  rbHotmail!!.isChecked = false
                  emailType = "Outlook"
                  ivEmailType!!.setImageResource(R.drawable.ic_outlook)
                  etEmail!!.setText("")
                  signOut()
                  Utils.showProgressDialog(activity)
                  if (mSingleAccountApp == null) {
                      return@setOnCheckedChangeListener
                  }
                  mSingleAccountApp!!.signIn(
                      this,
                      "",
                      getScopes()!!,
                      getAuthInteractiveCallback()!!
                  )
              }
          }
          rbLive!!.setOnCheckedChangeListener { _, p1 ->
              if (p1) {
                  llOutlook.setBackgroundResource(R.drawable.ic_grey_bg)
                  llLive.setBackgroundResource(R.drawable.ic_green_bg)
                  llGmail.setBackgroundResource(R.drawable.ic_grey_bg)
                  llHotmail.setBackgroundResource(R.drawable.ic_grey_bg)
                  rbGmail.isChecked = false
                  rbOutLook.isChecked = false
                  rbLive.isChecked = true
                  rbHotmail!!.isChecked = false
                  emailType = "Live"
                  ivEmailType!!.setImageResource(R.drawable.ic_outlook)
                  etEmail!!.setText("")
                  signOut()
                  Utils.showProgressDialog(activity)
                  if (mSingleAccountApp == null) {
                      return@setOnCheckedChangeListener
                  }
                  mSingleAccountApp!!.signIn(
                      this,
                      "",
                      getScopes()!!,
                      getAuthInteractiveCallback()!!
                  )
              }
          }
          rbHotmail!!.setOnCheckedChangeListener { _, p1 ->
              if (p1) {
                  llOutlook.setBackgroundResource(R.drawable.ic_grey_bg)
                  llGmail.setBackgroundResource(R.drawable.ic_grey_bg)
                  llLive.setBackgroundResource(R.drawable.ic_grey_bg)
                  llHotmail.setBackgroundResource(R.drawable.ic_green_bg)
                  rbGmail.isChecked = false
                  rbOutLook.isChecked = false
                  rbLive?.isChecked = false
                  rbHotmail.isChecked = true
                  emailType = "Hotmail"
                  ivEmailType!!.setImageResource(R.drawable.ic_outlook)
                  etEmail!!.setText("")
                  if (mSingleAccountApp == null) {
                      return@setOnCheckedChangeListener
                  }
              }
          }*/

        /* etEmail = selectEmailTypeDialog!!.findViewById(R.id.etEmail)!!
         btnNext = selectEmailTypeDialog!!.findViewById(R.id.btnNext)*/

        /* btnNext!!.setOnClickListener {
             if (!etEmail!!.text.isNullOrEmpty()) {
                 if (emailType == "Gmail") {
                     postUserDetails(
                         googleName,
                         googleFamilyName,
                         googleEmail,
                         accessToken,
                         accessTokenSource
                     )
                 }
                 if (emailType == "Outlook") {
                     socialViewModel!!.postUserDetails(
                         name,
                         name,
                         outlookEmail,
                         accessToken,
                         accessTokenSource
                     )
                 }
                 if (emailType == "Live") {
                     socialViewModel!!.postUserDetails(
                         name,
                         name,
                         outlookEmail,
                         accessToken,
                         accessTokenSource
                     )
                 }
                 if (emailType == "Hotmail") {
                     socialViewModel!!.postUserDetails(
                         name,
                         name,
                         outlookEmail,
                         accessToken,
                         accessTokenSource
                     )
                 }
             } else {
                 Toast.makeText(activity, "Please select email", Toast.LENGTH_SHORT).show()
             }


         }*/
        val ivCancel = selectEmailTypeDialog!!.findViewById<AppCompatImageView>(R.id.ivCancel)
        ivCancel!!.setOnClickListener {
            selectEmailTypeDialog!!.dismiss()
        }
        selectEmailTypeDialog!!.show()
    }

    private fun googleSignOut() {

        googleSignInClient?.signOut()
            ?.addOnCompleteListener(object : OnCompleteListener<Void>,
                com.google.android.gms.tasks.OnCompleteListener<Void> {
                override fun complete(p0: Void) {

                }

                override fun onComplete(p0: Task<Void>) {

                }

            })


    }

    private fun initGoogleLogin() {
        val gmailScope = Scope("https://www.googleapis.com/auth/gmail.readonly")
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            // .requestIdToken("143139010454-p91g4i1lgru0tvri08mts7927ojvs21f.apps.googleusercontent.com")
            .requestIdToken("143139010454-2bm1njnpn4j5gg6235tpsf65g5j2j8r3.apps.googleusercontent.com")

            .requestEmail()
            // .requestServerAuthCode("143139010454-p91g4i1lgru0tvri08mts7927ojvs21f.apps.googleusercontent.com")
            .requestServerAuthCode("143139010454-2bm1njnpn4j5gg6235tpsf65g5j2j8r3.apps.googleusercontent.com")
            .requestScopes(gmailScope)
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

    }


    override fun onResume() {
        super.onResume()
        activity?.setBottomTab(4)
        getSettingData()
    }

    private fun doGoogleLogin() {
        signInWithGoogleLauncher.launch(googleSignInClient!!.signInIntent)
    }

    private val signInWithGoogleLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultAuth = Auth.GoogleSignInApi.getSignInResultFromIntent(result.data!!)

            if (resultAuth != null) {
                if (resultAuth.status.isSuccess) {

                    val data: Intent? = result.data
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)

                    try {
                        accessTokenSource = "Gmail"

                        val account = task.getResult(ApiException::class.java)
                        googleName = account.givenName!!
                        googleFamilyName = account.familyName!!
                        // etEmail!!.setText(account.email!!)
                        isEmailFilled = true
                        //checkValidData()
                        googleId = account.id!!
                        googleIdToken = account.idToken!!
                        googleEmail = account.email!!
                        googleEmail = account.email!!
                        globalEmail = account.email!!
                        Utils.hideProgressDialog()
                        googleAuthCode = account.serverAuthCode!!
                        val runnable = Runnable {
                            try {
                                val scope = "oauth2:" + Scopes.EMAIL + " " + Scopes.PROFILE
                                accessToken = GoogleAuthUtil.getToken(
                                    this,
                                    account.account!!, scope, Bundle()
                                )

                                postUserDetails(
                                    account.givenName!!,
                                    account.familyName!!,
                                    account.email!!,
                                    accessToken,
                                    accessTokenSource,
                                    refreshToken = ""
                                )


                            } catch (e: IOException) {
                                e.printStackTrace()
                            } catch (e: GoogleAuthException) {
                                e.printStackTrace()
                            }


                        }
                        AsyncTask.execute(runnable)

                    } catch (e: ApiException) {
                        //Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                        Utils.hideProgressDialog()
                        openFailedDialog()

                    }

                } else if (resultAuth.status.statusCode == 12501) {
                    ///Cancel by User

                    openConfirmDialog()

                } else if (resultAuth.status.statusCode == 12500) {

                    // Failed
                    openFailedDialog()
                } else {
                    openFailedDialog()
                }
            }
        }


    private fun signOut() {

        if (mSingleAccountApp == null) {
            return
        }

        mSingleAccountApp!!.signOut(object : ISingleAccountPublicClientApplication.SignOutCallback {
            override fun onSignOut() {
                mAccount = null
            }

            override fun onError(exception: MsalException) {
                //displayError(exception)
            }
        })
    }

    private fun getScopes(): Array<String?>? {
        return scope.toLowerCase().split(" ".toRegex()).toTypedArray()
    }

    private fun getAuthInteractiveCallback(): AuthenticationCallback? {
        return object : AuthenticationCallback {
            override fun onSuccess(authenticationResult: IAuthenticationResult) {
                /* Successfully got a token, use it to call a protected resource - MSGraph */
                accessTokenSource = "Outlook"
                Utils.hideProgressDialog()
                name = "" + authenticationResult.account.claims!!["name"]

                /* Update account */
                mAccount = authenticationResult.account
                println("username ${authenticationResult.account.username}")
                outlookEmail = authenticationResult.account.username
                globalEmail = authenticationResult.account.username
                etEmail!!.setText(authenticationResult.account.username)
                isEmailFilled = true
                checkValidData()
                //updateUI()
                accessToken = authenticationResult.accessToken

                /* call graph */
                callGraphAPI(authenticationResult)
            }

            override fun onError(exception: MsalException) {
                /* Failed to acquireToken */
                Utils.hideProgressDialog()
                displayError(exception)
                if (exception is MsalClientException) {
                    /* Exception inside MSAL, more info inside MsalError.java */
                } else if (exception is MsalServiceException) {
                    /* Exception when communicating with the STS, likely config issue */
                }
            }

            override fun onCancel() {
                Utils.hideProgressDialog()
                /* User canceled the authentication */
            }
        }
    }


    private fun callGraphAPI(authenticationResult: IAuthenticationResult) {
        MSGraphRequestWrapper.callGraphAPIUsingVolley(
            this,
            MS_GRAPH_ROOT_ENDPOINT,
            authenticationResult.accessToken,
            { response ->
                displayGraphResult()
            }
        ) { error ->
            displayError(error)
        }
    }

    private fun displayGraphResult() {
        //Toast.makeText(this, graphResponse.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun displayError(exception: Exception) {
    }


    private fun postUserDetails(
        givenName: String,
        familyName: String,
        email: String,
        accessToken: String,
        accessTokenSource: String,
        refreshToken: String
    ) {
        socialViewModel!!.postUserDetails(
            givenName,
            familyName,
            email,
            accessToken,
            accessTokenSource,
            refreshToken
        )
    }

    @SuppressLint("InflateParams")
    private fun showLinkingBottomSheet() {
//        selectEmailTypeDialog!!.dismiss()
        linkingDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog_linking_process, null)
        linkingDialog!!.setContentView(view)
        linkingDialog!!.behavior.setPeekHeight(
            (resources.displayMetrics.heightPixels * 0.70).roundToInt(),
            true
        )
        linkingDialog!!.dismissWithAnimation = true
        val ivCancel = linkingDialog!!.findViewById<AppCompatImageView>(R.id.ivCancel)
        ivCancel!!.setOnClickListener {
            linkingDialog!!.dismiss()
        }


        linkingDialog!!.show()
    }

    @SuppressLint("SetTextI18n", "InflateParams")
    private fun openSuccessfullyLinkedDialog(status: String) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog_successfully_linked, null)
        dialog.setContentView(view)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.dismissWithAnimation = true
        val tvLinkedEmail = dialog.findViewById<AppCompatTextView>(R.id.tvLinkedEmail)
        val linkingTV = dialog.findViewById<TextView>(R.id.linkingTV)

        if (status == LINKING) {
            linkingTV?.apply {
                text = getString(R.string.linking)
                setTextColor(resources.getColor(R.color.golden_color, null))
                setCompoundDrawables(null, null, null, null)
            }
        } else {
            linkingTV?.apply {
                text = getString(R.string.success)
                setTextColor(resources.getColor(R.color.colorPrimary, null))
                setCompoundDrawables(
                    null,
                    null,
                    resources.getDrawable(R.drawable.ic_success_checq_safe, theme),
                    null
                )
            }
        }
        var userEmail = "<u>$globalEmail</u>"
//        tvLinkedEmail!!.text = "Your email address (${Html.fromHtml(userEmail)}) has been successfully linked"
        tvLinkedEmail!!.text = globalEmail
        val btnLetRoll = dialog.findViewById<AppCompatButton>(R.id.btnLetRoll)
        btnLetRoll!!.setOnClickListener {
            dialog.dismiss()
            this.startActivity(Intent(this, MainActivity::class.java))
        }
        val ivCancel = dialog.findViewById<AppCompatImageView>(R.id.ivCancel)
        ivCancel!!.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


    @SuppressLint("SetTextI18n", "InflateParams")
    private fun openFailedDialog() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog_linking_failed, null)
        dialog.setContentView(view)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.dismissWithAnimation = true
        val btnRetry = dialog.findViewById<AppCompatButton>(R.id.btnRetry)
        btnRetry!!.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, MainActivity::class.java))
        }
        val ivCancel = dialog.findViewById<AppCompatImageView>(R.id.ivCancel)
        ivCancel!!.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    @SuppressLint("SetTextI18n", "InflateParams")
    private fun openConfirmDialog() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog_linked_confirm, null)
        dialog.setContentView(view)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.dismissWithAnimation = true
        val btnDeny = dialog.findViewById<AppCompatButton>(R.id.btnDeny)
        val btnAllow = dialog.findViewById<AppCompatButton>(R.id.btnAllow)
        btnDeny!!.setOnClickListener {
            dialog.dismiss()
            //  openFailedDialog()

        }
        btnAllow?.setOnClickListener {
            dialog.dismiss()

        }
        val ivCancel = dialog.findViewById<AppCompatImageView>(R.id.ivCancel)
        ivCancel!!.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    @SuppressLint("InflateParams", "SetTextI18n")
    private fun openStatementDialog() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog_get_statement, null)
        dialog.setContentView(view)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.behavior.setPeekHeight(
            (resources.displayMetrics.heightPixels * 0.80).roundToInt(),
            true
        )
        dialog.dismissWithAnimation = true
        val ivCancel = dialog.findViewById<AppCompatImageView>(R.id.ivCancel)
        val tvEmail = dialog.findViewById<AppCompatTextView>(R.id.tvEmail)
        tvEmail!!.text = "Your Email ($globalEmail) is Linked"
        ivCancel!!.setOnClickListener {
            dialog.dismiss()
        }
        val btnLink = dialog.findViewById<AppCompatButton>(R.id.btnLink)
        btnLink!!.setOnClickListener {
            dialog.dismiss()
            openSelectEmailBottomSheet()
        }
        dialog.show()
    }

    private fun checkValidData() {
        btnNext!!.isEnabled = isEmailFilled && isEMailTypeSelected

    }


    private fun openManageCheqSafeDialog() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_manage_cheq_safe, null)
        dialog.setContentView(view)


        // tvLinkedEmail!!.text = "Your Email ($globalEmail) is successfully linked"
        // val btnLetRoll = dialog.findViewById<AppCompatButton>(R.id.btnLetRoll)
        /*   btnLetRoll!!.setOnClickListener {
               dialog.dismiss()
               this.startActivity(Intent(this, MainActivity::class.java))
           }*/
        val rv = dialog.findViewById<RecyclerView>(R.id.rvCheqSafeEmail)
        val linkBT = dialog.findViewById<AppCompatButton>(R.id.btnLinkEmail)
        emailCheqSafeAdapter = EmailCheqSafeAdapter(this, emailList)
        rv?.adapter = emailCheqSafeAdapter


        linkBT?.setOnClickListener {
            dialog.dismiss()
            openCheqSafeBottomSheet()
        }


        val ivCancel = dialog.findViewById<AppCompatImageView>(R.id.ivBack)
        ivCancel!!.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun onItemClicked(id: Int) {
        when (id) {
            1 -> {

                MparticleUtils.logEvent(
                    "Accounts_PersonalDetails_Clicked",
                    "User opts to go into personal details",
                    "Unique",
                    "Continue",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.ACCOUNT_PERSONAL_DETAIL_CLICKED),
                    this
                )
                startActivity(
                    Intent(this, PersonalDetailsActivity::class.java)
                        .putExtra(FIRST_NAME, firstName)
                        .putExtra(LAST_NAME, lastName)
                        .putExtra(FULL_NAME, name)
                        .putExtra(DATE_OF_BIRTH, dob)
                        .putExtra(PAN_DETAILS, pancard)
                        .putExtra(MOBILE_NUMBER, mobile)
                        .putExtra(EMAIL_ID, email)
                )
            }
            2 -> {
                MparticleUtils.logEvent(
                    "Accounts_TransactionHistory_Clicked",
                    "User opts to view transaction history",
                    "Unique",
                    "Continue",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Accounts_TransactionHistory_Clicked),
                    this
                )
                startActivity(Intent(this, TransactionHistoryActivity::class.java))
            }
            3 -> {
                /*
                if(emailCount>0){
                    openManageCheqSafeDialog()
//                    openCheqSafeBottomSheet()

                }
                else
                {
                    openCheqSafeBottomSheet()
                }
                 */
                startStartCheqSafeFlow()
            }
            4 -> {
                /*startActivity(
                    Intent(this, ManageCommunicationActivity::class.java)
                        .putExtra(MOBILE_NUMBER, mobile)
                        .putExtra(EMAIL_ID, email))*/
            }
            5 -> {
                startActivity(Intent(this, ReferralActivity::class.java))
            }
            6 -> {
                startActivity(Intent(this, PoliciesActivity::class.java))
            }
            7 -> {
                //  logOutPopUp()
            }


        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigateToParent()
    }
}