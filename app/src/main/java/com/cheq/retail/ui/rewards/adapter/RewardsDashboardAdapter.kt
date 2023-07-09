package com.cheq.retail.ui.rewards.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.cheq.retail.R
import com.cheq.retail.constants.Constant
import com.cheq.retail.constants.Constant.Companion.URL
import com.cheq.retail.databinding.*
import com.cheq.retail.extensions.faqsBaseUrl
import com.cheq.retail.extensions.voucherBaseUrl
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.accountSettings.webView.CommonWebViewActivity
import com.cheq.retail.ui.dashboard.view.adapter.OffersForYouWidgetsAdapter
import com.cheq.retail.ui.dashboard.view.fragment.HomeFragment
import com.cheq.retail.ui.dashboard.view.fragment.HomeFragment.Companion.CLOSE_SCREEN
import com.cheq.retail.ui.deeplinkHandler.DeepLinkHandler
import com.cheq.retail.ui.main.OffersItem
import com.cheq.retail.ui.main.maininterface.RewardsInterface
import com.cheq.retail.ui.main.model.RewardModuleItem
import com.cheq.retail.ui.main.model.TypesItem
import com.cheq.retail.ui.rewards.view.RewardsCoinHistoryActivity
import com.cheq.retail.utils.ImageUtils.loadSvg
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.Utils

