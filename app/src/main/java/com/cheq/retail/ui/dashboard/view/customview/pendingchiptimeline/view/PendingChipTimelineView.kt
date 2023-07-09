package com.cheq.retail.ui.dashboard.view.customview.pendingchiptimeline.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.cheq.retail.R
import com.cheq.retail.ui.dashboard.view.customview.pendingchiptimeline.data.ChipLineItem

class PendingChipTimelineView(context: Context, attrs: AttributeSet?) :
    FrameLayout(context, attrs) {

    init {

        val childView = LayoutInflater.from(context).inflate(
            R.layout.view_pending_chip_timeline, this, false
        )

        addView(childView)
    }

    private val childView: View
        get() = getChildAt(0)

    fun setData(data: List<ChipLineItem>) {
        val chipContainerLayout = childView.findViewById<LinearLayout>(R.id.chipContainerLayout)
        chipContainerLayout.removeAllViews()

        for (item in data) {
            val itemView = LayoutInflater.from(context).inflate(
                R.layout.view_pending_chip_timeline_item, this, false
            )
            val countTV = itemView.findViewById<TextView>(R.id.countTV)
            val textTV = itemView.findViewById<TextView>(R.id.textTV)

            countTV.text = item.count.toString()
            textTV.text = item.text

            chipContainerLayout.addView(itemView)
        }

        invalidate()
        requestLayout()
    }

}