package com.cheq.retail.ui.login.modelv2.productv1

import androidx.annotation.Keep
import java.io.Serializable
@Keep
data class UI(
    val cardColor: String,
    val opacity_border: String,
    val opacity_topLeft: String,
    val opacity_bottomRight: String,
) : Serializable
