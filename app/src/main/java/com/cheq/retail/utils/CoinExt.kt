package com.cheq.retail.utils

import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan

fun String.setImageSpan(replacements: Map<String, Drawable?>,align: Int) : CharSequence {
    val ss = SpannableString(this)

    replacements.forEach { entry ->

        val textToReplace = entry.key;
        val drawable = entry.value

        if(drawable != null) {
            drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

            val span = ImageSpan(drawable, align)

            val startIndex = ss.indexOf(textToReplace)

            if(startIndex >= 0) {
                ss.setSpan(span, startIndex, startIndex + textToReplace.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            }
        }
    }

    return ss
}