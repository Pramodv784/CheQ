package com.cheq.retail.ui.rewards.adapter

import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cheq.retail.R
import com.cheq.retail.databinding.ItemMyVoucherLayoutBinding
import com.cheq.retail.extensions.voucherBaseUrl
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.ui.main.model.VouchersHistoryItem
import com.cheq.retail.utils.DateTimeUtils
import java.text.SimpleDateFormat
import java.util.*

class MyVoucherAdapter(
    private val dummyList: ArrayList<VouchersHistoryItem>?,
    private val context: Context?,
    private val rewardsInterface: OnItemClick
) : RecyclerView.Adapter<MyVoucherAdapter.MyVoucherViewHolder>() {

    private var prefs: SharePrefs? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyVoucherViewHolder {
        prefs = context?.let { SharePrefs.getInstance(it) }
        val inflater = LayoutInflater.from(context)
        val binding = ItemMyVoucherLayoutBinding.inflate(inflater, parent, false)
        return MyVoucherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyVoucherViewHolder, position: Int) {
        val data = dummyList?.get(position)
        val colorMatrix = ColorMatrix()
        holder.binding.tvAmount.text = "₹${data?.amount.toString()}"

        val c = Calendar.getInstance().time

        val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = df.format(c)
        val remainingDays = DateTimeUtils.getDaysBetweenDates(
            formattedDate, data?.voucherExp?.replaceRange(
                10, data.voucherExp.length, ""
            )
        ).toString().toInt()
        if (remainingDays > 1) {
            colorMatrix.setSaturation(1f)

            context?.let {
                holder.binding.tvAmount.setTextColor(
                    ContextCompat.getColor(
                        it,
                        R.color.colornaturals
                    )
                )
                holder.binding.ivBackGround.setImageDrawable(
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.ic_my_vouchers_active
                    )
                )
            }

            if (remainingDays == 1) {
                holder.binding.tvExpires.text = context?.getString(R.string.expires_in_day, "$remainingDays")
            } else {
                holder.binding.tvExpires.text = context?.getString(R.string.expires_in_days, "$remainingDays")
            }
            holder.itemView.setOnClickListener {
                rewardsInterface.onMyVoucherClicked(true, data)
            }
        } else {
            colorMatrix.setSaturation(0f)
            context?.let {
                holder.binding.tvAmount.setTextColor(
                    ContextCompat.getColor(
                        it,
                        R.color.textColorExpired
                    )
                )
                holder.binding.ivBackGround.setImageDrawable(
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.ic_my_vouchers_in_active
                    )
                )
            }

            holder.binding.tvExpires.text = context?.getString(R.string.expired)
        }
        holder.itemView.setOnClickListener {
            rewardsInterface.onMyVoucherClicked(true, data)
        }
        if (context != null) {
            Glide.with(context).load("${prefs?.voucherBaseUrl}${data?.cheqBrands?.brandLogo}")
                .into(holder.binding.ivMerchantLogo)
        }
        holder.binding.ivMerchantLogo.colorFilter = ColorMatrixColorFilter(colorMatrix)

    }

    override fun getItemCount(): Int {
        return dummyList?.size ?: 0
    }

    inner class MyVoucherViewHolder(val binding: ItemMyVoucherLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    interface OnItemClick {
        fun onMyVoucherClicked(isClicked: Boolean, data: VouchersHistoryItem?)
    }

}
