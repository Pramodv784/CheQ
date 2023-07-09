package com.cheq.retail.ui.referral.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cheq.retail.R
import com.cheq.retail.constants.AFConstants
import com.cheq.retail.databinding.ReferHistoryRowBinding
import com.cheq.retail.ui.main.model.HistoryItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RefHistoryAdapter(
    private val context: Context,
    private val historyList: MutableList<HistoryItem?>
) : RecyclerView.Adapter<RefHistoryAdapter.RefHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RefHistoryViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ReferHistoryRowBinding.inflate(inflater, parent, false)
        return RefHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RefHistoryViewHolder, position: Int) {
        val data = historyList[position]

        if (data != null) {
            holder.binding.txtName.text = data.name
            holder.binding.tvDate.text = getFormattedDate(data.creationDate)
            if (data.status.equals(AFConstants.SUCCESS, ignoreCase = true)) {
                holder.binding.ivRewardsEarned.setImageResource(R.drawable.ic_ref_success)
                if (data.type.equals(AFConstants.ONE_SIDED)) {
                    holder.binding.txtMessage.visibility = View.GONE
                    holder.binding.linearYou.visibility = View.VISIBLE
                    holder.binding.linearFriend.visibility = View.GONE

                    if (data.message?.get(0) != null && data.message[1] != null) {
                        holder.binding.tvYoumessage.text = data.message[0]
                        holder.binding.txtYouCheqCoins.text = data.message[1]
                    }
                } else {
                    holder.binding.txtMessage.visibility = View.GONE
                    holder.binding.linearYou.visibility = View.VISIBLE
                    holder.binding.linearFriend.visibility = View.VISIBLE
                    if (data.message?.get(0) != null && data.message.get(1) != null && data.message.get(2) != null && data.message.get(3) != null) {
                        holder.binding.tvYoumessage.text = data.message[0]
                        holder.binding.txtYouCheqCoins.text = data.message[1] + ". "
                        holder.binding.tvFriendmessage.text = data.message[2]
                        holder.binding.txtFriendCheqCoins.text = data.message[3]
                    }
                }

            } else {
                holder.binding.ivRewardsEarned.setImageResource(R.drawable.ic_ref_waiting)
                holder.binding.txtMessage.visibility = View.VISIBLE
                holder.binding.linearYou.visibility = View.GONE
                holder.binding.linearFriend.visibility = View.GONE
                if (data.message?.get(0) != null)
                    holder.binding.txtMessage.text = data.message[0]
            }
        }

        // setItemCollapsed(holder, data)
        /* if (data.isExpanded)
             setItemExpanded(holder, data)
         else
             setItemCollapsed(holder, data)
         holder.itemView.setOnClickListener {
             data.isExpanded = !data.isExpanded
             notifyDataSetChanged()
         }*/

    }

    private fun getFormattedDate(date: String?): String {
        val parsedDate = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)
        return parsedDate.format(DateTimeFormatter.ofPattern("dd MMM yy"))
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    //Use later when needs to expand
    private fun setItemExpanded(holder: RefHistoryViewHolder, data: HistoryItem) {
        holder.binding.viewVertical1.visibility = View.VISIBLE
        holder.binding.ivAcntCreated.visibility = View.VISIBLE
        holder.binding.txtAcntCreated.visibility = View.VISIBLE
        holder.binding.viewVertical2.visibility = View.VISIBLE
        holder.binding.ivFirstPay.visibility = View.VISIBLE
        holder.binding.txtFirstPay.visibility = View.VISIBLE
        holder.binding.dividerBottom.visibility = View.VISIBLE

        /* holder.binding.ivEarnedChip.visibility = View.GONE
         holder.binding.txtCheqCoins.visibility = View.GONE
         holder.binding.tvStateMessage.text = "Invited"*/
        holder.binding.ivRewardsEarned.setImageResource(R.drawable.ic_chip_gold)

        /*when (data.state) {
            3 -> {
                holder.binding.ivAcntCreated.setImageResource(R.drawable.ic_check_right)
                holder.binding.ivFirstPay.setImageResource(R.drawable.ic_check_right)
            }
            2 -> {
                holder.binding.ivAcntCreated.setImageResource(R.drawable.ic_check_right)
                holder.binding.ivFirstPay.setImageResource(R.drawable.ic_pay_time)
            }
            else -> {
                holder.binding.ivAcntCreated.setImageResource(R.drawable.ic_pay_time)
                holder.binding.ivFirstPay.setImageResource(R.drawable.ic_pay_time)
            }
        }*/

    }

    //Use later when needs to expand
    private fun setItemCollapsed(holder: RefHistoryViewHolder, data: HistoryItem) {
        holder.binding.viewVertical1.visibility = View.GONE
        holder.binding.ivAcntCreated.visibility = View.GONE
        holder.binding.txtAcntCreated.visibility = View.GONE
        holder.binding.viewVertical2.visibility = View.GONE
        holder.binding.ivFirstPay.visibility = View.GONE
        holder.binding.txtFirstPay.visibility = View.GONE
        holder.binding.dividerBottom.visibility = View.GONE

        /* if (data.type != "ONE_SIDED") {
             holder.binding.ivEarnedChip.visibility = View.VISIBLE
             holder.binding.txtCheqCoins.visibility = View.VISIBLE
             holder.binding.ivRewardsEarned.setImageResource(R.drawable.iconright)
         } else {
             holder.binding.ivEarnedChip.visibility = View.GONE
             holder.binding.txtCheqCoins.visibility = View.GONE
             holder.binding.ivRewardsEarned.setImageResource(R.drawable.ic_pay_time)
         }*/

        //holder.binding.tvStateMessage.text = data.message
        /*holder.binding.tvStateMessage.text = when (data.state) {
            0 -> "Referral Successful"
            1 -> "Waiting to create account"
            2 -> "Waiting to make first payment"
            3 -> "You both earned"
            else -> ""
        }*/
    }


    inner class RefHistoryViewHolder(val binding: ReferHistoryRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

}