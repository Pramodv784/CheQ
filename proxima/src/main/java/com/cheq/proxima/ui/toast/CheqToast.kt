package com.cheq.proxima.ui.toast

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.cheq.proxima.R
import com.google.android.material.snackbar.Snackbar

/**
 * Created by Akash Khatkale on 31st May, 2023.
 * akash.k@cheq.one
 */
class CheqToast {
    fun showCheqSnackbar(
        parentLayout: View,
        context: Context,
        config: CheqToastConfig
    ) {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customSnackView: View =
            layoutInflater.inflate(R.layout.layout_cheq_snackbar, null).apply {
                findViewById<AppCompatTextView>(R.id.message_tv).text = config.message
                findViewById<AppCompatImageView>(R.id.icon_iv).apply {
                    setImageResource(config.startIcon ?: R.drawable.ic_check_circle)
                    isVisible = config.startIcon != null
                }
                findViewById<AppCompatButton>(R.id.accept_button).apply {
                    isVisible = config.acceptButton != null
                    text = config.acceptButton?.text ?: context.getString(R.string.accept)
                    setOnClickListener {
                        config.acceptButton?.onClick?.invoke()
                    }
                }
                findViewById<AppCompatButton>(R.id.dismiss_button).apply {
                    isVisible = config.dismissButton != null
                    text = config.dismissButton?.text ?: context.getString(R.string.cancel)
                    setOnClickListener {
                        config.dismissButton?.onClick?.invoke()
                    }
                }
            }
        val snackbar = Snackbar.make(parentLayout, "", Snackbar.LENGTH_LONG).apply {
            view.setBackgroundColor(Color.TRANSPARENT)
            (view as Snackbar.SnackbarLayout).apply {
                setPadding(0, 0, 0, 0)
                addView(customSnackView, 0)
            }
        }
        customSnackView.findViewById<AppCompatImageView>(R.id.cancel_iv).apply {
            setImageResource(config.endIcon ?: R.drawable.ic_close)
            isVisible = config.endIcon != null
            setOnClickListener {
                snackbar.dismiss()
            }
        }
        snackbar.show()
    }
}