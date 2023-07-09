package com.cheq.retail.ui.login.model.loan

import androidx.annotation.Keep

@Keep
data class CAISHolderDetails(
    val Date_of_birth: DateOfBirth,
    val First_Name_Non_Normalized: FirstNameNonNormalized,
    val Gender_Code: GenderCode,
    val Income_TAX_PAN: IncomeTAXPAN,
    val Surname_Non_Normalized: SurnameNonNormalized
)