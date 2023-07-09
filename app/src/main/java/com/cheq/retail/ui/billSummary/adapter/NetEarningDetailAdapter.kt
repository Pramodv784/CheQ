package com.cheq.retail.ui.billSummary.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cheq.retail.databinding.LayoutSingleItemLayoutGeBinding
import com.cheq.retail.ui.billSummary.model.ItemsItem

class NetEarningDetailAdapter(val context: Context, val itemList: List<ItemsItem?>?) :
    RecyclerView.Adapter<NetEarningDetailAdapter.NetEarningDetailViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): NetEarningDetailAdapter.NetEarningDetailViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = LayoutSingleItemLayoutGeBinding.inflate(inflater, parent, false)
        return NetEarningDetailViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: NetEarningDetailAdapter.NetEarningDetailViewHolder, position: Int
    ) {
        val data = itemList?.get(position)
        data?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    inner class NetEarningDetailViewHolder(val binding: LayoutSingleItemLayoutGeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(itemsItem: ItemsItem) {
            binding.items = itemsItem
            binding.executePendingBindings()
        }
    }
}