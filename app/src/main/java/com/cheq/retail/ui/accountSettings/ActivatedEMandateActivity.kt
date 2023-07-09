package com.cheq.retail.ui.accountSettings

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.databinding.ActivityActivatedEmandateBinding
import com.cheq.retail.utils.Utils

class ActivatedEMandateActivity : BaseActivity() {
    lateinit var binding: ActivityActivatedEmandateBinding
    private var bankLogo = ""
    private var bankValidity = ""
    private var bankName = ""
    private var bankAccountNumber = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activated_emandate)
        setUpUi()
        catchIntent()
    }

    @SuppressLint("SetTextI18n")
    private fun catchIntent() {
        this.intent?.apply {
            bankLogo = getStringExtra("BANK_LOGO").toString()
            bankName = getStringExtra("BANK_NAME").toString()
            bankValidity = getStringExtra("BANK_EXPIRY").toString()
            bankAccountNumber = getStringExtra("ACCOUNT_NUMBER").toString()
        }

        Glide.with(this).load(bankLogo).into(binding.ivBankLogo)
        binding.apply {
            tvBankName.text = bankName
            tvAccountNumber.text = "XXXXXXX${bankAccountNumber.takeLast(4)}"
            tvValidUntil.text = Utils.getDateLocalFormatInMMYY(bankValidity)
        }


    }

    private fun setUpUi() {
        Utils.setLightStatusBar(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_activated_emandate)
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }
}