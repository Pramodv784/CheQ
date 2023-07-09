package com.cheq.retail.ui.verifyOtp

import android.annotation.SuppressLint
import android.content.*
import android.graphics.Color
import android.os.*
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.cheq.retail.R
import com.cheq.retail.api.errormodel.BlockData
import com.cheq.retail.application.MainApplication
import com.cheq.retail.application.MainApplication.Companion.finartProcessInstance
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.constants.AFConstants
import com.cheq.retail.constants.AFConstants.AF_CUST_COMPLETE_REGIST
import com.cheq.retail.constants.AFConstants.AF_PERSONAL_DETAIL
import com.cheq.retail.constants.AFConstants.AF_VERIFY_OTP
import com.cheq.retail.constants.AFConstants.CHEQ_USER
import com.cheq.retail.constants.AFConstants.FBEvent_PERSONAL_DETAIL
import com.cheq.retail.constants.AFConstants.FBEvent_VERIFY_OTP
import com.cheq.retail.constants.AFConstants.FB_CUST_COMPLETE_REGIST
import com.cheq.retail.constants.AFConstants.FINART_INVOKE
import com.cheq.retail.constants.AFConstants.FINART_SUCCESS
import com.cheq.retail.constants.AFConstants.FINART_SUCCESS_OTP
import com.cheq.retail.constants.AFConstants.FINART_TRIGGER_OTP
import com.cheq.retail.constants.AFConstants.SCREEN_OTP
import com.cheq.retail.constants.AppsFlyEvents
import com.cheq.retail.constants.FirebaseLog
import com.cheq.retail.databinding.ActivityVerifyOtpBinding
import com.cheq.retail.databinding.OtpTimeoutBottomSheetLayoutBinding
import com.cheq.retail.sharePreferences.*
import com.cheq.retail.ui.blocker_screen.BlockerActivity
import com.cheq.retail.ui.login.LoginActivity
import com.cheq.retail.ui.login.model.RequestOTPConsent
import com.cheq.retail.ui.splash.viewModel.SplashViewModel
import com.cheq.retail.ui.verifyOtp.viewModel.VerifyOtpViewModel
import com.cheq.retail.utils.*
import com.cheq.retail.utils.SMSListner.Companion.bindListener
import com.cheq.retail.utils.SMSListner.Companion.unbindListener
import com.freshchat.consumer.sdk.Freshchat
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import com.smsapi.ProcessSMS
import com.smsapi.ResponseListener
import java.util.regex.Matcher
import java.util.regex.Pattern


class VerifyOtpActivity : BaseActivity(), ResponseListener {
    lateinit var activity: VerifyOtpActivity
    private val REQ_USER_CONSENT = 200
    lateinit var binding: ActivityVerifyOtpBinding
    var smsBroadcastReceiver: SMSBroadcastReceiver? = null
    lateinit var mobile: String
    private lateinit var comeFrom: String
    var canSignup = ObservableBoolean(false)
    var canRequestOTPonCall = ObservableBoolean(true)
    var isTimerOn = true
    private lateinit var viewModel: VerifyOtpViewModel
    private var counter = 1
    private var counterCall = 1
    private var wrongOTPCount = 0
    private lateinit var splashViewModel: SplashViewModel
    var fcmToken: String = ""
    var userType = ""

