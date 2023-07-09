package com.cheq.retail.ui.login

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.appsflyer.share.LinkGenerator
import com.appsflyer.share.ShareInviteHelper
import com.cheq.retail.R
import com.cheq.retail.api.errormodel.BlockData
import com.cheq.retail.application.MainApplication
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.constants.AFConstants
import com.cheq.retail.databinding.ActivityLoadingStateBinding
import com.cheq.retail.extensions.customToast
import com.cheq.retail.sharePreferences.SharePrefCheqUserId
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.blocker_screen.BlockerActivity
import com.cheq.retail.ui.landing.view.LandingActivity
import com.cheq.retail.ui.login.model.UpdateProfilePost
import com.cheq.retail.ui.login.model.UpdateProfileResponse
import com.cheq.retail.ui.login.viewModel.GetPanCardViewModel
import com.cheq.retail.ui.login.viewModel.UpdateProfileViewModel
import com.cheq.retail.ui.main.model.SendReferralRequest
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.ReferralHelper.getInviteHelper
import com.cheq.retail.utils.Utils
import kotlinx.coroutines.launch


class LoadingStateActivity : BaseActivity() {
    lateinit var mBinding: ActivityLoadingStateBinding
    private lateinit var viewModel: UpdateProfileViewModel
    private lateinit var getPanCardViewModel: GetPanCardViewModel
    private lateinit var postData: UpdateProfilePost
    private var creditRes: UpdateProfileResponse.CreditResponse? = null
    private var isDataLoaded = false
    private var isDataPanLoaded = false
    private var isCreditBureauSuccessFull = false
    private var isCreditBureauPanSuccessFull = false
    var pan = ""
    var dob = ""
    var comingFrom = ""
    private var rewardsCanAssign = 0
    private var rewardsAssigned = 0
    private var rewardsAssignUpto = 0

