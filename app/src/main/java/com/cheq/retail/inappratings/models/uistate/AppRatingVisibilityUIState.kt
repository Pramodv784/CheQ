package com.cheq.retail.inappratings.models.uistate

import com.cheq.retail.inappratings.models.response.Action


/**
 * Created by Sanket Mendon on 19,June,2023.
 * sanket@cheq.one
 */
sealed class AppRatingVisibilityUIState {
    object Loading : AppRatingVisibilityUIState()
    data class ShowRating(
        val ratingPrompt: Action?
    ) : AppRatingVisibilityUIState()

    object RatingShown : AppRatingVisibilityUIState()
    object ShowReferral : AppRatingVisibilityUIState()
}
