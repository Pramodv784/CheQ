package com.cheq.retail.ui.loans.model.loanv4

import androidx.annotation.Keep
import com.cheq.retail.ui.activateCard.model.InnerGridGradientColors
import com.cheq.retail.ui.login.model.loan.OuterGridGradientColors
@Keep
data class LoanV4(
    val bankmaster_id: Any,
    val beneficiary_id: Any,
    val bill: Bill?=null,
    val bill_generated_date: Any,
    val bill_repeat_count: Int,
    val cb_created_from: Any,
    val cheq_user_id: String,
    val created_at: String,
    val created_by: String,
    val created_from: String,
    val customer_name: String,
    val id: String,
    val ifsc_prefix: Any,
    val iin: Any,
    val innerGridGradientColors: InnerGridGradientColors,
    val institutionName: String,
    val institution_master_id: String,
    val interim_date: Any,
    val is_enabled_for_autopay: Boolean,
    val is_tokenized: String,
    val is_total_due_enabled: Boolean,
    val last4: String,

    val logo: Any,
    val logoWithName: String,
    val netwok_token_payout_support: Boolean,
    val outerGridGradientColors: OuterGridGradientColors,
    val product_number: String,
    val product_status: String,
    val product_type: String,
    val status: String,
    val tokenization_date: Any,
    val updated_at: String,
    val updated_by: String,
    val loanMasterRecord: LoanMasterRecord,
)