package com.cheq.retail.ui.dashboard.view.adapter


import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cheq.retail.R
import com.cheq.retail.databinding.CreditDashboardProductsBinding
import com.cheq.retail.extensions.bankMasterUrl
import com.cheq.retail.extensions.loanMasterUrl
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.ui.dashboard.model.homedashboad.CreditDashBoardProductModel
import com.cheq.retail.utils.ImageUtils.loadSvg



class CreditProductAdapter(var products: ArrayList<CreditDashBoardProductModel?>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var binding: CreditDashboardProductsBinding? = null
    private var layoutInflater: LayoutInflater? = null
    lateinit var prefs: SharePrefs
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        binding = DataBindingUtil.inflate(
            layoutInflater!!, R.layout.credit_dashboard_products, parent, false
        )
         prefs = SharePrefs.getInstance(parent.context)!!;
        return ViewHolder(binding!!)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        binding?.ivKeyYOurProduct?.let { it1 ->

            if (products.get(position)?.instrument_type == "CC") {
                binding?.creditCard?.text="Credit card"
                it1.loadSvg("${prefs.bankMasterUrl}${products.get(position)?.instrument_master_id}-logo.svg",R.drawable.bank_logo_placeholder)
            } else {
                binding?.creditCard?.text="Loan"
                it1.loadSvg("${prefs.loanMasterUrl}${products.get(position)?.instrument_master_id}-logo.svg",R.drawable.bank_logo_placeholder)
            }


        }


        if (position % 2 == 0) {
            binding?.tv?.text = "Bad impact"
            binding?.tv?.setTextColor(Color.parseColor("#F5466A"))
        } else {
            binding?.tv?.text = "Good impact"
            binding?.tv?.setTextColor(Color.parseColor("#00C197"))
        }
       // if (products.get(position).product_type == "CC"){
            binding?.ivKeyYOurProductHeading?.text = products?.get(position)?.instrument_master_name?: ""
      //  } else {
       //     binding?.ivKeyYOurProductHeading?.text = products?.get(position)?.bankMasterRecord?.billerName ?: ""
       // }

    }

    override fun getItemCount(): Int {
        return products.size
    }

    inner class ViewHolder(var binding: CreditDashboardProductsBinding) : RecyclerView.ViewHolder(
        binding.root
    )


}