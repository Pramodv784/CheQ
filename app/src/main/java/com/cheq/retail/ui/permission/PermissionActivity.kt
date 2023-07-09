package com.cheq.retail.ui.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModelProvider
import com.cheq.retail.R
import com.cheq.retail.api.errormodel.BlockData
import com.cheq.retail.application.MainApplication
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.constants.AFConstants
import com.cheq.retail.databinding.ActivityPermissionBinding
import com.cheq.retail.sharePreferences.SharePrefCheqUserId
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsKeys.Companion.OTP_ALLOWED_EXISTING_USER
import com.cheq.retail.sharePreferences.SharePrefsKeys.Companion.OTP_ALLOWED_NEW_USER
import com.cheq.retail.sharePreferences.SharePrefsKeys.Companion.OTP_DENIED_EXISTING_USER
import com.cheq.retail.sharePreferences.SharePrefsKeys.Companion.OTP_DENIED_NEW_USER
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.blocker_screen.BlockerActivity
import com.cheq.retail.ui.login.ExistingUserActivity
import com.cheq.retail.ui.login.LoginActivity
import com.cheq.retail.ui.login.model.RequestUserConsent
import com.cheq.retail.ui.login.model.isUserBlocked
import com.cheq.retail.ui.login.viewModel.LoginViewModel
import com.cheq.retail.ui.permission.PermissionActivity.Companion.EXTRA_MOBILE
import com.cheq.retail.ui.splash.viewModel.SplashViewModel
import com.cheq.retail.ui.verifyOtp.VerifyOtpActivity
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.NavigationUtils
import com.cheq.retail.utils.PermissionUtils
import com.cheq.retail.utils.Utils
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission

class PermissionActivity : BaseActivity() {
    lateinit var binding: ActivityPermissionBinding
    var isPermissionDenied = ObservableBoolean(false)
    private var viewModel: LoginViewModel? = null
    var mobile: String? = null
//    private var viewModel: SplashViewModel? = null
private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>


    companion object {
        val TAG = PermissionActivity::class.java.simpleName
        val EXTRA_MOBILE = "$TAG.EXTRA_MOBILE"
        val USER_FROM_LOGIN = "USER_FROM_LOGIN"
        val MOBILE = "MOBILE"
        fun startActivityForFirstTime(context: Context, mobile: String, fromLogin: Boolean = false): Intent {
            return Intent(context, PermissionActivity::class.java).apply {
                putExtra(EXTRA_MOBILE, mobile)
                putExtra(USER_FROM_LOGIN,fromLogin)
            }
        }
    }

    private var permissionListener: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            isPermissionDenied.set(false)

