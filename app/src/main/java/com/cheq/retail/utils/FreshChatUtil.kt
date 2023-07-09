package com.cheq.retail.utils

import android.content.Context
import com.freshchat.consumer.sdk.Freshchat


class FreshChatUtil {
    companion object {
        fun createFreshChatUser(context: Context,firstName: String, lastName: String, email: String, phone: String,checkUserId: String) {

            val freshchatUser = Freshchat.getInstance(context).user
            freshchatUser.setFirstName(firstName)
            freshchatUser.setLastName(lastName)
            freshchatUser.setEmail(email)
            freshchatUser.setPhone("+91", phone)
            Freshchat.getInstance(context).setUser(freshchatUser)
            val userMeta: MutableMap<String, String> = HashMap()
            userMeta["cf_cheq_user_id"] = checkUserId
            Freshchat.getInstance(context).setUserProperties(userMeta)

        }


    }
}