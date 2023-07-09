package com.cheq.retail.ui.main.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cheq.retail.R
import com.cheq.retail.databinding.TopBankItemBinding
import com.cheq.retail.extensions.loanMasterUrl
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.loans.DetailsForLoanActivity
import com.cheq.retail.ui.loans.model.Loan2
import com.cheq.retail.utils.ImageUtils.loadSvg
import com.cheq.retail.utils.IntentKeys
import com.cheq.retail.utils.MparticleUtils


class TopBankBillersAdapter(
    private var context: Context,
    private val topBillersList: List<Loan2?>?,
    var rewardsCanAssign: Int,
    var rewardsAssigned: Int,
    var rewardsAssignUpto: Int

) : RecyclerView.Adapter<TopBankBillersAdapter.TopBankBillerViewHolder>() {
    private var binding: TopBankItemBinding? = null
    private var layoutInflater: LayoutInflater? = null
    lateinit var prefs: SharePrefs

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TopBankBillerViewHolder {
        prefs = SharePrefs.getInstance(context)!!
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(context)
        }
        binding = DataBindingUtil.inflate(
            layoutInflater!!, R.layout.top_bank_item, parent, false
        )
        return TopBankBillerViewHolder(binding!!)
    }

    override fun onBindViewHolder(
        holder: TopBankBillerViewHolder,
        position: Int
    ) {
        val data = topBillersList?.get(position)
        holder.binding.tvBankName.text = data?.billerName?:""
        val imageUrl =
            "${prefs.loanMasterUrl}${data?.id}-logo.svg"
        holder.binding.ivBank.loadSvg(imageUrl)

        holder.binding.constL.setOnClickListener {
            context.startActivity(
                Intent(context, DetailsForLoanActivity::class.java).putExtra(
                    "LOAN_PROVIDER",
                    data
                ).putExtra("from", "loan_provider")
                    .putExtra(IntentKeys.REWARDS_ASSIGN_UPTO, rewardsAssignUpto)
                    .putExtra(IntentKeys.REWARDS_CAN_ASSIGN, rewardsCanAssign)
                    .putExtra(IntentKeys.REWARDS_ASSIGNED, rewardsAssigned)


            )
            MparticleUtils.logCurrentScreen(
                "/loan-activation/select-lender",
                "The customer is asked to select a loan provider for addition to their credit portfolio on CheQ",
                "",
                "",
                "",
                "",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Loan_Activation_Select_Lender),
                context
            )

            MparticleUtils.logEvent(
                "",
                "User selects their lender for loan activation",
                "Not Unique",
                "Continue",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Loan_Activation_Select_Lender),
                context
            )
        }


    }

    override fun getItemCount(): Int {
        return topBillersList?.size?:0
    }


    inner class TopBankBillerViewHolder(var binding: TopBankItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}