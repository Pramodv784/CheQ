package com.cheq.retail.ui.cheqsafe.subfragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.cheq.retail.R
import com.cheq.retail.databinding.BottomSheetDialogEnableCheqSafeBinding
import com.cheq.retail.ui.accountSettings.TermsConditionActivity
import com.cheq.retail.ui.accountSettings.webView.CommonWebViewActivity
import com.cheq.retail.ui.cheqsafe.CheqSafeViewModel
import com.cheq.retail.utils.Utils
import com.google.android.gms.auth.GoogleAuthException
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.callback.OnCompleteListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class IntroductionFragment : BottomSheetDialogFragment() {

    private val viewModel by lazy {
        ViewModelProvider(parentFragment ?: this)[CheqSafeViewModel::class.java]
    }

    private lateinit var binding: BottomSheetDialogEnableCheqSafeBinding
    private var googleSignInClient: GoogleSignInClient? = null
    private val USER_CANCEL = 12501
    private val FAILED = 12500


    private val signInWithGoogleLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val intent = result.data

            if (intent == null) {
                viewModel.notifyEmailLinkingFailed()
                return@registerForActivityResult
            }
            handleGoogleLogin(intent)
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetDialogEnableCheqSafeBinding.inflate(
            layoutInflater,
            container,
            false
        )
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        (dialog as? BottomSheetDialog)?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initGoogleLogin()
        setupActions()
        setupTermsAndConditions()
    }

    private fun initGoogleLogin() {
        val gmailScope = Scope("https://www.googleapis.com/auth/gmail.readonly")
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            // .requestIdToken("143139010454-p91g4i1lgru0tvri08mts7927ojvs21f.apps.googleusercontent.com")
            .requestIdToken("143139010454-2bm1njnpn4j5gg6235tpsf65g5j2j8r3.apps.googleusercontent.com")

            .requestEmail()
            // .requestServerAuthCode("143139010454-p91g4i1lgru0tvri08mts7927ojvs21f.apps.googleusercontent.com")
            .requestServerAuthCode("143139010454-2bm1njnpn4j5gg6235tpsf65g5j2j8r3.apps.googleusercontent.com")
            .requestScopes(gmailScope)
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

    }

    private fun setupActions() {
        binding.btnLinkEmail.setOnClickListener {
            googleSignOut()
            doGoogleLogin()
        }
        binding.ivCancel.setOnClickListener {
            dialog?.dismiss()
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
                startActivity(Intent(activity, TermsConditionActivity::class.java).apply {
                    putExtra("id", 2)
                })
            }
        }
        val clickSpanUserDataPolicy: ClickableSpan = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                ds.color = ds.linkColor
                ds.bgColor = Color.WHITE
                ds.isUnderlineText = false
            }

            override fun onClick(textView: View) {
                val googleApiPolicyUrl = "https://developers.google.com/terms/api-services-user-data-policy#additional_requirements_for_specific_api_scopes"
                startActivity(Intent(activity, CommonWebViewActivity::class.java).apply {
                    putExtra("URL", googleApiPolicyUrl)
                })
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
        binding.privacyTV.movementMethod = LinkMovementMethod.getInstance()
        binding.privacyTV.setText(privacyPolicySpannable, TextView.BufferType.SPANNABLE)
    }

    private fun googleSignOut() {
        googleSignInClient?.signOut()
            ?.addOnCompleteListener(object : OnCompleteListener<Void>,
                com.google.android.gms.tasks.OnCompleteListener<Void> {
                override fun complete(p0: Void) {

                }

                override fun onComplete(p0: Task<Void>) {

                }
            })
    }

    private fun doGoogleLogin() {
        signInWithGoogleLauncher.launch(googleSignInClient!!.signInIntent)
    }

    private fun handleGoogleLogin(intent: Intent) {
        val signInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(intent)

        if (signInResult == null) {
            viewModel.notifyEmailLinkingFailed()
            return
        }

      /*  if (!signInResult.status.isSuccess) {
            viewModel.notifyEmailLinkingFailed()
            Utils.setToast(activity,"User failed Sucess")
            return
        }*/

        when(signInResult.status.statusCode){
            USER_CANCEL ->{
                viewModel.userCancel()
            }
            FAILED ->{
                viewModel.notifyEmailLinkingFailed()
            }
            else ->{
                viewModel.notifyEmailLinkingFailed()
            }
        }
        GoogleSignIn.getSignedInAccountFromIntent(intent)
            .addOnSuccessListener {

                it.account?.let { account ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        val scope = "oauth2:" + Scopes.EMAIL + " " + Scopes.PROFILE
                        val accessToken = GoogleAuthUtil.getToken(
                            requireContext(),
                            account, scope, Bundle()
                        )

                        viewModel.linkEmailAddress(
                            it.givenName ?: "",
                            it.familyName ?: "",
                            it.email,
                            accessToken,
                            it.serverAuthCode
                        )
                    }
                }


            }


    }

}