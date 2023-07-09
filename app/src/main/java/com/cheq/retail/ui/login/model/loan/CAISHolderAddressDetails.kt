package com.cheq.retail.ui.login.model.loan

import androidx.annotation.Keep

@Keep
data class CAISHolderAddressDetails(
    val City_non_normalized: CityNonNormalized,
    val CountryCode_non_normalized: CountryCodeNonNormalized,
    val First_Line_Of_Address_non_normalized: FirstLineOfAddressNonNormalized,
    val Second_Line_Of_Address_non_normalized: SecondLineOfAddressNonNormalized,
    val State_non_normalized: StateNonNormalized,
    val Third_Line_Of_Address_non_normalized: ThirdLineOfAddressNonNormalized,
    val ZIP_Postal_Code_non_normalized: ZIPPostalCodeNonNormalized
)