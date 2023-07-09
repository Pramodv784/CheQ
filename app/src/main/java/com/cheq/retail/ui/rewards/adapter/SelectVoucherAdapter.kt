package com.cheq.retail.ui.rewards.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cheq.retail.R
import com.cheq.retail.databinding.SelectVoucherItemListLayoutBinding
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.ui.main.maininterface.RewardsInterface
import com.cheq.retail.ui.main.model.DenomsItem

class SelectVoucherAdapter(
    private val context: Context,
    private val dummyList: ArrayList<DenomsItem>,
    private val rewardsInterface: RewardsInterface,
    private val denominator : Double
) : RecyclerView.Adapter<SelectVoucherAdapter.SelectVoucherViewHolder>() {

    private var selected = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectVoucherViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = SelectVoucherItemListLayoutBinding.inflate(inflater, parent, false)
        return SelectVoucherViewHolder(binding)
    }
    fun setSelection(position: Int) {
        selected = position
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: SelectVoucherViewHolder, position: Int) {

       try {
           val sortedList = dummyList.sortedBy {
               it.denominations
           }

           val coinBalance = SharePrefs.instance?.getInt(SharePrefsKeys.REWARDS_COIN_BALANCE)
           val data = sortedList[position]
           holder.binding.tvAmount.text = "₹${data.denominations}"


           val chipToRupee = coinBalance.toString().toDouble() * denominator

           if ((data.denominations ?: 0) <= chipToRupee) {
               setBgEnaDis(holder.binding.clBackGround, false, holder.binding.tvAmount)
               if (selected == holder.adapterPosition){
                   rewardsInterface.onMyVoucherClicked(true, data)
                   setBgEnaDis(holder.binding.clBackGround, true, holder.binding.tvAmount)
               } else {
                   setBgEnaDis(holder.binding.clBackGround, false, holder.binding.tvAmount)
               }

               holder.itemView.setOnClickListener {

                   rewardsInterface.onMyVoucherClicked(true, data)
                   rewardsInterface.onCategoriesClicked(holder.adapterPosition)
               }
           } else {
               holder.binding.clBackGround.setBackgroundResource(R.drawable.ic_voucher_option_disabled)
               holder.binding.tvAmount.setTextColor(
                   ContextCompat.getColor(
                       context, R.color.colorTextDisabled
                   )
               )
           }
       }catch (e : Exception){

       }
    }

    override fun getItemCount(): Int {
        return dummyList.size
    }


    private fun setBgEnaDis(view: View, isBg: Boolean, textView: AppCompatTextView) {
        if (isBg) {
            view.setBackgroundResource(R.drawable.ic_voucher_option_selected)
            textView.setTextColor(
                ContextCompat.getColor(
                    context, R.color.colornaturals
                )
            )
        } else {
            view.setBackgroundResource(R.drawable.ic_voucher_option_enabled)
            textView.setTextColor(
                ContextCompat.getColor(
                    context, R.color.colornaturals
                )
            )
        }
    }

    inner class SelectVoucherViewHolder(val binding: SelectVoucherItemListLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)
}