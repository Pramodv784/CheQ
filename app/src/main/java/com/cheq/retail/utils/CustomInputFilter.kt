package com.cheq.retail.utils

import android.text.InputFilter
import android.text.Spanned
import java.util.regex.Pattern

class CustomInputFilter(var pattern: Pattern) : InputFilter {



    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val matcher = pattern.matcher(source)
        print(matcher.matches())
        return if (matcher.find()) {
            null
        } else {
            ""
        }
    }
}