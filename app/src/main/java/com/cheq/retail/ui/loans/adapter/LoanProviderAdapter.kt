package com.cheq.retail.ui.loans.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cheq.retail.R
import com.cheq.retail.databinding.AdapterBanksListBinding
import com.cheq.retail.extensions.loanMasterUrl
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.ui.loans.model.Loan2
import com.cheq.retail.utils.ImageUtils.loadSvg

class LoanProviderAdapter(
    private val onClicked:(Loan2)->Unit,
    private val activity: Context,
    private var bankList: ArrayList<Loan2>,
) : RecyclerView.Adapter<LoanProviderAdapter.ViewHolder>() {
    private var binding: AdapterBanksListBinding? = null
    private var layoutInflater: LayoutInflater? = null
    lateinit var prefs: SharePrefs

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): LoanProviderAdapter.ViewHolder {
        prefs = SharePrefs.getInstance(activity)!!
      //  if (layoutInflater == null) {
           // layoutInflater = LayoutInflater.from(viewGroup.context)
       // }
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(activity)!!, R.layout.adapter_banks_list, viewGroup, false
        )
        return ViewHolder(binding!!)
    }

    override fun onBindViewHolder(viewHolder: LoanProviderAdapter.ViewHolder, position: Int) {
        try {
            val item = bankList[position]
            binding!!.tvBankName.text =  item.billerName
            val imageUrl =
                "${prefs.loanMasterUrl}${item.id}-logo.svg"

            binding!!.llBank.setOnClickListener {
                onClicked(item)
                //bankInterface.onLoanProviderSelected(item)
            }

           // if (position == showTopSixCount-1) {
                viewHolder.binding.view.visibility = View.VISIBLE
          //  } else {
           //     viewHolder.binding.view.visibility = View.GONE
           // }

            viewHolder.binding.ivBankLogo.loadSvg(imageUrl, R.drawable.bank_logo_placeholder)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    public fun setList( bankList: ArrayList<Loan2>){
        this.bankList=bankList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return bankList.size
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }


    inner class ViewHolder(var binding: AdapterBanksListBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}