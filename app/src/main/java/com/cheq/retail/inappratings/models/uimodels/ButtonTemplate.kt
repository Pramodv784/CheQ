package com.cheq.retail.inappratings.models.uimodels

import kotlinx.parcelize.Parcelize


/**
 * Created by Sanket Mendon on 21,June,2023.
 * sanket@cheq.one
 */
@Parcelize
class ButtonTemplate(
    override val type: String?,
    override val title: String?,
    val titleIcon: String?,
    val style: String?,
    override val action: Template?,
    override val actions: List<Template>?
) : Template()