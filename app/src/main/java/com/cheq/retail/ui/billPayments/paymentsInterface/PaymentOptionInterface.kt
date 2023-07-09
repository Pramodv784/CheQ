package com.cheq.retail.ui.billPayments.paymentsInterface

interface PaymentOptionInterface {
    fun onPreferredMethodClicked(
        preferredMethod: String,
        id: String,
        logo: String,
        originalBankName: String,
        ifscPrefix : String,
        lastFour : String,
        cvv: String,
        bankingName : String,
        dcToken : String,
        bankMasterId : String
    )
}