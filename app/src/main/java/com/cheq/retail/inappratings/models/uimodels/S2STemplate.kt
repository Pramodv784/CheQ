package com.cheq.retail.inappratings.models.uimodels

import kotlinx.parcelize.Parcelize


/**
 * Created by Sanket Mendon on 21,June,2023.
 * sanket@cheq.one
 */
@Parcelize
class S2STemplate(
    override val type: String?,
    override val title: String? = null,
    val postUrl: String?,
    val keys: List<String>?,
    override val action: Template? = null,
    override val actions: List<Template>? = null
) : Template()