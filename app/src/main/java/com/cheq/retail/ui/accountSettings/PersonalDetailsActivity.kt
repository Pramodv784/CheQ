package com.cheq.retail.ui.accountSettings

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.cheq.retail.R
import com.cheq.retail.api.RestClient
import com.cheq.retail.application.MainApplication
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.databinding.ActivityPersonalDetailsBinding
import com.cheq.retail.sharePreferences.SharePrefCheqUserId
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.ui.main.fragment.AppMenuActivity
import com.cheq.retail.utils.Utils
import com.cheq.retail.utils.navigateToParent
import com.google.android.material.bottomsheet.BottomSheetDialog


class PersonalDetailsActivity : BaseActivity() {
    lateinit var mBinding: ActivityPersonalDetailsBinding
    private var firstName = ""
    private var lastName = ""
    private var fullName = ""
    private var mobileNUmber = ""
    private var emailId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.cheq.retail.R.layout.activity_personal_details)
        catChIntent()
        setUpUi()

        getDataFromServer()
        // MparticleUtils.logCurrentScreen(this::class.java.simpleName)
    }

    private fun catChIntent() {

        this.intent?.apply {
            firstName = getStringExtra(AppMenuActivity.FIRST_NAME).toString()
            lastName = getStringExtra(AppMenuActivity.LAST_NAME).toString()
            fullName = getStringExtra(AppMenuActivity.FULL_NAME).toString()
            mobileNUmber = getStringExtra(AppMenuActivity.MOBILE_NUMBER).toString()
            emailId = getStringExtra(AppMenuActivity.EMAIL_ID).toString()
        }

    }

    private fun getDataFromServer() {
        lifecycleScope.launchWhenStarted {
            try {
                val response = RestClient.getInstance().service.getUserSettingDetails()
                if ( response?.body() != null) {
                    val data = response.body()
                    firstName = data?.firstName ?: ""
                    lastName = data?.lastName ?: ""
                    fullName = listOfNotNull(data?.firstName, data?.lastName).joinToString(" ")
                    mobileNUmber = data?.mobile ?: ""
                    emailId = data?.email ?: ""

                    setUpUi()
                }
            }catch(e: Exception) {
                // do nothing
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpUi() {
        Utils.setLightStatusBar(this)
        mBinding = DataBindingUtil.setContentView(this, com.cheq.retail.R.layout.activity_personal_details)

        mBinding.ivBack.setOnClickListener {
            onBackPressed()
        }
        if (!fullName.isNullOrEmpty()) {
            mBinding.llFullName.visibility = View.VISIBLE
            mBinding.tvFullName.text = fullName
        } else {
            mBinding.llFullName.visibility = View.GONE
        }

        if (!mobileNUmber.isNullOrEmpty()) {
            mBinding.llMobileNumber.visibility = View.VISIBLE
            mBinding.tvMobileNumber.text = "+91 $mobileNUmber"
        } else {
            mBinding.llMobileNumber.visibility = View.GONE
        }

        if (!emailId.isNullOrEmpty()) {
            mBinding.llEMail.visibility = View.VISIBLE
            mBinding.tvEmail.text = emailId
        } else {
            mBinding.llEMail.visibility = View.GONE
        }

        mBinding?.txtNameInitials?.text = getInitialsFnLn(firstName, lastName)
    }

    private fun getInitialsFnLn(firstName: String, lastName: String): String? {
        return ((if (Utils.isValidData(firstName)) firstName[0].toString() else "")
                + if (Utils.isValidData(lastName)) lastName[0].toString() else "")
    }

    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_delete_acnt)
        val btnDelete = bottomSheetDialog.findViewById<AppCompatButton>(R.id.btnDelete)
        val btnDontDelete = bottomSheetDialog.findViewById<AppCompatButton>(R.id.btnDontDelete)

        btnDelete?.setOnClickListener {
            composeEmail(arrayOf(getString(R.string.support_cheq_email)), getString(R.string.request_to_delete_acnt))
        }
        btnDontDelete?.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    fun deleteAccount(view: View) {
        showBottomSheetDialog()
    }

    val body :String = "Please do not edit anything below this text, and please proceed to send the mail on confirmation\n" +
            "\n" +
            "\n" +
            "\n" +
            "User id: ${SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())?.getString(SharePrefsKeys.CHEQ_USER_ID)}"+
            "\n" +
            "Request: delete my account on CheQ\n" +
            "\n" +
            "Note: This may take upto 5-7 working days, and the deletion will be confirmed on mail once done."

    private fun composeEmail(addresses: Array<String?>?, subject: String?) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:") // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, body)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigateToParent()
    }
}