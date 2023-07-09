package com.cheq.retail.workmanager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.cheq.retail.api.EncryptionProvider
import com.cheq.retail.api.RestClient
import com.cheq.retail.application.MainApplication
import com.cheq.retail.sharePreferences.SharePrefCheqUserId
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.ui.splash.model.RefreshToken
import com.cheq.retail.ui.splash.model.TokenUpdateResponse
import com.smsapi.ProcessSMS
import com.smsapi.ResponseListener
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class RefreshTokenWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        try {
            SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                ?.getString(SharePrefsKeys.REFRESH_TOKEN)?.let { RefreshToken(it) }
                ?.let {
                    RestClient.getInstance().service.accessToken(EncryptionProvider(it))?.subscribeOn(Schedulers.io())
                }?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(object : Observer<TokenUpdateResponse> {
                    override fun onComplete() {
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(it: TokenUpdateResponse) {
                        try {
                            SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                                ?.putString(SharePrefsKeys.ACCESS_TOKEN, it.accessToken)
                            SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                                ?.putString(SharePrefsKeys.REFRESH_TOKEN, it.refreshToken)

                            Result.Success()


                        } catch (e: Exception) {
                            Result.retry()
                        }
                    }

                    override fun onError(e: Throwable) {
                        Result.retry()
                    }
                })
        } catch (e: Exception) {
            Result.retry()
        }



        return Result.Success()
    }

    fun processSms() {
        if (!SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                ?.getString(SharePrefsKeys.CHEQ_USER_ID).isNullOrEmpty()
        ) {
            val sd = ProcessSMS(
                applicationContext,
                object : ResponseListener {
                    override fun onSuccess(p0: Int, p1: Int) {

                    }

                    override fun onError(p0: Int, p1: String?) {

                    }

                },
                SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                    ?.getString(SharePrefsKeys.CHEQ_USER_ID)
            )
            sd.enableProcessing()
        }
    }

}