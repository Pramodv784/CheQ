package com.cheq.profile.presentation.bottomsheets

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.cheq.cache.sharedprefs.SharedPrefs
import com.cheq.common.R
import com.cheq.common.ui.bottomsheet.GenericBottomSheet
import com.cheq.common.databinding.LayoutNewLoanCardBinding
import com.cheq.common.extension.loadSvg
import com.cheq.common.models.CardType
import com.cheq.common.utils.CardUtils.getCardImage
import com.cheq.common.utils.Constants
import com.cheq.common.utils.Constants.BASE_BANK_MASTER
import com.cheq.common.utils.Constants.BASE_LOAN_MASTER
import com.cheq.common.utils.Constants.CAPTURED
import com.cheq.common.utils.Constants.DATE_TAG
import com.cheq.common.utils.Constants.FAILED
import com.cheq.common.utils.Constants.IMAGE_SUFFIX_URL
import com.cheq.common.utils.Constants.PROCESSING
import com.cheq.common.utils.Constants.REFUNDED
import com.cheq.common.utils.Constants.SEPARATOR
import com.cheq.common.utils.DateUtils.getFormattedDateFromDate
import com.cheq.common.utils.DateUtils.getFormattedTimeFromDate
import com.cheq.common.utils.ShareUtils
import com.cheq.common.utils.UiUtils
import com.cheq.common.utils.UiUtils.getBitmapFromView
import com.cheq.profile.databinding.BottomsheetTransactionHistoryDetailBinding
import com.cheq.profile.domain.entities.PRODUCTTYPE
import com.cheq.profile.domain.entities.TransactionHistoryDetail
import com.moengage.core.internal.utils.copyToClipboard
import com.cheq.common.R as commonR
import com.cheq.proxima.R as proxima

/**
 * Created by Akash Khatkale on 17th May, 2023.
 * akash.k@cheq.one
 */
