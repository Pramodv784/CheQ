package com.cheq.retail.ui.rewards.view

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AnimationUtils
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.cheq.retail.R
import com.cheq.retail.api.errormodel.BlockData
import com.cheq.retail.application.MainApplication.Companion.getApplicationContext
import com.cheq.retail.base.FirebaseRemoteConfigUtils
import com.cheq.retail.constants.*
import com.cheq.retail.constants.Constant.Companion.BIG_OFFER_WIDGET
import com.cheq.retail.databinding.*
import com.cheq.retail.extensions.bannerBaseUrl
import com.cheq.retail.extensions.voucherBaseUrl
import com.cheq.retail.sharePreferences.SharePrefCheqUserId
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.blocker_screen.BlockerActivity
import com.cheq.retail.ui.dashboard.view.fragment.HomeFragment.Companion.REWARDS
import com.cheq.retail.ui.main.MainActivity
import com.cheq.retail.ui.main.OffersItem
import com.cheq.retail.ui.main.maininterface.RewardsInterface
import com.cheq.retail.ui.main.model.*
import com.cheq.retail.ui.main.model.request.RedeemCouponRequest
import com.cheq.retail.ui.rewards.adapter.*
import com.cheq.retail.ui.rewards.repository.RewardsRepository
import com.cheq.retail.ui.rewards.util.State
import com.cheq.retail.ui.rewards.viewmodel.RewardsViewModelFactory
import com.cheq.retail.ui.rewards.viewmodel.RewardsViewModelNew
import com.cheq.retail.utils.CommonFirebaseData
import com.cheq.retail.utils.DateTimeUtils
import com.cheq.retail.utils.ImageUtils.loadSvg
import com.cheq.retail.utils.InAppReview
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.RealTimeDatabase
import com.cheq.retail.utils.RealTimeDatabase.SHOW_APP_RATING_REWARDS
import com.cheq.retail.utils.Utils
import com.cheq.retail.utils.Utils.Companion.setupFullHeight
import com.cheq.retail.utils.Utils.Companion.setupFullHeightBottomSheet
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*