            if(PermissionUtils.checkRequiredPermissionNew(applicationContext)){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    checkPermissionForNotification()
                }
                else{
                    navigateToApp()
                }
            }


        }

        override fun onPermissionDenied(deniedPermissions: List<String>) {

            if (PermissionUtils.checkRequiredPermission(this@PermissionActivity)){
                navigateToApp()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                checkPermissionForNotification()
            }
            binding.tvErrorText.visibility = View.VISIBLE
            SharePrefs.getInstance(this@PermissionActivity)!!.markNeverAskAgainPermission(true)

            if (!PermissionUtils.checkRequiredPermissionForSms(applicationContext)) {
                binding.tvError.visibility = View.VISIBLE
                binding.tvErrorText.visibility = View.VISIBLE
                binding.tvErrorText.text = "Please allow sms permission to proceed"

            }
        }


    }

    fun checkPermissionForNotification(){

            if (ContextCompat.checkSelfPermission(
                    this@PermissionActivity,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }


    }

    fun requestPermissionInSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 200)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkIntent()
        setupUI()
        setupViewModel()

        MparticleUtils.logCurrentScreen(
            "/onboarding/permissions",
            "The customer is asked for mandatory SMS permission on Android",
            "",
            "",
            "",
            "",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.PERMISSIONS),
            this@PermissionActivity
        )

    }

    private fun checkIntent() {
        mobile = intent?.getStringExtra(EXTRA_MOBILE)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

    }

    private fun setupUI() {
        Utils.setLightStatusBar(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_permission)
        binding.activity = this

        if (SharePrefs.getInstance(this)?.isNeverAskAgainPermission() == true) {
            if (!PermissionUtils.checkRequiredPermissionForSms(this)) {
                binding.tvError.visibility = View.VISIBLE
                binding.tvErrorText.visibility = View.VISIBLE
                binding.tvErrorText.text = getString(R.string.permission_error_message)
            } else {
                binding.tvError.visibility = View.GONE
                binding.tvErrorText.visibility = View.GONE
            }
        }

        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {

            if (PermissionUtils.checkRequiredPermissionNew(applicationContext)) {
                navigateToApp()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun navigateToApp() {
        checkPermissionForEvents()

        Handler(Looper.getMainLooper()).postDelayed({
            viewModel?.generateOtp(mobile)
        }, 500)

        viewModel?.otpObserver?.observe(this) { otpResponse ->
            if (otpResponse != null) {
                if (otpResponse.isUserBlocked()) {
                    showErrorBlocker(otpResponse.blockedData)
                } else {
                    MparticleUtils.logEvent(
                        eventName = if(intent?.extras?.getBoolean(USER_FROM_LOGIN) == true) OTP_ALLOWED_NEW_USER else OTP_ALLOWED_EXISTING_USER,
                        eventDescription = "User is allowed to go to verify otp when OTP fetch is successful")
                    startActivity(
                        Intent(this, VerifyOtpActivity::class.java).putExtra(
                            MOBILE,
                            mobile?.trim()
                        )
                    )
                    finish()
                }
            } else{
                MparticleUtils.logEvent(
                    eventName = if(intent?.extras?.getBoolean(USER_FROM_LOGIN) == true) OTP_DENIED_NEW_USER else OTP_DENIED_EXISTING_USER,
                    eventDescription = "User is allowed to go to verify otp when OTP fetch is successful")
                Utils.showToast(this@PermissionActivity, AFConstants.SOMETHING_WENT_WRONG)
            }
        }
        viewModel?.statusObserver?.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (PermissionUtils.checkRequiredPermission(this)) {
                isPermissionDenied.set(false)
                binding.tvError.visibility = View.GONE
                SharePrefs.getInstance(this@PermissionActivity)!!.markNeverAskAgainPermission(false)
                navigateToApp()
            } else {
                binding.tvError.visibility = View.VISIBLE
                binding.tvErrorText.visibility = View.VISIBLE

                if (!PermissionUtils.checkRequiredPermissionForSms(
                        this
                    ) && !PermissionUtils.checkRequiredPermissionForReceiveSMS(this)
                ) {
                    binding.tvErrorText.text = getString(R.string.please_allow_the_permissions_to_proceed)

                }

                isPermissionDenied.set(true)
            }

        } else {
            if (PermissionUtils.checkRequiredPermissionNew(this)) {
                isPermissionDenied.set(false)
                binding.tvError.visibility = View.GONE
                SharePrefs.getInstance(this@PermissionActivity)!!.markNeverAskAgainPermission(false)

                navigateToApp()
            } else {
                binding.tvError.visibility = View.VISIBLE
                binding.tvErrorText.visibility = View.VISIBLE
                if (!PermissionUtils.checkRequiredPermissionForSms(this)) {
                    binding.tvErrorText.text = getString(R.string.please_allow_sms_permission_to_proceed)


                }
                if (!PermissionUtils.checkRequiredPermissionForReceiveSMS(this)) {
                    binding.tvErrorText.text = getString(R.string.please_allow_sms_receive_permission)
                }

                if (!PermissionUtils.checkRequiredPermissionForSms(
                        this
                    ) && !PermissionUtils.checkRequiredPermissionForReceiveSMS(this)
                ) {
                    binding.tvErrorText.text =
                       getText(R.string.please_allow_sms_permission_to_proceed)

                }

                isPermissionDenied.set(true)
            }

        }
    }

    override fun onResume() {
        super.onResume()
        if (!PermissionUtils.checkRequiredPermissionForSms(this)) {
            binding.tvErrorText.text = getString(R.string.please_allow_sms_permission_to_proceed)
        }

        if ( !PermissionUtils.checkRequiredPermissionForSms(
                this
            )
        ) {
            binding.tvErrorText.text = getString(R.string.please_allow_the_permissions_to_proceed)
        }

    }

    fun checkPermission() {
        MparticleUtils.logEvent(
            "Onboarding_Permissions_Clicked",
            "User opts to progress ahead by pressing the continue CTA on permissions screen",
            "Not Unique",
            "Continue",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_PERMISSION_CLICKED),
            this@PermissionActivity
        )
       /* if (PermissionUtils.checkRequiredPermission(this)){
            navigateToApp()
        }*/
        if (SharePrefs.getInstance(this)?.isNeverAskAgainPermission() == true) {
            requestPermissionInSetting()

        } else {

                TedPermission.create().setPermissionListener(permissionListener).setPermissions(
                    Manifest.permission.READ_SMS,
                    Manifest.permission.RECEIVE_SMS,
                ).check()
                //   updatePermissionStatus("notificationPermission", true)


        }
    }

    fun checkPermissionForEvents(){
        if (PermissionUtils.checkRequiredPermissionForSms(applicationContext)) {
            //   updatePermissionStatus("sMSPermission", true)

            MparticleUtils.logEvent(
                "Onboarding_SMS_Permission_Allowed",
                "User allows SMS permission on Android devices",
                "Not Unique",
                "OS-Modal",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_SMS_PERMISSION_ALLOWED),
                this@PermissionActivity
            )


        }
        else{
            MparticleUtils.logEvent(
                "Onboarding_SMS_Permission_Denied",
                "User denies SMS permission on Android devices",
                "Unique",
                "OS-Modal",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_SMS_PERMISSION_DENIED),
                this@PermissionActivity
            )
        }
        if (PermissionUtils.checkRequiredPermissionForReceiveSMS(applicationContext)) {
            // updatePermissionStatus("receiveSMSPermission", true)
            MparticleUtils.logEvent(
                "Onboarding_SMS_Recieve_Permission_Allowed",
                "User allows SMS permission on Android devices",
                "Not Unique",
                "OS-Modal",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_SMS_Recieve_PERMISSION_ALLOWED),
                this@PermissionActivity
            )

        }
        else{
            MparticleUtils.logEvent(
                "Onboarding_SMS_Permission_Denied",
                "User denies SMS permission on Android devices",
                "Unique",
                "OS-Modal",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_SMS_PERMISSION_DENIED),
                this@PermissionActivity
            )
        }
        if (PermissionUtils.checkRequiredPermissionForNotification(applicationContext)) {
            //updatePermissionStatus("notificationPermission", true)
            MparticleUtils.logEvent(
                "Onboarding_Notification_Permission_Allowed",
                "User allows notification permission",
                "Unique",
                "OS-Modal",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_NOTIFICATION_PERMISSION_ALLOWED),
                this@PermissionActivity
            )
        }
        else{
            MparticleUtils.logEvent(
                "Onboarding_Notification_Permission_Denied",
                "User denies notification permission",
                "Unique",
                "OS-Modal",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_NOTIFICATION_PERMISSION_DENIED),
                this@PermissionActivity
            )
        }
    }

    fun updatePermissionStatus(name: String, status: Boolean) {
        val cheqUserId = SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
            ?.getString(SharePrefsKeys.CHEQ_USER_ID)
        /* viewModel?.updateUserConsent(
             RequestUserConsent(
                 cheqUserId,
                 name,
                 status,
             )
         )*/
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