package com.cheq.retail.ui.login.model.loan

import androidx.annotation.Keep

@Keep
data class CAISAccountHistory(
    val Asset_Classification: AssetClassification,
    val Days_Past_Due: DaysPastDue,
    val Month: Month,
    val Year: Year
)