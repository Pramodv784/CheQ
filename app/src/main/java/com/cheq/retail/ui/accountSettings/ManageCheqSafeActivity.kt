package com.cheq.retail.ui.accountSettings

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.cheq.retail.R
import com.cheq.retail.application.MainApplication
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.databinding.ActivityManageCheqSafeBinding
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.ui.accountSettings.model.ActiveEmailsItem
import com.cheq.retail.ui.accountSettings.model.EmailDelinkQuestionRequest
import com.cheq.retail.ui.accountSettings.viewmodel.AccountSettingsViewModel
import com.cheq.retail.ui.accountSettings.webView.adapter.EmailCheqSafeAdapter
import com.cheq.retail.ui.accountSettings.webView.adapter.LinkedEmailAdapter
import com.cheq.retail.utils.Utils

class ManageCheqSafeActivity : BaseActivity(), LinkedEmailAdapter.LinkedEmailClickedListener{
    lateinit var binding: ActivityManageCheqSafeBinding
    private var linkedEMailList: ArrayList<ActiveEmailsItem> = ArrayList()
    private var accountSettingsViewModel: AccountSettingsViewModel? = null
    private var emailCheqSafeAdapter: EmailCheqSafeAdapter? = null
    private var globalAnswerId = ""
    private var globalQuestionId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
        setUpUi()
        setUpObserver()
        catchIntent()
        accountSettingsViewModel!!.getUserDelinkQuestion()
       // MparticleUtils.logCurrentScreen(this::class.java.simpleName)
    }

    private fun setUpObserver() {
        accountSettingsViewModel!!.userDelinkResponseObserver.observe(this) {
            if (it != null) {

               /* emailCheqSafeAdapter = EmailCheqSafeAdapter(
                    this,
                    listOf(it.data!!), this
                )*/

            }
        }

        accountSettingsViewModel!!.delinkResponseObserver.observe(this) {
            if (it) {
                Toast.makeText(this, "Your email is successfully delinked", Toast.LENGTH_SHORT)
                    .show()
                onBackPressed()
            } else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        accountSettingsViewModel!!.delinkStatusObserver.observe(this) {
            if (it) {
                Utils.showProgressDialog(this)
            } else {
                Utils.hideProgressDialog()
            }
        }
    }

    private fun setUpViewModel() {
        accountSettingsViewModel = ViewModelProvider(this)[AccountSettingsViewModel::class.java]
    }

    private fun catchIntent() {
        linkedEMailList = intent.getParcelableArrayListExtra("EMAIL_LIST")!!
        println("linkedEMailListSize ${linkedEMailList.size}")
        val linkedEmailAdapter = LinkedEmailAdapter(this, linkedEMailList, this)
        binding.rvCheqSafeEmail.apply {
            adapter = linkedEmailAdapter
            hasFixedSize()
        }
    }

    private fun setUpUi() {
        Utils.setLightStatusBar(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_cheq_safe)
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnLinkEmail.setOnClickListener {
            onBackPressed()
            SharePrefs.getInstance(MainApplication.getApplicationContext())!!
                .putBoolean(SharePrefsKeys.IS_LINK_EMAIL_CLICKED, true)
        }
    }

    override fun onDelinkedClicked(dataItem: ActiveEmailsItem) {
        val bottomSheetDialogDeelinkConfirmation =
            Utils.showCustomDialogBottum(this, R.layout.bottom_sheet_confirmation_de_link_email)

        bottomSheetDialogDeelinkConfirmation.setCancelable(false)

        val btnCancel =
            bottomSheetDialogDeelinkConfirmation.findViewById<AppCompatButton>(R.id.btnCancel)
        val btnDelink =
            bottomSheetDialogDeelinkConfirmation.findViewById<AppCompatButton>(R.id.btnDelink)


        btnCancel?.setOnClickListener {
            bottomSheetDialogDeelinkConfirmation.dismiss()
        }

        btnDelink?.setOnClickListener {
            openDelinkDialog(dataItem.email, dataItem.id)
            bottomSheetDialogDeelinkConfirmation.dismiss()
        }

        bottomSheetDialogDeelinkConfirmation.show()
    }

    private fun openDelinkDialog(email: String?, token: String?) {
        val bottomSheetDialogDelink =
            Utils.showCustomDialogBottum(this, R.layout.bottom_sheet_de_link_email)
        bottomSheetDialogDelink.setCancelable(false)
        val btnCancel =
            bottomSheetDialogDelink.findViewById<AppCompatButton>(R.id.btnCancel)
        val btnDelink =
            bottomSheetDialogDelink.findViewById<AppCompatButton>(R.id.btnDelink)
        btnCancel?.setOnClickListener {
            bottomSheetDialogDelink.dismiss()
        }
        val rvDelinkQuestion =
            bottomSheetDialogDelink.findViewById<RecyclerView>(R.id.rvDelinkQuestion)
        rvDelinkQuestion?.apply {
            adapter = emailCheqSafeAdapter
            hasFixedSize()
        }
        btnDelink?.setOnClickListener {
            accountSettingsViewModel!!.deLinkEmail(
                EmailDelinkQuestionRequest(
                    globalAnswerId,
                    globalQuestionId,
                    token,
                    email
                )
            )
            bottomSheetDialogDelink.dismiss()
        }
        bottomSheetDialogDelink.show()

    }


}