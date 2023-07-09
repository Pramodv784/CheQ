package com.cheq.retail.ui.billPayments.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cheq.retail.R
import com.cheq.retail.databinding.PreferredMethodItemLayoutBinding
import com.cheq.retail.ui.billPayments.model.PaymentOptionsResponse
import com.cheq.retail.ui.billPayments.paymentsInterface.PaymentOptionInterface


class PreferredMethodAdapter(
    private val preferredMethodList: List<PaymentOptionsResponse.PreferredMethodsItem?>,
    private val context: Context,
    private val preferredMethodAdapterInterface: PaymentOptionInterface
) : RecyclerView.Adapter<PreferredMethodAdapter.PreferredMethodViewHolder>() {
    private var binding: PreferredMethodItemLayoutBinding? = null
    private var layoutInflater: LayoutInflater? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreferredMethodViewHolder {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(context)
        }
        binding = DataBindingUtil.inflate(
            layoutInflater!!, R.layout.preferred_method_item_layout, parent, false
        )
        return PreferredMethodViewHolder(binding!!)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PreferredMethodViewHolder, position: Int) {
        val item = preferredMethodList[position]
        when {
            item?.paymentMethod.equals("NET_BANKING") && item!!.BankMaster != null -> {
                holder.mBinding.tvBankName.text = item.BankMaster!!.originalBankName
                Glide.with(context).load(item.BankMaster.logo)
                    .placeholder(R.drawable.bank_logo_placeholder).into(holder.mBinding.ivBankLogo)
            }
            item?.paymentMethod.equals("DEBIT_CARD") && item?.cardToken != null && item.cardToken.BankMaster != null -> {
                holder.mBinding.tvBankName.text = item.cardToken.cardlast4
                println("bankName.......... ${item.cardToken.BankMaster.bankName}")
                holder.mBinding.tvDebitCardNumber.visibility = View.VISIBLE
                holder.mBinding.tvDebitCardNumber.text = " *  *  *  *  ${item.cardToken.cardlast4}"
                holder.mBinding.tvBankName.text = item.cardToken.BankMaster.originalBankName
                Glide.with(context).load(item.cardToken.BankMaster.logo)
                    .placeholder(R.drawable.bank_logo_placeholder).into(holder.mBinding.ivBankLogo)

            }
            else -> {
                println("bankName.......... something went wrong")
            }
        }

        holder.mBinding.btnPayNow.setOnClickListener {
            if (item!!.paymentMethod!! == "DEBIT_CARD") {
                preferredMethodAdapterInterface.onPreferredMethodClicked(
                    "SAVED_CARD",
                    item.cardToken!!.cardToken!!,
                    item.cardToken.BankMaster!!.id ?: "",
                    item.cardToken.BankMaster.originalBankName!!,
                    "",
                    item.cardToken.cardlast4!!,
                    holder.mBinding.etCvv.text.toString(),
                    item.cardToken.BankMaster.originalBankName,
                    "",
                    item.BankMaster?.id ?: ""
                )
                holder.mBinding.llCVVLayout.visibility = View.GONE
                holder.mBinding.btnPayNow.isEnabled = false
            }
            println("debitCVV............. ${holder.mBinding.etCvv.text}")
        }
        holder.itemView.setOnClickListener {
            if (item?.paymentMethod.equals("NET_BANKING")) {
                preferredMethodAdapterInterface.onPreferredMethodClicked(
                    item!!.paymentMethod!!,
                    item.BankMaster!!.id!!,
                    item.BankMaster.id ?: "",
                    item.BankMaster.originalBankName!! + " Net Banking",
                    item.BankMaster.IFSC_Prefix!!,
                    "",
                    "",
                    item.BankMaster.originalBankName.toString(),
                    "",
                    item.BankMaster.id ?: ""
                )
            } else if (item?.paymentMethod!! == "DEBIT_CARD") {


                holder.mBinding.llCVVLayout.visibility = View.VISIBLE
                holder.mBinding.etCvv.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        p0: CharSequence?, p1: Int, p2: Int, p3: Int
                    ) {

                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        holder.mBinding.btnPayNow.isEnabled = p0!!.length == 3
                    }

                    override fun afterTextChanged(p0: Editable?) {

                    }

                })


            }

        }


    }

    override fun getItemCount(): Int {
        return preferredMethodList.size
    }

    inner class PreferredMethodViewHolder(val mBinding: PreferredMethodItemLayoutBinding) :
        RecyclerView.ViewHolder(mBinding.root)
}