package com.cheq.retail.ui.loans.model.add_loan_response

import androidx.annotation.Keep
import com.cheq.retail.ui.login.model.loan.BillDetail
import com.cheq.retail.ui.login.modelv2.productv1.BankMasterRecord
import com.cheq.retail.ui.login.modelv2.productv1.ProductV2
import com.google.gson.annotations.SerializedName
import java.io.Serializable
@Keep
data class AddLoanResponseX(
    var product_status: String,
    val bankmaster_id: String,
    val beneficiary_id: String,
    val bill_generated_date: String,
    val bill_repeat_count: Int,
    val cb_created_from: String,
    val cheq_user_id: String,
    val created_from: String,
    val customer_name: String,
    val id: String,
    val ifsc_prefix: String,
    val iin: String,
    @SerializedName("instrumentMaster") val bankMasterRecord: BankMasterRecord?,
    val institution_master_id: String,
    val interim_date: String,
    val is_enabled_for_autopay: Boolean,
    val is_tokenized: String,
    val is_total_due_enabled: Boolean,
    val last4: String,
    var card_network: String,
    val product_number: String,
    val bill: BillDetail?,
    val product_type: String,
    val tokenization_date: String,
    var productStatus: String,
    var customAmount: Double?,
    var rewardsPoint: Int?,
    var total_due: Double?,
    var amount_paid: Double?,
    var isChecked: Boolean = false,
    var billStatus: String,
    @SerializedName("amountExactness")
    var amountExactness: String?,
    var institutionName: String?,
    var billerName: String?,
    var theView: Int,
    var InstitutionId: String?,
    var rewardRate : Double?,
    val apiMessage: String,
    @field:SerializedName("user_err_msg")
    val userErrorMessage: String? = null,
    val status: Boolean
): Serializable