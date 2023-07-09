package com.cheq.retail.ui.billPayments.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class AddpaymentResponse {
    @SerializedName("success")
    val success: Boolean? = null

    @SerializedName("httpStatus")
    val httpStatus: Integer? = null

    @SerializedName("data")
    val data: AddData? = null

    @SerializedName("rewardsEarned")
    val rewardsEarned: Integer? = null

    @Keep
    class AddData {
        @SerializedName("_id")
          val _id: String? = null

        @SerializedName("isActive")
          val isActive: String? = null

        @SerializedName("isDeleted")
          val isDeleted: String? = null

        @SerializedName("userId")
          val userId: String? = null

        @SerializedName("userSecretId")
          val userSecretId: String? = null

        @SerializedName("productId")
          val productId: String? = null

        @SerializedName("billId")
          val billId: String? = null

        @SerializedName("rpOrderId")
          val rpOrderId: String? = null

        @SerializedName("cfReFerenceId")
          val cfReFerenceId: String? = null

        @SerializedName("cfStatus")
          val cfStatus: String? = null

        @SerializedName("updatedAt")
          val updatedAt: String? = null

        @SerializedName("createdAt")
          val createdAt: String? = null

        @SerializedName("__v")
          val __v: String? = null

        @SerializedName("cfBeneId")
          val cfBeneId: String? = null

        @SerializedName("payInStatus")
          val payInStatus: String? = null

        @SerializedName("payOutAmount")
          val payOutAmount: String? = null

        @SerializedName("payOutMode")
          val payOutMode: String? = null

        @SerializedName("rpPaymentId")
          val rpPaymentId: String? = null
    }


}