open class RewardsFragment : Fragment(), RewardsInterface,
    TopFeaturedDealAdapter.OnFeatureDealClicked,OtherFeaturedDealAdapter.OnOtherFeatureDealClicked {
    private var activity: MainActivity? = null
    var mBinding: FragmentRewardsBinding? = null
    private var llButton: LinearLayoutCompat? = null
    private var btnRedeemNow: AppCompatButton? = null
    private var rewardsViewModelNew: RewardsViewModelNew? = null
    private var repository: RewardsRepository? = null
    private var voucherAdapter: SelectVoucherAdapter? = null
    private var tAndCUrl = ""
    private var selectedBrandLogo = ""
    private var rewardsDashboardList: ArrayList<RewardModuleItem> = ArrayList()
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var lottie_animation: LottieAnimationView? = null
    private var animationDialog: Dialog? = null
    val refUrl = SharePrefCheqUserId.getInstance(getApplicationContext())
        ?.getString(SharePrefsKeys.REF_SHORT_URL)
    var smallOfferWidgetList = arrayListOf<OffersItem>()
    private lateinit var voucherSuccessBottomSheetDialog: BottomSheetDialog
    var prefs: SharePrefs? = null
    private lateinit var rewardDashboardAdapter: RewardsDashboardAdapter
    var smallOfferWidgetTitle: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_rewards, container, false)
        setupViewModel()
        setupUI()
        return mBinding!!.root
    }

    private fun setupViewModel() {
    }

    override fun onAttach(_context: Context) {
        super.onAttach(_context)
        activity = _context as MainActivity
    }

    @SuppressLint("SetTextI18n")
    private fun setupUI() {
        repository = RewardsRepository(requireContext())
        rewardsViewModelNew = ViewModelProvider(
            this, RewardsViewModelFactory(repository!!)
        )[RewardsViewModelNew::class.java]
        initLocalFirebase()
        prefs = SharePrefs.getInstance(requireContext())
        rewardsViewModelNew?.redeemVoucherLiveData?.observe(requireActivity()) {
            when (it) {
                is State.Error -> {
                    mBinding?.loading?.visibility = View.GONE
                    animationDialog?.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                is State.Success -> {
                    mBinding?.loading?.visibility = View.GONE
                    doRedeemProcess(it)
                }
                is State.Loading -> {
                    mBinding?.loading?.visibility = View.VISIBLE
                }
                is State.NetworkError -> {
                    mBinding?.loading?.visibility = View.GONE
                    Toast.makeText(requireContext(), it.netWorkMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
        activity?.setBtnVisibilityFull(true)
        rewardsViewModelNew?.rewardsDashboardLiveData?.observe(viewLifecycleOwner) {
            when (it) {
                is State.Success -> {
                    mBinding?.loading?.visibility = View.GONE
                    if (it.data.isUserBlocked()) {
                        showErrorBlocker(it.data.blockedData)
                    } else {
                        loadDashBoardData(it)
                    }
                }
                is State.Error -> {
                    // Utils.hideProgressDialog()
                    mBinding?.loading?.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                is State.Loading -> {
                    mBinding?.loading?.visibility = View.VISIBLE
                    // Utils.showProgressDialog(requireActivity())
                }
                is State.NetworkError -> {
                    mBinding?.loading?.visibility = View.GONE
                    Toast.makeText(requireContext(), it.netWorkMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun doRedeemProcess(it: State.Success<RedeemCouponResponse>) {

        lottie_animation?.loop(false)
        lottie_animation?.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                animationDialog?.dismiss()

                openVoucherSuccessfulRedeemBottomSheet(
                    it.data.data?.amount,
                    it.data.data?.id,
                    it.data.data?.voucherPin,
                    it.data.data?.voucherCode,
                    it.data.data?.pinStatus,
                    it.data.data?.voucherExp,
                    tAndCUrl,
                    selectedBrandLogo,
                    it.data.data?.exceptionNarration,
                    it.data.data?.availPoints,
                    it.data.data?.websiteUrl,
                    it.data.data?.partnerBrandName
                )
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationStart(animation: Animator) {

            }

        })


    }
    private fun loadDashBoardData(it: State.Success<RewardsDashboardResponse>) {
        rewardsDashboardList.clear()
        if (it.data.rewardModule != null) {
            rewardsDashboardList.addAll(it.data.rewardModule)
        }
        rewardDashboardAdapter = RewardsDashboardAdapter(
            requireContext(),
            rewardsDashboardList,
            it.data.howRewardWorks,
            it.data.coinBalance?.toDouble(),
            it.data.lockedChips,
            it.data.convertToCash,
            it.data.convertToVoucher?.toDouble(),
            it.data.earnUpTo?.toDouble(),
            this@RewardsFragment,
            this@RewardsFragment,
            this@RewardsFragment,
            smallOfferWidgetList,
            smallOfferWidgetTitle
        )

        mBinding?.rvDynamic?.adapter = rewardDashboardAdapter
        mBinding?.rvDynamic?.hasFixedSize()
        rewardDashboardAdapter.notifyDataSetChanged()
        SharePrefs.instance?.putInt(SharePrefsKeys.REWARDS_COIN_BALANCE, it.data.coinBalance ?: 0)
    }

    override fun onResume() {
        super.onResume()
        activity?.setBottomTab(1)
        rewardsViewModelNew?.getRewardsDashBoard()
    }

    override fun onMyVoucherClicked(isClicked: Boolean, data: DenomsItem) {
        if (isClicked) {
            llButton?.visibility = View.VISIBLE
            val animationUp1 =
                AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_top)
            llButton?.startAnimation(animationUp1)
            btnRedeemNow?.setOnClickListener {
                bottomSheetDialog?.dismiss()

                MparticleUtils.logCurrentScreen(
                    "/rewards-tab/redeeming-voucher",
                    "The customer views the loading screen after clicking Redeem Now on a voucher",
                    "",
                    "",
                    "",
                    "",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.REWARD_TAB_REEDEEMING_VOUCHER),
                    requireContext()
                )
                rewardsViewModelNew?.redeemCoupon(RedeemCouponRequest(data.id))
                animationDialog = Dialog(requireContext(), android.R.style.Theme_DeviceDefault)
                animationDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
                animationDialog?.setContentView(R.layout.dialog_full_view)
                lottie_animation = animationDialog?.findViewById(R.id.animationView)
                animationDialog?.show()

            }
        } else {
            llButton?.visibility = View.GONE
        }
    }

    override fun onMyVoucherClicked(isClicked: Boolean) {
    }

    override fun onMyC2CClicked(isClicked: Boolean, amount: Float, convertToCashRate: Double) {
        val bottomSheetDialog = Utils.showCustomDialogBottum2(
            requireContext(),
            R.layout.bottom_sheet_convert_to_cash_info_layout,
        )
        val bindingSheet = DataBindingUtil.inflate<BottomSheetConvertToCashInfoLayoutBinding>(
            layoutInflater, R.layout.bottom_sheet_convert_to_cash_info_layout, null, false
        )
        if (amount.equals(0.0f) || amount.equals(0) || amount.equals(0.0)) {
            bindingSheet.btnC2C.text = "BACK TO REWARDS"
            bindingSheet.btnC2C.setOnClickListener {

                bottomSheetDialog.dismiss()
            }
        } else {

            MparticleUtils.logEvent(
                "Rewards_ConvertToCash_Banner_Clicked",
                "User clicks the convert to cash voucher",
                "Unique",
                "Continue",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Rewards_ConvertToCash_Banner_Clicked),
                requireActivity()
            )
            bindingSheet.btnC2C.setOnClickListener {
                startActivity(
                    Intent(
                        requireContext(), SelectAmountActivity::class.java
                    ).putExtra("AMOUNT", amount.toInt())
                        .putExtra("convertToCashRate", convertToCashRate)
                )
                bottomSheetDialog.dismiss()
            }
        }

        bottomSheetDialog.dismissWithAnimation
        if (amount.toInt() <= 0) {
            bindingSheet.tvAmount.text = "Convert CheQ Chips To Cash & Get Now!"
        } else {
            bindingSheet.tvAmount.text = "Convert CheQ Chips To Cash & Get ₹${amount.toInt()} Now!"
        }

        bottomSheetDialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                val behaviour = BottomSheetBehavior.from(it)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                it.setBackgroundResource(R.drawable.round_bottom_sheet)
            }
        }
        bottomSheetDialog.setContentView(bindingSheet.root)
        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.show()
    }

    override fun onCategoriesClicked(catId: String, categoryPosition: Int) {
        voucherAdapter?.setSelection(categoryPosition)
    }

    override fun onTopFeatureClicked(typesItem: TypesItem) {

        MparticleUtils.logEvent(
            "Rewards_FeaturedDeal_Clicked",
            "User clicks any of the featured details",
            "Unique",
            "Continue",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Rewards_ExploreVoucher_SeeAll_Clicked),
            requireContext()
        )
        if(bottomSheetDialog?.isShowing == true){
            bottomSheetDialog?.dismiss()
        }
        bottomSheetDialog =
            Utils.showCustomDialogBottum2(requireContext(), R.layout.bottom_sheet_vouchers_details)
        val bindingSheet = DataBindingUtil.inflate<BottomSheetVouchersDetailsBinding>(
            layoutInflater, R.layout.bottom_sheet_vouchers_details, null, false
        )
        bottomSheetDialog?.setContentView(bindingSheet.root)

        if (typesItem.availPoints != null) {
            bindingSheet.tvHowToAvail.visibility = View.VISIBLE
            val lstValues: List<String> = typesItem.availPoints?.split("|")!!.map { it.trim() }
            val availPointAdapter = AvailPointAdapter(requireContext(), lstValues)
            bindingSheet.rvAvailPoint.apply {
                adapter = availPointAdapter
                hasFixedSize()
            }
        } else {
            bindingSheet.tvHowToAvail.visibility = View.GONE
        }
        bindingSheet.tvCoinCount.text = "1 = ₹${typesItem.denominator ?: 1.00}"
        if (typesItem.exceptionNarration != null) {
            bindingSheet.tvException.text = "Please note: ${typesItem.exceptionNarration}"
            bindingSheet.tvException.visibility = View.VISIBLE
        } else {
            bindingSheet.tvException.visibility = View.GONE
        }
        if (typesItem.voucherValidTill != null) {
            bindingSheet.llValidTil.visibility = View.VISIBLE
            bindingSheet.tvVouchersValid.text =
                "All vouchers will be valid for at least ${typesItem.voucherValidTill} months"
        } else {
            bindingSheet.llValidTil.visibility = View.GONE
        }

        bindingSheet.ivCancel.setOnClickListener {
            bottomSheetDialog?.dismiss()
        }
        tAndCUrl = typesItem.tnc.toString()
        selectedBrandLogo = typesItem.brandLogo.toString()
        Glide.with(this).load("${SharePrefs.instance?.voucherBaseUrl}${typesItem.brandLogo}")
            .into(bindingSheet.ivBrandLogo)
        bottomSheetDialog?.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                val behaviour = BottomSheetBehavior.from(it)
                setupFullHeight(it, requireContext())
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                it.setBackgroundResource(R.drawable.round_bottom_sheet)
            }
        }
        voucherAdapter = SelectVoucherAdapter(
            requireContext(),
            typesItem.denoms as ArrayList<DenomsItem>,
            this,
            typesItem.denominator?.toDouble() ?: 1.00
        )
        bindingSheet.rvSelectVoucher.adapter = voucherAdapter
        val denomsItem = typesItem.denoms.sortedBy {
            it.denominations
        }
        bindingSheet.ivShare.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT, "Hey Check out this Great app:"
            )
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }
        for (item in typesItem.denoms.indices) {
            if (typesItem.cheqBrandAccessoriesId?.get(0)?.denominationAmount == denomsItem[item].denominations) {
                voucherAdapter?.setSelection(item)
            }

        }
        bindingSheet.rvSelectVoucher.hasFixedSize()
        llButton = bottomSheetDialog?.findViewById(R.id.llButton)!!
        btnRedeemNow = bottomSheetDialog?.findViewById(R.id.btnRedeemNow)!!
        bindingSheet.tvTAndC.setOnClickListener {
            openTAndCBottomSheet(typesItem.tnc.toString())
        }
        bottomSheetDialog?.setCancelable(true)
        bottomSheetDialog?.show()
    }



    override fun onOtherTopFeatureClicked(typesItem: TypesItem) {

        if(bottomSheetDialog?.isShowing == true){
            bottomSheetDialog?.dismiss()
        }
        bottomSheetDialog =
            Utils.showCustomDialogBottum2(requireContext(), R.layout.bottom_sheet_vouchers_details)
        val bindingSheet = DataBindingUtil.inflate<BottomSheetVouchersDetailsBinding>(
            layoutInflater, R.layout.bottom_sheet_vouchers_details, null, false
        )
        bottomSheetDialog?.setContentView(bindingSheet.root)

        if (typesItem.availPoints != null) {
            bindingSheet.tvHowToAvail.visibility = View.VISIBLE
            val lstValues: List<String> = typesItem.availPoints.split("|").map { it.trim() }
            val availPointAdapter = AvailPointAdapter(requireContext(), lstValues)
            bindingSheet.rvAvailPoint.apply {
                adapter = availPointAdapter
                hasFixedSize()
            }
        } else {
            bindingSheet.tvHowToAvail.visibility = View.GONE
        }
        bindingSheet.tvCoinCount.text = "1 = ₹${typesItem.denominator ?: 1.00}"
        if (typesItem.exceptionNarration != null) {
            bindingSheet.tvException.text = "Please note: ${typesItem.exceptionNarration}"
            bindingSheet.tvException.visibility = View.VISIBLE
        } else {
            bindingSheet.tvException.visibility = View.GONE
        }
        if (typesItem.voucherValidTill != null) {
            bindingSheet.llValidTil.visibility = View.VISIBLE
            bindingSheet.tvVouchersValid.text =
                "All vouchers will be valid for at least ${typesItem.voucherValidTill} months"
        } else {
            bindingSheet.llValidTil.visibility = View.GONE
        }

        bindingSheet.ivCancel.setOnClickListener {
            bottomSheetDialog?.dismiss()
        }
        tAndCUrl = typesItem.tnc.toString()
        selectedBrandLogo = typesItem.brandLogo.toString()
        Glide.with(this).load("${SharePrefs.instance?.voucherBaseUrl}${typesItem.brandLogo}")
            .into(bindingSheet.ivBrandLogo)
        bottomSheetDialog?.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                val behaviour = BottomSheetBehavior.from(it)
                setupFullHeight(it, requireContext())
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                it.setBackgroundResource(R.drawable.round_bottom_sheet)
            }
        }
        voucherAdapter = SelectVoucherAdapter(
            requireContext(),
            typesItem.denoms as ArrayList<DenomsItem>,
            this,
            typesItem.denominator?.toDouble() ?: 1.00
        )
        bindingSheet.rvSelectVoucher.adapter = voucherAdapter
        val denomsItem = typesItem.denoms.sortedBy {
            it.denominations
        }
        bindingSheet.ivShare.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT, "Hey Check out this Great app:"
            )
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }
        for (item in typesItem.denoms.indices) {
            if (typesItem.cheqBrandAccessoriesId?.get(0)?.denominationAmount == denomsItem[item].denominations) {
                voucherAdapter?.setSelection(item)
            }

        }
        bindingSheet.rvSelectVoucher.hasFixedSize()
        llButton = bottomSheetDialog?.findViewById(R.id.llButton)!!
        btnRedeemNow = bottomSheetDialog?.findViewById(R.id.btnRedeemNow)!!
        bindingSheet.tvTAndC.setOnClickListener {
            openTAndCBottomSheet(typesItem.tnc.toString())
        }
        bottomSheetDialog?.setCancelable(true)
        bottomSheetDialog?.show()
    }



    fun openTAndCBottomSheet(tandc: String) {
        val bottomSheetDialog =
            Utils.showCustomDialogBottum2(requireContext(), R.layout.bottom_sheet_tand_c)
        val bindingSheet = DataBindingUtil.inflate<BottomSheetTandCBinding>(
            layoutInflater, R.layout.bottom_sheet_tand_c, null, false
        )
        bottomSheetDialog.setContentView(bindingSheet.root)
        bottomSheetDialog.setOnShowListener {
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                val behaviour = BottomSheetBehavior.from(it)
                setupFullHeight(it, requireContext())
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                it.setBackgroundResource(R.drawable.round_bottom_sheet)
                behaviour.isDraggable = false
            }
        }

        bindingSheet.webView.settings.javaScriptEnabled = true
        bindingSheet.webView.settings.useWideViewPort = true
        bindingSheet.webView.loadUrl(SharePrefs.instance?.voucherBaseUrl + tandc)
        bindingSheet.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                bindingSheet.webView.visibility = View.GONE
                //  bindingSheet.progressLayout.visibility = View.VISIBLE
                if (progress == 100) {
                    bindingSheet.webView.visibility = View.VISIBLE
                    //   bindingSheet.progressLayout.visibility = View.GONE
                }
            }
        }
        bottomSheetDialog.setCancelable(true)
        bindingSheet.ivCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun openVoucherSuccessfulRedeemBottomSheet(
        amount: Int?,
        id: String?,
        pincode: String?,
        voucherCode: String?,
        pinStatus: String?,
        voucherExp: String?,
        tandc: String,
        selectedBrandLogo: String,
        exception: String?,
        avail: String?,
        goToWebsite: String?,
        partnerBrandName:Any?
    ) {

        voucherSuccessBottomSheetDialog =
            Utils.showCustomDialogBottum2(requireContext(), R.layout.bottom_sheet_voucher_reedem)
        val bindingSheet = DataBindingUtil.inflate<BottomSheetVoucherReedemBinding>(
            layoutInflater, R.layout.bottom_sheet_voucher_reedem, null, false
        )
        MparticleUtils.logCurrentScreen(
            "/rewards-tab/voucher-redeemed",
            "The customer views the post-redemption page for a voucher successfully redeemed",
            "${partnerBrandName}",
            "",
            "",
            "",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.REWARD_TAB_REEDEEMING_VOUCHER_SUCESS),
            requireContext()
        )
        voucherSuccessBottomSheetDialog.setContentView(bindingSheet.root)
        val c = Calendar.getInstance().time
        if (avail != null) {
            bindingSheet.tvHowToAvail.visibility = View.VISIBLE
            val lstValues: List<String> = avail.split("|").map { it.trim() }
            val availPointAdapter = AvailPointAdapter(requireContext(), lstValues)
            bindingSheet.rvAvailPoint.apply {
                adapter = availPointAdapter
                hasFixedSize()
            }
        } else {
            bindingSheet.tvHowToAvail.visibility = View.GONE
        }
        if (exception != null) {
            bindingSheet.tvException.text = "Exception: ${exception}"
            bindingSheet.tvException.visibility = View.VISIBLE
        } else {
            bindingSheet.tvException.visibility = View.GONE
        }
        if (goToWebsite != null) {
            bindingSheet.llGoToWebsite.visibility = View.VISIBLE
            bindingSheet.tvGoToWebsite.setOnClickListener {
                try {
                    val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse(goToWebsite))
                    startActivity(myIntent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(
                        requireContext(),
                        "No application can handle this request." + " Please install a webbrowser",
                        Toast.LENGTH_LONG
                    ).show()
                    e.printStackTrace()
                }
            }
        } else {
            bindingSheet.llGoToWebsite.visibility = View.GONE
        }
        Handler(Looper.getMainLooper()).postDelayed({
            bindingSheet.animationConefetti.visibility = View.VISIBLE
        }, 1000)
        try {
            if (pinStatus == "1") { // show pin when pin status == 0 or 2
                bindingSheet.tvPin.visibility = View.VISIBLE
                bindingSheet.tvPinView.visibility = View.VISIBLE
                bindingSheet.tvPinCode.text = pincode
            } else { // else hide the pinview
                bindingSheet.tvPin.visibility = View.GONE
                bindingSheet.tvPinView.visibility = View.GONE
            }

            voucherSuccessBottomSheetDialog.setOnShowListener {
                val bottomSheetDialog = it as BottomSheetDialog
                val parentLayout =
                    bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                parentLayout?.let { it ->
                    val behaviour = BottomSheetBehavior.from(it)
                    setupFullHeightBottomSheet(it, requireContext())
                    behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                    it.setBackgroundResource(R.drawable.round_bottom_sheet)
                }
            }
            val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = df.format(c)
            val remainingDays = DateTimeUtils.getDaysBetweenDates(
                formattedDate, voucherExp?.replaceRange(
                    10, voucherExp.length, ""
                )
            ).toString().toInt()
            bindingSheet.tvExpires.text = "Expires in $remainingDays days"
            Glide.with(this).load("${SharePrefs.instance?.voucherBaseUrl}${selectedBrandLogo}")
                .into(bindingSheet.ivBrandLogo)
            bindingSheet.tvAmount.text = "₹${amount.toString()}"
            bindingSheet.tvCouponCode.text = voucherCode

            bindingSheet.ivCopy.setOnClickListener {
                val clipboardManager1: ClipboardManager =
                    requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboardManager1.text = bindingSheet.tvCouponCode.text.toString()
                bindingSheet.llCopyToClipboard.visibility = View.VISIBLE
            }

            bindingSheet.ivCopyPin.setOnClickListener {
                val clipboardManager1: ClipboardManager =
                    requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboardManager1.text = bindingSheet.tvPinCode.text.toString()
                bindingSheet.llCopyPinToClipboard.visibility = View.VISIBLE
            }

            voucherSuccessBottomSheetDialog.setCancelable(false)
            bindingSheet.btnDone.setOnClickListener {
                RealTimeDatabase.checkIsInAppReviewEnable(SHOW_APP_RATING_REWARDS) {
                    if (it) {
                        activity?.let { act ->
                            InAppReview.requestReview(act)
                        }
                    }
                }
                openReferAndEarn()
            }
            bindingSheet.ivTAndC.setOnClickListener {
                openTAndCBottomSheet(tandc)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        voucherSuccessBottomSheetDialog.show()
    }

    override fun onCategoriesClicked(categoryPosition: Int) {
        voucherAdapter?.setSelection(categoryPosition)
    }


    fun initLocalFirebase() {
        CommonFirebaseData.getDataBaseRefrence(BIG_OFFER_WIDGET){
            try {
                smallOfferWidgetList.clear()
                if(it.offers != null){
                    if(it.visibility == true){
                        smallOfferWidgetTitle = it.section_title?:getString(R.string.offers_for_you)
                        for (item in it.offers) {
                            if(BannerConstant.isVisible(item, REWARDS)){
                                item?.let { smallOfferWidgetList.add(it) }
                            }

                        }
                        rewardDashboardAdapter.setOfferForYouList(smallOfferWidgetList,
                            smallOfferWidgetTitle!!
                        )
                    }



                }

            } catch (e: Exception) {

            }
        }


    }

    fun openReferAndEarn() {
        MparticleUtils.logEvent(
            "/refer-and-earn/nudge",
            "The customer views the post-redemption page for a voucher successfully redeemed",
            "Unique",
            "Continue",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.REFER_AND_EARN_VIEWED),
            requireContext()
        )
        val bottomSheetDialog =
            Utils.showCustomDialogBottum2(requireContext(), R.layout.bottom_sheet_refer)
        val bindingSheet = DataBindingUtil.inflate<BottomSheetReferBinding>(
            layoutInflater, R.layout.bottom_sheet_refer, null, false
        )
        bindingSheet.ivRefer.loadSvg(prefs?.bannerBaseUrl + "Refer-and-Earn.svg")
        bottomSheetDialog.setCancelable(false)
        bindingSheet.ivRefer.setOnClickListener {
            onWhatsAppclick()
        }
        bindingSheet.llShareOnWA.setOnClickListener {
            MparticleUtils.logEvent(
                "ReferAndEarnNudge_Primary_Clicked",
                "User clicks on invite your friend on the refer and earn page",
                "Unique",
                "Continue",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.REFER_AND_EARN_PRIMARY_CLICKED),
                requireContext()
            )
            onWhatsAppclick()
        }
        bindingSheet.btnNo.setOnClickListener {
            MparticleUtils.logEvent(
                "ReferAndEarnNudge_Secondary_Clicked",
                "User continue without invite your friend on the refer and earn page",
                "Unique",
                "Continue",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.REFER_AND_EARN_SECONDARY_CLICKED),
                requireContext()
            )
            bottomSheetDialog.dismiss()
            voucherSuccessBottomSheetDialog.dismiss()
            rewardsViewModelNew?.getRewardsDashBoard()
        }
        bottomSheetDialog.setContentView(bindingSheet.root)
        bottomSheetDialog.show()
    }
    fun onWhatsAppclick() {
        val beRefUrl = SharePrefCheqUserId.getInstance(getApplicationContext())
            ?.getString(SharePrefsKeys.REF_BE_URL)

        AppsFlyEvents.logEventFly(
            requireContext(),
            AFConstants.AF_REFERRAL_NOW_CLICK,
            AFConstants.AF_ONBOARD_REFERRAL,
            AFConstants.AF_REFERRAL_CLICK
        )
        FirebaseLog.FireBaseLogEvent(
            requireContext(), AFConstants.FBEvent_REFERRAL_NOW_CLICK,
            AFConstants.FBEvent_ONBOARD_REFERRAL, AFConstants.FB_REFERRAL_CLICK
        )

        MparticleUtils.logEvent(
            "ReferAndEarn_InviteYourFriend_Clicked",
            "User clicks on invite your friend on the refer and earn page",
            "Unique",
            "Continue",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Home_ReferAndEarn_Banner_Clicked),
            requireContext()
        )
        try {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                beRefUrl?.replace(
                    "[URL]",
                    refUrl ?: ""
                )
                    ?: (getString(R.string.hey_this_your_referral) + "\n\n" + refUrl)
            )
            sendIntent.type = "text/plain"
            sendIntent.setPackage("com.whatsapp")
            startActivity(sendIntent)
        } catch (exception: Exception) {
            Toast.makeText(
                requireContext(),
                getString(R.string.install_the_app),
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun showErrorBlocker(blockedData: BlockData?) {
        val intent = Intent(getActivity(), BlockerActivity::class.java).apply {
            putExtra(BlockerActivity.EXTRA_BLOCKER, blockedData)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }
}