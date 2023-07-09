package com.cheq.retail.ui.dashboard.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cheq.retail.databinding.EarnMoreLayoutThreeBinding
import com.cheq.retail.databinding.LayoutHomeToDoItemBinding
import com.cheq.retail.databinding.LayoutProductsCombineBillPaymentToggleBinding
import com.cheq.retail.ui.main.model.TypesItem


class ToDoVIewPagerAdapter(
    private val context: Context,
    titleList: ArrayList<String>,
) :
    RecyclerView.Adapter<ToDoVIewPagerAdapter.TODOVPViewHolder>() {
    private val newList: List<String> = titleList


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TODOVPViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = LayoutHomeToDoItemBinding.inflate(inflater, parent, false)
        return TODOVPViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TODOVPViewHolder, position: Int) {

        val data = newList[position]
//        holder.localBinding.tvTitle.text = data.title
//        holder.localBinding.tvCaption.text = data.description
//        Glide.with(context).load(data.image).into(holder.localBinding.ivBackGround)
    }


    override fun getItemCount(): Int {
        return newList.size
    }

    inner class TODOVPViewHolder(val localBinding: LayoutHomeToDoItemBinding) :
        RecyclerView.ViewHolder(localBinding.root)


}