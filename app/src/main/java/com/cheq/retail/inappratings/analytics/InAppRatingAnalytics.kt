package com.cheq.retail.inappratings.analytics

import com.mparticle.MPEvent
import com.mparticle.MParticle


/**
 * Created by Sanket Mendon on 25,June,2023.
 * sanket@cheq.one
 */
class InAppRatingAnalytics {

    companion object {
        private val userProperties = mutableMapOf<String, String>()

        const val CHEQ_USER_ID = "cheqUserId"
        const val TOUCHPOINT = "touchpoint"
        const val SCREEN_RATING_FILTER = "/rating-filter"
        const val SCREEN_RATING_FILTER_REDIRECT = "/rating-filter/redirect"
        const val SCREEN_RATING_FILTER_BADRATING = "/rating-filter/bad-rating"
        const val EVENT_RATING_YES = "RatingFilter_Yes_Clicked"
        const val EVENT_RATING_NO = "RatingFilter_No_Clicked"
        const val EVENT_RATING_CTA_CLICKED = "RatingRedirectCTA_Clicked"
        const val EVENT_BAD_RATING_SUBMIT = "BadRating_Submit_Clicked"
        const val EVENT_BAD_RATING_HELP = "BadRating_GetHelp_Clicked"
        const val KEY_RATING = "rating"
        const val KEY_FEEDBACK = "feedback"

        fun setUserProperties(cheqUserId: String, touchpoint: String) {
            userProperties[CHEQ_USER_ID] = cheqUserId
            userProperties[TOUCHPOINT] = touchpoint
        }

        fun trackScreen(screen: String) {
            MParticle.getInstance()?.logScreen(screen, userProperties)
        }

        fun trackEvent(eventName: String, map: HashMap<String, Any>? = null) {
            val metadata = mutableMapOf<String, Any>()
            metadata.putAll(userProperties)
            if (map?.isNotEmpty() == true) {
                metadata.putAll(map)
            }
            val event = MPEvent.Builder(eventName, MParticle.EventType.Other)
                .customAttributes(metadata)
                .build()
            MParticle.getInstance()?.logEvent(event)
        }
    }
}