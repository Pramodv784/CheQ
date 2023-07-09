package com.cheq.retail.inappratings.models.uimodels

import kotlinx.parcelize.Parcelize


/**
 * Created by Sanket Mendon on 21,June,2023.
 * sanket@cheq.one
 */
@Parcelize
class PopUpTemplate(
    override val type: String?,
    override val title: String?,
    val titleIcon: String?,
    val lottieFile: String?,
    override val action: Template?,
    override val actions: List<Template>?
) : Template() {

    fun buttons(): List<ButtonTemplate> {
        val listOfButtons: MutableList<ButtonTemplate> = mutableListOf()
        if (this.actions?.isEmpty() == false) {
            for (item in this.actions) {
                if (item is ButtonTemplate) listOfButtons.add(item)
            }
        }
        return listOfButtons
    }

    fun isSingleAction(): Boolean {
        return this.actions?.isEmpty() == false && this.actions.size == 1
    }
}