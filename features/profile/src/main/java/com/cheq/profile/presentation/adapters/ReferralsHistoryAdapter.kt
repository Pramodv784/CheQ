package com.cheq.profile.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cheq.common.utils.DateUtils
import com.cheq.common.R as commonR
import com.cheq.profile.databinding.LayoutReferralsHistoryBinding
import com.cheq.profile.domain.entities.ReferralHistory

/**
 * Created by Akash Khatkale on 22nd May, 2023.
 * akash.k@cheq.one
 */
class ReferralsHistoryAdapter(
    private val itemList: List<ReferralHistory.HistoryItem>
) : RecyclerView.Adapter<ReferralsHistoryAdapter.ReferralsHistoryViewHolder>() {

    inner class ReferralsHistoryViewHolder(val binding: LayoutReferralsHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReferralsHistoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutReferralsHistoryBinding.inflate(inflater, parent, false)
        return ReferralsHistoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ReferralsHistoryViewHolder, position: Int) {
        val data = itemList[position]

        with(holder.binding) {
            txtName.text = data.name
            tvDate.text = DateUtils.formattedTimeFromDate(data.creationDate)

            when (data.status) {
                ReferralHistory.HistoryItem.HistoryItemStatus.SUCCESS -> {
                    setSuccessView(holder.binding, data)
                }

                else -> {
                    setWaitingView(holder.binding, data)
                }
            }
        }
    }

    private fun setWaitingView(
        binding: LayoutReferralsHistoryBinding,
        data: ReferralHistory.HistoryItem
    ) {
        with(binding) {
            ivRewardsEarned.setImageResource(commonR.drawable.ic_ref_waiting)
            txtMessage.visibility = View.VISIBLE
            linearYou.visibility = View.GONE
            linearFriend.visibility = View.GONE
            if (data.message.isNotEmpty())
                txtMessage.text = data.message[0]
        }
    }

    private fun setSuccessView(
        binding: LayoutReferralsHistoryBinding,
        data: ReferralHistory.HistoryItem
    ) {
        with(binding) {
            ivRewardsEarned.setImageResource(com.cheq.common.R.drawable.ic_ref_success)
            when (data.type) {
                ReferralHistory.HistoryItem.HistoryItemType.ONE_SIDED -> {
                    binding.txtMessage.visibility = View.GONE
                    binding.linearYou.visibility = View.VISIBLE
                    binding.linearFriend.visibility = View.GONE

                    if (data.message.size >= 2) {
                        binding.tvYoumessage.text = data.message[0]
                        binding.txtYouCheqCoins.text = data.message[1]
                    }
                }

                else -> {
                    binding.txtMessage.visibility = View.GONE
                    binding.linearYou.visibility = View.VISIBLE
                    binding.linearFriend.visibility = View.VISIBLE
                    if (data.message.size > 3) {
                        binding.tvYoumessage.text = data.message[0]
                        binding.txtYouCheqCoins.text = "${data.message[1]}. "
                        binding.tvFriendmessage.text = data.message[2]
                        binding.txtFriendCheqCoins.text = data.message[3]
                    }
                }
            }
        }
    }

}