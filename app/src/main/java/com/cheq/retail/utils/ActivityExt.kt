package com.cheq.retail.utils

import android.app.Activity
import android.content.Intent
import androidx.core.app.NavUtils

fun Activity.navigateToParent() {
    NavUtils.getParentActivityIntent(this)?.let { upIntent ->
        if (NavUtils.shouldUpRecreateTask(this, upIntent) || isTaskRoot) {
            upIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(upIntent)
        } else {
        }
    }
}