package com.cheq.retail.ui.accountSettings

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.DataBindingUtil
import com.cheq.retail.R
import com.cheq.retail.application.MainApplication
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.databinding.ActivityManageCommunicationsBinding
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.ui.main.fragment.AppMenuActivity
import com.cheq.retail.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar

class ManageCommunicationActivity : BaseActivity() {
    private var mobileNUmber = ""
    private var emailId = ""
    lateinit var mBinding: ActivityManageCommunicationsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        catchIntent()
        setUi()
    }


    private fun setUi() {
        Utils.setLightStatusBar(this)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_manage_communications)
        mBinding.ivBack.setOnClickListener {
            onBackPressed()
        }
        mBinding.tvSubWhatsApp.text = "+91$mobileNUmber"
        mBinding.txtEmail.text = emailId

        mBinding.ivEmailEdit.setOnClickListener {
            showBottomSheetDialog()


        }
        mBinding.whatsAppSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                val snackbar = Snackbar.make(mBinding.parentLayout, "", Snackbar.LENGTH_SHORT)
                val customSnackView: View =
                    layoutInflater.inflate(R.layout.custom_toast, null)
                val txtMessage = customSnackView.findViewById<TextView>(R.id.txtMessage)
                txtMessage.text = getString(R.string.whtsapp_comm_disabled)
                snackbar.view.setBackgroundColor(Color.TRANSPARENT)
                val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
                snackbarLayout.setPadding(0, 0, 0, 0)
                snackbarLayout.addView(customSnackView, 0)
                snackbar.show()
            }
        }
    }
    private fun catchIntent() {

        this.intent?.apply {
            mobileNUmber = getStringExtra(AppMenuActivity.MOBILE_NUMBER).toString()
            emailId = getStringExtra(AppMenuActivity.EMAIL_ID).toString()
        }

    }
    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_edit_email)
        val edtEmail = bottomSheetDialog.findViewById<AppCompatEditText>(R.id.edtEmail)
        val imgClose = bottomSheetDialog.findViewById<AppCompatImageView>(R.id.ivClose)
        imgClose?.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        val email = SharePrefs.getInstance(MainApplication.getApplicationContext())?.getString(SharePrefsKeys.USER_EMAIL)
        edtEmail?.setText(email)
        bottomSheetDialog.show()
    }
}