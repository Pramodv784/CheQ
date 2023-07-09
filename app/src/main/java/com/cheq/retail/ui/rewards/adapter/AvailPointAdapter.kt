package com.cheq.retail.ui.rewards.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cheq.retail.databinding.AwailPointItemLayoutBinding

class AvailPointAdapter(private val context: Context, private val list: List<String>) :
    RecyclerView.Adapter<AvailPointAdapter.AvailPointViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailPointViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = AwailPointItemLayoutBinding.inflate(inflater, parent, false)
        return AvailPointViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AvailPointViewHolder, position: Int) {
        val data = list[position]
        holder.binding.tvAwailTitle.text = data
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class AvailPointViewHolder(val binding: AwailPointItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

}