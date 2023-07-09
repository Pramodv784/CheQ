package com.cheq.retail.inappratings.models.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


/**
 * Created by Sanket Mendon on 20,June,2023.
 * sanket@cheq.one
 */

@Parcelize
class InAppDiscoveryResponse(
    @SerializedName("inAppRatingDiscovery")
    val inAppRatingDiscovery: InAppRatingDiscovery?
) : Parcelable

@Parcelize
class InAppRatingDiscovery(
    @SerializedName("showInAppRatingOption")
    val showInAppRatingOption: Boolean? = false,

    @SerializedName("action")
    val action: Action?
) : Parcelable
