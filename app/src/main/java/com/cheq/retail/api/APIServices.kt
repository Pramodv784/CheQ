package com.cheq.retail.api


import com.cheq.retail.custom.ShouldNotDecrypt
import com.cheq.retail.inappratings.models.response.FeedbackResponse
import com.cheq.retail.inappratings.models.response.InAppDiscoveryResponse
import com.cheq.retail.ui.accountSettings.model.DataHistory
import com.cheq.retail.ui.accountSettings.model.DetailList
import com.cheq.retail.ui.accountSettings.model.GetEmailDelinkQuestionResponse
import com.cheq.retail.ui.accountSettings.model.UserSettingResponse
import com.cheq.retail.ui.activateCard.model.*
import com.cheq.retail.ui.billPayments.model.*
import com.cheq.retail.ui.billSummary.model.BillSummaryResponse
import com.cheq.retail.ui.dashboard.model.creditDashBoard.CreditDashBoardProductResponse
import com.cheq.retail.ui.dashboard.model.creditDashBoard.CreditDashBoardResponse
import com.cheq.retail.ui.dashboard.model.homedashboad.HomeDashBoardResponse
import com.cheq.retail.ui.emandate.model.BankListResponse
import com.cheq.retail.ui.emandate.model.EmandateOrderResponse
import com.cheq.retail.ui.loans.model.BBPSPREFILLED
import com.cheq.retail.ui.loans.model.LoanBillersResponse
import com.cheq.retail.ui.loans.model.LoanProviderResponse
import com.cheq.retail.ui.login.model.*
import com.cheq.retail.ui.login.modelv2.productv1.ProductV2
import com.cheq.retail.ui.main.model.*
import com.cheq.retail.ui.rewards.model.*
import com.cheq.retail.ui.socialLogin.model.UserGmailDetailsResponse
import com.cheq.retail.ui.splash.model.*
import com.cheq.retail.ui.verifyOtp.model.*
import com.cheq.retail.ui.vouchers.model.AssetsUrlModel
import com.google.gson.JsonObject
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface APIServices {

    @POST("/apis/auth/v2/createCheqUser")
    suspend fun createCheqUser(@Body model: EncryptionProvider): Response<DataCheq>?


    @POST("/apis/auth/v2/register")
    suspend fun generateOtpForLogin(@Body model: EncryptionProvider): Response<DataGenerateOtp>?


    @POST("/apis/auth/consent/userConsent")
    suspend fun userConsent(@Body model: EncryptionProvider): Response<GenerateLoginOtpResponseX>?

    @POST("/apis/auth/otpReport")
    suspend fun otpReport(@Body model: EncryptionProvider): Response<ReportOtpResponse>?

    @POST("/apis/auth/v2/verifyOTPv1")
    suspend fun verifyOtpByMobileNumber(@Body model: EncryptionProvider): Response<DataVerifyOtp>?

    @POST("/apis/auth/otpViaCall")
    suspend fun verifyOtpByMobileNumberCall(@Body model: EncryptionProvider): Response<VerifyOtpResponse>?

    @POST("/apis/user/sms")
    suspend fun postSmsToServer(@Body model: EncryptionProvider): Response<JsonObject>?

    @GET("/apis/user/sms")
    suspend fun getSyncedSmsFromServer(): Response<GetSmsResponse>?

    @PUT("/apis/auth/updateUser")
    suspend fun updateUserProfile(@Body model: EncryptionProvider): Response<UpdateProfileResponse>?

    @PUT("/apis/auth/updateUserV1")
    suspend fun updateUserPanDetails(@Body model: EncryptionProvider): Response<UpdateProfileResponse>?

    @POST("/apis/auth/signout")
    suspend fun logOut(): Response<VerifyOtpResponse>?

    @POST("/apis/crawling/emailcrawl/emailToken")
    suspend fun postUserGmailDetails(@Body model: EncryptionProvider): Response<UserGmailDetailsResponse>?

    /*  @GET("/apis/product/dashboardV1")
      suspend fun getDashBoardData(): Response<DashBoardResponse>?
  */

    @GET("/apis/admin/config/companyConstantV1")
    suspend fun getCompanyConstants(): Response<CompanyConstant>?

    @GET("/apis/user/dashboardV2")
    suspend fun getDashBoardData(): Response<DashBoardResponse?>?

    @POST("/apis/product/productv2/hideProduct")
    suspend fun hideProduct(@Body model: EncryptionProvider): Response<HideProductResponse>?

    @GET("/apis/admin/mparticleevents/mobileV1")
    suspend fun getLogEvents(): Response<LogEventResponse>?

    @GET("/apis/auth/v2/me")
    suspend fun getUserProfile(): Response<ProfileDetailsResponse>?

    @POST("/apis/auth/v2/accessToken")
    fun accessTokenCall(@Body model: EncryptionProvider): Call<TokenUpdateResponse>?

    @POST("/apis/auth/v2/accessToken")
    fun accessToken(@Body model: EncryptionProvider): Observable<TokenUpdateResponse>?

    @POST("/apis/auth/v2/validateAccessToken")
    suspend fun validateAccessToken(@Body model: EncryptionProvider): Response<GenerateOrderIdResponse>?

    @POST("/apis/product/cardv2/rpOrder")
    suspend fun getRazorpayOrderId(@Body model: EncryptionProvider): Response<GenerateOrderIdResponse>?

    @POST("/apis/product/cardv2/add")
    suspend fun postCardToServer(@Body model: EncryptionProvider): Response<AddCardResponseModel>?

    @GET("/apis/product/emandate/eMandateBanks")
    suspend fun getAllEmandateBanksList(): Response<BankListResponse>?

    @POST("/apis/product/emandate/eMandateOrder")
    suspend fun getRazorpayEmandateOrderId(@Body model: EncryptionProvider): Response<EmandateOrderResponse>?

    @POST("/apis/auth/findByDeviceId")
    suspend fun getExistingDeviceId(@Body model: EncryptionProvider): Response<GetDeviceIdResponse>?

    @POST("/apis/auth/loginbiometric")
    suspend fun getUserDetailsByMobileNo(@Body model: EncryptionProvider): Response<GetUserDetailsResponseModel>?

    @POST("/apis/product/emandate/addeMandate")
    suspend fun postEmandateConfirmation(@Body model: EncryptionProvider): Response<JsonObject>?

    @POST("/apis/product/card/autoPayToggle")
    suspend fun setAutopay(@Body model: EncryptionProvider): Response<JsonObject>?

    @GET("/apis/payment/getBankByIinV2/{number}/{cardType}")
    suspend fun fetchCardDetails(
        @Path("number") number: String, @Path("cardType") cardType: String
    ): Response<CardDetailsModel>?

    @GET("/apis/payment/getBankByIin/{number}/{cardType}")
    suspend fun fetchDebitCardDetails(
        @Path("number") number: String, @Path("cardType") cardType: String
    ): Response<DebitCardResponse>?



    @POST("/apis/payment/card/binCheckV2")
    suspend fun fetchCardType(@Body model: EncryptionProvider): Response<CardDetailTypeResponse>?


    @GET("/apis/payment/payments/rp-nb-methods")
    suspend fun fetchEnabledBanksList(): Response<PaymentBankListResponse>?







    @GET("/apis/payment/payments/paymentOptionsV2")
    suspend fun getPaymentOptions(): Response<PaymentOptionsResponse>?

    @GET("/apis/user/settings")
    suspend fun getUserSettingDetails(): Response<UserSettingResponse>?

    @GET("/apis/admin/feedback/getFeedback/email_delink")
    suspend fun getUserDelinkQuestion(): Response<GetEmailDelinkQuestionResponse>?

/*    @GET("/apis/payment/paymentsV3/getTxnList")
    suspend fun getTransactionDetails(): Response<TxnHistoryResponse>?*/

    @GET("/apis/payment/paymentsV3/getTxnListV2")
    suspend fun getTransactionHistory(): Response<DataHistory>?

    @POST("/apis/payment/paymentsV3/getTxnDetails")
    suspend fun getTransactionDetails(@Body txnDetail: EncryptionProvider): Response<DetailList>?

    @POST("/apis/user/email/delinkEmail")
    suspend fun delinkEmail(@Body delinkRequest: EncryptionProvider): Response<JsonObject>?

    @GET("/apis/admin/webpages")
    suspend fun getWebPages(): Response<WebPagesResponse>?

    @GET("/apis/product/loan/fetchLoansV3")
    suspend fun getLoanProvider(): Response<LoanProviderResponse>


    @GET("/apis/product/loan/getTopBillersV1")
    suspend fun getTopBillers(): Response<LoanBillersResponse?>


    @POST("/apis/product/loan/addLoanV3")
    suspend fun postLoanToServer(@Body model: EncryptionProvider): Response<ProductV2>?

    @GET("/apis/product/loan/bbpsPrefilledData/{productId}")
    suspend fun bbpsPrefilledData(@Path("productId") productId: String): Response<BBPSPREFILLED>?

    @GET("apis/user/reward/rewardDashboard")
    suspend fun getRewardDashboard(): Response<RewardsDashboardResponse>?

    /*@POST("/apis/payment/card/addV3")
    suspend fun addCard(@Body model: EncryptionProvider): Response<AddCardResponse>?*/


    @POST("/apis/payment/card/addV4")
    suspend fun addCard(@Body model: EncryptionProvider): Response<AddCardResponse>?


    @GET("apis/user/reward/vouchersListV1")
    suspend fun getVoucherListById(@Query("catId") catId: String): Response<VoucherListResponse>?

    @POST("apis/user/reward/redeemCoupons")
    suspend fun redeemCoupons(@Body model: EncryptionProvider): Response<RedeemCouponResponse>?

    @GET("apis/user/reward/userVouchersV1")
    suspend fun redeemVoucherHistory(): Response<VoucherRedeemHistoryResponse>?


    @POST("apis/payment/card/getProductById")
    suspend fun getProductById(@Body encryptionProvider: EncryptionProvider): Response<GetProductByIdResponse>?



    @POST("apis/payment/paymentsV3/startTxnV5")
    suspend fun startPaymentProcess(@Body encryptionProvider: EncryptionProvider): Response<ProcessTXNResponse>

//    @GET("apis/payment/paymentsV3/getTxnStatus/{txn_id}")
//    suspend fun getTXNStatusById(@Path("txn_id") txn_id: String): Response<GetTXNStatusResponse>?

    @POST("apis/payment/paymentsV3/getTxnStatusV2")
    suspend fun postTXNStatusById(@Body encryptionProvider: EncryptionProvider): Response<NewTranX>?

    @GET("apis/user/dashboard/credit/bureau-report")
    suspend fun creditDashBoard(): Response<CreditDashBoardResponse>? // done

    @GET("apis/user/dashboard/credit/bureau-products")
    suspend fun creditDashBoardProdcut(): Response<CreditDashBoardProductResponse>?


    @POST("/apis/auth/consent/otpConsent")
    suspend fun userConsentOTP(@Body model: EncryptionProvider): Response<GenerateLoginOtpResponseX>?

    @POST("/apis/payment/paymentsV3/verifyVPAV3")
    suspend fun verifyVPA(@Body encryptionProvider: EncryptionProvider): Response<DataRewardVPA3>?

    @POST("/apis/payment/paymentsV3/convertToCashV3")
    suspend fun startC2C(@Body encryptionProvider: EncryptionProvider): Response<ConvertToCashResponse>?

    @GET("/apis/payment/paymentsv3/getVPAList")
    suspend fun getVpaList(): Response<VpaListResponse>?

    @GET("apis/user/dashboard/home/widgets")
    suspend fun homeAndCreditDashBoard(): Response<HomeDashBoardResponse>?

    @GET("apis/user/waitlist/lockedChipsAndMessage/{cheqUserId}")
    suspend fun getLockedChips(@Path("cheqUserId") cheqUserId: String): Response<LockedChipResponse?>



    @POST("apis/crawling/smscrawl/finartTriggerLog")
    suspend fun finartTrigger(@Body body: EncryptionProvider): Response<FinartResponse>?

    @POST("apis/payment/payments/paymentDetails")
    suspend fun getBillPaymentDetails(@Body body: EncryptionProvider): Response<BillSummaryResponse>?

    @GET("/apis/user/rating/prompt")
    suspend fun getRatingPrompt(): Response<InAppDiscoveryResponse>?

    @POST
    suspend fun submitFeedback(@Url url: String, @Body request: EncryptionProvider): Response<FeedbackResponse>?
}