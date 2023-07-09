package com.cheq.retail.ui.login.model.loan

import androidx.annotation.Keep

@Keep
data class Json(
    val AccountHoldertypeCode: AccountHoldertypeCode,
    val Account_Number: AccountNumber,
    val Account_Status: AccountStatus,
    val Account_Type: AccountType,
    val Amount_Past_Due: AmountPastDue,
    val CAIS_Account_History: CAISAccountHistory,
    val CAIS_Holder_Address_Details: CAISHolderAddressDetails,
    val CAIS_Holder_Details: CAISHolderDetails,
    val CAIS_Holder_ID_Details: CAISHolderIDDetails,
    val CAIS_Holder_Phone_Details: CAISHolderPhoneDetails,
    val CurrencyCode: CurrencyCode,
    val Current_Balance: CurrentBalance,
    val DateOfAddition: DateOfAddition,
    val Date_Reported: DateReported,
    val Highest_Credit_or_Original_Loan_Amount: HighestCreditOrOriginalLoanAmount,
    val Identification_Number: IdentificationNumber,
    val Open_Date: OpenDate,
    val Payment_History_Profile: PaymentHistoryProfile,
    val Payment_Rating: PaymentRating,
    val Portfolio_Type: PortfolioType,
    val Repayment_Tenure: RepaymentTenure,
    val Subscriber_Name: SubscriberName
)