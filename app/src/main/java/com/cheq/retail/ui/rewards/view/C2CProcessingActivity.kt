package com.cheq.retail.ui.rewards.view

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.databinding.ActivityC2CprocessingBinding
import com.cheq.retail.extensions.customToast
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.rewards.model.ConvertToCashRequest
import com.cheq.retail.ui.rewards.model.ConvertToCashResponse
import com.cheq.retail.ui.rewards.repository.RewardsRepository
import com.cheq.retail.ui.rewards.util.State
import com.cheq.retail.ui.rewards.viewmodel.RewardsViewModelFactory
import com.cheq.retail.ui.rewards.viewmodel.RewardsViewModelNew
import com.cheq.retail.utils.IntentKeys.AMOUNT
import com.cheq.retail.utils.IntentKeys.COIN_WORTH
import com.cheq.retail.utils.IntentKeys.EARN_UP_TO
import com.cheq.retail.utils.IntentKeys.FAILED
import com.cheq.retail.utils.IntentKeys.TXN_STATUS
import com.cheq.retail.utils.IntentKeys.UPI_ID
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.Utils

class C2CProcessingActivity : BaseActivity() {
    lateinit var binding: ActivityC2CprocessingBinding
    private lateinit var rewardsViewModelNew: RewardsViewModelNew
    private lateinit var repository: RewardsRepository
    var coinWorth = 0
    var amount = ""
    var upiId = ""
    var earnUpTo = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_c2_cprocessing)
        catchIntent()
        setUpUi()
    }

    private fun catchIntent() {
        amount = intent.getStringExtra("AMOUNT").toString()
        upiId = intent.getStringExtra("UPI_ID").toString()
        coinWorth = intent.getIntExtra("COIN_WORTH", 0)
        earnUpTo = intent.getIntExtra("EARN_UP_TO", 0)
    }

    private fun setUpUi() {
        Utils.setLightStatusBar(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_c2_cprocessing)
        repository = RewardsRepository(this)
        rewardsViewModelNew = ViewModelProvider(
            this, RewardsViewModelFactory(repository)
        )[RewardsViewModelNew::class.java]
        binding.tvAmount.text = "Transferring ₹$amount to Your account"
        binding.animationOne.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                binding.animationOne.visibility = View.GONE
                binding.animationTwo.visibility = View.VISIBLE
                binding.animationTwo.playAnimation()
                binding.tvAmount.visibility = View.GONE
                binding.tvCaption.visibility = View.GONE
                rewardsViewModelNew.convertToCash(
                    ConvertToCashRequest(
                        amount.toInt(), upiId, coinWorth
                    )
                )
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationStart(animation: Animator) {

            }

        })




        rewardsViewModelNew.convertToCashLiveData.observe(this) {
            when (it) {
                is State.Error -> {
                    Utils.hideProgressDialog()
                  this.customToast(R.drawable.ic_information_new_orange,getString(R.string.c2c_error))
                    finish()

                }
                is State.Success -> {
                    Utils.hideProgressDialog()
                    if(it.data.txn_status.equals(FAILED)){
                        this.customToast(R.drawable.ic_information_new_orange,getString(R.string.c2c_error))
                        finish()
                    }
                    else doConvertToCash(it)

                }
                is State.Loading -> {
                    // Utils.showProgressDialog(this)
                }
                is State.NetworkError -> {
                    Utils.hideProgressDialog()
                    Toast.makeText(this, it.netWorkMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

        MparticleUtils.logCurrentScreen(
            "/rewards-tab/convert-to-cash/loading",
            "The customer views the loading screen for converting to cash after clicking credit to account",
            "",
            "",
            "",
            "",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.REWARD_TAB_CONVERT_TO_CASH_LOADiNG),
            this
        )
    }

    private fun doConvertToCash(success: State.Success<ConvertToCashResponse>) {
        if (success.data.status == true) {
            binding.animationOne.visibility = View.GONE
            binding.animationTwo.visibility = View.VISIBLE
            binding.animationTwo.playAnimation()
            binding.tvAmount.visibility = View.GONE
            binding.tvCaption.visibility = View.GONE
            startActivity(
                Intent(
                    this@C2CProcessingActivity, SuccessfulCreditedActivity::class.java
                ).putExtra(
                    AMOUNT, amount
                ).putExtra(UPI_ID, upiId.trim())
                    .putExtra(COIN_WORTH, coinWorth)
                    .putExtra(EARN_UP_TO, earnUpTo)
                    .putExtra(TXN_STATUS,success.data.txn_status)
            )
            finish()
        }
    }
}