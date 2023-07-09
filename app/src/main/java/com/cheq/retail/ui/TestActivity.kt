package com.cheq.retail.ui
/*

import android.os.Bundle
import android.text.SpannableString
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.cheq.retail.R
import com.cheq.retail.databinding.ActivityWaitList2Binding
import java.text.DecimalFormat

class TestActivity : BaseActivity() {

    sealed class TestData {

        abstract val referralOwnEarning: Int
        abstract val referralOtherEarning: Int

        data class FirstTime(
            val position: Long,
            val earnedChips: Int?,
            override val referralOwnEarning: Int,
            override val referralOtherEarning: Int,
        ) : TestData()

        data class SecondTime(
            val position: Long,
            val totalInLine: Long?,
            val referredCount: Int,
            val toReferCount: Int?,
            val earnedChips: Int,
            override val referralOwnEarning: Int,
            override val referralOtherEarning: Int
        ) : TestData()

        data class Failed(
            val titleText: String,
            override val referralOwnEarning: Int,
            override val referralOtherEarning: Int
        ) : TestData()
    }

    private val firstTime = TestData.FirstTime(
        position = 100,
        earnedChips = null,
        referralOwnEarning = 50,
        referralOtherEarning = 60
    )

    private val secondTime = TestData.SecondTime(
        10000,
        null,
        referredCount = 10,
        toReferCount = 10,
        earnedChips = 200,
        referralOwnEarning = 50,
        referralOtherEarning = 60
    )

    private val failed =
        TestData.Failed(
            titleText = "Can't add to you waitlist as we are not able to find your details",
            referralOwnEarning = 50,
            referralOtherEarning = 60
        )

    private val data: TestData = secondTime

    private lateinit var binding: ActivityWaitList2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityWaitList2Binding>(
            this,
            R.layout.activity_wait_list_2
        )

        setupView()
    }

    private fun setupView() {
        binding.firstTimeMainContentGroup.isVisible = false
        binding.firstTimeMainContentEarnedChipsGroup.isVisible = false
        binding.secondTimeMainContentGroup.isVisible = false
        binding.secondTimeMainContentTotalInLineGroup.isVisible = false
        binding.secondTimeBodyContentGroup.isVisible = false
        binding.failureReferAndShareContainer.isVisible = false
        binding.bottomReferAndShareCV.isVisible = false
        binding.failureMainContentGroup.isVisible = false
        binding.titleTV.isVisible = false
        when (data) {
            is TestData.FirstTime -> setupFirstTimeView(data)
            is TestData.SecondTime -> setupSecondTimeView(data)
            is TestData.Failed -> setupFailedView(data)
        }

        setupReferralView(
            data.referralOwnEarning,
            data.referralOtherEarning
        )
    }

    private fun setupReferralView(
        referralOwnEarning: Int,
        referralOtherEarning: Int,
    ) {
        binding.bottomReferAndShareCV.findViewById<TextView>(R.id.referTitleTV).text = getString(
            R.string.wait_list_referral_own_reward,
            referralOwnEarning
        )
        binding.bottomReferAndShareCV.findViewById<TextView>(R.id.referTitleTV2).text = getString(
            R.string.wait_list_referral_other_reward,
            referralOtherEarning
        )

        binding.failureReferAndShareContainer.findViewById<TextView>(R.id.referTitleTV).text =
            getString(
                R.string.wait_list_referral_own_reward,
                referralOwnEarning
            )

        binding.failureReferAndShareContainer.findViewById<TextView>(R.id.referTitleTV2).isVisible =
            false
    }


    private fun setupFirstTimeView(data: TestData.FirstTime) {
        binding.titleTV.isVisible = true
        binding.bottomReferAndShareCV.isVisible = true
        binding.firstTimeMainContentGroup.isVisible = true

        binding.pointTV.text = DecimalFormat("##,###").format(data.position)
        binding.chipsTV.text = getString(R.string.cheq_chips, data.earnedChips)

        val hasEarnedChips = data.earnedChips != null

        binding.firstTimeMainContentEarnedChipsGroup.isVisible = hasEarnedChips

    }

    private fun setupSecondTimeView(data: TestData.SecondTime) {
        binding.bottomReferAndShareCV.isVisible = true
        binding.secondTimeMainContentGroup.isVisible = true

        binding.totalInLinePositionTV.text = DecimalFormat("##,###").format(data.position)

        binding.totalInLineTV.text = data.totalInLine?.let { DecimalFormat("##,###").format(it) }

        binding.secondTimeMainContentTotalInLineGroup.isVisible = data.totalInLine != null

        binding.secondTimeBodyContentGroup.isVisible = true

        binding.totalReferTV.text = data.referredCount.toString()
        binding.totalChipTV.text = data.earnedChips.toString()
    }

    private fun setupFailedView(data: TestData.Failed) {
        binding.failureReferAndShareContainer.isVisible = true

        binding.failureTitleTV.text = data.titleText
    }

}*/
