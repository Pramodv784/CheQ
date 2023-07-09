package com.cheq.retail.ui.activateCard

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.ViewModelProvider
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.ui.login.viewModel.WebPageViewModel
import com.cheq.retail.utils.Utils


class PaymentWebViewActivity : BaseActivity() {
    private lateinit var viewModel: WebPageViewModel
    private lateinit var myWebView: WebView
    private lateinit var backButton: AppCompatImageView
    private var stringHtml = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupUI()
        // MparticleUtils.logCurrentScreen(this::class.java.simpleName)
        Utils.showProgressDialog(this)
    }

    private fun setUpObserver() {
        stringHtml = intent.getStringExtra("url").toString()
        //   myWebView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
        //  myWebView.getSettings().setAppCachePath("/data/data/com.cheq.retail.cheq/cache")
        //  myWebView.getSettings().setAppCacheEnabled(true);
        myWebView.settings.javaScriptEnabled = true

        // myWebView.loadData(stringHtml, "text/html","text/html", "UTF-8")
        /* myWebView.loadData(
             stringHtml)*/
        myWebView.loadUrl(stringHtml)
        /* viewModel.webPageObserver.observe(this, Observer {
             if (intent.getStringExtra("link") == "Terms") {
                 myWebView.loadUrl(it.data.map.termsAndCondition.url.toString())
             } else {
                 myWebView.loadUrl(it.data.map.privacyPolicy.url.toString())
             }
             Utils.hideProgressDialog()
             // intent.getStringExtra("link")?.let {   }
         })*/
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[WebPageViewModel::class.java]
    }

    private fun callWebPageApi() {
        Utils.showProgressDialog(this)
        viewModel.getWebpage()
    }

    private fun setupUI() {
        setContentView(R.layout.activity_web_view)
        myWebView = findViewById(R.id.webview)
        var btnBack = findViewById(R.id.btnBack) as ImageView
        btnBack.setOnClickListener {
            onBackPressed()
            Utils.hideProgressDialog()
        }
        // myWebView.webViewClient = WebViewClient(object :WebView)
        myWebView.webChromeClient = WebChromeClient()
        myWebView.setWebViewClient(object : WebViewClient() {
            override fun onPageCommitVisible(view: WebView?, url: String?) {
                super.onPageCommitVisible(view, url)
                Utils.hideProgressDialog()
            }

            override fun onPageFinished(view: WebView, url: String) {
                if (url.contains("surl")) {
                    myWebView.visibility = View.GONE
                    SharePrefs.instance?.putBoolean(SharePrefsKeys.IS_CARD_ACTIVATE, true)
                    SharePrefs.instance?.putBoolean(SharePrefsKeys.IS_PAYMENT_FAILED, false)
                    onBackPressed()
                    finish()

                } else if (url.contains("furl")) {
                    Utils.setToast(applicationContext, "Fail")
                    SharePrefs.instance?.putBoolean(SharePrefsKeys.IS_PAYMENT_FAILED, true)
                    onBackPressed()
                    finish()
                } else if (url.contains("callback/razor")) {
                    myWebView.visibility = View.GONE
                    SharePrefs.instance?.putBoolean(SharePrefsKeys.IS_PAYMENT_FAILED, true)
                    onBackPressed()
                    finish()
                }

            }
        })
        setUpObserver()


    }

    override fun onBackPressed() {
        SharePrefs.instance?.putBoolean(SharePrefsKeys.IS_PAYMENT_FAILED, true)
        finish();
    }
}