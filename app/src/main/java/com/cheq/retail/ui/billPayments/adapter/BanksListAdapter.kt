package com.cheq.retail.ui.billPayments.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cheq.retail.R
import com.cheq.retail.constants.AFConstants.NET_BANKING
import com.cheq.retail.databinding.AdapterPaymentMethodsBinding
import com.cheq.retail.extensions.bankMasterUrl
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.ui.billPayments.model.PaymentOptionsResponse
import com.cheq.retail.ui.billPayments.paymentsInterface.PaymentOptionInterface
import com.cheq.retail.utils.ImageUtils.loadSvg


class BanksListAdapter(
    private val activity: Context,
    private val bankList: List<PaymentOptionsResponse.BanksItem>,
    private val bankInterface: PaymentOptionInterface
) : RecyclerView.Adapter<BanksListAdapter.ViewHolder>() {
    private var binding: AdapterPaymentMethodsBinding? = null
    private var layoutInflater: LayoutInflater? = null
    lateinit var prefs: SharePrefs

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        prefs = SharePrefs.getInstance(activity)!!
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.context)
        }
        binding = DataBindingUtil.inflate(
            layoutInflater!!, R.layout.adapter_payment_methods, viewGroup, false
        )
        return ViewHolder(binding!!)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        try {

            val item = bankList[position]
            viewHolder.binding.tvBankName.text = item.originalBankName
            val imageUrl =
                "${prefs.bankMasterUrl}${item._id}-logo.svg"
            viewHolder.binding.llBank.setOnClickListener {

                item.iFSCPrefix?.let { it2 ->
                    bankInterface.onPreferredMethodClicked(
                        NET_BANKING,
                        "" + item._id,
                        item._id ?: "",
                        item.originalBankName ?: "",
                        it2,
                        "",
                        "",
                        item.originalBankName?:"",
                        "",
                        item._id ?: ""
                    )
                }

            }

            viewHolder.binding.ivBankLogo.loadSvg(imageUrl)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return bankList.size
    }

    inner class ViewHolder(var binding: AdapterPaymentMethodsBinding) : RecyclerView.ViewHolder(
        binding.root
    )

}