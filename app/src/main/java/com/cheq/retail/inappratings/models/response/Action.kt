package com.cheq.retail.inappratings.models.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


/**
 * Created by Sanket Mendon on 20,June,2023.
 * sanket@cheq.one
 */
@Parcelize
class Action(
    @SerializedName("type")
    val type: String?,

    @SerializedName("title")
    val title: String?,

    @SerializedName("titleIcon")
    val titleIcon: String?,

    @SerializedName("style")
    val style: String?,

    @SerializedName("lottieFile")
    val lottieFile: String?,

    @SerializedName("action")
    val action: Action?,

    @SerializedName("actions")
    val actions: List<Action>?,

    @SerializedName("options")
    val options: List<String>?,

    @SerializedName("showCustomFeedbackOption")
    val showCustomFeedbackOption: Boolean? = false,

    @SerializedName("postUrl")
    val postUrl: String?,

    @SerializedName("url")
    val url: String?,

    @SerializedName("keys")
    val keys: List<String>?
) : Parcelable
