package com.cheq.retail.ui.billPayments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.cheq.config.FeatureConfig
import com.cheq.navigation.IntentFactory
import com.cheq.navigation.IntentKey
import com.cheq.retail.R
import com.cheq.retail.application.MainApplication
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.constants.AFConstants.AF_CC_PAYMENT_SUCESS
import com.cheq.retail.constants.AFConstants.AF_CUST_CC_PAYMENT_SUCESS
import com.cheq.retail.constants.AFConstants.AF_CUST_LOAN_SUCESS
import com.cheq.retail.constants.AFConstants.AF_LOAN_PAYMENT_SUCESS
import com.cheq.retail.constants.AFConstants.AF_PAYMENT_SUCESS
import com.cheq.retail.constants.AFConstants.AF_PAY_TOGETHER_PAYMENT_SUCESS
import com.cheq.retail.constants.AFConstants.AF_PAY_TOGETHER_SUCESS
import com.cheq.retail.constants.AFConstants.CC
import com.cheq.retail.constants.AFConstants.FBEvent_CC_PAYMENT_SUCESS
import com.cheq.retail.constants.AFConstants.FBEvent_LOAN_PAYMENT_SUCESS
import com.cheq.retail.constants.AFConstants.FBEvent_PAYMENT_SUCESS
import com.cheq.retail.constants.AFConstants.FBEvent_PAY_TOGETHER_SUCESS
import com.cheq.retail.constants.AFConstants.FB_CC_PAYMENT_SUCESS
import com.cheq.retail.constants.AFConstants.FB_LOAN_SUCESS
import com.cheq.retail.constants.AFConstants.FB_PAY_TOGETHER_PAYMENT_SUCESS
import com.cheq.retail.constants.AppsFlyEvents
import com.cheq.retail.constants.FirebaseLog.FireBaseLogEvent
import com.cheq.retail.databinding.ActivityBillPaymentSuccessfulBinding
import com.cheq.retail.extensions.bankMasterUrl
import com.cheq.retail.extensions.bannerBaseUrl
import com.cheq.retail.extensions.loanMasterUrl
import com.cheq.retail.inappratings.ui.InAppRatingFragment
import com.cheq.retail.inappratings.ui.TOUCHPOINT_PAYIN
import com.cheq.retail.sharePreferences.SharePrefCheqUserId
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.activateCard.CardDetailsActivityNew
import com.cheq.retail.ui.billPayments.viewModel.BillPaymentsViewModel
import com.cheq.retail.ui.cheqsafe.CheqSafeParentFragment
import com.cheq.retail.ui.dashboard.view.fragment.HomeFragment
import com.cheq.retail.ui.login.modelv2.productv1.ProductV2
import com.cheq.retail.ui.main.MainActivity
import com.cheq.retail.ui.main.helper.MonthlyEarnRule
import com.cheq.retail.ui.referral.ReferralActivity
import com.cheq.retail.ui.referral.ReferralHistoryActivity
import com.cheq.retail.ui.socialLogin.model.CheqSafeScreens
import com.cheq.retail.ui.socialLogin.model.CheqSafeStatus
import com.cheq.retail.utils.*
import com.cheq.retail.utils.ImageUtils.loadSvg
import com.cheq.retail.utils.RealTimeDatabase.IS_IN_APP_REVIEW_ENABLE
import com.cheq.retail.utils.Utils.Companion.spellOut
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.ZoneOffset
import javax.inject.Inject

@AndroidEntryPoint
class BillPaymentSuccessfulActivity : BaseActivity() {
    lateinit var binding: ActivityBillPaymentSuccessfulBinding
    var logo = ""
    var amount = ""
    var isClicked = false
    private var paymentModeName = ""
    private var paymentModeLogo = ""
    var orderId = ""
    var date = ""
    var upiLogo = ""
    var upiName = ""
    var rewardsPoint = 0
    var selectedProduct: ArrayList<ProductV2> = ArrayList()
    var rewardsUsed = false
    var payByRewards = false
    var billStatus = ""
    var payOutMode = ""
    var productType = ""
    var isCard = false
    var payoutNarration = ""
    private var rewardsCanAssign = 0
    private var rewardsAssigned = 0
    private var rewardsAssignUpto = 0
    private var viewModel: BillPaymentsViewModel? = null
    lateinit var prefs: SharePrefs
    private var isPaytogether = false
    val REFERRAL_TAG="referral_tag"

