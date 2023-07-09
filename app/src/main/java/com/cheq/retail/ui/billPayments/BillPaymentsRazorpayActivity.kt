package com.cheq.retail.ui.billPayments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.constants.Constant
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.ui.billPayments.viewModel.BillPaymentsViewModel
import com.cheq.retail.utils.Utils
import com.razorpay.PaymentResultListener
import com.razorpay.Razorpay
import org.json.JSONObject


class BillPaymentsRazorpayActivity : BaseActivity(), PaymentResultListener {
    private var razorpay: Razorpay? = null
    private var webview: WebView? = null
    private var payload: JSONObject? = null
    private var name: String? = null
    private var date: String? = null
    private var cvv: String? = null
    private var cardNo: String? = null
    private var viewModel: BillPaymentsViewModel? = null
    private var isSecure = true
    var network: String = ""
    var type: String = ""
    var subType: String = ""
    var issuerCode: String = ""
    var issuerName: String = ""
    var issuerLogo: String = ""
    var international: Boolean = false
    var orderId: String = ""
    var accountNo: String = ""
    var paymentMode: String = ""
    var ifsc: String = ""
    var customerId: String = ""
    var amount: String = ""
    var billId: String = ""
    var productId: String = ""
    var email: String = ""
    var bankLogo = ""
    var paymentModeLogo = ""
    var paymentModeName = ""
    var rpAmount = ""
    var paymentType = ""
    var debitCardHolderName = ""
    var debitCardExpiryMonth = ""
    var debitCardExpiryYear = ""
    var debitCardCvv = ""
    var debitCardNumber = ""
    var debitBankMasterId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        catchIntent()
        setupUI()
        setupViewModel()
        setupObserver()
        setupRazorpay()
        initiatePayment()
      //  MparticleUtils.logCurrentScreen(this::class.java.simpleName)
    }

    private fun setupUI() {
        setContentView(R.layout.activity_card_verification)
        webview = findViewById<View>(R.id.payment_webview) as WebView
       var  backButton = findViewById<View>(R.id.btnBack) as ImageView
        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun catchIntent() {

        name = intent.getStringExtra("ACCOUNT_HOLDER_NAME").toString()
        accountNo = intent.getStringExtra("ACCOUNT_NUMBER").toString()
        ifsc = intent.getStringExtra("ACCOUNT_IFSC").toString()
        paymentMode = intent.getStringExtra("PAYMENT_MODE").toString()

        date = intent.getStringExtra("DATE")
        cvv = intent.getStringExtra("CVV")
        cardNo = intent.getStringExtra("CARD_NO")

        network = intent.getStringExtra("NETWORK").toString()
        type = intent.getStringExtra("TYPE").toString()
        subType = intent.getStringExtra("SUB_TYPE").toString()
        issuerCode = intent.getStringExtra("ISSUER_CODE").toString()
        issuerName = intent.getStringExtra("ISSUER_NAME").toString()
        international = intent.getBooleanExtra("INTERNATIONAL", false)

        orderId = intent.getStringExtra("ORDER_ID").toString()
        customerId = intent.getStringExtra("CUSTOMER_ID").toString()
        amount = intent.getStringExtra("AMOUNT").toString()
        billId = intent.getStringExtra("BILL_ID").toString()
        productId = intent.getStringExtra("PRODUCT_ID").toString()
        email = intent.getStringExtra("EMAIL").toString()
        bankLogo = intent.getStringExtra("BANK_LOGO").toString()

        paymentModeName = intent.getStringExtra("PAYMENT_MODE_NAME").toString()
        paymentModeLogo = intent.getStringExtra("PAYMENT_MODE_LOGO").toString()
        rpAmount = intent.getStringExtra("RP_AMOUNT").toString()
        paymentType = intent.getStringExtra("PAYMENT_TYPE").toString()
        debitCardNumber = intent.getStringExtra("DEBIT_CARD_NUMBER").toString()
        debitCardHolderName = intent.getStringExtra("DEBIT_CARD_HOLDER_NAME").toString()
        debitCardExpiryMonth = intent.getStringExtra("DEBIT_CARD_EXPIRY_MONTH").toString()
        debitCardExpiryYear = intent.getStringExtra("DEBIT_CARD_EXPIRY_YEAR").toString()
        debitCardCvv = intent.getStringExtra("DEBIT_CARD_CVV").toString()
        isSecure = intent.getBooleanExtra("IS_SECURE", true)
        debitBankMasterId = intent.getStringExtra("DEBIT_BANK_MASTER_ID").toString()
        println("isSecure   $isSecure")
    }

    private fun setupRazorpay() {
        razorpay = Razorpay(this)
        razorpay!!.changeApiKey(Constant.RP_KEY)
        razorpay!!.setWebView(webview)

        println("paymentTypeeeeeee $paymentType")
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

    private fun initiatePayment() {

        when (paymentType) {
            "NET_BANKING" -> doPaymentWithNetBanking()
            "DEBIT_CARD" -> doPaymentWitDebitCard()
            "SAVED_CARD" -> doPayMentWithSafedCard()
            else -> {
                println(" error ...............  something went wrong payment type is null")
            }
        }

    }

    private fun doPayMentWithSafedCard() {
        try {
            payload = JSONObject("{currency: 'INR'}")
            payload!!.put("amount", rpAmount.toInt())
            payload!!.put("order_id", orderId)
            payload!!.put("contact", SharePrefs.instance?.getString(SharePrefsKeys.MOBILE_NUMBER))
            payload!!.put("email", email)
            payload!!.put("method", "card")
            payload!!.put("customer_id", customerId)
            payload!!.put("card[cvv]", debitCardCvv)
            payload!!.put("token", debitBankMasterId)
            println("RP JSON : " + payload.toString())
            sendRequest()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[BillPaymentsViewModel::class.java]

    }

    private fun sendRequest() {
        razorpay!!.submit(payload, this)
    }

    override fun onBackPressed() {
        razorpay!!.onBackPressed()
        super.onBackPressed()
    }

    override fun onPaymentSuccess(p0: String?) {
        setResult(
            RESULT_OK,
            Intent().putExtra("RP_PAYMENT", p0)
        )
        finish()
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        setResult(
            RESULT_CANCELED,
            Intent().putExtra("STATUS", RESULT_CANCELED)
        )
        finish()
        println("RP ERROR ++++++++ $p1")
    }

    private fun doPaymentWithNetBanking() {
        try {
            payload = JSONObject("{currency: 'INR'}")
            payload!!.put("amount", rpAmount.toInt())
            payload!!.put("order_id", orderId)
            payload!!.put("contact", SharePrefs.instance?.getString(SharePrefsKeys.MOBILE_NUMBER))
            payload!!.put("email", email)
            payload!!.put("method", "netbanking")
            payload!!.put("customer_id", customerId)
            payload!!.put("bank", ifsc)
            println("RP JSON : " + payload.toString())
            sendRequest()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun doPaymentWitDebitCard() {
        try {
            payload = JSONObject("{currency: 'INR'}")
            payload!!.put("save", isSecure)
            println("isSecure   $isSecure")
            payload!!.put("amount", rpAmount.toInt())
            payload!!.put("order_id", orderId)
            payload!!.put("contact", SharePrefs.instance?.getString(SharePrefsKeys.MOBILE_NUMBER))
            payload!!.put("email", email)
            payload!!.put("method", "card")
            payload!!.put("customer_id", customerId)
            payload!!.put("card[name]", debitCardHolderName)
            payload!!.put("card[number]", debitCardNumber)
            payload!!.put("card[expiry_month]", debitCardExpiryMonth)
            payload!!.put("card[expiry_year]", debitCardExpiryYear)
            payload!!.put("card[cvv]", debitCardCvv)
            println("RP JSON : " + payload.toString())

            sendRequest()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}