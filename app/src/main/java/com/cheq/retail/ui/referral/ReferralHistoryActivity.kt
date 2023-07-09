package com.cheq.retail.ui.referral

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.SimpleItemAnimator
import com.cheq.retail.R
import com.cheq.retail.application.MainApplication
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.databinding.ActivityReferHistoryBinding
import com.cheq.retail.sharePreferences.SharePrefCheqUserId
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.ui.main.model.HistoryItem
import com.cheq.retail.ui.referral.adapter.RefHistoryAdapter
import com.cheq.retail.ui.referral.viewmodel.ReferralHistoryViewModel
import com.cheq.retail.utils.NetworkResult
import com.cheq.retail.utils.Utils


class ReferralHistoryActivity : BaseActivity() {
    lateinit var binding: ActivityReferHistoryBinding
    lateinit var viewModel: ReferralHistoryViewModel
    private val referredHistoryList = mutableListOf<HistoryItem?>()
    val cheqUserId = SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
        ?.getString(SharePrefsKeys.CHEQ_USER_ID)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setLightStatusBar(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_refer_history)

        (binding.rvHistory.getItemAnimator() as SimpleItemAnimator).supportsChangeAnimations = true
        binding.rvHistory.hasFixedSize()

        val repository = (application as MainApplication).referralRepository

        viewModel = ViewModelProvider(this, ReferralHistoryViewModel.HistoryVMFactory(repository, cheqUserId))[ReferralHistoryViewModel::class.java]

        viewModel.refHistory.observe(this) {
            when (it) {
                is NetworkResult.Loading -> {
                    Utils.showProgressDialog(this)
                }
                is NetworkResult.Success -> {
                    it.data?.let { it1 ->
                        referredHistoryList.clear()
                        if (it1.history != null)
                            referredHistoryList.addAll(it1.history)

                        if (binding.rvHistory.adapter == null) {
                            binding.rvHistory.adapter = RefHistoryAdapter(
                                this,
                                referredHistoryList
                            )
                        } else {
                            binding.rvHistory.adapter!!.notifyDataSetChanged()
                        }
                    }
                    Utils.hideProgressDialog()
                }
                is NetworkResult.Error -> {
                    Utils.hideProgressDialog()
                   // showToast(it.message.toString())
                }
            }


        }
    }

    fun onBackPress(view: View) {
        finish()
    }

}