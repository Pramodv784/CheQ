package com.cheq.retail.ui.login

import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.NavUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.extensions.policiesBaseUrl
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.ui.login.viewModel.WebPageViewModel
import com.cheq.retail.utils.Utils


class TermsAndConditionActivity : BaseActivity() {
    private lateinit var viewModel: WebPageViewModel
    private lateinit var myWebView: WebView
    private lateinit var backButton: AppCompatImageView
    lateinit var prefs: SharePrefs
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
        callWebPageApi()
        setupUI()
        // MparticleUtils.logCurrentScreen(this::class.java.simpleName)
    }

    private fun setUpObserver() {
        try {
          //  myWebView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
          //  myWebView.getSettings().setAppCachePath("/data/data/com.cheq.retail.cheq/cache")
          //  myWebView.getSettings().setAppCacheEnabled(true);
            viewModel.webPageObserver.observe(this, Observer {
                if (it?.list != null && it.list.isNotEmpty()) {
                    if (intent.getStringExtra("link") == "Terms") {
                        myWebView.loadUrl("${prefs.policiesBaseUrl}TermsandCondition.html")
                    } else {
                        myWebView.loadUrl(it.map.privacyPolicy.url)
                    }
                }

                Utils.hideProgressDialog()
                // intent.getStringExtra("link")?.let {   }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[WebPageViewModel::class.java]
    }

    private fun callWebPageApi() {
        Utils.showProgressDialog(this)
        viewModel.getWebpage()
    }

    private fun setupUI() {
        prefs = SharePrefs.getInstance(this)!!
        setContentView(R.layout.activity_terms_and_condition)
        myWebView = findViewById(R.id.webview)
        backButton = findViewById(R.id.ivBack)
        myWebView.webViewClient = WebViewClient()
        backButton.setOnClickListener {
//            onBackPressed()
            navigateToParent()
        }

        setUpObserver()


    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigateToParent()
    }

    private fun navigateToParent() {

        NavUtils.getParentActivityIntent(this)?.let { upIntent ->
            if (NavUtils.shouldUpRecreateTask(this, upIntent) || isTaskRoot) {
                upIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(upIntent)
            } else {
            }
        }
    }
}