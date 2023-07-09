package com.cheq.retail.ui.main.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Parcelize
@Keep
data class SelectOfferResponse(

	@field:SerializedName("list")
	val selectOfferResponse: List<SelectOfferResponseItem?>? = null
) : Parcelable

@Parcelize
@Keep
data class SelectOfferResponseItem(

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("reward_rate")
	val rewardRate: Double? = null
) : Parcelable
