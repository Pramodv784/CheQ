package com.cheq.retail.ui.billPayments.custom_view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import android.R

class CopyEditText : AppCompatEditText {
    var listeners: ArrayList<GoEditTextListener>

    constructor(context: Context) : super(context) {
        listeners = ArrayList()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        listeners = ArrayList()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        listeners = ArrayList()
    }

    fun addListener(listener: GoEditTextListener) {
        try {
            listeners.add(listener)
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    /**
     * Here you can catch paste, copy and cut events
     */
    override fun onTextContextMenuItem(id: Int): Boolean {
        val consumed = super.onTextContextMenuItem(id)
        when (id) {
            R.id.cut -> onTextCut()
            R.id.paste -> onTextPaste()
            R.id.copy -> onTextCopy()
        }
        return consumed
    }

    fun onTextCut() {}
    fun onTextCopy() {}

    /**
     * adding listener for Paste for example
     */
    fun onTextPaste() {
        for (listener in listeners) {
            listener.onUpdate()
        }
    }
}

interface GoEditTextListener {
    fun onUpdate();
}