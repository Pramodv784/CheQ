package com.cheq.retail.ui.emandate.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cheq.retail.R
import com.cheq.retail.databinding.AdapterBanksListBinding
import com.cheq.retail.ui.emandate.model.BankListResponse


class BanksListAdapter(
    private val activity: Context,
    private val bankList: ArrayList<BankListResponse.DataEntity>,
    private val bankInterface: BankListAdapterInterface,
    private var showTopSix: Boolean
) :
    RecyclerView.Adapter<BanksListAdapter.ViewHolder>() {
    private var binding: AdapterBanksListBinding? = null
    private var layoutInflater: LayoutInflater? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.context)
        }
        binding = DataBindingUtil.inflate(
            layoutInflater!!, R.layout.adapter_banks_list, viewGroup, false
        )
        return ViewHolder(binding!!)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        try {
            val item = bankList[position]
            binding!!.tvBankName.text = item.originalBankName

            binding!!.llBank.setOnClickListener {
                bankInterface.onBankSelected(item)
            }

            if (position == 5 && showTopSix) {
                viewHolder.binding.view.visibility = View.VISIBLE
            } else {
                viewHolder.binding.view.visibility = View.GONE
            }

            Glide.with(activity).load(item.logo).placeholder(R.drawable.bank_logo_placeholder)
                .into(binding!!.ivBankLogo)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return bankList.size
    }

    interface BankListAdapterInterface {
        fun onBankSelected(item: BankListResponse.DataEntity)
    }

    inner class ViewHolder(var binding: AdapterBanksListBinding) : RecyclerView.ViewHolder(
        binding.root
    )

}