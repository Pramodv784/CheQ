package com.cheq.retail.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.TextView.BufferType
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModelProvider
import com.cheq.retail.R
import com.cheq.retail.api.errormodel.BlockData
import com.cheq.retail.application.MainApplication
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.constants.AFConstants
import com.cheq.retail.constants.Constant
import com.cheq.retail.databinding.ActivityLoginBinding
import com.cheq.retail.extensions.policiesBaseUrl
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsKeys.Companion.OTP_ALLOWED_NEW_USER
import com.cheq.retail.sharePreferences.SharePrefsKeys.Companion.OTP_DENIED_NEW_USER
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.accountSettings.webView.CommonWebViewActivity
import com.cheq.retail.ui.blocker_screen.BlockerActivity
import com.cheq.retail.ui.login.model.isUserBlocked
import com.cheq.retail.ui.login.viewModel.LoginViewModel
import com.cheq.retail.ui.permission.PermissionActivity
import com.cheq.retail.ui.verifyOtp.VerifyOtpActivity
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.PermissionUtils
import com.cheq.retail.utils.Utils
import com.cheq.retail.utils.Utils.Companion.getUserClickBehaviour
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.moengage.core.analytics.MoEAnalyticsHelper


class LoginActivity : BaseActivity(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {
    lateinit var binding: ActivityLoginBinding
    private var viewModel: LoginViewModel? = null
    var isPhoneValid = ObservableBoolean(false)
    var isPhoneFocused = ObservableBoolean(false)
    private var mCredentialsApiClient: GoogleApiClient? = null
    private val RC_HINT = 1000
    var countForPhoneDialog = 0
    var backPressCount = 0;
    lateinit var prefs: SharePrefs
    private var doubleBackToExitPressedOnce = false

    private val phoneNumberHintIntentResultLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            try {
                val phoneNumber = Identity
                    .getSignInClient(applicationContext)
                    .getPhoneNumberFromIntent(result.data)
                //Do more stuff with phoneNumber
                binding.etMobileNo.setText(phoneNumber.takeLast(10))
                binding.etMobileNo.setSelection(binding.etMobileNo.length())

                MparticleUtils.logEvent(
                    "Onboarding_Mobile_Number_Selected",
                    "User selects one of the mobile numbers as fetched by the device and shown in the OS-modal on Android",
                    "Unique",
                    "Continue",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_MOBILE_NUMBER_SELECTED),
                    this
                )
            } catch (e: Exception) {
            }
        }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setupViewModel()
        setupObserver()
        getCredentialApiClient()
        setupActions()
    }

    private fun setupActions() {
        binding.tvTnc2.setOnClickListener {
//            startActivity(Intent(this, TermsConditionActivity::class.java))
            startActivity(
                CommonWebViewActivity.getStartIntent(
                    applicationContext,
                    url = prefs.policiesBaseUrl+Constant.BUREAU_URL
                )
            )
        }
    }


    private fun setupUI() {
        Utils.setLightStatusBar(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        prefs = SharePrefs.getInstance(this)!!
        binding.activity = this
        binding.etMobileNo.onFocusChangeListener = onMobileFocused()
        binding.etMobileNo.requestFocus()
        customTextView(binding.tvTnc)

        MparticleUtils.logCurrentScreen(
            "/onboarding/mobile-number",
            "The customer is asked to confirm or input the mobile number for account registration, and asked to provide two consents (bureau, whatsapp)",
            "error-state",
            "no-error",
            "",
            "",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_MOBILE_NUMBER),
            this
        )
        binding.termsCB.setOnCheckedChangeListener { _, isChecked ->
            binding.termsCB.isChecked = isChecked
            checkValidation()
        }
        SharePrefsLog.getInstance(applicationContext)
            ?.putLogBoolean(SharePrefsKeys.IS_WHATSAPP_CHECK, true)
        binding.whatsappCB.setOnCheckedChangeListener { _, isChecked ->
            binding.whatsappCB.isChecked = isChecked
            SharePrefsLog.getInstance(applicationContext)
                ?.putLogBoolean(SharePrefsKeys.IS_WHATSAPP_CHECK, binding.whatsappCB.isChecked)
        }


    }


    private fun customTextView(view: TextView) {
        val spanTxt = SpannableStringBuilder(
            "By continuing, you agree to CheQ’s "
        )

        spanTxt.append("terms-of-\nservice")

        val clickSpanTerms: ClickableSpan = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                ds.color = ds.linkColor // you can use custom color
                ds.bgColor = Color.WHITE
                ds.isUnderlineText = false // this remove the underline
            }

            override fun onClick(textView: View) {
                startActivity(
                    Intent(view.context, TermsAndConditionActivity::class.java).putExtra(
                        "link",
                        "Terms"
                    )
                )
            }
        }
        val clickSpanPrivacy: ClickableSpan = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                ds.color = ds.linkColor // you can use custom color
                ds.bgColor = Color.WHITE
                ds.isUnderlineText = false // this remove the underline
            }

            override fun onClick(textView: View) {
                startActivity(
                    Intent(view.context, TermsAndConditionActivity::class.java).putExtra(
                        "link",
                        "Privacy"
                    )
                )
            }
        }
        spanTxt.setSpan(
            clickSpanTerms, spanTxt.length - "terms-of\n-service".length, spanTxt.length, 0
        )
        spanTxt.append(" and")
        spanTxt.setSpan(ForegroundColorSpan(Color.BLACK), 52, spanTxt.length, 0)
        spanTxt.append(" privacy policy ")
        spanTxt.setSpan(
            clickSpanPrivacy, spanTxt.length - " privacy policy".length, spanTxt.length, 0
        )
        view.movementMethod = LinkMovementMethod.getInstance()
        view.setText(spanTxt, BufferType.SPANNABLE)

    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_HINT) {
            if (resultCode == RESULT_OK) {
                val cred: Credential? = data!!.getParcelableExtra(Credential.EXTRA_KEY)
                binding.etMobileNo.setText(cred?.id?.substring(3))
                binding.etMobileNo.setSelection(binding.etMobileNo.length())

                MparticleUtils.logEvent(
                    "Onboarding_Mobile_Number_Selected",
                    "User selects one of the mobile numbers as fetched by the device and shown in the OS-modal on Android",
                    "Unique",
                    "OS-Model",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_MOBILE_NUMBER_SELECTED),
                    this
                )


            } else {

                binding.etMobileNo.requestFocus()
                Utils.showKeyboard(this)
            }
        } else {
            binding.etMobileNo.requestFocus()
            Utils.showKeyboard(this)
        }
    }

    private fun getCredentialApiClient() {
        mCredentialsApiClient = GoogleApiClient.Builder(baseContext).addConnectionCallbacks(this)
            .enableAutoManage(this, this).addApi(Auth.CREDENTIALS_API).build()
    }

    private fun showNumberSelectorDialog() {

        val request: GetPhoneNumberHintIntentRequest =
            GetPhoneNumberHintIntentRequest.builder().build()

        Identity.getSignInClient(applicationContext)
            .getPhoneNumberHintIntent(request)
            .addOnSuccessListener {
                phoneNumberHintIntentResultLauncher.launch(
                    IntentSenderRequest.Builder(it.intentSender).build()
                )
            }
            .addOnFailureListener {

            }

        /*val hintRequest = HintRequest.Builder().setHintPickerConfig(
            CredentialPickerConfig.Builder().setShowCancelButton(true).build()
        ).setPhoneNumberIdentifierSupported(true).build()
        val intent = Auth.CredentialsApi.getHintPickerIntent(mCredentialsApiClient!!, hintRequest)
        try {
            startIntentSenderForResult(intent.intentSender, RC_HINT, null, 0, 0, 0, Bundle())
        } catch (e: SendIntentException) {
            Log.e("Login", "Could not start hint picker Intent", e)
        }*/
    }

    private fun onMobileFocused(): View.OnFocusChangeListener {
        return View.OnFocusChangeListener { _, hasFocused ->
            if (hasFocused) {
                isPhoneFocused.set(true)
                binding.llBtm.setBackgroundResource(R.drawable.et_btm_bg)

            } else {
                binding.llBtm.setBackgroundResource(R.drawable.et_btm_bg_un_focused)
                isPhoneFocused.set(false)
            }
        }
    }

    private fun setupObserver() {
        viewModel?.otpObserver?.observe(this) { otpResponse ->
            if (otpResponse != null) {
                if (otpResponse.isUserBlocked()) {
                    showErrorBlocker(otpResponse.blockedData)
                } else {
                    MparticleUtils.logEvent(
                        "Onboarding_Mobile_Number_Clicked",
                        "User opts to move ahead with OTP validation for the mobile number entered",
                        "Not Unique",
                        "Continue",
                        SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_MOBILE_NUMBER_CLICKED),
                        this
                    )

                    if (PermissionUtils.checkRequiredPermissionForSms(this)) {
                        MparticleUtils.logEvent(
                            eventName = OTP_ALLOWED_NEW_USER,
                            eventDescription = "User is allowed to go to verify otp when OTP fetch is successful")
                        startActivity(Intent(this, VerifyOtpActivity::class.java)
                            .putExtra("MOBILE", binding.etMobileNo.text.toString().trim()))
                    } else {
                        startActivity(PermissionActivity.startActivityForFirstTime(this,
                            binding.etMobileNo.text?.toString() ?: "",true))
                    }
                    finish()
                }
            } else {
                MparticleUtils.logEvent(
                    eventName = OTP_DENIED_NEW_USER,
                    eventDescription = "User is not allowed to go to verify otp when OTP fetch is not successful")
                showToast(AFConstants.SOMETHING_WENT_WRONG)
            }
        }

        viewModel!!.statusObserver.observe(this) {
              Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        viewModel!!.progressObserver.observe(this) {
            if (it) {
                //Utils.showProgressDialog(this)
                binding.flLottieIndicator.visibility = View.VISIBLE
                binding.btnRequestPermission.visibility = View.GONE
                getUserClickBehaviour(false, this)
            } else {
                /*   Utils.hideProgressDialog()*/
                binding.flLottieIndicator.visibility = View.GONE
                binding.btnRequestPermission.visibility = View.VISIBLE
                getUserClickBehaviour(true, this)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun onLogin() {
        if (!PermissionUtils.checkRequiredPermissionForSms(applicationContext)) {
            startActivity(
                PermissionActivity.startActivityForFirstTime(
                    applicationContext,
                    binding.etMobileNo.text.toString(),true
                )
            )
        } else {
            viewModel?.generateOtp(binding.etMobileNo.text.toString())
       }

    }

    fun onNumberChanged(): TextWatcher {

        return object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {


            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.length == 1) {
                    binding.llBtm.setBackgroundResource(R.drawable.et_btm_bg_un_focused)

                }
                if (charSequence.isNotEmpty()) {
                    if (charSequence[0].toString().isNotEmpty()) {
                        checkFirstDigit(charSequence[0].toString())
                    }
                } else {
                    binding.llError.visibility = View.INVISIBLE
                }
                if (charSequence.length in 8..9) {

                    binding.llBtm.setBackgroundResource(R.drawable.et_btm_bg_un_focused)
                }

                checkValidation()
                /*if (charSequence.length == 10 && Utils.isValidPhoneNumber(charSequence.toString()) && binding.termsCB.isChecked) {
                    binding.llError.visibility = View.GONE
                    binding.llBtm.setBackgroundResource(R.drawable.et_btm_bg)

                    isPhoneValid.set(true)

                } else {

                    isPhoneValid.set(false)
                }*/
            }

            override fun afterTextChanged(editable: Editable) {

            }

        }
    }

    fun checkValidation() {
        if (binding.etMobileNo.text.toString().length == 10 && Utils.isValidPhoneNumber(binding.etMobileNo.text.toString()) && binding.termsCB.isChecked) {
            binding.tvErrorNumber.visibility = View.GONE
            binding.errorIcon.visibility = View.GONE
            binding.llBtm.setBackgroundResource(R.drawable.et_btm_bg)
            MparticleUtils.logEvent(
                "Onboarding_Mobile_Number_Entered",
                "User enters the ten-digit mobile number in the input field for account registration",
                "Unique",
                "Input Field",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_MOBILE_NUMBER_ENTERED),
                this@LoginActivity
            )
            isPhoneValid.set(true)

            if(binding.whatsappCB.isChecked){
                MparticleUtils.logEvent(
                    "Onboarding_WhatsAppConsent_Checkbox_Selected",
                    "User allows optional consent for WhatsApp communications",
                    "Unique",
                    "Checkbox",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_WHATSAPP_CONSENT_CHECKBOX_SELECTED),
                    this@LoginActivity
                )
                MparticleUtils.logEvent(
                    "Onboarding_Mobile_Number_Clicked",
                    "User opts to move ahead after entering phone number and allowing one or both consents",
                    "Not Unique",
                    "Continue",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_MOBILE_NUMBER_CLICKED),
                    this@LoginActivity
                )


            }
            else{
                MparticleUtils.logEvent(
                    "Onboarding_WhatsAppConsent_Checkbox_Deselected",
                    "User disallows optional consent for WhatsApp communications",
                    "Unique",
                    "Checkbox",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_WHATSAPP_CONSENT_CHECKBOX_DESELECTED),
                    this@LoginActivity
                )
            }
            MparticleUtils.logEvent(
                "Onboarding_BureauConsent_T&C_Clicked",
                "User clicks on the hyperlink for bureau consent to open their T&Cs",
                "Unique",
                "Continue",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Onboarding_BureauConsent_T_C_Clicked),
                this@LoginActivity
            )


        } else {

            isPhoneValid.set(false)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
    }

    override fun onBackPressed() {
        //  exitApp()

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            finish()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Press BACK again to close the app", Toast.LENGTH_SHORT).show()



    }

    private fun checkFirstDigit(firstChar: String) {
        if (firstChar == "0" || firstChar == "1" || firstChar == "2" || firstChar == "3" || firstChar == "4" || firstChar == "5") {
            binding.llError.visibility = View.VISIBLE
        }
    }

    override fun onConnected(p0: Bundle?) {
        if (countForPhoneDialog == 1) {
            showNumberSelectorDialog()
        }
    }

    override fun onConnectionSuspended(p0: Int) {
        binding.etMobileNo.requestFocus()
        Utils.showKeyboard(this)
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        binding.etMobileNo.requestFocus()
        Utils.showKeyboard(this)
    }

    override fun onResume() {
        super.onResume()
        binding?.etMobileNo?.requestFocus()
        countForPhoneDialog += 1
        if (intent != null && intent.getStringExtra("MESSAGE") != null) {
            var error = intent.getStringExtra("MESSAGE")
            binding.llError.visibility = View.VISIBLE
            binding.tvErrorNumber.text = error


            if(getString(R.string.too_many_attempts_message)== intent.getStringExtra("MESSAGE")){
                MparticleUtils.logCurrentScreen(
                    "/onboarding/mobile-number",
                    "The customer is asked to confirm or input the mobile number for account registration, and asked to provide two consents (bureau, whatsapp)",
                    "error-state",
                    "otp-attempts-exceeded",
                    "",
                    "",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_MOBILE_NUMBER),
                    this
                )
            }
            else{
                MparticleUtils.logCurrentScreen(
                    "/onboarding/mobile-number",
                    "The customer is asked to confirm or input the mobile number for account registration, and asked to provide two consents (bureau, whatsapp)",
                    "error-state",
                    "unable-to-receive-otp",
                    "",
                    "",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_MOBILE_NUMBER),
                    this
                )
            }
        }


    }

    private fun showToast(message: String) {
        Utils.showToast(this@LoginActivity, message)
    }
    private fun showErrorBlocker(blockedData: BlockData?) {
        val intent = Intent(this, BlockerActivity::class.java).apply {
            putExtra(BlockerActivity.EXTRA_BLOCKER, blockedData)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(AFConstants.KEY_SCREEN_NAME, AFConstants.SCREEN_ONBOARDING_BLOCKED)
        }
        startActivity(intent)
    }


}