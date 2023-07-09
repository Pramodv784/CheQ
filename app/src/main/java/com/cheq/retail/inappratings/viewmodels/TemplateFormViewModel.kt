package com.cheq.retail.inappratings.viewmodels

import androidx.lifecycle.ViewModel
import com.cheq.retail.inappratings.models.uimodels.ButtonTemplate
import com.cheq.retail.inappratings.models.uimodels.FormTemplate
import com.cheq.retail.inappratings.models.uimodels.Template
import com.cheq.retail.inappratings.models.uistate.FormTemplateUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Created by Sanket Mendon on 15,June,2023.
 * sanket@cheq.one
 */

class TemplateFormViewModel() : ViewModel() {
    private val _uiState: MutableStateFlow<FormTemplateUIState> =
        MutableStateFlow(FormTemplateUIState.Loading)
    val uiState: StateFlow<FormTemplateUIState> = _uiState.asStateFlow()

    private val selectedOptions: MutableList<String> = mutableListOf()
    var buttonActions: HashMap<Int, ButtonTemplate> = hashMapOf()

    fun getAction(id: Int): Template? = buttonActions.get(id)?.action
    fun setUiTemplate(template: FormTemplate?) {
        val title = template?.title
        val titleIcon = template?.titleIcon
        val options = template?.options
        val buttons = HashMap<Int, ButtonTemplate>()
        val actions = template?.actions
        actions.takeIf {
            it?.isEmpty() == false
        }?.apply {
            this.forEachIndexed { index, template ->
                if (template is ButtonTemplate) {
                    when (template.style) {
                        "Primary" -> { buttons.put(1, template) }
                        "Secondary" -> { buttons.put(0, template) }
                    }
                }
            }
        }
        buttonActions = buttons
        _uiState.value = FormTemplateUIState.LoadingSuccess(title, titleIcon, options, buttons)
    }

    fun updateSelectedList(selections: List<String>) {
        if (selections.isEmpty()) {
            _uiState.value = FormTemplateUIState.SubmitDisabled
        } else {
            selectedOptions.apply {
                clear()
                addAll(selections)
                _uiState.value =
                    FormTemplateUIState.SubmitEnabled(selectedOptions)
            }
        }
    }
    fun getSelectedOptions() = selectedOptions
}