package com.cheq.common.utils

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi

/**
 * Created by Akash Khatkale on 18th May, 2023.
 * akash.k@cheq.one
 */
object UiUtils {

    private const val GRADIENT_STROKE = 6
    private const val GRADIENT_PERCENTAGE = 30
    private const val BORDER_RADIUS = 13
    private const val GRADIENT_START_PERCENTAGE = 6
    private const val GRADIENT_END_PERCENTAGE = 12

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun setBorderStroke(
        cardColor: String,
        gradient: View
    ) {
        try {
            val newColor = setColorAlpha(GRADIENT_PERCENTAGE, cardColor)
            val gradientDrawableBack = GradientDrawable(
                GradientDrawable.Orientation.BL_TR, intArrayOf(
                    Color.parseColor(newColor), Color.parseColor(newColor)
                )
            )
            gradientDrawableBack.setStroke(GRADIENT_STROKE, Color.parseColor(newColor))
            gradientDrawableBack.mutate()
            gradientDrawableBack.apply {
                setGradientCenter(gradient.width.toFloat(), 0f)
                cornerRadius = dpToFloat(BORDER_RADIUS)
                gradientType = GradientDrawable.LINEAR_GRADIENT
                gradient.background = this
            }
        } catch (e: Exception) {
            error(e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun setBackGround(
        cardColor: String,
        gradient: View
    ) {
        try {
            val startColor = setColorAlpha(GRADIENT_START_PERCENTAGE, cardColor)
            val endColor = setColorAlpha(GRADIENT_END_PERCENTAGE, cardColor)
            val gradientDrawableBack = GradientDrawable(
                GradientDrawable.Orientation.BL_TR, intArrayOf(
                    Color.parseColor(startColor),
                    Color.parseColor(endColor),
                )
            )

            gradientDrawableBack.mutate()
            gradientDrawableBack.apply {
                setGradientCenter(gradient.width.toFloat(), 0f)
                cornerRadius = dpToFloat(12)
                gradientType = GradientDrawable.LINEAR_GRADIENT
                gradient.background = this
            }
        } catch (e: Exception) {
            error(e)
        }
    }

    fun getBitmapFromView(view: View): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) bgDrawable.draw(canvas) else canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return returnedBitmap
    }

    private fun setColorAlpha(percentage: Int, colorCode: String): String {
        val decValue = percentage.toDouble() / 100 * 255
        val rawHexColor = colorCode.replace("#", "")
        val str = StringBuilder(rawHexColor)
        if (Integer.toHexString(decValue.toInt()).length == 1) str.insert(
            0, "#0" + Integer.toHexString(decValue.toInt())
        ) else str.insert(0, "#" + Integer.toHexString(decValue.toInt()))
        return str.toString()
    }

    fun dpToFloat(dp: Int): Float {
        val scale = Resources.getSystem().displayMetrics.density
        return (dp * scale)
    }

}