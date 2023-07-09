package com.cheq.retail.ui.accountSettings.webView

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.databinding.ActivityCommonWebViewBinding
import com.cheq.retail.utils.Utils
import java.lang.Exception


class CommonWebViewActivity : BaseActivity() {

    companion object {

        private val EXTRA_URL = "URL"

        fun getStartIntent(context: Context, url: String) : Intent {
            return Intent(context, CommonWebViewActivity::class.java)
                .putExtra(EXTRA_URL, url)
        }
    }

    lateinit var mBinding: ActivityCommonWebViewBinding
    var URL = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common_web_view)
        catchIntent()
        setUi()
    }

    private fun catchIntent() {
        if (intent != null) {
            URL = intent.getStringExtra("URL").toString()
        }
    }

    private fun setUi() {
        Utils.setLightStatusBar(this)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_common_web_view)
        mBinding.ivBack.setOnClickListener {
            onBackPressed()
        }

        mBinding.webView.setInitialScale(1)
        mBinding.webView.settings.loadWithOverviewMode = true
        mBinding.webView.settings.useWideViewPort = true





        mBinding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url.toString()

                return if (url.startsWith("http:") or url.startsWith("https:")) {
                    return false
                } else {
                    try {
                        val i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse(URL)
                        startActivity(i)
                        finish()
                        true
                    } catch (e: Exception) {
                        finish()
                        true
                    }
                }
            }

        }
        mBinding.webView.loadUrl(URL)
        mBinding.webView.settings.javaScriptEnabled = true
        mBinding.webView.settings.setSupportZoom(true)
    }

    override fun onBackPressed() {
        if (mBinding.webView.canGoBack())
            mBinding.webView.goBack()
        else
            super.onBackPressed()
    }

}