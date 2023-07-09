package com.cheq.retail.ui.accountSettings.webView.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cheq.retail.R
import com.cheq.retail.constants.AFConstants.FAILED
import com.cheq.retail.constants.AFConstants.PROCESSING
import com.cheq.retail.constants.AFConstants.REFUNDED
import com.cheq.retail.databinding.TransactionHistoryItemLayout2Binding
import com.cheq.retail.ui.accountSettings.model.TxnListsItem
import com.cheq.retail.utils.Utils

class TransactionDetailAdapter(
    private val context: Context, private val list: List<TxnListsItem?>?
) : RecyclerView.Adapter<TransactionDetailAdapter.TransactionDetailViewHolder>() {
    private var binding: TransactionHistoryItemLayout2Binding? = null
    private var layoutInflater: LayoutInflater? = null
    var onItemClick: ((TxnListsItem?) -> Unit)? = null
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): TransactionDetailViewHolder {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(context)
        }
        binding = DataBindingUtil.inflate(
            layoutInflater!!, R.layout.transaction_history_item_layout2, parent, false
        )
        return TransactionDetailViewHolder(binding!!)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor", "SetTextI18n")
    override fun onBindViewHolder(
        holder: TransactionDetailViewHolder, position: Int
    ) {

        val data = list?.get(position)
        holder.binding.tvDate.text = Utils.formattedTimeFromDate(data!!.createdAt!!)

        holder.binding.tvAmount.text =
            context.getString(R.string.rupee_symbol) + "${data.billTotalAmount ?:0}"
        if (data.paidTogether == true) {
            holder.binding.tvPaidTogether.visibility = View.VISIBLE
        } else
            holder.binding.tvPaidTogether.visibility = View.GONE
        holder.binding.tvDetails.text = data.narration
        if (data.payinStatus == REFUNDED) {
            setTransactionView(holder, R.color.ref_txt_color, R.drawable.ic_bill_refunded)
        } else if (data.payinStatus == PROCESSING) {
            setTransactionView(holder, R.color.ref_txt_color, R.drawable.ic_bill_pending)
        }
        else if (data.payinStatus == FAILED || data.payoutStatus == FAILED) {
            setTransactionView(holder, R.color.amount_red, R.drawable.ic_bill_payment_failed)
        }else {
            setTransactionView(holder, R.color.ref_txt_color, R.drawable.ic_bill_payment)
        }
        if (position == list!!.size - 1) holder.binding.viewVertical.visibility = View.GONE
        else
            holder.binding.viewVertical.visibility = View.VISIBLE

    }

    private fun setTransactionView(holder: TransactionDetailViewHolder, color: Int, imgResource: Int) {
        holder.binding.tvAmount.setTextColor(ContextCompat.getColor(context, color))
        holder.binding.ivTransaction.setImageResource(imgResource)
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    inner class TransactionDetailViewHolder(var binding: TransactionHistoryItemLayout2Binding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(list?.get(adapterPosition))
                //  onItemClick?.invoke(TxnListsItem())
            }
        }
    }
}