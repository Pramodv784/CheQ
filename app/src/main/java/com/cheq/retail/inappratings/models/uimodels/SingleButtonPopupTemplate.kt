package com.cheq.retail.inappratings.models.uimodels


/**
 * Created by Sanket Mendon on 25,June,2023.
 * sanket@cheq.one
 */

data class SingleButtonPopupTemplate(
    val title: String?,
    val titleIcon: String?,
    val lottieAvailable: Boolean = false,
    val btnText: String?,
    val btnIcon: String?,
    val btnAction: Template?
)