package com.cheq.retail.ui.rewards.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cheq.retail.databinding.EarnMoreLayoutThreeBinding
import com.cheq.retail.ui.main.model.TypesItem

class EarnMoreVPAdapter(
    private val context: Context,
    titleList: ArrayList<TypesItem>,
) :
    RecyclerView.Adapter<EarnMoreVPAdapter.EarnMoreVPViewHolder>() {
    private val newList: List<TypesItem> =
        listOf(titleList.last()) + titleList + listOf(titleList.first())


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EarnMoreVPViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = EarnMoreLayoutThreeBinding.inflate(inflater, parent, false)
        return EarnMoreVPViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EarnMoreVPViewHolder, position: Int) {

        val data = newList[position]
        holder.localBinding.tvTitle.text = data.title
        holder.localBinding.tvCaption.text = data.description
        Glide.with(context).load(data.image).into(holder.localBinding.ivBackGround)
    }


    override fun getItemCount(): Int {
        return newList.size
    }

    inner class EarnMoreVPViewHolder(val localBinding: EarnMoreLayoutThreeBinding) :
        RecyclerView.ViewHolder(localBinding.root)


}