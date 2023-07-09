package com.cheq.retail.ui.dashboard.view.fragment

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.cheq.common.config.remoteconfig.RemoteConfig
import com.cheq.common.config.remoteconfig.RemoteConfig.Companion.NEW_ARCH_ENABLED
import com.cheq.config.FeatureConfig
import com.cheq.navigation.IntentFactory
import com.cheq.navigation.IntentKey
import com.cheq.navigation.IntentKey.MyAccountActivityKey.Companion.REFER_EARN_DESTINATION
import com.cheq.retail.R
import com.cheq.retail.api.errormodel.BlockData
import com.cheq.retail.application.MainApplication
import com.cheq.retail.constants.AFConstants
import com.cheq.retail.base.FirebaseRemoteConfigUtils
import com.cheq.retail.base.FirebaseRemoteConfigUtils.isNewArchEnabled
import com.cheq.retail.constants.BannerConstant
import com.cheq.retail.constants.Constant
import com.cheq.retail.constants.Constant.Companion.BIG_OFFER_WIDGET
import com.cheq.retail.constants.Constant.Companion.SMAIL_OFFER_WIDGET
import com.cheq.retail.databinding.BottomSheetPayMoreBinding
import com.cheq.retail.databinding.BottomSheetPopUpBinding
import com.cheq.retail.databinding.FragmentHomeBinding
import com.cheq.retail.extensions.bankMasterUrl
import com.cheq.retail.extensions.bannerBaseUrl
import com.cheq.retail.extensions.loanMasterUrl
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.accountSettings.webView.CommonWebViewActivity
import com.cheq.retail.ui.activateCard.CardDetailsActivityNew
import com.cheq.retail.ui.billPayments.PaymentMethodsActivity
import com.cheq.retail.ui.blocker_screen.BlockerActivity
import com.cheq.retail.ui.cheqsafe.CheqSafeParentFragment
import com.cheq.retail.ui.dashboard.model.ChartEntity
import com.cheq.retail.ui.dashboard.model.homedashboad.BalanceDueWidget
import com.cheq.retail.ui.dashboard.model.homedashboad.CreditHealthWidget
import com.cheq.retail.ui.dashboard.model.homedashboad.HomeDashBoardResponse
import com.cheq.retail.ui.dashboard.model.homedashboad.RewardWidget
import com.cheq.retail.ui.dashboard.model.homedashboad.WaitListRewardWidget
import com.cheq.retail.ui.dashboard.model.homedashboad.isUserBlocked
import com.cheq.retail.ui.dashboard.repository.HomeRepository
import com.cheq.retail.ui.dashboard.view.activity.CreditHealthActivity
import com.cheq.retail.ui.dashboard.view.adapter.HomeProductAdapter
import com.cheq.retail.ui.dashboard.view.adapter.OffersForYouWidgetsAdapter
import com.cheq.retail.ui.dashboard.view.adapter.SmallOfferWidgetsAdapter
import com.cheq.retail.ui.dashboard.view.adapter.ToDoVIewPagerAdapter
import com.cheq.retail.ui.dashboard.view.customview.pendingchiptimeline.data.ChipLineItem
import com.cheq.retail.ui.dashboard.viewModel.HomeDashBoardViewModelFactory
import com.cheq.retail.ui.dashboard.viewModel.HomeViewModel
import com.cheq.retail.ui.deeplinkHandler.DeepLinkHandler.Companion.getDeeplinkIntent
import com.cheq.retail.ui.login.modelv2.productv1.ProductV2
import com.cheq.retail.ui.main.MainActivity
import com.cheq.retail.ui.main.OffersItem
import com.cheq.retail.ui.main.ProductBottomSheetDialog
import com.cheq.retail.ui.main.fragment.AppMenuActivity
import com.cheq.retail.ui.main.fragment.ProductFragment.Companion.TAG
import com.cheq.retail.ui.main.helper.MonthlyEarnRule
import com.cheq.retail.ui.referral.ReferralActivity
import com.cheq.retail.ui.rewards.util.State
import com.cheq.retail.ui.socialLogin.model.CheqSafeScreens
import com.cheq.retail.ui.socialLogin.model.CheqSafeStatus
import com.cheq.retail.utils.CheqSafeRealtimeDatabase
import com.cheq.retail.utils.CommonFirebaseData
import com.cheq.retail.utils.DateTimeUtils
import com.cheq.retail.utils.GradientUtils
import com.cheq.retail.utils.ImageUtils.loadSvg
import com.cheq.retail.utils.IntentKeys
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.ProductStatus
import com.cheq.retail.utils.Utils
import com.cheq.retail.utils.Utils.Companion.activityAttached
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlin.math.floor

@AndroidEntryPoint
open class HomeFragment : Fragment(), HomeProductAdapter.UpdateOverDue {

    var mBinding: FragmentHomeBinding? = null
    private var activity: MainActivity? = null
    private var mFirebaseDatabase: DatabaseReference? = null
    private var mFirebaseInstance: FirebaseDatabase? = null
    lateinit var viewModel: HomeViewModel
    private lateinit var repository: HomeRepository
    val graph1 = floatArrayOf(600f, 700f, 750f, 822f, 840f, 900f)
    val legend = arrayListOf("APR", "MAY", "JUN", "JUL", "AUG", "SEP")
    var bankImageUrl = ""
    var billStatus = ""
    var rewardsPoint = 0
    lateinit var prefs: SharePrefs
    var smallViewVisibility = false
    var smallOfferWidgetList = arrayListOf<OffersItem>()
    private lateinit var offerWidgetAdapter: SmallOfferWidgetsAdapter
    private lateinit var offerForYouWidgetAdapter: OffersForYouWidgetsAdapter

    @Inject
    lateinit var intentFactory: IntentFactory

    @Inject
    lateinit var featureConfig: FeatureConfig

    /**
     * how much user can earn more this month
     */
    private var rewardsCanAssign = 0

    /**
     * what user has already earned this month
     */
    private var rewardsAssigned = 0

    /**
     * global CheQ Chips limit for the month. This is the overall static limit that will remain same across all users
     */
    private var rewardsAssignUpto = 0

    companion object {
        const val MIN = "min"
        const val CUSTOM = "custom"
        const val FULL = "full"
        const val DEFAULT_PERCENTAGE = 1.0
        const val POP_UP = "POP_UP"
        const val DEEP_LINK = "DEEP_LINK"
        const val WEB_LINK = "WEB_LINK"
        const val HOME = "HOME"
        const val CLOSE_SCREEN = "CLOSE_SCREEN"
        const val REWARDS = "REWARDS"
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window? = activity?.window
            window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window?.statusBarColor = Color.parseColor("#FAF8F8")
        }
        mFirebaseInstance =
            FirebaseDatabase.getInstance(com.cheq.retail.BuildConfig.firebaseDataBaseURL)
        mFirebaseDatabase = mFirebaseInstance?.reference
        prefs = SharePrefs.getInstance(requireActivity())!!
        mBinding?.layoutTotalDue?.llRewards?.setOnClickListener {
            activity?.setPayScreen(3, false)
            activity?.setBottomTab(3)
        }
        initLocalFirebase()
        // val firstChartEntity = ArrayList<Float>()
        val legendArr = ArrayList<String>()

        //   firstChartEntity.addAll(graph1)
        //   firstChartEntity.addAll(graph1)
        legendArr.addAll(legend)


        val firstChartItem = ChartEntity(Color.WHITE, Color.WHITE, graph1)


        val list2 = ArrayList<ChartEntity>().apply {
            add(firstChartItem)

        }


        // mBinding?.linechart?.setLegend(legendArr)
        //  mBinding?.linechart?.setList(list2)
        setupViewModel()
        setupObserver()
        mBinding?.layoutTotalDue?.ivView?.setOnClickListener {
            replaceFragments(false)
        }

        /*  mBinding?.ivCheqSafe?.setOnClickListener {
              //  replaceFragments(true)
          }*/

