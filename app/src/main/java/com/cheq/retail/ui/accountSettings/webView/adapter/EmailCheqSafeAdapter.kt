package com.cheq.retail.ui.accountSettings.webView.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cheq.retail.R
import com.cheq.retail.databinding.DeLinkQuestionItemLayoutBinding
import com.cheq.retail.ui.accountSettings.model.ActiveEmailsItem
import com.cheq.retail.ui.socialLogin.model.EmailLinkingStatus


class EmailCheqSafeAdapter(
    private var context: Context,
    private val emailList: List<ActiveEmailsItem?>,

) : RecyclerView.Adapter<EmailCheqSafeAdapter.EmailDelinkQuestionViewHolder>() {
    private var binding: DeLinkQuestionItemLayoutBinding? = null
    private var layoutInflater: LayoutInflater? = null


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EmailDelinkQuestionViewHolder {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(context)
        }
        binding = DataBindingUtil.inflate(
            layoutInflater!!, R.layout.de_link_question_item_layout, parent, false
        )
        return EmailDelinkQuestionViewHolder(binding!!)
    }

    override fun onBindViewHolder(
        holder: EmailDelinkQuestionViewHolder,
        position: Int
    ) {
        val data = emailList[position]
        holder.binding.etEmail.text=data?.email

        if( data?.emailLinkingStatus == EmailLinkingStatus.SUCCESS){
            holder.binding.ivCheqSafeStatus.visibility= View.VISIBLE
            holder.binding.tvCheqSafeStatus.visibility= View.GONE
        }else{
            holder.binding.ivCheqSafeStatus.visibility= View.GONE
            holder.binding.tvCheqSafeStatus.visibility= View.VISIBLE
        }

        holder.binding.ivEmailType.setImageDrawable(context.resources.getDrawable(R.drawable.ic_gmail))









    }

    override fun getItemCount(): Int {
        return emailList.size
    }



    inner class EmailDelinkQuestionViewHolder(var binding: DeLinkQuestionItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)


}