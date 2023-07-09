package com.cheq.retail.ui.main.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class HideProductRequest(

    val productId: String? = null,

    ) : Parcelable
