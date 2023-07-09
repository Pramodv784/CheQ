package com.cheq.retail.utils

import android.content.Context
import android.content.res.Resources.getSystem
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.core.content.ContextCompat
import com.cheq.retail.utils.Utils.Companion.toPx
import com.google.firebase.FirebaseException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase


class GradientUtils {
    companion object {
        fun setBackGroundGradient(
            startColor: String, middleColor: String, endColor: String, gradient: View
        ) {
            try {
                val gradientDrawableBack = GradientDrawable(

                    GradientDrawable.Orientation.BL_TR, intArrayOf(
                        Color.parseColor("#$middleColor"),
                        Color.parseColor("#$endColor"),
                        Color.parseColor("#$middleColor"),
                    )
                )

                gradientDrawableBack.mutate()
                gradientDrawableBack.apply {
                    setGradientCenter(gradient.width.toFloat(), 0f)
                    cornerRadius = Utils.dpToFloat(12)
                    gradientType = GradientDrawable.LINEAR_GRADIENT
                    gradient.background = this
                }
            } catch (e: Exception) {

            }
        }

        fun setBoarderStroke(
            cardColor: String, opacity_border: String, isBorder: Boolean, gradient: View
        ) {
            try {
                val newColor = setColorAlpha(30, cardColor)
                println("new color $newColor")
                val gradientDrawableBack = GradientDrawable(

                    GradientDrawable.Orientation.BL_TR, intArrayOf(
                        Color.parseColor(newColor), Color.parseColor(newColor)


                    )
                )
                gradientDrawableBack.setStroke(6, Color.parseColor(newColor))
                gradientDrawableBack.mutate()
                gradientDrawableBack.apply {
                    setGradientCenter(gradient.width.toFloat(), 0f)
                    cornerRadius = Utils.dpToFloat(13)
                    gradientType = GradientDrawable.LINEAR_GRADIENT
                    gradient.background = this
                }
            } catch (e: Exception) {

            }
        }

        fun setBackGround(
            cardColor: String,
            opacity_border: String,
            opacity_topLeft: String,
            opacity_bottomRight: String,
            gradient: View
        ) {
            try {

                val newColor = setColorAlpha(0, cardColor)
                val newColor2 = setColorAlpha(12, cardColor)
                println("new color $newColor new color2 $newColor2")
                val gradientDrawableBack = GradientDrawable(

                    GradientDrawable.Orientation.BL_TR, intArrayOf(
                        Color.parseColor(newColor),
                        Color.parseColor(newColor2),

                        )
                )

                gradientDrawableBack.mutate()
                gradientDrawableBack.apply {
                    setGradientCenter(gradient.width.toFloat(), 0f)
                    cornerRadius = Utils.dpToFloat(12)
                    gradientType = GradientDrawable.LINEAR_GRADIENT
                    gradient.background = this
                }
            } catch (e: Exception) {

            }
        }

        fun setBackGroundSolid(
            cardColor: Int,
            gradient: View
        ) {
            try {

                val gradientDrawableBack = GradientDrawable(

                    GradientDrawable.Orientation.BL_TR, intArrayOf(
                        cardColor,
                        cardColor
                    )
                )

                gradientDrawableBack.mutate()
                gradientDrawableBack.apply {
                    setGradientCenter(gradient.width.toFloat(), 0f)
                    cornerRadius = Utils.dpToFloat(14)
                    gradientType = GradientDrawable.LINEAR_GRADIENT
                    gradient.background = this
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }

        fun setColorAlpha(percentage: Int, colorCode: String): String? {
            val decValue = percentage.toDouble() / 100 * 255
            val rawHexColor = colorCode.replace("#", "")
            val str = StringBuilder(rawHexColor)
            if (Integer.toHexString(decValue.toInt()).length == 1) str.insert(
                0, "#0" + Integer.toHexString(decValue.toInt())
            ) else str.insert(0, "#" + Integer.toHexString(decValue.toInt()))
            return str.toString()
        }

    }

}
