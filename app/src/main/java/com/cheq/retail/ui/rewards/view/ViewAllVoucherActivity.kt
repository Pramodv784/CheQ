package com.cheq.retail.ui.rewards.view


import android.animation.Animator
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.cheq.retail.R
import com.cheq.retail.application.MainApplication
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.constants.AFConstants
import com.cheq.retail.constants.AppsFlyEvents
import com.cheq.retail.constants.FirebaseLog
import com.cheq.retail.databinding.*
import com.cheq.retail.extensions.bannerBaseUrl
import com.cheq.retail.extensions.voucherBaseUrl
import com.cheq.retail.inappratings.models.response.Action
import com.cheq.retail.inappratings.models.uistate.AppRatingVisibilityUIState
import com.cheq.retail.inappratings.ui.InAppRatingFragment
import com.cheq.retail.inappratings.ui.TOUCHPOINT_VOUCHER
import com.cheq.retail.sharePreferences.SharePrefCheqUserId
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.main.MainActivity
import com.cheq.retail.ui.main.maininterface.RewardsInterface
import com.cheq.retail.ui.main.model.*
import com.cheq.retail.ui.main.model.request.RedeemCouponRequest
import com.cheq.retail.ui.rewards.adapter.AllCategoriesAdapter
import com.cheq.retail.ui.rewards.adapter.AllVouchersAdapter
import com.cheq.retail.ui.rewards.adapter.AvailPointAdapter
import com.cheq.retail.ui.rewards.adapter.SelectVoucherAdapter
import com.cheq.retail.ui.rewards.repository.RewardsRepository
import com.cheq.retail.ui.rewards.util.State
import com.cheq.retail.ui.rewards.viewmodel.RewardsViewModelFactory
import com.cheq.retail.ui.rewards.viewmodel.RewardsViewModelNew
import com.cheq.retail.utils.DateTimeUtils
import com.cheq.retail.utils.ImageUtils.loadSvg
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.RealTimeDatabase
import com.cheq.retail.utils.Utils
import com.cheq.retail.utils.Utils.Companion.setupFullHeightBottomSheet
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class ViewAllVoucherActivity : BaseActivity(), AllVouchersAdapter.VoucherInterface,
    RewardsInterface {
    private lateinit var binding: ActivityViewAllVoucherBinding
    private lateinit var llButton: LinearLayoutCompat
    private lateinit var btnRedeemNow: AppCompatButton
    private var categoryName = ""
    private lateinit var rewardsViewModelNew: RewardsViewModelNew
    private lateinit var repository: RewardsRepository
    private lateinit var allCategoriesAdapter: AllCategoriesAdapter
    private lateinit var selectVoucherAdapter: SelectVoucherAdapter
    private var typeItem: ArrayList<TypesItem> = ArrayList()
    private var url = ""
    var prefs: SharePrefs? = null
    val refUrl = SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
        ?.getString(SharePrefsKeys.REF_SHORT_URL)

    private var selectedBrandLogo = ""
    var rewardsCoinBalance = 0
    private var bottomSheetDialog: BottomSheetDialog? = null
    private lateinit var voucherSuccessBottomSheetDialog: BottomSheetDialog
    private var lottie_animation: LottieAnimationView? = null
    private var animationDialog: Dialog? = null
    var animationStatus = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        catchIntent()
        setUpUi()
    }

    private fun catchIntent() {
        intent.getParcelableArrayListExtra<TypesItem>("TYPE_ITEM")?.let { typeItem.addAll(it) }
        categoryName = intent.getStringExtra("TITLE").toString()

    }


    private fun setUpUi() {
        Utils.setLightStatusBar(this)
        prefs = SharePrefs.getInstance(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_all_voucher)
        repository = RewardsRepository(this)
        rewardsViewModelNew = ViewModelProvider(
            this, RewardsViewModelFactory(repository)
        )[RewardsViewModelNew::class.java]

        val sortedList = typeItem.sortedBy {
            it.sequence
        }
        allCategoriesAdapter = AllCategoriesAdapter(
            this@ViewAllVoucherActivity, sortedList, this, categoryName = categoryName
        )
        if (SharePrefs.instance?.getInt(SharePrefsKeys.REWARDS_COIN_BALANCE) != null) {
            binding.tvCoinBalance.text =
                SharePrefs.instance?.getInt(SharePrefsKeys.REWARDS_COIN_BALANCE).toString()
        }

        binding.rvAllCategories.adapter = allCategoriesAdapter

        if (SharePrefs.instance?.getInt(SharePrefsKeys.REWARDS_COIN_BALANCE) != null) {
            rewardsCoinBalance = SharePrefs.instance?.getInt(SharePrefsKeys.REWARDS_COIN_BALANCE)?:0
        }

        for (item in sortedList.indices) {
            if (sortedList[item].categoryName.equals(categoryName, true)) {
                rewardsViewModelNew.getVoucherListByCatId(sortedList[item].id.toString())
                customDelay(item)
                break
            }
            if (categoryName.equals("VIEW_ALL", true)) {
                allCategoriesAdapter.setSelection(0)
                rewardsViewModelNew.getVoucherListByCatId("")
            }

        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        rewardsViewModelNew.voucherListLiveData.observe(this) {
            when (it) {
                is State.NetworkError -> {
                    Toast.makeText(this, it.netWorkMessage, Toast.LENGTH_SHORT).show()
                    binding.loading.visibility = View.GONE
                    binding.rvALlVouchers.visibility = View.GONE
                }
                is State.Loading -> {
                    binding.loading.visibility = View.VISIBLE
                    binding.rvALlVouchers.visibility = View.GONE
                }
                is State.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    binding.loading.visibility = View.GONE
                    binding.rvALlVouchers.visibility = View.GONE
                }
                is State.Success -> {
                    binding.loading.visibility = View.GONE
                    bindView(it)
                }

            }
        }

        rewardsViewModelNew.redeemVoucherLiveData.observe(this) {
            when (it) {
                is State.Error -> {
                    binding.loading.visibility = View.GONE
                    animationDialog?.dismiss()
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
                is State.Success -> {
                    //  Utils.hideProgressDialog()
                    binding.loading.visibility = View.GONE
                    doRedeemProcess(it)
                }
                is State.Loading -> {
                    //  Utils.showProgressDialog(this)
                    binding.loading.visibility = View.VISIBLE
                }
                is State.NetworkError -> {
                    Toast.makeText(this, it.netWorkMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                rewardsViewModelNew.appRatingState.collect { state ->
                    when (state) {
                        AppRatingVisibilityUIState.Loading,
                        AppRatingVisibilityUIState.RatingShown -> { }
                        is AppRatingVisibilityUIState.ShowRating -> {
                            startInAppRatingFlow(state.ratingPrompt)
                        }
                        AppRatingVisibilityUIState.ShowReferral -> openReferAndEarn()
                    }
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

                bottomSheetDialog?.dismiss()
                openVoucherSuccessfulRedeemBottomSheet(
                    it.data.data?.amount,
                    it.data.data?.id,
                    it.data.data?.voucherCode,
                    it.data.data?.voucherPin,
                    it.data.data?.pinStatus,
                    it.data.data?.voucherExp,
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

    private fun bindView(it: State.Success<VoucherListResponse>) {
        binding.rvALlVouchers.visibility = View.VISIBLE
        binding.rvALlVouchers.apply {
            adapter = AllVouchersAdapter(
                this@ViewAllVoucherActivity,
                it.data.vouchers,
                this@ViewAllVoucherActivity
            )
            hasFixedSize()
        }
    }

    private fun customDelay(item: Int) {
        Handler(Looper.getMainLooper()).postDelayed(
            {
                binding.rvAllCategories.smoothScrollToPosition(item + 1)
                allCategoriesAdapter.setSelection(item + 1)
            }, 250
        )
    }

    private fun openVoucherDetailsBottomSheet(voucherList: VouchersItem) {
        animationStatus = true
        bottomSheetDialog =
            Utils.showCustomDialogBottum2(this, R.layout.bottom_sheet_vouchers_details)
        val bindingSheet = DataBindingUtil.inflate<BottomSheetVouchersDetailsBinding>(
            layoutInflater, R.layout.bottom_sheet_vouchers_details, null, false
        )
        bottomSheetDialog?.setContentView(bindingSheet.root)

        bindingSheet.ivCancel.setOnClickListener {
            animationStatus = false
            bottomSheetDialog?.dismiss()
        }

        if (voucherList.availPoints != null) {
            bindingSheet.tvHowToAvail.visibility = View.VISIBLE
            val lstValues: List<String> = voucherList.availPoints.split("|").map { it.trim() }
            val availPointAdapter = AvailPointAdapter(this, lstValues)
            bindingSheet.rvAvailPoint.apply {
                adapter = availPointAdapter
                hasFixedSize()
            }
        } else {
            bindingSheet.tvHowToAvail.visibility = View.GONE
        }

        if (voucherList.exceptionNarration != null) {
            bindingSheet.tvException.text = "Please note: ${voucherList.exceptionNarration}"
            bindingSheet.tvException.visibility = View.VISIBLE
        } else {
            bindingSheet.tvException.visibility = View.GONE
        }
        if (voucherList.voucherValidTill != null) {
            bindingSheet.llValidTil.visibility = View.VISIBLE
            bindingSheet.tvVouchersValid.text =
                "All vouchers will be valid for at least ${voucherList.voucherValidTill} months"
        } else {
            bindingSheet.llValidTil.visibility = View.GONE
        }
        bindingSheet.tvCoinCount.text = "1 = ₹${voucherList.denominator ?: 1.00}"
        MparticleUtils.logCurrentScreen(
            "/rewards-tab/merchant-voucher-page",
            "The customer views the merchant voucher page of a specific merchant",
            "merchant-name",
            "${voucherList.name}",
            "",
            "",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.REWARD_TAB_MERCHANT),
            this
        )
        Glide.with(this).load("${SharePrefs.instance?.voucherBaseUrl}${voucherList.brandLogo}")
            .into(bindingSheet.ivBrandLogo)
        bindingSheet.ivShare.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT, "Hey Check out this Great app:"
            )
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }
        bottomSheetDialog?.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                val behaviour = BottomSheetBehavior.from(it)
                setupFullHeight(it)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                it.setBackgroundResource(R.drawable.round_bottom_sheet)
            }
        }
        selectVoucherAdapter = SelectVoucherAdapter(
            this@ViewAllVoucherActivity,
            voucherList.denoms as ArrayList<DenomsItem>,
            this@ViewAllVoucherActivity,
            voucherList.denominator?.toDouble() ?: 1.00
        )
        bindingSheet.rvSelectVoucher.adapter = selectVoucherAdapter
        llButton = (bottomSheetDialog?.findViewById(R.id.llButton) ?: return)
        btnRedeemNow = (bottomSheetDialog?.findViewById(R.id.btnRedeemNow)?: return)
        url = voucherList.tnc.toString()
        bindingSheet.tvTAndC.setOnClickListener {
            openTAndCBottomSheet(voucherList.tnc)
        }
        selectedBrandLogo = voucherList.brandLogo.toString()
        bottomSheetDialog?.setCancelable(false)
        bottomSheetDialog?.show()
    }

    override fun onVoucherClicked(isClicked: Boolean, voucherList: VouchersItem) {
        if (isClicked) {
            if ( bottomSheetDialog?.isShowing == true) {
                bottomSheetDialog?.dismiss()
            }
            openVoucherDetailsBottomSheet(voucherList)

        }
    }

    private fun openVoucherSuccessfulRedeemBottomSheet(
        amount: Int?,
        id: String?,
        voucherCode: String?,
        voucherPin: String?,
        pinStatus: String?,
        voucherExp: String?,
        selectedBrandLogo: String,
        exception: String?,
        avail: String?,
        goToWebsite: String?,
        partnerBrandName: Any?
    ) {
        voucherSuccessBottomSheetDialog =
            Utils.showCustomDialogBottum2(this, R.layout.bottom_sheet_voucher_reedem)
        val bindingSheet = DataBindingUtil.inflate<BottomSheetVoucherReedemBinding>(
            layoutInflater, R.layout.bottom_sheet_voucher_reedem, null, false
        )
        voucherSuccessBottomSheetDialog.setContentView(bindingSheet.root)
        if (voucherSuccessBottomSheetDialog.window != null) voucherSuccessBottomSheetDialog.window?.setDimAmount(
            0F
        )
        MparticleUtils.logCurrentScreen(
            "/rewards-tab/voucher-redeemed",
            "The customer views the post-redemption page for a voucher successfully redeemed",
            "${partnerBrandName}",
            "",
            "",
            "",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.REWARD_TAB_REEDEEMING_VOUCHER_SUCESS),
            this
        )

        Handler(Looper.getMainLooper()).postDelayed({
            bindingSheet.animationConefetti.visibility = View.VISIBLE
        }, 1000)
        voucherSuccessBottomSheetDialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                val behaviour = BottomSheetBehavior.from(it)
                setupFullHeightBottomSheet(it, this)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                it.setBackgroundResource(R.drawable.round_bottom_sheet)
            }
        }
        SharePrefs.instance?.putInt(
            SharePrefsKeys.REWARDS_COIN_BALANCE, rewardsCoinBalance - amount!!
        )
        if (SharePrefs.instance?.getInt(SharePrefsKeys.REWARDS_COIN_BALANCE) != null) {
            binding.tvCoinBalance.text =
                SharePrefs.instance?.getInt(SharePrefsKeys.REWARDS_COIN_BALANCE).toString()
        }
        Glide.with(this).load("${SharePrefs.instance?.voucherBaseUrl}${selectedBrandLogo}")
            .into(bindingSheet.ivBrandLogo)
        bindingSheet.tvAmount.text = "₹${amount.toString()}"
        bindingSheet.tvCouponCode.text = voucherCode
        bindingSheet.ivCopy.setOnClickListener {
            val clipboardManager1: ClipboardManager =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboardManager1.text = bindingSheet.tvCouponCode.text.toString()
            bindingSheet.llCopyToClipboard.visibility = View.VISIBLE
        }
        if (pinStatus == "1") {
            bindingSheet.tvPin.visibility = View.VISIBLE
            bindingSheet.tvPinView.visibility = View.VISIBLE
            bindingSheet.tvPinCode.text = voucherPin
        } else {
            bindingSheet.tvPin.visibility = View.GONE
            bindingSheet.tvPinView.visibility = View.GONE
        }
        val c = Calendar.getInstance().time
        val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = df.format(c)
        val remainingDays = DateTimeUtils.getDaysBetweenDates(
            formattedDate, voucherExp?.replaceRange(
                10, voucherExp.length, ""
            )
        ).toString().toInt()
        bindingSheet.tvExpires.text = "Expires in $remainingDays days"
        if (avail != null) {
            bindingSheet.tvHowToAvail.visibility = View.VISIBLE
            val lstValues: List<String> = avail.split("|").map { it.trim() }
            val availPointAdapter = AvailPointAdapter(this, lstValues)
            bindingSheet.rvAvailPoint.apply {
                adapter = availPointAdapter
                hasFixedSize()
            }
        } else {
            bindingSheet.tvHowToAvail.visibility = View.GONE
        }

        if (exception != null) {
            bindingSheet.tvException.text = "Please note: $exception"
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
                        this,
                        "No application can handle this request." + " Please install a webbrowser",
                        Toast.LENGTH_LONG
                    ).show()
                    e.printStackTrace()
                }
            }
        } else {
            bindingSheet.llGoToWebsite.visibility = View.GONE
        }
        bindingSheet.ivCopyPin.setOnClickListener {
            val clipboardManager1: ClipboardManager =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboardManager1.text = bindingSheet.tvPinCode.text.toString()
            bindingSheet.llCopyPinToClipboard.visibility = View.VISIBLE
        }

        voucherSuccessBottomSheetDialog.setCancelable(false)
        bindingSheet.ivCancel.setOnClickListener {
            voucherSuccessBottomSheetDialog.dismiss()
            onBackPressed()
        }
        bindingSheet.ivTAndC.setOnClickListener {
            openTAndCBottomSheet(url)
        }
        bindingSheet.btnDone.setOnClickListener {
            RealTimeDatabase.checkIsInAppReviewEnable(RealTimeDatabase.SHOW_APP_RATING_REWARDS) {
                if (it) {
                    rewardsViewModelNew.getRatingPrompt()
                } else {
                    openReferAndEarn()
                }
            }
        }
        voucherSuccessBottomSheetDialog.show()
    }

    private fun openTAndCBottomSheet(tandc: String?) {
        val bottomSheetDialog = Utils.showCustomDialogBottum2(this, R.layout.bottom_sheet_tand_c)
        val bindingSheet = DataBindingUtil.inflate<BottomSheetTandCBinding>(
            layoutInflater, R.layout.bottom_sheet_tand_c, null, false
        )
        bottomSheetDialog.setContentView(bindingSheet.root)
        bottomSheetDialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                val behaviour = BottomSheetBehavior.from(it)
                setupFullHeightBottomSheet(it, this)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                it.setBackgroundResource(R.drawable.round_bottom_sheet)
                behaviour.isDraggable = false
            }
        }
        bottomSheetDialog.setCancelable(false)
        bindingSheet.ivCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bindingSheet.webView.settings.javaScriptEnabled = true
        bindingSheet.webView.settings.useWideViewPort = true

        bindingSheet.webView.loadUrl(SharePrefs.instance?.voucherBaseUrl + tandc)
        bindingSheet.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                bindingSheet.webView.visibility = View.GONE
                if (progress == 100) {
                    bindingSheet.webView.visibility = View.VISIBLE
                }
            }
        }
        bottomSheetDialog.show()
    }


    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = (resources.displayMetrics.heightPixels * 0.90).roundToInt()
        bottomSheet.layoutParams = layoutParams
    }


    override fun onMyVoucherClicked(isClicked: Boolean, data: DenomsItem) {

        if (isClicked) {
            llButton.visibility = View.VISIBLE
            if (animationStatus) {
                val animationUp1 = AnimationUtils.loadAnimation(
                    MainApplication.getApplicationContext(), R.anim.slide_in_top
                )
                llButton.startAnimation(animationUp1)
                llButton.animation.setAnimationListener(object : AnimationListener {
                    override fun onAnimationStart(p0: Animation?) {
                    }

                    override fun onAnimationEnd(p0: Animation?) {
                        animationStatus = false
                    }

                    override fun onAnimationRepeat(p0: Animation?) {
                    }
                })

            }

            btnRedeemNow.setOnClickListener {

                MparticleUtils.logCurrentScreen(
                    "/rewards-tab/redeeming-voucher",
                    "The customer views the loading screen after clicking Redeem Now on a voucher",
                    "",
                    "",
                    "",
                    "",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.REWARD_TAB_REEDEEMING_VOUCHER),
                    this
                )
                animationDialog =
                    Dialog(this@ViewAllVoucherActivity, android.R.style.Theme_DeviceDefault)
                animationDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
                animationDialog?.setContentView(R.layout.dialog_full_view)
                lottie_animation = animationDialog?.findViewById(R.id.animationView)
                animationDialog?.show()
                rewardsViewModelNew.redeemCoupon(RedeemCouponRequest(data.id))
            }
        } else {

            llButton.visibility = View.GONE

        }
    }

    override fun onMyVoucherClicked(isClicked: Boolean) {

    }

    override fun onMyC2CClicked(isClicked: Boolean, amount: Float, convertToCashRate: Double) {

    }

    override fun onCategoriesClicked(catId: String, categoryPosition: Int) {
        if (categoryPosition == 0) {
            rewardsViewModelNew.getVoucherListByCatId("")
            allCategoriesAdapter.setSelection(categoryPosition)

        } else {
            rewardsViewModelNew.getVoucherListByCatId(catId)
            allCategoriesAdapter.setSelection(categoryPosition)

        }
    }

    override fun onCategoriesClicked(categoryPosition: Int) {
        selectVoucherAdapter.setSelection(categoryPosition)
    }

    fun openReferAndEarn() {
        MparticleUtils.logEvent(
            "/refer-and-earn/nudge",
            "The customer views the post-redemption page for a voucher successfully redeemed",
            "Unique",
            "Continue",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.REFER_AND_EARN_VIEWED),
            this
        )
        val bottomSheetDialog = Utils.showCustomDialogBottum2(this, R.layout.bottom_sheet_refer)
        val bindingSheet = DataBindingUtil.inflate<BottomSheetReferBinding>(
            layoutInflater, R.layout.bottom_sheet_refer, null, false
        )
        bindingSheet.ivRefer.loadSvg(prefs?.bannerBaseUrl + "Refer-and-Earn.svg")
        bottomSheetDialog.setCancelable(false)
        bindingSheet.btnNo.setOnClickListener {
            MparticleUtils.logEvent(
                "ReferAndEarnNudge_Secondary_Clicked",
                "User continue without invite your friend on the refer and earn page",
                "Unique",
                "Continue",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.REFER_AND_EARN_SECONDARY_CLICKED),
                this
            )
            bottomSheetDialog.dismiss()
            voucherSuccessBottomSheetDialog.dismiss()
            onBackPressed()
        }
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
                this
            )
            onWhatsAppclick()
        }
        bottomSheetDialog.setContentView(bindingSheet.root)
        bottomSheetDialog.show()
    }

    private fun startInAppRatingFlow(action: Action?) {
        val cheqUserId =
            SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                ?.getString(SharePrefsKeys.CHEQ_USER_ID) ?: "NA"
        val touchpoint = TOUCHPOINT_VOUCHER
        val inappFragment = InAppRatingFragment.newInstance(cheqUserId, touchpoint, action)
        inappFragment.show(supportFragmentManager, InAppRatingFragment.TAG)
        rewardsViewModelNew.setRatingStateShown()
    }

    fun onWhatsAppclick() {
        val beRefUrl = SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
            ?.getString(SharePrefsKeys.REF_BE_URL)
        AppsFlyEvents.logEventFly(
            this,
            AFConstants.AF_REFERRAL_NOW_CLICK,
            AFConstants.AF_ONBOARD_REFERRAL,
            AFConstants.AF_REFERRAL_CLICK
        )
        FirebaseLog.FireBaseLogEvent(
            this, AFConstants.FBEvent_REFERRAL_NOW_CLICK,
            AFConstants.FBEvent_ONBOARD_REFERRAL, AFConstants.FB_REFERRAL_CLICK
        )

        MparticleUtils.logEvent(
            "ReferAndEarn_InviteYourFriend_Clicked",
            "User clicks on invite your friend on the refer and earn page",
            "Unique",
            "Continue",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Home_ReferAndEarn_Banner_Clicked),
            this
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
            Toast.makeText(this, getString(R.string.install_the_app), Toast.LENGTH_SHORT).show()
        }

    }
}
