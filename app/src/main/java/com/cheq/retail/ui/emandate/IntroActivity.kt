package com.cheq.retail.ui.emandate

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.databinding.ActivityIntroBinding
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.Utils

class IntroActivity : BaseActivity() {
    lateinit var binding: ActivityIntroBinding
    var isTermsAccepted = ObservableBoolean(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        MparticleUtils.logCurrentScreen("/emandate-registration/introduction", "The screen displays the benefits and the process for e-mandate registration", "", "", "", "", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.EMandate_Intro), this)
    }

    private fun setupUI() {
        Utils.setLightStatusBar(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro)
        binding.activity = this

        binding.cbTwo.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                MparticleUtils.logEvent("Emandate_Consent_Checkbox_Selected", "User allows the consent for e-mandate registration", "Not Unique", "Checkbox", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Emandate_Consent_Checkbox_Selected), this)
                isTermsAccepted.set(true)

            } else {
                MparticleUtils.logEvent("Emandate_Consent_Checkbox_Deselected", "User disallows the consent for e-mandate registration", "Checkbox", "Unique", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Emandate_Consent_Checkbox_Deselected), this)
                isTermsAccepted.set(false)

            }
        }

        binding.tvHelp.setOnClickListener {
            MparticleUtils.logEvent("Emandate_Help_Clicked", "User clicks on the Help CTA to seek support on e-mandate", "Unique", "Continue", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Emandate_Help_Clicked), this)
        }

    }

    fun navigateToNextScreen() {
        MparticleUtils.logEvent("Emandate_Introduction_Clicked", "User continues with the registration by progressing to the next screen", "Not Unique", "Continue", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Emandate_Introduction_Clicked), this)
        startActivity(Intent(this, SelectBankActivity::class.java))
    }

    override fun onBackPressed() {
        MparticleUtils.logEvent("Emandate_Introduction_BackClicked", "User clicks the back CTA to exit the emandate registration flow", "Unique", "Back", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Emandate_Introduction_BackClicked), this)
        super.onBackPressed()

    }
}