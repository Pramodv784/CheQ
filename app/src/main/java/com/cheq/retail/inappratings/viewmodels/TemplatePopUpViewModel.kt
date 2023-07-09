package com.cheq.retail.inappratings.viewmodels

import androidx.lifecycle.ViewModel
import com.cheq.retail.inappratings.models.uimodels.PopUpTemplate
import com.cheq.retail.inappratings.models.uimodels.Template

/**
 * Created by Sanket Mendon on 15,June,2023.
 * sanket@cheq.one
 */

class TemplatePopUpViewModel() : ViewModel() {
    private var uiTemplate: PopUpTemplate? = null
    private var buttons: HashMap<Int, Template> = hashMapOf()

    fun setUiTemplate(template: PopUpTemplate) {
        uiTemplate = template
        setButtonActions()
    }

    private fun setButtonActions() {
        val actions = uiTemplate?.actions
        actions.takeIf {
            it?.isEmpty() == false
        }?.apply {
            this.forEachIndexed { index, template ->
                buttons.put(index, template)
            }
        }
    }

    fun getButtonAction(id: Int): Template? = buttons.get(id)?.action
    fun uiTemplate() = uiTemplate
}