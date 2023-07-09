package com.cheq.retail.ui.rewards.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Build
import android.util.TypedValue
import android.view.Display
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cheq.retail.R
import com.cheq.retail.databinding.ExploreVouchersItemLayoutBinding
import com.cheq.retail.extensions.voucherBaseUrl
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.main.MainActivity
import com.cheq.retail.ui.main.model.TypesItem
import com.cheq.retail.ui.rewards.view.ViewAllVoucherActivity
import com.cheq.retail.utils.MparticleUtils


class ExploreVoucherAdapter(
    private val context: Context, private val vouchersList: ArrayList<TypesItem>
) : RecyclerView.Adapter<ExploreVoucherAdapter.ExploreVoucherViewHolder>() {
    private var prefs: SharePrefs? = null
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ExploreVoucherViewHolder {
        prefs = SharePrefs.getInstance(context)
        val inflater = LayoutInflater.from(context)
        val binding = ExploreVouchersItemLayoutBinding.inflate(inflater, parent, false)
        binding.root.layoutParams =
            configureCardViewLayoutParams(binding.root as ConstraintLayout, 4, 20f, 2f, 2f, 8f, 8f)
        return ExploreVoucherViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ExploreVoucherViewHolder, position: Int
    ) {
        if (vouchersList.isNotEmpty()) {
            val sortedList = vouchersList.sortedBy {
                it.sequence
            }

            val data = sortedList[position]
            Glide.with(context).load(  "${prefs?.voucherBaseUrl}${data.bgImage}").into(holder.binding.ivExplore)
            holder.binding.tvTitle.text = data.categoryName
            if (holder.adapterPosition == 3) {
                Glide.with(context).load(R.drawable.ic_see_all).into(holder.binding.ivExplore)
                holder.binding.tvTitle.text = "See All"
            }

            holder.itemView.setOnClickListener {
                if (holder.adapterPosition == 3) {
                    context.startActivity(
                        Intent(
                            context, ViewAllVoucherActivity::class.java
                        ).putParcelableArrayListExtra("TYPE_ITEM", vouchersList)
                            .putExtra("TITLE", "VIEW_ALL")
                    )

                    MparticleUtils.logEvent(
                        "Rewards_ExploreVoucher_SeeAll_Clicked",
                        "",
                        "Unique",
                        "Continue",
                        SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Rewards_ExploreVoucher_SeeAll_Clicked),
                        context
                    )
                } else {
                    data.isSelected = true
                    context.startActivity(
                        Intent(
                            context, ViewAllVoucherActivity::class.java
                        ).putParcelableArrayListExtra("TYPE_ITEM", vouchersList)
                            .putExtra("TITLE", data.categoryName)
                    )
                }

                MparticleUtils.logEvent(
                    "Rewards_ExploreVoucher_Category_Clicked",
                    "",
                    "Unique",
                    "Continue",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Rewards_ExploreVoucher_Category_Clicked),
                  context
                )

            }
        }
    }

    override fun getItemCount(): Int {
        return 4
    }

    inner class ExploreVoucherViewHolder(val binding: ExploreVouchersItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}


private fun configureCardViewLayoutParams(
    cardView: ConstraintLayout,
    numOfCardViewItemsOnScreen: Int,
    marginAtRecyclerViewsEnds: Float,
    marginLeftParam: Float,
    marginRightParam: Float,
    marginTopParam: Float,
    marginBottomParam: Float
): ViewGroup.MarginLayoutParams {
    val numOfGapsInBetweenItems = numOfCardViewItemsOnScreen - 1
    var combinedGapWidth =
        ((marginLeftParam + marginRightParam) * numOfGapsInBetweenItems) + (marginAtRecyclerViewsEnds * 2)
    //Provided approx. adjustment of 2dp to prevent the extreme edges of the card from being cut-off
    combinedGapWidth += 2
    val combinedGapWidthDp = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, combinedGapWidth, cardView.resources.displayMetrics
    ).toInt()
    //Since margins/gaps are provided in-between the items, these have to be taken to account & subtracted from the width in-order to obtain even spacing
    cardView.layoutParams.width =
        (getScreenWidth(cardView.context as MainActivity) - combinedGapWidthDp) / numOfCardViewItemsOnScreen

    //Margins in-between the items
    val marginLeftOfItem = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, marginLeftParam, cardView.resources.displayMetrics
    ).toInt()
    val marginRightOfItem = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, marginRightParam, cardView.resources.displayMetrics
    ).toInt()
    val marginTopOfItem = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, marginTopParam, cardView.resources.displayMetrics
    ).toInt()
    val marginBottomOfItem = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, marginBottomParam, cardView.resources.displayMetrics
    ).toInt()

    //Providing the above margins to the CardView
    val cardViewMarginParams: ViewGroup.MarginLayoutParams =
        cardView.layoutParams as ViewGroup.MarginLayoutParams
    cardViewMarginParams.setMargins(
        marginLeftOfItem, marginTopOfItem, marginRightOfItem, marginBottomOfItem
    )
    return cardViewMarginParams
}

private fun getScreenWidth(activity: MainActivity): Int {
    var width: Int = 0
    val size = Point()
    val windowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    width = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val bounds = windowManager.currentWindowMetrics.bounds
        bounds.width()
    } else {
        val display: Display? = windowManager.defaultDisplay
        display?.getSize(size)
        size.x
    }
    return width
}
