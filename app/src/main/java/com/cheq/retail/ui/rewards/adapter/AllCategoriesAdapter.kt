package com.cheq.retail.ui.rewards.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cheq.retail.R
import com.cheq.retail.databinding.CategoriesItemLayoutBinding
import com.cheq.retail.extensions.voucherBaseUrl
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.ui.main.maininterface.RewardsInterface
import com.cheq.retail.ui.main.model.TypesItem

class AllCategoriesAdapter(
    private val context: Context,
    private val typesItems: List<TypesItem>,
    private val mainInterface: RewardsInterface,
    private val categoryName: String

) : RecyclerView.Adapter<AllCategoriesAdapter.AllCategoriesViewHolder>() {

    // if checkedPosition = -1, there is no default selection
    // if checkedPosition = 0, 1st item is selected by default
    /*  private val newList: List<TypesItem> =
          listOf(titleList.last()) + typesItems + listOf(titleList.first())*/

    private val newList: List<TypesItem> = listOf(typesItems.first()) + typesItems
    private var selected = -1
    private var prefs: SharePrefs? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllCategoriesViewHolder {
        prefs = SharePrefs.getInstance(context)
        val inflater = LayoutInflater.from(context)
        val binding = CategoriesItemLayoutBinding.inflate(inflater, parent, false)
        return AllCategoriesViewHolder(binding)
    }

    fun setSelection(position: Int) {
        selected = position
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AllCategoriesViewHolder, position: Int) {
        val data = newList[position]
        if (holder.adapterPosition == 0) {
            holder.binding.tvCategoryTitle.text = "All"
            holder.binding.ivCategory.visibility = View.GONE
        } else {
            holder.binding.tvCategoryTitle.text = data.categoryName
            holder.binding.ivCategory.visibility = View.VISIBLE
        }

        Glide.with(context).load("${prefs?.voucherBaseUrl}${data.cat_icon}").placeholder(R.drawable.ic_shopping_bag_demo)
            .into(holder.binding.ivCategory)

        if (selected == holder.adapterPosition) {
            holder.binding.llBg.setBackgroundResource(R.drawable.categories_item_bg_selected_new)
            holder.binding.tvCategoryTitle.setTextColor(
                ContextCompat.getColor(
                    context, R.color.colorPrimary
                )
            )

        } else {
            holder.binding.llBg.setBackgroundResource(R.drawable.categories_item_bg)
            holder.binding.tvCategoryTitle.setTextColor(
                ContextCompat.getColor(
                    context, R.color.colorText
                )
            )
        }


        holder.itemView.setOnClickListener {
            if (data.categoryName == "All") {
                mainInterface.onCategoriesClicked("", 0)
            } else {
                mainInterface.onCategoriesClicked(data.id.toString(), position)
            }
        }

    }

    override fun getItemCount(): Int {
        return newList.size
    }

    inner class AllCategoriesViewHolder(val binding: CategoriesItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

}