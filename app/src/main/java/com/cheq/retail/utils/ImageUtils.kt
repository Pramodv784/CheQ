package com.cheq.retail.utils


import android.view.View
import android.widget.ImageView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.cheq.retail.R
import com.cheq.retail.application.MainApplication
import com.cheq.retail.extensions.bankMasterUrl
import com.cheq.retail.sharePreferences.SharePrefs
import com.google.firebase.crashlytics.FirebaseCrashlytics

object ImageUtils {

    fun ImageView.loadSvg(url: String, errorDrawableResId: Int? = null,loadingView: View?=null, placeholder: Int = R.drawable.bank_logo_placeholder) {

        val imageLoader =
            ImageLoader.Builder(this.context).apply {
                if (loadingView ==null){
                    placeholder(placeholder)
                }
                components { add(SvgDecoder.Factory()) }
            }.build()

        var request = ImageRequest
            .Builder(this.context)
            .data(url)
            .listener(
                onStart = {
                    loadingView?.visibility=View.VISIBLE
                },
                onSuccess = { request, metadata ->
                    // set your progressbar invisible here
                    loadingView?.visibility=View.GONE

                },
                onError = {request, throwable ->
                    loadingView?.visibility=View.GONE
                    FirebaseCrashlytics.getInstance().recordException(throwable.throwable)
                }



            )
            .target(this)



        if(errorDrawableResId != null) {
            request = request.error(errorDrawableResId)
        }
        imageLoader.enqueue(request.build())
    }

    val String.bankLogo: String

    get() {
        val imageLogo = "${SharePrefs.getInstance(MainApplication.getApplicationContext())?.bankMasterUrl}${this}-logo.svg"
        return imageLogo
    }



}