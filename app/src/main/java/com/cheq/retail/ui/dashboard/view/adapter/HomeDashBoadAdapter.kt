package com.cheq.retail.ui.dashboard.view.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cheq.retail.ui.dashboard.model.homedashboad.*
import com.cheq.retail.ui.rewards.adapter.RewardsDashboardAdapter

class HomeDashBoadAdapter(var widgets: ArrayList<HomeDashBoardResponse>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO()
    }

    override fun getItemCount(): Int {
        return 3
    }


    companion object {
        const val THE_FIRST_VIEW = 1
        const val THE_SECOND_VIEW = 2
        const val THE_THIRD_VIEW = 3
//        const val THE_FOURTH_VIEW = 4
//        const val THE_FIFTH_VIEW = 5
//        const val THE_SIXTH_VIEW = 6
//        const val THE_SEVENTH_VIEW = 7
//        const val THE_EIGHT_VIEW = 8
    }

    override fun getItemViewType(position: Int): Int {
//        return when (widgets[position].widgets) {
//            RewardWidget -> HomeDashBoadAdapter.THE_FIRST_VIEW
//             BalanceDueWidget -> HomeDashBoadAdapter.THE_SECOND_VIEW
//            CreditHealthWidget -> HomeDashBoadAdapter.THE_THIRD_VIEW
//
//            else -> 9
//        }
        TODO()
    }
}