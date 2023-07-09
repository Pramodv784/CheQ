package com.cheq.common.ui.webview

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.cheq.common.databinding.ActivityGenericWebViewBinding
import com.cheq.common.extension.empty
import com.cheq.common.extension.viewBinding
import com.cheq.common.utils.StatusBarUtils
import com.cheq.navigation.IntentKey
import com.cheq.navigation.IntentProvider

/**
 * Created by Akash Khatkale on 20th May, 2023.
 * akash.k@cheq.one
 */
class GenericWebViewActivity : AppCompatActivity() {

    private var binding: ActivityGenericWebViewBinding by viewBinding()
    private var webUrl: String = String.empty

    companion object {
        const val EXTRA_URL = "URL"

        val intentHelper = object :
            IntentProvider<IntentKey.GenericWebViewActivityKey> {
            override fun createIntent(
                context: Context,
                key: IntentKey.GenericWebViewActivityKey
            ): Intent {
                return Intent(
                    context,
                    GenericWebViewActivity::class.java
                ).apply {
                    putExtra(EXTRA_URL, key.url)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenericWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        StatusBarUtils.setLightStatusBar(this)
        setData()
        setupUi()
        setupClickListeners()
    }

    private fun setData() {
        webUrl = intent.getStringExtra(EXTRA_URL).orEmpty()
    }

    private fun setupClickListeners() {
        binding.backIv.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupUi() {
        binding.webview.apply {
            setInitialScale(1)
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            webViewClient = WebViewClient()
            loadUrl(webUrl)
            settings.javaScriptEnabled = true
            settings.setSupportZoom(true)
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    binding.loadingLottie.isVisible = false
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    binding.loadingLottie.isVisible = true
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.webview.canGoBack())
            binding.webview.goBack()
        else
            super.onBackPressed()
    }
}