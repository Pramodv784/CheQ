package com.cheq.retail.ui.socialLogin

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Nullable
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.databinding.ActivitySocialLogin1Binding
import com.cheq.retail.ui.main.MainActivity
import com.cheq.retail.ui.socialLogin.viewModel.SocialLoginViewModel
import com.cheq.retail.utils.Utils
import com.google.android.gms.auth.GoogleAuthException
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn.getClient
import com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.microsoft.identity.client.*
import com.microsoft.identity.client.IPublicClientApplication.ISingleAccountApplicationCreatedListener
import com.microsoft.identity.client.ISingleAccountPublicClientApplication.CurrentAccountCallback
import com.microsoft.identity.client.ISingleAccountPublicClientApplication.SignOutCallback
import com.microsoft.identity.client.exception.MsalClientException
import com.microsoft.identity.client.exception.MsalException
import com.microsoft.identity.client.exception.MsalServiceException
import org.json.JSONObject
import java.io.IOException


class SocialLoginActivity : BaseActivity() {
    private var _activity: SocialLoginActivity? = null
    private var _binding: ActivitySocialLogin1Binding? = null
    private var viewModel: SocialLoginViewModel? = null
    private var googleSignInClient: GoogleSignInClient? = null
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var mSingleAccountApp: ISingleAccountPublicClientApplication? = null
    private var mAccount: IAccount? = null
    val MS_GRAPH_ROOT_ENDPOINT = "https://graph.microsoft.com/"
    var name: String = ""

    var accessToken: String = ""
    var accessTokenSource: String = ""

    var scope: String = "user.read mail.read"

    private val signInWithGoogleLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val task = getSignedInAccountFromIntent(data)

                try {
                    accessTokenSource = "Gmail"

                    val account = task.getResult(ApiException::class.java)
                    val runnable = Runnable {
                        try {
                            val scope = "oauth2:" + Scopes.EMAIL + " " + Scopes.PROFILE
                            accessToken = GoogleAuthUtil.getToken(
                                applicationContext,
                                account!!.account!!, scope, Bundle()
                            )

                            viewModel!!.postUserDetails(
                                account.givenName!!,
                                account.familyName!!,
                                account.email!!,
                                accessToken!!,
                                accessTokenSource,
                                refreshToken = ""
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } catch (e: GoogleAuthException) {
                            e.printStackTrace()
                        }
                    }
                    AsyncTask.execute(runnable)

                } catch (e: ApiException) {

                }

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUi()
        setupViewModel()
        setupObserver()
        initGoogleLogin()
        createMicrosoft()
       // MparticleUtils.logCurrentScreen(this::class.java.simpleName)
    }

    private fun createMicrosoft() {

        PublicClientApplication.createSingleAccountPublicClientApplication(applicationContext,
            R.raw.auth_config_single_account,
            object : ISingleAccountApplicationCreatedListener {
                override fun onCreated(application: ISingleAccountPublicClientApplication) {

                    mSingleAccountApp = application
                    loadAccount()
                }

                override fun onError(exception: MsalException) {
                    displayError(exception)
                }
            })
    }

    private fun displayError(exception: Exception) {

    }

    private fun loadAccount() {
        if (mSingleAccountApp == null) {
            return
        }
        mSingleAccountApp!!.getCurrentAccountAsync(object : CurrentAccountCallback {
            override fun onAccountLoaded(@Nullable activeAccount: IAccount?) {
                // You can use the account data to update your UI or your app database.
                mAccount = activeAccount
                // updateUI(activeAccount)
            }

            override fun onAccountChanged(
                @Nullable priorAccount: IAccount?,
                @Nullable currentAccount: IAccount?
            ) {
                if (currentAccount == null) {
                    // Perform a cleanup task as the signed-in account changed.
                    // performOperationOnSignOut()
                }
            }

            override fun onError(exception: MsalException) {
                displayError(exception)
            }
        })
    }

    fun setupUi() {
        Utils.setLightStatusBar(this)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_social_login1)
        _activity = this

        _binding!!.tvSkip.setOnClickListener {
            skip()
        }

        _binding!!.btn.setOnClickListener {
            googleSignInClient?.signOut()
            signInWithGoogle()
        }


        _binding!!.tvAnother.setOnClickListener {
            signOut()
            if (mSingleAccountApp == null) {
                return@setOnClickListener
            }
            mSingleAccountApp!!.signIn(this, "", getScopes()!!, getAuthInteractiveCallback()!!)

        }
    }

    private fun getAuthInteractiveCallback(): AuthenticationCallback? {
        return object : AuthenticationCallback {
            override fun onSuccess(authenticationResult: IAuthenticationResult) {
                /* Successfully got a token, use it to call a protected resource - MSGraph */
                accessTokenSource = "Outlook"

                name = "" + authenticationResult.account.claims!!["name"]

                /* Update account */
                mAccount = authenticationResult.account
                //updateUI()
                accessToken = authenticationResult.accessToken
                viewModel!!.postUserDetails(
                    name,
                    name,
                    authenticationResult.account.username,
                    accessToken!!,
                    accessTokenSource,
                    refreshToken = ""
                )
                /* call graph */
                callGraphAPI(authenticationResult)
            }

            override fun onError(exception: MsalException) {
                /* Failed to acquireToken */
                displayError(exception)
                if (exception is MsalClientException) {
                    /* Exception inside MSAL, more info inside MsalError.java */
                } else if (exception is MsalServiceException) {
                    /* Exception when communicating with the STS, likely config issue */
                }
            }

            override fun onCancel() {
                /* User canceled the authentication */

            }
        }
    }

    private fun callGraphAPI(authenticationResult: IAuthenticationResult) {
        MSGraphRequestWrapper.callGraphAPIUsingVolley(
            applicationContext,
            MS_GRAPH_ROOT_ENDPOINT,
            authenticationResult.accessToken,
            { response ->

                displayGraphResult(response)
            }
        ) { error ->

            displayError(error)
        }
    }

    private fun displayGraphResult(graphResponse: JSONObject) {
        Toast.makeText(this, graphResponse.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun getScopes(): Array<String?>? {
        return scope.toLowerCase().split(" ".toRegex()).toTypedArray()
    }

    private fun skip() {
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
    }

    private fun initGoogleLogin() {
        val gmailScope = Scope("https://www.googleapis.com/auth/gmail.readonly")
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("509711030538-87010uapnmvvvl0vsbhbu0sr5r851foo.apps.googleusercontent.com")
            .requestEmail()
            .requestServerAuthCode("509711030538-87010uapnmvvvl0vsbhbu0sr5r851foo.apps.googleusercontent.com")
            .requestScopes(gmailScope)
            .build()
        googleSignInClient = getClient(this, gso)
    }


    private fun signInWithGoogle() {
        signInWithGoogleLauncher.launch(googleSignInClient!!.signInIntent)

    }

    private fun setupObserver() {
        viewModel!!.responseObserver.observe(this) {
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }

        viewModel!!.statusObserver.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        viewModel!!.progressObserver.observe(this) {
            if (it) {
                Utils.showProgressDialog(this)
            } else {
                Utils.hideProgressDialog()
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[SocialLoginViewModel::class.java]
    }


    private fun signOut() {

        if (mSingleAccountApp == null) {
            return
        }

        mSingleAccountApp!!.signOut(object : SignOutCallback {
            override fun onSignOut() {
                mAccount = null
            }

            override fun onError(exception: MsalException) {
                //displayError(exception)
            }
        })
    }

}