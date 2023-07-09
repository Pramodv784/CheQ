package com.cheq.retail.ui.rewards.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModelProvider
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.databinding.ActivitySelectAccountBinding
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.rewards.adapter.UPIAdapter
import com.cheq.retail.ui.rewards.model.DataRewardVPA3
import com.cheq.retail.ui.rewards.model.VerifyVPARequest
import com.cheq.retail.ui.rewards.model.VerifyVPAResponse
import com.cheq.retail.ui.rewards.model.VpaListResponse
import com.cheq.retail.ui.rewards.repository.RewardsRepository
import com.cheq.retail.ui.rewards.util.State
import com.cheq.retail.ui.rewards.viewmodel.RewardsViewModelFactory
import com.cheq.retail.ui.rewards.viewmodel.RewardsViewModelNew
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.Utils
import java.util.regex.Matcher
import java.util.regex.Pattern

class SelectAccountActivity : BaseActivity(), UPIAdapter.OnUpiClick {
    lateinit var binding: ActivitySelectAccountBinding
    private var isPhoneFocused = ObservableBoolean(false)
    var amount = ""
    private lateinit var rewardsViewModelNew: RewardsViewModelNew
    private lateinit var repository: RewardsRepository
    var coinWorth = 0
    var upiId = ""
    var earnUpTo = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        catchIntent()
        setUpUi()
    }

    private fun catchIntent() {
        amount = intent.getStringExtra("AMOUNT").toString()
        coinWorth = intent.getIntExtra("COIN_WORTH", 0)
        earnUpTo = intent.getIntExtra("EARN_UP_TO", 0)
    }

    private fun setUpUi() {
        Utils.setLightStatusBar(this)
        repository = RewardsRepository(this)
        rewardsViewModelNew = ViewModelProvider(
            this, RewardsViewModelFactory(repository)
        )[RewardsViewModelNew::class.java]

        binding.etUpiId.onFocusChangeListener = onMobileFocused()
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            overridePendingTransition(R.anim.no_animation, R.anim.fade_out_activity)
        }

        binding.tvAmount.text = "₹$amount"
        rewardsViewModelNew.getVpaList()
        binding.btnVerify.setOnClickListener {
            rewardsViewModelNew.verifyVPA(VerifyVPARequest(binding.etUpiId.text.toString().trim()))
            upiId = binding.etUpiId.text.toString()
        }

        binding.btnCredit.setOnClickListener {

            startActivity(
                Intent(this, C2CProcessingActivity::class.java).putExtra(
                    "AMOUNT", amount
                ).putExtra("UPI_ID", upiId).putExtra("COIN_WORTH", coinWorth)
                    .putExtra("EARN_UP_TO", earnUpTo)
            )
        }

        rewardsViewModelNew.verifyVPALiveData.observe(this) {
            when (it) {
                is State.Error -> {
                    Utils.hideProgressDialog()
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
                is State.Success -> {
                    Utils.hideProgressDialog()
                    doVerifyVPA(it)
                }
                is State.Loading -> {
                    Utils.showProgressDialog(this)
                }
                is State.NetworkError -> {
                    Utils.hideProgressDialog()
                    Toast.makeText(this, it.netWorkMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
        rewardsViewModelNew.vpaListLiveData.observe(this) {
            when (it) {
                is State.Error -> {
                    Utils.hideProgressDialog()
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
                is State.Success -> {
                    Utils.hideProgressDialog()
                    setVpaList(it)
                }
                is State.Loading -> {
                    Utils.showProgressDialog(this)
                }
                is State.NetworkError -> {
                    Utils.hideProgressDialog()
                    Toast.makeText(this, it.netWorkMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.etUpiId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.btnVerify.isEnabled = validateUPI(s.toString())
                binding.ivCheck.visibility = View.GONE
                binding.btnCredit.visibility = View.GONE
                binding.btnVerify.visibility = View.VISIBLE
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)


        MparticleUtils.logCurrentScreen(
            "/rewards-tab/convert-to-cash/enter-account",
            "The customer views the enter account screen for converting to cash",
            "",
            "",
            "",
            "",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.REWARD_TAB_CONVERT_TO_CASH_ACCOUNT),
            this
        )
    }

    private fun setVpaList(it: State.Success<VpaListResponse>) {

        if (it.data.upiList != null && it.data.upiList.isNotEmpty()) {
            binding.tvUpi.visibility = View.VISIBLE
            binding.rvUPIList.visibility = View.VISIBLE
            binding.rvUPIList.apply {
                adapter = UPIAdapter(
                    this@SelectAccountActivity, it.data.upiList, this@SelectAccountActivity
                )
                hasFixedSize()
            }
        } else {
            binding.tvUpi.visibility = View.GONE
            binding.rvUPIList.visibility = View.GONE
        }
    }

    private fun doVerifyVPA(it: State.Success<DataRewardVPA3>) {
        if (it.data?.accountExists.equals("NO")) {
            binding.ivCheck.visibility = View.GONE
            binding.btnVerify.text = "VERIFY"
            Toast.makeText(this, "Please enter valid UPI address", Toast.LENGTH_SHORT).show()
        } else {
            binding.ivCheck.visibility = View.VISIBLE
            binding.btnCredit.visibility = View.VISIBLE
            binding.btnVerify.visibility = View.GONE
        }
    }

    private fun onMobileFocused(): View.OnFocusChangeListener {
        return View.OnFocusChangeListener { _, hasFocused ->
            if (hasFocused) {
                isPhoneFocused.set(true)
                binding.llBtm.setBackgroundResource(R.drawable.et_btm_bg)
                binding.btnVerify.visibility = View.VISIBLE
            } else {
                binding.llBtm.setBackgroundResource(R.drawable.et_btm_bg_un_focused)
                isPhoneFocused.set(false)
                binding.btnVerify.visibility = View.GONE
            }
        }
    }

    fun validateUPI(upi: String?): Boolean {
        val validEmailRegex: Pattern =
            Pattern.compile("^(.+)@(.+)(.+)$", Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = validEmailRegex.matcher(upi.toString())
        return matcher.find()
    }

    override fun onUpiClick(upi: String) {
        upiId = upi
        binding.etUpiId.setText(upi)
        rewardsViewModelNew.verifyVPA(VerifyVPARequest(upi))
    }


    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }


}