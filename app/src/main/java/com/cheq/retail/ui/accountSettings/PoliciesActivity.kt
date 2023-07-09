package com.cheq.retail.ui.accountSettings

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.databinding.ActivityPoliciesBinding
import com.cheq.retail.ui.login.TermsAndConditionActivity
import com.cheq.retail.utils.Utils

class PoliciesActivity : BaseActivity() {
    lateinit var mBinding: ActivityPoliciesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpUi()
       // MparticleUtils.logCurrentScreen(this::class.java.simpleName)
    }

    private fun setUpUi() {
        Utils.setLightStatusBar(this)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_policies)
        mBinding.ivBack.setOnClickListener {
            onBackPressed()
        }

        mBinding.llTerms.setOnClickListener {
            startActivity(Intent(this, TermsAndConditionActivity::class.java).apply {
                putExtra(
                    "link",
                    "Terms"
                )
            })
        }
        mBinding.llPrivacyPolicy.setOnClickListener {
            startActivity(Intent(this, TermsAndConditionActivity::class.java).apply {
                putExtra(
                    "link",
                    "Privacy"
                )
            })
        }
    }
}