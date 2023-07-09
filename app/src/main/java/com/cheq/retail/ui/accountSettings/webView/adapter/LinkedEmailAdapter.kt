package com.cheq.retail.ui.accountSettings.webView.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cheq.retail.R
import com.cheq.retail.databinding.CheqSafeEmailItemLayoutBinding
import com.cheq.retail.ui.accountSettings.model.ActiveEmailsItem

class LinkedEmailAdapter(
    private val context: Context,
    private val linkedUserEmailList: ArrayList<ActiveEmailsItem>,
    private val linkedEmailClickedListener: LinkedEmailClickedListener
) : RecyclerView.Adapter<LinkedEmailAdapter.LinkedEmailViewHolder>() {
    private var binding: CheqSafeEmailItemLayoutBinding? = null
    private var layoutInflater: LayoutInflater? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkedEmailViewHolder {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(context)
        }
        binding = DataBindingUtil.inflate(
            layoutInflater!!, R.layout.cheq_safe_email_item_layout, parent, false
        )
        return LinkedEmailViewHolder(binding!!)
    }

    override fun onBindViewHolder(holder: LinkedEmailViewHolder, position: Int) {
        val data = linkedUserEmailList[position]


        if (data.isActive!!) {
            holder.itemView.visibility = View.VISIBLE
            holder.binding.tvEmail.text = data.email
            if (data.emailTokenSource!!.equals("Outlook") || data.emailTokenSource.equals("Live") || data.emailTokenSource.equals(
                    "Hotmail"
                )
            ) {
                holder.binding.ivEmailType.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_outlook
                    )
                )
            } else {
                holder.binding.ivEmailType.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_gmail
                    )
                )
            }

            holder.binding.ivCancel.setOnClickListener {
                linkedEmailClickedListener.onDelinkedClicked(data)
            }
        } else {
            holder.itemView.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return linkedUserEmailList.size
    }

    interface LinkedEmailClickedListener {
        fun onDelinkedClicked(dataItem: ActiveEmailsItem)
    }

    inner class LinkedEmailViewHolder(var binding: CheqSafeEmailItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

}