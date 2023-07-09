package com.cheq.retail.ui.billSummary.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.cheq.retail.databinding.LayoutGeItemLayoutBinding
import com.cheq.retail.ui.billSummary.model.InfoPopupItem
import com.cheq.retail.ui.billSummary.model.ItemsItem

class NetEarningAdapter(
    val context: Context?, val itemList: List<InfoPopupItem?>? = ArrayList()
) : RecyclerView.Adapter<NetEarningAdapter.NetEarningDetailsViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): NetEarningAdapter.NetEarningDetailsViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = LayoutGeItemLayoutBinding.inflate(inflater, parent, false)
        return NetEarningDetailsViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: NetEarningAdapter.NetEarningDetailsViewHolder, position: Int
    ) {
        val itemData = itemList?.get(position)
        itemData?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    inner class NetEarningDetailsViewHolder(val binding: LayoutGeItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(popupItem: InfoPopupItem) {
            binding.items = popupItem
            val itemList: List<ItemsItem?>? = popupItem.items
            binding.rvGeItem.apply {
                adapter = NetEarningDetailAdapter(context, itemList)
                hasFixedSize()
            }
            if (adapterPosition == 0){
                binding.viewDivider.visibility = View.VISIBLE
            } else {
                binding.viewDivider.visibility = View.INVISIBLE
            }
            binding.executePendingBindings()

        }
    }

}