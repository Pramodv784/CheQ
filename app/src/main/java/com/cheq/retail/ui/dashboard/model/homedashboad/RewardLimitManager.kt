package com.cheq.retail.ui.dashboard.model.homedashboad

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class RewardLimitManager(

	@field:SerializedName("rewardsAssigned")
	val rewardsAssigned: Int? = null,

	@field:SerializedName("response")
	val response: Response? = null,

	@field:SerializedName("rewardsCanAssign")
	val rewardsCanAssign: Int? = null,

	@field:SerializedName("rewardsAssignUpto")
	val rewardsAssignUpto: Int? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("error")
	val error: String? = null,

	@field:SerializedName("resId")
	val resId: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
) : Parcelable

@Parcelize
data class DataItem(

	@field:SerializedName("value_type")
	val valueType: String? = null,

	@field:SerializedName("accumulated_value")
	val accumulatedValue: Int? = null,

	@field:SerializedName("threshold_value")
	val thresholdValue: Int? = null,

	@field:SerializedName("window_type")
	val windowType: String? = null,

	@field:SerializedName("window")
	val window: String? = null
) : Parcelable

@Parcelize
data class Response(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("httpStatus")
	val httpStatus: Int? = null,

	@field:SerializedName("message")
	val message: String? = null
) : Parcelable
