package com.cheq.retail.ui.login

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModelProvider
import com.cheq.retail.R
import com.cheq.retail.api.errormodel.BlockData
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.constants.AFConstants
import com.cheq.retail.databinding.ActivityGetPanCardBinding
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.blocker_screen.BlockerActivity
import com.cheq.retail.ui.login.viewModel.GetPanCardViewModel
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.Utils
import java.text.SimpleDateFormat
import java.util.*

class GetPanCardActivity : BaseActivity() {
    lateinit var binding: ActivityGetPanCardBinding
    lateinit var activity: GetPanCardActivity

    private lateinit var dob: String
    private lateinit var viewModel: GetPanCardViewModel
    var isPanValid = ObservableBoolean(false)
    var isDateValid = ObservableBoolean(false)
    private var pan: Boolean = false
    private var date: Boolean = false
    var isPanFocused = ObservableBoolean(false)
    var isAllFieldValid = ObservableBoolean(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setupViewModel()
        setupObserver()
        MparticleUtils.logCurrentScreen(
            "/onboarding/pan-and-dob",
            "The customer is asked to input additional details - PAN and DOB, if the FName, LName, Mobile No. do not result in successful bureau fetch\n",
            "",
            "",
            "",
            "",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.PERSONAL_DETAILS_ADDITIONAL),
            this
        )
    }

    private fun setupUI() {
        Utils.setLightStatusBar(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_get_pan_card)
        binding.activity = this
        activity = this

        binding.etPan.onFocusChangeListener = onEtFocused()
        binding.etPan.requestFocus()

    }

    private fun onEtFocused(): View.OnFocusChangeListener {
        return View.OnFocusChangeListener { _, hasFocused ->
            if (hasFocused) {

                isPanFocused.set(true)
                binding.llBtmOne.setBackgroundResource(R.drawable.et_btm_bg)
                binding.llBtmTwo.setBackgroundResource(R.drawable.et_btm_bg_un_focused)
            } else {
                binding.llBtmOne.setBackgroundResource(R.drawable.et_btm_bg_un_focused)
                isPanFocused.set(false)

                if (binding.etPan.text.toString().length < 10) {
                    binding.tvError.visibility = View.VISIBLE
                }

//                if (binding.etPan.text.toString().length == 10) {
//
//                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun onDatePick() {
        binding.llBtmOne.setBackgroundResource(R.drawable.et_btm_bg_un_focused)
        binding.llBtmTwo.setBackgroundResource(R.drawable.et_btm_bg)
        binding.etPan.clearFocus()
        val myCalendar = Calendar.getInstance()
        myCalendar.add(Calendar.YEAR, -18)
        val date = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, day)

            dob = SimpleDateFormat("dd MMM yyyy").format(myCalendar.time)
            binding.tvDob.text = dob

            date = true
            isDateValid.set(dob != "")
            isAllFieldValid.set(isAllValid())
            MparticleUtils.logEvent(
                "Onboarding_PersonalDetails_DOB_Entered",
                "User enters the DOB as per PAN",
                "Input Field",
                "Input Field",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_PERSONAL_DETAIL_DOB_ENTERED),
                this
            )
        }

        val dialog = DatePickerDialog(
            this, date,
            myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH),
        )
        val myCalendar2 = Calendar.getInstance()
        myCalendar2.add(Calendar.YEAR, -100)
        dialog.datePicker.maxDate = myCalendar.timeInMillis
        dialog.datePicker.minDate = myCalendar2.timeInMillis

        dialog.show()
    }

    private fun setupObserver() {
        viewModel.errorBlockObserver.observe(this) { blockdata ->
            showErrorBlocker(blockdata)
        }

        viewModel.responseObserver.observe(this) {
            MparticleUtils.logEvent(
                "Onboarding_PersonalDetails_Additional_Clicked",
                "User confirms the PAN and DOB details by clicking the CTA",
                "Unique",
                "Continue",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_PERSONAL_DETAIL_ADDITIONAL_CLICKED),
                this
            )

        }

        viewModel.statusObserver.observe(this) {
            // Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        viewModel.progressObserver.observe(this) {
            if (it) {
                //  Utils.showProgressDialog(this)
                binding.flLottieIndicator.visibility = View.VISIBLE
                binding.button.visibility = View.GONE
                Utils.getUserClickBehaviour(false, this)
            } else {
                //  Utils.hideProgressDialog()
                binding.flLottieIndicator.visibility = View.GONE
                binding.button.visibility = View.VISIBLE
                Utils.getUserClickBehaviour(true, this)
            }
        }

    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[GetPanCardViewModel::class.java]

    }

    fun validatePanNumber(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence, i: Int, i1: Int, i2: Int
            ) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                if (!Utils.isPanNoCodeValid(charSequence.toString()) && charSequence.toString().length == 10) {
                    binding.tvError.visibility = View.VISIBLE
                } else if (Utils.isPanNoCodeValid(charSequence.toString())) {
                    binding.tvError.visibility = View.GONE
                    isPanValid.set(true)
                    pan = true
                    closeKeyBoard()
                    binding.etPan.clearFocus()
                    isAllFieldValid.set(isAllValid())
                    MparticleUtils.logEvent(
                        "Onboarding_PersonalDetails_PAN_Entered",
                        "User enters the ten-digit PAN",
                        "Unique",
                        "Input Field",
                        SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_PERSONAL_DETAIL_PAN_ENTERED),
                        this@GetPanCardActivity
                    )
                } else {
                    pan = false
                    isPanValid.set(false)
                    isAllFieldValid.set(isAllValid())

                }
            }

            override fun afterTextChanged(editable: Editable) {

            }

        }
    }

    private fun closeKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun onContinue() {
        MparticleUtils.logEvent(
            "Onboarding_PANandDOB_Clicked",
            "User confirms the PAN and DOB details by clicking the CTA",
            "Unique",
            "Continue",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Onboarding_PANandDOB_Clicked),
            this@GetPanCardActivity
        )

        // viewModel.updateProfile(dob, binding.etPan.text.toString().trim())
        isAllFieldValid.set(false)
        startActivity(
            Intent(this, LoadingStateActivity::class.java).putExtra(
                getString(R.string.pan), binding.etPan.text.toString().trim()
            ).putExtra(getString(R.string.DOB), dob).putExtra(getString(R.string.coming_from), getString(R.string.pancard_activity))
        )
      // finish()
    }

    fun isAllValid(): Boolean {
        return pan && date
    }
    private fun showErrorBlocker(blockedData: BlockData?) {
        val intent = Intent(this, BlockerActivity::class.java).apply {
            putExtra(BlockerActivity.EXTRA_BLOCKER, blockedData)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(AFConstants.KEY_SCREEN_NAME, AFConstants.SCREEN_ONBOARDING_BLOCKED)
        }
        startActivity(intent)
    }
}