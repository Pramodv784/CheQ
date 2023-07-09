package com.cheq.retail.ui.billSummary.bottomSheetFragment

import android.app.Dialog
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.cheq.retail.R
import com.cheq.retail.base.FirebaseRemoteConfigUtils
import com.cheq.retail.constants.AFConstants
import com.cheq.retail.databinding.FragmentBillSummaryBinding
import com.cheq.retail.extensions.bankMasterUrl
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.billPayments.PaymentMethodsActivity
import com.cheq.retail.ui.billPayments.PaymentMethodsActivity.SupportedUPIApps.Companion.FRAGMENT_TAG
import com.cheq.retail.ui.billSummary.BillSummaryListener
import com.cheq.retail.ui.billSummary.adapter.BillSummaryAdapter
import com.cheq.retail.ui.billSummary.model.*
import com.cheq.retail.ui.main.helper.MonthlyEarnRule
import com.cheq.retail.ui.main.helper.PopupHelper
import com.cheq.retail.utils.Base64
import com.cheq.retail.utils.ImageUtils.loadSvg
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.crashlytics.FirebaseCrashlytics


class BillSummaryFragment: BottomSheetDialogFragment(), BillSummaryListener {
    private lateinit var binding: FragmentBillSummaryBinding
    var prefs: SharePrefs? = null
    var serviceFess = 0
    var payableAmount = 0
    var netEarning = 0
    var localRewardPoint = 0
    var rewardPoint = 0
    private var activity: PaymentMethodsActivity? = null
    var response: BillSummaryResponse? = null
    private var callback: BillSummaryCall? = null
    private var logo: String = ""
    private var originalBankName: String = ""
    private var paymentType: String = ""
    private var productItemList: ArrayList<ProductItem> = arrayListOf()
    private var isRewardUsed: Boolean = false
    private var rewardsCanAssign: Int = 0
    private var rewardsAssigned: Int = 0
    private var rewardsAssignUpto: Int = 0
    private var billSummaryResponse: BillSummaryResponse? = null
    private var isClicked: Boolean = true
    private var bankName: String? = null
    private var payinAmount: String? = null
    private var lastFour: String? = null
    private var productType: String? = null
    private var paymentMethodName: String? = null

    companion object {
        private const val BILL_SUMMARY_LOGO = "BILL_SUMMARY_LOGO"
        private const val BILL_SUMMARY_BANK_NAME = "BILL_SUMMARY_BANK_NAME"
        private const val BILL_SUMMARY_PAYMENT_TYPE = "BILL_SUMMARY_PAYMENT_TYPE"
        private const val BILL_SUMMARY_REWARD_USED = "BILL_SUMMARY_REWARD_USED"
        private const val BILL_SUMMARY_REWARD_ASSIGN = "BILL_SUMMARY_REWARD_ASSIGN"
        private const val BILL_SUMMARY_REWARD_ASSIGNED = "BILL_SUMMARY_REWARD_ASSIGNED"
        private const val BILL_SUMMARY_REWARD_ASSIGNED_UPTO = "BILL_SUMMARY_REWARD_ASSIGNED_UPTO"
        private const val BILL_SUMMARY_REWARD_CLICKED = "BILL_SUMMARY_REWARD_CLICKED"
        private const val BANK_NAME = "BANK_NAME"
        private const val BILL_SUMMARY_PAYIN_AMOUNT = "BILL_SUMMARY_PAYIN_AMOUNT"
        private const val BILL_SUMMARY_LAST_FOUR = "BILL_SUMMARY_LAST_FOUR"
        private const val BILL_SUMMARY_PAYMENT_METHOD_NAME = "BILL_SUMMARY_PAYMENT_METHOD_NAME"
        private const val BILL_SUMMARY_PRODUCT_TYPE = "BILL_SUMMARY_PRODUCT_TYPE"
        fun newInstance(
            logo: String = "",
            originalBankName: String = "",
            paymentType: String,
            productItemList: ArrayList<ProductItem>,
            isRewardUsed: Boolean = false,
            rewardsCanAssign: Int,
            rewardsAssigned: Int,
            rewardsAssignUpto: Int,
            billSummaryResponse: BillSummaryResponse? = null,
            isClicked: Boolean = true,
            bankName: String? = null,
            payinAmount: String? = null,
            lastFour: String? = null,
            paymentMethodName: String? = null,
            productType: String? = null
        ): BillSummaryFragment {
            return BillSummaryFragment().apply {
                setProductItemList(productItemList)
                setBillSummaryResponse(billSummaryResponse)
                val bundle = Bundle().apply {
                    putString(BILL_SUMMARY_LOGO, logo)
                    putString(BILL_SUMMARY_BANK_NAME, originalBankName)
                    putString(BILL_SUMMARY_PAYMENT_TYPE, paymentType)
                    putBoolean(BILL_SUMMARY_REWARD_USED, isRewardUsed)
                    putBoolean(BILL_SUMMARY_REWARD_CLICKED, isClicked)
                    putInt(BILL_SUMMARY_REWARD_ASSIGN, rewardsCanAssign)
                    putInt(BILL_SUMMARY_REWARD_ASSIGNED, rewardsAssigned)
                    putInt(BILL_SUMMARY_REWARD_ASSIGNED_UPTO, rewardsAssignUpto)
                    putString(BANK_NAME, bankName)
                    putString(BILL_SUMMARY_PAYIN_AMOUNT, payinAmount)
                    putString(BILL_SUMMARY_LAST_FOUR, lastFour)
                    putString(BILL_SUMMARY_PAYMENT_METHOD_NAME, paymentMethodName)
                    putString(BILL_SUMMARY_PRODUCT_TYPE, productType)
                }
                arguments = bundle
            }
        }
    }

