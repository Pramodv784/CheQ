package com.cheq.common.auth

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.cheq.config.AppConfig
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import javax.inject.Inject

/**
 * Created by Akash Khatkale on 24th May, 2023.
 * akash.k@cheq.one
 */
class GoogleAuthImpl @Inject constructor(
    private val appConfig: AppConfig
) : GoogleAuth {

    private var googleSignInClient: GoogleSignInClient? = null

    companion object {
        const val SCOPE = "https://www.googleapis.com/auth/gmail.readonly"
    }

    override fun init(context: Context) {
        val gmailScope = Scope(SCOPE)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(appConfig.getGoogleRequestIdToken())
            .requestEmail()
            .requestServerAuthCode(appConfig.getGoogleRequestServerAuthCode())
            .requestScopes(gmailScope)
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    override fun signIn(launcher: ActivityResultLauncher<Intent>) {
        launcher.launch(googleSignInClient?.signInIntent)
    }

    override fun signOut() {
        googleSignInClient?.signOut()
    }
}