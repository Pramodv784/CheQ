package com.cheq.retail.inappratings.models.uimodels

import kotlinx.parcelize.Parcelize


/**
 * Created by Sanket Mendon on 23,June,2023.
 * sanket@cheq.one
 */
@Parcelize
class IntentTemplate(
    override val type: String?,
    val url: String? = null,
    override val title: String? = null,
    override val action: Template? = null,
    override val actions: List<Template>? = null
) : Template()