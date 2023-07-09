package com.cheq.retail.ui.landing.view

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.constants.AFConstants.AF_CUST_ONBOARD_SUCESS
import com.cheq.retail.constants.AFConstants.AF_ONBORAD_SUCESS
import com.cheq.retail.constants.AFConstants.AF_ONBORAD_SUCESS_WITH_PRODUCT
import com.cheq.retail.constants.AFConstants.FBEvent_ONBORAD_SUCESS
import com.cheq.retail.constants.AFConstants.FBEvent_ONBORAD_SUCESS_WITH_PRODUCT
import com.cheq.retail.constants.AFConstants.FB_CUST_ONBOARD_SUCESS
import com.cheq.retail.constants.AppsFlyEvents
import com.cheq.retail.constants.FirebaseLog.FireBaseLogEvent
import com.cheq.retail.databinding.ActivityLandingBinding
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.landing.adapter.LandingAdapter
import com.cheq.retail.ui.landing.model.LandingProduct
import com.cheq.retail.ui.landing.model.asViewObject
import com.cheq.retail.ui.login.model.UpdateProfileResponse
import com.cheq.retail.ui.main.MainActivity
import com.cheq.retail.ui.main.ProductBottomSheetDialog
import com.cheq.retail.utils.IntentKeys
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.Resource

class LandingActivity : BaseActivity() {
    private var rewardsCanAssign = 0
    private var rewardsAssigned = 0
    private var rewardsAssignUpto = 0
    companion object {
        val TAG = LandingActivity::class.java.simpleName
        val CREDIT_RESPONSE = "$TAG.CREDIT_RESPONSE"
        fun startActivity(
            context: Context,
            creditResponse: UpdateProfileResponse.CreditResponse?,
            rewardsAssignUpto : Int,
            rewardsCanAssign : Int,
            rewardsAssigned : Int
        ): Intent {
            return Intent(context, LandingActivity::class.java).apply {
                putExtra(CREDIT_RESPONSE, creditResponse)
                    .putExtra(IntentKeys.REWARDS_ASSIGN_UPTO, rewardsAssignUpto)
                    .putExtra(IntentKeys.REWARDS_CAN_ASSIGN, rewardsCanAssign)
                    .putExtra(IntentKeys.REWARDS_ASSIGNED, rewardsAssigned)
            }
        }
    }

    lateinit var binding: ActivityLandingBinding

    private val viewModel by viewModels<LandingActivityViewModel>()

