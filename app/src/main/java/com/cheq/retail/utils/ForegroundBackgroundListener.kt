package com.cheq.retail.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class ForegroundBackgroundListener : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun appStarted() {

        //Todo visibility: gone
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun appStopped() {

        //Todo visibility: visible
    }
}