package com.cheq.retail.ui.emandate

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.databinding.ActivityEmandateVerificationMethodBinding
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.emandate.model.EmandateConfirmationPost
import com.cheq.retail.ui.emandate.model.EmandateRpPostModel
import com.cheq.retail.ui.emandate.viewModel.EmandateViewModel
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetDialog

class EmandateVerificationMethodActivity : BaseActivity() {
    var detailsValidated = ObservableBoolean(false)
    lateinit var binding: ActivityEmandateVerificationMethodBinding
    var name = ""
    var accountNo = ""
    var ifsc = ""
    var paymentMode = ""
    var bankName = ""
    var bankLogo = ""
    var bankId = ""
    var bottomSheetDialog: BottomSheetDialog? = null
    private var viewModel: EmandateViewModel? = null
    var orderId: String = ""
    var customerId: String = ""
    var email: String = ""
    lateinit var authList: ArrayList<String>
    var razorpayPaymentId = ""

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            println("RESULT ++++++++++++  " + result.resultCode)

            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                razorpayPaymentId = data!!.getStringExtra("RP_PAYMENT").toString()

                showProcessingBottomSheet(
                    this,
                    "Activation is ongoing",
                    "Please wait until activation is complete",
                    false,
                    false
                )

            } else {
                showFailedBottomSheet()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        catchIntent()
        setUI()
        setupViewModel()
        setupObserver()
        setPaymentMethods()
        MparticleUtils.logCurrentScreen("/emandate-registration/choose-authentication-method", "The customer views the success status of the e-mandate registration", "type", "netbanking", "", "", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.EMandate_Choose_Auth_Method), this)
    }

    private fun setPaymentMethods() {
        for (i in authList) {
            when (i) {
                "netbanking" -> {
                    binding.rlNetbanking.visibility = View.VISIBLE
                }
                "debitcard" -> {
                    binding.rlDebit.visibility = View.VISIBLE
                }
                "aadhaar" -> {
                    binding.rlDebit.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupObserver() {
        viewModel!!.generateOrderObserver.observe(this) {
            try {
                orderId = it.id.toString()
                customerId = it.rzpCustomerId.toString()
                email = it.token?.bank_account?.beneficiary_email.toString()

                resultLauncher.launch(
                    Intent(
                        this@EmandateVerificationMethodActivity,
                        EmandateVerificationActivity::class.java
                    )
                        .putExtra("ACCOUNT_HOLDER_NAME", name)
                        .putExtra("ACCOUNT_NUMBER", accountNo)
                        .putExtra("ACCOUNT_IFSC", ifsc)
                        .putExtra("BANK_NAME", bankName)
                        .putExtra("PAYMENT_MODE", paymentMode)
                        .putExtra("BANK_LOGO", bankLogo)
                        .putExtra("ORDER_ID", orderId)
                        .putExtra("CUSTOMER_ID", customerId)
                        .putExtra("EMAIL", email)
                )

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        viewModel!!.statusObserver.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        viewModel!!.progressObserver.observe(this) {
            if (it) {
                Utils.showProgressDialog(this)
            } else {
                Utils.hideProgressDialog()
            }
        }

        viewModel!!.confirmationStatusObserver.observe(this) {
            bottomSheetDialog!!.dismiss()

            startActivity(
                Intent(this, EmandateConfirmationActivity::class.java)
                    .putExtra("BANK_NAME", bankName)
                    .putExtra("ACCOUNT_NUMBER", accountNo)
                    .putExtra("ACCOUNT_IFSC", ifsc)
                    .putExtra("BANK_LOGO", bankLogo)
            )
            finish()
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[EmandateViewModel::class.java]
    }

    private fun getOrderID() {
        viewModel!!.generateRazorpayOrder(
            EmandateRpPostModel(
                "savings",
                accountNo,
                paymentMode,
                ifsc
            )
        )
    }

    private fun catchIntent() {
        name = intent.getStringExtra("ACCOUNT_HOLDER_NAME").toString()
        accountNo = intent.getStringExtra("ACCOUNT_NUMBER").toString()
        ifsc = intent.getStringExtra("ACCOUNT_IFSC").toString()
        bankName = intent.getStringExtra("BANK_NAME").toString()
        bankLogo = intent.getStringExtra("BANK_LOGO").toString()
        bankId = intent.getStringExtra("BANK_ID").toString()
        authList = intent.getStringArrayListExtra("AUTH_LIST")!!
    }

    private fun setUI() {
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_emandate_verification_method)
        binding.activity = this
        Utils.setLightStatusBar(this)

        Glide.with(this).load(bankLogo).into(binding.ivBankLogo)

        binding.rlNetbanking.setOnClickListener {
            binding.rlNetbanking.setBackgroundResource(R.drawable.ic_green_rectangle_border)
            binding.rlDebit.setBackgroundResource(R.drawable.ic_grey_rectangle_border)
            binding.rlAadhar.setBackgroundResource(R.drawable.ic_grey_rectangle_border)
            detailsValidated.set(true)
            paymentMode = "netbanking"
            MparticleUtils.logEvent("Emandate_AuthVia_NetBanking_Clicked", "User opts for the Net Banking option for authentication", "Unique", "Continue", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Emandate_AuthVia_NetBanking_Clicked), this)
        }

        binding.rlDebit.setOnClickListener {
            binding.rlDebit.setBackgroundResource(R.drawable.ic_green_rectangle_border)
            binding.rlNetbanking.setBackgroundResource(R.drawable.ic_grey_rectangle_border)
            binding.rlAadhar.setBackgroundResource(R.drawable.ic_grey_rectangle_border)
            detailsValidated.set(true)
            paymentMode = "debitcard"
            MparticleUtils.logEvent("Emandate_AuthVia_DebitCard_Clicked", "User opts for the Debit Card option for authentication", "Unique", "Continue", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Emandate_AuthVia_DebitCard_Clicked), this)
        }

        binding.rlAadhar.setOnClickListener {
            binding.rlAadhar.setBackgroundResource(R.drawable.ic_green_rectangle_border)
            binding.rlNetbanking.setBackgroundResource(R.drawable.ic_grey_rectangle_border)
            binding.rlDebit.setBackgroundResource(R.drawable.ic_grey_rectangle_border)
            detailsValidated.set(true)
            MparticleUtils.logEvent("Emandate_AuthVia_Aadhar_Clicked", "User opts for the Aadhaar option for authentication", "Unique", "Continue", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Emandate_AuthVia_Aadhar_Clicked), this)
        }

        binding.button.setOnClickListener {
            MparticleUtils.logEvent("Emandate_AuthenticationMethod_Clicked", "User proceeds ahead with the selected authentication method", "Not Unique", "Continue", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Emandate_AuthenticationMethod_Clicked), this)
            getOrderID()
        }
    }

    private fun showProcessingBottomSheet(
        context: Context,
        title: String,
        message: String,
        showCloseButton: Boolean,
        showRetryButton: Boolean
    ) {
        bottomSheetDialog = BottomSheetDialog(context)
        bottomSheetDialog!!.setContentView(R.layout.bottom_sheet_common)
        bottomSheetDialog!!.setCancelable(false)
        MparticleUtils.logCurrentScreen("/emandate-registration/pending", "The customer authenticates the bank account within the Razorpay webview and awaits the terminal status", "", "", "", "", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.EMandate_Register_Pending), this)

        val tvTitle =
            bottomSheetDialog!!.findViewById<AppCompatTextView>(R.id.bs_tv_title)
        val tvMessage =
            bottomSheetDialog!!.findViewById<AppCompatTextView>(R.id.bs_tv_message)
        val tvRetryButton =
            bottomSheetDialog!!.findViewById<AppCompatButton>(R.id.bs_btn_retry)
        val lottieAnimation =
            bottomSheetDialog!!.findViewById<LottieAnimationView>(R.id.animationTwo)
        val llAnimation =
            bottomSheetDialog!!.findViewById<LinearLayoutCompat>(R.id.llAnimation)
        val llCreditCard =
            bottomSheetDialog!!.findViewById<LinearLayoutCompat>(R.id.llCreditCard)
        val tvCloseImageView =
            bottomSheetDialog!!.findViewById<AppCompatImageView>(R.id.bs_iv_cancel)

        tvTitle!!.text = title
        tvMessage!!.text = message

        if (!showCloseButton) {
            tvCloseImageView!!.visibility = View.INVISIBLE
        }

        if (!showRetryButton) {
            tvRetryButton!!.visibility = View.INVISIBLE
        }

        llAnimation!!.visibility = View.VISIBLE
        llCreditCard!!.visibility = View.GONE

        var isFirst = true

        lottieAnimation!!.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
                try {
                    if (isFirst) {
                        viewModel!!.postConfirmationToServer(
                            EmandateConfirmationPost(
                                razorpayPaymentId, name, accountNo, ifsc, bankName, bankId
                            )
                        )
                        isFirst = false
                    }
                } catch (ex: Exception) {
                    ex.toString()
                }
            }
        })

        tvCloseImageView!!.setOnClickListener {
            MparticleUtils.logEvent("Emandate_Success_BackClicked", "User chooses to exit the flow on success and does not proceed forward to autopay activation", "Unique", "Continue", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Emandate_Success_BackClicked), this)
            bottomSheetDialog!!.dismiss()
        }

        tvRetryButton!!.setOnClickListener {
            bottomSheetDialog!!.dismiss()
        }

        bottomSheetDialog!!.show()
    }

    private fun showFailedBottomSheet() {
        val failedBottomSheetDialog = BottomSheetDialog(this)
        failedBottomSheetDialog.setContentView(R.layout.bottom_sheet_emandate_failed)
        failedBottomSheetDialog.setCancelable(false)
        MparticleUtils.logCurrentScreen("/emandate-registration/failed", "The customer views the failed status of e-mandate registration\n", "error-code", "", "", "", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.EMandate_Registration_Failed), this)

        val tvBankName = failedBottomSheetDialog.findViewById<TextView>(R.id.tv_bank_name)
        val ivLogo = failedBottomSheetDialog.findViewById<ImageView>(R.id.em_iv_bank_logo)
        val tvAcNo = failedBottomSheetDialog.findViewById<TextView>(R.id.bs_tv_ac_no)
        val tvIfsc = failedBottomSheetDialog.findViewById<TextView>(R.id.tv_ifsc)
        val tvAcHolderName = failedBottomSheetDialog.findViewById<TextView>(R.id.tvAcHolderName)

        tvBankName!!.text = bankName
        tvAcNo!!.text = accountNo
        tvIfsc!!.text = ifsc
        tvAcHolderName!!.text = name

        Glide.with(this).load(bankLogo).into(ivLogo!!)

        val tvRetryButton = failedBottomSheetDialog.findViewById<AppCompatButton>(R.id.bs_btn_retry)

        tvRetryButton!!.setOnClickListener {
            MparticleUtils.logEvent("Emandate_Registration_Retry", "Retry button clicked", "Unique", "Continue", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Emandate_Registration_Retry), this)
            failedBottomSheetDialog.dismiss()
        }

        failedBottomSheetDialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        MparticleUtils.logEvent("Emandate_AuthenticationMethod_BackClicked", "User clicks the back CTA to go to the previous screen ", "Unique", "Back", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Emandate_AuthenticationMethod_BackClicked), this)
    }

}