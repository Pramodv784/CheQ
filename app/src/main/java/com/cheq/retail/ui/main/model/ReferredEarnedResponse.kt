package com.cheq.retail.ui.main.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ReferredEarnedResponse(

	@field:SerializedName("show")
	var show: Boolean? = false,

	@field:SerializedName("totalRewardsEarned")
	var totalRewardsEarned: Int? = 0,

	@field:SerializedName("httpStatus")
	var httpStatus: Int? = 0,

	@field:SerializedName("totalFriendsInvited")
	var totalFriendsInvited: Int? = 0,

	@field:SerializedName("totalReferralLimit")
	var totalReferralLimit: Int? = 0
)