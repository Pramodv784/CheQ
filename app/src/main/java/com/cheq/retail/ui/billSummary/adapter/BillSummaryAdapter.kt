package com.cheq.retail.ui.billSummary.adapter

import android.content.Context
import android.graphics.Paint
import android.text.Html
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cheq.retail.R
import com.cheq.retail.databinding.LayoutBillSummarySingleItemBinding
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.billSummary.model.LineItemsItem
import com.cheq.retail.ui.main.helper.PopupHelper
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.Utils

class BillSummaryAdapter(
    private val isPayTogether: Boolean?,
    private val isRewardsFeeApplicable: Boolean?,
    val context: Context, var serviceFees: ((Int?) -> Unit)
) : ListAdapter<LineItemsItem, BillSummaryAdapter.BillSummaryViewHolder>(DiffCallBack()) {
    var isClicked: Boolean = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillSummaryViewHolder {
        val binding = LayoutBillSummarySingleItemBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
        return BillSummaryViewHolder(binding)
    }


    override fun onBindViewHolder(holder: BillSummaryViewHolder, position: Int) {
        val data = getItem(position)
        when (data.key) {
            BILL.BILL_AMOUNT.key -> {
                if (isPayTogether == true) {
                    holder.mBinding.tvTitle.text = Html.fromHtml(
                        context.getString(
                            R.string.str_pay_together_count,
                            data.displayText,
                            data.displaySubtext
                        ),0)

                } else {
                    holder.mBinding.tvTitle.text = data.displayText
                }
                holder.mBinding.tvAmount.text =
                    "₹${Utils.getFormattedDecimal(data.value?.toDouble() ?: 0.0)}"
                holder.mBinding.ivInfo.visibility = View.GONE
                if (isRewardsFeeApplicable != true) {
                    serviceFees.invoke(getLocalServiceFees(0, null))

                }
            }

            BILL.REWARD_FEE.key -> {
                var minusAmount: Int?
                holder.mBinding.tvTitle.text = data.displayText
                holder.mBinding.tvAmount.text = "₹${data.value}"
                holder.mBinding.ivInfo.visibility = View.VISIBLE
                holder.mBinding.ivInfo.setOnClickListener {
                    MparticleUtils.logEvent(
                        "CC_Payment_BillSummary_Fees_KnowMore_Clicked",
                        "User clicks on question mark icon for knowing more about service fees",
                        "Not Unique",
                        "Continue",
                        SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_PAYMENT_BILL_SUMMARY_FEES_KNOW_MORE_CLICKED),
                        context
                    )
                    PopupHelper.showPopupWindow(
                        anchor = holder.mBinding.ivInfo,
                        isToShow = false,
                        gravityOne = Gravity.TOP,
                        gravityTwo = Gravity.END,
                        width = 3,
                        height = 180,
                        sizeWidth = 600,
                        context = context,
                        title = data.displayText,
                        caption = data.infoText
                            ?: context.getString(R.string.str_srvice_fees_details)
                    ){
                        if (it == true){
                            holder.mBinding.infoClicked = false
                        }
                    }
                    holder.mBinding.infoClicked = PopupHelper.popUpWindow?.isShowing
                }
                serviceFees.invoke(getLocalServiceFees(data.value ?: 0, null))
                if (data?.meta?.waivedFee != null) {
                    holder.mBinding.tvAmount.text = "₹${data.value}"
                    holder.mBinding.tvAmount.paintFlags =
                        holder.mBinding.tvAmount.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    minusAmount = data.value?.minus(
                        data.meta.waivedFee
                    )
                    serviceFees.invoke(getLocalServiceFees(minusAmount ?: 0, null))

                    holder.mBinding.tvAdditionalAmount.visibility = View.VISIBLE
                    holder.mBinding.tvAdditionalAmount.text = "₹$minusAmount"
                    holder.mBinding.tvAmount.setTextColor(context.getColor(R.color.colorText))

                }
            }

            BILL.CHIPS_AMOUNT.key -> {
                holder.mBinding.tvTitle.text = data?.displayText
                holder.mBinding.tvAmount.text = "-₹${data.value}"
                holder.mBinding.ivInfo.visibility = View.GONE
                holder.mBinding.tvTitle.setTextColor(context.getColor(R.color.colorPrimary))
                holder.mBinding.tvAmount.setTextColor(context.getColor(R.color.colorPrimary))
            }
            else -> {
                holder.mBinding.llServiceFees.visibility = View.GONE
            }
        }
    }

    override fun getItemCount() = currentList.size
    class BillSummaryViewHolder(val mBinding: LayoutBillSummarySingleItemBinding) :
        RecyclerView.ViewHolder(mBinding.root)

    private class DiffCallBack : DiffUtil.ItemCallback<LineItemsItem>() {
        override fun areItemsTheSame(
            oldItem: LineItemsItem, newItem: LineItemsItem
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: LineItemsItem, newItem: LineItemsItem
        ): Boolean = oldItem == newItem

    }

    enum class BILL(var key: String) {
        BILL_AMOUNT("billAmount"), REWARD_FEE("rewardsFee"), WAIVED_OFF("waivedFee"), CHIPS_AMOUNT("chipsAmount"),
        PAYABLE_AMOUNT("payableAmount")
    }


    fun getLocalServiceFees(sFess: Int?, waivedOff: Int?): Int? {

        return if (sFess != null && waivedOff == null) {
            sFess
        } else if (sFess != null && waivedOff != null) {
            sFess.minus(waivedOff)
        } else 0
    }
}