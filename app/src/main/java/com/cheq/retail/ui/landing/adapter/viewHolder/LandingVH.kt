package com.cheq.retail.ui.landing.adapter.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.cheq.retail.ui.landing.model.LandingItemVO

abstract class LandingVH<T>(itemView: View) : ViewHolder(itemView) where T : LandingItemVO {

    abstract fun bind(item: T)
}