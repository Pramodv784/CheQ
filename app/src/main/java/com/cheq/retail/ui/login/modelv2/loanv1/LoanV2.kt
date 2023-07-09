package com.cheq.retail.ui.login.modelv2.loanv1

import androidx.annotation.Keep
import com.cheq.retail.ui.login.model.loan.BillDetail
import com.cheq.retail.ui.login.modelv2.productv1.OuterGridGradientColorsX
@Keep
data class LoanV2(
    val bankmaster_id: String,
    val beneficiary_id: String,
    val bill_generated_date: String,
    val bill_repeat_count: Int,
    val cb_created_from: String,
    val cheq_user_id: String,
    val created_from: String,
    val customer_name: String,
    val id: String,
    val iin: Any,
    val innerGridGradientColors: Any,
    val institutionName: String,
    val institution_master_id: String,
    val interim_date: Any,
    val is_enabled_for_autopay: Boolean,
    val is_tokenized: String,
    val is_total_due_enabled: Boolean,
    val last4: String,
    val loanMasterRecord: LoanMasterRecord,
    val logo: String,
    val logoWithName: String,
    val netwok_token_payout_support: Boolean,
    val outerGridGradientColors: OuterGridGradientColorsX,
    val product_number: String,
    val product_status: String,
    val product_type: String,
    val bills: BillDetail,
    val tokenization_date: Any
)