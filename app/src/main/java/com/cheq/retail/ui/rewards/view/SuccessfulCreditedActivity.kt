package com.cheq.retail.ui.rewards.view

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.cheq.config.FeatureConfig
import com.cheq.navigation.IntentFactory
import com.cheq.navigation.IntentKey
import com.cheq.retail.R
import com.cheq.retail.api.RestClient
import com.cheq.retail.application.MainApplication
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.databinding.ActivitySuccessfulCreditedBinding
import com.cheq.retail.extensions.bannerBaseUrl
import com.cheq.retail.inappratings.ui.InAppRatingFragment
import com.cheq.retail.inappratings.ui.TOUCHPOINT_C2C
import com.cheq.retail.sharePreferences.SharePrefCheqUserId
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.cheqsafe.CheqSafeParentFragment
import com.cheq.retail.ui.main.MainActivity
import com.cheq.retail.ui.referral.ReferralActivity
import com.cheq.retail.ui.socialLogin.model.CheqSafeScreens
import com.cheq.retail.ui.socialLogin.model.CheqSafeStatus
import com.cheq.retail.utils.CheqSafeRealtimeDatabase
import com.cheq.retail.utils.ImageUtils.loadSvg
import com.cheq.retail.utils.InAppReview
import com.cheq.retail.utils.IntentKeys.AMOUNT
import com.cheq.retail.utils.IntentKeys.CAPTURED
import com.cheq.retail.utils.IntentKeys.COIN_WORTH
import com.cheq.retail.utils.IntentKeys.EARN_UP_TO
import com.cheq.retail.utils.IntentKeys.PROCESSING
import com.cheq.retail.utils.IntentKeys.SUCCESS
import com.cheq.retail.utils.IntentKeys.TXN_STATUS
import com.cheq.retail.utils.IntentKeys.UPI_ID
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.RealTimeDatabase.SHOW_APP_RATING_C2C
import com.cheq.retail.utils.RealTimeDatabase.checkIsInAppReviewEnable
import com.cheq.retail.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SuccessfulCreditedActivity : BaseActivity() {
     var _binding: ActivitySuccessfulCreditedBinding?=null
    val binding get() = _binding!!
    var amount = ""
    var upiId = ""
    var chipUsed = 0
    var earnUpTo = 0
    var txnStatus = ""

    @Inject
    lateinit var featureConfig: FeatureConfig

    @Inject
    lateinit var intentFactory: IntentFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySuccessfulCreditedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        catchIntent()
        setUpUi()
        startInAppRating()
    }

    private fun startInAppRating() {
        checkIsInAppReviewEnable(
            SHOW_APP_RATING_C2C
        ) {
            if (it) {
                InAppReview.requestReview(this)
            }
        }
    }

    private fun catchIntent() {
        amount = intent.getStringExtra(AMOUNT).toString()
        chipUsed = intent.getIntExtra(COIN_WORTH, 0)
        upiId = intent.getStringExtra(UPI_ID).toString()
        earnUpTo = intent.getIntExtra(EARN_UP_TO, 0)
        txnStatus = intent.getStringExtra(TXN_STATUS).toString()

        when (txnStatus) {
            PROCESSING -> {
                binding.apply {
                    tvTitle.text =
                        getString(R.string.hang_in_will_be_credited,amount)
                    ivStatus.setImageResource(R.drawable.ic_processing_status)
                }
            }
            SUCCESS, CAPTURED ->{
                binding.apply {
                    binding.tvTitle.text =
                        getString(R.string.congrates_was_successfully,amount)
                    ivStatus.setImageResource(R.drawable.ic_check_right)
                }
            }
        }
    }

    private fun setUpUi() {
        val prefs = SharePrefs.getInstance(this)!!
        Utils.setLightStatusBar(this)
        setStatusBar()
        binding.btnOk.setOnClickListener {
            startInAppRatingFlow()
        }

        binding.tvAmount.text = "₹$amount"
        binding.tvUpiId.text = upiId
        binding.tvChipsUsed.text = "$chipUsed"

        if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
            lifecycleScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                t.printStackTrace()
            }) {
                val response = RestClient.getInstance().service.getUserProfile()

                if (response?.body() != null) {
                    when (response.body()?.cheqSafeStatus) {
                        CheqSafeStatus.NO_EMAIL_LINKED -> {
                            checkOnFirebase(CheqSafeStatus.NO_EMAIL_LINKED.name)
                        }
                        CheqSafeStatus.LINKED -> {
                            binding.cheqSafeLayout.clMainLayout.isVisible = false
                            binding.tvActionRecommended.isVisible = false
                        }
                        CheqSafeStatus.FAILED -> {
                            checkOnFirebase(CheqSafeStatus.FAILED.name)
                        }
                        else -> {}
                    }
                }

            }
        } else {
            Toast.makeText(applicationContext, "Please connect to the internet!", Toast.LENGTH_LONG)
                .show()
        }

        binding.cheqSafeLayout.ivOfferImage.setOnClickListener {
            startStartCheqSafeFlow()
        }
        /*CheqSafeRealtimeDatabase.checkSafeFromFb { it ->
            if (it != null) {
                binding.tvActionRecommended.visibility = View.VISIBLE
                binding.cheqSafeLayout.demoView.visibility = View.VISIBLE
                binding.tvActionRecommended.text = it.title
                val layoutParams =
                    binding.cheqSafeLayout.demoView.layoutParams as ConstraintLayout.LayoutParams
                layoutParams.dimensionRatio = it.ratio
                binding.cheqSafeLayout.demoView.layoutParams = layoutParams
                if (it.banner_type == 1) {
                    binding.cheqSafeLayout.ivOfferImage.let { it1 ->
                        Glide.with(this).load(it.banner_url).into(
                            it1
                        )
                    }
                }else {
                    binding.cheqSafeLayout.animationView.setFailureListener {

                    }
                    binding.cheqSafeLayout.animationView.setAnimationFromUrl(it.banner_url)
                }
             /*   binding.cheqSafeLayout.demoView.setOnClickListener { view ->
                    when (it.banner_redirection_type) {
                        HomeFragment.POP_UP -> {
                            //openOfferPopUp(it)
                        }
                        HomeFragment.DEEP_LINK -> {
                            it.banner_redirection?.let {
                                DeepLinkHandler.getDeeplinkIntent(this, it)
                            }?.let {
                                startActivity(it)
                            }
                        }
                        HomeFragment.WEB_LINK -> {
                            startActivity(
                                Intent(this, CommonWebViewActivity::class.java).putExtra(
                                    Constant.URL, "${it.banner_redirection}"
                                )
                            )
                        }
                    }

                }*/
            } else {
                binding.tvActionRecommended.visibility = View.GONE
                binding.cheqSafeLayout.demoView.visibility = View.GONE
            }
        }*/ //CHEQSAFE CHANGE
        binding.ivRefer.loadSvg(prefs.bannerBaseUrl + "Refer-and-Earn.svg", loadingView = binding.convertToCashRefer.ivHomePlaceholderTopBanner)
        binding.ivVoucher.loadSvg(prefs.bannerBaseUrl + "Rewards-Tab.svg",loadingView = binding.convertToCashVoucher.ivHomePlaceholderTopBanner)


        var total = earnUpTo.minus(amount.toInt())
        if (total != 0) {
            binding.tvRewardAmount.text = "₹$total"
        } else {
            binding.tvRewardAmount.text = "₹0"
        }

        binding.ivRefer.setOnClickListener {
            if (featureConfig.isNewProfileEnabled()){
                val intent = intentFactory.createIntent(
                    this,
                    IntentKey.MyAccountActivityKey(
                        cheqSafe = false,
                        startDestination = IntentKey.MyAccountActivityKey.REFER_EARN_DESTINATION
                    )
                )
                startActivity(intent)
            } else {
                startActivity(Intent(this@SuccessfulCreditedActivity, ReferralActivity::class.java))
            }
            finish()
        }
        binding.ivVoucher.setOnClickListener {
            startActivity(Intent(this@SuccessfulCreditedActivity, MainActivity::class.java).apply {
                putExtra("COMING_FROM_BILL", "COMING_FROM_BILL")
            })

            finish()
        }


        MparticleUtils.logCurrentScreen(
            "/rewards-tab/convert-to-cash/success",
            "The customer views the sucess screen after converting amount to credit",
            "",
            "",
            "",
            "",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.REWARD_TAB_CONVERT_TO_CASH_SUCESS),
            this
        )
    }

    private fun setStatusBar() {

    }

    fun checkOnFirebase(status: String) {
 CheqSafeRealtimeDatabase.checkSafeFromFb(from = CheqSafeScreens.CONVERT_TO_CASE, onValueFetched = {
                if (it != null) {
                    binding.cheqSafeLayout.clMainLayout.isVisible = it.isVisible
                    binding.tvActionRecommended.isVisible = it.isVisible
                    val bannerUrl =   if (status == CheqSafeStatus.FAILED.name) it.failedBannerUrl else it.successBannerUrl
                    Glide.with(applicationContext).
                    load(bannerUrl).
                    addListener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                binding.cheqSafeLayout.clMainLayout.isVisible = false
                                binding.tvActionRecommended.isVisible = false
                                binding.cheqSafeLayout.homeSmallBanner.root.isVisible = false
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                binding?.cheqSafeLayout?.homeSmallBanner?.root?.isVisible = false
                                return false
                            }

                        }).
                    into(
                        binding.cheqSafeLayout.ivOfferImage
                    )


                }
            })
    }

    private fun startStartCheqSafeFlow() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.cheqSafeContainer, CheqSafeParentFragment())
        fragmentTransaction.commit()
    }
    private fun startInAppRatingFlow() {
        checkIsInAppReviewEnable(SHOW_APP_RATING_C2C) {
            if (it) {
                val cheqUserId = SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                    ?.getString(SharePrefsKeys.CHEQ_USER_ID) ?: "NA"
                val touchpoint = TOUCHPOINT_C2C
                val inappFragment = InAppRatingFragment.newInstance(cheqUserId, touchpoint)
                inappFragment.show(supportFragmentManager, InAppRatingFragment.TAG)
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}