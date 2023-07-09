package com.cheq.retail.ui.rewards.adapter

import android.content.Context
import android.content.Intent
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cheq.retail.R
import com.cheq.retail.databinding.VouchersItemListLayoutBinding
import com.cheq.retail.extensions.voucherBaseUrl
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.ui.main.model.VouchersItem

class AllVouchersAdapter(
    private val context: Context,
    private val voucherListResponses: ArrayList<VouchersItem>?,
    private val voucherInterface: VoucherInterface
) : RecyclerView.Adapter<AllVouchersAdapter.AllVouchersViewHolder>() {
    private var prefs: SharePrefs? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllVouchersViewHolder {
        prefs = SharePrefs.getInstance(context)
        val inflater = LayoutInflater.from(context)
        val binding = VouchersItemListLayoutBinding.inflate(inflater, parent, false)
        return AllVouchersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AllVouchersViewHolder, position: Int) {
        try {
            val data = voucherListResponses?.get(position)
            val coinBalance = SharePrefs.instance?.getInt(SharePrefsKeys.REWARDS_COIN_BALANCE)

            if (!data?.denominator.isNullOrEmpty()) {
                holder.binding.ivChip.visibility = View.VISIBLE
                holder.binding.tvConversionRate.text = "1 = ₹${data?.denominator}"
            } else {
                holder.binding.ivChip.visibility = View.VISIBLE
                holder.binding.tvConversionRate.text = "1 = ₹1.00"
            }

            if (data?.minValue != null && coinBalance != null) {
                val chipToRupee = ((coinBalance * (data?.denominator?.toDouble() ?: 1.00)))
                val amountNeeded =
                    (data?.minValue.toString()
                        .toDouble() - chipToRupee) / (data?.denominator?.toDouble() ?: 1.00)
                if (amountNeeded <= 0) {
                    val amountActive = "Min Value ₹<b>${data?.minValue}</b>"
                    holder.binding.clBackGround.setBackgroundResource(R.drawable.ic_category_voucher_active)
                    holder.binding.tvMinValueAmount.text = Html.fromHtml(amountActive)
                    holder.binding.tvMinValueAmount.setTextColor(
                        ContextCompat.getColor(
                            context, R.color.white
                        )
                    )
                    Glide.with(context).load("${prefs?.voucherBaseUrl}${data.brandLogo}")
                        .into(holder.binding.ivBrandLogo)
                    holder.binding.ivLock.visibility = View.GONE
                    holder.binding.ivShare.visibility = View.GONE
                    holder.itemView.setOnClickListener {
                        voucherInterface.onVoucherClicked(true, data)
                    }
                    holder.binding.ivShare.setOnClickListener {
                        val sendIntent = Intent()
                        sendIntent.action = Intent.ACTION_SEND
                        sendIntent.putExtra(
                            Intent.EXTRA_TEXT, "Hey Check out this Great app:"
                        )
                        sendIntent.type = "text/plain"
                        context.startActivity(sendIntent)
                    }
                } else {
                    val amountInActive =
                        "Earn <b>${amountNeeded.toInt()} Chips</b> more\n to redeem"
                    holder.binding.clBackGround.setBackgroundResource(R.drawable.ic_category_voucher_in_active)
                    holder.binding.tvMinValueAmount.text = Html.fromHtml(amountInActive)
                    holder.binding.tvMinValueAmount.setTextColor(
                        ContextCompat.getColor(
                            context, R.color.black
                        )
                    )
                    Glide.with(context).load("${prefs?.voucherBaseUrl}${data.brandLogo}")
                        .into(holder.binding.ivBrandLogo)
                    holder.binding.ivLock.visibility = View.VISIBLE
                    holder.binding.ivShare.visibility = View.GONE
                    holder.itemView.setOnClickListener {
                        voucherInterface.onVoucherClicked(true, data)
                    }
                }
            }
        } catch (e: Exception) {

        }


    }

    override fun getItemCount(): Int {
        return voucherListResponses?.size?:0
    }

    inner class AllVouchersViewHolder(val binding: VouchersItemListLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }


    interface VoucherInterface {
        fun onVoucherClicked(isClicked: Boolean, voucherList: VouchersItem)
    }
}