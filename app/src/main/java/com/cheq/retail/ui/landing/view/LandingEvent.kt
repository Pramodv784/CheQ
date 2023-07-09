package com.cheq.retail.ui.landing.view

import com.cheq.retail.ui.landing.model.LandingProduct

sealed class LandingEvent {
    object DoExploreCheq : LandingEvent()
}
