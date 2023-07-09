package com.cheq.retail.ui.accountSettings

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.databinding.ActivityTermsPoliciesBinding
import com.cheq.retail.ui.accountSettings.viewmodel.AccountSettingsViewModel
import com.cheq.retail.ui.main.adapter.MenuItemAdapter
import com.cheq.retail.ui.main.model.SettingData
import com.cheq.retail.utils.Utils

class TermsPoliciesActivity : BaseActivity() {
    lateinit var mBinding: ActivityTermsPoliciesBinding
    private var accountSettingsViewModel: AccountSettingsViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUi()
    }


    private fun setUi() {
        Utils.setLightStatusBar(this)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_terms_policies)
        var listData = listOf(
            SettingData(1, getString(R.string.terms_and_conditions), "", R.drawable.ic_terms_cond,0),
            SettingData(2, getString(R.string.privacy_policy), "", R.drawable.ic_privacy_policy,0),

          //  SettingData(3, getString(R.string.disclaimer), "", R.drawable.ic_disclaimer,0),
          // SettingData(4, getString(R.string.about_cheq), "", R.drawable.ic_about_cheq,0),

            )
        mBinding.recyclerview.setHasFixedSize(true)
        mBinding.recyclerview.layoutManager = LinearLayoutManager(this)


        val _adaptor = MenuItemAdapter(
            this, listData, ::onItemClicked,false
        )

        mBinding.recyclerview.adapter = _adaptor
        mBinding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }



    private fun onItemClicked(id:Int){
        val intent  = Intent(this, TermsConditionActivity::class.java)
        intent.putExtra("id",id)
        startActivity(intent)
    }
}