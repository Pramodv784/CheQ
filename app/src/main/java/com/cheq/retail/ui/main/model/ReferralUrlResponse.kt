package com.cheq.retail.ui.main.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize



@Keep
@Parcelize
data class ReferralUrlResponse(

    @field:SerializedName("referralUrl")
    var referralUrl: String? = null,

    @field:SerializedName("upsertedId")
    val upsertedId: Int? = null,

    @field:SerializedName("upsertedCount")
    val upsertedCount: Int? = null,

    @field:SerializedName("acknowledged")
    val acknowledged: Boolean? = null,

    @field:SerializedName("modifiedCount")
    val modifiedCount: Int? = null,

    @field:SerializedName("matchedCount")
    val matchedCount: Int? = null
) : Parcelable

