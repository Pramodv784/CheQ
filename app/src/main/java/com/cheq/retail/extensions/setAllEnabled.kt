package com.cheq.retail.extensions

import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children

fun View.setAllEnabled(enabled: Boolean) {
    isEnabled = enabled
    if (this is ViewGroup) children.forEach { child -> child.setAllEnabled(enabled) }
}

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()