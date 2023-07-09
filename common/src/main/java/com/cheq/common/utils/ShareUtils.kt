package com.cheq.common.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.cheq.common.R
import com.cheq.common.extension.showToast
import java.io.BufferedOutputStream
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

/**
 * Created by Akash Khatkale on 20th May, 2023.
 * akash.k@cheq.one
 */
object ShareUtils {

    enum class ShareType(val packageName: String) {
        SHARE_WHATSAPP("com.whatsapp"),
        GENERAL(""),
    }

    fun shareImage(context: Context, bitmap: Bitmap, message: String) {
        val contentUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        val contentResolver = context.contentResolver
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "filename")
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);
        val imageContentUri = contentResolver.insert(contentUri, contentValues)
        try {
            imageContentUri?.let {
                contentResolver.openFileDescriptor(imageContentUri, "w", null)
                    .use { fileDescriptor ->
                        val fd: FileDescriptor = fileDescriptor!!.fileDescriptor
                        val outputStream: OutputStream = FileOutputStream(fd)
                        val bufferedOutputStream = BufferedOutputStream(outputStream)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bufferedOutputStream)
                        bufferedOutputStream.flush()
                        bufferedOutputStream.close()
                    }
            }
        } catch (e: IOException) {
            Log.d("ShareUtils", "Error happening while sharing image: $e")
        }

        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_STREAM, imageContentUri)
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            message
        )
        sendIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        sendIntent.type = "image/*"
        val shareIntent = Intent.createChooser(sendIntent, "Share with")
        context.startActivity(shareIntent)
    }

    fun genericShare(
        context: Context,
        message: String,
        type: ShareType,
        errorMessage: Int = R.string.try_again
    ) {
        try {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                message
            )
            sendIntent.type = "text/plain"
            when (type) {
                ShareType.GENERAL -> Unit
                else -> {
                    sendIntent.setPackage(type.packageName)
                }
            }
            context.startActivity(sendIntent)
        } catch (exception: Exception) {
            context.showToast(context.getString(errorMessage))
        }
    }

}