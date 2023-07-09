package com.cheq.retail.ui.main.helper

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import com.cheq.retail.R
import com.cheq.retail.databinding.LayoutCustomToastBinding
import com.cheq.retail.ui.billSummary.adapter.NetEarningAdapter
import com.cheq.retail.ui.billSummary.model.InfoPopupItem


object PopupHelper {
    var popUpWindow: PopupWindow? = null
    fun showPopupWindow(
        anchor: View,
        isToShow: Boolean,
        netEarning: Int? = null,
        rewardsPoint: Int?= null,
        rewardsRate: Double? = null,
        platformFee: Int? = null,
        gravityOne: Int,
        gravityTwo: Int,
        width: Int,
        height: Int,
        sizeWidth: Int,
        context: Context?,
        title: String? = null,
        caption: String? = null,
        infoPopUpItemList: List<InfoPopupItem?>? = null,
        onItemClick: ((Boolean?) -> Unit)? = null,
    ) {
        popUpWindow = PopupWindow(context)
        val inflater = LayoutInflater.from(anchor.context)
        popUpWindow?.isOutsideTouchable = true
        popUpWindow?.isFocusable = true
        val binding = LayoutCustomToastBinding.inflate(LayoutInflater.from(context))
        popUpWindow?.contentView = binding.root

        binding.root.apply {
            measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
        }
        popUpWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        popUpWindow?.animationStyle =
            if (isToShow) R.style.netEarningAnimation else R.style.processingFeeAnimation
        binding.tvTitle.setOnClickListener {
            popUpWindow?.dismiss()
        }
        binding.tvCaption.text = caption
        binding.tvTitle.text = title
        popUpWindow?.setOnDismissListener {
            onItemClick?.invoke(true)
        }
        if (isToShow) {
            binding.tvTitle.visibility = View.GONE
            binding.tvCaption.visibility = View.GONE
            binding.rvDetails.apply {
                visibility = View.VISIBLE
                adapter = NetEarningAdapter(context, infoPopUpItemList)

            }
            /*binding.llPeBreakOut.visibility = View.VISIBLE
            binding.tvRewardsPoint.text = context?.getString(R.string.plus_sign, rewardsPoint)
            platformFee?.let {
                if (it > 0) {
                    binding.llPlatformFee.visibility = View.VISIBLE
                    binding.tvPlatformFeeTitle.text =
                        FirebaseRemoteConfigUtils.getAdditionalFeesText()
                    binding.tvPlatformFee.text =
                        context?.getString(R.string.minus_sign, platformFee)
                } else {
                    binding.llPlatformFee.visibility = View.GONE
                }
            }
            binding.tvNetEarning.text = context?.getString(R.string.str_rs_sym, netEarning)
            binding.tvPotentialEarningTitle.text = FirebaseRemoteConfigUtils.getEarningsText()
            binding.tvChipsEarnAt.text = context?.getString(
                R.string.str_chips_earn_at,
                rewardsRate,
                context.getString(R.string.str_percentage_sign)
            )*/
        } else {
            binding.tvTitle.visibility = View.VISIBLE
            binding.tvCaption.visibility = View.VISIBLE
            binding.rvDetails.visibility = View.GONE
        }

        popUpWindow?.let {
            val size = Size(
                it.contentView.measuredWidth,
                it.contentView.measuredHeight
            )
            it.showAsDropDown(
                anchor,
                if (isToShow) -size.width + size.width/5 else 0,
                if (isToShow) -size.height * 2 + height else -size.height * 2,
                gravityOne and gravityTwo
            )
        }

    }
}