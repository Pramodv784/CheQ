package com.cheq.retail.ui.rewards.view


import android.content.ActivityNotFoundException
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.cheq.config.FeatureConfig
import com.cheq.navigation.IntentFactory
import com.cheq.navigation.IntentKey
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.databinding.ActivityRewardsCoinHistoryBinding
import com.cheq.retail.databinding.BottomSheetTandCBinding
import com.cheq.retail.databinding.BottomSheetVoucherReedemBinding
import com.cheq.retail.extensions.bannerBaseUrl
import com.cheq.retail.extensions.voucherBaseUrl
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.ui.main.SpacesItemDecoration
import com.cheq.retail.ui.main.model.VoucherRedeemHistoryResponse
import com.cheq.retail.ui.main.model.VouchersHistoryItem
import com.cheq.retail.ui.referral.ReferralActivity
import com.cheq.retail.ui.rewards.adapter.AvailPointAdapter
import com.cheq.retail.ui.rewards.adapter.MyVoucherAdapter
import com.cheq.retail.ui.rewards.repository.RewardsRepository
import com.cheq.retail.ui.rewards.util.State
import com.cheq.retail.ui.rewards.viewmodel.RewardsViewModelFactory
import com.cheq.retail.ui.rewards.viewmodel.RewardsViewModelNew
import com.cheq.retail.utils.DateTimeUtils
import com.cheq.retail.utils.ImageUtils.loadSvg
import com.cheq.retail.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class RewardsCoinHistoryActivity : BaseActivity(), MyVoucherAdapter.OnItemClick {
    lateinit var binding: ActivityRewardsCoinHistoryBinding
    private lateinit var rewardsViewModelNew: RewardsViewModelNew
    private lateinit var repository: RewardsRepository
    lateinit var prefs: SharePrefs

    @Inject
    lateinit var featureConfig: FeatureConfig

    @Inject
    lateinit var intentFactory: IntentFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRewardsCoinHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpUi()
    }


    private fun setUpUi() {
        prefs = SharePrefs.getInstance(this)!!
        Utils.setLightStatusBar(this)
        Utils.setColorStatusBar(this)
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        repository = RewardsRepository(this)
        rewardsViewModelNew = ViewModelProvider(
            this, RewardsViewModelFactory(repository)
        )[RewardsViewModelNew::class.java]

        rewardsViewModelNew.redeemHistoryLiveData.observe(this) {
            when (it) {
                is State.NetworkError -> {
                    Utils.hideProgressDialog()
                    Toast.makeText(this, it.netWorkMessage, Toast.LENGTH_SHORT).show()
                }
                is State.Loading -> {
                    Utils.showProgressDialog(this)
                }
                is State.Success -> {
                    Utils.hideProgressDialog()
                    bindView(it)
                }
                is State.Error -> {
                    Utils.hideProgressDialog()
                    if (it.message == getString(R.string.no_record_found)) {
                        binding.tvTotalVouchers.text = getString(R.string.__0)
                        binding.tvTotalVoucherValue.text = getString(R.string._0)
                    } else {
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        rewardsViewModelNew.getVoucherHistory()
        binding.ivRefer.loadSvg("${prefs.bannerBaseUrl}Refer-and-Earn.svg")
        binding.ivRefer.setOnClickListener {
            if (featureConfig.isNewProfileEnabled()){
                val intent = intentFactory.createIntent(
                    this,
                    IntentKey.MyAccountActivityKey(
                        cheqSafe = false,
                        startDestination = IntentKey.MyAccountActivityKey.REFER_EARN_DESTINATION,
                        goToHomeOnBack = false
                    )
                )
                startActivity(intent)
            } else {
                startActivity(Intent(this@RewardsCoinHistoryActivity, ReferralActivity::class.java))
            }
        }

    }

    private fun bindView(success: State.Success<VoucherRedeemHistoryResponse?>) {
        binding.rvMyVouchers.apply {
            addItemDecoration(SpacesItemDecoration(11))
            adapter = MyVoucherAdapter(
                success.data?.vouchers as ArrayList<VouchersHistoryItem>,
                context,
                this@RewardsCoinHistoryActivity
            )
            hasFixedSize()
        }
        if (success.data?.valueAndCount != null) {
            if (success.data.valueAndCount.totalValue != null) {
                binding.tvTotalVoucherValue.text = "₹${success.data.valueAndCount.totalValue}"
            } else {
                binding.tvTotalVoucherValue.text = getString(R.string._0)
            }

            if (success.data.valueAndCount.totalVouchers != null) {
                binding.tvTotalVouchers.text = "${success.data.valueAndCount.totalVouchers}"
            } else {
                binding.tvTotalVouchers.text = getString(R.string.__0)
            }
        }


    }


    private fun openVoucherSuccessfulRedeemBottomSheet(
        brandLogo: String,
        amount: Int?,
        id: String?,
        pincode: String?,
        voucherCode: String?,
        pinStatus: String?,
        voucherExp: String?,
        tAndC: String?,
        exception: String?,
        avail: String?,
        goToWebsite: String?
    ) {
        val bottomSheetDialog =
            Utils.showCustomDialogBottum2(this, R.layout.bottom_sheet_voucher_reedem)
        val bindingSheet = DataBindingUtil.inflate<BottomSheetVoucherReedemBinding>(
            layoutInflater, R.layout.bottom_sheet_voucher_reedem, null, false
        )
            bottomSheetDialog.window?.setDimAmount(0F)
        if (pinStatus == "1") { // show pin when pin status == 0 or 2
            bindingSheet.tvPin.visibility = View.VISIBLE
            bindingSheet.tvPinView.visibility = View.VISIBLE
            bindingSheet.tvPinCode.text = pincode
        } else { // else hide the pinview
            bindingSheet.tvPin.visibility = View.GONE
            bindingSheet.tvPinView.visibility = View.GONE
        }

        if (avail != null) {
            bindingSheet.tvHowToAvail.visibility = View.VISIBLE
            val lstValues: List<String> = avail.split("|")!!.map { it.trim() }
            val availPointAdapter = AvailPointAdapter(this, lstValues)
            bindingSheet.rvAvailPoint.apply {
                adapter = availPointAdapter
                hasFixedSize()
            }
        } else {
            bindingSheet.tvHowToAvail.visibility = View.GONE
        }

        if (exception != null) {
            bindingSheet.tvException.text = "Please note: ${exception}"
            bindingSheet.tvException.visibility = View.VISIBLE
        } else {
            bindingSheet.tvException.visibility = View.INVISIBLE
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



        Glide.with(this).load("${prefs.voucherBaseUrl}${brandLogo}").into(bindingSheet.ivBrandLogo)
        bottomSheetDialog.setContentView(bindingSheet.root)
        bottomSheetDialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                val behaviour = BottomSheetBehavior.from(it)
                Utils.setupFullHeightBottomSheet(it, this)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                it.setBackgroundResource(R.drawable.round_bottom_sheet)
            }
        }
        val c = Calendar.getInstance().time

        val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = df.format(c)
        val remainingDays = DateTimeUtils.getDaysBetweenDates(
            formattedDate, voucherExp?.replaceRange(
                10, voucherExp.length, ""
            )
        ).toString().toInt()

        bindingSheet.tvAmount.text = "₹${amount.toString()}"
        bindingSheet.tvCouponCode.text = voucherCode
        bindingSheet.tvExpires.text = "Expires in $remainingDays days"
        bindingSheet.ivCopy.setOnClickListener {
            val clipboardManager1: ClipboardManager =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboardManager1.text = bindingSheet.tvCouponCode.text.toString()
            bindingSheet.llCopyToClipboard.visibility = View.VISIBLE
        }

        bindingSheet.ivCopyPin.setOnClickListener {
            val clipboardManager1: ClipboardManager =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboardManager1.text = bindingSheet.tvPinCode.text.toString()
            bindingSheet.llCopyPinToClipboard.visibility = View.VISIBLE
        }
        bindingSheet.ivBack.visibility = View.VISIBLE
        bindingSheet.ivBack.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bindingSheet.llButton.visibility = View.GONE
        bottomSheetDialog.setCancelable(true)
        bindingSheet.ivTAndC.setOnClickListener {
            openTAndCBottomSheet(tAndC)
        }
        bottomSheetDialog.show()
    }


    private fun openTAndCBottomSheet(tAndC: String?) {
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
                setupFullHeight(it)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                it.setBackgroundResource(R.drawable.round_bottom_sheet)
            }
        }
        bottomSheetDialog.setCancelable(true)
        bindingSheet.ivCancel.setOnClickListener {

            bottomSheetDialog.dismiss()
        }
        bindingSheet.webView.settings.javaScriptEnabled = true
        bindingSheet.webView.settings.useWideViewPort = true

        bindingSheet.webView.loadUrl(tAndC.toString())
        bindingSheet.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                bindingSheet.webView.visibility = View.GONE
                //  bindingSheet.progressLayout.visibility = View.VISIBLE
                if (progress == 100) {
                    bindingSheet.webView.visibility = View.VISIBLE
                    // bindingSheet.progressLayout.visibility = View.GONE
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

    override fun onMyVoucherClicked(isClicked: Boolean, data: VouchersHistoryItem?) {
        data?.cheqBrands?.brandLogo?.let {
            openVoucherSuccessfulRedeemBottomSheet(

                it,
                data.amount,
                data.id,
                data.voucherPin,
                data.voucherCode,
                data.pinStatus,
                data.voucherExp,
                prefs.voucherBaseUrl + data.cheqBrands.tnc,
                data.cheqBrands.exceptionNarration,
                data.cheqBrands.availPoints,
                data.cheqBrands.websiteUrl
            )
        }
    }


}