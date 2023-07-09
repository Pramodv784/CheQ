package com.cheq.retail.ui.login

//import androidx.biometric.BiometricManager
//import androidx.biometric.BiometricPrompt
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.cheq.navigation.IntentKey
import com.cheq.navigation.IntentProvider
import com.cheq.retail.R
import com.cheq.retail.api.errormodel.BlockData
import com.cheq.retail.application.MainApplication
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.constants.AFConstants
import com.cheq.retail.databinding.ActivityExistingUserBinding
import com.cheq.retail.sharePreferences.SharePrefCheqUserId
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsKeys.Companion.OTP_ALLOWED_EXISTING_USER
import com.cheq.retail.sharePreferences.SharePrefsKeys.Companion.OTP_ALLOWED_NEW_USER
import com.cheq.retail.sharePreferences.SharePrefsKeys.Companion.OTP_DENIED_EXISTING_USER
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.blocker_screen.BlockerActivity
import com.cheq.retail.ui.login.model.isUserBlocked
import com.cheq.retail.ui.login.viewModel.LoginViewModel
import com.cheq.retail.ui.permission.PermissionActivity
import com.cheq.retail.ui.splash.viewModel.SplashViewModel
import com.cheq.retail.ui.verifyOtp.VerifyOtpActivity
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.NavigationUtils
import com.cheq.retail.utils.PermissionUtils
import com.cheq.retail.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executor

@AndroidEntryPoint
class ExistingUserActivity : BaseActivity() {
    var binding: ActivityExistingUserBinding? = null
    var fullName: String? = null
    var mobile: String? = null

    private var viewModel: LoginViewModel? = null

    // private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var executor: Executor

    //  private lateinit var callBack: BiometricPrompt.AuthenticationCallback
    private var keyguardManager: KeyguardManager? = null
    private var viewModel2: SplashViewModel? = null
    private var doubleBackToExitPressedOnce = false

    companion object {
        val intentHelper = object :
            IntentProvider<IntentKey.ExistingUserActivityKey> {
            override fun createIntent(
                context: Context,
                key: IntentKey.ExistingUserActivityKey
            ): Intent {
                return Intent(context, ExistingUserActivity::class.java)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadPreferences()
        setUI()
        MparticleUtils.logCurrentScreen(
            "/onboarding/login",
            "The application recognises the customer as an existing customer and allow them to choose whether to login with pre-filled details or as a new user",
            "",
            "",
            "",
            "",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.LOGIN),
            this
        )
    }

    private fun loadPreferences() {
        if (SharePrefs.instance?.getString(SharePrefsKeys.FIRST_NAME) != null && SharePrefs.instance?.getString(
                SharePrefsKeys.LAST_NAME
            ) != null
        ) {
            fullName =
                SharePrefs.instance?.getString(SharePrefsKeys.FIRST_NAME) + " " + SharePrefs.instance?.getString(
                    SharePrefsKeys.LAST_NAME
                )
        }
        if (SharePrefs.instance?.getString(SharePrefsKeys.MOBILE_NUMBER) != null) {
            mobile = SharePrefs.instance?.getString(SharePrefsKeys.MOBILE_NUMBER)
        }
    }

    private fun setUI() {

        Utils.setLightStatusBar(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_existing_user)
        binding!!.activity = this
        binding!!.tvMobileN.text = "+91 $mobile"
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun onContinue() {
        if (!SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                ?.getString(SharePrefsKeys.CHEQ_USER_ID).isNullOrEmpty()) {
            MparticleUtils.logEvent(
                "Onboarding_Login_Clicked",
                "User opts to login as the existing and identified individual",
                "Unique",
                "Continue",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_LOGIN_CLICKED),
                this
            )

            if (PermissionUtils.checkRequiredPermissionForSms(this)) {
                viewModel?.generateOtp(mobile)
            } else {
                startActivity(
                    PermissionActivity.startActivityForFirstTime(
                        this,
                        mobile ?: ""
                    )
                )
            }
        }else{
            MparticleUtils.logEvent(
                eventName = "Onboarding_No_CheQ_user_found",
                eventDescription = "when cheQ user id is not found")
        }
    }

    fun clearUserData() {
        MparticleUtils.logEvent(
            "Onboarding_Login_DifferentUser_Clicked",
            "User authenticates via device authentication",
            "Unique",
            "Continue",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_LOGIN_DIFFERENT_USER_CLICKED),
            this
        )
        SharePrefs.getInstance(this)!!.clearSharePrefs()
        startActivity(Intent(this, LoginActivity::class.java))
        finishAffinity()
    }

    private fun setupObserver() {
        viewModel?.otpObserver?.observe(this) { otpResponse ->
            if (otpResponse != null) {
                if (otpResponse.isUserBlocked()) {
                    showErrorBlocker(otpResponse.blockedData)
                } else {
                    authenticateWithOtp()
                }
            } else {
                MparticleUtils.logEvent(
                    eventName = OTP_DENIED_EXISTING_USER,
                    eventDescription = "Existing User is not allowed to go to verify otp when OTP fetch is not successful",)
                Utils.showToast(this@ExistingUserActivity, AFConstants.SOMETHING_WENT_WRONG)
            }
        }

        viewModel!!.userDetailsObserver.observe(this) {
            startActivity(NavigationUtils.getNavigation(this))
            finishAffinity()
        }

        viewModel!!.statusObserver.observe(this) {
           Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        viewModel!!.progressObserver.observe(this) {
            if (it) {
                // Utils.showProgressDialog(this)
                Utils.getUserClickBehaviour(false, this)
                binding?.button?.visibility = View.GONE
                binding?.flLottieIndicator?.visibility = View.VISIBLE
            } else {
                binding?.flLottieIndicator?.visibility = View.GONE
                binding?.button?.visibility = View.VISIBLE
                Utils.getUserClickBehaviour(true, this)
                //  Utils.hideProgressDialog()
            }
        }
    }

    private fun authenticateWithOtp() {
        MparticleUtils.logEvent(
            eventName = OTP_ALLOWED_EXISTING_USER,
            eventDescription = "Existing User is allowed to go to verify otp when OTP fetch is successful")
        startActivity(
            Intent(this, VerifyOtpActivity::class.java).putExtra(
                "MOBILE",
                mobile
            )
        )
        finish()

    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            finishAffinity()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Press BACK again to close the app", Toast.LENGTH_SHORT).show()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        viewModel2 = ViewModelProvider(this)[SplashViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()
        loadPreferences()
        setupViewModel()
        viewModel2?.checkCheqUser()
        setupObserver()
        binding?.activity = this
        binding?.tvMobileN?.text = "+91 $mobile"

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