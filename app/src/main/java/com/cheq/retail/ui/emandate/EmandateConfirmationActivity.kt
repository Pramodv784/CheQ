package com.cheq.retail.ui.emandate

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.databinding.ActivityEmandateConfirmationBinding
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.Utils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class EmandateConfirmationActivity : BaseActivity() {
    lateinit var binding: ActivityEmandateConfirmationBinding
    var bankName = ""
    var accountNo = ""
    var ifsc = ""
    var bankLogo = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        catchIntent()
        setUpUi()
        MparticleUtils.logCurrentScreen("/emandate-registration/success", "The customer views the success status of the e-mandate registration", "", "", "", "", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.EMandate_Register_Success), this)
    }

    private fun catchIntent() {
        accountNo = intent.getStringExtra("ACCOUNT_NUMBER").toString()
        ifsc = intent.getStringExtra("ACCOUNT_IFSC").toString()
        bankName = intent.getStringExtra("BANK_NAME").toString()
        bankLogo = intent.getStringExtra("BANK_LOGO").toString()
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun setUpUi() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_emandate_confirmation)
        binding.activity = this

        binding.tvBankName.text = bankName
        binding.tvAccountNo.text = accountNo.replaceRange(0, (accountNo.length - 4), "XXXXXXXX")

        Glide.with(this).load(bankLogo).into(binding.ivBankLogo)
        Utils.setLightStatusBar(this)
        val date = getDefaultNextYearDate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            println("current date ${date.format(DateTimeFormatter.ofPattern("MM/yy"))}")
            binding.tvIfsc.text = date.format(DateTimeFormatter.ofPattern("MM/yy"))
        }

    }

    fun onButtonClick() {
        MparticleUtils.logEvent("Emandate_Success_Clicked", "User chooses to proceed to autopay activation after emandate registration success\n", "Unique", "Continue", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Emandate_Success_Clicked), this)
        startActivity(Intent(this, EnableProductActivity::class.java))
        finishAffinity()
    }


    fun getDefaultNextYearDate() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDateTime.now().plusYears(10)
    } else {
        TODO("VERSION.SDK_INT < O")
    }
}