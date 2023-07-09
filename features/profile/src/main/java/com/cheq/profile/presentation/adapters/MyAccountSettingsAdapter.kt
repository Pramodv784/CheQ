package com.cheq.profile.presentation.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.cheq.common.R
import com.cheq.proxima.R as proxima
import com.cheq.profile.databinding.MyAccountSettingSingleItemLayoutBinding
import com.cheq.profile.presentation.listeners.OnAccountSettingsClicked
import com.cheq.profile.presentation.models.SettingsModel.SettingsIdentifier.*
import com.cheq.profile.presentation.models.SettingsModel

/**
 * Created by Akash Khatkale on 15th May, 2023.
 * akash.k@cheq.one
 */
class MyAccountSettingsAdapter(
    val itemList: List<SettingsModel>,
    val listener: OnAccountSettingsClicked
) : RecyclerView.Adapter<MyAccountSettingsAdapter.MyAccountSettingsViewHolder>() {

    inner class MyAccountSettingsViewHolder(val binding: MyAccountSettingSingleItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAccountSettingsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MyAccountSettingSingleItemLayoutBinding.inflate(inflater, parent, false)
        return MyAccountSettingsViewHolder(binding)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: MyAccountSettingsViewHolder, position: Int) {
        val currentData = itemList[position]
        with(holder.binding) {
            settingsTitleTv.text = settingsTitleTv.context.getString(currentData.title)
            settingsIconIv.setImageResource(currentData.drawable)
            emailTv.text = getFormattedEmailText(currentData.emailCount, emailTv.context)
            ViewCompat.setBackgroundTintList(
                emailTv,
                ColorStateList.valueOf(
                    emailTv.resources.getColor(
                        getEmailColor(
                            currentData.emailCount
                        )
                    )
                )
            )
            parentCl.setOnClickListener {
                listener.onClick(currentData.id)
            }
        }
        setViewVisibility(currentData.id, holder.binding)
    }

    private fun getEmailColor(emailCount: Int): Int =
        when (emailCount) {
            0 -> proxima.color.negative_p5
            else -> proxima.color.colorPrimary
        }

    private fun getFormattedEmailText(emailCount: Int, context: Context): String =
        when (emailCount) {
            0 -> context.resources.getString(R.string.no_emails_linked)
            1 -> context.resources.getString(R.string.email_linked)
            else -> context.resources.getString(R.string.emails_linked, emailCount.toString())
        }


    private fun setViewVisibility(
        id: SettingsModel.SettingsIdentifier,
        binding: MyAccountSettingSingleItemLayoutBinding
    ) {
        when (id) {
            MANAGE_CHEQ_SAFE -> {
                binding.emailTv.isVisible = true
            }

            else -> {
                binding.emailTv.isVisible = false
                binding.forwardIconIv.isVisible = true
                binding.borderView.isVisible = true
            }
        }
    }


}