package com.cheq.network.interceptors

import android.content.Context
import androidx.annotation.NonNull
import com.cheq.cache.sharedprefs.SharedPrefs
import com.cheq.cache.sharedprefs.SharedPrefsCheQ
import com.cheq.cache.sharedprefs.SharedPrefsCheQUser
import com.cheq.cache.sharedprefs.SharedPrefsConstants
import com.cheq.cache.sharedprefs.SharedPrefsConstants.ACCESS_TOKEN
import com.cheq.cache.sharedprefs.SharedPrefsConstants.IS_USER_AUTHENTICATE
import com.cheq.cache.sharedprefs.SharedPrefsConstants.QUICK_LOGIN_AVAILABLE
import com.cheq.cache.sharedprefs.SharedPrefsConstants.REFRESH_TOKEN
import com.cheq.cache.sharedprefs.SharedPrefsLog
import com.cheq.navigation.IntentFactory
import com.cheq.navigation.IntentKey
import com.cheq.network.NetworkConstants.AUTHORIZATION
import com.cheq.network.NetworkConstants.BEARER
import com.cheq.network.api.NetworkApi
import com.cheq.network.models.RefreshToken
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

/**
 * Created by Akash Khatkale on 1st June, 2023.
 * akash.k@cheq.one
 */
class TokenAuthenticator @Inject constructor(
    @ApplicationContext private val context: Context,
    @SharedPrefsCheQUser private val sharedPrefsCheqUser: SharedPrefs,
    @SharedPrefsCheQ private val sharedPrefsCheq: SharedPrefs,
    @SharedPrefsLog private val sharedPrefsLog: SharedPrefs,
    private val intentFactory: IntentFactory,
    private val api: NetworkApi
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val accessToken: String = sharedPrefsCheqUser.getString(SharedPrefsConstants.ACCESS_TOKEN)
        if (!isRequestWithAccessToken(response)) {
            return null //will return null in case of error so that our api will not just keep trying to refresh the token.
        }
        synchronized(this) {
            val newAccessToken: String =
                sharedPrefsCheqUser.getString(SharedPrefsConstants.ACCESS_TOKEN)
            // if the newToken is different, it means that it has been refreshed in the mean time by another call
            // so we can just use it to authenticate and skip the refresh
            if (accessToken != newAccessToken) {
                return newRequestWithAccessToken(response.request, newAccessToken)
            }
            // Otherwise this is the first call trying to refresh a token - so just refresh it
            // Previous refresh flow
            val refreshToken = RefreshToken(
                sharedPrefsCheqUser.getString(REFRESH_TOKEN)
            )
            val callSync =
                api.accessTokenCall(refreshToken)
            return try {
                val response1 = callSync?.execute()
                val tokenUpdateResponse = response1?.body()
                if (tokenUpdateResponse?.accessToken != null && tokenUpdateResponse.refreshToken != null) {
                    sharedPrefsCheqUser.putString(ACCESS_TOKEN, tokenUpdateResponse.accessToken)
                    sharedPrefsCheqUser.putString(REFRESH_TOKEN, tokenUpdateResponse.refreshToken)
                    newRequestWithAccessToken(response.request, tokenUpdateResponse.accessToken)
                } else {
                    handleServerError(response.code)
                    null
                }
            } catch (ex: Exception) {
                handleServerError(response.code)
                null
            } finally {
                response.body?.close()
            }
        }
    }

    private fun isRequestWithAccessToken(response: Response): Boolean {
        val header = response.request.header(AUTHORIZATION)
        return header != null && header.startsWith(BEARER)
    }

    @NonNull
    private fun newRequestWithAccessToken(
        request: Request,
        accessToken: String?
    ): Request {
        return request.newBuilder()
            .header(AUTHORIZATION, BEARER + accessToken)
            .build()
    }

    private fun handleServerError(errorCode: Int) {
        when (errorCode) {
            401 -> {
                sharedPrefsCheq.clearPrefs()
                sharedPrefsLog.clearPrefs()
                sharedPrefsCheqUser.clearPrefs()
                sharedPrefsCheq.putBoolean(IS_USER_AUTHENTICATE, false)
                sharedPrefsCheq.putBoolean(QUICK_LOGIN_AVAILABLE, true)
                intentFactory.createIntent(
                    context,
                    IntentKey.SplashActivityKey(
                        newTask = true
                    )
                )
            }
        }
    }

}