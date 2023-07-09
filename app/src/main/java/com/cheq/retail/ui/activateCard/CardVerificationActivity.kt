package com.cheq.retail.ui.activateCard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.constants.Constant
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.activateCard.viewModel.ProductActivationViewModel
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.Utils
import com.razorpay.CardsFlowCallback
import com.razorpay.PaymentResultListener
import com.razorpay.Razorpay
import com.razorpay.ValidationListener
import org.json.JSONObject


class CardVerificationActivity : BaseActivity(), PaymentResultListener {
    private var razorpay: Razorpay? = null
    private var webview: WebView? = null
    private var payload: JSONObject? = null
    private var name: String? = null
    private var date: String? = null
    private var cvv: String? = null
    private var cardNo: String? = null
    private var viewModel: ProductActivationViewModel? = null

    var network: String = ""
    var type: String = ""
    var subType: String = ""
    var issuerCode: String = ""
    var issuerName: String = ""
    var issuerLogo: String = ""
    var international: Boolean = false
    var orderId: String = ""
    var productId: String = "null"
    var email = ""
    var isExisting = false
    var bankMasterId = ""
    var logo = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        catchIntent()
        setupUI()
        setupViewModel()
        setupObserver()
        setupRazorpay()
        verifyCreditCard()
        MparticleUtils.logCurrentScreen("/cc-activation/verify-otp\n", "The customer enters the OTP to authorise the debit natively on the app", "", "", "", "", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Activation_Verify_Otp), this)
    }

    private fun setupUI() {
        Utils.setLightStatusBar(this)
        setContentView(R.layout.activity_card_verification)
        webview = findViewById<View>(R.id.payment_webview) as WebView
    }

    private fun catchIntent() {
        name = intent.getStringExtra("NAME")
        date = intent.getStringExtra("DATE")
        cvv = intent.getStringExtra("CVV")
        cardNo = intent.getStringExtra("CARD_NO")
        network = intent.getStringExtra("NETWORK").toString()
        type = intent.getStringExtra("TYPE").toString()
        subType = intent.getStringExtra("SUB_TYPE").toString()
        issuerCode = intent.getStringExtra("ISSUER_CODE").toString()
        issuerName = intent.getStringExtra("ISSUER_NAME").toString()
        international = intent.getBooleanExtra("INTERNATIONAL", false)
        isExisting = intent.getBooleanExtra("EXISTING", false)
        productId = intent.getStringExtra("productId").toString()
        bankMasterId = intent.getStringExtra("BANK_MASTER_ID").toString()
        logo = intent.getStringExtra("BANK_LOGO").toString()
        email = intent.getStringExtra("EMAIL").toString()
        orderId = intent.getStringExtra("ORDER_ID").toString()
    }

    private fun setupRazorpay() {
        razorpay = Razorpay(this)
        razorpay!!.changeApiKey(Constant.RP_KEY)
        razorpay!!.setWebView(webview)

    }


    private fun setupObserver() {
        viewModel!!.statusObserver.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        viewModel!!.progressObserver.observe(this) {
            if (it) {
                Utils.showProgressDialog(this)
            } else {
                Utils.hideProgressDialog()
            }
        }
    }

    private fun verifyCreditCard() {
        try {
            val index = date!!.indexOf('/')
            val month = date!!.substring(0, index)
            val year = date!!.substring(index + 1)
            payload = JSONObject("{currency: 'INR'}")
            payload!!.put("amount", "200")
            payload!!.put("order_id", orderId)
            payload!!.put("contact", SharePrefs.instance!!.getString(SharePrefsKeys.MOBILE_NUMBER))
            payload!!.put("email", email)
            payload!!.put("method", "card")
            payload!!.put("card[name]", name)
            payload!!.put("card[number]", cardNo)
            payload!!.put("card[expiry_month]", month)
            payload!!.put("card[expiry_year]", year)
            payload!!.put("card[cvv]", cvv)

            println("RAZORPAY ++++ " + payload.toString())

            sendRequest()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[ProductActivationViewModel::class.java]
    }

    private fun sendRequest() {
        razorpay!!.validateFields(payload, object : ValidationListener {
            override fun onValidationSuccess() {
                try {
                    razorpay!!.submit(payload, this@CardVerificationActivity)
                } catch (e: Exception) {
                }
            }

            override fun onValidationError(error: Map<String, String>) {

            }
        })
    }

    override fun onBackPressed() {
        razorpay!!.onBackPressed()
        MparticleUtils.logEvent("CC_Activation_OTP_Edit_BackClicked", "User goes back to edit the card details that were entered on the previous screen", "Unique", "Back", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Activation_OTP_Edit_BackClicked), this@CardVerificationActivity)
    }

    /* callback for permission requested from android */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (razorpay != null) {
            razorpay!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    /**
     * Is called if the payment was successful
     * You can now destroy the webview
     */
    override fun onPaymentSuccess(razorpayPaymentId: String) {
        MparticleUtils.logEvent("/cc-activation/success", "User chooses to click on the top nudge ", "Unique", "Continue", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Activation_Success_Nudge1), this@CardVerificationActivity)
        setResult(
            RESULT_OK,
            Intent().putExtra("RP_PAYMENT", razorpayPaymentId)
        )
        finish()
    }

    /**
     * Is called if the payment failed
     * possible values for code in this sdk are:
     * 2: in case of network error
     * 4: response parsing error
     * 5: This will contain meaningful message and can be shown to user
     * Format: {"error": {"description": "Expiry year should be greater than current year"}}
     */
    override fun onPaymentError(errorCode: Int, errorDescription: String) {
        setResult(
            RESULT_CANCELED,
            Intent().putExtra("STATUS", RESULT_CANCELED)
        )
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        razorpay!!.onActivityResult(requestCode, resultCode, data)
    }
}

class CallBack : CardsFlowCallback {
    override fun isNativeOtpEnabled(p0: Boolean) {

    }

    override fun otpGenerateResponse(p0: Boolean) {

    }

    override fun otpResendResponse(p0: Boolean) {

    }

    override fun onOtpSubmitError(p0: Boolean) {

    }

}