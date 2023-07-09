package com.cheq.retail.ui.referral

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.cheq.retail.R
import com.cheq.retail.application.MainApplication
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.constants.AFConstants
import com.cheq.retail.constants.AFConstants.AF_ONBOARD_REFERRAL
import com.cheq.retail.constants.AFConstants.AF_REFERRAL_CLICK
import com.cheq.retail.constants.AFConstants.AF_REFERRAL_NOW_CLICK
import com.cheq.retail.constants.AFConstants.FBEvent_ONBOARD_REFERRAL
import com.cheq.retail.constants.AFConstants.FBEvent_REFERRAL_NOW_CLICK
import com.cheq.retail.constants.AFConstants.FB_REFERRAL_CLICK
import com.cheq.retail.constants.AFConstants.TEMPLATE_ID
import com.cheq.retail.constants.AppsFlyEvents
import com.cheq.retail.constants.FirebaseLog.FireBaseLogEvent
import com.cheq.retail.databinding.ActivityReferEarnBinding
import com.cheq.retail.extensions.faqsBaseUrl
import com.cheq.retail.sharePreferences.SharePrefCheqUserId
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.accountSettings.TermsConditionActivity
import com.cheq.retail.ui.accountSettings.webView.CommonWebViewActivity
import com.cheq.retail.ui.main.MainActivity
import com.cheq.retail.ui.referral.viewmodel.ReferralViewModel
import com.cheq.retail.ui.referral.viewmodel.ReferralVmFactory
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.NetworkResult
import com.cheq.retail.utils.Utils
import com.google.android.material.snackbar.Snackbar


