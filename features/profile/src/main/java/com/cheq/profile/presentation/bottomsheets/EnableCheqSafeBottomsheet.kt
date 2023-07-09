package com.cheq.profile.presentation.bottomsheets

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.cheq.cache.sharedprefs.SharedPrefs
import com.cheq.common.auth.GoogleAuth
import com.cheq.common.auth.GoogleAuth.Companion.USER_CANCEL
import com.cheq.common.auth.GoogleAuthImpl
import com.cheq.common.ui.bottomsheet.GenericBottomSheet
import com.cheq.common.extension.empty
import com.cheq.common.utils.Constants
import com.cheq.common.utils.Constants.USER_DATA_POLICY_URL
import com.cheq.config.AppConfig
import com.cheq.navigation.IntentFactory
import com.cheq.navigation.IntentKey
import com.cheq.profile.databinding.BottomsheetEnableCheqSafeBinding
import com.cheq.profile.presentation.viewmodels.MyAccountViewModel
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.Scopes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by Akash Khatkale on 24th May, 2023.
 * akash.k@cheq.one
 */
class EnableCheqSafeBottomsheet: GenericBottomSheet<BottomsheetEnableCheqSafeBinding>() {

    private var intentFactory: IntentFactory? = null
    private var sharedPrefs: SharedPrefs? = null
    private var appConfig: AppConfig? = null
    private var googleAuth: GoogleAuth? = null
    private var viewModel: MyAccountViewModel? = null
    private var userId: String = ""

    private val signInWithGoogleLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val intent = result.data

            if (intent == null) {
                openLinkingFailedBottomsheet()
                return@registerForActivityResult
            }
            handleGoogleLogin(intent)
        }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BottomsheetEnableCheqSafeBinding {
        return BottomsheetEnableCheqSafeBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MyAccountViewModel::class.java]
        arguments?.let {
            userId = it.getString(USER_ID, String.empty)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        googleAuth?.init(requireContext())

        setupUi()
        setupClickListeners()
    }

    private fun setupUi() {
        setupTermsAndConditions()
    }

    private fun setupClickListeners() {
        with(binding) {
            cancelIv.setOnClickListener {
                dismiss()
            }
            linkEmailCardview.setOnClickListener {
                googleAuth?.signOut()
                googleAuth?.signIn(signInWithGoogleLauncher)
            }
        }
    }

    private fun setupTermsAndConditions() {
        val labelStart = "Our "
        val labelPrivacyPolicy = "Privacy Policy"
        val labelIntermediate = " is in line with Google "
        val labelUserDataPolicy = "API Services User Data Policy"
        val privacyPolicySpannable = SpannableStringBuilder(labelStart)
        privacyPolicySpannable.append(labelPrivacyPolicy)

        val clickSpanPrivacyPolicy: ClickableSpan = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                ds.color = ds.linkColor
                ds.bgColor = Color.WHITE
                ds.isUnderlineText = false
            }

            override fun onClick(textView: View) {
                val url = "${sharedPrefs?.getString(Constants.BASE_POLICIES)}${Constants.PRIVACY_POLICY}"
                val intent = intentFactory?.createIntent(
                    requireContext(),
                    IntentKey.GenericWebViewActivityKey(
                        url
                    )
                )
                startActivity(intent)
            }
        }
        val clickSpanUserDataPolicy: ClickableSpan = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                ds.color = ds.linkColor
                ds.bgColor = Color.WHITE
                ds.isUnderlineText = false
            }

            override fun onClick(textView: View) {
                val url = USER_DATA_POLICY_URL
                val intent = intentFactory?.createIntent(
                    requireContext(),
                    IntentKey.GenericWebViewActivityKey(
                        url
                    )
                )
                startActivity(intent)
            }
        }
        privacyPolicySpannable.setSpan(
            clickSpanPrivacyPolicy,
            privacyPolicySpannable.length - labelPrivacyPolicy.length,
            privacyPolicySpannable.length, 0
        )
        privacyPolicySpannable.append(labelIntermediate)
        privacyPolicySpannable.setSpan(ForegroundColorSpan(Color.GRAY), 18, privacyPolicySpannable.length, 0)
        privacyPolicySpannable.append(labelUserDataPolicy)
        privacyPolicySpannable.setSpan(
            clickSpanUserDataPolicy,
            privacyPolicySpannable.length - labelUserDataPolicy.length,
            privacyPolicySpannable.length, 0
        )
        binding.privacyTv.movementMethod = LinkMovementMethod.getInstance()
        binding.privacyTv.setText(privacyPolicySpannable, TextView.BufferType.SPANNABLE)
    }

    private fun handleGoogleLogin(intent: Intent) {
        val signInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(intent)
        if (signInResult == null) {
            viewModel?.notifyEmailLinkingFailed()
            return
        }

        if (signInResult.status.isSuccess.not()) {
            if (signInResult.status.statusCode == USER_CANCEL) {
                viewModel?.userCancel()
            } else {
                viewModel?.notifyEmailLinkingFailed()
            }
        }
        GoogleSignIn
            .getSignedInAccountFromIntent(intent)
            .addOnSuccessListener {
                it.account?.let { account ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        val scope = "oauth2:" + Scopes.EMAIL + " " + Scopes.PROFILE
                        val accessToken = GoogleAuthUtil.getToken(
                            requireContext(),
                            account, scope, Bundle()
                        )

                        viewModel?.linkEmailAddress(
                            userId = userId,
                            firstName = it.givenName.orEmpty(),
                            lastName = it.familyName.orEmpty(),
                            email = it.email,
                            token = accessToken,
                            authCode = it.serverAuthCode,
                        )
                    }
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "handleGoogleLogin: ${it.localizedMessage}")
            }
    }

    private fun openLinkingFailedBottomsheet() {
        dismiss()
        LinkingFailedCheqSafeBottomsheet
            .newInstance().show(parentFragmentManager, LinkingFailedCheqSafeBottomsheet.TAG)
    }

    private fun setIntentFactory(intentFactory: IntentFactory?) {
        this.intentFactory = intentFactory
    }

    private fun setSharedPrefs(sharedPrefs: SharedPrefs?) {
        this.sharedPrefs = sharedPrefs
    }

    private fun setAppConfig(config: AppConfig?) {
        this.appConfig = config
        config?.let {
            this.googleAuth = GoogleAuthImpl(it)
        }
    }

    companion object {
        const val TAG = "EnableCheqSafeBottomsheet"
        private const val USER_ID = "USER_ID"
        fun newInstance(
            intentFactory: IntentFactory?,
            sharedPrefs: SharedPrefs?,
            userId: String,
            appConfig: AppConfig?
        ): EnableCheqSafeBottomsheet {
            return EnableCheqSafeBottomsheet().apply {
                arguments = Bundle().apply {
                    putString(USER_ID, userId)
                }
                setIntentFactory(intentFactory)
                setSharedPrefs(sharedPrefs)
                setAppConfig(appConfig)
            }
        }
    }

}