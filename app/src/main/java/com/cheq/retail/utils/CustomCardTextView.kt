package com.cheq.retail.utils

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.cheq.retail.R


class CustomCardTextView(context: Context, attributeSet: AttributeSet) :
    LinearLayout(context, attributeSet) {
    private var hint: String? = null
    var tv: TextView? = null
    private var ll_layout: ImageView? = null
        set(value) {
            field = value
            // We assign the prefix using the assignPrefix method in PrefixEditText.kt
            tv?.hint = hint
        }

    init {
        inflate(context, R.layout.cuatom_card_edit_text, this)
        tv = findViewById(R.id.tv)
        ll_layout = findViewById(R.id.ll_layout)


        context.obtainStyledAttributes(attributeSet, R.styleable.PrefixLayout).let {

            hint = it.getString(R.styleable.PrefixLayout_hint).orEmpty()
            it.recycle()
        }
    }
}