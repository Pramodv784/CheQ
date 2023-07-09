package com.cheq.retail.ui.loans

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cheq.config.FeatureConfig
import com.cheq.navigation.IntentFactory
import com.cheq.navigation.IntentKey
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.constants.AFConstants.CUSTOM
import com.cheq.retail.constants.AFConstants.FULL
import com.cheq.retail.constants.Constant
import com.cheq.retail.databinding.ActivityLoanSuccessBinding
import com.cheq.retail.extensions.bannerBaseUrl
import com.cheq.retail.extensions.loanMasterUrl
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.accountSettings.webView.CommonWebViewActivity
import com.cheq.retail.ui.billPayments.PaymentMethodsActivity
import com.cheq.retail.ui.billPayments.viewModel.BillPaymentsViewModel
import com.cheq.retail.ui.cheqsafe.CheqSafeParentFragment
import com.cheq.retail.ui.dashboard.view.fragment.HomeFragment
import com.cheq.retail.ui.deeplinkHandler.DeepLinkHandler
import com.cheq.retail.ui.loans.model.add_loan_response.AddLoanResponseX
import com.cheq.retail.ui.loans.viewmodel.LoanProviderViewModel
import com.cheq.retail.ui.login.modelv2.productv1.ProductV2
import com.cheq.retail.ui.main.MainActivity
import com.cheq.retail.ui.main.adapter.ProductsAdapterNew
import com.cheq.retail.ui.main.fragment.ProductFragment.Companion.ONE_PERCENT
import com.cheq.retail.ui.main.helper.MonthlyEarnRule
import com.cheq.retail.ui.referral.ReferralActivity
import com.cheq.retail.ui.socialLogin.model.CheqSafeScreens
import com.cheq.retail.ui.socialLogin.model.CheqSafeStatus
import com.cheq.retail.utils.*
import com.cheq.retail.utils.ImageUtils.loadSvg
import com.cheq.retail.utils.IntentKeys.LOAN_DATA
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

import kotlin.math.floor

@AndroidEntryPoint
class LoanSuccessActivity : BaseActivity() {
    lateinit var mBinding: ActivityLoanSuccessBinding
    lateinit var loanData: ProductV2
    var rewardsPoint = 0
    var billStatus = "full"
    var total = 0.0
    var totalDue = 0.0
    var minDue = 0.0
    lateinit var prefs: SharePrefs
    private var rewardsCanAssign = 0
    private var rewardsAssigned = 0
    private var rewardsAssignUpto = 0
    private var viewModel: LoanProviderViewModel? = null
    private var mFirebaseDatabase: DatabaseReference? = null
    private var mFirebaseInstance: FirebaseDatabase? = null

    @Inject
    lateinit var featureConfig: FeatureConfig

