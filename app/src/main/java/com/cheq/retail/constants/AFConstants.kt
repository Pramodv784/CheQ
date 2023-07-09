package com.cheq.retail.constants

object AFConstants {

     const val AF_DEV_KEY = "Zz7fG7FEJp5upYsaTEjFMY"
     const val AF_DOMAIN = "app.cheq.one"
     const val TEMPLATE_ID = "3UIC"
     const val CONNECT_TO_INTERNET = "CONNECT TO INTERNET"
     const val SOMETHING_WENT_WRONG = "Something went wrong"
     const val MESSAGE = "message"
     const val SUCCESS = "SUCCESS"
     const val FAILED = "FAILED"
     const val STATUS = "status"
     const val API_MESSAGE = "apiMessage"
     const val ONE_SIDED = "ONE_SIDED"
     const val CAPTURED = "CAPTURED"
     const val CAPTURED_SMALL = "captured"
     const val CREATED = "CREATED"
     const val CREATED_SMALL = "created"
     const val REFUNDED = "REFUNDED"
     const val PROCESSING = "PROCESSING"
     const val MASTER_CARD = "MasterCard"
     const val VISA = "Visa"
     const val DINERS_CARD = "Diners Club"
     const val AMERICAN_CARD = "American Express"
     const val RUPAY_CARD = "RuPay"
     const val CC = "CC"
     const val IMPS = "imps"
     const val NEFT = "neft"
     const val LOAN = "LOAN"
     const val TWO_MINS = "2 Mins"
     const val THIRTY_MINS = "30 Mins"
     const val TWO_DAYS = "2 Days"
     const val SEPARATOR = "|"
     const val NUM_REGEX = "[^0-9]"
     const val DATE_TAG = "<date>"
     const val CUSTOM = "custom"
     const val FULL = "full"
     const val MIN = "min"
     const val NET_BANKING = "NET_BANKING"
     const val NO_REWARDS = "no-rewards"
     const val USING_REWARDS = "using-rewards"
     // Appsflyer Events KEY *******

     const val AF_USER_OPENS="User opens the app - first open"
     const val AF_SPLASH_EVENT="onboarding/splashscreen"
     const val AF_CUST_FIRST_OPEN="af_first_open"
    // const val FB_CUST_FIRST_OPEN="firebase_first_open"

     const val AF_VERIFY_OTP="OTP successfully verified"
     const val AF_PERSONAL_DETAIL="/onboarding/personal-details"
     const val AF_CUST_COMPLETE_REGIST="af_complete_registration"
   //  const val FB_CUST_COMPLETE_REGIST="firebase_complete_registration"

     const val AF_ONBORAD_SUCESS="Onboarding success: we have found at least one product of the user (excluding manual addition)"
     const val AF_ONBORAD_SUCESS_WITH_PRODUCT="/onboarding/success-with-products-found"
     const val AF_CUST_ONBOARD_SUCESS="af_onboarding_success"
   //  const val FB_CUST_ONBOARD_SUCESS="firebase_onboarding_success"

     const val AF_WAITLIST_SUCESS="Waitlist Success"
     const val AF_ONBORAD_WAITLIST_SUCESS="/onboarding/waitlist-success"
     const val AF_CUST_WAITLIST_SUCESS="af_waitlist_success"
   //  const val FB_CUST_WAITLIST_SUCESS="firebase_waitlist_success"

     const val AF_REFERRAL_NOW_CLICK="Referral - Refer Now button click"
     const val AF_ONBOARD_REFERRAL="Onboarding_Waitlist_ReferNow_Clicked"
     const val AF_REFERRAL_CLICK="af_refer_clicked"
    // const val FB_REFERRAL_CLICK="firebase_refer_clicked"

     //content urls
     const val BANK_BASE="https://content.cheq.one/bankmaster/"
     const val LOAN_BASE="https://content.cheq.one/loansmaster/"
     const val BANNER_BASE="https://content.cheq.one/banners/"
     const val HTML_BASE="https://content.cheq.one/html/"
     const val POLICIES_BASE="https://content.cheq.one/html/policies/"
     const val FAQS_BASE="https://content.cheq.one/html/faqs/"
     const val VOUCHER_BASE="https://content.cheq.one/"
     const val CVV_BASE="https://content.cheq.one/"
     const val WAITLIST_BASE="https://content.cheq.one/waitlist/"


     const val AF_PAYMENT_SUCESS="Payment success"
     const val AF_PAY_TOGETHER_SUCESS="pay_together_success"
     const val AF_CC_PAYMENT_SUCESS="cc_payment_success"
     const val AF_PAY_TOGETHER_PAYMENT_SUCESS="af_paytogether_payment_success"
     const val AF_LOAN_PAYMENT_SUCESS="loan_payment_success"
     const val AF_CUST_CC_PAYMENT_SUCESS="af_cc_payment_success"
     const val AF_CUST_LOAN_SUCESS="af_loan_payment_success"
 //    const val FB_PAY_TOGETHER_PAYMENT_SUCESS="firebase_paytogether_payment_success"
  //   const val FB_CC_PAYMENT_SUCESS="firebase_cc_payment_success"
  //   const val FB_LOAN_SUCESS="firebase_loan_payment_success"



