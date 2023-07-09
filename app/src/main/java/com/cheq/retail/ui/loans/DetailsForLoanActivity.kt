package com.cheq.retail.ui.loans

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.databinding.ActivityDetailsForLoanBinding
import com.cheq.retail.extensions.faqsBaseUrl
import com.cheq.retail.extensions.loanMasterUrl
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.accountSettings.webView.CommonWebViewActivity
import com.cheq.retail.ui.loans.adapter.LoanFormAdapter
import com.cheq.retail.ui.loans.model.Loan2
import com.cheq.retail.ui.loans.model.add_loan.AddLoanDTO
import com.cheq.retail.ui.loans.model.add_loan.DeviceDetails
import com.cheq.retail.ui.loans.viewmodel.LoanProviderViewModel
import com.cheq.retail.utils.ImageUtils.loadSvg
import com.cheq.retail.utils.IntentKeys
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetDialog


class DetailsForLoanActivity : BaseActivity(), LoanFormAdapter.ButtonEnabled {
    lateinit var mBinding: ActivityDetailsForLoanBinding
    lateinit var viewModel: LoanProviderViewModel
    lateinit var loan: Loan2
    lateinit var prefs: SharePrefs
    private   var bottomSheetDialog: BottomSheetDialog?=null
    private var rewardsCanAssign = 0
    private var rewardsAssigned = 0
    private var rewardsAssignUpto = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
        setUpUI()
        getIntentData()
        setupObserver()
    }


    private fun setupObserver() {
        viewModel.statusObserver.observe(this) {
            bottomSheetDialog?.dismiss()
            mBinding.errorMessage.visibility = View.VISIBLE
            mBinding.errorMessage1.text = it
            mBinding.btnRequestPermission.visibility = View.VISIBLE
        }



        viewModel.addLoanResponse.observe(this) {

            var eventError = ""

            if (it != null && it.status?:false) {
                eventError = "no-error"
                LogEvent(eventError, it.billerName ?: "")
                bottomSheetDialog?.dismiss()
                mBinding.btnRequestPermission.visibility = View.VISIBLE
                val intent = (Intent(this@DetailsForLoanActivity, LoanSuccessActivity::class.java))
                intent.putExtra("loan_data", it)
                intent.putExtra(IntentKeys.REWARDS_ASSIGN_UPTO, rewardsAssignUpto)
                intent.putExtra(IntentKeys.REWARDS_CAN_ASSIGN, rewardsCanAssign)
                intent.putExtra(IntentKeys.REWARDS_ASSIGNED, rewardsAssigned)
                startActivity(intent)
                finish()
            } else {
                eventError = "unable-to-verify"
                LogEvent(eventError, "")
                bottomSheetDialog?.dismiss()
                mBinding.btnRequestPermission.visibility = View.VISIBLE
                mBinding.btnLottieAnimation.visibility = View.GONE
                mBinding.errorMessage.visibility = View.VISIBLE
                mBinding.errorMessage1.text = it.apiMessage
            }


        }

    }

    fun LogEvent(error: String, billerName: String) {
        MparticleUtils.logCurrentScreen(
            "/loan-addition/enter-details",
            "The customer enters the details as required for a bill fetch on the lender",
            "state",
            error,
            "lender-name",
            billerName,
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.LOAN_ENTER_DETAIL),
            this
        )
    }

    private fun setUpUI() {
        prefs = SharePrefs.getInstance(this)!!
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_details_for_loan)
        mBinding.activity = this
        mBinding.selectLoanProviderHelpButton.setOnClickListener {
            help()
        }


    }

    fun help() {
        val url = "${prefs.faqsBaseUrl}${getString(R.string.loan_faq)}"
        startActivity(
            Intent(
                this,
                CommonWebViewActivity::class.java
            ).apply {
                putExtra("URL", url)
            })
    }

    fun onBackPress(view: View) {
        onBackPressed()
        finish()
    }

    private fun getIntentData() {

        loan = intent.getSerializableExtra("LOAN_PROVIDER") as Loan2
        loan.updatedAt
        val imageUrl = "${prefs.loanMasterUrl}${loan.id}-logo-with-name.svg"




        MparticleUtils.logCurrentScreen(
            "/loan-addition/enter-details",
            "The customer enters the details as required for a bill fetch on the lender",
            "intent",
            "manual",
            "",
            "",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.LOAN_ENTER_DETAIL),
            this
        )

        //   mBinding.fLGradient.setBackgroundColor(Color.parseColor(loan.uiConfig.cardColor))
        mBinding.tvLoanProviderBankName.text = loan.billerName
        rewardsAssignUpto = intent.getIntExtra(IntentKeys.REWARDS_ASSIGN_UPTO, 0)
        rewardsCanAssign = intent.getIntExtra(IntentKeys.REWARDS_CAN_ASSIGN, 0)
        rewardsAssigned = intent.getIntExtra(IntentKeys.REWARDS_ASSIGNED, 0)
        mBinding.tvLoanProviderBankName.text = loan.billerName
        if (!loan.outerGridGradientColors?.gOne.isNullOrBlank()) {

            Utils.setBackGroundGradient(
                "#" + loan.outerGridGradientColors?.gOne,
                "#FFFFFF", "#" + loan.outerGridGradientColors?.gTwo,
                mBinding.fLGradient
            )
        }

        mBinding.ivLoanProvider.loadSvg(imageUrl)
        mBinding.rvLoanFormDetails.layoutManager = LinearLayoutManager(this)
        mBinding.rvLoanFormDetails.adapter = LoanFormAdapter(loan.customerParams, this)
    }


    @SuppressLint("SetTextI18n")
    private fun openProcessingBottomSheet() {
        bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialog)
        bottomSheetDialog?.setContentView(R.layout.bottom_sheet_common)
        bottomSheetDialog?.setCancelable(false)
        val tvTitle = bottomSheetDialog?.findViewById<AppCompatTextView>(R.id.bs_tv_title)
        val tvMessage = bottomSheetDialog?.findViewById<AppCompatTextView>(R.id.bs_tv_message)
        val tvRetryButton = bottomSheetDialog?.findViewById<AppCompatButton>(R.id.bs_btn_retry)
        val ivClose = bottomSheetDialog?.findViewById<AppCompatImageView>(R.id.bs_iv_cancel)
        val lottieAnimation = bottomSheetDialog?.findViewById<LottieAnimationView>(R.id.animationTwo)
        val animationProcessingLoan =
            bottomSheetDialog?.findViewById<LottieAnimationView>(R.id.animationProcessingLoan)
        val animationProcessing =
            bottomSheetDialog?.findViewById<LottieAnimationView>(R.id.animationProcessing)
        val doNotPressClose =
            bottomSheetDialog?.findViewById<AppCompatTextView>(R.id.doNotPressClose)
        val llAnimation =
            bottomSheetDialog?.findViewById<LinearLayoutCompat>(R.id.llAnimation)
        val llCreditCard =
            bottomSheetDialog?.findViewById<LinearLayoutCompat>(R.id.llCreditCard)
        tvTitle?.text = getString(R.string.str_finding_your_loan)
        tvMessage?.text = getString(R.string.str_this_may_take_a_moment)
        ivClose?.visibility = View.GONE


        MparticleUtils.logCurrentScreen(
            "/loan-addition/searching",
            "The customer awaits the terminal status of bill fetch for loan addition",
            "",
            "",
            "",
            "",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.LOAN_ENTER_DETAIL),
            this
        )
        tvTitle!!.text = "Finding Your Loan…"
        tvMessage!!.text =
            "This may take a moment"

        ivClose!!.visibility = View.GONE
        tvTitle?.text = getString(R.string.str_finding_your_loan)
        tvMessage?.text = getString(R.string.str_this_may_take_a_moment)
        ivClose?.visibility = View.GONE
        animationProcessingLoan?.visibility = View.VISIBLE
        animationProcessing?.visibility = View.GONE
        llAnimation?.visibility = View.VISIBLE
        doNotPressClose?.visibility = View.VISIBLE
        llCreditCard?.visibility = View.GONE
        tvRetryButton?.visibility = View.GONE
        llAnimation?.visibility = View.VISIBLE
        llCreditCard?.visibility = View.GONE
        lottieAnimation?.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        })

        bottomSheetDialog?.show()
    }


    fun submitAddLoanData(view: View) {
        mBinding.errorMessage.visibility = View.GONE
        mBinding.btnRequestPermission.visibility = View.GONE
        loan.customerParams.forEach {
            if (it.value.isNullOrBlank()) {
                print("values ${it.paramName} ")
                mBinding.errorMessage.visibility = View.VISIBLE
                mBinding.errorMessage1.text = "Invalid ${it.paramName}"
                mBinding.btnRequestPermission.visibility = View.VISIBLE
                mBinding.btnLottieAnimation.visibility = View.GONE
                return
            }
        }
        openProcessingBottomSheet();
        var IMEI = "34234"
        var productId = ""
        var loanId = ""
        val IPAddress = Utils.getIPAddress(true)
        if (intent.getStringExtra("from") == "home") {
            productId = loan.id
            loanId = intent.getStringExtra("instId") as String
        } else {
            productId = ""
            if (loan.id != null) {
                loanId = loan.id
            }
        }
        val data = prefs.getString(SharePrefsKeys.MOBILE_NUMBER)?.let {
            AddLoanDTO(
                productId = productId,
                billerId = loan.billerId,
                customerName = prefs.getString(SharePrefsKeys.FIRST_NAME).toString(),
                customerParams = loan.customerParams,
                customerPhoneNumber = it,
                deviceDetails = DeviceDetails(
                    APP = "ANDROID",
                    INITIATING_CHANNEL = "MOB",
                    OS = "android",
                    IP = IPAddress,
                    IMEI = IMEI
                ),
                id = loanId
            )

        }
        if (data != null) {
            viewModel.postLoanToServer(data)
        }

        viewModel.addLoanResponse.observe(this) {
            if(it.status!=null && !it.status) {
                    mBinding.errorMessage.visibility = View.VISIBLE
                    val errorMessage = it.userErrorMessage ?: it.apiMessage
                    mBinding.errorMessage1.text = errorMessage
                }
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.getLoanList()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[LoanProviderViewModel::class.java]

    }

    override fun onBackPressed() {
        super.onBackPressed()

        MparticleUtils.logEvent(
            "/loan-activation/select-lender",
            "User clicks the back CTA to go to the previous screen ",
            "Unique",
            "Back",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Loan_Activation_Enter_Detail),
            this@DetailsForLoanActivity
        )
    }

    override fun isButtonEnabled(isEnabled: Boolean) {
        mBinding.btnRequestPermission.isEnabled = isEnabled
    }

}