    @Inject
    lateinit var intentFactory: IntentFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[LoanProviderViewModel::class.java]
        mFirebaseInstance =
            FirebaseDatabase.getInstance(com.cheq.retail.BuildConfig.firebaseDataBaseURL)
        mFirebaseDatabase = mFirebaseInstance?.reference
        catchIntent()
        setUpUI()
    }

    companion object {
        const val LOAN_ADD = "LOAN_ADD"
    }

    private fun catchIntent() {
        rewardsAssignUpto = intent.getIntExtra(IntentKeys.REWARDS_ASSIGN_UPTO, 0)
        rewardsCanAssign = intent.getIntExtra(IntentKeys.REWARDS_CAN_ASSIGN, 0)
        rewardsAssigned = intent.getIntExtra(IntentKeys.REWARDS_ASSIGNED, 0)
    }

    private fun setUpUI() {
        prefs = SharePrefs.getInstance(this)!!
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_loan_success)
        mBinding.activity = this
        mBinding.ivRefer.loadSvg(prefs.bannerBaseUrl + "Refer-and-Earn.svg")
        mBinding.ivVoucher.loadSvg(prefs.bannerBaseUrl + "Rewards-Tab.svg")

        viewModel?.cheqSafeStatusObserver?.observe(this, androidx.lifecycle.Observer {
            when (val status = it ?:  CheqSafeStatus.NO_EMAIL_LINKED) {
                CheqSafeStatus.NO_EMAIL_LINKED,CheqSafeStatus.FAILED -> {
                    checkOnFirebase(status.name)
                }
                CheqSafeStatus.LINKED -> {
                    mBinding.cheqSafeLayout.clMainLayout.isVisible = false
                    mBinding.tvActionRecommended.isVisible = false
                }
            }
        })

        mBinding.cheqSafeLayout.ivOfferImage.setOnClickListener {
            startStartCheqSafeFlow()
        }

        /*CheqSafeRealtimeDatabase.checkSafeFromFb { it ->
            if (it != null) {
                mBinding.tvActionRecommended.visibility = View.VISIBLE
                mBinding.cheqSafeLayout.demoView.visibility = View.VISIBLE
                mBinding.tvActionRecommended.text = it.title
                val layoutParams =
                    mBinding.cheqSafeLayout.demoView.layoutParams as ConstraintLayout.LayoutParams
                layoutParams.dimensionRatio = it.ratio
                mBinding.cheqSafeLayout.demoView.layoutParams = layoutParams
                if (it.banner_type == 1) {
                    mBinding.cheqSafeLayout.ivOfferImage.let { it1 ->
                        Glide.with(this).load(it.banner_url).into(
                            it1
                        )
                    }
                }else {
                    mBinding.cheqSafeLayout.animationView.setFailureListener {

                    }
                    mBinding.cheqSafeLayout.animationView.setAnimationFromUrl(it.banner_url)


                }
              /*  mBinding.cheqSafeLayout.demoView.setOnClickListener { view ->
                    when (it.banner_redirection_type) {
                        HomeFragment.POP_UP -> {
                            //openOfferPopUp(it)
                        }
                        HomeFragment.DEEP_LINK -> {
                            it.banner_redirection?.let {
                                DeepLinkHandler.getDeeplinkIntent(this, it)
                            }?.let {
                                startActivity(it)
                            }
                        }
                        HomeFragment.WEB_LINK -> {
                            startActivity(
                                Intent(this, CommonWebViewActivity::class.java).putExtra(
                                    Constant.URL, "${it.banner_redirection}"
                                )
                            )
                        }
                    }

                }*/
            } else {
                mBinding.tvActionRecommended.visibility = View.GONE
                mBinding.cheqSafeLayout.demoView.visibility = View.GONE
            }
        }*/ //CHEQSAFE CHANGE
        mBinding.ivRefer.setOnClickListener {
            if (featureConfig.isNewProfileEnabled()){
                val intent = intentFactory.createIntent(
                    this,
                    IntentKey.MyAccountActivityKey(
                        cheqSafe = false,
                        startDestination = IntentKey.MyAccountActivityKey.REFER_EARN_DESTINATION
                    )
                )
                startActivity(intent)
            } else {
                startActivity(Intent(this@LoanSuccessActivity, ReferralActivity::class.java))
            }
            finish()
        }


        mBinding.ivVoucher.setOnClickListener {
            startActivity(Intent(this@LoanSuccessActivity, MainActivity::class.java).apply {
                putExtra("COMING_FROM_BILL", "COMING_FROM_BILL")
            })

            finish()
        }
        getIntentData()

    }

    fun checkOnFirebase(status: String) {
        CheqSafeRealtimeDatabase.checkSafeFromFb(from = CheqSafeScreens.PAYMENT, onValueFetched = {
            if (it != null) {
                Log.e("CHECK",it.toString())
                mBinding.cheqSafeLayout.clMainLayout.isVisible = it.isVisible
                mBinding.tvActionRecommended.isVisible = it.isVisible

                val bannerUrl = if (status == CheqSafeStatus.FAILED.name) it.failedBannerUrl else it.successBannerUrl
                mBinding.cheqSafeLayout.ivOfferImage.let { it1 ->
                    Glide.with(applicationContext).load(bannerUrl).into(
                        it1
                    )
                }
            }else {
                mBinding.tvActionRecommended.visibility = View.GONE
            }
        })
    }

    private fun startStartCheqSafeFlow() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.cheqSafeContainer, CheqSafeParentFragment())
        fragmentTransaction.commit()
    }

    private fun getIntentData() {
        loanData = intent.getSerializableExtra(LOAN_DATA) as ProductV2
        mBinding.llBillGenerated.tvBankName.text = loanData.bankMasterRecord?.billerName ?: ""
        if (loanData.total_due != null) {
            totalDue = loanData.total_due ?: 0.0
        }
        if (loanData.bill != null && loanData.bill?.min_due != null) {
            minDue = loanData.bill?.min_due ?: 0.0
        }


        MparticleUtils.logCurrentScreen(
            "/loan-addition/success",
            "The customer views the terminal status as success, with the bill due details fetched from the lender",
            "bill-amount",
            "${loanData.total_due}",
            "",
            "",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.LOAN_SUCCESS),
            this
        )

        val imageUrl =
            "${prefs.loanMasterUrl}${loanData.bankMasterRecord?.id}-logo-with-name.svg"
        val imageUrlBack =
            "${prefs.loanMasterUrl}${loanData.bankMasterRecord?.id}-card-image.svg"
        mBinding.llBillGenerated.ivBankImage.loadSvg(imageUrl)
        mBinding.llBillGenerated.ivLogoBack.loadSvg(imageUrlBack)
        mBinding.llBillGenerated.tvCardNumber.text = "··· ${loanData.last4}"
        mBinding.llBillGenerated.tvBankName.text = loanData.bankMasterRecord?.billerName
        val c = Calendar.getInstance().time

        val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = df.format(c)
        if (loanData.bill != null) {
            mBinding.btnCommon.visibility = View.VISIBLE
            if (loanData.productStatus == ProductsAdapterNew.ProductStatus.ProductStatusFive.status) {
                mBinding.llFullyPaid.visibility = View.VISIBLE
                if (loanData.bill?.total_due.toString() != "0.0") {
                    mBinding.tvLastBillAmt.text = loanData.bill?.total_due.toString()
                } else {
                    mBinding.tvLastBillAmt.visibility = View.GONE
                }
                mBinding.btnCommon.visibility = View.GONE
                mBinding.rl1.visibility = View.VISIBLE
            } else {
                mBinding.rl1.visibility = View.VISIBLE
                mBinding.rl2.visibility = View.GONE
                if (loanData.bill?.due_date != null) {
                    val remainingDays = DateTimeUtils.getDaysBetweenDates(formattedDate,
                        loanData.bill?.due_date?.length?.let {
                            loanData.bill?.due_date?.replaceRange(
                                10, it, ""
                            )
                        }).toString().toInt()

                    when {
                        remainingDays < -1 -> {
                            mBinding.tvDueDate.text =
                                "Overdue By ${remainingDays.toString().replace("-", "")} Days"
                            mBinding.tvDueDate.setTextColor(getColor(R.color.red))

                        }
                        remainingDays == -1 -> {
                            mBinding.tvDueDate.text = getString(R.string.str_over_due_by_one)
                            mBinding.tvDueDate.setTextColor(Color.parseColor("#F5466A"))
                        }
                        remainingDays == 0 -> {
                            mBinding.tvDueDate.text = getString(R.string.str_due_today)
                            mBinding.tvDueDate.setTextColor(Color.parseColor("#F5466A"))
                        }
                        remainingDays == 1 -> {
                            mBinding.tvDueDate.text = getString(R.string.str_due_in_one_day)
                            mBinding.tvDueDate.setTextColor(Color.parseColor("#F5466A"))
                        }
                        remainingDays in 2..7 -> {
                            mBinding.tvDueDate.text =
                                getString(R.string.str_due_in_days, remainingDays)
                            mBinding.tvDueDate.setTextColor(Color.parseColor("#F5466A"))
                        }

                        remainingDays > 7 -> {
                            mBinding.tvDueDate.text =
                                getString(R.string.str_due_in_days, remainingDays)
                            mBinding.tvDueDate.setTextColor(getColor(R.color.yellow))
                        }
                    }
                } else {
                    mBinding.tvDueDate.text = getString(R.string.no_due_date_found)
                    mBinding.tvDueDate.setTextColor(Color.parseColor("#858989"))
                }
                if (loanData.bill?.total_due != null) {
                    mBinding.llFullyPaid.visibility = View.GONE
                    mBinding.llPayNow.visibility = View.GONE
                    mBinding.llBillDue.visibility = View.VISIBLE

                    if (loanData.bill?.total_due?.toInt() == 0) {
                        mBinding.tvDueDate.text = getString(R.string.no_amount_due)
                        mBinding.btnCommon.visibility = View.VISIBLE
                    } else if (loanData.bill?.total_due!!.toInt() < 0) {
                        mBinding.tvDueAmount.text = getString(R.string._0)
                        mBinding.tvDueDate.text = getString(R.string.no_amount_due)
                        mBinding.btnCommon.visibility = View.VISIBLE
                        mBinding.tvDueDate.setTextColor(Color.parseColor("#ADB2B2"))

                    } else {
                        mBinding.tvDueAmount.text =

                            "₹${Utils.getFormattedDecimal(loanData.bill?.total_due ?: 0.0)}"
                    }

                } else {
                    mBinding.llFullyPaid.visibility = View.GONE
                    mBinding.llBillDue.visibility = View.GONE
                    mBinding.llPayNow.visibility = View.VISIBLE

                }
            }
        }


    }

    fun onButtonClick() {
        startActivity(Intent(this, MainActivity::class.java).putExtra(LOAN_ADD, LOAN_ADD))
        finishAffinity()
    }

    fun loanPaymentDialog() {

        MparticleUtils.logEvent(
            "Loan_addition_Success_PayNow_Clicked",
            "User opts to pay the outstanding bill fetched from the lender",
            "Unique",
            "Continue",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.LOAN_ADDITION_Success_PAYNOW_CLICKED),
            this@LoanSuccessActivity
        )

        val dialog = Utils.showCustomDialogBottum(this, R.layout.bottom_sheet_total_due_loan)
        dialog.setCancelable(false)
        val ivCancel = dialog.findViewById<FrameLayout>(R.id.ivCancel)
        var imageUrl =
            "${prefs.loanMasterUrl}${loanData.bankMasterRecord?.id}-logo-with-name.svg"
        ivCancel.setOnClickListener {
            dialog.dismiss()
            Utils.hideKeyboard(this)
        }
        val iv_Card_Type = dialog.findViewById<AppCompatImageView>(R.id.ivCardType)
        iv_Card_Type.visibility = View.GONE
        val iv_bank_image = dialog.findViewById<AppCompatImageView>(R.id.iv_bank_image)
        iv_bank_image.loadSvg(imageUrl)
        val ivMore = dialog.findViewById<AppCompatImageView>(R.id.ivMore)
        ivMore.setOnClickListener {
            openKnowMoreDialog()

        }
        val tvCardNumber = dialog.findViewById<AppCompatTextView>(R.id.tvCardNumber)
        tvCardNumber.text = "··· " + loanData.last4
        val flSolidColor = dialog.findViewById<ImageView>(R.id.flSolidColor)
        if (loanData.bankMasterRecord?.ui_config != null) {
            GradientUtils.setBoarderStroke(
                loanData.bankMasterRecord!!.ui_config.cardColor, "", true, flSolidColor
            )
        }

        val tvBankName = dialog.findViewById<AppCompatTextView>(R.id.tvBankName)
        tvBankName.text = loanData.bankMasterRecord?.billerName
        val etAmount = dialog.findViewById<EditText>(R.id.etAmount)
        val tvCaption = dialog.findViewById<AppCompatTextView>(R.id.tvCaption)
        val tvRewards = dialog.findViewById<AppCompatTextView>(R.id.tvRewards)
        val chipTotalDue = dialog.findViewById<Chip>(R.id.chipTotalDue)
        val chipMinimumDue = dialog.findViewById<Chip>(R.id.chipMinimumDue)
        val customChip = dialog.findViewById<Chip>(R.id.chipCustom)
        val tvRewardPercentage = dialog.findViewById<AppCompatTextView>(R.id.tvRewardPercentage)
        val tvAtTheRate = dialog.findViewById<AppCompatTextView>(R.id.tvAtTheRate)
        val tvRewardEarned = dialog.findViewById<AppCompatTextView>(R.id.tvRewardEarned)

        val llMessageMinimumDue = dialog.findViewById<LinearLayoutCompat>(R.id.llMessageMinimumDue)
        val llMessageTotalDue = dialog.findViewById<LinearLayoutCompat>(R.id.llMessageTotalDue)
        val llAmtView = dialog.findViewById<LinearLayoutCompat>(R.id.llAmtView)
        val llMessageError = dialog.findViewById<LinearLayoutCompat>(R.id.llMessageError)
        val llRewards = dialog.findViewById<LinearLayoutCompat>(R.id.llRewards)
        val tvError = dialog.findViewById<TextView>(R.id.tvError)
        val ivLoanError = dialog.findViewById<AppCompatImageView>(R.id.ivLoanError)
        val btnOkay = dialog.findViewById<AppCompatButton>(R.id.btnOkay)
        tvError.setTextColor(Color.BLACK)
        ivLoanError.setImageResource(R.drawable.ic_warning)


        if ((loanData.bill?.total_due ?: 0.0) >= 100) {
            rewardsPoint = roundupAmount(loanData.bill?.total_due ?: 1.0, 1.0)
            loanData.rewardsPoint = rewardsPoint
            totalDue = loanData.bill?.total_due ?: 0.0
            llRewards.visibility = View.VISIBLE
            billStatus = FULL
            loanData.billStatus = FULL
            tvRewards.text = getString(R.string.str_you_will_earn_reward_user, rewardsPoint)
            tvRewardPercentage.text = ONE_PERCENT
        } else if ((loanData.bill?.total_due ?: 0.0) < 100) {
            llRewards.visibility = View.VISIBLE
            tvRewards.text = getString(R.string.str_pay_total_bill)
        } else {
            llRewards.visibility = View.GONE
        }
        if (totalDue > 0) {
            etAmount.setText(Utils.getFormattedDecimal(totalDue))
            btnOkay.isEnabled = true
        }

        chipTotalDue.setOnClickListener {
            billStatus = FULL
            loanData.billStatus = FULL
            if (totalDue != null) {
                etAmount.setText(Utils.getFormattedDecimal(totalDue.toDouble()))
            }
            etAmount.isEnabled = false
            llMessageError.visibility = View.GONE
            llMessageTotalDue.visibility = View.VISIBLE
            llMessageMinimumDue.visibility = View.GONE
            tvCaption.text = resources.getText(R.string.you_have_to_pay)
            if ((loanData.bill?.total_due ?: 0.0) >= 100) {
                rewardsPoint = roundupAmount(loanData.bill?.total_due ?: 1.0, 1.0)
                llRewards.visibility = View.VISIBLE
                tvRewards.text = getString(R.string.str_you_will_earn_reward_user, rewardsPoint)
                tvRewardPercentage.text = ONE_PERCENT
            } else if ((loanData.bill?.total_due ?: 0.0) < 100) {
                llRewards.visibility = View.VISIBLE
                tvRewards.text = getString(R.string.str_pay_total_bill)
            } else {
                llRewards.visibility = View.GONE
            }
        }
        var paymentAmountExactness = loanData.bankMasterRecord?.paymentAmountExactness

        if (paymentAmountExactness == getString(R.string.adhoc)) {
            customChip.visibility = View.VISIBLE
            llMessageError.visibility = View.GONE
            if (totalDue > 0) {
                chipTotalDue.visibility = View.VISIBLE
            } else {
                chipTotalDue.visibility = View.GONE
            }
            if (minDue > 0) {
                chipMinimumDue.visibility = View.VISIBLE
            } else {
                chipMinimumDue.visibility = View.GONE
            }
            etAmount.isEnabled = true
            etAmount.requestFocus()
            llMessageTotalDue.visibility = View.GONE
            llMessageMinimumDue.visibility = View.GONE
            tvCaption.text = getString(R.string.str_enter_amount)
            this.let { it1 -> Utils.showKeyboard(it1) }

        } else if (paymentAmountExactness == getString(R.string.exact_and_below)) {
            chipMinimumDue.visibility = View.GONE
            chipTotalDue.visibility = View.VISIBLE
            customChip.visibility = View.VISIBLE


        } else if (paymentAmountExactness == getString(R.string.exact_and_above)) {
            chipMinimumDue.visibility = View.GONE
            chipTotalDue.visibility = View.VISIBLE
            customChip.visibility = View.VISIBLE

        } else if (paymentAmountExactness == getString(R.string.exact)) {
            chipMinimumDue.visibility = View.GONE
            chipTotalDue.visibility = View.GONE
            customChip.visibility = View.VISIBLE


        } else {
            etAmount?.setText(totalDue.toString())
            etAmount?.isEnabled = false
            customChip.visibility = View.VISIBLE
            chipMinimumDue.visibility = View.GONE
            chipTotalDue.visibility = View.VISIBLE
            llMessageError.visibility = View.GONE
            llMessageError.visibility = View.GONE
            billStatus = CUSTOM
            customChip.isChecked = true
        }

        if (totalDue != 0.0) {
            chipTotalDue.visibility = View.VISIBLE
        } else {
            chipTotalDue.visibility = View.GONE
            billStatus = CUSTOM
            customChip.isChecked = true
            etAmount.isEnabled = true
            etAmount.requestFocus()
            etAmount.setText("")
        }
        if (rewardsAssignUpto != 0) {
            tvRewardEarned.text =
                "$rewardsAssigned/$rewardsAssignUpto chips have been earned this month!"
        }
        MonthlyEarnRule.setRewardLimit(
            rewardsCanAssign,
            rewardsAssignUpto,
            rewardsPoint,
            tvRewards,
            tvAtTheRate,
            tvRewardPercentage,
            tvRewardEarned,
            this
        )
        etAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (p0.toString().isNotEmpty()) {
                    val amount = p0.toString().replace(",", "").toDouble()
                    if (billStatus == CUSTOM) {

                        if (amount > 0 && amount < 100) {
                            tvRewards.text = getString(R.string.str_enter_100)
                            tvRewardPercentage.visibility = View.GONE
                            tvAtTheRate.visibility = View.GONE
                            btnOkay.isEnabled = true
                            llRewards.visibility = View.VISIBLE
                            tvRewardEarned.visibility = View.GONE
                        } else {
                            tvRewardPercentage.text = ONE_PERCENT
                            rewardsPoint = roundupAmount(amount, 1.0)
                            tvRewardPercentage.visibility = View.VISIBLE
                            tvAtTheRate.visibility = View.VISIBLE
                            llRewards.visibility = View.VISIBLE
                            MonthlyEarnRule.setRewardLimit(
                                rewardsCanAssign,
                                rewardsAssignUpto,
                                rewardsPoint,
                                tvRewards,
                                tvAtTheRate,
                                tvRewardPercentage,
                                tvRewardEarned,
                                this@LoanSuccessActivity
                            )
                            btnOkay.isEnabled = true
                        }
                    } else {
                        if (amount > 0 && amount < 100) {
                            tvRewards.text = getString(R.string.str_enter_100)
                            tvRewardPercentage.visibility = View.GONE
                            tvAtTheRate.visibility = View.GONE
                            btnOkay.isEnabled = true
                            llRewards.visibility = View.VISIBLE
                            tvRewardEarned.visibility = View.GONE
                        } else {
                            tvRewardPercentage.text = ONE_PERCENT
                            rewardsPoint = roundupAmount(amount, 1.0)
                            tvRewardPercentage.visibility = View.VISIBLE
                            tvAtTheRate.visibility = View.VISIBLE
                            llRewards.visibility = View.VISIBLE
                            MonthlyEarnRule.setRewardLimit(
                                rewardsCanAssign,
                                rewardsAssignUpto,
                                rewardsPoint,
                                tvRewards,
                                tvAtTheRate,
                                tvRewardPercentage,
                                tvRewardEarned,
                                this@LoanSuccessActivity
                            )
                            btnOkay.isEnabled = true
                        }
                        if (paymentAmountExactness == getString(R.string.adhoc)) {
                            btnOkay.isEnabled = true
                        } else if (paymentAmountExactness == getString(R.string.exact_and_below)) {
                            if (totalDue != null) {
                                if (amount > totalDue) {
                                    tvError.text = getString(R.string.str_enter_amount_is_more)
                                    llMessageError.visibility = View.VISIBLE
                                    btnOkay.isEnabled = false
                                }
                            }
                        } else if (paymentAmountExactness == getString(R.string.exact_and_above)) {
                            if (totalDue != null) {
                                if (amount < totalDue) {
                                    tvError.text = getString(R.string.str_amount_less_total_due)
                                    llMessageError.visibility = View.VISIBLE
                                    btnOkay.isEnabled = true
                                }
                            }
                        } else {
                            btnOkay.isEnabled = true
                        }
                    }
                } else {
                    llMessageError.visibility = View.GONE
                    btnOkay.isEnabled = false
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        etAmount.setOnFocusChangeListener { _, hasFocused ->
            if (hasFocused) {
                llAmtView.setBackgroundResource(R.drawable.et_btm_bg)
            } else {
                llAmtView.setBackgroundResource(R.drawable.et_btm_bg_un_focused)

            }
        }


        customChip.setOnClickListener {
            llRewards.visibility = View.GONE
            tvRewards.text = ""
            billStatus = CUSTOM
            etAmount.isEnabled = true
            etAmount.requestFocus()
            etAmount.setText("")
            btnOkay.isEnabled = false
            llMessageTotalDue.visibility = View.GONE
            llMessageMinimumDue.visibility = View.GONE
            tvCaption.text = getString(R.string.str_enter_amount)
            this.let { it1 -> Utils.showKeyboard(it1) }

        }

        btnOkay.setOnClickListener {
            if (etAmount.text.toString().isNotEmpty()) {
                if (etAmount.text.toString().replace(",", "").toDouble() < 10) {
                    tvError.text = getString(R.string.str_amount_greater_10)
                    llMessageError.visibility = View.VISIBLE
                } else if (etAmount.text.toString().replace(",", "").toDouble() >= 1000000) {
                    tvError.text = getString(R.string.str_amount_less_1000000)
                    llMessageError.visibility = View.VISIBLE
                } else {
                    if (loanData.product_number != null) {
                        val amount = etAmount.text.toString().replace(",", "")
                        loanData.customAmount = amount.toDouble()
                        loanData.rewardsPoint = rewardsPoint
                        loanData.billStatus = billStatus
                        val selectedProduct: ArrayList<ProductV2> = ArrayList()
                        selectedProduct.add(loanData)
                        this.startActivity(
                            Intent(
                                this, PaymentMethodsActivity::class.java
                            ).putExtra(IntentKeys.PRODUCT_LIST, selectedProduct)
                                .putExtra(IntentKeys.IS_PAY_TOGETHER, false)
                                .putExtra(IntentKeys.TOTAL_AMOUNT, amount.trim())
                                .putExtra(IntentKeys.REWARDS_POINT, rewardsPoint)
                                .putExtra(IntentKeys.BILL_STATUS, billStatus)
                                .putExtra(IntentKeys.REWARDS_ASSIGN_UPTO, rewardsAssignUpto)
                                .putExtra(IntentKeys.REWARDS_CAN_ASSIGN, rewardsCanAssign)
                                .putExtra(IntentKeys.REWARDS_ASSIGNED, rewardsAssigned)
                        )


                    }
                    Utils.hideKeyboard(this)
                    dialog.dismiss()

                }
            }
        }
        dialog.show()
    }

    private fun openKnowMoreDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_know_more)
        bottomSheetDialog.setCancelable(false)

        val btnSubmit = bottomSheetDialog.findViewById<AppCompatButton>(R.id.btnSubmit)
        val ivCancel = bottomSheetDialog.findViewById<AppCompatImageView>(R.id.ivCancel)
        val info_Image = bottomSheetDialog.findViewById<ImageView>(R.id.info_Image)
        mFirebaseDatabase?.child("payment_info_icon")?.get()?.addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            val options = RequestOptions().placeholder(R.drawable.button_white_bordered)
                .error(R.drawable.button_white_bordered)



            if (info_Image != null) {
                Glide.with(this).load(it.value).apply(options).into(info_Image)
            }
        }?.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }
        btnSubmit?.setOnClickListener {
            bottomSheetDialog.dismiss()
            this.let { it1 -> Utils.showKeyboard(it1) }
        }
        ivCancel?.setOnClickListener {
            bottomSheetDialog.dismiss()

        }

        Utils.hideKeyboard(this)
        bottomSheetDialog.show()
    }


    fun roundupAmount(amount: Double, percentage: Double): Int {
        val percentageAMount = ((percentage * (amount)) / 100)
        return floor(percentageAMount).toInt()
    }
}