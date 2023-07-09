package com.cheq.retail.utils

import android.content.Context
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.airbnb.lottie.LottieAnimationView
import com.cheq.retail.R
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.google.android.material.bottomsheet.BottomSheetDialog


class BottomSheetUtils {
    companion object {
        lateinit var bottomSheetDialog: BottomSheetDialog

        fun showCommonBottomSheet(
            context: Context,
            title: String,
            message: String,
            showCloseButton: Boolean,
            showRetryButton: Boolean,
            showAnimation: Boolean,
            showFailureAnimation : Boolean,
            showProcessingAnimation : Boolean
        ) {

            if (showCloseButton) {
                bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialog2)

            } else {
                bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialog)
            }

            bottomSheetDialog.setContentView(R.layout.bottom_sheet_common)
            bottomSheetDialog.setCancelable(false)

            val tvTitle = bottomSheetDialog.findViewById<AppCompatTextView>(R.id.bs_tv_title)
            val tvMessage = bottomSheetDialog.findViewById<AppCompatTextView>(R.id.bs_tv_message)
            val tvRetryButton = bottomSheetDialog.findViewById<AppCompatButton>(R.id.bs_btn_retry)
            val llAnimation = bottomSheetDialog.findViewById<LinearLayoutCompat>(R.id.llAnimation)
            val llCreditCard = bottomSheetDialog.findViewById<LinearLayoutCompat>(R.id.llCreditCard)
            val doNotPressClose = bottomSheetDialog.findViewById<AppCompatTextView>(R.id.doNotPressClose)
            val animationTwo = bottomSheetDialog.findViewById<LottieAnimationView>(R.id.animationTwo)
            val animationFailed = bottomSheetDialog.findViewById<LottieAnimationView>(R.id.animationFailed)
            val animationProcessing = bottomSheetDialog.findViewById<LottieAnimationView>(R.id.animationProcessing)
            val tvCloseImageView =
                bottomSheetDialog.findViewById<AppCompatImageView>(R.id.bs_iv_cancel)
            tvTitle!!.text = title
            tvMessage!!.text = message

            if (!showCloseButton) {
                tvCloseImageView!!.visibility = View.GONE
                doNotPressClose!!.visibility = View.VISIBLE
            }else{
                doNotPressClose!!.visibility = View.GONE
            }

            if (!showRetryButton) {
                tvRetryButton!!.visibility = View.GONE
            }else{
                tvRetryButton!!.visibility = View.VISIBLE
            }
            if (showAnimation) {
                llAnimation!!.visibility = View.VISIBLE
                llCreditCard!!.visibility = View.GONE
            } else {
                llAnimation!!.visibility = View.GONE
                llCreditCard!!.visibility = View.VISIBLE
            }
            if (showFailureAnimation){
                animationFailed?.visibility = View.VISIBLE
                animationTwo?.visibility = View.GONE
            }else{
                animationFailed?.visibility = View.GONE
                animationTwo?.visibility = View.VISIBLE
            }

            if (showProcessingAnimation){
                animationProcessing?.visibility = View.VISIBLE
                animationFailed?.visibility = View.GONE
                animationTwo?.visibility = View.GONE
            }else{
                animationFailed?.visibility = View.GONE
                animationTwo?.visibility = View.GONE
                animationProcessing?.visibility = View.GONE
            }
            tvCloseImageView!!.setOnClickListener {
                bottomSheetDialog.dismiss()
                if (title.equals("Card Activation failed")) {
                    MparticleUtils.logEvent(
                        "CC_Activation_Failed_BackClicked",
                        "User chooses to exit the activation flow by clicking cancel on the failed screen\n",
                        "Unique",
                        "Back",
                        SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Activation_Failed_BackClicked),
                        context
                    )
                } else {
                    MparticleUtils.logEvent(
                        "CC_Payment_Failed_BackClicked",
                        "User goes back to previous screen by cancelling the review screen",
                        "Unique",
                        "Back",
                        SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Failed_BackClicked),
                        context
                    )
                }
            }

            tvRetryButton!!.setOnClickListener {
                bottomSheetDialog.dismiss()
                if (title.equals("Card Activation failed")) {
                    MparticleUtils.logEvent(
                        "CC_Activation_Retry",
                        "User chooses to retry CC activation on failure\n",
                        "Unique",
                        "Continue",
                        SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Activation_Retry),
                        context
                    )
                }


                else {
                    MparticleUtils.logEvent(
                        "CC_Payment_Retry",
                        "User chooses to retry credit card payment on failure",
                        "Unique",
                        "Continue",
                        SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Retry),
                        context
                    )
                }

            }

            bottomSheetDialog.show()
        }

        fun hideBottomSheet() {
            if (bottomSheetDialog != null && bottomSheetDialog.isShowing) {
                bottomSheetDialog.dismiss()
            }
        }

    }
}