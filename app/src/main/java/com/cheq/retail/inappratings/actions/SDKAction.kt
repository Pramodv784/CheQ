package com.cheq.retail.inappratings.actions

import android.content.Context


/**
 * Created by Sanket Mendon on 23,June,2023.
 * sanket@cheq.one
 */

class SDKActionImpl(ctx: Context, type: SDKActionType) : BaseAction {
    private val context: Context = ctx
    private val actionType: SDKActionType = type

    override fun execute() {
        when (actionType) {
            SDKActionType.FRESHCHAT -> FreshChatAction(context).execute()
        }
    }
}
enum class SDKActionType {
    FRESHCHAT
}