    @Inject
    lateinit var featureConfig: FeatureConfig

    @Inject
    lateinit var intentFactory: IntentFactory

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[BillPaymentsViewModel::class.java]
        setUpUi()
        catchIntent()
        setUpAction()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun catchIntent() {
        billStatus = intent.getStringExtra(IntentKeys.BILL_STATUS).toString()
        upiLogo = intent.getStringExtra(IntentKeys.UPI_LOGO).toString()
        upiName = intent.getStringExtra(IntentKeys.UPI_NAME).toString()
        amount = intent.getStringExtra(IntentKeys.AMOUNT).toString()
        paymentModeName = intent.getStringExtra(IntentKeys.PAYMENT_MODE_NAME).toString()
        paymentModeLogo = intent.getStringExtra(IntentKeys.PAYMENT_MODE_LOGO).toString()
        orderId = intent.getStringExtra(IntentKeys.TXN_ID).toString()
        payOutMode = intent.getStringExtra(IntentKeys.PAYOUT_MODE).toString()
        productType = intent.getStringExtra(IntentKeys.PRODUCT_TYPE).toString()
        date = intent.getStringExtra(IntentKeys.DATE).toString()
        rewardsPoint = intent.getIntExtra(IntentKeys.REWARDS_POINT, 0)
        if (intent.getSerializableExtra(IntentKeys.PRODUCT_LIST) != null) {
            selectedProduct =
                intent.getSerializableExtra(IntentKeys.PRODUCT_LIST) as ArrayList<ProductV2>
        }
        rewardsUsed = intent.getBooleanExtra(IntentKeys.CHIP_USED, false)
        payByRewards = intent.getBooleanExtra(IntentKeys.PAY_BY_REWARDS, false)
        rewardsAssignUpto = intent.getIntExtra(IntentKeys.REWARDS_ASSIGN_UPTO, 0)
        rewardsCanAssign = intent.getIntExtra(IntentKeys.REWARDS_CAN_ASSIGN, 0)
        rewardsAssigned = intent.getIntExtra(IntentKeys.REWARDS_ASSIGNED, 0)
        payoutNarration = intent.getStringExtra(IntentKeys.PAYOUT_NARRATION).toString()
        binding.llCreditCard.tvSingleAmount.text =
            "₹${Utils.getFormattedDecimal(amount.toDouble())}"
        try {
            if (payoutNarration.isNotEmpty()) {
                val narrationSplit = payoutNarration.split("|")
                if (narrationSplit.isNotEmpty()) {
                    val firstNarration = narrationSplit[0]
                    val secondNarration = narrationSplit[1]
                    val thirdNarration = narrationSplit[2]
                    binding.estTime.text = Html.fromHtml(firstNarration)

                    spanString(secondNarration, binding.llCreditCard.tvPaymentDate)
                    spanString(thirdNarration, binding.llCreditCard.tvEstDay)
                    binding.llCreditCard.tvSingleAmount.text =
                        "₹${Utils.getFormattedDecimal(amount.toDouble())}"

                }

            }
        } catch (e: Exception) {

        }



        binding.llCreditCard.tvPayingAmount.text =
            "₹${Utils.getFormattedDecimal(amount.toDouble())}"
        if (selectedProduct.size > 1) {
            binding.llCreditCard.llSingleCard.visibility = View.GONE
            binding.llCreditCard.flLoan.visibility = View.GONE
            binding.llCreditCard.llPayTogether.visibility = View.VISIBLE
            binding.ivBillPay.visibility = View.GONE
            binding.llCreditCard.tvPayTogetherProductCount.text =
                "${selectedProduct.size} PRODUCTS VIA"
            binding.llCreditCard.tvPayingAmount.text =
                "₹${Utils.getFormattedDecimal(amount.toDouble())}"
            binding.tvTitle.text = getString(
                R.string.payment_success_txt_pay_together,
                Utils.getFormattedDecimal(amount.toDouble()),
                spellOut(selectedProduct.size.toString())
            )
            if (selectedProduct[0].product_type == CC) {
                binding.llCreditCard.ivBankLogoOne.loadSvg("${prefs.bankMasterUrl}${selectedProduct[0].bankMasterRecord?.id}-logo.svg")
            } else {
                binding.llCreditCard.ivBankLogoOne.loadSvg("${prefs.loanMasterUrl}${selectedProduct[0].bankMasterRecord?.id}-logo.svg")
            }
            if (selectedProduct[1].product_type == CC) {
                binding.llCreditCard.ivBankLogoTwo.loadSvg("${prefs.bankMasterUrl}${selectedProduct[1].bankMasterRecord?.id}-logo.svg")
            } else {
                binding.llCreditCard.ivBankLogoTwo.loadSvg("${prefs.loanMasterUrl}${selectedProduct[1].bankMasterRecord?.id}-logo.svg")
            }
            binding.llCreditCard.flTotalProductList.visibility = View.VISIBLE
            if (selectedProduct.size > 2) {
                binding.llCreditCard.llCardCount.visibility = View.VISIBLE
                binding.llCreditCard.tvCardCount.text = "+${selectedProduct.size - 2}"
            } else {
                binding.llCreditCard.llCardCount.visibility = View.GONE
            }
            AppsFlyEvents.logEventFly(
                this,
                AF_PAYMENT_SUCESS,
                AF_PAY_TOGETHER_SUCESS,
                AF_PAY_TOGETHER_PAYMENT_SUCESS
            )
            FireBaseLogEvent(
                this,
                FBEvent_PAYMENT_SUCESS,
                FBEvent_PAY_TOGETHER_SUCESS,
                FB_PAY_TOGETHER_PAYMENT_SUCESS
            )
            isPaytogether = true

            MparticleUtils.logCurrentScreen(
                "/pay-together/success",
                "The customer views the payment success screen for pay together",
                "",
                "",
                "",
                "",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.PAY_TOGETHER_SUCESS),
                this
            )
        } else {


            isPaytogether = false

            binding.tvTitle.text =
                getString(R.string.success_txt, Utils.getFormattedDecimal(amount.toDouble()))

            if (selectedProduct[0].product_type == CC) {
                isCard = true
                binding.llCreditCard.llSingleCard.visibility = View.VISIBLE
                binding.llCreditCard.llPayTogether.visibility = View.GONE
                binding.llCreditCard.flLoan.visibility = View.GONE
                binding.ivBillPay.visibility = View.GONE
                binding.llCreditCard.ivBankImage.loadSvg("${prefs.bankMasterUrl}${selectedProduct[0].bankMasterRecord?.id}-logo-with-name.svg")
                if (selectedProduct[0].bankMasterRecord?.ui_config != null) {
                    GradientUtils.setBoarderStroke(
                        selectedProduct[0].bankMasterRecord?.ui_config?.cardColor
                            ?: (ContextCompat.getColor(this, R.color.white)).toString(),
                        selectedProduct[0].bankMasterRecord?.ui_config?.opacity_border
                            ?: (ContextCompat.getColor(this, R.color.white)).toString(),
                        true,
                        binding.llCreditCard.llSingleCard
                    )
                    GradientUtils.setBackGround(
                        selectedProduct[0].bankMasterRecord?.ui_config?.cardColor
                            ?: (ContextCompat.getColor(this, R.color.white)).toString(),
                        "",
                        selectedProduct[0].bankMasterRecord?.ui_config?.opacity_topLeft
                            ?: (ContextCompat.getColor(this, R.color.white)).toString(),
                        selectedProduct[0].bankMasterRecord?.ui_config?.opacity_bottomRight
                            ?: (ContextCompat.getColor(this, R.color.white)).toString(),
                        binding.llCreditCard.llCardSolidBackGround
                    )
                }

                AppsFlyEvents.logEventFly(
                    this,
                    AF_PAYMENT_SUCESS,
                    AF_CC_PAYMENT_SUCESS,
                    AF_CUST_CC_PAYMENT_SUCESS
                )
                FireBaseLogEvent(
                    this, FBEvent_PAYMENT_SUCESS,
                    FBEvent_CC_PAYMENT_SUCESS, FB_CC_PAYMENT_SUCESS
                )

                MparticleUtils.logCurrentScreen(
                    "/cc-payment/success",
                    "The customer views the credit card activation as success",
                    "",
                    "",
                    "",
                    "",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Success),
                    this@BillPaymentSuccessfulActivity
                )
            } else {
                isCard = false
                binding.llCreditCard.llSingleCard.visibility = View.GONE
                binding.llCreditCard.llPayTogether.visibility = View.GONE
                binding.ivBillPay.visibility = View.VISIBLE
                binding.llCreditCard.flLoan.visibility = View.VISIBLE
                binding.llCreditCard.loanCard.flSolidColor.setBackgroundResource(R.drawable.loan_bottom_shap)
                val bg: Drawable? = binding.llCreditCard.loanCard.flSolidColor.background
                bg?.setTint(Color.parseColor(selectedProduct[0].bankMasterRecord?.ui_config?.cardColor))

                AppsFlyEvents.logEventFly(
                    this,
                    AF_PAYMENT_SUCESS,
                    AF_LOAN_PAYMENT_SUCESS,
                    AF_CUST_LOAN_SUCESS
                )
                FireBaseLogEvent(
                    this, FBEvent_PAYMENT_SUCESS,
                    FBEvent_LOAN_PAYMENT_SUCESS, FB_LOAN_SUCESS
                )

                MparticleUtils.logCurrentScreen(
                    "/loan-payment/success",
                    "The customer views the loan payment as success",
                    "",
                    "",
                    "",
                    "",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.LOAN_Payment_SUCCESS),
                    this
                )
            }

            binding.llCreditCard.tvSingleCardNumber.text = "··· ${selectedProduct[0].last4}"
            binding.llCreditCard.loanCard.tvCardNumber.text = "··· ${selectedProduct[0].last4}"
            binding.llCreditCard.tvSingleBankName.text =
                selectedProduct[0].bankMasterRecord?.bankName ?: ""

            binding.llCreditCard.loanCard.ivBankImage.loadSvg("${prefs.loanMasterUrl}${selectedProduct[0].bankMasterRecord?.id}-logo-with-name.svg")
            if (selectedProduct[0].card_network == CardDetailsActivityNew.MASTER_CARD) {
                binding.llCreditCard.ivSingleCardType.setImageResource(R.drawable.ic_mastercard)
            }
            binding.llCreditCard.loanCard.tvBankName.text =
                "${selectedProduct[0].bankMasterRecord?.billerName}"
            if (selectedProduct[0].card_network == CardDetailsActivityNew.VISA) {
                binding.llCreditCard.ivSingleCardType.setImageResource(R.drawable.visa)
            }
            if (selectedProduct[0].card_network == CardDetailsActivityNew.DINERS_CLUB) {
                binding.llCreditCard.ivSingleCardType.setImageResource(R.drawable.ic_dinner_icon)
            }
            if (selectedProduct[0].card_network == CardDetailsActivityNew.AMERICAN_EXPRESS) {
                binding.llCreditCard.ivSingleCardType.setImageResource(R.drawable.ic_american_icon)
            }
            if (selectedProduct[0].card_network == CardDetailsActivityNew.RUPAY) {
                binding.llCreditCard.ivSingleCardType.setImageResource(R.drawable.ic_rupay_card_icon)
            }
        }
        if (rewardsUsed) {
            binding.llCreditCard.llPayRewards.visibility = View.VISIBLE
        } else {
            binding.llCreditCard.llPayRewards.visibility = View.GONE
        }
        if (payByRewards) {
            binding.llCreditCard.llPayRewards.visibility = View.VISIBLE
            binding.llCreditCard.llBankDetails.visibility = View.GONE
        }
        if (rewardsPoint > 0) {
            binding.llRewards.visibility = View.VISIBLE
        } else {
            binding.llRewards.visibility = View.VISIBLE
        }



        if (selectedProduct.size > 1) {
            binding.rewardHeading3.text = getString(R.string.str_pay_total_bill_together)
        } else {
            if (amount.toDouble()
                    .toInt() < 100 && billStatus == HomeFragment.FULL || billStatus == HomeFragment.CUSTOM
            ) {
                binding.rewardHeading3.text = getString(R.string.str_pay_total_bill)
            }

            if (amount.toDouble().toInt() >= 100 && billStatus == HomeFragment.FULL) {
                MonthlyEarnRule.setRewardLimit(
                    rewardsCanAssign,
                    rewardsAssignUpto,
                    rewardsPoint,
                    binding.rewardHeading3,
                    this
                )

            }

            if (amount.toDouble().toInt() >= 100 && billStatus == HomeFragment.MIN) {
                MonthlyEarnRule.setRewardLimit(
                    rewardsCanAssign,
                    rewardsAssignUpto,
                    rewardsPoint,
                    binding.rewardHeading3,
                    this
                )
            }
            if (amount.toDouble().toInt() >= 100 && billStatus == HomeFragment.CUSTOM) {
                MonthlyEarnRule.setRewardLimit(
                    rewardsCanAssign, rewardsAssignUpto, rewardsPoint, binding.rewardHeading3, this
                )
            }
        }


        binding.llCreditCard.tvTxnId.text = orderId
        binding.llCreditCard.tvDetails.text = getTime()


        if (upiLogo.isNotEmpty()) {
            val decodedString: ByteArray = Base64.decode(upiLogo, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            binding.llCreditCard.ivPaymentModeLogo.setImageBitmap(decodedByte)
        } else {
            binding.llCreditCard.ivPaymentModeLogo.loadSvg("${prefs.bankMasterUrl}${paymentModeLogo}-logo.svg")
        }
        if (upiName.isNotEmpty()) {
            binding.llCreditCard.tvPaymentModeName.text = upiName
        } else {
            binding.llCreditCard.tvPaymentModeName.text = paymentModeName.substringBefore("*")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTime(): String {

        try {
            val l = Instant.parse(date).atOffset(ZoneOffset.UTC)
            var month = "" + l.month
            month = month.lowercase()
            val formatedMonth: String = month.substring(0, 1).uppercase() + month.substring(1)
            return "${l.dayOfMonth}-${
                formatedMonth.subSequence(
                    0, 3
                )
            } at ${Utils.convertTimeToAmPm("${l.hour}:${l.minute}:${l.second}")}"
        } catch (e: Exception) {
            return ""
        }

    }

    @SuppressLint("ObsoleteSdkInt")
    private fun setUpUi() {
        prefs = SharePrefs.getInstance(this)!!;
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bill_payment_successful)
        binding.activity = this
        binding.ivRefer.loadSvg(prefs.bannerBaseUrl + "Refer-and-Earn.svg")
        binding.ivVoucher.loadSvg(prefs.bannerBaseUrl + "Rewards-Tab.svg")

        viewModel?.cheqSafeStatusObserver?.observe(this, Observer {
            //checkOnFirebase(CheqSafeStatus.NO_EMAIL_LINKED.name)
            when (val status = it ?:  CheqSafeStatus.NO_EMAIL_LINKED) {
                CheqSafeStatus.NO_EMAIL_LINKED, CheqSafeStatus.FAILED  -> {
                    checkOnFirebase(status.name)
                }
                CheqSafeStatus.LINKED -> {
                    binding.cheqSafeLayout.clMainLayout.isVisible = false
                    binding.tvActionRecommended.isVisible = false
                }
            }


        })



        /*CheqSafeRealtimeDatabase.checkSafeFromFb { it ->
            if (it != null) {
                binding.tvActionRecommended.visibility = View.VISIBLE
                binding.cheqSafeLayout.demoView.visibility = View.VISIBLE
                binding.tvActionRecommended.text = it.title

                val layoutParams =
                    binding.cheqSafeLayout.demoView.layoutParams as ConstraintLayout.LayoutParams
                layoutParams.dimensionRatio = it.ratio
                binding.cheqSafeLayout.demoView.layoutParams = layoutParams
                if (it.banner_type == 1) {
                    binding.cheqSafeLayout.ivOfferImage.let { it1 ->
                        Glide.with(this).load(it.banner_url).into(
                            it1
                        )
                    }
                }else {
                    binding.cheqSafeLayout.animationView.setFailureListener {

                    }
                    binding.cheqSafeLayout.animationView.setAnimationFromUrl(it.banner_url)


                }
              /*  binding.cheqSafeLayout.demoView.setOnClickListener { view ->
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
                binding.tvActionRecommended.visibility = View.GONE
                binding.cheqSafeLayout.demoView.visibility = View.GONE
            }
        }*/
        Utils.setLightStatusBar(this)
        Utils.setColorStatusBar(ContextCompat.getColor(this, R.color.colorPrimary), this)
        GradientUtils.setBackGroundSolid(
            (ContextCompat.getColor(this, R.color.white)), binding.llCreditCard.llCard
        )
        binding.llViewDetails.setOnClickListener {
            MparticleUtils.logEvent(
                "CC_Payment_Success_ViewDetails_Clicked",
                "User chooses to view the transaction details for the successful payment",
                "Unique",
                "Continue",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Success_ViewDetails_Clicked),
                this@BillPaymentSuccessfulActivity
            )
            if (isClicked) {
                binding.llCreditCard.llPaymentDetails.visibility = View.GONE
                animateViewTranslation(1.0F, 1.0F, 400, binding.tvViewDetails)
                isClicked = false
                binding.ivArrow.animate().translationX(1.0F).rotation(0f).setDuration(600).start()

            } else {
                animateViewTranslation(0F, 0.0F, 400, binding.tvViewDetails)
                binding.llCreditCard.llPaymentDetails.visibility = View.VISIBLE
                binding.ivArrow.animate().translationX(-100F).rotation(180f).setDuration(600)
                    .start()
                isClicked = true
            }
        }
        startAnimation(1500, binding.flBg)
        startAnimation(1700, binding.llBg)


        try {
            Handler(Looper.getMainLooper()).postDelayed({
                TransitionManager.beginDelayedTransition(
                    binding.llCreditCard.llPaymentDate, AutoTransition()
                )
                binding.llCreditCard.llPaymentDate.visibility = View.VISIBLE
                binding.llViewDetails.visibility = View.VISIBLE
                binding.llCreditCard.llCard.layoutParams = FrameLayout.LayoutParams(
                    LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                    LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                )

            }, 2000)
        } catch (_: Exception) {


        }
        try {
            Handler(Looper.getMainLooper()).postDelayed({
                animateViewAlpha(1.0f, binding.llBanner)
                animateViewAlpha(1.0f, binding.btnOk)
            }, 2400)
        } catch (_: Exception) {


        }
        binding.ivVoucher.setOnClickListener {
            MparticleUtils.logEvent(
                "CC_Payment_Success_Nudge1",
                "User chooses to click on the top nudge ",
                "Unique",
                "Continue",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Success_Nudge1),

                this@BillPaymentSuccessfulActivity
            )

            startActivity(Intent(
                this@BillPaymentSuccessfulActivity, MainActivity::class.java
            ).apply {
                putExtra("COMING_FROM_BILL", "COMING_FROM_BILL")
            })

            finish()
        }
        binding.cheqSafeLayout.demoView.setOnClickListener {
            MparticleUtils.logEvent(
                "CC_Payment_Success_Nudge2",
                "User chooses to click on the second nudge",
                "Unique",
                "Continue",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Success_Nudge2),

                this@BillPaymentSuccessfulActivity
            )
        }

        binding.cheqSafeLayout.ivOfferImage.setOnClickListener {
            startStartCheqSafeFlow()
        }

        binding.ivRefer.setOnClickListener {
            if (isPaytogether) {
                MparticleUtils.logEvent(
                    "Pay_Together_ReferAndEarn_Banner_Clicked",
                    "User click on the refer and earn banner post successful pay together payment",
                    "Unique",
                    "Continue",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Pay_Together_ReferAndEarn_Banner_Clicked),
                    this@BillPaymentSuccessfulActivity
                )
            } else {
                if (isCard) {
                    MparticleUtils.logEvent(
                        "CC_Payment_ReferAndEarn_Banner_Clicked",
                        "User click on the refer and earn banner post successful cc payment",
                        "Unique",
                        "Continue",
                        SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_ReferAndEarn_Banner_Clicked),
                        this@BillPaymentSuccessfulActivity
                    )
                } else {
                    MparticleUtils.logEvent(
                        "Loan_Payment_ReferAndEarn_Banner_Clicked",
                        "User click on the refer and earn banner post successful loan payment",
                        "Unique",
                        "Continue",
                        SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Loan_Payment_ReferAndEarn_Banner_Clicked),
                        this@BillPaymentSuccessfulActivity
                    )
                }
            }

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
                Intent(
                    this@BillPaymentSuccessfulActivity, ReferralActivity::class.java
                ).putExtra(REFERRAL_TAG,true)
            }
            finish()
        }
        viewModel?.lockedChipLiveData?.observe(this) {
            try {
                if (it?.showPaymentMessage != null) {
                    binding.unlockedChipTV.isVisible = it.showPaymentMessage
                    binding.unlockedChipTV.text = it.paymentMessage
                } else {
                    binding.unlockedChipTV.isVisible = false
                }
            } catch (e: Exception) {

            }
        }

    }

    fun checkOnFirebase(status: String) {
        CheqSafeRealtimeDatabase.checkSafeFromFb(from = CheqSafeScreens.PAYMENT, onValueFetched = {
            if (it != null) {
                binding.cheqSafeLayout.clMainLayout.isVisible = it.isVisible
                binding.tvActionRecommended.isVisible = it.isVisible
                val bannerUrl =
                    if (status == CheqSafeStatus.FAILED.name) it.failedBannerUrl else it.successBannerUrl

                binding.cheqSafeLayout.ivOfferImage.let { it1 ->
                    Glide.with(applicationContext)
                        .load(bannerUrl)
                        .into(binding.cheqSafeLayout.ivOfferImage)
                }
            } else {
                binding.tvActionRecommended.visibility = View.GONE
            }
        })
    }

    private fun startStartCheqSafeFlow() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.cheqSafeContainer, CheqSafeParentFragment())
        fragmentTransaction.commit()
    }

    private fun animateViewTranslation(
        translation: Float,
        alpha: Float,
        duration: Long,
        view: View
    ) {
        TransitionManager.beginDelayedTransition(
            binding.llCreditCard.llPaymentDetails,
            AutoTransition()
        )
        view.animate().translationX(translation).alpha(alpha).duration = duration
    }

    private fun animateViewAlpha(fl: Float, llBanner: View) {
        llBanner.animate().alpha(fl).setUpdateListener {
            llBanner.visibility = View.VISIBLE
        }
    }

    private fun setUpAction() {
        binding.pendingChipTimelineView.setOnClickListener {
            startActivity(Intent(applicationContext, ReferralHistoryActivity::class.java))
        }
    }

    fun onButtonClick() {
        if (isCard) {
            MparticleUtils.logEvent(
                "CC_Payment_Success_Clicked",
                "User chooses to click on the third nudge",
                "Unique",
                "Continue",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Success_Clicked),
                this@BillPaymentSuccessfulActivity
            )
        } else {
            MparticleUtils.logEvent(
                "Loan_addition_Success_Clicked",
                "User chooses to proceed to Home/Product after addition success",
                "Unique",
                "Continue",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.LOAN_Payment_Success_CLICKED),
                this@BillPaymentSuccessfulActivity
            )
        }
        startInAppRatingFlow()
    }

    private fun startInAppRatingFlow() {
        RealTimeDatabase.checkIsInAppReviewEnable(IS_IN_APP_REVIEW_ENABLE) {
            if (it) {
                val cheqUserId =
                    SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                        ?.getString(SharePrefsKeys.CHEQ_USER_ID) ?: "NA"
                val touchpoint = TOUCHPOINT_PAYIN
                val inappFragment = InAppRatingFragment.newInstance(cheqUserId, touchpoint)
                inappFragment.show(supportFragmentManager, InAppRatingFragment.TAG)
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
        }

    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
    }

    private fun startAnimation(duration: Long, viewToAnimate: View) {
        val animation: Animation = AnimationUtils.loadAnimation(this, R.anim.slide_down)
        viewToAnimate.startAnimation(animation)
        animation.duration = duration
    }

    fun spanString(actualText: String, view: AppCompatTextView) {
        val spannable = SpannableStringBuilder(actualText)
        val typefaceBold = ResourcesCompat.getFont(this, R.font.basiercircle_semibold)
        if (typefaceBold != null) {
            spannable.setSpan(
                StyleSpan(typefaceBold.style),
                spannable.indexOf("<b>"), spannable.indexOf("</b>"), Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
        }

        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorGreyText)),
            spannable.indexOf("</b>"),
            spannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.delete(0, 3)
        spannable.delete(spannable.indexOf("</b>"), spannable.indexOf("</b>") + 4)
        view.setText(
            spannable, TextView.BufferType.SPANNABLE
        )
    }
}
