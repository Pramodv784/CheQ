package com.cheq.retail.api

import androidx.annotation.NonNull
import com.cheq.retail.api.ErrorManager.Companion.handleServerError
import com.cheq.retail.application.MainApplication.Companion.getApplicationContext
import com.cheq.retail.constants.Constant.Companion.AUTHORIZATION
import com.cheq.retail.constants.Constant.Companion.BEARER
import com.cheq.retail.sharePreferences.SharePrefCheqUserId.Companion.getInstance
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.ui.splash.model.RefreshToken
import com.cheq.retail.utils.Utils.Companion.getRefreshToken
import com.cheq.retail.utils.Utils.Companion.getToken
import com.google.firebase.crashlytics.FirebaseCrashlytics
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route


class TokenAuthenticator : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val accessToken: String? = getToken(getApplicationContext())
        if (!isRequestWithAccessToken(response)) {
            return null //will return null in case of error so that our api will not just keep trying to refresh the token.
        }
        synchronized(this) {
            val newAccessToken: String? = getToken(getApplicationContext())
            // if the newToken is different, it means that it has been refreshed in the mean time by another call
            // so we can just use it to authenticate and skip the refresh
            if (accessToken != newAccessToken) {
                return newRequestWithAccessToken(response.request, newAccessToken)
            }
            // Otherwise this is the first call trying to refresh a token - so just refresh it
            // Previous refresh flow
            val refreshToken = getRefreshToken(getApplicationContext())?.let {
                RefreshToken(
                    it
                )
            }
            if (refreshToken != null) {
                val callSync =
                    RestClient.getInstance().service.accessTokenCall(EncryptionProvider(refreshToken))
                return try {
                    val response1 = callSync?.execute()
                    val tokenUpdateResponse = response1?.body()
                    if (tokenUpdateResponse?.accessToken != null && tokenUpdateResponse.refreshToken!=null) {
                        getInstance(getApplicationContext())?.putString(
                                SharePrefsKeys.ACCESS_TOKEN,
                                tokenUpdateResponse.accessToken
                            )
                        getInstance(getApplicationContext())
                            ?.putString(
                                SharePrefsKeys.REFRESH_TOKEN,
                                tokenUpdateResponse.refreshToken
                            )
                        newRequestWithAccessToken(response.request, tokenUpdateResponse.accessToken)
                    } else {
                        handleServerError(response.code)
                        null
                    }
                } catch (ex: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(ex);
                    handleServerError(response.code)
                    null
                }finally {
                    response.body?.close()
                }
            } else {
                handleServerError(response.code)
                return null
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
    ): Request{
        return request.newBuilder()
            .header(AUTHORIZATION, BEARER + accessToken)
            .build()
    }

}