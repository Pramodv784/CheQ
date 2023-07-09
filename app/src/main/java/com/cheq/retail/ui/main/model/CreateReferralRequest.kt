package com.cheq.retail.ui.main.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class CreateReferralRequest(

	@field:SerializedName("action_huddle")
	val actionHuddle: String? = null,

	@field:SerializedName("referral_from")
	val referralFrom: String? = null,

	@field:SerializedName("referral_to")
	val referralTo: String? = null,

	@field:SerializedName("campaign")
	val campaign: String? = null,

	@field:SerializedName("term")
	val term: String? = null,

	@field:SerializedName("source")
	val source: String? = null,

	@field:SerializedName("medium")
	val medium: String? = null,

	@field:SerializedName("content")
	val content: String? = null
) : Parcelable
