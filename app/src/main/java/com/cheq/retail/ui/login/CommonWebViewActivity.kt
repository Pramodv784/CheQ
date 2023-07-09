package com.cheq.retail.ui.login


import android.os.Bundle
import android.webkit.WebView
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity

class CommonWebViewActivity : BaseActivity() {
    private lateinit var myWebView: WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common_web_view2)
        setupUI()
    }

    private fun setupUI() {
        myWebView = findViewById(R.id.webView)
        myWebView.loadUrl("https://www.lipsum.com/")
    }
}