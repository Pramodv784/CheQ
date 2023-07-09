package com.cheq.retail.inappratings.models.uistate

import com.cheq.retail.inappratings.models.uimodels.ButtonTemplate


/**
 * Created by Sanket Mendon on 19,June,2023.
 * sanket@cheq.one
 */
sealed class FormTemplateUIState {
    object Loading : FormTemplateUIState()
    data class LoadingSuccess(
        val title: String?,
        val titleImage: String?,
        val options: List<String>?,
        val buttons: HashMap<Int, ButtonTemplate>
    ) : FormTemplateUIState()

    data class SubmitEnabled(val selectedOptions: List<String>) :
        FormTemplateUIState()

    object SubmitDisabled : FormTemplateUIState()
}