class RewardsDashboardAdapter(
    private val context: Context,
    private val list: ArrayList<RewardModuleItem>,
    private val howRewardWorks: Boolean?,
    private val coinBalance: Double?,
    private val lockedChips: Int?,
    private val convertToCash: Double?,
    private val convertToVoucher: Double?,
    private val earnUpTo: Double?,
    private val rewardsInterface: RewardsInterface,
    private val onTopFeatureDealClicked: TopFeaturedDealAdapter.OnFeatureDealClicked,
    private val onOtherFeatureDealClicked: OtherFeaturedDealAdapter.OnOtherFeatureDealClicked,
    private var offerForYou: ArrayList<OffersItem>,
    private var offerTitle: String?

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isOffervisible: Boolean = false
    lateinit var prefs: SharePrefs
    private var offersForYouWidgetsAdapter: OffersForYouWidgetsAdapter? = null
    fun setOfferForYouList(localOfferForYou: ArrayList<OffersItem>, smallOfferWidgetTitle: String) {
        offerForYou = localOfferForYou
        offerTitle = smallOfferWidgetTitle
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        prefs = SharePrefs.getInstance(context)!!
        when (viewType) {
            THE_SECOND_VIEW -> {
                return DynamicEarnMoreViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(context), R.layout.earn_more_layout, parent, false
                    )
                )
            }
            THE_FIRST_VIEW -> {
                return DynamicHeaderViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(context), R.layout.layout_rewards, parent, false
                    )
                )
            }
            THE_THIRD_VIEW -> {
                return DynamicFeaturedDealViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(context),
                        R.layout.layout_top_feature_deal,
                        parent,
                        false
                    )
                )
            }

            THE_FOURTH_VIEW -> {
                return DynamicExploreVoucherViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(context), R.layout.layout_vouchers, parent, false
                    )
                )
            }

            THE_FIFTH_VIEW -> {
                return DynamicOffersViewHolder(

                    DataBindingUtil.inflate(
                        LayoutInflater.from(context), R.layout.layout_offers, parent, false
                    )
                )
            }

            THE_SIXTH_VIEW -> {
                return DynamicDonationViewHolder(
                    LayoutInflater.from(context).inflate(R.layout.layout_donation, parent, false)
                )
            }

            THE_SEVENTH_VIEW -> {
                return DynamicFooterViewHolder(
                    LayoutInflater.from(context).inflate(R.layout.layout_footer, parent, false)
                )
            }

            THE_EIGHT_VIEW -> {
                return DynamicConvertToCashViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(context), R.layout.layout_convert_to_cash, parent, false
                    )
                )
            }

            THE_NINE_VIEW -> {
                return DynamicOtherFeatureDealViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(context),
                        R.layout.layout_other_feature_deal,
                        parent,
                        false
                    )
                )
            }


            else -> {
                return DynamicHeaderViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(context), R.layout.layout_rewards, parent, false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (list[position].moduleName == FOOTER) {

        } else if (list[position].moduleName == DONATION) {
            Glide.with(context).load(list[position].image)
                .into((holder as DynamicDonationViewHolder).ivDonate)
        } else if (list[position].moduleName == FEATURE_DEAL) {
            (holder as DynamicFeaturedDealViewHolder).binding.topFeaturedDeal.vpTopFeaturedDeal.apply {
                adapter = TopFeaturedDealAdapter(
                    context, list[position].types as ArrayList<TypesItem>, onTopFeatureDealClicked
                )
                hasFixedSize()
            }
            var module_bg_url = "${prefs?.voucherBaseUrl}/${list[position].moduleBgImg}"
            /*   val layoutParams =
                   holder.binding.topFeaturedDeal.mainContent.layoutParams as ConstraintLayout.LayoutParams
               layoutParams.dimensionRatio = list[position].moduleBgImgRatio
               holder.binding.topFeaturedDeal.mainContent.layoutParams = layoutParams */

            if (!list[position].moduleBgImg.isNullOrEmpty()) {
                holder.binding.mainBg.loadSvg(module_bg_url, placeholder = R.drawable.bg_grey_rectangle)
            } else {
                holder.binding.topFeaturedDeal.mainContent.setBackgroundColor(
                    context.resources.getColor(
                        R.color.colorHeaderBg
                    )
                )
            }

            holder.binding.topFeaturedDeal.tvCoinCount.text =
                "1 = ₹${convertToVoucher?.let { Utils.convertDecimalFormate(it) }}"


        } else if (list[position].moduleName == OTHER_FEATURE_DEAL) {
            (holder as DynamicOtherFeatureDealViewHolder).binding.topFeaturedDeal.vpTopFeaturedDeal.apply {
                adapter = OtherFeaturedDealAdapter(
                    context, list[position].types as ArrayList<TypesItem>, onOtherFeatureDealClicked
                )
                hasFixedSize()
            }
            var module_bg_url = "${prefs?.voucherBaseUrl}/${list[position].moduleBgImg}"
            /*   val layoutParams =
                   holder.binding.topFeaturedDeal.mainContent.layoutParams as ConstraintLayout.LayoutParams
               layoutParams.dimensionRatio = list[position].moduleBgImgRatio
               holder.binding.topFeaturedDeal.mainContent.layoutParams = layoutParams*/
            if (!list[position].moduleBgImg.isNullOrEmpty()) {
                holder.binding.mainBg.loadSvg(module_bg_url)
            } else {
                holder.binding.topFeaturedDeal.mainContent.setBackgroundColor(
                    context.resources.getColor(
                        R.color.colorHeaderBg
                    )
                )
            }


            holder.binding.topFeaturedDeal.tvCoinCount.text =
                "1 = ₹${convertToVoucher?.let { Utils.convertDecimalFormate(it) }}"


        } else if (list[position].moduleName == OTHER_OFFERS) {
            (holder as DynamicOffersViewHolder).binding.otherOffers.rvOtherOffers.apply {
                adapter = OffersAdapters(
                    context, list[position].types as ArrayList<TypesItem>, onTopFeatureDealClicked
                )
                hasFixedSize()
            }
        } else if (list[position].moduleName == EXPLORE_VOUCHER) {

            (holder as DynamicExploreVoucherViewHolder).binding.exploreVoucher.rvExploreVoucher.apply {
                adapter =
                    ExploreVoucherAdapter(context, list[position].types as ArrayList<TypesItem>)
                hasFixedSize()
            }
            holder.binding.exploreVoucher.tvCoinCount.text =
                "1 = ₹${convertToVoucher?.let { Utils.convertDecimalFormate(it) }}"
        } else if (list[position].moduleName == EARN_MORE) {
            val adapterNew =
                EarnMoreVPAdapter(context, list[position].types as ArrayList<TypesItem>)
            (holder as DynamicEarnMoreViewHolder).binding.vpEarnMore.apply {
                adapter = adapterNew
            }
            list[position].types?.size?.let {
                setUpVp(
                    holder.binding.vpEarnMore,
                    it,
                    list[position].types?.size!!,
                    holder.binding.llDots
                )
            }
        } else if (list[position].moduleName == HEADER) {

            MparticleUtils.logCurrentScreen(
                "/rewards-tab",
                "The rewards tab completely loads, and customer views the tab",
                "chips-balance",
                "${coinBalance?.toInt()}",
                "",
                "",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.REWARD_TAB),
                context
            )
            (holder as DynamicHeaderViewHolder).binding.tvCoinBalance.text =
                "${coinBalance?.toInt()}"


            holder.binding.waitingCoinBalanceTV.text = lockedChips.toString()

            (holder).binding.waitingCoinGroup.isVisible = lockedChips != 0

            if (howRewardWorks == true) holder.binding.ivRewardsWorks.visibility =
                View.GONE else holder.binding.ivRewardsWorks.visibility = View.GONE

            holder.binding.clYourVouchers.setOnClickListener {
                context.startActivity(Intent(context, RewardsCoinHistoryActivity::class.java))
            }
            holder.binding.tvHelp.setOnClickListener {
                var url = "${prefs.faqsBaseUrl}${context.getString(R.string.cheq_chip_faq)}"
                context.startActivity(Intent(context, CommonWebViewActivity::class.java).apply {
                    putExtra("URL", url)
                })

            }
        } else if (list[position].moduleName == C2C) {

            (holder as DynamicConvertToCashViewHolder).binding.convertToCcash.tvCoinCount.text =
                context.getString(R.string.str_convert_to_cash_value, convertToCash)
            if ((earnUpTo?.toInt() ?: 0) <= 0) {
                holder.binding.convertToCcash.tvRewardAmount.text = context.getString(R.string._0)
                holder.binding.convertToCcash.tvLearnMore.visibility = View.VISIBLE
                holder.binding.convertToCcash.tvGetCashInstantly.text =
                    context.getString(R.string.cash_balance)
                holder.binding.convertToCcash.clConvertToCash.setOnClickListener {
                    convertToCash?.let { it1 ->
                        rewardsInterface.onMyC2CClicked(
                            true, earnUpTo?.toFloat() ?: 0f, it1
                        )
                    }
                }
            } else {
                holder.binding.convertToCcash.tvRewardAmount.text = "₹${earnUpTo?.toInt()}"
                holder.binding.convertToCcash.clConvertToCash.setOnClickListener {
                    convertToCash?.let { it1 ->
                        rewardsInterface.onMyC2CClicked(
                            true, earnUpTo?.toFloat() ?: 0f, it1
                        )
                    }
                }
            }
            if (offerForYou.isNotEmpty()) {

                holder.binding.convertToCcash.tvOffersForYou.visibility = View.VISIBLE
                holder.binding.convertToCcash.tvOffersForYou.text = offerTitle?:context.getString(R.string.offers_for_you)
                holder.binding.convertToCcash.rvOffersForYou.visibility = View.VISIBLE
                offersForYouWidgetsAdapter =
                    OffersForYouWidgetsAdapter(context = context, offersItem = offerForYou)
                holder.binding.convertToCcash.rvOffersForYou.adapter = offersForYouWidgetsAdapter
                holder.binding.convertToCcash.rvOffersForYou.hasFixedSize()

                offersForYouWidgetsAdapter?.onItemClick = { it, position ->
                    MparticleUtils.logEvent(
                        "Banner_Clicked",
                        "user clicks on the banner",
                        "Unique",
                        "Continue",
                        null,
                        context,
                        otherAttributes = mapOf(
                            "position" to position.toString(),
                            "screen" to "rewards",
                            "banner-type" to "big",
                            "offer-name" to (it?.meta_data ?: "")
                        )
                    )
                    if (it?.banner_redirection_type == HomeFragment.POP_UP) {
                        openOfferPopUp(it)
                    } else if (it?.banner_redirection_type == HomeFragment.DEEP_LINK) {
                        it.banner_redirection
                            ?.let {
                                DeepLinkHandler.getDeeplinkIntent(context, it)
                            }
                            ?.let {
                                context.startActivity(it)
                            }
                    } else if (it?.banner_redirection_type == HomeFragment.WEB_LINK) {
                        context.startActivity(
                            Intent(context, CommonWebViewActivity::class.java).putExtra(
                                URL, "${it.banner_redirection}"
                            )
                        )
                    }
                }
            } else {
                holder.binding.convertToCcash.tvOffersForYou.visibility = View.GONE
                holder.binding.convertToCcash.rvOffersForYou.visibility = View.GONE
            }
        }

    }

    private fun openOfferPopUp(item: OffersItem) {
        val dialog = Utils.showCustomDialogBottum(context, R.layout.bottom_sheet_pop_up)
        val bindingSheet = DataBindingUtil.inflate<BottomSheetPopUpBinding>(
            dialog.layoutInflater, R.layout.bottom_sheet_pop_up, null, false
        )
        dialog.setContentView(bindingSheet.root)
        dialog.setCancelable(false)
        bindingSheet.ivCancel.setOnClickListener {
            dialog.dismiss()
        }
        bindingSheet?.tvTandC?.setOnClickListener {
            context.startActivity(
                Intent(context, CommonWebViewActivity::class.java).putExtra(
                    URL,
                    "${item.banner_details?.tncLink}"
                )
            )
            dialog.dismiss()
        }

        if (item.banner_details?.cta?.primary?.text.isNullOrEmpty()) {
            bindingSheet.btnPrimary.visibility = View.INVISIBLE
        } else {
            bindingSheet.btnPrimary.visibility = View.VISIBLE
        }
        if (item.banner_details?.cta?.secondary?.text.isNullOrEmpty()) {
            bindingSheet.btnSecondary.visibility = View.INVISIBLE
        } else {
            bindingSheet.btnSecondary.visibility = View.VISIBLE
        }

        bindingSheet?.btnPrimary?.setOnClickListener {
            MparticleUtils.logEvent(
                "Offer_PrimaryCTA_Clicked",
                "When user clicks on primary CTA",
                "Unique",
                bindingSheet.btnPrimary.text.toString(),
                null,
                context
            )

            if (item.banner_details?.cta?.primary?.action_type == HomeFragment.DEEP_LINK) {
                item.banner_details.cta?.primary?.action
                    ?.let {
                        DeepLinkHandler.getDeeplinkIntent(context, it)
                    }
                    ?.let {
                        context.startActivity(it)
                    }
                dialog.dismiss()
            } else if (item.banner_details?.cta?.primary?.action_type == HomeFragment.WEB_LINK) {
                context.startActivity(
                    Intent(context, CommonWebViewActivity::class.java).putExtra(
                        Constant.URL,
                        "${item.banner_details.cta.primary.action}"
                    )
                )
                dialog.dismiss()
            }
        }
        bindingSheet.btnSecondary.setOnClickListener {
            MparticleUtils.logEvent(
                "Offer_SecondaryCTA_Clicked",
                "When user clicks on primary CTA",
                "Unique",
                "Continue",
                null,
                context
            )
            if (item.banner_details?.cta?.secondary?.action_type == CLOSE_SCREEN) {
                dialog.dismiss()
            } else {
                dialog.dismiss()
            }

        }
        val layoutParams = bindingSheet.flMainContent.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.dimensionRatio = item.banner_details?.aspect_ratio
        bindingSheet.flMainContent.layoutParams = layoutParams
        if (item.banner_details?.descprition_asset_type == 1) {
            Glide.with(context).load(item.banner_details.descprition_asset)
                .into(bindingSheet.ivOfferImage)
        } else {
            bindingSheet.animationView.setFailureListener {

            }
            bindingSheet.animationView.setAnimationFromUrl(
                item.banner_details?.descprition_asset ?: ""
            )

        }


        bindingSheet.btnPrimary.text = item.banner_details?.cta?.primary?.text ?: ""
        bindingSheet.btnSecondary.text = item.banner_details?.cta?.secondary?.text ?: ""
        dialog.show()
    }


    override fun getItemCount(): Int {
        return list.size
    }

    inner class DynamicEarnMoreViewHolder(val binding: EarnMoreLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)


    inner class DynamicHeaderViewHolder(val binding: LayoutRewardsBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class DynamicFeaturedDealViewHolder(val binding: LayoutTopFeatureDealBinding) :
        RecyclerView.ViewHolder(binding.root)


    inner class DynamicOtherFeatureDealViewHolder(val binding: LayoutOtherFeatureDealBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class DynamicExploreVoucherViewHolder(val binding: LayoutVouchersBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class DynamicOffersViewHolder(val binding: LayoutOffersBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class DynamicDonationViewHolder(binding: View) : RecyclerView.ViewHolder(binding) {
        var ivDonate: AppCompatImageView = binding.findViewById(R.id.ivDonate)
    }

    inner class DynamicFooterViewHolder(binding: View) : RecyclerView.ViewHolder(binding) {
        var ivFooter: AppCompatImageView = binding.findViewById(R.id.ivFooter)
    }

    inner class DynamicConvertToCashViewHolder(val binding: LayoutConvertToCashBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        const val THE_FIRST_VIEW = 1
        const val THE_SECOND_VIEW = 2
        const val THE_THIRD_VIEW = 3
        const val THE_FOURTH_VIEW = 4
        const val THE_FIFTH_VIEW = 5
        const val THE_SIXTH_VIEW = 6
        const val THE_SEVENTH_VIEW = 7
        const val THE_EIGHT_VIEW = 8
        const val THE_NINE_VIEW = 9
        const val THE_TENTH_VIEW = 10

        const val HEADER = "header"
        const val EARN_MORE = "earnMore"
        const val FEATURE_DEAL = "featuredDeal"
        const val OTHER_FEATURE_DEAL = "otherFeaturedDeal"
        const val EXPLORE_VOUCHER = "exploreVoucher"
        const val OTHER_OFFERS = "otherOffers"
        const val DONATION = "donation"
        const val FOOTER = "footer"
        const val C2C = "c2c"
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position].moduleName) {
            HEADER -> THE_FIRST_VIEW
            EARN_MORE -> THE_SECOND_VIEW
            FEATURE_DEAL -> THE_THIRD_VIEW
            EXPLORE_VOUCHER -> THE_FOURTH_VIEW
            OTHER_OFFERS -> THE_FIFTH_VIEW
            DONATION -> THE_SIXTH_VIEW
            FOOTER -> THE_SEVENTH_VIEW
            C2C -> THE_EIGHT_VIEW
            OTHER_FEATURE_DEAL -> THE_NINE_VIEW
            else -> THE_TENTH_VIEW
        }
    }

    private fun setUpVp(
        viewPager2: ViewPager2, size: Int, newSize: Int, linearLayoutCompat: LinearLayoutCompat
    ) {
        viewPager2.apply {
            clipToPadding = false
            clipChildren = false
        }
        val pageTranslationX = 0 + 102
        val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
            page.translationX = -pageTranslationX * position
            page.scaleY = 1 - (0.00f * kotlin.math.abs(position))
        }
        viewPager2.setPageTransformer(pageTransformer)
        viewPager2.currentItem = 1
        viewPager2.offscreenPageLimit = 1
        onInfinitePageChangeCallback(newSize + 2, viewPager2, linearLayoutCompat)
        setUpIndicator(linearLayoutCompat, newSize)

    }

    private fun onInfinitePageChangeCallback(
        listSize: Int, viewPager2: ViewPager2, linearLayoutCompat: LinearLayoutCompat
    ) {
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)

                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    when (viewPager2.currentItem) {
                        listSize - 1 -> {
                            viewPager2.setCurrentItem(1, false)
                            setCurrentIndicator(0, linearLayoutCompat)
                        }

                        0 -> {
                            viewPager2.setCurrentItem(listSize - 2, false)
                            setCurrentIndicator(listSize - 3, linearLayoutCompat)
                        }

                        1 -> {
                            setCurrentIndicator(0, linearLayoutCompat)
                        }

                        else -> {
                            setCurrentIndicator(viewPager2.currentItem - 1, linearLayoutCompat)
                        }
                    }
                }
            }


        })
    }

    private fun setUpIndicator(linearLayoutCompat: LinearLayoutCompat, listSize: Int) {

        val indicators = arrayOfNulls<ImageView>(listSize)
        val layoutParams: LinearLayoutCompat.LayoutParams = LinearLayoutCompat.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(12, 0, 0, 0)
        linearLayoutCompat.removeAllViews()
        for (i in indicators.indices) {
            indicators[i] = ImageView(context)
            indicators[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        context, R.drawable.ic_indicator_inactive
                    )
                )
                this?.layoutParams = layoutParams
            }
            linearLayoutCompat.addView(indicators[i])
        }
    }

    private fun setCurrentIndicator(index: Int, linearLayoutCompat: LinearLayoutCompat) {
        val childCount = linearLayoutCompat.childCount
        for (i in 0 until childCount) {
            val imageView = linearLayoutCompat[i] as ImageView
            when (i) {
                index -> {
                    imageView.setImageDrawable(
                        ContextCompat.getDrawable(
                            context, R.drawable.ic_indicator_active
                        )
                    )

                }

                0 -> {
                    imageView.setImageDrawable(
                        ContextCompat.getDrawable(
                            context, R.drawable.ic_indicator_inactive_small
                        )
                    )
                }

                childCount - 1 -> {
                    imageView.setImageDrawable(
                        ContextCompat.getDrawable(
                            context, R.drawable.ic_indicator_inactive_small
                        )
                    )
                }

                else -> {
                    imageView.setImageDrawable(
                        ContextCompat.getDrawable(
                            context, R.drawable.ic_indicator_inactive
                        )
                    )

                }
            }

        }
    }

}