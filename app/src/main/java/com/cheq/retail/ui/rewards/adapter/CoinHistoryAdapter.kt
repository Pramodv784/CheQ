package com.cheq.retail.ui.rewards.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cheq.retail.databinding.CoinHistoryItemLayoutBinding

class CoinHistoryAdapter(
    private val context: Context,
    private val dummyList: ArrayList<String> = arrayListOf()
) : RecyclerView.Adapter<CoinHistoryAdapter.CoinHistoryViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinHistoryViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = CoinHistoryItemLayoutBinding.inflate(inflater, parent, false)
        return CoinHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CoinHistoryViewHolder, position: Int) {
        val data = dummyList[position]
        holder.binding.tvAmount.text = "₹$data"
    }

    override fun getItemCount(): Int {
        return 4
    }

    inner class CoinHistoryViewHolder(val binding: CoinHistoryItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)
}