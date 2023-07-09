package com.cheq.retail.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.cheq.retail.R
import com.cheq.retail.databinding.EditTextDevSettingsItemBinding
import com.cheq.retail.databinding.SwitchDevSettingsItemBinding
import com.cheq.retail.ui.item.EditTextSettingsItem
import com.cheq.retail.ui.item.SettingsItem
import com.cheq.retail.ui.item.SwitchSettingsItem
import com.cheq.retail.ui.viewholder.EditTextViewHolder
import com.cheq.retail.ui.viewholder.SettingsViewHolder
import com.cheq.retail.ui.viewholder.SwitchViewHolder

/**
 * Created by Akash Khatkale on 5th June, 2023.
 * akash.k@cheq.one
 */
class DevSettingsRecyclerAdapter(
    private val list: List<SettingsItem>
): RecyclerView.Adapter<SettingsViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return list[position].getLayout()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val binding: ViewDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            viewType,
            parent,
            false
        )
        return when(viewType) {
            R.layout.edit_text_dev_settings_item -> EditTextViewHolder(binding)
            R.layout.switch_dev_settings_item -> SwitchViewHolder(binding)
            else -> EditTextViewHolder(binding)
        }

    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        when (holder) {
            is EditTextViewHolder -> {
                (holder.binding as? EditTextDevSettingsItemBinding)?.apply {
                    val settingsItem = (list[position] as EditTextSettingsItem)
                    item = settingsItem
                    editTextEt.doOnTextChanged { text, _, _, _ ->
                        settingsItem.onTextChange(text.toString())
                    }
                }
            }
            is SwitchViewHolder -> {
                (holder.binding as? SwitchDevSettingsItemBinding)?.apply {
                    val settingsItem = (list[position] as SwitchSettingsItem)
                    item = settingsItem
                    checkbox.setOnCheckedChangeListener { _, isChecked ->
                        settingsItem.onSwitchChange(isChecked)
                    }
                }
            }
        }
    }
}