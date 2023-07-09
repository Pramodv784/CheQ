package com.cheq.retail.ui.emandate

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.databinding.ActivitySelectBankBinding
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.emandate.adapter.BanksListAdapter
import com.cheq.retail.ui.emandate.model.BankListResponse
import com.cheq.retail.ui.emandate.viewModel.EmandateViewModel
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.Utils

class SelectBankActivity : BaseActivity(), BanksListAdapter.BankListAdapterInterface {
    lateinit var binding: ActivitySelectBankBinding
    lateinit var viewModel: EmandateViewModel
    lateinit var banksList: ArrayList<BankListResponse.DataEntity>
    lateinit var banksListWithoutTopSix: ArrayList<BankListResponse.DataEntity>
    lateinit var activity: SelectBankActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setupViewModel()
        setupObserver()
        MparticleUtils.logCurrentScreen("/emandate-registration/select-bank", "The customer is asked to select a bank account to be linked with CheQ for Autopay", "", "", "", "", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.EMandate_Select_bank), this)
    }

    private fun setupUI() {
        Utils.setLightStatusBar(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_bank)
        binding.activity = this
        activity = this
        binding.etSearch.onFocusChangeListener = onSearchFocused()
    }

    private fun onSearchFocused(): View.OnFocusChangeListener {
        return View.OnFocusChangeListener { _, hasFocused ->
            if (hasFocused) {
                binding.llSearch.setBackgroundResource(R.drawable.et_btm_bg)
            } else {
                binding.llSearch.setBackgroundResource(R.drawable.et_btm_bg_un_focused)
                // MparticleUtils.logEvent("SelectBankScreen", "Bank searched")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Utils.showProgressDialog(activity)
        viewModel.getBanksList()
    }

    private fun setupObserver() {
        viewModel.bankListObserver.observe(this) {
            if (it.banks.isNotEmpty()) {
                banksList = it.top6
                banksList.addAll(it.banks)
                banksListWithoutTopSix = it.banks
                binding.rvBankList.adapter = BanksListAdapter(this, banksList, this, true)
            }
            Utils.hideProgressDialog()
        }
    }

    fun search(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, i: Int, i1: Int, i2: Int) {
                MparticleUtils.logEvent("Emandate_Bank_Search_Entered", "User searches from amongst the available Bank options using the search option", "Unique", "Input Field", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Emandate_Bank_Search_Entered), this@SelectBankActivity)
                if (s.isEmpty()) {
                    binding.rvBankList.adapter =
                        BanksListAdapter(activity, banksList, activity, true)

                } else {
                    val tempList: ArrayList<BankListResponse.DataEntity> = ArrayList()
                    for (element in banksListWithoutTopSix) {
                        if (element.originalBankName.lowercase()
                                .contains(s.toString().lowercase())
                        ) {
                            tempList.add(element)
                        }
                    }

                    if (tempList.isNotEmpty()) {
                        binding.rvBankList.adapter =
                            BanksListAdapter(activity, tempList, activity, false)
                    }
                }
            }

            override fun afterTextChanged(editable: Editable) {

            }

        }
    }

    fun setupViewModel() {
        viewModel = ViewModelProvider(this)[EmandateViewModel::class.java]
    }

    override fun onBankSelected(item: BankListResponse.DataEntity) {
        MparticleUtils.logEvent("Emandate_Bank_Clicked", "User selects their bank for e-mandate registration", "Not Unique", "Continue", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Emandate_Bank_Clicked), this)
        startActivity(
            Intent(this, AccountDetailsActivity::class.java).putExtra(
                "BANK_NAME",
                item.originalBankName
            ).putExtra("BANK_IFSC", item.IFSC_Prefix)
                .putExtra("BANK_LOGO", item.logo)
                .putExtra("AUTH_LIST", item.auth_types)
                .putExtra("BANK_ID", item._id)
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        MparticleUtils.logEvent("Emandate_Bank_BackClicked", "User clicks the back CTA to go to the previous screen ", "Unique", "Back", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Emandate_Bank_BackClicked), this)
    }
}