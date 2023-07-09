package com.cheq.retail.ui.emandate

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
import com.cheq.retail.ui.emandate.viewModel.EmandateViewModel
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.Utils
import com.razorpay.PaymentResultListener
import com.razorpay.Razorpay
import org.json.JSONObject


class EmandateVerificationActivity : BaseActivity(), PaymentResultListener {
    private var razorpay: Razorpay? = null
    private var webview: WebView? = null
    private var payload: JSONObject? = null
    private var name: String? = null
    private var date: String? = null
    private var cvv: String? = null
    private var cardNo: String? = null
    private var viewModel: EmandateViewModel? = null

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
    var email: String = ""
    var bankName: String = ""
    var bankLogo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        catchIntent()
        setupUI()
        setupViewModel()
        setupObserver()
        setupRazorpay()
        verifyEmandate()
        // MparticleUtils.logCurrentScreen(this::class.java.simpleName)

        MparticleUtils.logCurrentScreen("/emandate-registration/pending", "The customer authenticates the bank account within the Razorpay webview and awaits the terminal status\n", "type", "netbanking", "", "", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.EMandate_Register_Pending), this)
    }

    private fun setupUI() {
        setContentView(R.layout.activity_card_verification)
        webview = findViewById<View>(R.id.payment_webview) as WebView
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
        bankName = intent.getStringExtra("BANK_NAME").toString()
        bankLogo = intent.getStringExtra("BANK_LOGO").toString()
        orderId = intent.getStringExtra("ORDER_ID").toString()
        customerId = intent.getStringExtra("CUSTOMER_ID").toString()
        email = intent.getStringExtra("EMAIL").toString()
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

    private fun verifyEmandate() {
        try {
            payload = JSONObject("{currency: 'INR'}")
            payload!!.put("amount", 0)
            payload!!.put("order_id", orderId)
            payload!!.put("contact", SharePrefs.instance?.getString(SharePrefsKeys.MOBILE_NUMBER))
            payload!!.put("email", email)
            payload!!.put("method", "emandate")
            payload!!.put("customer_id", customerId)
            payload!!.put("recurring", 1)
            payload!!.put("bank", "SBIN")
            payload!!.put("auth_type", paymentMode)
            payload!!.put("bank_account[name]", name)
            payload!!.put("bank_account[account_number]", accountNo)
            payload!!.put("bank_account[account_type]", "savings")
            payload!!.put("bank_account[ifsc]", ifsc)
            println("RP JSON : " + payload.toString())

            sendRequest()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[EmandateViewModel::class.java]
    }

    private fun sendRequest() {
        razorpay!!.submit(payload, this)
    }

    override fun onBackPressed() {
        razorpay!!.onBackPressed()
        super.onBackPressed()
    }

    override fun onPaymentSuccess(p0: String?) {
        MparticleUtils.logCurrentScreen("/emandate-registration/success", "The customer views the success status of the e-mandate registration\n", "type", "netbanking", "", "", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.EMandate_Register_Success), this)
        setResult(
            RESULT_OK,
            Intent().putExtra("RP_PAYMENT", p0)
        )
        finish()
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        print("Emandate error $p1")
        MparticleUtils.logCurrentScreen("/emandate-registration/failed", "The customer views the failed status of e-mandate registration", "error-code", "" + p1, "", "", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.EMandate_Registration_Failed), this)
        setResult(
            RESULT_CANCELED,
            Intent().putExtra("STATUS", RESULT_CANCELED)
        )
        finish()
    }
}