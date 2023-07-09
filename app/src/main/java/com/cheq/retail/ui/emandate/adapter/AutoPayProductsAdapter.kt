package com.cheq.retail.ui.emandate.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cheq.retail.R
import com.cheq.retail.databinding.AdapterAutopayProductListBinding
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.ui.login.model.Product
import com.cheq.retail.ui.login.model.UpdateProfileResponse
import com.cheq.retail.ui.login.modelv2.productv1.ProductV2
import com.cheq.retail.utils.GradientUtils

class AutoPayProductsAdapter(
    private val activity: Context,
    private val productInterface: AutopayProductInterface,
    private val list: List<ProductV2>
) :
    RecyclerView.Adapter<AutoPayProductsAdapter.ViewHolder>() {
    private var binding: AdapterAutopayProductListBinding? = null
    private var layoutInflater: LayoutInflater? = null
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.context)
        }
        binding = DataBindingUtil.inflate(
            layoutInflater!!, R.layout.adapter_autopay_product_list, viewGroup, false
        )
        return ViewHolder(binding!!)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        try {
            var totalDueEnabled = true

            val item = list[position]
            viewHolder.binding.llCreditCard.tvBankName.text = item.bankMasterRecord?.bankName?:""
            viewHolder.binding.llCreditCard.tvCardHolderName.text =
                SharePrefs.getInstance(activity)!!
                    .getString(SharePrefsKeys.FIRST_NAME) + " " + SharePrefs.getInstance(activity)!!
                    .getString(SharePrefsKeys.LAST_NAME)

            /*Glide.with(activity).load(item.logoWithName)
                .into(viewHolder.binding.llCreditCard.ivBankImage)*/

            viewHolder.binding.chTotal.setOnClickListener {
                totalDueEnabled = true
                Toast.makeText(
                    viewHolder.itemView.context,
                    "Autopay enabled on Total Due",
                    Toast.LENGTH_SHORT
                ).show()
                item.id?.let { it1 ->
                    productInterface.onActiveClick(
                        it1,
                        totalDueEnabled,
                        true
                    )
                }
            }

            viewHolder.binding.chMinimum.setOnClickListener {
                totalDueEnabled = false
                Toast.makeText(
                    viewHolder.itemView.context,
                    "Autopay enabled on Minimum Due",
                    Toast.LENGTH_SHORT
                ).show()
                item.id?.let { it1 ->
                    productInterface.onActiveClick(
                        it1,
                        totalDueEnabled,
                        true
                    )
                }
            }

            if (item.product_number?.length != 16) {
                viewHolder.binding.llCreditCard.tvCardNumber.text =
                    "XXXX XX" + (item.product_number?.subSequence(0, 2)
                        .toString() + " " + item.product_number?.subSequence(2, 6))

            } else {
                viewHolder.binding.llCreditCard.tvCardNumber.text =
                    (item.product_number?.subSequence(0, 4)
                        .toString() + " " + item.product_number?.subSequence(4, 8).toString() + " " +
                            item.product_number?.subSequence(8, 12)
                                .toString() + " " + item.product_number?.subSequence(12, 16)
                        .toString())

            }

            if (item.product_type == "MasterCard") {
                viewHolder.binding.llCreditCard.ivCardType.setImageResource(R.drawable.ic_mastercard)
            } else if (item.product_type == "Visa") {
                viewHolder.binding.llCreditCard.ivCardType.setImageResource(R.drawable.visa)
            }

            viewHolder.binding.switchAutopay.setOnClickListener {
                item.id?.let { it1 ->
                    productInterface.onActiveClick(
                        it1,
                        true,
                        viewHolder.binding.switchAutopay.isChecked
                    )
                }


                if (viewHolder.binding.switchAutopay.isChecked) {
                    viewHolder.binding.chipGroup.visibility = View.VISIBLE
                    viewHolder.binding.tvAutopayStatus.text = "Autopay on"
                    Toast.makeText(
                        viewHolder.itemView.context,
                        "Autopay enabled on Total Due",
                        Toast.LENGTH_SHORT
                    ).show()
                    viewHolder.binding.chTotal.isChecked = true
                    viewHolder.binding.chMinimum.isChecked = false
                } else {
                    viewHolder.binding.chipGroup.visibility = View.GONE
                    viewHolder.binding.tvAutopayStatus.text = "Autopay off"
                    Toast.makeText(
                        viewHolder.itemView.context,
                        "Autopay disabled",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            if (item.is_enabled_for_autopay != null && item.is_enabled_for_autopay) {
                viewHolder.binding.chipGroup.visibility = View.VISIBLE

                viewHolder.binding.tvAutopayStatus.text = "Autopay on"
            } else {
                viewHolder.binding.chipGroup.visibility = View.GONE
                viewHolder.binding.tvAutopayStatus.text = "Autopay off"
            }

            viewHolder.binding.chTotal.isChecked = item.is_total_due_enabled
            viewHolder.binding.chMinimum.isChecked = !item.is_total_due_enabled
            viewHolder.binding.switchAutopay.isChecked = item.is_enabled_for_autopay

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return getListSize()
    }

    private fun getListSize(): Int {
        return list.size
    }

    inner class ViewHolder(var binding: AdapterAutopayProductListBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    interface AutopayProductInterface {
        fun onActiveClick(productId: String, totalDueEnabled: Boolean, isAutoPayEnabled: Boolean)
        fun onFullDue(productId: Int)
    }
}