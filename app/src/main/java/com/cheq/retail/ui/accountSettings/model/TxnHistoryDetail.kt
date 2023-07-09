package com.cheq.retail.ui.accountSettings.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class TxnHistoryDetail(

	@field:SerializedName("data")
	val data: DetailList? = null,

	@field:SerializedName("requestId")
	val requestId: String? = null,

	@field:SerializedName("httpStatus")
	val httpStatus: Int? = null
)
@Keep
data class TxnLists(

	@field:SerializedName("summary_txn_started_at")
	val summaryTxnStartedAt: String? = null,

	@field:SerializedName("reward_expires_in")
	val rewardExpiresIn: Any? = null,

	@field:SerializedName("summary_payin_status")
	val summaryPayinStatus: String? = null,

	@field:SerializedName("summary_narration")
	val summaryNarration: String? = null,

	@field:SerializedName("payout_bene_id")
	val payoutBeneId: Any? = null,

	@field:SerializedName("bill_bill_amount")
	val billBillAmount: Int? = null,

	@field:SerializedName("summary_payout_status")
	val summaryPayoutStatus: Any? = null,

	@field:SerializedName("summary_payout_count")
	val summaryPayoutCount: Any? = null,

	@field:SerializedName("payout_amount")
	val payoutAmount: Any? = null,

	@field:SerializedName("reward_event_id")
	val rewardEventId: Any? = null,

	@field:SerializedName("bill_updated_at")
	val billUpdatedAt: String? = null,

	@field:SerializedName("bill_payin_status")
	val billPayinStatus: String? = null,

	@field:SerializedName("payout_created_by")
	val payoutCreatedBy: Any? = null,

	@field:SerializedName("bill_id")
	val billId: String? = null,

	@field:SerializedName("bill_payment_method")
	val billPaymentMethod: Any? = null,

	@field:SerializedName("summary_partially_refunded")
	val summaryPartiallyRefunded: Any? = null,

	@field:SerializedName("bill_product_id")
	val billProductId: String? = null,

	@field:SerializedName("reward_product_id")
	val rewardProductId: Any? = null,

	@field:SerializedName("summary_status")
	val summaryStatus: String? = null,

	@field:SerializedName("summary_bankmaster_id")
	val summaryBankmasterId: String? = null,

	@field:SerializedName("payout_repayment_type")
	val payoutRepaymentType: Any? = null,

	@field:SerializedName("reward_bankmaster_id")
	val rewardBankmasterId: Any? = null,

	@field:SerializedName("reward_status")
	val rewardStatus: Any? = null,

	@field:SerializedName("reward_expire_reward_points")
	val rewardExpireRewardPoints: Any? = null,

	@field:SerializedName("summary_updated_at")
	val summaryUpdatedAt: String? = null,

	@field:SerializedName("payout_narration")
	val payoutNarration: Any? = null,
 
	@field:SerializedName("narration")
	val narration: String? = null,

	@field:SerializedName("txn_status")
	val txn_status: String? = null,

	@field:SerializedName("payout_txn_updated_at")
	val payoutTxnUpdatedAt: Any? = null,

	@field:SerializedName("bill_payin_txn_id")
	val billPayinTxnId: Any? = null,

	@field:SerializedName("bill_payout_failure_reason")
	val billPayoutFailureReason: Any? = null,

	@field:SerializedName("summary_payout_time")
	val summaryPayoutTime: Any? = null,

	@field:SerializedName("reward_updated_by")
	val rewardUpdatedBy: Any? = null,

	@field:SerializedName("reward_available_reward_points")
	val rewardAvailableRewardPoints: Any? = null,

	@field:SerializedName("payout_referenceId")
	val payoutReferenceId: Any? = null,

	@field:SerializedName("reward_reward_id")
	val rewardRewardId: Any? = null,

	@field:SerializedName("summary_auth_txn")
	val summaryAuthTxn: Int? = null,

	@field:SerializedName("summary_id")
	val summaryId: String? = null,

	@field:SerializedName("reward_created_by")
	val rewardCreatedBy: Any? = null,

	@field:SerializedName("reward_bill_id")
	val rewardBillId: Any? = null,

	@field:SerializedName("reward_earn_reward_points")
	var rewardEarnRewardPoints: Int? = null,

	@field:SerializedName("payout_updated_at")
	val payoutUpdatedAt: Any? = null,

	@field:SerializedName("bill_payout_txn_id")
	val billPayoutTxnId: Any? = null,

	@field:SerializedName("payout_id")
	val payoutId: Any? = null,

	@field:SerializedName("payout_failure_reason")
	val payoutFailureReason: Any? = null,

	@field:SerializedName("reward_trace_id")
	val rewardTraceId: Any? = null,

	@field:SerializedName("reward_id")
	val rewardId: Any? = null,

	@field:SerializedName("payout_created_at")
	val payoutCreatedAt: Any? = null,

	@field:SerializedName("summary_amount")
	val summaryAmount: Int? = null,

	@field:SerializedName("bill_payout_status")
	val billPayoutStatus: String? = null,

	@field:SerializedName("reward_updated_at")
	val rewardUpdatedAt: Any? = null,

	@field:SerializedName("reward_created_at")
	val rewardCreatedAt: Any? = null,

	@field:SerializedName("summary_comment")
	val summaryComment: Any? = null,

	@field:SerializedName("reward_cheq_user_id")
	val rewardCheqUserId: Any? = null,

	@field:SerializedName("payout_txn_started_at")
	val payoutTxnStartedAt: Any? = null,

	@field:SerializedName("payout_updated_by")
	val payoutUpdatedBy: Any? = null,

	@field:SerializedName("payout_ect_narration")
	val payout_ect_narration: String? = null,

	@field:SerializedName("productDetails")
	val productDetails: ProductDetails? = null,

	@field:SerializedName("bill_created_at")
	val billCreatedAt: String? = null,

	@field:SerializedName("summary_bill_payment_type")
	val summaryBillPaymentType: String? = null,

	@field:SerializedName("payout_product_id")
	val payoutProductId: Any? = null,

	@field:SerializedName("summary_cheq_user_id")
	val summaryCheqUserId: String? = null,

	@field:SerializedName("payout_payment_repayment_name")
	val payoutPaymentRepaymentName: Any? = null,

	@field:SerializedName("bill_tender")
	val billTender: Any? = null,

	@field:SerializedName("reward_event_payload")
	val rewardEventPayload: Any? = null,

	@field:SerializedName("payout_payout_repayment_card_network")
	val payoutPayoutRepaymentCardNetwork: Any? = null,

	@field:SerializedName("summary_chip_used")
	val summaryChipUsed: Int? = null,

	@field:SerializedName("bill_cheq_uni_txn_id")
	val billCheqUniTxnId: String? = null,

	@field:SerializedName("summary_payment_split")
	val summaryPaymentSplit: String? = null,

	@field:SerializedName("summary_created_at")
	val summaryCreatedAt: String? = null,

	@field:SerializedName("payout_txn_status")
	val payoutTxnStatus: Any? = null,

	@field:SerializedName("payout_status")
	val payoutStatus: Any? = null,

	@field:SerializedName("payout_payout_method")
	val payoutPayoutMethod: Any? = null,

	@field:SerializedName("bill_bill_id")
	val billBillId: String? = null,

	@field:SerializedName("payout_pg_txn_status")
	val payoutPgTxnStatus: Any? = null,

	@field:SerializedName("payout_product_type")
	val payoutProductType: String? = null,

	@field:SerializedName("bill_reward_ref_id")
	val billRewardRefId: String? = null,

	@field:SerializedName("bill_narration")
	val billNarration: String? = null,

	@field:SerializedName("bill_paid_together")
	val billPaidTogether: Int? = null,

	@field:SerializedName("summary_product_id")
	val summaryProductId: Any? = null,

	@field:SerializedName("bill_system_generated")
	val billSystemGenerated: Int? = null,

	@field:SerializedName("summary_payin_time")
	val summaryPayinTime: String? = null,

	@field:SerializedName("bill_payin_failure_reason")
	val billPayinFailureReason: Any? = null,

	@field:SerializedName("summary_created_by")
	val summaryCreatedBy: String? = null,

	@field:SerializedName("payout_repayment_product")
	val payoutRepaymentProduct: Any? = null,

	@field:SerializedName("reward_consumer_transaction_id")
	val rewardConsumerTransactionId: Any? = null,

	@field:SerializedName("reward_consumer_reference_id")
	val rewardConsumerReferenceId: Any? = null,

	@field:SerializedName("payout_cheq_user_id")
	val payoutCheqUserId: Any? = null,

	@field:SerializedName("bill_bankmaster_id")
	val billBankmasterId: String? = null,

	@field:SerializedName("reward_used_reward_points")
	val rewardUsedRewardPoints: Any? = null,

	@field:SerializedName("reward_rule_id")
	val rewardRuleId: Any? = null,

	@field:SerializedName("bill_chip_amount")
	val billChipAmount: Int? = null,

	@field:SerializedName("timestampDiff")
	val timestampDiff: String? = null,

	@field:SerializedName("payout_cheq_uni_txn_id")
	val payoutCheqUniTxnId: Any? = null,

	@field:SerializedName("bill_chip_used")
	val billChipUsed: Int? = null,

	@field:SerializedName("summary_updated_by")
	val summaryUpdatedBy: String? = null,

	@field:SerializedName("payout_bill_id")
	val payoutBillId: Any? = null,

	@field:SerializedName("summary_txn_updated_at")
	val summaryTxnUpdatedAt: String? = null,

	@field:SerializedName("payout_payout_mode")
	val payoutPayoutMode: String? = null,

	@field:SerializedName("bill_updated_by")
	val billUpdatedBy: String? = null,

	@field:SerializedName("summary_txn_status")
	val summaryTxnStatus: String? = null,

	@field:SerializedName("payout_pg_name")
	val payoutPgName: Any? = null,

	@field:SerializedName("payout_bankmaster_id")
	val payoutBankmasterId: Any? = null,

	@field:SerializedName("bill_cheq_user_id")
	val billCheqUserId: String? = null,

	@field:SerializedName("summary_refunded_amount")
	val summaryRefundedAmount: Int? = null,

	@field:SerializedName("bill_status")
	val billStatus: String? = null,

	@field:SerializedName("bill_cash_amount")
	val billCashAmount: Int? = null,

	@field:SerializedName("summary_bill_count")
	val summaryBillCount: Int? = null,

	@field:SerializedName("payout_payout_utr")
	val payoutPayoutUtr: Any? = null,

	@field:SerializedName("summary_refunded_count")
	val summaryRefundedCount: Int? = null,

	@field:SerializedName("payout_pg_mid")
	val payoutPgMid: Any? = null,

	@field:SerializedName("bill_created_by")
	val billCreatedBy: String? = null,

	@field:SerializedName("bill_reward_txn_id")
	val billRewardTxnId: String? = null,

	@field:SerializedName("reward_event_rule_json")
	val rewardEventRuleJson: Any? = null,

	@field:SerializedName("bill_comment")
	val billComment: Any? = null,

	@field:SerializedName("summary_failure_reason")
	val summaryFailureReason: Any? = null,

	@field:SerializedName("summary_chip_amount")
	val summaryChipAmount: Int? = null,

	@field:SerializedName("payout_pg_id")
	val payoutPgId: Any? = null,

	@field:SerializedName("bill_total_amount")
	val billTotalAmount: Int? = null,

	@field:SerializedName("bill_display_amount")
	val billDisplayAmount: Int? = null,

	@field:SerializedName("fee_narration")
	val feeNaration: String? = null,

	@field:SerializedName("refund_rrn")
	val refundRRN: String? = null,

	@field:SerializedName("payout_utr")
	val payoutUtr: String? = null

)
@Keep
data class UiConfig(

	@field:SerializedName("opacity_topLeft")
	val opacityTopLeft: String? = null,

	@field:SerializedName("opacity_bottomRight")
	val opacityBottomRight: String? = null,

	@field:SerializedName("opacity_border")
	val opacityBorder: String? = null,

	@field:SerializedName("cardColor")
	val cardColor: String? = null
)
@Keep
data class ProductDetails(

	@field:SerializedName("last4")
	val last4: String? = null,

	@field:SerializedName("cb_created_from")
	val cbCreatedFrom: Any? = null,

	@field:SerializedName("ifsc_prefix")
	val ifscPrefix: String? = null,

	@field:SerializedName("card_network")
	val cardNetwork: String? = null,

	@field:SerializedName("institution_master_id")
	val institutionMasterId: Any? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("product_status")
	val productStatus: String? = null,

	@field:SerializedName("interim_date")
	val interimDate: Any? = null,

	@field:SerializedName("tokenization_status")
	val tokenizationStatus: String? = null,

	@field:SerializedName("verification_status")
	val verificationStatus: String? = null,

	@field:SerializedName("product_number")
	val productNumber: String? = null,

	@field:SerializedName("iin")
	val iin: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("is_total_due_enabled")
	val isTotalDueEnabled: Boolean? = null,

	@field:SerializedName("tokenization_date")
	val tokenizationDate: Any? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("bankmaster_id")
	val bankmasterId: String? = null,

	@field:SerializedName("bill_repeat_count")
	val billRepeatCount: Int? = null,

	@field:SerializedName("instrumentMaster")
	val instrumentMaster: InstrumentMaster? = null,

	@field:SerializedName("is_tokenized")
	val isTokenized: Boolean? = null,

	@field:SerializedName("bill_generated_date")
	val billGeneratedDate: Any? = null,

	@field:SerializedName("payability_status")
	val payabilityStatus: String? = null,

	@field:SerializedName("created_by")
	val createdBy: String? = null,

	@field:SerializedName("is_enabled_for_autopay")
	val isEnabledForAutopay: Boolean? = null,

	@field:SerializedName("beneficiary_id")
	val beneficiaryId: String? = null,

	@field:SerializedName("primary_payable_method")
	val primaryPayableMethod: String? = null,

	@field:SerializedName("product_type")
	val productType: String? = null,

	@field:SerializedName("confirmation_status")
	val confirmationStatus: Boolean? = null,

	@field:SerializedName("cheq_user_id")
	val cheqUserId: String? = null,

	@field:SerializedName("token_last_used")
	val tokenLastUsed: Any? = null,

	@field:SerializedName("updated_by")
	val updatedBy: String? = null,

	@field:SerializedName("comment")
	val comment: Any? = null,

	@field:SerializedName("customer_name")
	val customerName: String? = null,

	@field:SerializedName("network_token_payout_support")
	val networkTokenPayoutSupport: Boolean? = null,

	@field:SerializedName("created_from")
	val createdFrom: String? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("bene_id_status")
	val beneIdStatus: String? = null
)
@Keep
data class DetailList(

	@field:SerializedName("txnLists")
	val txnLists: TxnLists? = null,@field:SerializedName("apiMessage")
	val apiMessage: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)
@Keep
data class InstrumentMaster(

	@field:SerializedName("billerName")
	val billerName: String? = null,

	@field:SerializedName("bankName")
	val bankName: String? = null,

	@field:SerializedName("isActive")
	val isActive: Boolean? = null,

	@field:SerializedName("IFSC_Prefix")
	val iFSCPrefix: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("benePaymentMethod")
	val benePaymentMethod: String? = null,

	@field:SerializedName("isDeleted")
	val isDeleted: Boolean? = null,

	@field:SerializedName("creditCardIfsc")
	val creditCardIfsc: String? = null,

	@field:SerializedName("__v")
	val v: Int? = null,

	@field:SerializedName("originalBankName")
	val originalBankName: String? = null,

	@field:SerializedName("alias")
	val alias: List<String?>? = null,

	@field:SerializedName("rank")
	val rank: Int? = null,

	@field:SerializedName("_id")
	val _id: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("ui_config")
	val uiConfig: UiConfig? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