     // Firebase Events KEY *******

     const val FBEvent_USER_OPENS="User opens the app - first open"
     const val FBEvent_SPLASH_EVENT="onboarding_splashscreen"
     const val FBEvent_CUST_FIRST_OPEN="af_first_open"
     const val FB_CUST_FIRST_OPEN="firebase_first_open"

     const val FBEvent_VERIFY_OTP="OTP successfully verified"
     const val FBEvent_PERSONAL_DETAIL="onboarding_personal_details"
     const val FBEvent_CUST_COMPLETE_REGIST="af_complete_registration"
     const val FB_CUST_COMPLETE_REGIST="firebase_complete_registration"

     const val FBEvent_ONBORAD_SUCESS="Onboarding success: we have found at least one product of the user (excluding manual addition)"
     const val FBEvent_ONBORAD_SUCESS_WITH_PRODUCT="onboarding_success_with_products_found"
     const val FBEvent_CUST_ONBOARD_SUCESS="af_onboarding_success"
     const val FB_CUST_ONBOARD_SUCESS="firebase_onboarding_success"

     const val FBEvent_WAITLIST_SUCESS="Waitlist Success"
     const val FBEvent_ONBORAD_WAITLIST_SUCESS="onboarding_waitlist_success"
     const val FBEvent_CUST_WAITLIST_SUCESS="af_waitlist_success"
     const val FB_CUST_WAITLIST_SUCESS="firebase_waitlist_success"

     const val FBEvent_REFERRAL_NOW_CLICK="Referral - Refer Now button click"
     const val FBEvent_ONBOARD_REFERRAL="Onboarding_Waitlist_ReferNow_Clicked"
     const val FBEvent_REFERRAL_CLICK="af_refer_clicked"
     const val FB_REFERRAL_CLICK="firebase_refer_clicked"

     const val FBEvent_PAYMENT_SUCESS="Payment success"
     const val FBEvent_PAY_TOGETHER_SUCESS="pay_together_success"
     const val FBEvent_CC_PAYMENT_SUCESS="cc_payment_success"
     const val FBEvent_PAY_TOGETHER_PAYMENT_SUCESS="af_paytogether_payment_success"
     const val FBEvent_LOAN_PAYMENT_SUCESS="loan_payment_success"
     const val FBEvent_CUST_CC_PAYMENT_SUCESS="af_cc_payment_success"
     const val FBEvent_CUST_LOAN_SUCESS="af_loan_payment_success"
     const val FB_PAY_TOGETHER_PAYMENT_SUCESS="firebase_paytogether_payment_success"
     const val FB_CC_PAYMENT_SUCESS="firebase_cc_payment_success"
     const val FB_LOAN_SUCESS="firebase_loan_payment_success"

     const val FBEvent_SSL_PINNING="Ssl Pinning"
     const val FBEvent_SSL_PINNING_ERROR="ssl_pinning_error"
     const val FBEvent_SSL_DELAY="ssl_pinning_delay"


     const val EncryptionPassApplication="EncryptionPassApplication"
     const val EncryptionPassRestClient="EncryptionPassApplication"


     /// C2C Error Log

     const val FBEvent_USER_WANT_TO_CONVERT_TO_CASH="User want to convert to cash"
     const val FBEvent_CONVERT_TO_CASH_ERROR="Oops! Unable to convert to cash. Please try again later."
     const val FBEvent_CUST_C2C_ERROR="convert_to_cash_error"


     //DeepLink Constants
     const val DEEP_LINK_VALUE="deep_link_value"
     const val DEEP_LINK_SUB1="deep_link_sub1"
     const val DEEP_LINK_SUB2="deep_link_sub2"
     const val AF_OG_IMAGE="af_og_image"
     const val AF_OG_TITLE="af_og_title"
     const val AF_OG_DESCRIPTION="af_og_description"
     const val INVITE="invite"
     const val PAYMENT_DOWNTIME="payment_downtime"

     //Restclient
     const val OS="os"
     const val ANDROID="ANDROID"
     const val APP_VERSION="appversion"
     const val SALT="salt"
     const val DEVICE_ID="deviceid"
     const val HEADER_HASH="headerhash"

     //Referral events
     const val CURRENT_USER_CHEQ_ID="currentUserCheqId"
     const val REFERRAL_REQEUST_DATA="referralRequestData"
     const val ERROR_DESCRIPTION="errorDescription"

