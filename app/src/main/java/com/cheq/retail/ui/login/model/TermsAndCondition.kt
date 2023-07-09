package com.cheq.retail.ui.login.model

import androidx.annotation.Keep
import java.io.Serializable
@Keep
data class TermsAndCondition(
    val __v: Int,
    val createdAt: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val type: String,
    val updatedAt: String,
    val url: String
): Serializable