    fun setProductItemList(productItemList: ArrayList<ProductItem>) {
        this.productItemList = productItemList
    }

    fun setBillSummaryResponse(billSummaryResponse: BillSummaryResponse?) {
        this.billSummaryResponse = billSummaryResponse
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBillSummaryBinding.inflate(layoutInflater, container, false)
        dialog?.setCancelable(false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            logo = it.getString(BILL_SUMMARY_LOGO, "")
            originalBankName = it.getString(BILL_SUMMARY_BANK_NAME, "")
            paymentType = it.getString(BILL_SUMMARY_PAYMENT_TYPE, "")
            isRewardUsed = it.getBoolean(BILL_SUMMARY_REWARD_USED, false)
            isClicked = it.getBoolean(BILL_SUMMARY_REWARD_CLICKED, false)
            rewardsCanAssign = it.getInt(BILL_SUMMARY_REWARD_ASSIGN)
            rewardsAssigned = it.getInt(BILL_SUMMARY_REWARD_ASSIGNED)
            rewardsAssignUpto = it.getInt(BILL_SUMMARY_REWARD_ASSIGNED_UPTO)
            bankName = it.getString(BANK_NAME)
            payinAmount = it.getString(BILL_SUMMARY_PAYIN_AMOUNT)
            lastFour = it.getString(BILL_SUMMARY_LAST_FOUR)
            productType = it.getString(BILL_SUMMARY_PRODUCT_TYPE)
            paymentMethodName = it.getString(BILL_SUMMARY_PAYMENT_METHOD_NAME)
        }
    }

    interface BillSummaryCall {
        fun payCallBack()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        billSummaryResponse?.let {
            initView(billSummaryResponse)
            setUpAction()
        }
    }

