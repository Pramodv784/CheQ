package com.cheq.retail.ui.rewards.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cheq.retail.R
import com.cheq.retail.databinding.UpiItemLayoutNewBinding
import com.cheq.retail.ui.rewards.model.UpiListItem

class UPIAdapter(
    private val context: Context,
    private val dummyList: List<UpiListItem?>?,
    private val onUpiClick: OnUpiClick
) : RecyclerView.Adapter<UPIAdapter.UPIViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UPIViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = UpiItemLayoutNewBinding.inflate(inflater, parent, false)
        return UPIViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UPIViewHolder, position: Int) {
        val data = dummyList?.get(position)
        if (data != null) {
            holder.binding.tvUPIName.text = data.vpa ?: ""
            holder.binding.ivBankLogo.setImageResource(R.drawable.ic_upi_logo)
            holder.binding.llBank.setOnClickListener {
                onUpiClick.onUpiClick(data.vpa ?: "")
            }
        }
    }

    override fun getItemCount(): Int {
        return dummyList?.size ?: 0
    }

    inner class UPIViewHolder(val binding: UpiItemLayoutNewBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    interface OnUpiClick {
        fun onUpiClick(upi: String)
    }

}