package com.cheq.profile.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.cheq.common.R
import com.cheq.profile.databinding.LayoutCheqSafeEmailLinkingItemBinding
import com.cheq.profile.domain.entities.EmailLinkingStatus
import com.cheq.profile.domain.entities.PersonalDetails

/**
 * Created by Akash Khatkale on 25th May, 2023.
 * akash.k@cheq.one
 */
class EmailCheqSafeAdapter(
    private val emailList: List<PersonalDetails.ActiveEmails>
) : RecyclerView.Adapter<EmailCheqSafeAdapter.EmailCheqSafeViewholder>() {

    inner class EmailCheqSafeViewholder(var binding: LayoutCheqSafeEmailLinkingItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmailCheqSafeViewholder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutCheqSafeEmailLinkingItemBinding.inflate(inflater, parent, false)
        return EmailCheqSafeViewholder(binding)
    }

    override fun getItemCount(): Int = emailList.size

    override fun onBindViewHolder(holder: EmailCheqSafeViewholder, position: Int) {
        val data = emailList[position]
        with(holder.binding) {
            emailTv.text = data.email
            cheqSafeStatusIv.isVisible = data.emailLinkingStatus == EmailLinkingStatus.SUCCESS
            cheqSafeStatusTv.isVisible = data.emailLinkingStatus != EmailLinkingStatus.SUCCESS
            emailTypeIv.setImageDrawable(emailTypeIv.context.resources.getDrawable(R.drawable.ic_gmail))
        }
    }
}