    private fun initView(response: BillSummaryResponse?) {

        prefs = SharePrefs.getInstance(requireContext())

        if (response?.footers != null) {
            if (response.footers[0]?.key == BillSummaryAdapter.BILL.PAYABLE_AMOUNT.key) {
                payableAmount = response.footers[0]?.value ?: 0
                binding.tvPayable.text = response.footers[0]?.displayText
                binding.tvPayableAmount.text = requireContext().getString(
                    R.string.str_rs_symbol,
                    Utils.getFormattedDecimal(response.footers[0]?.value?.toDouble() ?: 0.0)
                )
                binding.btmPay.btnPay.text = requireContext().getString(
                    R.string.str_pay_rs,
                    Utils.getFormattedDecimal(response.footers[0]?.value?.toDouble() ?: 0.0)
                )
            }

        }
        binding.tvRewardsEarnAt.text = "CheQ Chips earned @ ${Utils.getRewardRate(productItemList?.get(0)?.id, Utils.itemList)}%"
        if (rewardsAssignUpto != 0) {
            binding.tvRewardLimit.text =
                requireContext().getString(
                    R.string.str_already_earned_this_month,
                    rewardsAssigned,
                    rewardsAssignUpto
                )
        }

        localRewardPoint = Utils.roundupAmount(
            payableAmount.toDouble(),
            PaymentMethodsActivity.SupportedUPIApps.DEFAULT_PERCENTAGE,
            null,
            productItemList?.get(0)?.id,
            Utils.itemList
        )
        rewardPoint = MonthlyEarnRule.getLimit(
            rewardsCanAssign,
            rewardsAssignUpto,
            localRewardPoint,
            binding.tvRewardLimit,
            binding.tvChipEarned,
        )
        val billSummaryAdapter = BillSummaryAdapter(
            response?.payTogether,
            response?.isRewardsFeeApplicable,
            requireContext()
        ) {
            serviceFess = it ?: 0
            if (rewardPoint > 0) {
                netEarning = rewardPoint.minus(serviceFess)
            }


            try {
                MparticleUtils.logBillSummaryEvent(
                    "/cc-payment/bill-summary",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_PAYMENT_BILL_SUMMARY),
                    mapOf(
                        AFConstants.PAYABLE_AMOUNT to response?.footers?.get(0)?.value as Int,
                        AFConstants.MONTHLY_LIMIT_HIT to if (rewardsCanAssign > 0) AFConstants.NO else AFConstants.YES,
                        AFConstants.PAY_METHOD to response.paymentMode as String,
                        AFConstants.REWARD_USE to isRewardUsed,
                        AFConstants.REWARDS_EARN to if (localRewardPoint > 0) localRewardPoint else 0,
                        AFConstants.NET_EARNINGS to netEarning,
                        AFConstants.FEES to serviceFess,
                        AFConstants.PAYMENT_METHOD_INVALID to if (response.isLimitBreach == true) AFConstants.YES else AFConstants.NO,
                        AFConstants.BANK_NAME_ to bankName as String,
                        AFConstants.PAYIN_AMOUNT to payinAmount as String,
                        AFConstants.CC_LAST_4_DIGITS to lastFour as String,
                        AFConstants.PRODUCT_TYPE to productType as String,
                        AFConstants.PAYMENT_METHOD_NAME to paymentMethodName as String
                    )
                )
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
            setNetEarningView(response, response?.payTogether)

        }

        billSummaryAdapter.submitList(response?.lineItems)
        binding.rvBillSummary.apply {
            adapter = billSummaryAdapter
        }
        if (rewardPoint > 0 ) {
            binding.llChipsEarned.visibility = View.VISIBLE
        } else {
            binding.llChipsEarned.visibility = View.GONE
        }


        val imageUrl = "${prefs?.bankMasterUrl}${logo}-logo.svg"
        binding.btmPay.clPaying.setOnClickListener {
            dialog?.dismiss()
            MparticleUtils.logEvent(
                "CC_Payment_BillSummary_ChangePaymentMethod_Clicked",
                "User shows intent to change payment method",
                "Unique",
                "Back",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_PAYMENT_BILL_SUMMARY_CHANGED_PAYMENT_METHOD_CLICKED),
                requireContext()
            )
        }
        if (paymentType == PaymentMethodsActivity.SupportedUPIApps.UPI) {
            val decodedString: ByteArray = Base64.decode(logo, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            binding.btmPay.ivPayingLogo.setImageBitmap(decodedByte)
        } else {
            binding.btmPay.ivPayingLogo.loadSvg(imageUrl)
        }

        if (response?.isLimitBreach == true) {
            response.instrumentLimits?.let {
                val key = it.keys.first()
                val value = it.getValue(key)
                if (payableAmount > value) {
                    binding.flError.visibility = View.VISIBLE
                    binding.iLayoutError.tvError.text =
                        "$key ${FirebaseRemoteConfigUtils.getLimitBreachFirstText()} ₹${
                            Utils.getFormattedDecimal(value.toDouble())
                        }${FirebaseRemoteConfigUtils.getLimitBreachLastText()}"
                    binding.btmPay.btnGoBack.visibility = View.VISIBLE
                    binding.btmPay.btnPay.visibility = View.GONE
                    binding.btmPay.clPaying.visibility = View.GONE
                }
            }
        }
        binding.tvNetEarnings.text = FirebaseRemoteConfigUtils.getNetEarningText()


        binding.ivCancel.setOnClickListener {
            MparticleUtils.logEvent(
                "CC_Payment_BillSummary_Cancel_Clicked",
                "User clicks on cross CTA on the screen to cancel the bill payment",
                "Not Unique",
                "Back",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_PAYMENT_BILL_SUMMARY_CANCEL_CLICKED),
                requireContext()
            )

