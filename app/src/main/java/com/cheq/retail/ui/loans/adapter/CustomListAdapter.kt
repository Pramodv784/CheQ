package com.cheq.retail.ui.loans.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cheq.retail.R
import com.cheq.retail.databinding.AdapterLoanListBinding


class CustomListAdapter(
    private val activity: Context,
    private val loanList: List<String>,
    private  var itemInterFace:CustomListAdapterInterface
    ) : RecyclerView.Adapter<CustomListAdapter.ViewCustomHolder>() {
    private var binding: AdapterLoanListBinding? = null
    private var layoutInflater: LayoutInflater? = null
    private var checkedPosition = 0

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CustomListAdapter.ViewCustomHolder {

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.context)
        }
        binding = DataBindingUtil.inflate(
            layoutInflater!!, R.layout.adapter_loan_list, viewGroup, false
        )
        return ViewCustomHolder(binding!!)
    }

    override fun onBindViewHolder(viewHolder: CustomListAdapter.ViewCustomHolder, position: Int) {
        try {
            val item = loanList[position]
            binding!!.tvProductName.text = item.trim()
            if (checkedPosition == position) {
                viewHolder.mbinding.radioBt.setChecked(true);
            } else {
                viewHolder.mbinding.radioBt.setChecked(false);
            }

            binding?.radioBt?.setOnClickListener {
                checkedPosition=viewHolder.adapterPosition
                itemInterFace.onItemSelected(viewHolder.mbinding.tvProductName.text.toString())

                notifyDataSetChanged()
            }




        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun getItemCount(): Int {
        return loanList.size
    }

    interface CustomListAdapterInterface {
        fun onItemSelected(item: String)
    }

    inner class ViewCustomHolder(val mbinding: AdapterLoanListBinding) : RecyclerView.ViewHolder(
        mbinding.root
    ){

    }
}