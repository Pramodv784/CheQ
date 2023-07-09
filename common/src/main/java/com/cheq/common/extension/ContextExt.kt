package com.cheq.common.extension

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by Akash Khatkale on 20th May, 2023.
 * akash.k@cheq.one
 */
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, message, duration).show()
}

fun Context?.copyClipboard(
    label: String,
    message: String
) = try {
    val clipboard = this?.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as? ClipboardManager
    val clip = ClipData.newPlainText(label, message)
    clipboard?.setPrimaryClip(clip)
} catch (exception: Exception) {
    Log.d("ContextExt", "Error in copying to clipboard: $exception")
}
