package com.cheq.retail.ui.accountSettings

import android.os.Bundle
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.databinding.ActivityTermsConditionBinding
import com.cheq.retail.extensions.policiesBaseUrl
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.ui.accountSettings.viewmodel.AccountSettingsViewModel
import com.cheq.retail.utils.Utils

class TermsConditionActivity : BaseActivity() {
    private val TERMS_CONDITION = "TermsandCondition.html"
    private val PRIVACY_POLICY = "PrivacyPolicy.html"
    private val T_C_REFERRAL = "tncReferls.html"

    lateinit var mBinding: ActivityTermsConditionBinding
    private var accountSettingsViewModel: AccountSettingsViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUi()
    }


    private fun setUi() {
        Utils.setLightStatusBar(this)
        val id = intent.getIntExtra("id", 0)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_terms_condition)

        when (id) {
            1 -> {
                mBinding.tvTitle.text = getString(R.string.terms_and_conditions)
                loadUrl(TERMS_CONDITION)

            }
            2 -> {
                mBinding.tvTitle.text = getString(R.string.privacy_policy)
                loadUrl(PRIVACY_POLICY)
            }
            3 -> {
                mBinding.tvTitle.text = getString(R.string.privacy_policy)
                loadUrl(T_C_REFERRAL)
            }
            else -> getString(R.string.terms_and_conditions)
        }


        mBinding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun loadUrl(url: String) {
        mBinding.webView.webViewClient = WebViewClient()
        mBinding.webView.loadUrl(SharePrefs.getInstance(this)?.policiesBaseUrl + url)
        mBinding.webView.settings.javaScriptEnabled = true
        mBinding.webView.settings.setSupportZoom(true)
    }

}