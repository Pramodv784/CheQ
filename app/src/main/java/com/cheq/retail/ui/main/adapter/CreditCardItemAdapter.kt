package com.cheq.retail.ui.main.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cheq.retail.R
import com.cheq.retail.databinding.CreditCardItemBinding
import com.cheq.retail.databinding.DeLinkQuestionItemLayoutBinding
import com.cheq.retail.databinding.TopBankItemBinding
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.activateCard.CardDetailsActivityNew
import com.cheq.retail.ui.loans.DetailsForLoanActivity
import com.cheq.retail.ui.loans.model.Loan2
import com.cheq.retail.ui.main.model.SettingData
import com.cheq.retail.ui.main.model.cardItem
import com.cheq.retail.utils.ImageUtils.loadSvg
import com.cheq.retail.utils.MparticleUtils


class CreditCardItemAdapter(
    private var context: Context,
    private val cardList: List<SettingData?>,

) : RecyclerView.Adapter<CreditCardItemAdapter.TopBankBillerViewHolder>() {
    private var binding: CreditCardItemBinding? = null
    private var layoutInflater: LayoutInflater? = null


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TopBankBillerViewHolder {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(context)
        }
        binding = DataBindingUtil.inflate(
            layoutInflater!!, R.layout.credit_card_item, parent, false
        )
        return TopBankBillerViewHolder(binding!!)
    }

    override fun onBindViewHolder(
        holder: TopBankBillerViewHolder,
        position: Int
    ) {
        val data = cardList[position]
        holder.binding.tvBankName.text=data?.title
        holder.binding.ivBank.setImageResource(data!!.drawable)


        holder.binding.constL.setOnClickListener {
            context.startActivity(
                Intent(context, CardDetailsActivityNew::class.java).putExtra(
                    "productId", "null"
                ).putExtra("startColor", "AAC7FF").putExtra("middleColor", "F5F9FF")
                    .putExtra("endColor", "497CDF")


            )

        }



    }

    override fun getItemCount(): Int {
        return cardList.size
    }



    inner class TopBankBillerViewHolder(var binding: CreditCardItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}