        mBinding?.ivRefer?.setOnClickListener {

            MparticleUtils.logEvent(
                "Home_ReferAndEarn_Banner_Clicked",
                "User clicks on the Refer and Earn banner",
                "Unique",
                "Continue",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Home_ReferAndEarn_Banner_Clicked),
                requireActivity()
            )
            if (featureConfig.isNewProfileEnabled()) {
                val intent = intentFactory.createIntent(
                    requireContext(),
                    IntentKey.MyAccountActivityKey(
                        cheqSafe = false,
                        startDestination = REFER_EARN_DESTINATION
                    )
                )
                startActivity(intent)
            } else {
                startActivity(
                    Intent(
                        activity, ReferralActivity::class.java
                    )
                )
            }
        }
        mBinding?.ivVoucher?.setOnClickListener {

            MparticleUtils.logEvent(
                "Home_Rewards_Banner_Clicked",
                "User clicks on the Rewards banner",
                "Unique",
                "Continue",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Home_Rewards_Banner_Clicked),
                requireActivity()
            )
            activity?.navController?.navigate(R.id.nav_rewards_fragment)
        }

        activity?.setBtnVisibilityFull(true)
        mBinding?.ivRefer?.loadSvg(
            prefs.bannerBaseUrl + "Refer-and-Earn.svg",
            loadingView = mBinding?.Homerefer?.ivHomePlaceholderTopBanner
        )
        mBinding?.ivVoucher?.loadSvg(
            prefs.bannerBaseUrl + "Rewards-Tab.svg",
            loadingView = mBinding?.HomeVoucher?.ivHomePlaceholderTopBanner
        )

        mBinding?.cheqSafeLayout?.clMainLayout?.setOnClickListener {
            startStartCheqSafeFlow()
        }



        return mBinding?.root
    }


    fun calTotalDue(amount_paid: Double?, totalDue: Double?): Int? {
        return amount_paid?.let {
            totalDue?.minus(
                it
            )?.toInt()
        }
    }


    private fun setupObserver() {
        Log.e(TAG, "${smallOfferWidgetList.size}")
        viewModel.homeDashBoard.observe(viewLifecycleOwner) {
            when (it) {
                is State.NetworkError -> {
                    Toast.makeText(activity, it.netWorkMessage, Toast.LENGTH_SHORT).show()
                    mBinding?.loading?.visibility = View.GONE
                }
                is State.Loading -> {
                    mBinding?.loading?.visibility = View.VISIBLE
                }
                is State.Error -> {
                    mBinding?.loading?.visibility = View.GONE
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                    //  Utils.hideProgressDialog()
                }
                is State.Success -> {
                    mBinding?.loading?.visibility = View.GONE
                    mBinding?.llHOme?.visibility = View.VISIBLE

                    if (it.data.isUserBlocked()) {
                        showErrorBlocker(it.data.blockedData)
                    } else {
                        setDataBoardData(it)
                    }
                }

                else -> {

                }
            }
        }

        viewModel.cheqSafeStatusObserver.observe(viewLifecycleOwner) {
            when (it) {
                CheqSafeStatus.NO_EMAIL_LINKED, CheqSafeStatus.FAILED -> {
                    checkOnFirebase(it.name)
                }
                CheqSafeStatus.LINKED -> {
                    mBinding?.cheqSafeLayout?.clMainLayout?.isVisible = false
                    mBinding?.tvActionRecommended?.isVisible = false
                }

            }
        }

    }

    private fun startStartCheqSafeFlow() {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.cheqSafeContainer, CheqSafeParentFragment())
        fragmentTransaction.commit()
    }

    fun getRemaingDays(dueDate: String?): Int {
        if (dueDate != null) {
            val c = Calendar.getInstance().time
            val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = df.format(c)
            val remainingDays = DateTimeUtils.getDaysBetweenDates(
                formattedDate, dueDate.replaceRange(
                    10, dueDate.substringBefore("T").length, ""
                )
            ).toString().toInt()
            println("totaldue++++ ${dueDate}")
            return remainingDays
        } else {
            return 0
        }

    }

    fun checkOnFirebase(status: String) {
        CheqSafeRealtimeDatabase.checkSafeFromFb(from = CheqSafeScreens.HOME, onValueFetched = {
            if(getActivity()?.activityAttached(this) == true) {
                if (it != null) {
                    Log.e("CHECK", it.toString())
                    mBinding?.cheqSafeLayout?.clMainLayout?.isVisible = it.isVisible
                    mBinding?.tvActionRecommended?.isVisible =
                        it.isVisible && (status == CheqSafeStatus.NO_EMAIL_LINKED.name || status == CheqSafeStatus.FAILED.name)
                    val bannerUrl =
                        if (status == CheqSafeStatus.FAILED.name) it.failedBannerUrl else it.successBannerUrl
                    mBinding?.cheqSafeLayout?.ivOfferImage?.let { it1 ->
                        Glide
                            .with(requireContext())
                            .load(bannerUrl)
                            .addListener(object : RequestListener<Drawable> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    mBinding?.cheqSafeLayout?.homeSmallBanner?.root?.isVisible =
                                        false
                                    mBinding?.cheqSafeLayout?.clMainLayout?.isVisible = false
                                    mBinding?.tvActionRecommended?.isVisible = false
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    mBinding?.cheqSafeLayout?.homeSmallBanner?.root?.isVisible =
                                        false
                                    return false
                                }

                            })
                            .into(it1)


                    }
                } else {
                    mBinding?.tvActionRecommended?.isVisible = false
                    mBinding?.cheqSafeLayout?.homeSmallBanner?.root?.isVisible =
                        false
                    mBinding?.cheqSafeLayout?.clMainLayout?.isVisible = false
                    mBinding?.tvActionRecommended?.isVisible = false
                }
            }
        })
    }


    private fun setDataBoardData(it: State.Success<HomeDashBoardResponse>) {
        var response = it.data
        if (response != null) {
            if (response?.rewardWidget != null) {
                setupRewarWidget(response?.rewardWidget)
            } else {
                mBinding?.layoutTotalDue?.tvRewardPoints?.text = "0"
            }

            if (response?.balanceDueWidget != null) {
                setUpBalanceDueWidget(response?.balanceDueWidget)

                if (response?.creditHealthWidget != null) {
                    if (response?.balanceDueWidget?.products?.isNotEmpty() == true) {
                        mBinding?.layoutCreditHealth?.llCreditHealth?.visibility = View.VISIBLE
                        //   mBinding?.creditHealthWidget2?.visibility = View.GONE
                        //  mBinding?.creditComparison?.visibility = View.GONE

                        // setUpHealthWidget(response.data?.creditHealthWidget)
                        setUpHealthWidget(response?.creditHealthWidget)
                        mBinding?.ivBottom?.visibility = View.VISIBLE

                    } else {
                        mBinding?.layoutCreditHealth?.llCreditHealth?.visibility = View.GONE
                        //  mBinding?.creditHealthWidget2?.visibility = View.VISIBLE
                        //   mBinding?.creditComparison?.visibility = View.VISIBLE
                        // setCreditWidget2(response.data?.creditHealthWidget)
                        setUpHealthWidget(response?.creditHealthWidget)
                        mBinding?.ivBottom?.visibility = View.VISIBLE

                    }
                } else {

                    mBinding?.ivBottom?.visibility = View.VISIBLE
                    mBinding?.layoutCreditHealth?.llCreditHealth?.visibility = View.GONE
                    //    mBinding?.creditHealWidget2?.visibility = View.GONE
                    mBinding?.creditComparison?.visibility = View.GONE

                }
            }

            setUpWaitlistRewardWidget(response?.waitlistRewardWidget)


            //mBinding?.layoutCreditHealth.pbHealth
            var arrayLIst = ArrayList<String>()
            arrayLIst.add("One")
            arrayLIst.add("Two")
            arrayLIst.add("Three")
            val adapterNew = context?.let { ToDoVIewPagerAdapter(it, arrayLIst) }

            mBinding?.vpToDO.apply {
                this?.adapter = adapterNew
            }

            arrayLIst.size.let {
                mBinding?.vpToDO?.let { it1 ->
                    mBinding?.llDots?.let { it2 ->
                        setUpVp(
                            it1, it, arrayLIst.size, it2
                        )
                    }
                }
            }

//            val text = "<font color=#282828>You’re among top</font> " +
//                    "<font color=#00C197>5% in India</font>"
//
//            val text2 = "<font color=#282828>You are in</font> " +
//                    "<font color=#00C197>Top 15%</font>" +
//                    "<font color=#282828> of people </font>" +
//                    "<font color=#00C197>in your area</font>"
//
//            val text3 = "<font color=#282828>You’re among</font> " +
//                    "<font color=#F6AD0B >67% in people like you</font>"
//
//            val text4 = "<font color=#282828>Your credit is</font> " +
//                    "<font color=#00C197>855</font>"
//
//            mBinding?.tvComp1?.text = (Html.fromHtml(text))
//            mBinding?.tvComp2?.text = (Html.fromHtml(text2))
//            mBinding?.tvComp3?.text = (Html.fromHtml(text3))
//            mBinding?.tvComp4?.text = (Html.fromHtml(text4))


        }
        rewardsAssignUpto = it.data?.rewardLimitManager?.rewardsAssignUpto ?: 0
        rewardsAssigned = it.data?.rewardLimitManager?.rewardsAssigned ?: 0
        rewardsCanAssign = it.data?.rewardLimitManager?.rewardsCanAssign ?: 0
    }

    private fun setUpWaitlistRewardWidget(waitlistRewardWidget: WaitListRewardWidget?) {

        if (waitlistRewardWidget == null) {
            mBinding?.pendingChipTimelineView?.isVisible = false
            return
        }

        val chipLine = waitlistRewardWidget.steps.map {
            ChipLineItem(
                count = it.chips, text = it.message
            )
        }
        mBinding?.pendingChipTimelineView?.isVisible = waitlistRewardWidget.visibility
        mBinding?.pendingChipTimelineView?.setData(chipLine)
    }

    private fun setUpHealthWidget(creditHealthWidget: CreditHealthWidget?) {
        if (creditHealthWidget?.bureauProvider != null) {
            mBinding?.layoutCreditHealth?.llCreditHealth?.visibility = View.VISIBLE
            if (creditHealthWidget.creditScoreValue != null) {
                mBinding?.layoutCreditHealth?.tvCreditScore?.text =
                    creditHealthWidget.creditScoreValue.toString()
                val creditHealth = Utils.getCreditHealthScore(creditHealthWidget.creditScoreValue)
                mBinding?.layoutCreditHealth?.creditScoreText?.apply {
                    creditHealth.apply {
                        text = first
                        setTextColor(second)
                    }
                }
            }

            val creaditHealthText = creditHealthWidget.creditScoreStateValueInterpretation
            val topValue = creditHealthWidget.creditScoreStateValue
            if (creditHealthWidget.creditScoreStateTemplate != null) {
                val topValueBefore =
                    creditHealthWidget.creditScoreStateTemplate?.substringBefore("{{")
                val topValueAfter =
                    creditHealthWidget.creditScoreStateTemplate?.substringAfter("}}")
                if (creaditHealthText != null) {
                    creditHealthWidget.creditScoreStateTemplate = creaditHealthText
                }

            }

        } else {
            mBinding?.layoutCreditHealth?.llCreditHealth?.visibility = View.GONE
        }

    }

    fun bottomSheetIndividualProductFlow(productV2: ProductV2) {
        val dialog = Utils.showCustomDialogBottum(activity, R.layout.bottom_sheet_pay_more)
        val bindingSheet = DataBindingUtil.inflate<BottomSheetPayMoreBinding>(
            layoutInflater, R.layout.bottom_sheet_pay_more, null, false
        )
        var rewardsPoint = 0
        dialog.setContentView(bindingSheet.root)
        dialog.setCancelable(false)
        activity?.let { Utils.showKeyboard(it) }
        bindingSheet.llRewards.visibility = View.GONE
        bindingSheet.ivCancel.setOnClickListener {
            dialog.dismiss()
            Utils.hideKeyboard(activity)
        }
        bindingSheet.etAmount.setOnFocusChangeListener { _, hasFocused ->
            if (hasFocused) {
                bindingSheet.llAmtView.setBackgroundResource(R.drawable.et_btm_bg)
            } else {
                bindingSheet.llAmtView.setBackgroundResource(R.drawable.et_btm_bg_un_focused)
            }
        }
        bindingSheet.ivMore.setOnClickListener {
            openKnowMoreDialog()
        }
        bindingSheet.llCreditCard.tvCardNumber.text = "···  ${productV2.last4}"
        if (productV2.product_type == "CC") {
            if (productV2.card_network == "MasterCard") {
                bindingSheet.llCreditCard.ivCardType.setImageResource(R.drawable.ic_mastercard)
            } else if (productV2.card_network == "Visa") {
                bindingSheet.llCreditCard.ivCardType.setImageResource(R.drawable.visa)
            }
            bindingSheet.llCreditCard.ivCardType.visibility = View.VISIBLE
            bindingSheet.llCreditCard.tvBankName.text = productV2.bankMasterRecord?.bankName ?: ""
            bindingSheet.llCreditCard.ivBankImage.loadSvg("${prefs.bankMasterUrl}${productV2.bankMasterRecord?.id}-logo-with-name.svg")
        } else {
            bindingSheet.llCreditCard.ivCardType.visibility = View.GONE
            bindingSheet.llCreditCard.tvBankName.text = productV2.bankMasterRecord?.billerName ?: ""
            bindingSheet.llCreditCard.ivBankImage.loadSvg("${prefs.loanMasterUrl}${productV2.bankMasterRecord?.id}-logo-with-name.svg")
        }

        bindingSheet.etAmount.setText("")
        bindingSheet.etAmount.requestFocus()

        if (rewardsAssignUpto != 0) {
            bindingSheet.tvRewardEarned.text =
                getString(R.string.str_chips_earned_this_month, rewardsAssigned, rewardsAssignUpto)
        }
        bindingSheet.etAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (p0.toString().isNotEmpty()) {
                    val amountForRewards = p0.toString().toDouble()
                    if (amountForRewards > 0 && amountForRewards < 100) {
                        bindingSheet.tvRewards.text = getString(R.string.str_enter_100)
                        bindingSheet.tvRewardPercentage.visibility = View.GONE
                        bindingSheet.tvRewardEarned.visibility = View.GONE
                        bindingSheet.tvAtTheRate.visibility = View.GONE
                        bindingSheet.llRewards.visibility = View.VISIBLE
                    } else {


                        rewardsPoint = Utils.roundupAmount(
                            amountForRewards,
                            DEFAULT_PERCENTAGE,
                            bindingSheet.tvRewardPercentage,
                            productV2.bankMasterRecord?.id,
                            Utils.itemList
                        )
                        bindingSheet.tvRewards.text =
                            getString(R.string.str_you_will_earn_reward_user, rewardsPoint)

                        MonthlyEarnRule.setRewardLimit(
                            rewardsCanAssign,
                            rewardsAssignUpto,
                            rewardsPoint,
                            bindingSheet.tvRewards,
                            bindingSheet.tvAtTheRate,
                            bindingSheet.tvRewardPercentage,
                            bindingSheet.tvRewardEarned,
                            requireContext()
                        )
                    }

                    bindingSheet.btnOkay.isEnabled = true
                    val amount = p0.toString().replace(",", "").toDouble()
                    if (amount <= 1000000) {
                        bindingSheet.llMessageError.visibility = View.GONE
                    }
                } else {
                    bindingSheet.llMessageError.visibility = View.GONE
                    bindingSheet.btnOkay.isEnabled = false
                    bindingSheet.llRewards.visibility = View.GONE
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        GradientUtils.setBoarderStroke(
            productV2.bankMasterRecord?.ui_config?.cardColor ?: "#FFFFFF",
            productV2.bankMasterRecord?.ui_config?.opacity_border ?: "#FFFFFF",
            true,
            bindingSheet.llCreditCard.flStroke
        )
        GradientUtils.setBackGround(
            productV2.bankMasterRecord?.ui_config?.cardColor ?: "#FFFFFF",
            "",
            productV2.bankMasterRecord?.ui_config?.opacity_topLeft ?: "#FFFFFF",
            productV2.bankMasterRecord?.ui_config?.opacity_bottomRight ?: "#FFFFFF",
            bindingSheet.llCreditCard.llCardSolidBackGround
        )

        bindingSheet.btnOkay.setOnClickListener {
            billStatus = CUSTOM
            if (bindingSheet.etAmount.text.toString().isNotEmpty()) {
                if (bindingSheet.etAmount.text.toString().replace(",", "").toDouble() < 10) {
                    bindingSheet.llMessageError.visibility = View.VISIBLE
                    bindingSheet.tvError.text = getString(R.string.str_minimum_amount)

                } else if (bindingSheet.etAmount.text.toString().replace(",", "")
                        .toDouble() > 1000000
                ) {
                    bindingSheet.llMessageError.visibility = View.VISIBLE
                    bindingSheet.tvError.text = getString(R.string.str_amount_less_1000000)
                } else {
                    bindingSheet.llMessageError.visibility = View.GONE
                    val amount = bindingSheet.etAmount.text.toString().replace(",", "")
                    productV2.customAmount = amount.toDouble()
                    productV2.rewardsPoint = rewardsPoint
                    productV2.billStatus = billStatus
                    val selectedProduct: ArrayList<ProductV2> = ArrayList()
                    selectedProduct.add(productV2)
                    requireActivity().startActivity(
                        Intent(requireContext(), PaymentMethodsActivity::class.java).putExtra(
                            IntentKeys.PRODUCT_LIST, selectedProduct
                        ).putExtra(IntentKeys.IS_PAY_TOGETHER, false)
                            .putExtra(IntentKeys.TOTAL_AMOUNT, amount.trim())
                            .putExtra(IntentKeys.REWARDS_ASSIGN_UPTO, rewardsAssignUpto)
                            .putExtra(IntentKeys.REWARDS_CAN_ASSIGN, rewardsCanAssign)
                            .putExtra(IntentKeys.REWARDS_ASSIGNED, rewardsAssigned)
                    )

                    Utils.hideKeyboard(activity)
                    dialog.dismiss()

                }
            } else {
                bindingSheet.tvError.text = "Minimum amount allowed is ₹10"
            }
        }
        dialog.show()
    }


    fun onPayNowCLick(
        productV2: ProductV2
    ) {
        val dialog = Utils.showCustomDialogBottum(activity, R.layout.bottom_sheet_total_due)
        dialog.setCancelable(false)
        val ivCancel = dialog.findViewById<FrameLayout>(R.id.ivCancel)
        ivCancel.setOnClickListener {
            dialog.dismiss()
            Utils.hideKeyboard(activity)
            MparticleUtils.logEvent(
                "CC_Payment_SelectAmount_BackClicked",
                "User cancels the payment flow by closing the view",
                "Unique",
                "Back",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_SelectAmount_BackClicked),
                requireActivity()
            )

        }

        val iv_Card_Type = dialog.findViewById<AppCompatImageView>(R.id.ivCardType)
        val iv_bank_image = dialog.findViewById<AppCompatImageView>(R.id.iv_bank_image)

        bankImageUrl = "${prefs.bankMasterUrl}${productV2.bankMasterRecord?.id}-logo-with-name.svg"

        iv_bank_image.loadSvg(bankImageUrl)
        val ivMore = dialog.findViewById<AppCompatImageView>(R.id.ivMore)
        ivMore.setOnClickListener {
            openKnowMoreDialog()
            context?.let { it1 ->
                MparticleUtils.logEvent(
                    "CC_Payment_Rewards_KnowMore_Clicked",
                    "User clicks the information icon to learn more about the rewards on bill payment\n",
                    "Unique",
                    "Continue",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Rewards_KnowMore_Clicked),
                    it1
                )
            }
        }
        val tvRewardPercentage = dialog.findViewById<AppCompatTextView>(R.id.tvRewardPercentage)
        val tvRewardEarned = dialog.findViewById<AppCompatTextView>(R.id.tvRewardEarned)
        val tvAtTheRate = dialog.findViewById<AppCompatTextView>(R.id.tvAtTheRate)
        val tvInfo = dialog.findViewById<AppCompatTextView>(R.id.tvInfo)
        val ivMessageType = dialog.findViewById<AppCompatImageView>(R.id.ivMessageType)
        val tvCardNumber = dialog.findViewById<AppCompatTextView>(R.id.tvCardNumber)
        tvCardNumber.text = "··· " + productV2.last4
        val tvBankName = dialog.findViewById<AppCompatTextView>(R.id.tvBankName)
        val tvRewards = dialog.findViewById<AppCompatTextView>(R.id.tvRewards)
        tvBankName.text = productV2.bankMasterRecord?.bankName ?: ""
        val etAmount = dialog.findViewById<EditText>(R.id.etAmount)
        etAmount.setText(Utils.getFormattedDecimal(productV2.bill?.amount_paid?.let {
            productV2.bill.total_due.minus(
                it
            )
        } ?: 0.0))
        val tvCaption = dialog.findViewById<AppCompatTextView>(R.id.tvCaption)
        val chipTotalDue = dialog.findViewById<Chip>(R.id.chipTotalDue)
        val chipMinimumDue = dialog.findViewById<Chip>(R.id.chipMinimumDue)
        val customChip = dialog.findViewById<Chip>(R.id.chipCustom)

        val llMessageMinimumDue = dialog.findViewById<LinearLayoutCompat>(R.id.llMessageMinimumDue)
        val llRewards = dialog.findViewById<LinearLayoutCompat>(R.id.llRewards)
        val llMessageTotalDue = dialog.findViewById<LinearLayoutCompat>(R.id.llMessageTotalDue)
        val llAmtView = dialog.findViewById<LinearLayoutCompat>(R.id.llAmtView)
        val llMessageError = dialog.findViewById<LinearLayoutCompat>(R.id.llMessageError)
        val tvError = dialog.findViewById<TextView>(R.id.tvError)
        val btnOkay = dialog.findViewById<AppCompatButton>(R.id.btnOkay)
        val flStroke = dialog.findViewById<FrameLayout>(R.id.flStroke)
        tvInfo.text = getString(R.string.str_great_move)
        ivMessageType.setImageResource(R.drawable.ic_happy_face)
        val llCardSolidBackGround =
            dialog.findViewById<LinearLayoutCompat>(R.id.llCardSolidBackGround)
        GradientUtils.setBoarderStroke(
            productV2.bankMasterRecord?.ui_config?.cardColor ?: "#FFFFFF",
            productV2.bankMasterRecord?.ui_config?.opacity_border ?: "#FFFFFF",
            true,
            flStroke
        )
        GradientUtils.setBackGround(
            productV2.bankMasterRecord?.ui_config?.cardColor ?: "#FFFFFF",
            "",
            productV2.bankMasterRecord?.ui_config?.opacity_topLeft ?: "#FFFFFF",
            productV2.bankMasterRecord?.ui_config?.opacity_bottomRight ?: "#FFFFFF",
            llCardSolidBackGround
        )
        if ((productV2.bill?.total_due ?: 0.0) >= 100) {
            rewardsPoint = Utils.roundupAmount(
                productV2.bill?.total_due ?: DEFAULT_PERCENTAGE,
                DEFAULT_PERCENTAGE,
                tvRewardPercentage,
                productV2.bankMasterRecord?.id,
                Utils.itemList
            )
            llRewards.visibility = View.VISIBLE
            tvRewards.text = getString(R.string.str_you_will_earn_reward_user, rewardsPoint)

        } else if ((productV2.bill?.total_due ?: 0.0) < 100) {
            llRewards.visibility = View.VISIBLE
            tvRewards.text = getString(R.string.str_pay_total_bill)
        } else {
            llRewards.visibility = View.GONE
        }
        billStatus = FULL
        productV2.billStatus = FULL
        chipTotalDue.setOnClickListener {
            tvInfo.text = getString(R.string.str_great_move)
            ivMessageType.setImageResource(R.drawable.ic_happy_face)
            billStatus = FULL
            productV2.billStatus = FULL
            etAmount.setText(
                "" + Utils.getFormattedDecimal(
                    productV2.bill?.total_due?.minus(productV2.bill.amount_paid) ?: 0.0
                )
            )
            etAmount.isEnabled = false
            llMessageError.visibility = View.GONE
            llMessageTotalDue.visibility = View.VISIBLE
            llMessageMinimumDue.visibility = View.GONE
            tvCaption.text = resources.getText(R.string.you_have_to_pay)
            context?.let { it1 ->
                MparticleUtils.logCurrentScreen(
                    "/cc-payment/select-amount",
                    "The customer chooses the amount for individual credit card payment",
                    "type",
                    "total-only",
                    "",
                    "",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Select_Amount),
                    activity!!.applicationContext
                )
                MparticleUtils.logEvent(
                    "CC_Payment_TotalDue_Clicked",
                    "User clicks the total due bubble to view total due amount\n",
                    "Unique",
                    "Continue",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_TotalDue_Clicked),
                    it1
                )
            }

            if ((productV2.bill?.total_due ?: 0.0) >= 100) {
                rewardsPoint = Utils.roundupAmount(
                    productV2.total_due?.minus(productV2.amount_paid!!) ?: DEFAULT_PERCENTAGE,
                    DEFAULT_PERCENTAGE,
                    tvRewardPercentage,
                    productV2.bankMasterRecord?.id,
                    Utils.itemList
                )
                llRewards.visibility = View.VISIBLE
                tvRewards.text = getString(R.string.str_you_will_earn_reward_user, rewardsPoint)

            } else if ((productV2.bill?.total_due ?: 0.0) < 100) {
                llRewards.visibility = View.VISIBLE
                tvRewards.text = getString(R.string.str_pay_total_bill)
            } else {
                llRewards.visibility = View.GONE
            }
        }

        if (productV2.card_network == "MasterCard") {
            iv_Card_Type.setImageResource(R.drawable.ic_mastercard)
        }
        if (productV2.card_network == "Visa") {
            iv_Card_Type.setImageResource(R.drawable.visa)
        }
        if (rewardsAssignUpto != 0) {
            tvRewardEarned.text =
                "$rewardsAssigned/$rewardsAssignUpto chips have been earned this month!"
        }
        MonthlyEarnRule.setRewardLimit(
            rewardsCanAssign,
            rewardsAssignUpto,
            rewardsPoint,
            tvRewards,
            tvAtTheRate,
            tvRewardPercentage,
            tvRewardEarned,
            requireContext()
        )

        etAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (p0.toString().isNotEmpty()) {
                    val amount = p0.toString().replace(",", "").toDouble()
                    if (amount > 0 && amount < 100) {
                        tvRewards.text = getString(R.string.str_enter_100)
                        tvRewardPercentage.visibility = View.GONE
                        tvAtTheRate.visibility = View.GONE
                        llRewards.visibility = View.VISIBLE
                    } else {

                        rewardsPoint = Utils.roundupAmount(
                            amount,
                            DEFAULT_PERCENTAGE,
                            tvRewardPercentage,
                            productV2.bankMasterRecord?.id,
                            Utils.itemList
                        )
                        MonthlyEarnRule.setRewardLimit(
                            rewardsCanAssign,
                            rewardsAssignUpto,
                            rewardsPoint,
                            tvRewards,
                            tvAtTheRate,
                            tvRewardPercentage,
                            tvRewardEarned,
                            requireContext()
                        )

                    }

                    if (amount > productV2.bill?.total_due ?: 0.0) {
                        tvError.text = getString(R.string.str_enter_amount_is_more)
                        llMessageError.visibility = View.VISIBLE
                        btnOkay.isEnabled = true
                        llMessageTotalDue.visibility = View.GONE
                    } else if (amount < 1) {
                        llMessageError.visibility = View.VISIBLE
                        tvError.text = getString(R.string.str_minimum_amount_one)
                        btnOkay.isEnabled = false
                        llMessageTotalDue.visibility = View.GONE
                    } else if (amount > 1000000) {
                        llMessageError.visibility = View.VISIBLE
                        tvError.text = getString(R.string.str_amount_less_1000000)
                        btnOkay.isEnabled = false
                        llMessageTotalDue.visibility = View.GONE
                    } else {
                        llMessageError.visibility = View.GONE
                        btnOkay.isEnabled = true
                        llMessageTotalDue.visibility = View.VISIBLE
                    }

                    val amountForRewards = p0.toString().replace(",", "").toInt()

                    rewardsPoint = Utils.roundupAmount(
                        amountForRewards.toDouble(),
                        DEFAULT_PERCENTAGE,
                        tvRewardPercentage,
                        productV2.bankMasterRecord?.id,
                        Utils.itemList
                    )

                } else {
                    llMessageError.visibility = View.GONE
                    btnOkay.isEnabled = false
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        etAmount.setOnFocusChangeListener { _, hasFocused ->
            if (hasFocused) {
                llAmtView.setBackgroundResource(R.drawable.et_btm_bg)
            } else {
                llAmtView.setBackgroundResource(R.drawable.et_btm_bg_un_focused)
                context?.let {
                    MparticleUtils.logEvent(
                        "CC_Payment_Custom_Entered",
                        "User enters a custom amount for bill payment\n",
                        "Unique",
                        "Input Field",
                        SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Custom_Entered),
                        it
                    )
                }
            }
        }

        if (productV2.bill?.min_due != null && productV2.bill.min_due.minus(productV2.bill.amount_paid) > 0) {
            chipMinimumDue.setOnClickListener {
                tvInfo.text = getString(R.string.str_you_will_incur)
                ivMessageType.setImageResource(R.drawable.ic_warning)
                if ((productV2.bill.min_due ?: 0.0) >= 100) {

                    rewardsPoint = Utils.roundupAmount(
                        productV2.bill.min_due,
                        DEFAULT_PERCENTAGE,
                        tvRewardPercentage,
                        productV2.bankMasterRecord?.id,
                        Utils.itemList
                    )
                    llRewards.visibility = View.VISIBLE
                    tvRewards.text = getString(R.string.str_you_will_earn_reward_user, rewardsPoint)
                } else if ((productV2.bill.min_due ?: 0.0) < 100) {

                    llRewards.visibility = View.VISIBLE
                    tvRewards.text = getString(R.string.str_pay_total_bill)
                } else {
                    llRewards.visibility = View.GONE
                }
                billStatus = MIN
                productV2.billStatus = MIN
                etAmount.setText(Utils.getFormattedDecimal(
                    productV2.bill.min_due.minus(productV2.bill.amount_paid)))
                etAmount.isEnabled = false
                llMessageError.visibility = View.GONE
                llMessageTotalDue.visibility = View.VISIBLE
                llMessageMinimumDue.visibility = View.VISIBLE
                tvCaption.text = resources.getText(R.string.you_have_to_pay)
                context?.let { it1 ->
                    MparticleUtils.logCurrentScreen(
                        "/cc-payment/select-amount",
                        "The customer chooses the amount for individual credit card payment",
                        "type",
                        "total-minimum-only",
                        "",
                        "",
                        SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Select_Amount),
                        it1
                    )
                    MparticleUtils.logEvent(
                        "CC_Payment_MinimumDue_Clicked",
                        "User clicks the minimum due bubble to view minimum due amount\n",
                        "Unique",
                        "Continue",
                        SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_MinimumDue_Clicked),
                        it1
                    )
                }


                rewardsPoint = Utils.roundupAmount(
                    productV2.bill.min_due,
                    DEFAULT_PERCENTAGE,
                    tvRewardPercentage,
                    productV2.bankMasterRecord?.id,
                    Utils.itemList
                )

            }
        } else {
            chipMinimumDue.visibility = View.GONE
        }
        customChip.setOnClickListener {
            tvInfo.text = getString(R.string.str_you_will_incur)
            ivMessageType.setImageResource(R.drawable.ic_warning)
            llRewards.visibility = View.GONE
            tvRewards.text = ""
            billStatus = CUSTOM
            etAmount.isEnabled = true

            etAmount.requestFocus()
            etAmount.setText("")
            btnOkay.isEnabled = false
            llMessageTotalDue.visibility = View.VISIBLE
            llMessageMinimumDue.visibility = View.GONE
            tvCaption.text = getString(R.string.str_enter_amount)
            activity?.let { it1 -> Utils.showKeyboard(it1) }
            activity?.let { it1 ->

                MparticleUtils.logCurrentScreen(
                    "/cc-payment/select-amount",
                    "The customer chooses the amount for individual credit card payment",
                    "type",
                    "total-custom-only",
                    "",
                    "",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Select_Amount),
                    it1
                )
                MparticleUtils.logEvent(
                    "CC_Payment_Custom_Clicked",
                    "User clicks the custom bumble to enter a custom amount for bill payment\n",
                    "Unique",
                    "Continue",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Custom_Clicked),
                    it1
                )
            }

        }

        btnOkay.setOnClickListener {
            val amount = etAmount.text.toString().replace(",", "")
            if (amount.replace(",", "").toDouble() > 1000000) {
                llMessageError.visibility = View.VISIBLE
                tvError.text = getString(R.string.str_amount_less_1000000)
                llMessageError.visibility = View.VISIBLE

            } else {
                productV2.customAmount = amount.toDouble()
                productV2.billStatus = billStatus
                productV2.rewardsPoint = rewardsPoint
                val selectedProduct: ArrayList<ProductV2> = ArrayList()
                selectedProduct.add(productV2)
                requireActivity().startActivity(
                    Intent(
                        requireContext(), PaymentMethodsActivity::class.java
                    ).putExtra(IntentKeys.PRODUCT_LIST, selectedProduct)
                        .putExtra(IntentKeys.IS_PAY_TOGETHER, false)
                        .putExtra(IntentKeys.TOTAL_AMOUNT, amount.trim())
                        .putExtra(IntentKeys.REWARDS_POINT, rewardsPoint)
                        .putExtra(IntentKeys.BILL_STATUS, billStatus)
                        .putExtra(IntentKeys.REWARDS_ASSIGN_UPTO, rewardsAssignUpto)
                        .putExtra(IntentKeys.REWARDS_CAN_ASSIGN, rewardsCanAssign)
                        .putExtra(IntentKeys.REWARDS_ASSIGNED, rewardsAssigned)
                )

                Utils.hideKeyboard(activity)

                MparticleUtils.logEvent(
                    "CC_Payment_SelectAmount_Clicked",
                    "User confirms the selection of amount and progresses to next screen",
                    "Not Unique",
                    "Continue",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_SelectAmount_Clicked),
                    activity!!.applicationContext
                )


            }

        }
        dialog.show()
    }


    private fun openKnowMoreDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireActivity())
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_know_more)
        bottomSheetDialog.setCancelable(false)
        val btnSubmit = bottomSheetDialog.findViewById<AppCompatImageView>(R.id.ivCancel)

        val info_Image = bottomSheetDialog.findViewById<ImageView>(R.id.info_Image)
        mFirebaseDatabase?.child("payment_info_icon")?.get()?.addOnSuccessListener {

            val options = RequestOptions().placeholder(R.drawable.button_white_bordered)
                .error(R.drawable.button_white_bordered)



            if (info_Image != null) {
                Glide.with(this).load(it.value).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).apply(options).into(info_Image)
            }
        }?.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }

        btnSubmit!!.setOnClickListener {
            bottomSheetDialog.dismiss()
            activity?.let { it1 -> Utils.showKeyboard(it1) }
        }

        bottomSheetDialog.setOnKeyListener(object : DialogInterface.OnKeyListener {
            override fun onKey(dialog: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    MparticleUtils.logEvent(
                        "CC_Payment_Rewards_KnowMore_BackClicked",
                        "User closes the window for rewards description by clicking the bottom CTA with rewards description",
                        "Unique",
                        "Back",
                        SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Rewards_KnowMore_BackClicked),
                        requireActivity()
                    )
                }
                return true
            }
        })
        Utils.hideKeyboard(activity)
        bottomSheetDialog.show()
    }

    private fun setUpBalanceDueWidget(balanceDueWidget: BalanceDueWidget?) {
        var productListSize = balanceDueWidget?.products?.size ?: 0
        if (!balanceDueWidget?.products.isNullOrEmpty() && productListSize >= 2) {
            var payabitity = true
            balanceDueWidget?.products?.forEach {
                if (it.bill == null || (it.productStatus in arrayOf(ProductStatus.ProductStatusDefault.status, ProductStatus.ProductStatusOne.status, ProductStatus.ProductStatusTwo.status) || (it.bill.total_due != null && it.bill.total_due.toInt() == 0))) {
                    payabitity = false
                }


            }
            if (payabitity) {
                MparticleUtils.logCurrentScreen(
                    "/home-tab",
                    "The home tab completely loads, and customer views the tab",
                    "cta-type",
                    "pay-together",
                    "",
                    "",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.HOME_TAB),
                    requireActivity()
                )
            } else {
                MparticleUtils.logCurrentScreen(
                    "/home-tab",
                    "The home tab completely loads, and customer views the tab",
                    "cta-type",
                    "pay-now",
                    "",
                    "",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.HOME_TAB),
                    requireActivity()
                )
            }
        } else {
            if (balanceDueWidget?.products?.isNotEmpty() == true) {
                MparticleUtils.logCurrentScreen(
                    "/home-tab",
                    "The home tab completely loads, and customer views the tab",
                    "cta-type",
                    "pay-now",
                    "",
                    "",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.HOME_TAB),
                    requireActivity()
                )
            } else {
                MparticleUtils.logCurrentScreen(
                    "/home-tab",
                    "The home tab completely loads, and customer views the tab",
                    "cta-type",
                    "add-new",
                    "",
                    "",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.HOME_TAB),
                    requireActivity()
                )
            }


        }
        if (balanceDueWidget?.balanceDue != null && balanceDueWidget.products != null) {
            mBinding?.layoutTotalDue?.noProductFound?.visibility = View.GONE
            mBinding?.layoutTotalDue?.AddNewProduct?.visibility = View.GONE
            mBinding?.layoutTotalDue?.llDueFound?.visibility = View.VISIBLE
            mBinding?.layoutTotalDue?.ViewNOProductFound?.visibility = View.GONE
            if (balanceDueWidget.balanceDue.toInt() != 0) {
                mBinding?.layoutTotalDue?.tvTotalValue?.text =
                    "₹" + balanceDueWidget.balanceDue.toDouble()
                        .let { Utils.getFormattedDecimal(it) }
            } else {
                mBinding?.layoutTotalDue?.tvTotalValue?.text = "₹0"
                mBinding?.layoutTotalDue?.tvTotalValue?.setTextColor(resources.getColor(R.color.colorDivider))
            }

            if (!balanceDueWidget.products.isNullOrEmpty()) {
                var adapter = HomeProductAdapter(balanceDueWidget.products, this)
                mBinding?.layoutTotalDue?.recyclerviewHomeProduct?.adapter = adapter
                when (balanceDueWidget.products.size) {
                    1 -> {
                        if (balanceDueWidget.products[0].product_type == "CC") {

                            mBinding?.layoutTotalDue?.btnPayTogahter?.setOnClickListener {
                                MparticleUtils.logEvent(
                                    "Home_Primary_CTA_Clicked",
                                    "User clicks Pay Now CTA on the pay widget on Home Tab",
                                    "Unique",
                                    "Continue",
                                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Home_Primary_CTA_Clicked),
                                    requireActivity()
                                )

                                //1 Unsecured without bill
                                if (balanceDueWidget.products[0].productStatus == ProductStatus.ProductStatusOne.status|| balanceDueWidget.products[0].productStatus == ProductStatus.ProductStatusDefault.status) {
                                    startActivity(
                                        Intent(
                                            activity, CardDetailsActivityNew::class.java
                                        ).putExtra(
                                            IntentKeys.PRODUCT_ID,
                                            balanceDueWidget.products[0].id,
                                        ).putExtra(
                                            IntentKeys.LAST_FOUR, balanceDueWidget.products[0].last4
                                        ).putExtra(
                                            IntentKeys.BANK_LOGO,
                                            balanceDueWidget.products[0].bankMasterRecord?.id
                                        ).putExtra(
                                            IntentKeys.BANK_NAME,
                                            balanceDueWidget.products[0].bankMasterRecord?.bankName
                                        ).putExtra(
                                            IntentKeys.PRODUCT_ITEM, balanceDueWidget.products[0]
                                        ).putExtra(IntentKeys.IS_FROM_HOME, "doToken").putExtra(
                                            IntentKeys.REWARDS_ASSIGN_UPTO, rewardsAssignUpto
                                        ).putExtra(
                                            IntentKeys.REWARDS_CAN_ASSIGN, rewardsCanAssign
                                        ).putExtra(IntentKeys.REWARDS_ASSIGNED, rewardsAssigned)
                                    )
                                }

                                //2 Unsecured with bill
                                if (balanceDueWidget.products[0].productStatus == ProductStatus.ProductStatusTwo.status) {
                                    startActivity(
                                        Intent(
                                            activity, CardDetailsActivityNew::class.java
                                        ).putExtra(
                                            IntentKeys.PRODUCT_ID,
                                            balanceDueWidget.products[0].id,
                                        ).putExtra(
                                            IntentKeys.LAST_FOUR, balanceDueWidget.products[0].last4
                                        ).putExtra(
                                            IntentKeys.BANK_LOGO,
                                            balanceDueWidget.products[0].bankMasterRecord?.id
                                        ).putExtra(
                                            IntentKeys.BANK_NAME,
                                            balanceDueWidget.products[0].bankMasterRecord?.bankName
                                        ).putExtra(
                                            IntentKeys.PRODUCT_ITEM, balanceDueWidget.products[0]
                                        ).putExtra(IntentKeys.IS_FROM_HOME, "doToken").putExtra(
                                            IntentKeys.REWARDS_ASSIGN_UPTO, rewardsAssignUpto
                                        ).putExtra(
                                            IntentKeys.REWARDS_CAN_ASSIGN, rewardsCanAssign
                                        ).putExtra(IntentKeys.REWARDS_ASSIGNED, rewardsAssigned)

                                    )

                                }

                                if (balanceDueWidget.products[0].productStatus == ProductStatus.ProductStatusThree.status )  //with bill and active
                                {
                                    onPayNowCLick(balanceDueWidget.products[0])
                                }
                                if (balanceDueWidget.products[0].productStatus ==ProductStatus.ProductStatusFour.status)  //only active
                                {
                                    bottomSheetIndividualProductFlow(balanceDueWidget.products[0])
                                }
                                if (balanceDueWidget.products[0].productStatus == ProductStatus.ProductStatusFive.status )  //with bill and active
                                {
                                    onPayMoreCLick(balanceDueWidget.products[0])
                                }
                            }
                        } else {

                            mBinding?.layoutTotalDue?.btnPayTogahter?.setOnClickListener {
                                onLoanPayClick(
                                    balanceDueWidget.products[0].product_number,
                                    balanceDueWidget.products[0].bankMasterRecord?.billerName,
                                    balanceDueWidget.products[0].product_type,
                                    "",
                                    0.0,
                                    balanceDueWidget.products[0].bill?.amount_paid?.toInt()
                                        ?.let { it1 ->
                                            balanceDueWidget.products[0].bill?.total_due?.toInt()
                                                ?.minus(it1)
                                        },
                                    balanceDueWidget.products[0].institution_master_id,
                                    balanceDueWidget.products[0].id,
                                    0.0,
                                    balanceDueWidget.products[0].last4,
                                    "",
                                    "",
                                    "",
                                    balanceDueWidget.products[0]
                                )
                            }
                        }
                        mBinding?.layoutTotalDue?.btnPayHome?.text = getString(R.string.pay_now)
                        mBinding?.layoutTotalDue?.earnReward?.text = getString(R.string.str_1_chips)
                    }
                    2 -> {
                        var payabitity = true
                        balanceDueWidget.products.forEach {
                            if ((it.productStatus == ProductStatus.ProductStatusDefault.status || it.productStatus == ProductStatus.ProductStatusOne.status || it.productStatus == ProductStatus.ProductStatusTwo.status)) {
                                payabitity = false
                            }
                            if (it.bill == null) {
                                payabitity = false
                            } else {
                                if (it.bill.total_due != null && it.bill.total_due.toInt() == 0) {
                                    payabitity = false
                                }
                            }
                        }

                        if (!payabitity) {

                            mBinding?.layoutTotalDue?.btnPayHome?.text = getString(R.string.pay_now)
                            mBinding?.layoutTotalDue?.earnReward?.text =
                                getString(R.string.str_1_chips)
                            mBinding?.layoutTotalDue?.btnPayTogahter?.setOnClickListener {
                                MparticleUtils.logEvent(
                                    "Home_Primary_CTA_Clicked",
                                    "User clicks Pay Together CTA on the pay widget on Home Tab",
                                    "Unique",
                                    "Continue",
                                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Home_Primary_CTA_Clicked),
                                    requireActivity()
                                )

                                activity?.setPayScreen(
                                    1, false
                                )

                            }
                            mBinding?.layoutTotalDue?.ivEdit?.setOnClickListener {

                            }

                        } else {


                            mBinding?.layoutTotalDue?.btnPayHome?.text =
                                getString(R.string.pay_together)
                            mBinding?.layoutTotalDue?.earnReward?.text =
                                getString(R.string.str_earn_i)
                            mBinding?.layoutTotalDue?.btnPayTogahter?.setOnClickListener {
                                activity?.setPayScreen(
                                    0, true
                                )
                            }
                            mBinding?.layoutTotalDue?.ivEdit?.setOnClickListener {
//                                activity?.setPayScreen(
//                                    0, true
//                                )
                            }
                        }


                    }

                    3 -> {


///


                        var payabitity = true
                        balanceDueWidget.products.forEach {
                            if (it.productStatus == ProductStatus.ProductStatusDefault.status || it.productStatus == ProductStatus.ProductStatusOne.status || it.productStatus == ProductStatus.ProductStatusTwo.status) {
                                payabitity = false
                            }
                            if (it.bill == null) {
                                payabitity = false
                            } else {
                                if (it.bill.total_due != null && it.bill.total_due.toInt() == 0) {
                                    payabitity = false
                                }

                            }
                        }

                        if (!payabitity) {
                            mBinding?.layoutTotalDue?.btnPayHome?.text = getString(R.string.pay_now)
                            mBinding?.layoutTotalDue?.earnReward?.text =
                                getString(R.string.str_1_chips)
                            mBinding?.layoutTotalDue?.btnPayTogahter?.setOnClickListener {
                                activity?.setPayScreen(
                                    0, false
                                )

                            }

                            mBinding?.layoutTotalDue?.ivEdit?.setOnClickListener {


                            }

                        } else {
                            mBinding?.layoutTotalDue?.btnPayHome?.text =
                                getString(R.string.pay_together)
                            mBinding?.layoutTotalDue?.earnReward?.text =
                                getString(R.string.str_earn_i)
                            mBinding?.layoutTotalDue?.ivEdit?.setOnClickListener {
//                                activity?.setPayScreen(
//                                    0, true
//                                )
                            }
                            mBinding?.layoutTotalDue?.btnPayTogahter?.setOnClickListener {
                                activity?.setPayScreen(
                                    0, true
                                )
                            }
                        }
                        //


                    }

                    else -> {


//
                        var totalDueForMoreCard = 0.0


                        for (index in balanceDueWidget.products.indices) {
                            if (index > 1) {
                                var amount =
                                    balanceDueWidget.products[index].bill?.amount_paid?.toInt()
                                        ?.let {
                                            balanceDueWidget.products[index].bill?.total_due?.toInt()
                                                ?.minus(
                                                    it
                                                )
                                        }
                                if (amount != null) {
                                    totalDueForMoreCard += amount
                                }


                            }
                        }


                        var payability = true

                        balanceDueWidget.products.forEach {


                            if (it.productStatus == ProductStatus.ProductStatusDefault.status || it.productStatus == ProductStatus.ProductStatusOne.status|| it.productStatus == ProductStatus.ProductStatusTwo.status) {
                                payability = false
                            }
                            if (it.bill == null) {
                                payability = false
                            } else {
                                if (it.bill.total_due != null && it.bill.total_due.toInt() == 0) {
                                    payability = false
                                }
                            }


                        }



                        if (!payability) {
                            mBinding?.layoutTotalDue?.btnPayHome?.text = getString(R.string.pay_now)
                            mBinding?.layoutTotalDue?.earnReward?.text =
                                getString(R.string.str_1_chips)
                            mBinding?.layoutTotalDue?.btnPayTogahter?.setOnClickListener {


                                activity?.setPayScreen(
                                    0, false
                                )
                            }


                        } else {
                            mBinding?.layoutTotalDue?.btnPayHome?.text =
                                getString(R.string.pay_together)
                            mBinding?.layoutTotalDue?.earnReward?.text =
                                getString(R.string.str_earn_i)
                            mBinding?.layoutTotalDue?.btnPayTogahter?.setOnClickListener {
                                activity?.setPayScreen(
                                    0, true
                                )
                            }

                        }

                    }


                }
            } else {

                MparticleUtils.logEvent(
                    "Home_Primary_CTA_Clicked",
                    "User clicks Add New CTA on the pay widget on Home Tab",
                    "Unique",
                    "Continue",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Home_Primary_CTA_Clicked),
                    requireActivity()
                )

                MparticleUtils.logCurrentScreen(
                    "/home-tab",
                    "The home tab completely loads, and customer views the tab",
                    "cta-type",
                    "add-new",
                    "",
                    "",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.HOME_TAB),
                    requireActivity()
                )
                MparticleUtils.logEvent(
                    "PayTab_AddNew_Clicked",
                    "",
                    "Unique",
                    "Continue",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.PayTab_AddNew_Clicked),
                    requireActivity()
                )

                mBinding?.layoutTotalDue?.noProductFound?.visibility = View.VISIBLE
                mBinding?.layoutTotalDue?.AddNewProduct?.visibility = View.VISIBLE
                mBinding?.layoutTotalDue?.llDueFound?.visibility = View.GONE
                mBinding?.layoutTotalDue?.ViewNOProductFound?.visibility = View.VISIBLE
                mBinding?.layoutCreditHealth?.llCreditHealth?.visibility = View.GONE
                mBinding?.creditdashBoard?.visibility = View.VISIBLE

                mBinding?.layoutTotalDue?.AddNewProduct?.setOnClickListener {

                    try {
                        context?.let { it1 ->
                            ProductBottomSheetDialog.newInstance(it1, rewardsCanAssign, rewardsAssigned, rewardsAssignUpto).show(
                                childFragmentManager, ""
                            )
                        }
                    } catch (e: Exception) {

                    }

                }


            }

        } else {
            mBinding?.layoutTotalDue?.noProductFound?.visibility = View.VISIBLE
            mBinding?.layoutTotalDue?.AddNewProduct?.visibility = View.VISIBLE
            mBinding?.layoutTotalDue?.AddNewProduct?.visibility = View.VISIBLE
            mBinding?.layoutTotalDue?.llDueFound?.visibility = View.GONE
            mBinding?.layoutTotalDue?.ViewNOProductFound?.visibility = View.VISIBLE
            mBinding?.layoutCreditHealth?.llCreditHealth?.visibility = View.GONE
            mBinding?.creditdashBoard?.visibility = View.VISIBLE

            mBinding?.layoutTotalDue?.AddNewProduct?.setOnClickListener {

                try {
                    context?.let { it1 ->
                        ProductBottomSheetDialog.newInstance(it1, rewardsCanAssign, rewardsAssigned, rewardsAssignUpto).show(
                            childFragmentManager, ""
                        )
                    }
                } catch (e: Exception) {
                }

            }


        }
    }


    private fun setupRewarWidget(rewardWidget: RewardWidget?) {
        if (rewardWidget != null) {
            mBinding?.layoutTotalDue?.llRewards?.visibility = View.VISIBLE
            mBinding?.layoutTotalDue?.tvRewardPoints?.text =
                Utils.getFormattedDecimal(rewardWidget.availableRewardPoints.toDouble())
            mBinding?.layoutCreditHealth?.llCreditHealth?.setOnClickListener {
                val intent = Intent(activity, CreditHealthActivity::class.java)
                intent.putExtra(
                    "coin", "" + rewardWidget.availableRewardPoints
                )
                startActivity(
                    intent
                )
            }
        } else {
            mBinding?.layoutTotalDue?.llRewards?.visibility = View.GONE
        }
    }


    private fun setupViewModel() {
        repository = HomeRepository(requireContext())
        viewModel = ViewModelProvider(
            this, HomeDashBoardViewModelFactory(repository)
        )[HomeViewModel::class.java]


    }

    private fun setUpVp(
        viewPager2: ViewPager2, size: Int, newSize: Int, linearLayoutCompat: LinearLayoutCompat
    ) {
        viewPager2.apply {
            clipToPadding = false
            clipChildren = false
        }
        val pageTranslationX = 0 + 90
        val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
            page.translationX = -pageTranslationX * position
            page.scaleY = 1 - (0.00f * kotlin.math.abs(position))
        }
        viewPager2.setPageTransformer(pageTransformer)
        viewPager2.currentItem = 0
        viewPager2.offscreenPageLimit = 1
        setUpIndicator(viewPager2)

    }

    private fun setUpIndicator(viewPager2: ViewPager2) {
        mBinding?.slideIndicator?.setupWithViewPager2(viewPager2)
        mBinding?.slideIndicator?.todoItemCount?.observeForever {
            mBinding?.indicatorNumber?.text = "$it/3"
        }

    }

    fun replaceFragments(cheqSafe: Boolean) {
        if (featureConfig.isNewProfileEnabled()) {
            val intent = intentFactory.createIntent(
                requireContext(),
                IntentKey.MyAccountActivityKey(
                    cheqSafe = cheqSafe
                )
            )
            startActivity(intent)
        } else {
            startActivity(Intent(
                requireContext(), AppMenuActivity::class.java
            ).apply { putExtra("CHEQ_SAFE", cheqSafe) })
        }
    }


    override fun onAttach(_context: Context) {
        super.onAttach(_context)
        activity = _context as MainActivity
    }

    override fun onResume() {
        super.onResume()
        activity?.setBottomTab(2)
        viewModel.checkCheqSafeStatus()
        viewModel.getDashBoard()
    }


    fun onLoanPayClick(
        product_number: String?,
        billerName: String?,
        product_type: String?,
        bankLogo: String?,
        minDue: Double?,
        totalDue: Int?,
        billId: String?,
        productId: String?,
        paidAmount: Double?,
        lastFour: String?,
        startColor: String,
        middleColor: String,

        endColor: String,
        item: ProductV2
    ) {

        val dialog = Utils.showCustomDialogBottum(activity, R.layout.bottom_sheet_total_due_loan)
        dialog.setCancelable(false)
        val ivCancel = dialog.findViewById<FrameLayout>(R.id.ivCancel)
        var imageUrl = "${prefs.loanMasterUrl}${item.bankMasterRecord?.id}-logo-with-name.svg"
        ivCancel.setOnClickListener {
            dialog.dismiss()
            Utils.hideKeyboard(activity)
        }
        val iv_Card_Type = dialog.findViewById<AppCompatImageView>(R.id.ivCardType)
        iv_Card_Type.visibility = View.GONE
        val iv_bank_image = dialog.findViewById<AppCompatImageView>(R.id.iv_bank_image)
        iv_bank_image.loadSvg(imageUrl)
        val ivMore = dialog.findViewById<AppCompatImageView>(R.id.ivMore)
        ivMore.setOnClickListener {
            openKnowMoreDialog()

        }
        val tvCardNumber = dialog.findViewById<AppCompatTextView>(R.id.tvCardNumber)
        tvCardNumber.text = "··· " + item.last4
        val flSolidColor = dialog.findViewById<ImageView>(R.id.flSolidColor)
        if (item.bankMasterRecord?.ui_config != null) {
            GradientUtils.setBoarderStroke(
                item.bankMasterRecord.ui_config.cardColor, "", true, flSolidColor
            )
        }
        val tvBankName = dialog.findViewById<AppCompatTextView>(R.id.tvBankName)
        tvBankName.text = item.bankMasterRecord?.billerName
        val etAmount = dialog.findViewById<EditText>(R.id.etAmount)
        val tvCaption = dialog.findViewById<AppCompatTextView>(R.id.tvCaption)
        val tvRewards = dialog.findViewById<AppCompatTextView>(R.id.tvRewards)
        val chipTotalDue = dialog.findViewById<Chip>(R.id.chipTotalDue)
        val chipMinimumDue = dialog.findViewById<Chip>(R.id.chipMinimumDue)
        val customChip = dialog.findViewById<Chip>(R.id.chipCustom)
        val tvRewardPercentage = dialog.findViewById<AppCompatTextView>(R.id.tvRewardPercentage)
        val tvAtTheRate = dialog.findViewById<AppCompatTextView>(R.id.tvAtTheRate)
        val llMessageMinimumDue = dialog.findViewById<LinearLayoutCompat>(R.id.llMessageMinimumDue)
        val llMessageTotalDue = dialog.findViewById<LinearLayoutCompat>(R.id.llMessageTotalDue)
        val llAmtView = dialog.findViewById<LinearLayoutCompat>(R.id.llAmtView)
        val llMessageError = dialog.findViewById<LinearLayoutCompat>(R.id.llMessageError)
        val llRewards = dialog.findViewById<LinearLayoutCompat>(R.id.llRewards)
        val tvError = dialog.findViewById<TextView>(R.id.tvError)
        val ivLoanError = dialog.findViewById<AppCompatImageView>(R.id.ivLoanError)
        val btnOkay = dialog.findViewById<AppCompatButton>(R.id.btnOkay)
        val tvRewardEarned = dialog.findViewById<AppCompatTextView>(R.id.tvRewardEarned)
        tvError.setTextColor(Color.BLACK)
        ivLoanError.setImageResource(R.drawable.ic_warning)
        if ((item.bill?.total_due ?: 0.0) >= 100) {
            rewardsPoint = Utils.roundupAmount(
                (item.bill?.total_due?.minus(item.bill.amount_paid)) ?: DEFAULT_PERCENTAGE,
                DEFAULT_PERCENTAGE,
                tvRewardPercentage,
                item.bankMasterRecord?.id,
                Utils.itemList
            )
            item.rewardsPoint = rewardsPoint
            llRewards.visibility = View.VISIBLE
            billStatus = FULL
            item.billStatus = FULL
            tvRewards.text = getString(R.string.str_you_will_earn_reward_user, rewardsPoint)
        } else if ((item.bill?.total_due ?: 0.0) < 100) {
            llRewards.visibility = View.VISIBLE
            tvRewards.text = getString(R.string.str_pay_total_bill)
        } else {
            llRewards.visibility = View.GONE
        }
        if (totalDue != null && totalDue > 0) {
            etAmount.setText(Utils.getFormattedDecimal(totalDue.toDouble()))
            btnOkay.isEnabled = true
        }

        chipTotalDue.setOnClickListener {
            billStatus = FULL
            item.billStatus = FULL
            if (totalDue != null) {
                etAmount.setText(Utils.getFormattedDecimal(totalDue.toDouble()))
            }
            etAmount.isEnabled = false
            llMessageError.visibility = View.GONE
            llMessageTotalDue.visibility = View.VISIBLE
            llMessageMinimumDue.visibility = View.GONE
            tvCaption.text = resources.getText(R.string.you_have_to_pay)
            if ((item.bill?.total_due ?: 0.0) >= 100) {

                rewardsPoint = Utils.roundupAmount(
                    (item.bill?.total_due?.minus(item.bill.amount_paid!!)) ?: DEFAULT_PERCENTAGE,
                    DEFAULT_PERCENTAGE,
                    tvRewardPercentage,
                    item.bankMasterRecord?.id,
                    Utils.itemList
                )

                item.rewardsPoint = rewardsPoint
                llRewards.visibility = View.VISIBLE
                billStatus = FULL
                item.billStatus = FULL
                MonthlyEarnRule.setRewardLimit(
                    rewardsCanAssign,
                    rewardsAssignUpto,
                    rewardsPoint,
                    tvRewards,
                    tvAtTheRate,
                    tvRewardPercentage,
                    tvRewardEarned,
                    requireContext()
                )
            } else if ((item.bill?.total_due ?: 0.0) < 100) {
                llRewards.visibility = View.VISIBLE
                tvRewards.text = getString(R.string.str_pay_total_bill)
            } else {
                llRewards.visibility = View.GONE
            }
        }
        var paymentAmountExactness = item.bankMasterRecord?.paymentAmountExactness

        if (paymentAmountExactness == "ADHOC") {
            customChip.visibility = View.VISIBLE
            llMessageError.visibility = View.GONE
            if (totalDue != null && totalDue > 0 && !totalDue.equals(0)) {
                chipTotalDue.visibility = View.VISIBLE
            } else {
                chipTotalDue.visibility = View.GONE
            }
            if (minDue != null && minDue > 0 && !minDue.equals(0)) {
                chipMinimumDue.visibility = View.VISIBLE
            } else {
                chipMinimumDue.visibility = View.GONE
            }
            etAmount.isEnabled = true
            etAmount.requestFocus()
            llMessageTotalDue.visibility = View.GONE
            llMessageMinimumDue.visibility = View.GONE
            tvCaption.text = getString(R.string.str_enter_amount)
            activity?.let { it1 -> Utils.showKeyboard(it1) }

        } else if (paymentAmountExactness == "EXACT_AND_BELOW") {
            chipMinimumDue.visibility = View.GONE
            chipTotalDue.visibility = View.VISIBLE
            customChip.visibility = View.VISIBLE


        } else if (paymentAmountExactness == "EXACT_AND_ABOVE") {
            chipMinimumDue.visibility = View.GONE
            chipTotalDue.visibility = View.VISIBLE
            customChip.visibility = View.VISIBLE

        } else if (paymentAmountExactness == "EXACT") {
            chipMinimumDue.visibility = View.GONE
            chipTotalDue.visibility = View.GONE
            customChip.visibility = View.VISIBLE

            etAmount.isEnabled = false
            // customChip.style

        } else {
            etAmount?.setText(totalDue.toString())
            etAmount?.isEnabled = false
            customChip.visibility = View.VISIBLE
            chipMinimumDue.visibility = View.GONE
            chipTotalDue.visibility = View.VISIBLE
            llMessageError.visibility = View.GONE
            llMessageError.visibility = View.GONE
        }

        if (totalDue != null && totalDue > 0 && !totalDue.equals(0)) {
            chipTotalDue.visibility = View.VISIBLE

        } else {
            llRewards.visibility = View.GONE
            tvRewards.text = ""
            chipTotalDue.visibility = View.GONE
            billStatus = CUSTOM
            customChip.isChecked = true

            etAmount.isEnabled = true
            etAmount.requestFocus()
            etAmount.setText("")
            btnOkay.isEnabled = false
            llMessageTotalDue.visibility = View.GONE
            llMessageMinimumDue.visibility = View.GONE
            tvCaption.text = getString(R.string.str_enter_amount)
            activity?.let { it1 -> Utils.showKeyboard(it1) }
        }

        if (rewardsAssignUpto != 0) {
            tvRewardEarned.text =
                "$rewardsAssigned/$rewardsAssignUpto chips have been earned this month!"
        }
        MonthlyEarnRule.setRewardLimit(
            rewardsCanAssign,
            rewardsAssignUpto,
            rewardsPoint,
            tvRewards,
            tvAtTheRate,
            tvRewardPercentage,
            tvRewardEarned,
            requireContext()
        )
        etAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (p0.toString().isNotEmpty()) {
                    val amount = p0.toString().replace(",", "").toDouble()
                    if (billStatus == CUSTOM) {

                        if (amount > 0 && amount < 100) {
                            tvRewards.text = getString(R.string.str_enter_100)
                            tvRewardPercentage.visibility = View.GONE
                            tvAtTheRate.visibility = View.GONE
                            btnOkay.isEnabled = true
                            llRewards.visibility = View.VISIBLE
                            tvRewardEarned.visibility = View.GONE
                        } else {

                            rewardsPoint = Utils.roundupAmount(
                                amount,
                                DEFAULT_PERCENTAGE,
                                tvRewardPercentage,
                                item.bankMasterRecord?.id,
                                Utils.itemList
                            )
                            MonthlyEarnRule.setRewardLimit(
                                rewardsCanAssign,
                                rewardsAssignUpto,
                                rewardsPoint,
                                tvRewards,
                                tvAtTheRate,
                                tvRewardPercentage,
                                tvRewardEarned,
                                requireContext()
                            )

                            btnOkay.isEnabled = true
                        }
                    } else {
                        if (amount > 0 && amount < 100) {
                            tvRewards.text = getString(R.string.str_enter_100)
                            tvRewardPercentage.visibility = View.GONE
                            tvAtTheRate.visibility = View.GONE
                            btnOkay.isEnabled = true
                            llRewards.visibility = View.VISIBLE
                            tvRewardEarned.visibility = View.GONE
                        } else {

                            rewardsPoint = Utils.roundupAmount(
                                amount,
                                DEFAULT_PERCENTAGE,
                                tvRewardPercentage,
                                item.bankMasterRecord?.id,
                                Utils.itemList
                            )
                            MonthlyEarnRule.setRewardLimit(
                                rewardsCanAssign,
                                rewardsAssignUpto,
                                rewardsPoint,
                                tvRewards,
                                tvAtTheRate,
                                tvRewardPercentage,
                                tvRewardEarned,
                                requireContext()
                            )
                            btnOkay.isEnabled = true


                        }
                        if (paymentAmountExactness == "ADHOC") {
                            btnOkay.isEnabled = true
                        } else if (paymentAmountExactness == "EXACT_AND_BELOW") {
                            if (totalDue != null) {
                                if (amount > totalDue) {
                                    tvError.text = getString(R.string.str_enter_amount_is_more)
                                    llMessageError.visibility = View.VISIBLE
                                    btnOkay.isEnabled = false
                                }
                            }
                        } else if (paymentAmountExactness == "EXACT_AND_ABOVE") {
                            if (totalDue != null) {
                                if (amount < totalDue) {
                                    tvError.text = getString(R.string.str_amount_less_total_due)
                                    llMessageError.visibility = View.VISIBLE
                                    btnOkay.isEnabled = true
                                }
                            }
                        } else {
                            btnOkay.isEnabled = true
                        }
                    }
                } else {
                    llMessageError.visibility = View.GONE
                    btnOkay.isEnabled = false
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        etAmount.setOnFocusChangeListener { _, hasFocused ->
            if (hasFocused) {
                llAmtView.setBackgroundResource(R.drawable.et_btm_bg)
            } else {
                llAmtView.setBackgroundResource(R.drawable.et_btm_bg_un_focused)

            }
        }


        customChip.setOnClickListener {
            llRewards.visibility = View.GONE
            tvRewards.text = ""
            billStatus = CUSTOM
            etAmount.isEnabled = true
            etAmount.requestFocus()
            etAmount.setText("")
            btnOkay.isEnabled = false
            llMessageTotalDue.visibility = View.GONE
            llMessageMinimumDue.visibility = View.GONE
            tvCaption.text = getString(R.string.str_enter_amount)
            activity?.let { it1 -> Utils.showKeyboard(it1) }

        }

        btnOkay.setOnClickListener {
            if (etAmount.text.toString().isNotEmpty()) {
                if (etAmount.text.toString().replace(",", "").toDouble() < 10) {
                    tvError.text = getString(R.string.str_amount_greater_10)
                    llMessageError.visibility = View.VISIBLE
                } else if (etAmount.text.toString().replace(",", "").toDouble() > 1000000) {
                    tvError.text = getString(R.string.str_amount_less_1000000)
                    llMessageError.visibility = View.VISIBLE
                } else {
                    if (product_number != null) {
                        val amount = etAmount.text.toString().replace(",", "")
                        item.customAmount = amount.toDouble()
                        item.rewardsPoint = rewardsPoint
                        item.billStatus = billStatus
                        val selectedProduct: ArrayList<ProductV2> = ArrayList()
                        selectedProduct.add(item)
                        requireActivity().startActivity(
                            Intent(requireContext(), PaymentMethodsActivity::class.java).putExtra(
                                IntentKeys.PRODUCT_LIST, selectedProduct
                            ).putExtra(IntentKeys.IS_PAY_TOGETHER, false)
                                .putExtra(IntentKeys.TOTAL_AMOUNT, amount.trim())
                                .putExtra(IntentKeys.REWARDS_POINT, rewardsPoint)
                                .putExtra(IntentKeys.BILL_STATUS, billStatus)
                                .putExtra(IntentKeys.REWARDS_ASSIGN_UPTO, rewardsAssignUpto)
                                .putExtra(IntentKeys.REWARDS_CAN_ASSIGN, rewardsCanAssign)
                                .putExtra(IntentKeys.REWARDS_ASSIGNED, rewardsAssigned)
                        )


                    }
                    Utils.hideKeyboard(activity)
                    dialog.dismiss()

                }
            }
        }
        dialog.show()
    }


    fun roundupAmount(amount: Double, percentage: Double): Int {
        val percentageAMount = ((percentage * (amount)) / 100)
        return floor(percentageAMount).toInt()
    }

    fun initLocalFirebase() {

        CommonFirebaseData.getDataBaseRefrence(SMAIL_OFFER_WIDGET) {
            try {
                if (it.visibility == true) {
                    mBinding?.rvSmallOfferWidgets?.visibility = View.VISIBLE
                    mBinding?.tvSmallOffersForYou?.visibility = View.VISIBLE
                    mBinding?.tvSmallOffersForYou?.text = it.section_title?:"OFFERS FOR YOU"

                    if (it.offers != null) {
                        val smallOffer: ArrayList<OffersItem> = arrayListOf()
                        for (item in it.offers) {
                            if (BannerConstant.isVisible(item, HOME)) {
                                if (item != null) {
                                    smallOffer.add(item)
                                }
                            }
                        }
                        initSmallOfferAdapter(smallOffer)
                    } else {
                        mBinding?.homeSmallBanner?.ivHomePlaceholderTopBanner?.visibility =
                            View.GONE
                    }
                } else {
                    mBinding?.rvSmallOfferWidgets?.visibility = View.GONE
                    mBinding?.homeSmallBanner?.ivHomePlaceholderTopBanner?.visibility = View.GONE
                    mBinding?.tvSmallOffersForYou?.visibility = View.GONE

                }


            } catch (e: Exception) {
                mBinding?.homeSmallBanner?.ivHomePlaceholderTopBanner?.visibility = View.GONE
            }
        }

        CommonFirebaseData.getDataBaseRefrence(BIG_OFFER_WIDGET) {
            try {

                if (it.visibility == true) {
                    mBinding?.rvOffersForYou?.visibility = View.VISIBLE
                    mBinding?.tvOffersForYou?.visibility = View.VISIBLE
                    mBinding?.tvOffersForYou?.text = it.section_title?:"OFFERS FOR YOU"
                    val offerForYou: ArrayList<OffersItem> = arrayListOf()

                    if (it.offers != null) {
                        for (item in it.offers) {
                            if (BannerConstant.isVisible(item, HOME)) {
                                if (item != null) {
                                    offerForYou.add(item)
                                }

                            }
                        }
                        initOfferForYouAdapter(offerForYou)
                    } else {
                        mBinding?.offerForYouBanner?.ivHomePlaceholderTopBanner?.visibility =
                            View.GONE
                    }
                } else {
                    mBinding?.rvOffersForYou?.visibility = View.GONE
                    mBinding?.offerForYouBanner?.ivHomePlaceholderTopBanner?.visibility = View.GONE
                    mBinding?.tvOffersForYou?.visibility = View.GONE
                }


            } catch (e: Exception) {
                mBinding?.offerForYouBanner?.ivHomePlaceholderTopBanner?.visibility = View.GONE
            }
        }
    }

    private fun initOfferForYouAdapter(offerForYou: ArrayList<OffersItem>) {
        if (offerForYou.isNotEmpty()) {

            offerForYouWidgetAdapter = OffersForYouWidgetsAdapter(
                requireContext(),
                mBinding?.rvOffersForYou,
                mBinding?.offerForYouBanner?.ivHomePlaceholderTopBanner,
                offerForYou
            )

            mBinding?.rvOffersForYou?.adapter = offerForYouWidgetAdapter
            offerForYouWidgetAdapter.onItemClick = { it, position ->
                activity?.let { ac ->
                    MparticleUtils.logEvent(
                        "Banner_Clicked",
                        "user clicks on the banner",
                        "Unique",
                        "Continue",
                        null,
                        ac,
                        otherAttributes = mapOf(
                            "position" to position.toString(),
                            "screen" to "home",
                            "banner-type" to "big",
                            "offer-name" to (it?.meta_data ?: "")
                        )
                    )
                }

                if (it?.banner_redirection_type == POP_UP) {
                    openOfferPopUp(it)
                } else if (it?.banner_redirection_type == DEEP_LINK) {
                    it.banner_redirection?.let {
                        getDeeplinkIntent(requireContext(), it)
                    }?.let {
                        startActivity(it)
                    }
                } else if (it?.banner_redirection_type == WEB_LINK) {
                    startActivity(
                        Intent(activity, CommonWebViewActivity::class.java).putExtra(
                            Constant.URL, "${it.banner_redirection}"
                        )
                    )
                }
            }
        } else {

            mBinding?.tvOffersForYou?.visibility = View.GONE
            mBinding?.rvOffersForYou?.visibility = View.GONE
            mBinding?.offerForYouBanner?.ivHomePlaceholderTopBanner?.visibility = View.GONE
        }


    }

    private fun openOfferPopUp(item: OffersItem) {
        val dialog = Utils.showCustomDialogBottum(activity, R.layout.bottom_sheet_pop_up)
        val bindingSheet = DataBindingUtil.inflate<BottomSheetPopUpBinding>(
            layoutInflater, R.layout.bottom_sheet_pop_up, null, false
        )
        dialog.setContentView(bindingSheet.root)
        dialog.setCancelable(false)
        bindingSheet.ivCancel.setOnClickListener {
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

        bindingSheet?.btnSecondary?.setOnClickListener {
            activity?.let { ac ->
                MparticleUtils.logEvent(
                    "Offer_SecondaryCTA_Clicked",
                    "When user clicks on primary CTA",
                    "Unique",
                    "Continue",
                    null,
                    ac
                )
            }
            if (item.banner_details?.cta?.secondary?.action_type == CLOSE_SCREEN) {
                dialog.dismiss()
            } else {
                dialog.dismiss()
            }

        }
        bindingSheet?.btnPrimary?.setOnClickListener {
            activity?.let { ac ->
                MparticleUtils.logEvent(
                    "Offer_PrimaryCTA_Clicked",
                    "When user clicks on primary CTA",
                    "Unique",
                    bindingSheet.btnPrimary.text.toString(),
                    null,
                    ac
                )
            }
            if (item.banner_details?.cta?.primary?.action_type == DEEP_LINK) {
                item.banner_details.cta?.primary?.action?.let {
                    getDeeplinkIntent(requireContext(), it)
                }?.let {
                    startActivity(it)
                }
                dialog.dismiss()
            } else if (item.banner_details?.cta?.primary?.action_type == WEB_LINK) {
                startActivity(
                    Intent(activity, CommonWebViewActivity::class.java).putExtra(
                        Constant.URL, "${item.banner_details.cta.primary.action}"
                    )
                )
                dialog.dismiss()
            }
        }

        bindingSheet?.tvTandC?.setOnClickListener {
            startActivity(
                Intent(activity, CommonWebViewActivity::class.java).putExtra(
                    Constant.URL, "${item.banner_details?.tncLink}"
                )
            )
            dialog.dismiss()
        }
        val layoutParams = bindingSheet.flMainContent.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.dimensionRatio = item.banner_details?.aspect_ratio
        bindingSheet.flMainContent.layoutParams = layoutParams
        if (item.banner_details?.descprition_asset_type == 1) {
            Glide.with(requireActivity()).load(item.banner_details.descprition_asset)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(bindingSheet.ivOfferImage)
        } else {

            bindingSheet.animationView.setAnimationFromUrl(
                item.banner_details?.descprition_asset ?: ""
            )

        }



        bindingSheet.btnPrimary.text = item.banner_details?.cta?.primary?.text ?: ""
        bindingSheet.btnSecondary.text = item.banner_details?.cta?.secondary?.text ?: ""
        dialog.show()
    }

    private fun initSmallOfferAdapter(smallOffer: ArrayList<OffersItem>) {
        if (smallOffer.isNotEmpty()) {
            mBinding?.homeSmallBanner?.ivHomePlaceholderTopBanner?.visibility = View.VISIBLE

            offerWidgetAdapter = SmallOfferWidgetsAdapter(
                requireContext(),
                mBinding?.rvSmallOfferWidgets,
                mBinding?.homeSmallBanner?.ivHomePlaceholderTopBanner,
                smallOffer
            )
            mBinding?.rvSmallOfferWidgets?.adapter = offerWidgetAdapter
            offerWidgetAdapter.onItemClick = { it, position ->
                activity?.let { ac ->
                    MparticleUtils.logEvent(
                        "Banner_Clicked",
                        "user clicks on the banner",
                        "Unique",
                        "Continue",
                        null,
                        ac,
                        otherAttributes = mapOf(
                            "position" to position.toString(),
                            "screen" to "home",
                            "banner-type" to "small",
                            "offer-name" to (it?.meta_data ?: "")
                        )
                    )
                }
                if (it?.banner_redirection_type == POP_UP) {
                    openOfferPopUp(it)
                } else if (it?.banner_redirection_type == DEEP_LINK) {
                    it.banner_redirection?.let {
                        getDeeplinkIntent(requireContext(), it)
                    }?.let {
                        startActivity(it)
                    }
                } else if (it?.banner_redirection_type == WEB_LINK) {
                    startActivity(
                        Intent(activity, CommonWebViewActivity::class.java).putExtra(
                            Constant.URL, "${it.banner_redirection}"
                        )
                    )
                }

            }
        } else {
            mBinding?.homeSmallBanner?.ivHomePlaceholderTopBanner?.visibility = View.GONE
            mBinding?.rvSmallOfferWidgets?.visibility = View.GONE
        }

    }


    override fun isOverDue(isOverDue: Boolean) {
        if (isOverDue) {
            mBinding?.layoutTotalDue?.ProductBack?.setBackgroundResource(R.drawable.overdue_back_on_home_and_dashboard)
        }
    }

    fun onPayMoreCLick(
        productV2: ProductV2
    ) {



        val dialog = Utils.showCustomDialogBottum(activity, R.layout.bottom_sheet_pay_more)
        var rewardsPoint = 0
        val bindingSheet = DataBindingUtil.inflate<BottomSheetPayMoreBinding>(
            layoutInflater, R.layout.bottom_sheet_pay_more, null, false
        )

        bankImageUrl = "${prefs?.bankMasterUrl}${productV2.bankMasterRecord?.id}-logo-with-name.svg"
        dialog.setContentView(bindingSheet.root)
        dialog.setCancelable(false)
        activity?.let { Utils.showKeyboard(it) }
        bindingSheet.ivCancel.setOnClickListener {
            dialog.dismiss()
            Utils.hideKeyboard(activity)
        }
        if (rewardsPoint == 0) {
            bindingSheet.llRewards.visibility = View.GONE
        } else {
            bindingSheet.llRewards.visibility = View.VISIBLE
        }
        bindingSheet.etAmount.setOnFocusChangeListener { _, hasFocused ->
            if (hasFocused) {
                bindingSheet.llAmtView.setBackgroundResource(R.drawable.et_btm_bg)
            } else {
                bindingSheet.llAmtView.setBackgroundResource(R.drawable.et_btm_bg_un_focused)
            }
        }
        if (rewardsAssignUpto != 0) {
            bindingSheet.tvRewardEarned.text =
                "$rewardsAssigned/$rewardsAssignUpto chips have been earned this month!"
        }
        bindingSheet.llCreditCard.ivBankImage.loadSvg(bankImageUrl)
        bindingSheet.ivMore.setOnClickListener {
            openKnowMoreDialog()
        }
        GradientUtils.setBoarderStroke(
            productV2.bankMasterRecord?.ui_config?.cardColor ?: "#FFFFFF",
            productV2.bankMasterRecord?.ui_config?.opacity_border ?: "#FFFFFF",
            true,
            bindingSheet.llCreditCard.flStroke
        )
        GradientUtils.setBackGround(
            productV2.bankMasterRecord?.ui_config?.cardColor ?: "#FFFFFF",
            "",
            productV2.bankMasterRecord?.ui_config?.opacity_topLeft ?: "#FFFFFF",
            productV2.bankMasterRecord?.ui_config?.opacity_bottomRight ?: "#FFFFFF",
            bindingSheet.llCreditCard.llCardSolidBackGround
        )
        bindingSheet.llCreditCard.tvCardNumber.text = "···  ${productV2.last4}"


        bindingSheet.llCreditCard.tvBankName.text = productV2.bankMasterRecord?.bankName ?: ""
        bindingSheet.etAmount.setText("")
        bindingSheet.etAmount.requestFocus()

        when (productV2.card_network) {
            AFConstants.MASTER_CARD -> {
                bindingSheet.llCreditCard.ivCardType.setImageResource(R.drawable.ic_mastercard)
            }
            AFConstants.VISA -> {
                bindingSheet.llCreditCard.ivCardType.setImageResource(R.drawable.visa)
            }
            AFConstants.AMERICAN_CARD -> {
                bindingSheet.llCreditCard.ivCardType.setImageResource(R.drawable.ic_american_icon)
            }
            AFConstants.DINERS_CARD -> {
                bindingSheet.llCreditCard.ivCardType.setImageResource(R.drawable.ic_dinner_icon)
            }
            AFConstants.RUPAY_CARD -> {
                bindingSheet.llCreditCard.ivCardType.setImageResource(R.drawable.ic_rupay_card_icon)
            }
        }
        bindingSheet.etAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isNotEmpty()) {
                    val amountForRewards = p0.toString().toDouble()
                    if (amountForRewards > 0 && amountForRewards < 100) {
                        bindingSheet.tvRewards.text = getString(R.string.str_enter_100)
                        bindingSheet.tvRewardPercentage.visibility = View.GONE
                        bindingSheet.tvAtTheRate.visibility = View.GONE
                        bindingSheet.llRewards.visibility = View.VISIBLE
                    } else {
                        rewardsPoint = Utils.roundupAmount(
                            amountForRewards,
                            PaymentMethodsActivity.SupportedUPIApps.DEFAULT_PERCENTAGE,
                            bindingSheet.tvRewardPercentage,
                            productV2.bankMasterRecord?.id,
                            Utils.itemList
                        )
                        bindingSheet.tvRewardPercentage.visibility = View.VISIBLE
                        bindingSheet.tvAtTheRate.visibility = View.VISIBLE
                        bindingSheet.llRewards.visibility = View.VISIBLE
                        MonthlyEarnRule.setRewardLimit(
                            rewardsCanAssign,
                            rewardsAssignUpto,
                            rewardsPoint,
                            bindingSheet.tvRewards,
                            bindingSheet.tvAtTheRate,
                            bindingSheet.tvRewardPercentage,
                            bindingSheet.tvRewardEarned,
                            requireContext()
                        )

                    }

                    bindingSheet.btnOkay.isEnabled = true
                    val amount = p0.toString().replace(",", "").toDouble()
                    if (amount <= 1000000) {
                        bindingSheet.llMessageError.visibility = View.GONE
                    }
                } else {
                    bindingSheet.llMessageError.visibility = View.GONE
                    bindingSheet.btnOkay.isEnabled = false
                    bindingSheet.llRewards.visibility = View.GONE
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        bindingSheet.btnOkay.setOnClickListener {
            billStatus = AFConstants.CUSTOM
            if (!bindingSheet.etAmount.text.toString().isNullOrEmpty()) {
                if (bindingSheet.etAmount.text.toString().replace(",", "").toDouble() < 10) {
                    bindingSheet.llMessageError.visibility = View.VISIBLE
                    bindingSheet.tvError.text = getString(R.string.str_minimum_amount)

                } else if (bindingSheet.etAmount.text.toString().replace(",", "")
                        .toDouble() > 1000000
                ) {
                    bindingSheet.llMessageError.visibility = View.VISIBLE
                    bindingSheet.tvError.text = getString(R.string.str_amount_less_1000000)
                } else {
                    bindingSheet.llMessageError.visibility = View.GONE
                    val amount = bindingSheet.etAmount.text.toString().replace(",", "")
                    productV2.customAmount = amount.toDouble()
                    productV2.rewardsPoint = rewardsPoint
                    productV2.billStatus = billStatus
                    val selectedProduct: ArrayList<ProductV2> = ArrayList()
                    selectedProduct.add(productV2)

                    requireActivity().startActivity(
                        Intent(requireContext(), PaymentMethodsActivity::class.java).putExtra(
                            IntentKeys.PRODUCT_LIST, selectedProduct
                        ).putExtra(IntentKeys.IS_PAY_TOGETHER, false)
                            .putExtra(IntentKeys.TOTAL_AMOUNT, amount.trim())
                            .putExtra(IntentKeys.REWARDS_POINT, rewardsPoint)
                            .putExtra(IntentKeys.BILL_STATUS, billStatus)
                            .putExtra(IntentKeys.REWARDS_ASSIGN_UPTO, rewardsAssignUpto)
                            .putExtra(IntentKeys.REWARDS_CAN_ASSIGN, rewardsCanAssign)
                            .putExtra(IntentKeys.REWARDS_ASSIGNED, rewardsAssigned)
                    )

                    Utils.hideKeyboard(activity)


                }
            } else {
                bindingSheet.tvError.text = getString(R.string.str_minimum_amount)
            }
        }
        dialog.show()
    }

    private fun showErrorBlocker(blockedData: BlockData?) {
        val intent = Intent(getActivity(), BlockerActivity::class.java).apply {
            putExtra(BlockerActivity.EXTRA_BLOCKER, blockedData)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }
}