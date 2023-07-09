package com.cheq.retail.ui.activateCard

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.databinding.ActivityPaymentSuccessfulBinding
import com.cheq.retail.ui.main.MainActivity
import com.cheq.retail.utils.GradientUtils.Companion.setBackGroundGradient
import com.cheq.retail.utils.Utils

class PaymentSuccessfulActivity : BaseActivity() {
    lateinit var binding: ActivityPaymentSuccessfulBinding
    private var name: String? = null
    private var cardNo: String? = null
    var network: String = ""
    private var issuerName: String = ""
    var logo = ""
    private var startColor = ""
    private var middleColor = ""
    private var endColor = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpUi()
        catchIntent()
        setGradient()
    }

    private fun setGradient() {
        setBackGroundGradient("FFFFFF", "FFFFFF", "FFFFFF", binding.llCreditCard.llCardBackGround)
        setBackGroundGradient(startColor, middleColor, endColor, binding.llCreditCard.llCardDetails)
    }

    @SuppressLint("SetTextI18n")
    private fun catchIntent() {
        name = intent.getStringExtra("NAME")
        print("Name $name")
        cardNo = intent.getStringExtra("CARD_NO")
        network = intent.getStringExtra("NETWORK").toString()
        issuerName = intent.getStringExtra("BANK_NAME").toString()
        logo = intent.getStringExtra("BANK_LOGO").toString()

        startColor = intent.getStringExtra("START_COLOR").toString()
        middleColor = intent.getStringExtra("MIDDLE_COLOR").toString()
        endColor = intent.getStringExtra("END_COLOR").toString()


        Glide.with(this).load(logo).into(binding.llCreditCard.ivBankImage)

        /* binding.llCreditCard.tvCardNumber.text = cardNo!!.subSequence(0, 4)
             .toString() + " " + cardNo!!.subSequence(4, 8).toString() + " " +
                 "XXXX" + " " + cardNo!!.subSequence(12, 16).toString()*/

        binding.llCreditCard.tvCardNumber.text = cardNo!!.subSequence(0, 4)
            .toString() + " " + cardNo!!.subSequence(4, 6)
            .toString() + "XX" + " " + "XXXX" + " " + cardNo!!.subSequence(12, 16).toString()
        binding.llCreditCard.tvBankName.text = issuerName
        binding.llCreditCard.tvCardHolderName.text = name


        when {
            network.equals("visa", true) -> {
                binding.llCreditCard.ivCardType.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this,
                        R.drawable.visa
                    )
                )

            }
            network.equals("mastercard", true) -> {
                binding.llCreditCard.ivCardType.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this, R.drawable.ic_mastercard
                    )
                )
            }
            else -> {
                binding.llCreditCard.ivCardType.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this, R.color.white
                    )
                )

            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun setUpUi() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_successful)
        binding.activity = this
        Utils.setLightStatusBar(this)
    }

    fun onButtonClick() {
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
    }


}