package com.cheq.retail.ui.activateCard

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.login.viewModel.WebPageViewModel
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.Utils

class WebViewActivity : BaseActivity() {
    private lateinit var myWebView: WebView
    private var stringHtml = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()

    }

    private fun setUpObserver() {
        stringHtml = intent.getStringExtra("url").toString()
        myWebView.settings.javaScriptEnabled = true
        myWebView.loadData(stringHtml, "text/html", "UTF-8")
    }

    private fun setupUI() {
        setContentView(R.layout.activity_web_view)
        myWebView = findViewById(R.id.webview)
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            cancelPopup()
        }


        MparticleUtils.logCurrentScreen(
            "/cc-verification/verify-otp",
            "The customer is redirected to verify OTP webpage to confirm debit of INR 2",
            "",
            "",
            "",
            "",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Activation_Verify_Otp),
            this
        )
        myWebView.webChromeClient = WebChromeClient()
        myWebView.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                if (url.contains("surl")) {
                    //Utils.setToast(applicationContext,"Sucess")
                    myWebView.visibility = View.GONE
                    SharePrefs.instance?.putBoolean(SharePrefsKeys.IS_CARD_ACTIVATE, true)
                    onBackPressed()
                    finish()

                } else if (url.contains("furl")) {
                    SharePrefs.instance?.putBoolean(SharePrefsKeys.IS_CARD_SECURE, true)
                    onBackPressed()
                    finish()
                } else if (url.contains("callback/razor")) {
                    myWebView.visibility = View.GONE
                    SharePrefs.instance?.putBoolean(SharePrefsKeys.IS_CARD_ACTIVATE, true)
                    onBackPressed()
                    finish()
                }
            }
        })
        setUpObserver()


    }

    private fun cancelPopup() {
        val dialog = Utils.showCustomDialog(this, R.layout.logout_dialog)
        dialog.window?.setDimAmount(0.8f);
        dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        val text = dialog.findViewById<View>(R.id.text_dialog) as TextView
        val text1 = dialog.findViewById<View>(R.id.tvLetStart) as TextView
        val ok = dialog.findViewById<View>(R.id.btn_ok) as TextView

        text1.text = "Cancel"
        text.text = "Are you sure you want to Cancel?"

        ok.setOnClickListener {


            ok.setBackgroundColor(resources.getColor(R.color.colorPrimary))


            dialog.dismiss()
            finish()
        }
        val cancel = dialog.findViewById<View>(R.id.btn_cancel) as TextView
        cancel.setOnClickListener {
            cancel.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onBackPressed() {
        if (!isFinishing) {
            cancelPopup()
        }


    }
}