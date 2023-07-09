package com.cheq.navigation

/**
 * Created by Akash Khatkale on 21st May, 2023.
 * akash.k@cheq.one
 */
sealed class IntentKey {
    object MainActivityKey : IntentKey()

    data class MyAccountActivityKey(
        val cheqSafe: Boolean,
        val startDestination: String = PROFILE_DESTINATION,
        val goToHomeOnBack: Boolean = false
    ) : IntentKey() {
        companion object {
            const val PROFILE_DESTINATION = "PROFILE_DESTINATION"
            const val REFER_EARN_DESTINATION = "REFER_EARN_DESTINATION"
        }
    }

    data class GenericWebViewActivityKey(
        val url: String
    ) : IntentKey()

    object ExistingUserActivityKey : IntentKey()

    data class SplashActivityKey(
        val newTask: Boolean
    ) : IntentKey()
}