package com.cheq.retail.ui.main.helper

import android.content.Context
import android.text.Html
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.cheq.retail.R

/**
 * this method will provide the required calculation for the user monthly earned cheQ chips and how much he will earn more
 */

object MonthlyEarnRule {
    fun setRewardLimit(
        rewardUser: Int,
        rewardsAssignUpto: Int,
        rewardsPoint: Int,
        setToRewardText: AppCompatTextView,
        setAtTheRate: AppCompatTextView,
        tvRewardPercentage: AppCompatTextView,
        tvRewardEarned: AppCompatTextView,

        context: Context
    ) {
        if (rewardUser == 0) {
            setToRewardText.text = context.getString(R.string.str_you_will_earn)
            setAtTheRate.visibility = View.GONE
            tvRewardPercentage.visibility = View.GONE
            tvRewardEarned.visibility = View.VISIBLE
        } else {
            if (rewardsAssignUpto == 0) {
                setToRewardText.text =
                    context.getString(R.string.str_you_will_earn_reward_user, rewardsPoint)
                setAtTheRate.visibility = View.VISIBLE
                tvRewardPercentage.visibility = View.VISIBLE
                tvRewardEarned.visibility = View.GONE
            } else {
                if (rewardUser < rewardsPoint) {
                    setToRewardText.text =
                        context.getString(R.string.str_you_will_earn_reward_user, rewardUser)
                    setAtTheRate.visibility = View.GONE
                    tvRewardPercentage.visibility = View.GONE
                    tvRewardEarned.visibility = View.VISIBLE

                } else {
                    setToRewardText.text =
                        context.getString(R.string.str_you_will_earn_reward_user, rewardsPoint)
                    setAtTheRate.visibility = View.VISIBLE
                    tvRewardPercentage.visibility = View.VISIBLE
                    tvRewardEarned.visibility = View.GONE

                }

            }

        }
    }

    fun setRewardLimit(
        rewardUser: Int,
        rewardsAssignUpto: Int,
        rewardsPoint: Int,
        tvRewardEarned: AppCompatTextView,
        context: Context
    ) {
        if (rewardUser == 0) {
            tvRewardEarned.text = context.getString(R.string.str_you_will_earn)
            tvRewardEarned.visibility = View.VISIBLE
        } else {
            if (rewardsAssignUpto == 0) {
                tvRewardEarned.text =
                    Html.fromHtml(context.getString(R.string.str_congrats_you_earned, rewardsPoint))
                tvRewardEarned.visibility = View.GONE
            } else {
                if (rewardUser < rewardsPoint) {
                    tvRewardEarned.text = Html.fromHtml(
                        context.getString(
                            R.string.str_congrats_you_earned,
                            rewardsPoint
                        )
                    )
                    tvRewardEarned.visibility = View.VISIBLE

                } else {
                    tvRewardEarned.text = Html.fromHtml(
                        context.getString(
                            R.string.str_congrats_you_earned,
                            rewardsPoint
                        )
                    )
                    tvRewardEarned.visibility = View.VISIBLE

                }

            }

        }
    }

    fun getLimit(
        rewardUser: Int,
        rewardsAssignUpto: Int,
        rewardsPoint: Int,
        tvRewardEarned: AppCompatTextView?,
        tvChipEarned: AppCompatTextView?,

    ) : Int{

        if (rewardUser == 0) {
            tvRewardEarned?.visibility = View.VISIBLE
        } else {
            if (rewardsAssignUpto == 0) {
                tvChipEarned?.text = "$rewardsPoint"
                tvRewardEarned?.visibility = View.GONE
                return rewardsPoint
            } else {
                return if (rewardUser < rewardsPoint) {
                    tvChipEarned?.text = "$rewardUser"
                    tvRewardEarned?.visibility = View.VISIBLE
                    rewardUser
                } else {
                    tvChipEarned?.text = "$rewardsPoint"
                    tvRewardEarned?.visibility = View.GONE
                    rewardsPoint
                }

            }

        }
        return 0
    }
}