class ReferralActivity : BaseActivity() {
    lateinit var binding: ActivityReferEarnBinding
    lateinit var viewModel: ReferralViewModel
    val cheqUserId = SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
        ?.getString(SharePrefsKeys.CHEQ_USER_ID)
    lateinit var prefs: SharePrefs
   private var REFERRAL =false
    val REFERRAL_TAG="referral_tag"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatuBar()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_refer_earn)
        binding.activity = this
        setTermsCondtion()
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        AppsFlyerLib.getInstance().setAppInviteOneLink(TEMPLATE_ID)

        startAppsFlyer()
        initializeObservables()
        checkShortLink()

    }

    private fun setStatuBar() {
        prefs = SharePrefs.getInstance(this)!!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = window.decorView.systemUiVisibility // get current flag
            flags =
                flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // add LIGHT_STATUS_BAR to flag
            window.decorView.systemUiVisibility = flags
            window.statusBarColor = ContextCompat.getColor(this, R.color.ref_earned_bg) // optional
        }
    }

    private fun initializeObservables() {

        val repository = (application as MainApplication).referralRepository

        viewModel = ViewModelProvider(
            this,
            ReferralVmFactory(repository, cheqUserId, application)
        )[ReferralViewModel::class.java]

        viewModel.refStaticdata.observe(this) {
            when (it) {
                is NetworkResult.Loading -> {
                    Utils.showProgressDialog(this)
                }
                is NetworkResult.Success -> {
                    it.data?.let { it1 ->
                        binding.referralStatic = it1
                    }
                    Utils.hideProgressDialog()
                }
                is NetworkResult.Error -> {
                    Utils.hideProgressDialog()
                    showToast(it.message.toString())
                }
            }
        }

        viewModel.referral.observe(this) {
            when (it) {
                is NetworkResult.Loading -> {
                    Utils.showProgressDialog(this)
                }
                is NetworkResult.Success -> {
                    it.data?.let { it1 ->
                        binding.referral = it1
                    }
                    Utils.hideProgressDialog()
                }
                is NetworkResult.Error -> {
                    Utils.hideProgressDialog()
                    showToast(it.message.toString())
                }
            }
        }

        viewModel.genLinkLiveData.observe(this) {
            binding.txtRefLink.text = it
            SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                ?.putString(SharePrefsKeys.REF_SHORT_URL, it)
        }

        MparticleUtils.logCurrentScreen(
            "/refer-and-earn",
            "The customer views the refer & earn screen",
            "",
            "",
            "",
            "",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.REFERRAL_EARN),
            this
        )
    }

    private fun checkShortLink() {
        val refUrl = SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
            ?.getString(SharePrefsKeys.REF_SHORT_URL)

        if (refUrl.isNullOrEmpty())
            viewModel.generateRefLink(applicationContext)
        else
            binding.txtRefLink.text = refUrl
    }


    fun help() {

        var url = "${prefs.faqsBaseUrl}${getString(R.string.referral_faq)}"

        startActivity(Intent(this@ReferralActivity, CommonWebViewActivity::class.java).apply {
            putExtra("URL", url)
        })
    }

    private fun startAppsFlyer() {

        AppsFlyerLib.getInstance()
            .start(this, AFConstants.AF_DEV_KEY, object : AppsFlyerRequestListener {
                override fun onSuccess() {
                }

                override fun onError(errorCode: Int, errorDesc: String) {
                }
            })
    }

    private fun setTermsCondtion() {
        val text = "<font color=#858989>Read</font> <font color=#00C197> Terms & Conditions</font>"
        binding.txtTerms.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)

        binding.txtTerms.setOnClickListener {
            val intent = Intent(this, TermsConditionActivity::class.java)
            intent.putExtra("id", 3)
            startActivity(intent)
        }
    }

    fun onWhatsAppclick(view: View) {

        AppsFlyEvents.logEventFly(
            this,
            AF_REFERRAL_NOW_CLICK,
            AF_ONBOARD_REFERRAL,
            AF_REFERRAL_CLICK
        )
        FireBaseLogEvent(
            this, FBEvent_REFERRAL_NOW_CLICK,
            FBEvent_ONBOARD_REFERRAL, FB_REFERRAL_CLICK
        )

        MparticleUtils.logEvent(
            "ReferAndEarn_InviteYourFriend_Clicked",
            "User clicks on invite your friend on the refer and earn page",
            "Unique",
            "Continue",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Home_ReferAndEarn_Banner_Clicked),
            this
        )
        try {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                binding.referralStatic?.whatsappMessage?.replace(
                    "[URL]",
                    binding.txtRefLink.text.toString()
                )
                    ?: (getString(R.string.hey_this_your_referral) + "\n\n" + binding.txtRefLink.text)
            )
            sendIntent.type = "text/plain"
            sendIntent.setPackage("com.whatsapp")
            startActivity(sendIntent)
        } catch (exception: Exception) {
            showToast(getString(R.string.install_the_app))
        }

    }

    fun onClickCopy(v: View) {
        try {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", binding.txtRefLink.text.toString())
            clipboard.setPrimaryClip(clip)
            copyCustomView()
        } catch (exception: Exception) {

        }
    }

    private fun copyCustomView() {
        val snackbar = Snackbar.make(binding.parentLayout, "", Snackbar.LENGTH_SHORT)
        val customSnackView: View =
            layoutInflater.inflate(R.layout.custom_toast, null)
        snackbar.view.setBackgroundColor(Color.TRANSPARENT)
        val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
        snackbarLayout.setPadding(0, 0, 0, 0)
        snackbarLayout.addView(customSnackView, 0)
        snackbar.show()
        /*   val inflater = layoutInflater
           val layout: View = inflater.inflate(
               R.layout.custom_toast, null
           )
           val toast = Toast(applicationContext)
           toast.duration = Toast.LENGTH_LONG
           toast.view = layout
           toast.show()*/
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun showReferredHistory(view: View) {
        startActivity(Intent(this, ReferralHistoryActivity::class.java))
    }

    fun onBackPress(view: View) {
        REFERRAL = intent.getBooleanExtra(REFERRAL_TAG,false)
        if(REFERRAL){
            startActivity(Intent(this, MainActivity::class.java))
        }

        finish()


    }

    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            REFERRAL = intent.getBooleanExtra(REFERRAL_TAG,false)
            if(REFERRAL){
                startActivity(Intent(this@ReferralActivity, MainActivity::class.java))
            }
            else{
                finish()
            }

        }
    }


}