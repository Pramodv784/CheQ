package com.cheq.retail.ui.loans.model

import androidx.annotation.Keep

@Keep
data class LoanBillersResponse(
    val topBillers:List<Loan2>
)
@Keep
data class TopLoanBillersResponse(

    val topBillers:List<Loan2>

)