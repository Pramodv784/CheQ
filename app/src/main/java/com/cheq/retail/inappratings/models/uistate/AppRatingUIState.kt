package com.cheq.retail.inappratings.models.uistate

import com.cheq.retail.inappratings.models.response.Action


/**
 * Created by Sanket Mendon on 19,June,2023.
 * sanket@cheq.one
 */
sealed class AppRatingUIState {
    object Loading : AppRatingUIState()

    object NoPrompt : AppRatingUIState()

    data class ShowPrompt(
        val ratingPrompt: Action?
    ) : AppRatingUIState()

    data class Error(
        val message: String
    ) : AppRatingUIState()
}