    val otpActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK && it.data != null) {
                val message = it.data?.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                getOtpFromMessage(message)
            }
        }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainApplication.finartLogout()
        catchIntent()
        setupUI()
        setupViewModel()
        setupObserver()
        startTimer(31000)
        startTimeout()
        generateFcmToken()
        updateThirdPartyUser()
        //startSmartUserConsent()
        fetchOTPFromReciver()
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(Runnable {
            if (SharePrefCheqUserId.getInstance(this)
                    ?.getString(SharePrefsKeys.MESSAGE_Id) != null
            ) {
                checkByMessageId()
            } else {
                /* counter = 1
                 counterCall = 1
                 onResend()*/

                val intent = Intent(VerifyOtpActivity@ this, LoginActivity::class.java)
                intent.putExtra(
                    "MESSAGE",
                    getString(R.string.failed_to_send_otp_message)
                )
                startActivity(intent)
                finish()
            }
        }, 18000)


    }

    private fun fetchOTPFromReciver() {
        binding.llProgress.visibility = View.VISIBLE
        bindListener(object : OTPInterface.OTPListener {
            override fun onOTPReceived(otp: String) {

                if (!otp.isNullOrEmpty() && otp.length == 4) {
                    binding.tvCodeOne.setText(otp.subSequence(0, 1))
                    binding.tvCodeTwo.setText(otp.subSequence(1, 2))
                    binding.tvCodeThree.setText(otp.subSequence(2, 3))
                    binding.tvCodeFour.setText(otp.subSequence(3, 4))
                    MparticleUtils.logEvent(
                        "Onboarding_OTP_AutoFetch_Clicked",
                        "The OTP is auto-fetched and validated automatically by the device",
                        "Unique",
                        "Continue",
                        SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_OTP_CLICKED),
                        this@VerifyOtpActivity
                    )
                    binding.llProgress.visibility = View.GONE
                    binding.tvErrorOtp.visibility = View.GONE


                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.etOtp.setText(otp)
                    }, 1000)
                }

                //onVerify()
            }

        })

    }

    private fun startTimeout() {
        viewModel?.startTimeout()

        binding.etOtp.requestFocus()
        binding.etOtp.isCursorVisible = false
    }

    override fun onPause() {
        super.onPause()
        //  timer.cancel()
    }

    override fun onDestroy() {
        unbindListener()
        super.onDestroy()
        // timer.cancel()

    }

    private fun generateFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

            // Get new FCM registration token
            fcmToken = task.result

            // Toast.makeText(activity, fcmToken, Toast.LENGTH_SHORT).show()
        })
    }

    private fun startSmartUserConsent() {
        val client = SmsRetriever.getClient(this)
        client.startSmsUserConsent(null)
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.N)
    fun openTimeoutBottomSheet() {


        updatePermissionStatus("otp_timeout")
        val bottomSheetDialog = BottomSheetDialog(this)

        val mBinding: OtpTimeoutBottomSheetLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this), R.layout.otp_timeout_bottom_sheet_layout, null, false
        )
        mBinding.executePendingBindings()
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetDialog.setContentView(mBinding.root)
        bottomSheetDialog.setCanceledOnTouchOutside(true)
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.show()
        binding.activity = this
        val newMobile = mobile

        val mOtpSpannable = SpannableString(getString(R.string.otp_not_receive, newMobile))

        if(mOtpSpannable.length>48) {
            mOtpSpannable.setSpan(
                ForegroundColorSpan(Color.BLACK),
                35,
                49,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        mBinding.tvOTPDesc.text = mOtpSpannable
        mBinding.btnRetry.setOnClickListener {
            counter = 1
            counterCall = 1
            onResend()
            bottomSheetDialog.dismiss()
            startTimeout()

            //checkByMessageId()
            /*  val intent = Intent(VerifyOtpActivity@this,LoginActivity::class.java)
              intent.putExtra("MESSAGE", "Failed to send OTP to your mobile number")
             startActivity(intent)
              finish()*/


        }
    }

    fun onEditNumberClicked() {
        MparticleUtils.logEvent(
            "Onboarding_OTP_Edit_BackClicked",
            "Users opts to edit the mobile number that was selected on the previous screen",
            "Unique",
            "Back",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_OTP_EDIT_BACK_CLICKED),
            this@VerifyOtpActivity
        )

        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun startTimer(countDownTimeInMs: Long) {
        binding.tvRresendOTP.isEnabled = false
        binding.tvErrorOtp.visibility = View.GONE

        binding.etOtp.setText("")
        binding.tvTimer.visibility = View.VISIBLE
        binding.tvRresendOTPIn.visibility = View.VISIBLE

        binding.tvRresendOTP.text = getString(R.string.resend_otp_text)

        binding.tvRresendOTP.setTextColor(resources.getColor(R.color.colorTextGrey))

        counter++
        isTimerOn = true

        object : CountDownTimer(countDownTimeInMs, 1000) {
            @SuppressLint("DefaultLocale", "SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                isTimerOn = true
                val remainingTime = millisUntilFinished / 1000
                println("remainingTime $remainingTime")
                if (remainingTime.toString().length == 1) {
                    if (remainingTime.toString() == "0") {
                        binding.tvRresendOTP.text = "RESEND"
                        binding.tvTimer.visibility = View.GONE
                        binding.tvRresendOTPIn.visibility = View.GONE
                        binding.tvRresendOTP.isEnabled = true
                        binding.tvRresendOTP.setTextColor(resources.getColor(R.color.colorPrimary))
                    } else {
                        binding.tvTimer.text = "0$remainingTime" + "s"
                    }

                } else {
                    binding.tvTimer.text = "$remainingTime" + "s"
                }
                if (remainingTime <= 20) {
                    binding.llProgress.visibility = View.GONE
                }

            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun onFinish() {
                isTimerOn = false
                binding.tvTimer.visibility = View.GONE
                binding.tvRresendOTPIn.visibility = View.GONE

                if (counter < 4) {
                    binding.tvRresendOTP.isEnabled = true
                    canRequestOTPonCall.set(false)
                    binding.tvRresendOTP.setTextColor(resources.getColor(R.color.colorPrimary))

                } else {
                    binding.tvTimer.visibility = View.GONE
                    binding.tvRresendOTP.setTextColor(resources.getColor(R.color.colorTextGrey))

                }

                binding.tvRresendOTP.text = getString(R.string.resend_otp_text)

            }
        }.start()
    }


    private fun startTimerCall(countDownTimeInMs: Long) {

        binding.tvErrorOtp.visibility = View.GONE
        canRequestOTPonCall.set(false)
        binding.etOtp.setText("")
        counterCall++
        isTimerOn = true

        object : CountDownTimer(countDownTimeInMs, 1000) {
            @SuppressLint("DefaultLocale", "SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                isTimerOn = true
                val remainingTime = millisUntilFinished / 1000

                val stRemTime: String = if (remainingTime.toString().length == 1) {
                    "0$remainingTime" + "s"
                } else {
                    "$remainingTime" + "s"
                }

                if (remainingTime <= 20) {
                    binding.llProgress.visibility = View.GONE
                }

                //  binding.tvTimer.text = stRemTime
            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun onFinish() {
                isTimerOn = false



                if (counterCall < 4) {
                    canRequestOTPonCall.set(true)


                } else {
                    canRequestOTPonCall.set(false)

                }


            }
        }.start()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun checkByMessageId() {
        viewModel.otpReport(
            SharePrefCheqUserId.getInstance(this)
                ?.getString(SharePrefsKeys.MESSAGE_Id).toString()
        )

    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun catchIntent() {
        mobile = intent.getStringExtra("MOBILE").toString()
        comeFrom = intent.getStringExtra("comeFrom").toString()
    }

    private fun setupUI() {

        Utils.setLightStatusBar(this)


        if (SharePrefs.getInstance(MainApplication.getApplicationContext())?.getString(SharePrefsKeys.FIRST_NAME)
                .isNullOrEmpty() && SharePrefs.getInstance(this)!!
                .getString(SharePrefsKeys.LAST_NAME).isNullOrEmpty()
            && SharePrefs.getInstance(MainApplication.getApplicationContext())?.getString(SharePrefsKeys.USER_EMAIL)
                .isNullOrEmpty()
        ) {
            userType = "new"
        } else {
            userType = "existing"
        }
        MparticleUtils.logCurrentScreen(
            "/onboarding/verify-otp",
            "The customer validates the mobile number via the OTP sent on the device through SMS or voice",
            "user-type",
            userType,
            "",
            "",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.VERIFY_OTP),
            this
        )
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verify_otp)
        binding.etOtp.requestFocus()


        binding.activity = this
        activity = this

        binding.tvCodeOne.setOnClickListener {
            changeBtmBg(binding.tvCodeOne)
        }
        binding.tvNumber.text = ("+91 $mobile")

    }

    private fun updateThirdPartyUser() {
        val cheqUserId =
            SharePrefCheqUserId.getInstance(this)
                ?.getString(SharePrefsKeys.CHEQ_USER_ID)
        cheqUserId?.let { it1 ->
            AppsFlyEvents.setCurrentUserForAF(this, it1)
            MparticleUtils.setCurrentUser(
                this,
                it1
            )

            /*  //  enable finart sms api
              val sd = ProcessSMS(applicationContext, this, it1)
              sd.enableProcessing()*/
        }
    }


    private fun setupObserver() {
        updateFirebaseToken()
        viewModel.verifyOTPStateObserver.observe(this) { state ->
            if (state) {
                triggerFinartSdk()
            }
        }
        viewModel.errorBlockObserver.observe(this) { blockdata ->
            showErrorBlocker(blockdata)
        }
        viewModel.responseObserver.observe(this) {

            val cheqUserId =
                SharePrefCheqUserId.getInstance(this)?.getString(SharePrefsKeys.CHEQ_USER_ID)
            cheqUserId?.let { cheqId ->
                AppsFlyEvents.setCurrentUserForAF(this, cheqId)
                MparticleUtils.setCurrentUser(this, cheqId)
            }
            if (userType == "new"
            ) {
                AppsFlyEvents.logEventFly(
                    this,
                    AF_VERIFY_OTP,
                    AF_PERSONAL_DETAIL,
                    AF_CUST_COMPLETE_REGIST
                )
                FirebaseLog.FireBaseLogEvent(
                    this,
                    FBEvent_VERIFY_OTP,
                    FBEvent_PERSONAL_DETAIL,
                    FB_CUST_COMPLETE_REGIST
                )


            }

            startActivity(NavigationUtils.getNavigation(this))
            finish()

        }

        viewModel?.createReferralObserver?.observe(this, Observer {

            when (it) {
                is NetworkResult.Loading -> {
                }
                is NetworkResult.Success -> {
                    SharePrefsReferral.getInstance(this)
                        ?.setReferralId(SharePrefsKeys.REFERRAL_KEY, "")
                }
                is NetworkResult.Error -> {
                }
            }
        })

        viewModel.statusObserver.observe(this) {
            if (!it.equals(AFConstants.SOMETHING_WENT_WRONG)) {
                binding.tvErrorOtp.visibility = View.VISIBLE
                binding.tvErrorOtp.text = getString(R.string.otp_incorrect_please_try_again)
                binding.etOtp.setText("")
                changeBtmBg(binding.tvCodeOne)
                binding.etOtp.requestFocus()
                binding.etOtp.isCursorVisible = false
                binding.llBtmFour.setBackgroundResource(R.drawable.otp_et_btm_un_focused)
                wrongOTPCount++
                if (wrongOTPCount > 2) {

                    updatePermissionStatus("incorrect_limit_exceeded")
                    val intent = Intent(VerifyOtpActivity@ this, LoginActivity::class.java)
                    intent.putExtra("MESSAGE", getString(R.string.too_many_attempts_message))
                    startActivity(intent)
                    finish()

                }
            } else {
                //  Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                binding.tvErrorOtp.visibility = View.GONE
                // if (it.equals("Please connect to the internet!")) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                //   }

            }

        }

        viewModel.smsObserver.observe(this) {
            if (it != null) {
                if (!it.status?.groupName.equals("DELIVERED")) {
                    binding.tvErrorOtp.visibility = View.VISIBLE
                    binding.tvErrorOtp.text=getString(R.string.otp_send_failed)
                }else{
                    binding.tvErrorOtp.visibility = View.GONE
                }

            }else{
                binding.tvErrorOtp.visibility = View.GONE
            }
        }

        viewModel.progressObserver.observe(this) {
            if (it) {
                binding.loading.visibility = View.VISIBLE
            } else {
                binding.loading.visibility = View.GONE

            }
        }
        viewModel.loadingObserver.observe(this) {
            if (!it) {
                binding.tvErrorOtp.visibility = View.GONE
            }
        }
        viewModel.timeOutObserver.observe(this) {
            if (it) {
                openTimeoutBottomSheet()
                MparticleUtils.logEvent(
                    "Onboarding_OTP_Timeout_Viewed",
                    "User views the OTP timeout bottomsheet as the OTP is not validated within the stipulated time",
                    "Unique",
                    "View",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_OTP_TIMEOUT_VIEWED),
                    this@VerifyOtpActivity
                )
            }

        }
    }

    private fun updateFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                // Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Freshchat.getInstance(this).setPushRegistrationToken(token)
        })
    }
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[VerifyOtpViewModel::class.java]
        splashViewModel = ViewModelProvider(this)[SplashViewModel::class.java]
        splashViewModel.companyConstantObserver?.observe(this) {
            if (it?.assetsBaseUrl != null) {
                SharePrefs.getInstance(this)?.apply {
                    putBoolean(SharePrefsKeys.IS_BASE_URLS_AVAIL, true)
                    it.assetsBaseUrl.apply {
                        putString(SharePrefsKeys.BASE_BANK_MASTER, "${bankMaster}/")
                        putString(SharePrefsKeys.BASE_LOAN_MASTER, "${loanMaster}/")
                        putString(SharePrefsKeys.BASE_BANNER, "${banner}/")
                        putString(SharePrefsKeys.BASE_HTML, "${html}/")
                        putString(SharePrefsKeys.BASE_POLICIES, "${policies}/")
                        putString(SharePrefsKeys.BASE_FAQS, "${faqs}/")
                        putString(SharePrefsKeys.BASE_VOUCHER, "${voucher}")
                        putString(SharePrefsKeys.BASE_WAITLIST, "${waitlist}/")
                    }


                }
            }
        }
    }

    fun onVerify() {
        binding.tvErrorOtp.visibility = View.GONE
        binding.llProgress.visibility = View.GONE
        viewModel?.verifyOtp(binding.etOtp.text.toString(), mobile, fcmToken, this)

    }

    fun onNumberChangeClick() {
        onBackPressed()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        MparticleUtils.logEvent(
            "Onboarding_OTP_Edit_BackClicked",
            "Users opts to edit the mobile number that was selected on the previous screen",
            "Unique",
            "Back",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_OTP_EDIT_BACK_CLICKED),
            this@VerifyOtpActivity
        )
    }

    fun onVerifyCall() {
        //OTP on call is disabled.
        /*viewModel.verifyOtpCall(binding, mobile)
        canRequestOTPonCall.set(false)*/
    }

    var otpWatcher = object : TextWatcher {


        override fun beforeTextChanged(
            charSequence: CharSequence, i: Int, i1: Int, i2: Int
        ) {

        }

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            if (charSequence.length == 1) {

            }
            if (charSequence.length == 4) {
                canSignup.set(true)
                MparticleUtils.logEvent(
                    "Onboarding_OTP_Entered",
                    "User enters the four-digit OTP manually",
                    "Unique",
                    "Input Field",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_OTP_ENTERED),
                    this@VerifyOtpActivity
                )
                onVerify()


            } else {

                canSignup.set(false)
            }
        }

        override fun afterTextChanged(editable: Editable) {
            val cArr = editable.toString().trim { it <= ' ' }.toCharArray()

            binding.tvCodeOne.text = ""
            binding.tvCodeTwo.text = ""
            binding.tvCodeThree.text = ""
            binding.tvCodeFour.text = ""
            for (i in editable.indices) {
                if (i == 0) {
                    binding.tvCodeOne.text = cArr[i].toString()
                    changeBtmBg(binding.tvCodeOne)
                    binding.etOtp.isCursorVisible = false
                }
                if (i == 1) {
                    binding.tvCodeTwo.text = cArr[i].toString()
                    changeBtmBg(binding.tvCodeTwo)
                    binding.etOtp.isCursorVisible = false
                }
                if (i == 2) {
                    binding.tvCodeThree.text = cArr[i].toString()
                    changeBtmBg(binding.tvCodeThree)
                    binding.etOtp.isCursorVisible = false
                }
                if (i == 3) {
                    binding.tvCodeFour.text = cArr[i].toString()
                    changeBtmBg(binding.tvCodeFour)
                    binding.etOtp.isCursorVisible = false

                }
            }
        }
    }


    fun onResend() {
        MparticleUtils.logEvent(
            "Onboarding_OTP_Resend_Clicked",
            "User opts to receive a new OTP\n",
            "Unique",
            "Continue",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_OTP_RESEND_CLICKED),
            this@VerifyOtpActivity
        )

        binding.tvErrorOtp.visibility = View.GONE
        viewModel.generateOtp(mobile, this)
        canRequestOTPonCall.set(true)

        binding.etOtp.text?.clear()
        binding.etOtp.requestFocus()
        binding.etOtp.isCursorVisible = false

        if (counter < 3) {
            startTimer(31000)
        } else {
            binding.tvRresendOTP.isEnabled = false
            //   canRequestOTPonCall .set(false)
            binding.tvRresendOTP.setTextColor(resources.getColor(R.color.colorTextGrey))
        }


    }


    fun changeBtmBg(textView: TextView) {
        when (textView) {
            binding.tvCodeOne -> {
                binding.llBtmOne.setBackgroundResource(R.drawable.otp_btn_focused)
                binding.llBtmTwo.setBackgroundResource(R.drawable.otp_et_btm_un_focused)
                binding.llBtmThree.setBackgroundResource(R.drawable.otp_et_btm_un_focused)
                binding.llBtmFour.setBackgroundResource(R.drawable.otp_et_btm_un_focused)

            }
            binding.tvCodeTwo -> {
                binding.llBtmOne.setBackgroundResource(R.drawable.otp_et_btm_un_focused)
                binding.llBtmTwo.setBackgroundResource(R.drawable.otp_btn_focused)
                binding.llBtmThree.setBackgroundResource(R.drawable.otp_et_btm_un_focused)
                binding.llBtmFour.setBackgroundResource(R.drawable.otp_et_btm_un_focused)
            }
            binding.tvCodeThree -> {
                binding.llBtmOne.setBackgroundResource(R.drawable.otp_et_btm_un_focused)
                binding.llBtmTwo.setBackgroundResource(R.drawable.otp_et_btm_un_focused)
                binding.llBtmThree.setBackgroundResource(R.drawable.otp_btn_focused)
                binding.llBtmFour.setBackgroundResource(R.drawable.otp_et_btm_un_focused)
            }
            binding.tvCodeFour -> {
                binding.llBtmOne.setBackgroundResource(R.drawable.otp_et_btm_un_focused)
                binding.llBtmTwo.setBackgroundResource(R.drawable.otp_et_btm_un_focused)
                binding.llBtmThree.setBackgroundResource(R.drawable.otp_et_btm_un_focused)
                binding.llBtmFour.setBackgroundResource(R.drawable.otp_btn_focused)
            }
        }
    }


    private fun registerBroadcastReceiver() {
        smsBroadcastReceiver = SMSBroadcastReceiver()
        smsBroadcastReceiver?.smsBroadcastListener =
            object : SMSBroadcastReceiver.SmsBroadcastListener {
                override fun onSuccess(intent: Intent?) {
                    val name: ComponentName? = intent?.resolveActivity(packageManager)
                    if ("com.google.android.gms" == name?.packageName
                        && "com.google.android.gms.auth.api.phone.ui.UserConsentPromptActivity" == name.className
                    ) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            intent.removeFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            intent.removeFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                            intent.removeFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                            intent.removeFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
                        }
                        otpActivityResult.launch(intent)
                    }
                }

                override fun onFailure() {}
            }
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsBroadcastReceiver, intentFilter)
    }

    override fun onStart() {
        super.onStart()
        smsBroadcastReceiver?.let{registerBroadcastReceiver()}
    }

    override fun onStop() {
        super.onStop()
        smsBroadcastReceiver?.let {unregisterReceiver(smsBroadcastReceiver)}

    }


    private fun getOtpFromMessage(message: String?) {
        val otpPattern: Pattern = Pattern.compile("(|^)\\d{4}")
        val matcher: Matcher = otpPattern.matcher(message)


        if (matcher.find()) {
            val otp: String = matcher.group(0)
            if (otp.length == 4) {
            binding.tvCodeOne.setText(otp.subSequence(0, 1))
            binding.tvCodeTwo.setText(otp.subSequence(1, 2))
            binding.tvCodeThree.setText(otp.subSequence(2, 3))
            binding.tvCodeFour.setText(otp.subSequence(3, 4))
                binding.etOtp.setText(otp)
                onVerify()
            }

    }
    }
    fun updatePermissionStatus(name: String) {
        val cheqUserId = SharePrefCheqUserId.getInstance(this)
            ?.getString(SharePrefsKeys.CHEQ_USER_ID)
        splashViewModel.updateUserOTPConsent(
            RequestOTPConsent(
                cheqUserId,
                name,
            )
        )
    }

    private fun triggerFinartSdk() {
        val cheqUserId = SharePrefCheqUserId.getInstance(this)?.getString(SharePrefsKeys.CHEQ_USER_ID)
        cheqUserId?.let { cheqid ->
            finartProcessInstance = ProcessSMS(applicationContext, this, cheqid)
            finartProcessInstance.enableProcessing()
            viewModel.finartTrigger(cheqid, SCREEN_OTP)
            MparticleUtils.logFinartEvent(FINART_INVOKE, SCREEN_OTP)
            val bundle = Bundle().apply { putString(CHEQ_USER, cheqid) }
            FirebaseAnalytics.getInstance(MainApplication.getApplicationContext())
                .logEvent(FINART_TRIGGER_OTP, bundle)
        }
    }
    override fun onSuccess(p0: Int, p1: Int) {
        MparticleUtils.logFinartEvent(FINART_SUCCESS, SCREEN_OTP)
        FirebaseAnalytics.getInstance(MainApplication.getApplicationContext())
            .logEvent(FINART_SUCCESS_OTP, null)
    }
    override fun onError(p0: Int, p1: String?) {
        FirebaseCrashlytics.getInstance().recordException(Throwable("Finart_SMS_Error $p0 $p1"))
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