package com.cheq.profile.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.cheq.common.utils.Constants.FAILED
import com.cheq.common.utils.Constants.PROCESSING
import com.cheq.common.utils.Constants.REFUNDED
import com.cheq.common.utils.DateUtils
import com.cheq.profile.databinding.LayoutTransactionHistoryItemBinding
import com.cheq.profile.domain.entities.TransactionHistory
import com.cheq.profile.presentation.listeners.OnTransactionHistoryItemClicked
import com.cheq.common.R as commonR
import com.cheq.proxima.R as proxima

/**
 * Created by Akash Khatkale on 15th May, 2023.
 * akash.k@cheq.one
 */
class TransactionHistoryAdapter(
    private val transactionHistory: List<TransactionHistory.TransactionHistoryItem>,
    private val listener: OnTransactionHistoryItemClicked
) : RecyclerView.Adapter<TransactionHistoryAdapter.TransactionHistoryViewHolder>() {

    inner class TransactionHistoryViewHolder(val binding: LayoutTransactionHistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionHistoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutTransactionHistoryItemBinding.inflate(inflater, parent, false)
        return TransactionHistoryViewHolder(binding)
    }

    override fun getItemCount(): Int = transactionHistory.size

    override fun onBindViewHolder(holder: TransactionHistoryViewHolder, position: Int) {
        val data = transactionHistory[position]
        with(holder.binding) {
            dateTv.text = DateUtils.formattedTimeFromDate(data.createdAt)
            amountTv.text =
                amountTv.context.getString(commonR.string.rupee_symbol) + "${data.billTotalAmount}"
            paidTogetherTv.isVisible = data.paidTogether
            detailsTv.text = data.narration
            if (data.payingStatus == REFUNDED) {
                setTransactionView(
                    holder,
                    proxima.color.greyscale_p9,
                    commonR.drawable.ic_bill_refunded
                )
            } else if (data.payingStatus == PROCESSING) {
                setTransactionView(
                    holder,
                    proxima.color.greyscale_p9,
                    commonR.drawable.ic_bill_pending
                )
            } else if (data.payingStatus == FAILED || data.payoutStatus == FAILED) {
                setTransactionView(
                    holder,
                    proxima.color.negative_p5,
                    commonR.drawable.ic_bill_payment_failed
                )
            } else {
                setTransactionView(
                    holder,
                    proxima.color.greyscale_p9,
                    commonR.drawable.ic_bill_payment
                )
            }
            holder.binding.verticalView.isVisible = position != transactionHistory.size - 1

            parentCl.setOnClickListener {
                listener.onItemClicked(data)
            }
        }
    }

    private fun setTransactionView(
        holder: TransactionHistoryViewHolder,
        color: Int,
        imgResource: Int
    ) {
        with(holder.binding) {
            amountTv.setTextColor(ContextCompat.getColor(amountTv.context, color))
            transactionIconIv.setImageResource(imgResource)
        }
    }

}