            getActivity()?.let {
                CancelPaymentFragment.newInstance(netEarning, response?.payTogether ?: false, response?.guaranteedEarning?.subtitle).show(
                    childFragmentManager, FRAGMENT_TAG
                )
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        (dialog as? BottomSheetDialog)?.apply {
            dismissWithAnimation = true
        }
        return dialog
    }
    private fun setUpAction() {
        binding.lifecycleOwner = this
        binding.btmPay.btnGoBack.setOnClickListener {
            dialog?.dismiss()
        }
        binding.btmPay.tvPayingViaName.text = originalBankName
        binding.btmPay.btnPay.setOnClickListener {
            callback?.payCallBack()
            MparticleUtils.logEvent(
                "CC_Payment_BillSummary_Clicked",
                "User clicks on Pay to continue with payment",
                "Not Unique",
                "Continue",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_PAYMENT_BILL_SUMMARY_CLICKED),
                requireContext()
            )

            binding.btmPay.btnPay.visibility = View.INVISIBLE
            binding.btmPay.flLottieIndicator.visibility = View.VISIBLE
            binding.btmPay.clPaying.isClickable = false
            binding.ivCancel.isClickable = false

        }
    }

    private fun setNetEarningView(localResponse: BillSummaryResponse?, isPayTogether: Boolean?) {
        if ((localResponse?.guaranteedEarning != null) && (localResponse.guaranteedEarning?.isMonthlyLimitBreached == false) && (isPayTogether == false)) {

            if (localResponse.guaranteedEarning?.splitTitle != true) {
                setInfoVisibility(binding.flInfoBgOld, binding.ivInfoPePopupOld, localResponse)
                binding.clGe.visibility = View.GONE
                binding.flNetEarning.visibility = View.VISIBLE
                binding.ivInfoPePopupOld.visibility = View.VISIBLE
                binding.llPe.visibility = View.VISIBLE
                binding.shimmerViewContainerOld.visibility = View.VISIBLE
                binding.llRewardLimit.visibility = View.GONE
                binding.tvNetEarnings.text = localResponse.guaranteedEarning?.title
                binding.tvNetEarningsAmount.text = localResponse.guaranteedEarning?.subtitle

            } else {
                setInfoVisibility(binding.flInfoBg, binding.ivInfoPePopup, localResponse)
                binding.clGe.visibility = View.VISIBLE
                binding.flNetEarning.visibility = View.VISIBLE
                binding.ivInfoPePopup.visibility = View.VISIBLE
                binding.llPe.visibility = View.GONE
                binding.shimmerViewContainerOld.visibility = View.GONE
                binding.shimmerViewContainer.visibility = View.VISIBLE
                binding.llRewardLimit.visibility = View.GONE
                binding.tvGuaranteedEar.text = localResponse.guaranteedEarning?.title
                binding.tvGEAmount.text = localResponse.guaranteedEarning?.subtitle
            }

        } else if ((localResponse?.guaranteedEarning != null) && (localResponse.guaranteedEarning?.isMonthlyLimitBreached == true) && (isPayTogether == false)) {
            binding.tvLimitBreach.text = localResponse.guaranteedEarning?.title
            binding.clGe.visibility = View.GONE
            binding.flNetEarning.visibility = View.VISIBLE
            binding.llRewardLimit.visibility = View.VISIBLE
            binding.llChipsEarned.visibility = View.GONE
        } else if ((localResponse?.guaranteedEarning?.isMonthlyLimitBreached == false) && (isPayTogether == true)) {
            binding.shimmerViewContainer.visibility = View.GONE
            binding.clGe.visibility = View.GONE
            binding.flNetEarning.visibility = View.VISIBLE
            binding.llPayTogether.visibility = View.VISIBLE
            binding.tvPayTogether.text = localResponse.guaranteedEarning?.title
            binding.llChipsEarned.visibility = View.GONE

        } else {
            binding.flNetEarning.visibility = View.GONE
            binding.viewDividerBtm.visibility = View.INVISIBLE
        }



    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as PaymentMethodsActivity
        callback = context
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    fun setInfoVisibility(view: View?, infoImage: View?, localResponse: BillSummaryResponse?) {
        view?.setOnClickListener {
            MparticleUtils.logEvent(
                "CC_Payment_BillSummary_PotentialEarning_KnowMore_Clicked",
                "User clicks on info icon for knowing more about Potential Earning",
                "Not Unique",
                "Continue",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_PAYMENT_BILL_SUMMARY_PE_KM_CLICKED),
                requireContext()
            )


            infoImage?.let { it1 ->
                PopupHelper.showPopupWindow(
                    anchor = it,
                    isToShow = true,
                    gravityOne = Gravity.TOP,
                    gravityTwo = Gravity.START,
                    width = 1,
                    height = 50,
                    sizeWidth = 70,
                    context = requireContext(),
                    infoPopUpItemList = localResponse?.guaranteedEarning?.infoPopup
                ) {
                    if (it == true) {
                        binding.infoClicked = false
                    }
                }
            }
            binding.infoClicked = PopupHelper.popUpWindow?.isShowing
            binding.executePendingBindings()
        }
    }
}

@BindingAdapter("bindInfoIcon")
fun setImage(imageView: ImageView, isShowing: Boolean) {
    if (isShowing) {
        imageView.setImageResource(R.drawable.ic_question_primary_color)
    } else {
        imageView.setImageResource(R.drawable.ic_info_grey)
    }
}

@BindingAdapter("bindNetInfoIcon")
fun setNetInfoImage(imageView: ImageView, isShowing: Boolean) {
    if (isShowing) {
        imageView.setImageResource(R.drawable.ic_info)
    } else {
        imageView.setImageResource(R.drawable.ic_info_new_grey)
    }
}
@BindingAdapter("font")
fun AppCompatTextView.font( type: FontsTypes) {
        typeface = ResourcesCompat.getFont(context, type.fontRes)
}