     //Deeplink Events
     const val AF_CONVERSION_DATA_FAILED="AF_CONVERSION_DATA_FAILED"
     const val AF_ATTRIBUTION_FAILED="AF_ATTRIBUTION_FAILED"
     const val DEEPLINK_NOT_FOUND="DEEPLINK_NOT_FOUND"
     const val DEEPLINK_ERROR="DEEPLINK_ERROR"
     const val REFERRER_ID_NOT_FOUND="REFERRER_ID_NOT_FOUND"
     const val SUPPORT="support"
     const val DEEPLINK_SUBSCRIPTION_FAILED="DEEPLINK_SUBSCRIPTION_FAILED"

     const val SHA_256 = "sha256/"
     const val FILENAME_CHEQ = "CHEQ"
     const val FILENAME_CHEQ_ENCRYPT = "CHEQ_ENCRYPT"
     const val FILENAME_CHEQ_PERMISSION = "CHEQ_PERMISSION"
     const val FILENAME_CHEQ_PERMISSION_ENCRYPT = "CHEQ_PERMISSION_ENCRYPT"
     const val FILENAME_CHEQUSERID = "CHEQUSERID"
     const val FILENAME_CHEQUSERID_ENCRYPT = "CHEQUSERID_ENCRYPT"
     const val FILENAME_LOG_SCREEN = "LogScreen"
     const val FILENAME_LOG_SCREEN_ENCRYPT = "LogScreen_ENCRYPT"
     const val FILENAME_REFERRAL = "referral"
     const val FILENAME_REFERRAL_ENCRYPT = "referral_ENCRYPT"
     const val PAYABLE_AMOUNT = "payable-amount"
     const val FEES = "fees"
     const val BANK_NAME_ = "bank_name"
     const val PAYIN_AMOUNT = "payin_amount"
     const val CC_LAST_4_DIGITS = "cc_last4digits"
     const val PRODUCT_TYPE = "product_type"
     const val PAYMENT_METHOD_NAME = "payment_method_name"
     const val REWARDS_EARN = "rewards-earn"
     const val NET_EARNINGS = "net-earnings"
     const val REWARD_USE = "rewards-use"
     const val PAY_METHOD = "pay-method"
     const val PAYMENT_METHOD_INVALID = "payment-method-invalid"
     const val MONTHLY_LIMIT_HIT = "monthly-limit-hit"
     const val NO = "NO"
     const val YES = "YES"

     //Finart
     const val FINART_INVOKE = "invoke"
     const val FINART_SUCCESS = "success"
     const val SCREEN_OTP = "OTP"
     const val SCREEN_SPLASH = "SPLASH"
     const val SCREEN_BACKGROUND = "BACKGROUND"
     const val CHEQ_USER = "CHEQ_USER"
     const val FINART_TRIGGER_OTP = "FINART_TRIGGER_OTP"
     const val FINART_SUCCESS_OTP = "FINART_SUCCESS_OTP"
     const val FINART_TRIGGER_SPLASH = "FINART_TRIGGER_SPLASH"
     const val FINART_SUCCESS_SPLASH = "FINART_SUCCESS_SPLASH"
     const val FINART_BACKGROUND_SMS_PROCESSING = "FINART_BACKGROUND_SMS_PROCESSING"

     //Error-blocker-v2 events
     const val KEY_SCREEN_NAME = "screen_name"
     const val SCREEN_ONBOARDING_BLOCKED = "/onboarding/blocked"
     const val PROPERTY_TITLE = "title"
     const val PROPERTY_DESCRIPTION = "description"
     const val PROPERTY_PRIMARY_CTA = "primary-cta"
     const val PROPERTY_SECONDARY_CTA = "secondary-cta"
     const val EVENT_PRIMARY_CTA_CLICKED = "Onboarding_Blocked_PrimaryCTA_Clicked"
     const val DESC_PRIMARY_CTA_CLICKED = "When user clicks the primary CTA on the blocker screen"
     const val EVENT_SECONDARY_CTA_CLICKED = "Onboarding_Blocked_SecondaryCTA_Clicked"
     const val DESC_SECONDARY_CTA_CLICKED = "When user clicks the secondary CTA on the blocker screen"

     // Enter Card number screen
     const val  EVENT_CC_VERIFICATION_CARDNUMBER_CLICKED="CC_Verification_CardNumber_Clicked"
     const val  DESC_CC_VERIFICATION_CARDNUMBER_CLICKED="When customer clicks on NEXT CTA on the card number entered screen"
     const val  CC_VERIFICATION_SCREEN_NAME="/cc-verification/enter-card-number"
     const val  CC_VERIFICATION_ERROR_NAME="/cc-verification/card-number-error"
     const val  BANK_NAME="bankname"
     const val  LAST_FOUR="lastfour"
     const val  SCREEN_TYPE="screentype"
     const val IDENTIFIED_CARD="identified_card"
     const val MANUAL_CARD="manual_card"
}