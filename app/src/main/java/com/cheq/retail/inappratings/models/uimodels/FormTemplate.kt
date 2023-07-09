package com.cheq.retail.inappratings.models.uimodels

import kotlinx.parcelize.Parcelize


/**
 * Created by Sanket Mendon on 21,June,2023.
 * sanket@cheq.one
 */
@Parcelize
class FormTemplate(
    override val type: String?,
    override val title: String?,
    val titleIcon: String?,
    val options: List<String>?,
    val showCustomFeedbackOption: Boolean? = false,
    override val action: Template?,
    override val actions: List<Template>?
) : Template()