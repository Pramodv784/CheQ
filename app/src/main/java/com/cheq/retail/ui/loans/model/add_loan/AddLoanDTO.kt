package com.cheq.retail.ui.loans.model.add_loan

import androidx.annotation.Keep
import com.cheq.retail.ui.loans.model.CustomerParam
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class AddLoanDTO(
    @field:SerializedName("productId")
    val productId: String,
    @field:SerializedName("billerId")
    val billerId: String,
    @field:SerializedName("customerName")
    val customerName: String,
    @field:SerializedName("customerParams")
    val customerParams: ArrayList<CustomerParam>,
    @field:SerializedName("customerPhoneNumber")
    val customerPhoneNumber: String,
    @field:SerializedName("deviceDetails")
    val deviceDetails: DeviceDetails,
    @field:SerializedName("id")
    val id: String?
)