package com.cheq.retail.ui.dashboard.model.homedashboad

import androidx.annotation.Keep
import com.cheq.retail.api.errormodel.BlockData

@Keep
data class HomeDashBoardResponse(
    val __v: Int,
    val balanceDueWidget: BalanceDueWidget,
    val cheqUserId: String,
    val createdAt: String,
    val creditHealthWidget: CreditHealthWidget,
    val id: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val rewardWidget: RewardWidget?,
    val updatedAt: String,
    val rewardLimitManager: RewardLimitManager,
    val waitlistRewardWidget: WaitListRewardWidget,
    val blockedData: BlockData?,
)

fun HomeDashBoardResponse?.isUserBlocked(): Boolean {
    return this?.blockedData != null
}