    companion object {
        val TAG = LoadingStateActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        catchIntent()
        initView()
        setupViewModel()
        setupObserver()



        if (comingFrom == "PAN_CARD_ACTIVITY") {

            getPanCardViewModel.updateProfile(dob, pan)
            MparticleUtils.logCurrentScreen(
                "/onboarding/loading",
                "A loading screen is displayed as CheQ fetches the bureau data, scrapes SMS, and performs pay later pulls using customer's data",
                "type",
                "second-call",
                "",
                "",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_LOADING),
                this
            )
        } else {
            val cheqUserId =
                SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                    ?.getString(SharePrefsKeys.CHEQ_USER_ID)
            if (cheqUserId != null) {
                viewModel.updateProfile(postData, cheqUserId)

            }
            MparticleUtils.logCurrentScreen(
                "/onboarding/loading",
                "A loading screen is displayed as CheQ fetches the bureau data, scrapes SMS, and performs pay later pulls using customer's data",
                "type",
                "first-call",
                "",
                "",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_LOADING),
                this
            )
        }
    }

    private fun catchIntent() {

        pan = intent.getStringExtra("PAN").toString()
        dob = intent.getStringExtra("DOB").toString()
        comingFrom = intent.getStringExtra("COMING_FROM").toString()

        if (comingFrom != "PAN_CARD_ACTIVITY") {
            postData = intent.getSerializableExtra("DATA") as UpdateProfilePost
        }
    }

    private fun setupObserver() {
        viewModel?.errorBlockObserver?.observe(this) { blockdata ->
            showErrorBlocker(blockdata)
        }
        getPanCardViewModel?.errorBlockObserver?.observe(this) { blockdata ->
            showErrorBlocker(blockdata)
        }

        viewModel.responseObserver.observe(this) {
            if (it.creditResponse.products?.isNotEmpty() == true || it.creditResponse.loans?.isNotEmpty() == true) {
                isCreditBureauSuccessFull = true
            }
            try {
                rewardsAssignUpto = it.rewardLimitManager.rewardsAssignUpto ?: 0
                rewardsAssigned = it.rewardLimitManager.rewardsAssigned ?: 0
                rewardsCanAssign = it.rewardLimitManager.rewardsCanAssign ?: 0
            } catch (e: Exception) {
                Log.e(TAG, "setupObserver: ${e.localizedMessage}")
            }
            creditRes = it.creditResponse
            isDataLoaded = true
            SharePrefs.getInstance(this)?.putString(SharePrefsKeys.DOB, creditRes?.Date_Of_Birth_Applicant)
            SharePrefs.getInstance(this)?.putString(SharePrefsKeys.PAN_CARD, creditRes?.pan)
            loadingAnimation()
        }

        viewModel.statusObserver.observe(this) {
            isDataLoaded = true
            loadingAnimation()
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }




        getPanCardViewModel.creditResponse.observe(this) {
            if (it.creditResponse.products?.isNotEmpty() == true || it.creditResponse.loans?.isNotEmpty() == true) {
                isCreditBureauPanSuccessFull = true
            }
            creditRes = it.creditResponse
            try {
                rewardsAssignUpto = it.rewardLimitManager.rewardsAssignUpto ?: 0
                rewardsAssigned = it.rewardLimitManager.rewardsAssigned ?: 0
                rewardsCanAssign = it.rewardLimitManager.rewardsCanAssign ?: 0
            } catch (e: Exception) {
                Log.e(TAG, "setupObserver: ${e.localizedMessage}")
            }
            isCreditBureauPanSuccessFull = true
            isDataPanLoaded = true
            SharePrefs.getInstance(this)?.putString(SharePrefsKeys.DOB, dob)
            SharePrefs.getInstance(this)?.putString(SharePrefsKeys.PAN_CARD, pan)

            loadingAnimation()
        }

        /*getPanCardViewModel.userWaitListObserver.observe(this, Observer {
            startActivity(WaitListActivity.startActivity(applicationContext,it))
        })*/

        getPanCardViewModel.statusObserver.observe(this) {
            //   Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            SharePrefs.getInstance(this)?.putString(SharePrefsKeys.DOB, dob)
            SharePrefs.getInstance(this)?.putString(SharePrefsKeys.PAN_CARD, pan)
            loadingAnimation()
        }

        getPanCardViewModel.failureObserver.observe(this) {
             customToast(R.drawable.error_warning, it)

            finish()
        }


    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[UpdateProfileViewModel::class.java]
        getPanCardViewModel = ViewModelProvider(this)[GetPanCardViewModel::class.java]
    }

    private fun initView() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_loading_state)
        Utils.setLightStatusBar(this)
        /* mBinding.animationView.addAnimatorListener(object : Animator.AnimatorListener {
             override fun onAnimationRepeat(animation: Animator) {
             }

             override fun onAnimationEnd(animation: Animator) {
                 if (comingFrom == "PAN_CARD_ACTIVITY") {
                     if (isDataPanLoaded) {
                         if (isCreditBureauPanSuccessFull) {
                             openAnotherAnimation()
                         } else {
                             startActivity(
                                 Intent(
                                     this@LoadingStateActivity, MainActivity::class.java
                                 )
                             )
                             finishAffinity()
                         }
                     } else {
                         Toast.makeText(
                             this@LoadingStateActivity, "Something went wrong", Toast.LENGTH_SHORT
                         ).show()
                     }
                 } else {
                     if (isDataLoaded) {
                         if (isCreditBureauSuccessFull) {
                             openAnotherAnimation()
                         } else {
                             startActivity(
                                 Intent(
                                     this@LoadingStateActivity, GetPanCardActivity::class.java
                                 )
                             )
                             finishAffinity()
                         }
                     } else {
                         Toast.makeText(
                             this@LoadingStateActivity, "Something went wrong", Toast.LENGTH_SHORT
                         ).show()
                     }
                 }




             }

             override fun onAnimationCancel(animation: Animator) {
             }

             override fun onAnimationStart(animation: Animator) {
                 mBinding.tvDesc.visibility = View.VISIBLE
                 mBinding.tvTitle.visibility = View.VISIBLE
             }

         })*/
    }

    private fun loadingAnimation() {
        mBinding.animationView.repeatCount = 0
        mBinding.animationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {

                Handler(mainLooper)
                    .postDelayed(
                        {
                            if (comingFrom == "PAN_CARD_ACTIVITY") {

                                redirectToLandingActivity(creditRes)
                            } else {
                                if (isDataLoaded) {

                                    if (isCreditBureauSuccessFull) {
//                            openAnotherAnimation()
                                        redirectToLandingActivity(creditRes)

                                    } else {
                                        startActivity(
                                            Intent(
                                                this@LoadingStateActivity,
                                                GetPanCardActivity::class.java
                                            )
                                        )
                                        finishAffinity()
                                    }
                                } else {
                                    startActivity(
                                        Intent(
                                            this@LoadingStateActivity,
                                            GetPanCardActivity::class.java
                                        )
                                    )
                                    finishAffinity()
                                    /*Toast.makeText(
                                        this@LoadingStateActivity, "Something went wrong", Toast.LENGTH_SHORT
                                    ).show()*/
                                }
                            }
                        },
                        5000
                    )
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationStart(animation: Animator) {
                mBinding.tvDesc.visibility = View.VISIBLE
                mBinding.tvTitle.visibility = View.VISIBLE
            }

        })
    }

    private fun openAnotherAnimation() {
        mBinding.llTickAnimation.visibility = View.VISIBLE
        mBinding.tvDesc.visibility = View.GONE
        mBinding.tvTitle.visibility = View.GONE
        mBinding.animationView.visibility = View.GONE
        mBinding.animationViewTick.setMaxFrame(60)
        mBinding.animationViewTick.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                try {
                    redirectToLandingActivity(creditRes)
                } catch (e: Exception) {
                    redirectToLandingActivity(creditRes)
                }
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationStart(animation: Animator) {
                mBinding.tvDesc.visibility = View.GONE
                mBinding.tvTitle.visibility = View.GONE
            }

        })
    }

    private fun redirectToLandingActivity(creditRes: UpdateProfileResponse.CreditResponse?) {
        startActivity(
            LandingActivity.startActivity(
                this@LoadingStateActivity,
                creditRes,
                rewardsAssignUpto,
                rewardsCanAssign,
                rewardsAssigned
            )

        )
        finishAffinity()
    }

    fun generateRefLink(
        onLinkGenerated: () -> Unit
    ) {
        val cheqUserId = SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
            ?.getString(SharePrefsKeys.CHEQ_USER_ID) ?: return
        val linkGenerator = ShareInviteHelper.generateInviteUrl(applicationContext)
        linkGenerator.getInviteHelper(cheqUserId, this)

        val listener2: LinkGenerator.AFa1xSDK = object : LinkGenerator.AFa1xSDK {
            override fun onResponse(s: String) {
                sendReferral(cheqUserId, s, onLinkSynced = onLinkGenerated)
            }

            override fun onResponseError(s: String) {
            }
        }

        linkGenerator.generateLink(applicationContext, listener2)
    }

    fun sendReferral(
        cheqUserId: String,
        linkGenerated: String,
        onLinkSynced: () -> Unit
    ) {
        val sendReferralRequest = SendReferralRequest(cheqUserId, linkGenerated)
        val repository = (application as MainApplication).referralRepository

        lifecycleScope.launch {
            repository.sendReferralUrl(sendReferralRequest)
            onLinkSynced()
        }
    }

    private fun showErrorBlocker(blockedData: BlockData?) {
        val intent = Intent(this, BlockerActivity::class.java).apply {
            putExtra(BlockerActivity.EXTRA_BLOCKER, blockedData)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(AFConstants.KEY_SCREEN_NAME, AFConstants.SCREEN_ONBOARDING_BLOCKED)
        }
        startActivity(intent)
    }
}