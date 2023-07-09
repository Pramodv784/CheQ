package com.cheq.retail.ui.splash.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable
@Keep
data class Event(

    @SerializedName("/onboarding/fetching-details")
    val fetching_details: Boolean?,
    @SerializedName("/onboarding/CVP/one")
    val CVP_one: Boolean?,
    @SerializedName("/onboarding/success")
    val success: Boolean?,
    @SerializedName("/onboarding/CVP/three")
    val CVP_three: Boolean?,
    @SerializedName("/onboarding/CVP/two")
    val CVP_two: Boolean?,
    @SerializedName("/onboarding/consents")
    val consents: Boolean?,
    @SerializedName("/onboarding/login")
    val login: Boolean?,
    @SerializedName("/onboarding/mobile-number")
    val mobile_number: Boolean?,
    @SerializedName("/onboarding/no-products-found")
    val no_products_found: Boolean?,
    @SerializedName("/onboarding/permissions")
    val permissions: Boolean?,
    @SerializedName("/onboarding/personal-details")
    val personal_details: Boolean?,
    @SerializedName("/onboarding/personal-details/additional")
    val personal_details_additional: Boolean?,
    @SerializedName("/onboarding/splashscreen")
    val splashscreen: Boolean?,
    @SerializedName("/onboarding/verify-otp")
    val verify_otp: Boolean?,
     @SerializedName("Onboarding_Add_New_Clicked")
    val Onboarding_Add_New_Clicked: Boolean?,
     @SerializedName("Onboarding_BureauConsent_Checkbox_Selected")
    val Onboarding_BureauConsent_Checkbox_Selected: Boolean?,
    @SerializedName("Onboarding_CVP_One_Clicked")
    val Onboarding_CVP_One_Clicked: Boolean?,
    @SerializedName("Onboarding_CVP_One_Skipped")
    val Onboarding_CVP_One_Skipped: Boolean?,
    @SerializedName("Onboarding_CVP_Three_BackClicked")
    val Onboarding_CVP_Three_BackClicked: Boolean?,
    @SerializedName("Onboarding_CVP_Three_Clicked")
    val Onboarding_CVP_Three_Clicked: Boolean?,
    @SerializedName("Onboarding_CVP_Two_BackClicked")
    val Onboarding_CVP_Two_BackClicked: Boolean?,
    @SerializedName("Onboarding_CVP_Two_Clicked")
    val Onboarding_CVP_Two_Clicked: Boolean?,
    @SerializedName("Onboarding_CVP_Two_Skipped")
    val Onboarding_CVP_Two_Skipped: Boolean?,
    @SerializedName("Onboarding_Consents_Clicked")
    val Onboarding_Consents_Clicked: Boolean?,
    @SerializedName("Onboarding_Login_Auth_Entered")
    val Onboarding_Login_Auth_Entered: Boolean?,
    @SerializedName("Onboarding_Login_Auth_Viewed")
    val Onboarding_Login_Auth_Viewed: Boolean?,
    @SerializedName("Onboarding_Login_Clicked")
    val Onboarding_Login_Clicked: Boolean?,
    @SerializedName("Onboarding_Login_DifferentUser_Clicked")
    val Onboarding_Login_DifferentUser_Clicked: Boolean?,
    @SerializedName("Onboarding_Mobile_Number_Clicked")
    val Onboarding_Mobile_Number_Clicked: Boolean?,
    @SerializedName("Onboarding_Mobile_Number_Entered")
    val Onboarding_Mobile_Number_Entered: Boolean?,
    @SerializedName("Onboarding_Mobile_Number_Selected")
    val Onboarding_Mobile_Number_Selected: Boolean?,
    @SerializedName("Onboarding_Notification_Permission_Allowed")
    val Onboarding_Notification_Permission_Allowed: Boolean?,
    @SerializedName("Onboarding_Notification_Permission_Denied")
    val Onboarding_Notification_Permission_Denied: Boolean?,
    @SerializedName("Onboarding_OTP_Clicked")
    val Onboarding_OTP_Clicked: Boolean?,
    @SerializedName("Onboarding_OTP_Edit_BackClicked")
    val Onboarding_OTP_Edit_BackClicked: Boolean?,
    @SerializedName("Onboarding_OTP_Entered")
    val Onboarding_OTP_Entered: Boolean?,
    @SerializedName("Onboarding_OTP_RequestCall_Clicked")
    val Onboarding_OTP_RequestCall_Clicked: Boolean?,
    @SerializedName("Onboarding_OTP_Resend_Clicked")
    val Onboarding_OTP_Resend_Clicked: Boolean?,
    @SerializedName("Onboarding_OTP_Timeout_Viewed")
    val Onboarding_OTP_Timeout_Viewed: Boolean?,
    @SerializedName("Onboarding_Permissions_Clicked")
    val Onboarding_Permissions_Clicked: Boolean?,
    @SerializedName("Onboarding_PersonalDetails_Additional_Clicked")
    val Onboarding_PersonalDetails_Additional_Clicked: Boolean?,
    @SerializedName("Onboarding_PersonalDetails_Clicked")
    val Onboarding_PersonalDetails_Clicked: Boolean?,
    @SerializedName("Onboarding_PersonalDetails_DOB_Entered")
    val Onboarding_PersonalDetails_DOB_Entered: Boolean?,
    @SerializedName("Onboarding_PersonalDetails_Email_Clicked")
    val Onboarding_PersonalDetails_Email_Clicked: Boolean?,
    @SerializedName("Onboarding_PersonalDetails_Email_Entered")
    val Onboarding_PersonalDetails_Email_Entered: Boolean?,
    @SerializedName("Onboarding_PersonalDetails_FName_Clicked")
    val Onboarding_PersonalDetails_FName_Clicked: Boolean?,
    @SerializedName("Onboarding_PersonalDetails_FName_Entered")
    val Onboarding_PersonalDetails_FName_Entered: Boolean?,
    @SerializedName("Onboarding_PersonalDetails_LName_Clicked")
    val Onboarding_PersonalDetails_LName_Clicked: Boolean?,
    @SerializedName("Onboarding_PersonalDetails_LName_Entered")
    val Onboarding_PersonalDetails_LName_Entered: Boolean?,
    @SerializedName("Onboarding_PersonalDetails_PAN_Entered")
    val Onboarding_PersonalDetails_PAN_Entered: Boolean?,
    @SerializedName("Onboarding_Phone_Permission_Allowed")
    val Onboarding_Phone_Permission_Allowed: Boolean?,
    @SerializedName("Onboarding_Phone_Permission_Denied")
    val Onboarding_Phone_Permission_Denied: Boolean?,
    @SerializedName("Onboarding_SMS_Permission_Allowed")
    val Onboarding_SMS_Permission_Allowed: Boolean?,
    @SerializedName("Onboarding_SMS_Permission_Denied")
    val Onboarding_SMS_Permission_Denied: Boolean?,
    @SerializedName("Onboarding_Success_Continue")
    val Onboarding_Success_Continue: Boolean?,
    @SerializedName("Onboarding_WhatsAppConsent_Checkbox_Deselected")
    val Onboarding_WhatsAppConsent_Checkbox_Deselected: Boolean?,
    @SerializedName("Onboarding_WhatsAppConsent_Checkbox_Selected")
    val Onboarding_WhatsAppConsent_Checkbox_Selected: Boolean?,


    @SerializedName("/cc-activation/other-card-details")
    val other_call_detail: Boolean?,
    @SerializedName("/cc-activation/verify-otp")
    val cc_verify_otp: Boolean?,
    @SerializedName("/cc-activation/success")
    val cc_success: Boolean?,
    @SerializedName("/cc-activation/enter-card-number")
    val cc_enter_card_number: Boolean?,
    @SerializedName("CC_Activation_CardNumber_Entered")
    val CC_Activation_CardNumber_Entered: Boolean?,
    @SerializedName("/cc-activation/pending")
    val cc_pending: Boolean?,
    @SerializedName("CC_Activation_Expiry_Entered")
    val CC_Activation_Expiry_Entered: Boolean?,
    @SerializedName("/cc-activation/failed")
    val cc_failed: Boolean?,
    @SerializedName("CC_Activation_CVV_Entered")
    val CC_Activation_CVV_Entered: Boolean?,
    @SerializedName("CC_Activation_CardNumber_Clicked")
    val CC_Activation_CardNumber_Clicked: Boolean?,
    @SerializedName("CC_Activation_EditName_Entered")
    val CC_Activation_EditName_Entered: Boolean?,
    @SerializedName("CC_Activation_EditName_Clicked")
    val CC_Activation_EditName_Clicked: Boolean?,
    @SerializedName("CC_Activation_OTP_Entered")
    val CC_Activation_OTP_Entered: Boolean?,
    @SerializedName("CC_Activation_OTP_Clicked")
    val CC_Activation_OTP_Clicked: Boolean?,
    @SerializedName("CC_Activation_OTP_RequestCall_Clicked")
    val CC_Activation_OTP_Resend_Clicked: Boolean?,
    @SerializedName("CC_Activation_OTP_Edit_BackClicked")
    val CC_Activation_OTP_Edit_BackClicked: Boolean?,
    @SerializedName("CC_Activation_OTP_Timeout_Viewed")
    val CC_Activation_OTP_Timeout_Viewed: Boolean?,
    @SerializedName("CC_Activation_Retry")
    val CC_Activation_Retry: Boolean?,
    @SerializedName("/cc-payment/pending")
    val CC_Payment_Pending: Boolean?,
    @SerializedName("CC_Activation_Success_Nudge2")
    val CC_Activation_Success_Nudge2: Boolean?,
    @SerializedName("/cc-payment/failed")
    val CC_Payment_failed: Boolean?,
    @SerializedName("/cc-payment/select-payment-method")
    val CC_Payment_Select_payment_method: Boolean?,
    @SerializedName("CC_Activation_Success_Nudge1")
    val CC_Activation_Success_Nudge1: Boolean?,
    @SerializedName("CC_Activation_Failed_BackClicked")
    val CC_Activation_Failed_BackClicked: Boolean?,
    @SerializedName("/cc-payment/select-amount")
    val CC_Payment_Select_amount: Boolean?,
    @SerializedName("/cc-payment/review-details")
    val CC_Payment_review_details: Boolean?,
    @SerializedName("CC_Activation_Success_Clicked")
    val CC_Activation_Success_Clicked: Boolean?,
    @SerializedName("/cc-payment/success")
    val CC_payment_Success: Boolean?,
    @SerializedName("CC_Payment_TotalDue_Clicked")
    val CC_Payment_TotalDue_Clicked: Boolean?,
    @SerializedName("CC_Payment_MinimumDue_Clicked")
    val CC_Payment_MinimumDue_Clicked: Boolean?,
    @SerializedName("CC_Payment_SelectAmount_Clicked")
    val CC_Payment_SelectAmount_Clicked: Boolean?,
    @SerializedName("CC_Payment_Custom_Clicked")
    val CC_Payment_Custom_Clicked: Boolean?,
    @SerializedName("CC_Payment_SelectAmount_BackClicked")
    val CC_Payment_SelectAmount_BackClicked: Boolean?,
    @SerializedName("CC_Payment_Custom_Entered")
    val CC_Payment_Custom_Entered: Boolean?,
    @SerializedName("CC_Payment_RewardsOption_Clicked")
    val CC_Payment_RewardsOption_Clicked: Boolean?,
    @SerializedName("CC_Payment_Rewards_KnowMore_Clicked")
    val CC_Payment_Rewards_KnowMore_Clicked: Boolean?,
    @SerializedName("CC_Payment_Rewards_KnowMore_BackClicked")
    val CC_Payment_Rewards_KnowMore_BackClicked: Boolean?,
    @SerializedName("CC_Payment_UPI_Search_Entered")
    val CC_Payment_UPI_Search_Entered: Boolean?,
    @SerializedName("CC_Payment_UPI_SeeAll_Clicked")
    val CC_Payment_UPI_SeeAll_Clicked: Boolean?,
    @SerializedName("CC_Payment_NetBanking_SeeAll_Clicked")
    val CC_Payment_NetBanking_SeeAll_Clicked: Boolean?,
    @SerializedName("CC_Payment_PreferredMethod_Clicked")
    val CC_Payment_PreferredMethod_Clicked: Boolean?,
    @SerializedName("CC_Payment_NetBanking_Search_Entered")
    val CC_Payment_NetBanking_Search_Entered: Boolean?,
    @SerializedName("CC_Payment_UPI_Clicked")
    val CC_Payment_UPI_Clicked: Boolean?,
    @SerializedName("CC_Payment_RewardsAmount_Entered")
    val CC_Payment_RewardsAmount_Entered: Boolean?,
    @SerializedName("CC_Payment_RewardsAmount_Clicked")
    val CC_Payment_RewardsAmount_Clicked: Boolean?,
    @SerializedName("CC_Payment_DebitCard_AddNew_Clicked")
    val CC_Payment_DebitCard_AddNew_Clicked: Boolean?,
    @SerializedName("CC_Payment_DebitCard_SeeAll_Clicked")
    val CC_Payment_DebitCard_SeeAll_Clicked: Boolean?,
    @SerializedName("CC_Payment_NetBanking_Clicked")
    val CC_Payment_NetBanking_Clicked: Boolean?,
    @SerializedName("CC_Payment_DebitCard_CardNumber_Entered")
    val CC_Payment_DebitCard_CardNumber_Entered: Boolean?,
    @SerializedName("CC_Payment_DebitCard_CVV_Entered")
    val CC_Payment_DebitCard_CVV_Entered: Boolean?,
    @SerializedName("CC_Payment_Failed_BackClicked")
    val CC_Payment_Failed_BackClicked: Boolean?,
    @SerializedName("CC_Payment_DebitCard_Checkbox_Deselected")
    val CC_Payment_DebitCard_Checkbox_Deselected: Boolean?,
    @SerializedName("CC_Payment_DebitCard_Expiry_Entered")
    val CC_Payment_DebitCard_Expiry_Entered: Boolean?,
    @SerializedName("CC_Payment_DebitCard_Checkbox_Selected")
    val CC_Payment_DebitCard_Checkbox_Selected: Boolean?,
    @SerializedName("CC_Payment_Success_ViewDetails_Clicked")
    val CC_Payment_Success_ViewDetails_Clicked: Boolean?,
    @SerializedName("CC_Payment_DebitCard_Name_Entered")
    val CC_Payment_DebitCard_Name_Entered: Boolean?,
    @SerializedName("CC_Payment_Review_EditMethod_Clicked")
    val CC_Payment_Review_EditMethod_Clicked: Boolean?,
    @SerializedName("CC_Payment_Success_Nudge1")
    val CC_Payment_Success_Nudge1: Boolean?,
    @SerializedName("CC_Payment_DebitCard_AddNew_BackClicked")
    val CC_Payment_DebitCard_AddNew_BackClicked: Boolean?,
    @SerializedName("CC_Payment_Review_Clicked")
    val CC_Payment_Review_Clicked: Boolean?,
    @SerializedName("CC_Payment_Review_BackClicked")
    val CC_Payment_Review_BackClicked: Boolean?,
    @SerializedName("CC_Payment_Success_Nudge2")
    val CC_Payment_Success_Nudge2: Boolean?,
    @SerializedName("CC_Payment_Success_Clicked")
    val CC_Payment_Success_Clicked: Boolean?,
    @SerializedName("CC_Payment_Retry")
    val CC_Payment_Retry: Boolean?,
    @SerializedName("/emandate-registration/select-bank")
    val Emandate_select_bank: Boolean?,
    @SerializedName("/emandate-registration/failed")
    val Emandate_Register_Failed: Boolean?,
    @SerializedName("/emandate-registration/choose-authentication-method")
    val Emandate_Choose_Auth_methode: Boolean?,
    @SerializedName("/emandate-registration/pending")
    val Emandate_Pending: Boolean?,
    @SerializedName("/emandate-registration/enter-account-details")
    val Emandate_Enter_Account_Detail: Boolean?,
    @SerializedName("/emandate-registration/introduction")
    val Emandate_register_intro: Boolean?,
    @SerializedName("/emandate-registration/enable-autopay")
    val Emandate_register_Enable_AutoPay: Boolean?,
    @SerializedName("Emandate_Bank_Search_Entered")
    val Emandate_Bank_Search_Entered: Boolean?,
    @SerializedName("Emandate_Consent_Checkbox_Selected")
    val Emandate_Consent_Checkbox_Selected: Boolean?,
    @SerializedName("/emandate-registration/success")
    val Emandate_Success: Boolean?,
    @SerializedName("Emandate_Bank_Clicked")
    val Emandate_Bank_Clicked: Boolean?,
    @SerializedName("Emandate_Consent_Checkbox_Deselected")
    val Emandate_Consent_Checkbox_Deselected: Boolean?,
    @SerializedName("Emandate_Introduction_BackClicked")
    val Emandate_Introduction_BackClicked: Boolean?,
    @SerializedName("Emandate_IFSC_Entered")
    val Emandate_IFSC_Entered: Boolean?,
    @SerializedName("Emandate_Help_Clicked")
    val Emandate_Help_Clicked: Boolean?,
    @SerializedName("Emandate_Bank_BackClicked")
    val Emandate_Bank_BackClicked: Boolean?,
    @SerializedName("Emandate_Introduction_Clicked")
    val Emandate_Introduction_Clicked: Boolean?,
    @SerializedName("Emandate_BankAccountNumber_Entered")
    val Emandate_BankAccountNumber_Entered: Boolean?,
    @SerializedName("Emandate_AccountHolderName_Entered")
    val Emandate_AccountHolderName_Entered: Boolean?,
    @SerializedName("Emandate_BankAccountDetails_Clicked")
    val Emandate_BankAccountDetails_Clicked: Boolean?,
    @SerializedName("Emandate_BankAccountDetails_BackClicked")
    val Emandate_BankAccountDetails_BackClicked: Boolean?,
    @SerializedName("Emandate_AuthVia_DebitCard_Clicked")
    val Emandate_AuthVia_DebitCard_Clicked: Boolean?,
    @SerializedName("Emandate_AuthVia_NetBanking_Clicked")
    val Emandate_AuthVia_NetBanking_Clicked: Boolean?,
    @SerializedName("Emandate_Success_Clicked")
    val Emandate_Success_Clicked: Boolean?,
    @SerializedName("Emandate_AuthVia_Aadhar_Clicked")
    val Emandate_AuthVia_Aadhar_Clicked: Boolean?,
    @SerializedName("Emandate_AuthenticationMethod_BackClicked")
    val Emandate_AuthenticationMethod_BackClicked: Boolean?,
    @SerializedName("Emandate_AuthenticationMethod_Clicked")
    val Emandate_AuthenticationMethod_Clicked: Boolean?,
    @SerializedName("Emandate_Registration_Retry")
    val Emandate_Registration_Retry: Boolean?,
    @SerializedName("Emandate_Success_BackClicked")
    val Emandate_Success_BackClicked: Boolean?,
    @SerializedName("Emandate_Success_Nudge1")
    val Emandate_Success_Nudge1: Boolean?,
    @SerializedName("Emandate_Success_Nudge2")
    val Emandate_Success_Nudge2: Boolean?,
    @SerializedName("Emandate_AutopayEnable_BackClicked")
    val Emandate_AutopayEnable_BackClicked: Boolean?,
    @SerializedName("Autopay_Product1_MinDue_Clicked")
    val Autopay_Product1_MinDue_Clicked: Boolean?,
    @SerializedName("Autopay_Product2_Deselected")
    val Autopay_Product2_Deselected: Boolean?,
    @SerializedName("Autopay_Product1_Deselected")
    val Autopay_Product1_Deselected: Boolean?,
    @SerializedName("Autopay_Product1_Selected")
    val Autopay_Product1_Selected: Boolean?,
    @SerializedName("Autopay_Product2_MinDue_Clicked")
    val Autopay_Product2_MinDue_Clicked: Boolean?,
    @SerializedName("Autopay_Product1_TotalDue_Clicked")
    val Autopay_Product1_TotalDue_Clicked: Boolean?,
    @SerializedName("Autopay_Product2_TotalDue_Clicked")
    val Autopay_Product2_TotalDue_Clicked: Boolean?,
    @SerializedName("Autopay_Product2_Selected")
    val Autopay_Product2_Selected: Boolean?,
    @SerializedName("/cheq-safe/pending")
    val Cheq_Safe_Pending: Boolean?,
    @SerializedName("/cheq-safe/select-email")
    val Cheq_Safe_Select_Email: Boolean?,
    @SerializedName("CheqSafe_Introduction_Clicked")
    val CheqSafe_Introduction_Clicked: Boolean?,
    @SerializedName("CheqSafe_EmailClient_Gmail_Selected")
    val CheqSafe_EmailClient_Gmail_Selected: Boolean?,
    @SerializedName("/cheq-safe/success")
    val Cheq_Safe_Sucess: Boolean?,
    @SerializedName("/cheq-safe/no-statement-found")
    val Cheq_Safe_Statement_found: Boolean?,
    @SerializedName("/cheq-safe/introduction")
    val Cheq_Safe_Introduction: Boolean?,
    @SerializedName("/cheq-safe/failed")
    val Cheq_Safe_Failed: Boolean?,
    @SerializedName("CheqSafe_Introduction_BackClicked")
    val CheqSafe_Introduction_BackClicked: Boolean?,
    @SerializedName("CheqSafe_EmailAccess_Denied")
    val CheqSafe_EmailAccess_Denied: Boolean?,
    @SerializedName("CheqSafe_EmailClient_Yahoo_Selected")
    val CheqSafe_EmailClient_Yahoo_Selected: Boolean?,
    @SerializedName("CheqSafe_EmailClient_Hotmail_Selected")
    val CheqSafe_EmailClient_Hotmail_Selected: Boolean?,
    @SerializedName("CheqSafe_Success_Clicked")
    val CheqSafe_Success_Clicked: Boolean?,
    @SerializedName("CheqSafe_EmailClient_Outlook_Selected")
    val CheqSafe_EmailClient_Outlook_Selected: Boolean?,
    @SerializedName("CheqSafe_EmailClient_Live_Selected")
    val CheqSafe_EmailClient_Live_Selected: Boolean?,
    @SerializedName("CheqSafe_EmailAccess_Allowed")
    val CheqSafe_EmailAccess_Allowed: Boolean?,
    @SerializedName("CheqSafe_Access_Denied")
    val CheqSafe_Access_Denied: Boolean?,
    @SerializedName("CheqSafe_SelectEmail_Clicked")
    val CheqSafe_SelectEmail_Clicked: Boolean?,
    @SerializedName("CheqSafe_SelectEmail_Selected")
    val CheqSafe_SelectEmail_Selected: Boolean?,
    @SerializedName("CheqSafe_Success_BackClicked")
    val CheqSafe_Success_BackClicked: Boolean?,
    @SerializedName("CheqSafe_Failed_Retry")
    val CheqSafe_Failed_Retry: Boolean?,
    @SerializedName("CheqSafe_Access_Retry")
    val CheqSafe_Access_Retry: Boolean?,
    @SerializedName("CheqSafe_StatementNotFound_BackClicked")
    val CheqSafe_StatementNotFound_BackClicked: Boolean?,
    @SerializedName("CheqSafe_Failed_BackClicked")
    val CheqSafe_Failed_BackClicked: Boolean?,
    @SerializedName("CheqSafe_StatementNotFound_Link_Clicked")
    val CheqSafe_StatementNotFound_Link_Clicked: Boolean?,
    @SerializedName("/loan-activation/select-lender")
    val Loan_select_lender: Boolean?,
    @SerializedName("/loan-activation/failed")
    val Loan_activation_failed: Boolean?,
    @SerializedName("/loan-activation/success")
    val Loan_success: Boolean?,
    @SerializedName("/loan-activation/enter-details")
    val Loan_enter_detail: Boolean?,
    @SerializedName("Loan_Activation_Search_Entered")
    val Loan_Activation_Search_Entered: Boolean?,
    @SerializedName("/loan-activation/pending")
    val Loan_Pending: Boolean?,
    @SerializedName("Loan_Activation_Retry")
    val Loan_Activation_Retry: Boolean?,
    @SerializedName("Loan_Activation_Lender_BackClicked")
    val Loan_Activation_Lender_BackClicked: Boolean?,
    @SerializedName("Loan_Activation_Lender_Clicked")
    val Loan_Activation_Lender_Clicked: Boolean?,
    @SerializedName("Loan_Activation_DOB_Entered")
    val Loan_Activation_DOB_Entered: Boolean?,
    @SerializedName("Loan_Activation_MobileNumber_Entered")
    val Loan_Activation_MobileNumber_Entered: Boolean?,
    @SerializedName("Loan_Activation_Success_PayNow_Clicked")
    val Loan_Activation_Success_PayNow_Clicked: Boolean?,
    @SerializedName("Loan_Activation_OtherDetails_Entered")
    val Loan_Activation_OtherDetails_Entered: Boolean?,
    @SerializedName("Loan_Activation_LoanAccountNumber_Entered")
    val Loan_Activation_LoanAccountNumber_Entered: Boolean?,
    @SerializedName("Loan_Activation_Success_Nudge2")
    val Loan_Activation_Success_Nudge2: Boolean?,
    @SerializedName("Loan_Activation_Success_Clicked")
    val Loan_Activation_Success_Clicked: Boolean?,
    @SerializedName("/loan-payment/select-amount")
    val Loan_select_Amount: Boolean?,
    @SerializedName("Loan_Activation_Success_Nudge1")
    val Loan_Activation_Success_Nudge1: Boolean?,
    @SerializedName("Loan_Activation_BillFetch_Clicked")
    val Loan_Activation_BillFetch_Clicked: Boolean?,
    @SerializedName("Loan_Activation_Failed_BackClicked")
    val Loan_Activation_Failed_BackClicked: Boolean?,
    @SerializedName("Loan_Payment_TotalDue_Clicked")
    val Loan_Payment_TotalDue_Clicked: Boolean?,
    @SerializedName("/loan-payment/review-details")
    val Loan_Review_Detail: Boolean?,
    @SerializedName("/loan-payment/success")
    val Loan_Sucess: Boolean?,
    @SerializedName("Loan_Payment_MinimumDue_Clicked")
    val Loan_Payment_MinimumDue_Clicked: Boolean?,
    @SerializedName("/loan-payment/failed")
    val Loan_Failed: Boolean?,
    @SerializedName("Loan_Payment_Custom_Clicked")
    val Loan_Payment_Custom_Clicked: Boolean?,
    @SerializedName("/loan-payment/pending")
    val Loan_Payment_Pending: Boolean?,
    @SerializedName("Loan_Payment_SelectAmount_Clicked")
    val Loan_Payment_SelectAmount_Clicked: Boolean?,
    @SerializedName("/loan-payment/select-payment-method")
    val Laon_Payment_Select_payment_Method: Boolean?,
    @SerializedName("Loan_Payment_Rewards_KnowMore_Clicked")
    val Loan_Payment_Rewards_KnowMore_Clicked: Boolean?,
    @SerializedName("Loan_Payment_Custom_Entered")
    val Loan_Payment_Custom_Entered: Boolean?,
    @SerializedName("Loan_Payment_UPI_SeeAll_Clicked")
    val Loan_Payment_UPI_SeeAll_Clicked: Boolean?,
    @SerializedName("Loan_Payment_UPI_Search_Entered")
    val Loan_Payment_UPI_Search_Entered: Boolean?,
    @SerializedName("Loan_Payment_SelectAmount_BackClicked")
    val Loan_Payment_SelectAmount_BackClicked: Boolean?,
    @SerializedName("Loan_Payment_Rewards_KnowMore_BackClicked")
    val Loan_Payment_Rewards_KnowMore_BackClicked: Boolean?,
    @SerializedName("Loan_Payment_NetBanking_SeeAll_Clicked")
    val Loan_Payment_NetBanking_SeeAll_Clicked: Boolean?,
    @SerializedName("Loan_Payment_UPI_Clicked")
    val Loan_Payment_UPI_Clicked: Boolean?,
    @SerializedName("Loan_Payment_NetBanking_Search_Entered")
    val Loan_Payment_NetBanking_Search_Entered: Boolean?,
    @SerializedName("Loan_Payment_NetBanking_Clicked")
    val Loan_Payment_NetBanking_Clicked: Boolean?,
    @SerializedName("Loan_Payment_RewardsAmount_Entered")
    val Loan_Payment_RewardsAmount_Entered: Boolean?,
    @SerializedName("Loan_Payment_RewardsAmount_Clicked")
    val Loan_Payment_RewardsAmount_Clicked: Boolean?,
    @SerializedName("Loan_Payment_PreferredMethod_Clicked")
    val Loan_Payment_PreferredMethod_Clicked: Boolean?,
    @SerializedName("Loan_Payment_RewardsOption_Clicked")
    val Loan_Payment_RewardsOption_Clicked: Boolean?,
    @SerializedName("Loan_Payment_DebitCard_SeeAll_Clicked")
    val Loan_Payment_DebitCard_SeeAll_Clicked: Boolean?,
    @SerializedName("Loan_Payment_DebitCard_Expiry_Entered")
    val Loan_Payment_DebitCard_Expiry_Entered: Boolean?,
    @SerializedName("Loan_Payment_DebitCard_CVV_Entered")
    val Loan_Payment_DebitCard_CVV_Entered: Boolean?,
    @SerializedName("Loan_Payment_DebitCard_CardNumber_Entered")
    val Loan_Payment_DebitCard_CardNumber_Entered: Boolean?,
    @SerializedName("Loan_Payment_DebitCard_Name_Entered")
    val Loan_Payment_DebitCard_Name_Entered: Boolean?,
    @SerializedName("Loan_Payment_DebitCard_AddNew_Clicked")
    val Loan_Payment_DebitCard_AddNew_Clicked: Boolean?,
    @SerializedName("Loan_Payment_DebitCard_Checkbox_Selected")
    val Loan_Payment_DebitCard_Checkbox_Selected: Boolean?,
    @SerializedName("Loan_Payment_DebitCard_Checkbox_Deselected")
    val Loan_Payment_DebitCard_Checkbox_Deselected: Boolean?,
    @SerializedName("Loan_Payment_Review_Clicked")
    val Loan_Payment_Review_Clicked: Boolean?,
    @SerializedName("Loan_Payment_Review_EditMethod_Clicked")
    val Loan_Payment_Review_EditMethod_Clicked: Boolean?,
    @SerializedName("Loan_Payment_DebitCard_AddNew_BackClicked")
    val Loan_Payment_DebitCard_AddNew_BackClicked: Boolean?,
    @SerializedName("Loan_Payment_Review_BackClicked")
    val Loan_Payment_Review_BackClicked: Boolean?,
    @SerializedName("Loan_Payment_Failed_BackClicked")
    val Loan_Payment_Failed_BackClicked: Boolean?,
    @SerializedName("Loan_Payment_Success_ViewDetails_Clicked")
    val Loan_Payment_Success_ViewDetails_Clicked: Boolean?,
    @SerializedName("Loan_Payment_Success_Clicked")
    val Loan_Payment_Success_Clicked: Boolean?,
    @SerializedName("Loan_Payment_Retry")
    val Loan_Payment_Retry: Boolean?,
    @SerializedName("Loan_Payment_Success_Nudge1")
    val Loan_Payment_Success_Nudge1: Boolean?,
    @SerializedName("Loan_Payment_Success_Nudge2")
    val Loan_Payment_Success_Nudge2: Boolean?,


    ) : Serializable