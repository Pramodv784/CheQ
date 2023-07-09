package com.cheq.common.freshchat

import android.content.Context
import com.cheq.cache.sharedprefs.SharedPrefs
import com.cheq.cache.sharedprefs.SharedPrefsCheQ
import com.cheq.cache.sharedprefs.SharedPrefsConstants
import com.cheq.common.utils.Constants.PHONE_NUMBER_PREFIX
import com.freshchat.consumer.sdk.Freshchat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


/**
 * Created by Akash Khatkale on 28th May, 2023.
 * akash.k@cheq.one
 */
class FreshChatImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @SharedPrefsCheQ private val sharedPrefs: SharedPrefs
) : FreshChat {

    init {
        createFreshChatUser()
    }

    companion object {
        private const val FRESH_CHAT_USER_ID = "cf_cheq_user_id"
    }

    private fun createFreshChatUser() {
        sharedPrefs.apply {
            val firstName = getString(SharedPrefsConstants.FIRST_NAME)
            val lastName = getString(SharedPrefsConstants.LAST_NAME)
            val mobile = getString(SharedPrefsConstants.MOBILE_NUMBER)
            val email = getString(SharedPrefsConstants.USER_EMAIL)
            val userId = getString(SharedPrefsConstants.CHEQ_USER_ID)
            val freshChatUser = Freshchat.getInstance(context).user.apply {
                setFirstName(firstName)
                setLastName(lastName)
                setEmail(email)
                setPhone(PHONE_NUMBER_PREFIX, mobile)
            }
            val userMeta: MutableMap<String, String> = HashMap()
            userMeta[FRESH_CHAT_USER_ID] = userId
            Freshchat.getInstance(context).apply {
                user = freshChatUser
                setUserProperties(userMeta)
            }
        }
    }

    override fun showConversation() {
        Freshchat.showConversations(context)
    }
}