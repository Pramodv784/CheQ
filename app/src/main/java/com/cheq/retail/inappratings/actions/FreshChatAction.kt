package com.cheq.retail.inappratings.actions

import android.content.Context
import com.cheq.retail.application.MainApplication
import com.cheq.retail.sharePreferences.SharePrefs
import com.freshchat.consumer.sdk.Freshchat


/**
 * Created by Sanket Mendon on 23,June,2023.
 * sanket@cheq.one
 */

const val FIRST_NAME = "FIRST_NAME"
const val LAST_NAME = "LAST_NAME"
const val USER_EMAIL = "USER_EMAIL"
const val MOBILE_NUMBER = "MOBILE_NUMBER"
const val CHEQ_USER_ID = "CHEQ_USER_ID"
const val COUNTRY_CODE = "+91"
const val CHEQ_FRESH_CHAT_USER_ID = "cf_cheq_user_id"

class FreshChatAction(ctx: Context) : BaseAction {
    private val context: Context = ctx
    private val sharedPrefs: SharePrefs? =
        SharePrefs.getInstance(MainApplication.getApplicationContext())

    init {
        createFreshchatUser()
    }

    private fun createFreshchatUser() {
        sharedPrefs?.apply {
            val firstName = getString(FIRST_NAME)
            val lastName = getString(LAST_NAME)
            val mobile = getString(MOBILE_NUMBER)
            val email = getString(USER_EMAIL)
            val userId = getString(CHEQ_USER_ID)
            val freshChatUser = Freshchat.getInstance(context).user.apply {
                setFirstName(firstName)
                setLastName(lastName)
                setEmail(email)
                setPhone(COUNTRY_CODE, mobile)
            }
            val userProperties = HashMap<String, String>()
            userProperties[CHEQ_FRESH_CHAT_USER_ID] = userId.toString()
            Freshchat.getInstance(context).apply {
                user = freshChatUser
                setUserProperties(userProperties)
            }
        }
    }

    override fun execute() {
        Freshchat.showConversations(context)
    }
}