@SuppressLint("LongLogTag")
class TransactionHistoryDetailBottomsheet :
    GenericBottomSheet<BottomsheetTransactionHistoryDetailBinding>() {

    private var transactionDetail: TransactionHistoryDetail? = null
    private var loanBinding: LayoutNewLoanCardBinding? = null
    private var sharedPrefs: SharedPrefs? = null

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BottomsheetTransactionHistoryDetailBinding {
        return BottomsheetTransactionHistoryDetailBinding.inflate(inflater, container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            transactionDetail = it.getParcelable(TRANSACTION_DETAIL)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loanBinding = LayoutNewLoanCardBinding.bind(binding.loanLayout.root)
        setupUi()
        setClickListeners()
    }

    private fun setClickListeners() {
        with(binding) {
            closeIv.setOnClickListener {
                dismiss()
            }

            shareIv.setOnClickListener {
                transactionDetail?.let{ transactionDetail ->
                    try {
                        ShareUtils.shareImage(
                            shareIv.context,
                            getBitmapFromView(parentCl),
                            shareIv.context.getString(R.string.transaction_share_message, transactionDetail.transactionList.billBillAmount.toString(), transactionDetail.transactionList.rewardEarnRewardPoints.toString())
                        )
                    } catch (e: Exception) {
                        Toast.makeText(context, commonR.string.no_app_available, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            ivCopyTxnId.setOnClickListener {
                copyToClipboard(requireContext(), transactionIdTv.text.toString())
            }
            ivCopyRnnId.setOnClickListener {
                copyToClipboard(requireContext(), rnnTv.text.toString())
            }
        }
    }

    private fun setSharedPrefs(sharedPrefs: SharedPrefs) {
        this.sharedPrefs = sharedPrefs
    }

    private fun setupUi() {
        transactionDetail?.let { transactionDetail ->
            val data = transactionDetail.transactionList
            val product = data.productDetails
            val instrumentMaster = product.instrumentMaster

            with(binding) {
                setCardBackground(instrumentMaster)
                setCardOrLoan(product)
                setTimelineLayout(data)

                earnedTv.text = earnedTv.context.getString(
                    commonR.string.you_earned_cheq_chips,
                    data.rewardEarnRewardPoints.toString()
                )
                completedOnTv.text = completedOnTv.context.getString(
                    commonR.string.payment_completed_on,
                    getFormattedDateFromDate(data.summaryTransactionStartedAt),
                    getFormattedTimeFromDate(data.summaryTransactionStartedAt)
                )
                billAmountTv.text =
                    "${billAmountTv.context.getString(commonR.string.rupee_symbol)} ${data.billBillAmount}"
                billPayMethodTv.text = data.billPaymentMethod
                transactionIdTv.text = data.billCheqUniTransactionId
                adjustedRl.isVisible = data.billPaidTogether == 1
                adjustTv.text = adjustTv.context.getString(
                    commonR.string.adjusted_text,
                    data.summaryAmount.toString(),
                    getFormattedDateFromDate(
                        data.summaryTransactionStartedAt
                    ).split(" ").joinToString(separator = "-")
                )
                billCashAmountTv.isVisible = data.billCashAmount > 0
                bankAmountLl.isVisible = data.billCashAmount > 0
                billCashAmountTv.text =
                    "${getString(commonR.string.rupee_symbol)} ${data.billCashAmount}"
                billChipAmountTv.isVisible = data.billChipAmount > 0
                cheqChipsLl.isVisible = data.billChipAmount > 0
                billChipAmountTv.text =
                    "${getString(commonR.string.rupee_symbol)} ${data.billChipAmount}"

                if(data.feeNarration.isNotEmpty()) {
                    txtProcessingFees.isVisible = true
                    txtProcessingFees.text = data.feeNarration
                }else{
                    txtProcessingFees.isVisible = false
                }
            }
        } ?: dismiss()
    }

    private fun setTimelineLayout(
        data: TransactionHistoryDetail.TransactionHistoryDetailList
    ) {
        with(binding) {
            if (data.billPayinStatus.uppercase() == REFUNDED && (data.billPayoutStatus.isEmpty() || data.billPayoutStatus.uppercase() == FAILED)) {
                setFailureView(
                    commonR.drawable.ic_refunded_info,
                    data.narration.ifEmpty { getString(commonR.string.money_debited_has_been_refunded) },
                    ContextCompat.getColor(requireContext(), proxima.color.golden_p6),
                    getString(commonR.string.refunded)
                )

                if (data.refundRnn.isNotEmpty() && data.productDetails.productType == PRODUCTTYPE.CREDIT_CARD) {
                    setRnnView(true)
                    rnnTv.text = data.refundRnn
                } else {
                    setRnnView(false)
                }
            } else if (data.billPayinStatus.uppercase() == PROCESSING && (data.billPayoutStatus.isEmpty() || data.billPayoutStatus.isEmpty() || data.billPayoutStatus.isEmpty())) {
                setFailureView(
                    commonR.drawable.ic_pending_info,
                    data.narration.ifEmpty { getString(commonR.string.attempting_to_get_latest_status) },
                    ContextCompat.getColor(requireContext(), proxima.color.golden_p6),
                    getString(commonR.string.pending)
                )
            } else if (data.billPayinStatus.uppercase() == FAILED || data.billPayoutStatus.uppercase() == FAILED) {
                setFailureView(
                    commonR.drawable.ic_payment_failed,
                    data.narration.ifEmpty { getString(commonR.string.money_debited_will_be_refunded) },
                    ContextCompat.getColor(requireContext(), proxima.color.negative_p5),
                    getString(commonR.string.failed)
                )
            } else if (data.billPayinStatus.uppercase() == CAPTURED && (data.billPayoutStatus.isEmpty() || data.billPayoutStatus.uppercase() == Constants.CREATED || data.billPayoutStatus.uppercase() == PROCESSING)) {
                creditedIv.setImageResource(commonR.drawable.ic_pay_time_green)
                setSuccessView(data)
                billStatusTv.text = if (data.billPaidTogether == 1) getString(commonR.string.paid_together) else billStatusTv.text
            } else {
                creditedIv.setImageResource(commonR.drawable.ic_check_right)
                setSuccessView(data)
                billStatusTv.text = if (data.billPaidTogether == 1) getString(commonR.string.paid_together) else billStatusTv.text
                if (data.payoutPayoutUtr.isNotEmpty() && data.productDetails.productType == PRODUCTTYPE.CREDIT_CARD) {
                    setRnnView(true)
                    rnnTv.text = data.payoutPayoutUtr
                    rnnIdLabelTv.text = getString(R.string.utr)
                } else {
                    setRnnView(false)
                }
            }
        }
    }

    private fun setFailureView(
        ivFailedIcon: Int,
        failedMessage: String, color: Int, billStatus: String
    ) {
        with(binding) {
            paidSuccessRl.isVisible = false
            paidFailedRl.isVisible = true
            shareIv.isVisible = false
            failedIv.setImageResource(ivFailedIcon)
            failedReasonTv.text = failedMessage
            failedReasonTv.setTextColor(color)
            billStatusTv.text = billStatus
        }
    }

    private fun setSuccessView(transaction: TransactionHistoryDetail.TransactionHistoryDetailList) {
        with(binding) {
            paidSuccessRl.isVisible = true
            paidFailedRl.isVisible = false
            shareIv.isVisible = true

            if (transaction.payoutEctNarration.isEmpty().not() &&
                transaction.payoutEctNarration.contains(SEPARATOR)
            ) {
                val payOutNarration = transaction.payoutEctNarration.split(SEPARATOR)
                if (payOutNarration.isNotEmpty()) {
                    try {
                        val hours = getNumericValue(payOutNarration[0])
                        if (hours != Char.MIN_VALUE) {
                            val narration = payOutNarration[0].split(hours)
                            if (narration.isNotEmpty()) {
                                creditedInLabelTv.text = narration[0]
                            }
                            creditedInTv.text =
                                payOutNarration[0].substring(payOutNarration[0].indexOf(hours))
                            if (
                                payOutNarration.size > 1 &&
                                payOutNarration[1].isNotEmpty() &&
                                transaction.summaryTransactionStartedAt.isEmpty().not()
                            ) {
                                mayTakeTv.text = payOutNarration[1].replace(
                                    DATE_TAG,
                                    getFormattedDateFromDate(transaction.summaryTransactionStartedAt)
                                ).trim()
                            }
                        }
                    } catch (exception: Exception) {
                        creditedInLabelTv.text = payOutNarration[0]
                        if (
                            payOutNarration.size > 1 &&
                            payOutNarration[1].isNotEmpty() &&
                            transaction.summaryTransactionStartedAt.isEmpty().not()
                        ) {
                            mayTakeTv.text = payOutNarration[1].replace(
                                DATE_TAG,
                                getFormattedDateFromDate(transaction.summaryTransactionStartedAt)
                            ).trim()
                        }
                    }
                }
            }
        }
    }

    private fun getNumericValue(str: String): Char {
        for (i in str.indices) {
            if (Character.isDigit(str[i])) {
                return str[i]
            }
        }
        return Char.MIN_VALUE
    }


    private fun setCardOrLoan(
        product: TransactionHistoryDetail.TransactionHistoryDetailList.ProductDetails
    ) {
        with(binding) {
            when (product.productType) {
                PRODUCTTYPE.LOAN -> {
                    loanBinding?.loanCl?.isVisible = true
                    creditCardFl.isVisible = false
                    val imageUrl =
                        "${sharedPrefs?.getString(BASE_LOAN_MASTER)}${product.instrumentMaster.id}${IMAGE_SUFFIX_URL}"
                    loanBinding?.bankImageIv?.loadSvg(imageUrl)
                    loanBinding?.bankNameTv?.text = product.instrumentMaster.billerName

                    loanBinding?.cardNumberTv?.text = "··· ${product.last4}"
                    if (product.instrumentMaster.uiConfig.cardColor.isNotEmpty()) {
                        loanBinding?.flSolidColor?.setBackgroundResource(commonR.drawable.loan_bottom_shap)
                        loanBinding?.flSolidColor?.background?.setTint(Color.parseColor(product.instrumentMaster.uiConfig.cardColor))
                    }
                    transactionIdLabelTv.text = getString(commonR.string.bbps_transaction_id)
                    bbpsLogoIv.isVisible = true
                }

                PRODUCTTYPE.CREDIT_CARD -> {
                    creditCardFl.isVisible = true
                    loanBinding?.loanCl?.isVisible = false
                    ccCardNumberTv.text = "··· ${product.last4}"
                    val imageUrl =
                        "${sharedPrefs?.getString(BASE_BANK_MASTER)}${product.instrumentMaster.id}${IMAGE_SUFFIX_URL}"
                    ccBankNameTv.text = product.instrumentMaster.bankName
                    bankImageCardviewIv.loadSvg(imageUrl)
                    ccCardTypeIv.setImageResource(getCardImage(CardType.find(product.cardNetwork)))
                    transactionIdLabelTv.text = getString(commonR.string.transactional_id)
                    bbpsLogoIv.isVisible = false
                }
            }
        }
    }

    private fun setCardBackground(
        instrumentMaster: TransactionHistoryDetail.TransactionHistoryDetailList.ProductDetails.InstrumentMaster
    ) {
        with(binding) {
            if (
                (instrumentMaster.uiConfig.cardColor.isNotEmpty()) &&
                (instrumentMaster.uiConfig.opacityBorder.isNotEmpty()) &&
                (instrumentMaster.uiConfig.opacityTopLeft.isNotEmpty()) &&
                (instrumentMaster.uiConfig.opacityBottomRight.isNotEmpty())
            ) {
                UiUtils.setBorderStroke(
                    instrumentMaster.uiConfig.cardColor,
                    strokeFl
                )
                UiUtils.setBackGround(
                    instrumentMaster.uiConfig.cardColor,
                    cardSolidBackgroundLl
                )
            }
        }
    }

    private fun setRnnView(visible: Boolean) {
        with(binding){
            rnnIdLabelTv.isVisible = visible
            ivCopyRnnId.isVisible = visible
            rnnTv.isVisible = visible
        }
    }

    companion object {
        const val TAG = "TransactionHistoryDetailBottomsheet"
        private const val TRANSACTION_DETAIL = "TRANSACTION_DETAIL"
        fun newInstance(
            transactionDetail: TransactionHistoryDetail,
            sharedPrefs: SharedPrefs
        ): TransactionHistoryDetailBottomsheet {
            return TransactionHistoryDetailBottomsheet().apply {
                setSharedPrefs(sharedPrefs)
                arguments = Bundle().apply {
                    putParcelable(TRANSACTION_DETAIL, transactionDetail)
                }
            }
        }
    }
}