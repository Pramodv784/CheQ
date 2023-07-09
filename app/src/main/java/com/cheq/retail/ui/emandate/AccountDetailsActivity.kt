package com.cheq.retail.ui.emandate

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import com.bumptech.glide.Glide
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.databinding.ActivityAccountDetailsBinding
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.Utils

class AccountDetailsActivity : BaseActivity() {
    lateinit var bankName: String
    lateinit var bankIfsc: String
    lateinit var bankLogo: String
    lateinit var bankId: String
    lateinit var authList: ArrayList<String>

    var isIfscValid = ObservableBoolean(false)
    var isNameFilled = ObservableBoolean(false)
    var isAccountNoFilled = ObservableBoolean(false)
    var detailsValidated = ObservableBoolean(false)

    lateinit var binding: ActivityAccountDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        catchIntent()
        setUI()
        MparticleUtils.logCurrentScreen("/emandate-registration/enter-account-details", "The customer is expected to enter the account holder name, IFSC Code, and Bank Account Number", "bank-name", bankName, "", "", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.EMandate_Enter_Account_Detail), this)
    }

    private fun catchIntent() {
        bankIfsc = intent.getStringExtra("BANK_IFSC").toString()
        bankName = intent.getStringExtra("BANK_NAME").toString()
        bankLogo = intent.getStringExtra("BANK_LOGO").toString()
        bankId = intent.getStringExtra("BANK_ID").toString()
        authList = intent.getStringArrayListExtra("AUTH_LIST")!!

        println("AUTH ++++ " + authList)
    }

    @SuppressLint("SetTextI18n")
    private fun setUI() {
        Utils.setLightStatusBar(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_account_details)
        binding.activity = this
        binding.etName.onFocusChangeListener = onNameFocused()
        binding.etAccountNo.onFocusChangeListener = onAccountNoFocused()
        binding.etIfsc.onFocusChangeListener = onIfscFocused()

        Glide.with(this).load(bankLogo).into(binding.ivBankLogo)

        isNameFilled.set(true)
        binding.etName.setText(
            SharePrefs.instance!!.getString(SharePrefsKeys.FIRST_NAME)
                .toString() + " " + SharePrefs.instance!!.getString(SharePrefsKeys.LAST_NAME)
                .toString()
        )
    }

    fun onAccountNumber(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, i: Int, i1: Int, i2: Int) {
                if (s.toString().isNotEmpty() && s.toString().length > 8) {
                    isAccountNoFilled.set(true)
                } else {
                    isAccountNoFilled.set(false)
                }
                binding.tvError.visibility = View.GONE
                validateFields()
                MparticleUtils.logEvent("Emandate_BankAccountNumber_Entered", "User enters their bank account number", "Not Unique", "Input Field", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Emandate_BankAccountNumber_Entered), this@AccountDetailsActivity)
            }

            override fun afterTextChanged(editable: Editable) {

            }

        }
    }

    fun onIfsc(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.length == 7) {
//                    closeKeyBoard()
                    isIfscValid.set(true)
                } else {
                    isIfscValid.set(false)
                }
                validateFields()
                MparticleUtils.logEvent("Emandate_IFSC_Entered", "User enters the IFSC Code relevant to their bank account", "Not Unique", "Input Field", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Emandate_IFSC_Entered), this@AccountDetailsActivity)

            }

            override fun afterTextChanged(editable: Editable) {

            }

        }
    }

    fun validateFields() {
        detailsValidated.set(
            (isNameFilled.get().toString().trim().toBoolean() && isAccountNoFilled.get().toString()
                .trim().toBoolean() && isIfscValid.get())
        )
    }

    fun onName(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, i: Int, i1: Int, i2: Int) {
                MparticleUtils.logEvent("Emandate_AccountHolderName_Entered", "User edits or confirms the accountholder name for the bank account", "Not Unique", "Input Field", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Emandate_AccountHolderName_Entered), this@AccountDetailsActivity)
                isNameFilled.set(!binding.etName.text.isNullOrEmpty())
                validateFields()
            }

            override fun afterTextChanged(editable: Editable) {

            }

        }
    }

    private fun onNameFocused(): View.OnFocusChangeListener {
        return View.OnFocusChangeListener { _, hasFocused ->
            if (hasFocused) {
                binding.llName.setBackgroundResource(R.drawable.et_btm_bg)
            } else {
                binding.llName.setBackgroundResource(R.drawable.et_btm_bg_un_focused)
                if (binding.etName.text.length > 3) {
                    //  MparticleUtils.logEvent("AccountDetailsScreen", "Name entered")
                }
            }
        }
    }

    private fun onAccountNoFocused(): View.OnFocusChangeListener {
        return View.OnFocusChangeListener { _, hasFocused ->
            if (hasFocused) {
                binding.llAccountNo.setBackgroundResource(R.drawable.et_btm_bg)
            } else {
                if (binding.etAccountNo.text.toString()
                        .isNotEmpty() && binding.etAccountNo.text.toString().length < 9
                ) {
                    binding.tvError.visibility = View.VISIBLE
                } else {
                    binding.tvError.visibility = View.GONE
                    //  MparticleUtils.logEvent("AccountDetailsScreen", "Account number entered")
                }
                binding.llAccountNo.setBackgroundResource(R.drawable.et_btm_bg_un_focused)
            }
        }
    }

    private fun onIfscFocused(): View.OnFocusChangeListener {
        return View.OnFocusChangeListener { _, hasFocused ->
            if (hasFocused) {
                binding.llIfsc.setBackgroundResource(R.drawable.et_btm_bg)
            } else {
                binding.llIfsc.setBackgroundResource(R.drawable.et_btm_bg_un_focused)
                if (binding.etIfsc.text.length == 10) {
                    //  MparticleUtils.logEvent("AccountDetailsScreen", "Ifsc entered")
                }
            }
        }
    }

    fun confirmDetails() {
        MparticleUtils.logEvent("Emandate_BankAccountDetails_Clicked", "User submits the personal account details of the bank account to be linked", "Not Unique", "Continue", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Emandate_BankAccountDetails_Clicked), this)
        startActivity(
            Intent(this, EmandateVerificationMethodActivity::class.java)
                .putExtra("BANK_NAME", bankName)
                .putExtra("ACCOUNT_HOLDER_NAME", binding.etName.text.toString())
                .putExtra("ACCOUNT_NUMBER", binding.etAccountNo.text.toString())
                .putExtra("ACCOUNT_IFSC", bankIfsc + binding.etIfsc.text.toString())
                .putExtra("BANK_LOGO", bankLogo)
                .putExtra("BANK_ID", bankId)
                .putExtra("AUTH_LIST", authList)
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        MparticleUtils.logEvent("Emandate_BankAccountDetails_BackClicked", "User clicks the back CTA to go to the previous screen ", "Unique", "Back", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Emandate_BankAccountDetails_BackClicked), this)
    }
}