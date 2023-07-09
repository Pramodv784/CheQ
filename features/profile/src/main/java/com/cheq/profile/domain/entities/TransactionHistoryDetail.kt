package com.cheq.profile.domain.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue


/**
 * Created by Akash Khatkale on 16th May, 2023.
 * akash.k@cheq.one
 */

enum class PRODUCTTYPE(val value: String) {
    LOAN("LOAN"),
    CREDIT_CARD("CC");

    companion object {
        fun find(value: String): PRODUCTTYPE {
            return values().firstOrNull { it.value == value } ?: CREDIT_CARD
        }
    }
}

@Parcelize
data class TransactionHistoryDetail(
    val transactionList: TransactionHistoryDetailList,
): Parcelable {
    @Parcelize
    data class TransactionHistoryDetailList(
        val summaryTransactionStartedAt: String,
        val rewardExpiresIn: String,
        val summaryPayinStatus: String,
        val summaryNarration: String,
        val payoutBeneId: String,
        val billBillAmount: Int,
        val summaryPayoutStatus: String,
        val summaryPayoutCount: Int?,
        val payoutAmount: Int?,
        val rewardEventId: String,
        val billUpdatedAt: String,
        val billPayinStatus: String,
        val payoutCreatedBy: String,
        val billId: String,
        val billPaymentMethod: String,
        val summaryPartiallyRefunded: String,
        val billProductId: String,
        val rewardProductId: String,
        val summaryStatus: String,
        val summaryBankMasterId: String,
        val payoutRepaymentType: String,
        val rewardBankMasterId: String,
        val rewardStatus: String,
        val rewardExpireRewardPoints: Int?,
        val summaryUpdatedAt: String,
        val payoutNarration: String,
        val narration: String,
        val transactionStatus: String,
        val payoutTransactionUpdatedAt: String,
        val billPayinTransactionId: String,
        val billPayoutFailureReason: String,
        val summaryPayoutTime: String,
        val rewardUpdatedBy: String,
        val rewardAvailableRewardPoints: Int?,
        val payoutReferenceId: String,
        val rewardRewardId: String,
        val summaryAuthTransaction: Int,
        val summaryId: String,
        val rewardCreatedBy: String,
        val rewardBillId: String,
        var rewardEarnRewardPoints: Int,
        val payoutUpdatedAt: String,
        val billPayoutTransactionId: String,
        val payoutId: String,
        val payoutFailureReason: String,
        val rewardTraceId: String,
        val rewardId: String,
        val payoutCreatedAt: String,
        val summaryAmount: Int,
        val billPayoutStatus: String,
        val rewardUpdatedAt: String,
        val rewardCreatedAt: String,
        val summaryComment: String,
        val rewardCheqUserId: String,
        val payoutTransactionStartedAt: String,
        val payoutUpdatedBy: String,
        val payoutEctNarration: String,
        val productDetails: ProductDetails,
        val billCreatedAt: String,
        val summaryBillPaymentType: String,
        val payoutProductId: String,
        val summaryCheqUserId: String,
        val payoutPaymentRepaymentName: String,
        val billTender: String,
        val rewardEventPayload: RewardEventPayload,
        val payoutPayoutRepaymentCardNetwork: String?,
        val summaryChipUsed: Int,
        val billCheqUniTransactionId: String,
        val summaryPaymentSplit: String,
        val summaryCreatedAt: String,
        val payoutTransactionStatus: String,
        val payoutStatus: String,
        val payoutPayoutMethod: String,
        val billBillId: String,
        val payoutPgTransactionStatus: String,
        val payoutProductType: String,
        val billRewardRefId: String,
        val billNarration: String,
        val billPaidTogether: Int,
        val summaryProductId: String,
        val billSystemGenerated: Int,
        val summaryPayinTime: String,
        val billPayinFailureReason: String,
        val summaryCreatedBy: String,
        val payoutRepaymentProduct: String,
        val rewardConsumerTransactionId: String,
        val rewardConsumerReferenceId: String,
        val payoutCheqUserId: String,
        val billBankMasterId: String,
        val rewardUsedRewardPoints: Int?,
        val rewardRuleId: String,
        val billChipAmount: Int,
        val timestampDiff: String,
        val payoutCheqUniTransactionId: String,
        val billChipUsed: Int,
        val summaryUpdatedBy: String,
        val payoutBillId: String,
        val summaryTransactionUpdatedAt: String,
        val payoutPayoutMode: String,
        val billUpdatedBy: String,
        val summaryTransactionStatus: String,
        val payoutPgName: String,
        val payoutBankmasterId: String,
        val billCheqUserId: String,
        val summaryRefundedAmount: Int,
        val billStatus: String,
        val billCashAmount: Int,
        val summaryBillCount: Int,
        val payoutPayoutUtr: String,
        val summaryRefundedCount: Int,
        val payoutPgMid: String,
        val billCreatedBy: String,
        val billRewardTransactionId: String,
        val rewardEventRuleJson: @RawValue Any?,
        val billComment: String,
        val summaryFailureReason: String,
        val summaryChipAmount: Int,
        val payoutPgId: String,
        val refundRnn: String,
        val feeNarration: String,
    ): Parcelable {
        @Parcelize
        data class RewardEventPayload(
            val eventId: String,
            val traceId: String,
            val earnMode: String,
            val createdAt: String,
            val eventName: String,
            val narration: String,
            val cheqUserId: String,
            val earnRewardsUserEventsId: String,
            val infoPayload: InfoPayload,
            val eventPayload: EventPayload,
        ): Parcelable {
            @Parcelize
            data class InfoPayload(
                val eventId: String,
                val traceId: String,
                val earnMode: String,
                val createdAt: String,
                val eventName: String,
            ): Parcelable
            @Parcelize
            data class EventPayload(
                val bin: String,
                val amount: Int,
                val totalDuePaid: Boolean,
            ): Parcelable
        }

        @Parcelize
        data class ProductDetails(
            val last4: String,
            val cbCreatedFrom: String,
            val ifscPrefix: String,
            val cardNetwork: String,
            val institutionMasterId: String,
            val createdAt: String,
            val productStatus: String,
            val interimDate: String,
            val tokenizationStatus: String,
            val verificationStatus: String,
            val productNumber: String,
            val iin: String,
            val updatedAt: String,
            val isTotalDueEnabled: Boolean,
            val tokenizationDate: String,
            val id: String,
            val bankMasterId: String,
            val billRepeatCount: Int,
            val instrumentMaster: InstrumentMaster,
            val isTokenized: Boolean,
            val billGeneratedDate: String,
            val payabilityStatus: String,
            val createdBy: String,
            val isEnabledForAutopay: Boolean,
            val beneficiaryId: String,
            val primaryPayableMethod: String,
            val productType: PRODUCTTYPE,
            val confirmationStatus: Boolean,
            val cheqUserId: String,
            val tokenLastUsed: String?,
            val updatedBy: String,
            val comment: String,
            val customerName: String,
            val networkTokenPayoutSupport: Boolean,
            val createdFrom: String,
            val status: String,
            val beneIdStatus: String
        ): Parcelable {
            @Parcelize
            data class InstrumentMaster(
                val billerName: String,
                val bankName: String,
                val isActive: Boolean,
                val iFSCPrefix: String,
                val createdAt: String,
                val benePaymentMethod: String,
                val isDeleted: Boolean,
                val creditCardIfsc: String,
                val v: Int,
                val originalBankName: String,
                val alias: List<String>,
                val rank: Int,
                val _id: String,
                val id: String,
                val uiConfig: UiConfig,
                val updatedAt: String
            ): Parcelable {
                @Parcelize
                data class UiConfig(
                    val opacityTopLeft: String,
                    val opacityBottomRight: String,
                    val opacityBorder: String,
                    val cardColor: String
                ): Parcelable
            }
        }
    }
}