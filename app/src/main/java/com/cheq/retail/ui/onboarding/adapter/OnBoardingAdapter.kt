package com.cheq.retail.ui.onboarding.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cheq.retail.R
import com.cheq.retail.databinding.OnBoardingItemLayoutBinding
import com.cheq.retail.ui.onboarding.model.OnBoardingItemModel

class OnBoardingAdapter(
    private val context: Context,
    private val onBoardingItemList: ArrayList<OnBoardingItemModel>,

    ) : RecyclerView.Adapter<OnBoardingAdapter.OnBoardingViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OnBoardingAdapter.OnBoardingViewHolder {
        val binding =
            OnBoardingItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return OnBoardingViewHolder(binding)
    }


    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: OnBoardingAdapter.OnBoardingViewHolder, position: Int) {
        val item = onBoardingItemList[position]

        /* if (position == 0) {
             holder.mBinding.tvTitle.text = onBoardingItemList[0].onBoardingTitle
         } else if (position == 1) {
             holder.mBinding.tvTitle.setTextColor(R.color.colorTextGrey)
             holder.mBinding.tvTitle.text = onBoardingItemList[0].onBoardingTitle
             holder.mBinding.tvTitle1.text = onBoardingItemList[1].onBoardingTitle
             holder
         } else if (position == 2) {
             holder.mBinding.tvTitle.setTextColor(R.color.colorTextGrey)
             holder.mBinding.tvTitle1.setTextColor(R.color.colorTextGrey)
             holder.mBinding.tvTitle.text = onBoardingItemList[0].onBoardingTitle
             holder.mBinding.tvTitle1.text = onBoardingItemList[1].onBoardingTitle
             holder.mBinding.tvTitle2.text = onBoardingItemList[2].onBoardingTitle
         }*/
//        holder.mBinding.tvTitle.text = item.onBoardingTitle
        holder.mBinding.animationOne.setAnimation(item.animationJson)
    }

    override fun getItemCount(): Int {
        return onBoardingItemList.size
    }

    inner class OnBoardingViewHolder(val mBinding: OnBoardingItemLayoutBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(onBoardingItemModel: OnBoardingItemModel) {
            mBinding.onBoardingItem = onBoardingItemModel
            mBinding.executePendingBindings()
        }
    }
}