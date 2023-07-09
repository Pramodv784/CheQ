package com.cheq.common.extension

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.cheq.common.R

/**
 * Created by Akash Khatkale on 18th May, 2023.
 * akash.k@cheq.one
 */
fun AppCompatImageView.loadSvg(
    url: String,
    errorDrawableResId: Int? = null,
    loadingView: View? = null
) {

    val imageLoader =
        ImageLoader.Builder(this.context).apply {
            if (loadingView == null) {
                placeholder(R.drawable.bank_icon)
            }
            components {
                add(SvgDecoder.Factory())
            }
        }.build()

    var request = ImageRequest
        .Builder(this.context)
        .data(url)
        .listener(
            onStart = {
                loadingView?.isVisible = true
            },
            onSuccess = { _, _ ->
                loadingView?.isVisible = false
            },
            onError = { _, _ ->
                loadingView?.isVisible = false
            }
        )
        .target(this)



    if (errorDrawableResId != null) {
        request = request.error(errorDrawableResId)
    }
    imageLoader.enqueue(request.build())
}