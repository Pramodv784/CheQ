package com.cheq.retail.inappratings.viewmodels

import androidx.lifecycle.ViewModel
import com.cheq.retail.inappratings.models.uimodels.ButtonTemplate
import com.cheq.retail.inappratings.models.uimodels.PopUpTemplate
import com.cheq.retail.inappratings.models.uimodels.SingleButtonPopupTemplate

/**
 * Created by Sanket Mendon on 15,June,2023.
 * sanket@cheq.one
 */

class TemplatePopUpSingleButtonViewModel() : ViewModel() {

    private var templateData: SingleButtonPopupTemplate? = null
    fun setUiTemplate(template: PopUpTemplate?) {
        val title = template?.title
        val titleIcon = template?.titleIcon
        val lottieAvailable = template?.lottieFile?.equals("inappRating", true) == true
        val buttonTemplate = template?.actions?.get(0) as ButtonTemplate
        val btnText = buttonTemplate?.title
        val btnIcon = buttonTemplate?.titleIcon
        val btnAction = buttonTemplate?.action
        templateData = SingleButtonPopupTemplate(
            title = title ?: "",
            titleIcon = titleIcon,
            lottieAvailable = lottieAvailable,
            btnText = btnText,
            btnIcon = btnIcon,
            btnAction = btnAction
        )
    }
    fun uiTemplate() = templateData
}