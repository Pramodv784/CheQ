package com.cheq.retail.inappratings.mappers

import com.cheq.retail.inappratings.models.response.Action
import com.cheq.retail.inappratings.models.uimodels.*


/**
 * Created by Sanket Mendon on 20,June,2023.
 * sanket@cheq.one
 */

const val TYPE_POPUP = "POPUP"
const val TYPE_FORM = "FORM"
const val TYPE_BUTTON = "BUTTON"
const val TYPE_S2S = "S2S"
const val TYPE_REDIRECTION = "REDIRECTION"
const val TYPE_INTENT = "INTENT"
const val TYPE_SDK = "SDK"

fun Action.toUITemplate(): Template? {
    return when (this.type) {
        TYPE_POPUP -> PopUpTemplate(
            type = this.type,
            title = this.title,
            titleIcon = this.titleIcon,
            lottieFile = this.lottieFile,
            action = if (this.action != null) this.action.toUITemplate() else null,
            actions = this.actions?.toListOfUITemplates()
        )
        TYPE_FORM -> FormTemplate(
            type = this.type,
            title = this.title,
            titleIcon = this.titleIcon,
            options = this.options,
            showCustomFeedbackOption = this.showCustomFeedbackOption,
            action = if (this.action != null) this.action.toUITemplate() else null,
            actions = this.actions?.toListOfUITemplates()
        )
        TYPE_BUTTON -> ButtonTemplate(
            type = this.type,
            title = this.title,
            titleIcon = this.titleIcon,
            style =  this.style,
            action = if (this.action != null) this.action.toUITemplate() else null,
            actions = this.actions?.toListOfUITemplates()
        )
        TYPE_S2S -> S2STemplate(
            type = this.type,
            postUrl = this.postUrl,
            keys = this.keys,
        )
        TYPE_REDIRECTION -> RedirectionTemplate(
            type = this.type,
            url = this.url
        )
        TYPE_INTENT -> IntentTemplate(
            type = this.type
        )
        TYPE_SDK -> SDKTemplate(
            type = this.type,
            url = this.url
        )
        else -> { null }
    }
}

fun List<Action?>?.toListOfUITemplates(): List<Template> {
    val templatesList: MutableList<Template> = mutableListOf()
    if (this != null) {
        for (action in this) {
            if (action != null) {
                action.toUITemplate()?.let { templatesList.add(it) }
            }
        }
    }
    return templatesList
}
