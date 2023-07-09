package com.cheq.retail.ui.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModelProvider
import com.cheq.retail.R
import com.cheq.retail.application.MainApplication
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.constants.Constant
import com.cheq.retail.databinding.ActivityProfileBinding
import com.cheq.retail.databinding.ConsentCheckBottomSheetBinding
import com.cheq.retail.extensions.policiesBaseUrl
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.login.model.UpdateProfilePost
import com.cheq.retail.ui.login.viewModel.UpdateProfileViewModel
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog


class ProfileActivity : BaseActivity() {
    lateinit var binding: ActivityProfileBinding
    private var mobile = ""
    var isEmailValid = ObservableBoolean(false)
    var isFirstFilled = ObservableBoolean(false)
    var isLastFilled = ObservableBoolean(false)
    private var isTermsAccepted = ObservableBoolean(false)
    var detailsValidated = ObservableBoolean(false)
    private var isWhatsAppChecked: Boolean = true
    lateinit var viewModel: UpdateProfileViewModel
    private var prefs: SharePrefs? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        catchIntent()
        setupUI()
        setupViewModel()
        setUpAction()
    }

    private fun catchIntent() {
        mobile = intent?.getStringExtra("MOBILE").toString()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[UpdateProfileViewModel::class.java]
    }

    private fun setupUI() {
        Utils.setLightStatusBar(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        binding.activity = this
        prefs = SharePrefs.getInstance(MainApplication.getApplicationContext())?.let { it }
        binding.tvFirstName.onFocusChangeListener = onFirstNameFocused()
        binding.tvLastName.onFocusChangeListener = onLastNameFocused()
        binding.tvEmail.onFocusChangeListener = onEmailFocused()
        MparticleUtils.logCurrentScreen(
            "/onboarding/personal-details",
            "The customer inputs the first name, last name as per PAN, and also inputs e-mail that would allow us send mail communications",
            "",
            "",
            "",
            "",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.PERSONAL_DETAILS),
            this
        )
    }

    private fun setUpAction() {
        val spanString = SpannableString(binding.tvTnc.text)

        val termsTV: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                MparticleUtils.logEvent(
                    "Onboarding_T&C_Clicked",
                    "User clicks on the hyperlink for terms & conditions of CheQ App",
                    "Unique",
                    "Continue",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Onboarding_T_C_Clicked),
                    this@ProfileActivity
                )
                prefs?.let {startActivity(com.cheq.retail.ui.accountSettings.webView.CommonWebViewActivity.getStartIntent(applicationContext, url = it.policiesBaseUrl+Constant.TERMS_URL))  }
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }

        val privacyTV: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                MparticleUtils.logEvent(
                    "Onboarding_PrivacyPolicy_Clicked",
                    "User clicks on the hyperlink for privacy policy",
                    "Unique",
                    "Continue",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Onboarding_PrivacyPolicy_Clicked),
                    this@ProfileActivity
                )
                prefs?.let {startActivity(com.cheq.retail.ui.accountSettings.webView.CommonWebViewActivity.getStartIntent(applicationContext, url = it?.policiesBaseUrl+Constant.PRIVACY_URL))}
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }

        val tncStartIndex = 35
        val tncEndIndex = tncStartIndex + 17
        val privacyStartIndex = 57
        val privacyEndIndex = privacyStartIndex + 14

        spanString.setSpan(termsTV, tncStartIndex, tncEndIndex, 0)
        spanString.setSpan(privacyTV, privacyStartIndex,privacyEndIndex,0)
        binding.tvTnc.movementMethod = LinkMovementMethod.getInstance()
        binding.tvTnc.setText(spanString, TextView.BufferType.SPANNABLE)

    }
    override fun onBackPressed() {

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) Toast.makeText(
            applicationContext, " you can't go back from here ", Toast.LENGTH_LONG
        ).show()
        return false
        // Disable back button..............
    }

    fun redirectToLoading() {
        logEvents()
            val postData = UpdateProfilePost(
                binding.tvEmail.text.toString().trim(),
                binding.tvFirstName.text.toString().trim(),
                binding.tvLastName.text.toString().trim(),
                SharePrefsLog.getInstance(applicationContext)
                    ?.getLogBoolean(SharePrefsKeys.IS_WHATSAPP_CHECK)
            )


            startActivity(
                Intent(this, LoadingStateActivity::class.java).putExtra(
                    "DATA", postData
                )

            )
            finish()
    }

    private fun logEvents() {

        MparticleUtils.logEvent(
            "Onboarding_PersonalDetails_Clicked",
            "User confirms the personal details entered by clicking the CTA ",
            "Not Unique",
            "Continue",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_PERSONAL_DETAIL_CLICKED),
            this@ProfileActivity
        )

        MparticleUtils.logEvent(
            "Onboarding_PersonalDetails_FName_Entered",
            "User enters the FName as per PAN",
            "Not Unique",
            "Input Field",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_PERSONAL_DETAIL_FNAME_ENTERED),
            this@ProfileActivity
        )
        MparticleUtils.logEvent(
            "Onboarding_PersonalDetails_LName_Entered",
            "User enters the LName as per PAN",
            "Not Unique",
            "Input Field",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_PERSONAL_DETAIL_LNAME_ENTERED),
            this@ProfileActivity
        )
        MparticleUtils.logEvent(
            "Onboarding_PersonalDetails_Email_Entered",
            "User enters the email id that would be used for communications",
            "Not Unique",
            "Input Field",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_PERSONAL_DETAIL_EMAIL_ENTERED),
            this@ProfileActivity
        )
    }

    fun openDialog() {
        MparticleUtils.logEvent(
            "Onboarding_PersonalDetails_Clicked",
            "User confirms the personal details entered by clicking the CTA ",
            "Not Unique",
            "Continue",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_PERSONAL_DETAIL_CLICKED),
            this@ProfileActivity
        )
        MparticleUtils.logCurrentScreen(
            "/onboarding/consents",
            "The customer is asked to provide mandatory consent for bureau fetch and fetch from pay laters, loans along with consent for WhatsApp communications",
            "",
            "",
            "",
            "",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CONSENTS),
            this
        )

        val bottomSheetDialog = BottomSheetDialog(this)
        /* val view = layoutInflater.inflate(R.layout.consent_check_bottom_sheet, null)*/
        val mBinding: ConsentCheckBottomSheetBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this), R.layout.consent_check_bottom_sheet, null, false
        )
        customTextView2(mBinding.tvDescOne)
        println("isWhatsAppChecked ....$isWhatsAppChecked")
        mBinding.executePendingBindings()
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetDialog.setContentView(mBinding.root)
        bottomSheetDialog.setCanceledOnTouchOutside(true)
        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.show()
        mBinding.termsAccepted = isTermsAccepted
        mBinding.cbOne.isChecked = isTermsAccepted.get()
        mBinding.cbOne.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                isTermsAccepted.set(true)
                MparticleUtils.logEvent(
                    "Onboarding_BureauConsent_Checkbox_Selected",
                    "User allows mandatory consent for bureau pull",
                    "Not Unique",
                    "Checkbox",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_BUREAU),
                    this@ProfileActivity
                )
            } else {
                isTermsAccepted.set(false)
            }
        }
        mBinding.cbTwo.setOnCheckedChangeListener { _, isChecked ->
            isWhatsAppChecked = isChecked
            if (isChecked) {
                MparticleUtils.logEvent(
                    "Onboarding_WhatsAppConsent_Checkbox_Selected",
                    "User allows optional consent for WhatsApp communications",
                    "Unique",
                    "Checkbox",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_WHATSAPP_CONSENT_CHECKBOX_SELECTED),
                    this@ProfileActivity
                )

            } else {
                MparticleUtils.logEvent(
                    "Onboarding_WhatsAppConsent_Checkbox_Deselected",
                    "User disallows optional consent for WhatsApp communications",
                    "Unique",
                    "Checkbox",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_WHATSAPP_CONSENT_CHECKBOX_DESELECTED),
                    this@ProfileActivity
                )


            }

        }


        mBinding.btnSubmit.setOnClickListener {
            MparticleUtils.logEvent(
                "Onboarding_Consents_Clicked",
                "User provides mandatory consent and moves ahead in the flow",
                "Not Unique",
                "Continue",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_CONSENT_CLICKED),
                this@ProfileActivity
            )

            try {
                val postData = UpdateProfilePost(
                    binding.tvEmail.text.toString().trim(),
                    binding.tvFirstName.text.toString().trim(),
                    binding.tvLastName.text.toString().trim(),
                    isWhatsAppChecked
                )
                startActivity(
                    Intent(this, LoadingStateActivity::class.java).putExtra(
                        "DATA", postData
                    )

                )
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
            }
          //  bottomSheetDialog.dismiss()
        }
    }



    private fun customTextView2(view: TextView) {
        "I allow CheQ access to your credit and bill information from the RBI approved credit bureaus, fintech partners, and other loan providers"
        val spanTxt = SpannableStringBuilder(
            "I allow CheQ access to your credit and bill information from the RBI approved "
        )

        spanTxt.append("credit bureaus")


        val otherLoanProvider: ClickableSpan = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                ds.color = ds.linkColor // you can use custom color
                ds.bgColor = Color.WHITE
                ds.isUnderlineText = false // this remove the underline
            }

            override fun onClick(textView: View) {
                openWebView()
            }
        }
        val fintecPartner: ClickableSpan = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                ds.color = ds.linkColor // you can use custom color
                ds.bgColor = Color.WHITE
                ds.isUnderlineText = false // this remove the underline
            }

            override fun onClick(textView: View) {
                openWebView()
            }
        }


        val clickCreditBuereu: ClickableSpan = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                ds.color = ds.linkColor // you can use custom color
                ds.bgColor = Color.WHITE
                ds.isUnderlineText = false // this remove the underline
            }

            override fun onClick(textView: View) {
                openWebView()
            }
        }
        spanTxt.setSpan(
            clickCreditBuereu, spanTxt.length - " credit bureaus".length, spanTxt.length, 0
        )
        spanTxt.append(",")
        spanTxt.append(" fintech partners")
        spanTxt.setSpan(
            fintecPartner, spanTxt.length - " fintech partners".length, spanTxt.length, 0
        )
        spanTxt.append(",")
        spanTxt.append(" and ")
        spanTxt.append("other loan providers")
        spanTxt.setSpan(
            otherLoanProvider, spanTxt.length - " other loan providers".length, spanTxt.length, 0
        )
        view.movementMethod = LinkMovementMethod.getInstance()
        view.setText(spanTxt, TextView.BufferType.SPANNABLE)

    }

    private fun openWebView() {
        startActivity(Intent(this, CommonWebViewActivity::class.java))
    }

    fun onFirstName(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence, i: Int, i1: Int, i2: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, i: Int, i1: Int, i2: Int) {

                if (s.length > 0 && s.subSequence(0, 1).toString().equals(" ")) {
                    binding.tvFirstName.setText("")
                    // closeKeyBoard()
                } else {
                    isFirstFilled.set(!binding.tvFirstName.text.isNullOrEmpty())
                    validateFields()
                }


            }

            override fun afterTextChanged(editable: Editable) {

            }

        }
    }

    private fun onFirstNameFocused(): View.OnFocusChangeListener {
        return View.OnFocusChangeListener { _, hasFocused ->
            if (hasFocused) {
                binding.llFirstName.setBackgroundResource(R.drawable.et_btm_bg)
            } else {
                binding.llFirstName.setBackgroundResource(R.drawable.et_btm_bg_un_focused)
            }
        }
    }

    private fun onLastNameFocused(): View.OnFocusChangeListener {
        return View.OnFocusChangeListener { _, hasFocused ->
            if (hasFocused) {
                binding.llLastName.setBackgroundResource(R.drawable.et_btm_bg)
            } else {
                binding.llLastName.setBackgroundResource(R.drawable.et_btm_bg_un_focused)
            }
        }
    }

    private fun onEmailFocused(): View.OnFocusChangeListener {
        return View.OnFocusChangeListener { _, hasFocused ->
            if (hasFocused) {
                binding.llEmail.setBackgroundResource(R.drawable.et_btm_bg)
            } else {
                binding.llEmail.setBackgroundResource(R.drawable.et_btm_bg_un_focused)
            }
        }
    }

    fun onLastName(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence, i: Int, i1: Int, i2: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, i: Int, i1: Int, i2: Int) {
                if (s.length > 0 && s.subSequence(0, 1).toString().equals(" ")) {
                    binding.tvLastName.setText("")
                    // closeKeyBoard()
                } else {
                    isLastFilled.set(!binding.tvLastName.text.isNullOrEmpty())
                    validateFields()
                }

            }

            override fun afterTextChanged(editable: Editable) {

            }

        }
    }

    fun onEmailChanged(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence, i: Int, i1: Int, i2: Int
            ) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                isEmailValid.set(Utils.isValidEmail(binding.tvEmail.text.toString().trim()))
                if (Utils.isValidEmail(binding.tvEmail.text.toString())) {

                    val index = binding.tvEmail.text!!.trim().indexOf('.')
                    val extension = binding.tvEmail.text!!.trim().substring(index + 1)

                    if (extension.length > 2) {
                        //  closeKeyBoard()
                    }


                }

                validateFields()
            }

            override fun afterTextChanged(editable: Editable) {

            }

        }
    }


    fun validateFields() {
        detailsValidated.set(
            (isFirstFilled.get().toString().trim().toBoolean() && isLastFilled.get().toString()
                .trim().toBoolean() && isEmailValid.get())
        )
    }
}