package com.cheq.retail.utils

import android.os.Build
import android.text.Html
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import java.text.MessageFormat
import java.util.*

object TextUtils {
    fun Double.toWords(language: String, country: String): String {
        val formatter = MessageFormat(
            "{0,spellout,currency}",
            Locale(language, country)
        )
        return formatter.format(arrayOf(this))
    }
}

fun TextView.setAsHtml(htmlText: String) {
    text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(htmlText, Html.FROM_HTML_MODE_COMPACT)
    } else {
        Html.fromHtml(htmlText)
    }
}

fun EditText.disableCopyPaste() {
    this.isLongClickable = false
    this.setTextIsSelectable(false)
    this.customInsertionActionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(p0: ActionMode?, p1: Menu?): Boolean {
            return false
        }

        override fun onPrepareActionMode(p0: ActionMode?, p1: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(p0: ActionMode?, p1: MenuItem?): Boolean {
            return false
        }

        override fun onDestroyActionMode(p0: ActionMode?) {
        }
    }
}