    lateinit var adapter: LandingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatuBar()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_landing)

        initViewModel()
        setUpView()
        setUpObserver()
        setUpAction()
        observeEvents()
    }

    private fun setStatuBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = window.decorView.systemUiVisibility // get current flag
            flags =
                flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // add LIGHT_STATUS_BAR to flag
            window.decorView.systemUiVisibility = flags
            window.statusBarColor = ContextCompat.getColor(this,R.color.ref_earned_bg) // optional
        }
    }

    private fun initViewModel() {
        val creditResponse = intent.getSerializableExtra(CREDIT_RESPONSE) as? UpdateProfileResponse.CreditResponse
        viewModel.loadData(creditResponse, SharePrefs.getInstance(this)?.getString(SharePrefsKeys.FIRST_NAME) ?: "Buddy")
        rewardsAssignUpto = intent.getIntExtra(IntentKeys.REWARDS_ASSIGN_UPTO, 0)
        rewardsCanAssign = intent.getIntExtra(IntentKeys.REWARDS_CAN_ASSIGN, 0)
        rewardsAssigned = intent.getIntExtra(IntentKeys.REWARDS_ASSIGNED, 0)

    }

    private fun logEventTrack() {
        var cardCount = 0
        var loanCount = 0
        when (val value = viewModel.view.value) {
            is Resource.Error -> {}
            is Resource.Initial -> {}
            is Resource.Loaded -> {
                val allProducts = value.value.products.map { it.products }.flatten()
                cardCount = allProducts.count { it is LandingProduct.Card }
                loanCount = allProducts.count { it is LandingProduct.Loan }


            }
            is Resource.Pending -> {}
            null -> {}
        }

        if(cardCount > 0 || loanCount > 0){
            AppsFlyEvents.logEventFly(
                this,
                AF_ONBORAD_SUCESS,
                AF_ONBORAD_SUCESS_WITH_PRODUCT,
                AF_CUST_ONBOARD_SUCESS
            )
            FireBaseLogEvent(this,  FBEvent_ONBORAD_SUCESS,
               FBEvent_ONBORAD_SUCESS_WITH_PRODUCT,FB_CUST_ONBOARD_SUCESS)


            MparticleUtils.logCurrentScreen(
                "/onboarding/success-with-products-found",
                "The customer is displayed an onboarding-success screen, with a snapshot of the products identified",
                "total-products-found",
                "${cardCount + loanCount}",
                "",
                "",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_SUCESS),
                this
            )

        }


        if (cardCount > 0 && loanCount > 0) {


            MparticleUtils.logCurrentScreen(
                "/onboarding/success-with-products-found",
                "The customer is displayed an onboarding-success screen, with a snapshot of the products identified",
                "types-of-credit",
                "cc-and-loans",
                "",
                "",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_SUCESS),
                this
            )



        } else if (cardCount > 0) {
            MparticleUtils.logCurrentScreen(
                "/onboarding/success-with-products-found",
                "The customer is displayed an onboarding-success screen, with a snapshot of the products identified",
                "types-of-credit",
                "only-cc",
                "",
                "",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_SUCESS),
                this
            )
        } else if (loanCount > 0) {
            MparticleUtils.logCurrentScreen(
                "/onboarding/success-with-products-found",
                "The customer is displayed an onboarding-success screen, with a snapshot of the products identified",
                "types-of-credit",
                "only-loans",
                "",
                "",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_SUCESS),
                this
            )
        }/* else {
            MparticleUtils.logCurrentScreen(
                "/onboarding/success-with-products-found",
                "The customer is displayed an onboarding-success screen, with a snapshot of the products identified",
                "types-of-credit",
                "none-found",
                "",
                "",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_SUCESS),
                this
            )
        }*/




    }

    private fun setUpView() {
        adapter = LandingAdapter()
        binding.cardRV.layoutManager = LinearLayoutManager(applicationContext)
        binding.cardRV.adapter = adapter

        binding.animationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                binding.animationView.isVisible = false
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationStart(animation: Animator) {

            }

        })
    }

    private fun setUpObserver() {

        viewModel.view.observe(this) {

            when (it) {
                is Resource.Error -> {}
                is Resource.Initial -> {}
                is Resource.Pending -> {}
                is Resource.Loaded -> {
                    logEventTrack()

                    // name
                    // binding.userNameTV.text = it.value.name

                    // list
                    val vo = it.value.asViewObject

                    adapter.submitList(vo.items)

                     if(it.value.productCount ==0){
                         if (it.value.creditScore != null) {
                             MparticleUtils.logCurrentScreen(
                                 "/onboarding/success-with-no-products-found",
                                 "The customer is displayed an onboarding-success screen, with a snapshot of the products identified",
                                 "credit-score-status",
                                 "found",
                                 "credit-score",
                                 "${it.value.creditScore}",
                                 SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_SUCESS),
                                 this
                             )


                         } else {
                             MparticleUtils.logCurrentScreen(
                                 "/onboarding/success-with-no-products-found",
                                 "The customer is displayed an onboarding-success screen, with a snapshot of the products identified",
                                 "credit-score-status",
                                 "not-found",
                                 "",
                                 "",
                                 SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_SUCESS),
                                 this
                             )
                         }
                     }


                    val isDecoratedFooterVisible = vo.isDecoratedFooterVisible

                    binding.decoratedFooterContainer.isVisible = isDecoratedFooterVisible
                    binding.exploreTV.isVisible = !isDecoratedFooterVisible
                }

            }

        }

    }

    private fun setUpAction() {

        adapter.actionListener = object : LandingAdapter.ActionListener {
            override fun addProduct() {
                MparticleUtils.logEvent(
                    "Onboarding_AddProduct_Clicked",
                    "User clicks on Add Product, and proceeds to add a credit card or loan or CheQ",
                    "Unique",
                    "Continue",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Onboarding_AddProduct_Clicked),
                    this@LandingActivity
                )
                showAddBottomSheet()
            }

        }

        binding.exploreTV.setOnClickListener {
            viewModel.doExploreCheq()
        }

        binding
            .decoratedFooterContainer
            .findViewById<Button>(R.id.exploreBtn)
            .setOnClickListener {
                MparticleUtils.logEvent(
                    "Onboarding_ExploreCheQ_Clicked",
                    "User clicks on primary CTA to proceed with making a payment for one of the identified products",
                    "Unique",
                    "Continue",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Onboarding_ExploreCheQ_Clicked),
                    this@LandingActivity
                )
                viewModel.doExploreCheq()
            }
    }

    private fun showAddBottomSheet() {
        try {
            ProductBottomSheetDialog.newInstance(this, rewardsCanAssign, rewardsAssigned, rewardsAssignUpto ).show(supportFragmentManager, "")
        } catch (e: Exception) {
        }
    }


    private fun observeEvents() {
        lifecycleScope.launchWhenStarted {
            viewModel.event.collect {

                when (it) {
                    LandingEvent.DoExploreCheq -> {
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }
}