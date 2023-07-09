package com.cheq.retail.ui.onboarding


import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.Window
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.databinding.ActivityOnBoardingBinding
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.login.ExistingUserActivity
import com.cheq.retail.ui.login.LoginActivity
import com.cheq.retail.ui.onboarding.adapter.OnBoardingAdapter
import com.cheq.retail.ui.onboarding.model.OnBoardingItemModel
import com.cheq.retail.ui.onboarding.viewmodel.OnBoardingViewModel
import com.cheq.retail.ui.permission.PermissionActivity
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.PermissionUtils
import com.cheq.retail.utils.Utils


class OnBoardingActivity : BaseActivity() {
    private var isClicked = false
    private lateinit var mBinding: ActivityOnBoardingBinding
    private lateinit var onBoardingViewModel: OnBoardingViewModel
    private lateinit var onOnBoardingAdapter: OnBoardingAdapter
    private var itemListNew: ArrayList<OnBoardingItemModel>? = null
    private var screenCount = 0
    var screenChangeCountMax = 1
    var screenChangeLow = 1
    private var isStoryOneFinished = false
    private var isStoryTwoFinished = false
    private var isStoryThreeFinished = false
    var isFirst = false
    private lateinit var progressAnimatorOne: ObjectAnimator
    private lateinit var progressAnimatorTwo: ObjectAnimator
    private lateinit var progressAnimatorThree: ObjectAnimator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_on_boarding)

        setUpUi()
    }

    private fun setUpUi() {
        Utils.setLightStatusBar(this)
        val window: Window = this.window

        mBinding.vpOnBoarding.isUserInputEnabled = false
        window.statusBarColor =
            ContextCompat.getColor(this, R.color.colorGradientGreen)
        progressAnimatorOne = ObjectAnimator.ofInt(mBinding.progressIndicatorOne, "progress", 0, 100)
        progressAnimatorTwo = ObjectAnimator.ofInt(mBinding.progressIndicatorTwo, "progress", 0, 100)
        progressAnimatorThree = ObjectAnimator.ofInt(mBinding.progressIndicatorThree, "progress", 0, 100)
        onBoardingViewModel = ViewModelProvider(this)[OnBoardingViewModel::class.java]
        /* onBoardingViewModel.onBoardingItemObserver.observe(this) {
             //setUpViewPager(it)
             itemListNew = it
         }*/
        onBoardingViewModel.setOnBoardingItems()

        mBinding.tvSkip.setOnClickListener {
            cancelIfRunning()
            if (screenCount == 1) {
                MparticleUtils.logEvent(
                    "Onboarding_CVP_One_Skipped",
                    "User clicks on Skip CTA to skip the CVP screens",
                    "Unique",
                    "Skip",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_CVP_ONE_SKIPPED),
                    this@OnBoardingActivity
                )
            } else if (screenCount == 2) {
                MparticleUtils.logEvent(
                    "Onboarding_CVP_Two_Skipped",
                    "User clicks on Skip CTA to skip the CVP screens",
                    "Unique",
                    "Skip",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_CVP_TWO_SKIPPED),
                    this@OnBoardingActivity
                )
            }
            navigateToApp()
        }

        //setDrawableStatusBar()
        //setUpProgressIndicatorOne(mBinding.progressIndicatorOne)


        mBinding.viewForward.setOnClickListener {
            when (screenChangeCountMax) {
                1 -> {
                    isClicked = true
                    MparticleUtils.logEvent(
                        "Onboarding_CVP_One_Clicked",
                        "User clicks on the screen to move to next CVP story",
                        "Unique",
                        "Continue",
                        SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_CVP_ONE_CLICK),
                        this@OnBoardingActivity
                    )
                    mBinding.vpOnBoarding.setCurrentItem(1, true)
                    progressAnimatorOne.cancel()
                    mBinding.progressIndicatorOne.progress = 100
                }
                2 -> {
                    isClicked = true
                    MparticleUtils.logEvent(
                        "Onboarding_CVP_Two_Clicked",
                        "User clicks on the screen to move to next CVP story",
                        "Unique",
                        "Continue",
                        SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_CVP_TWO_CLICKED),
                        this@OnBoardingActivity
                    )
                    mBinding.vpOnBoarding.setCurrentItem(2, true)
                    progressAnimatorOne.cancel()
                    progressAnimatorTwo.cancel()
                    mBinding.progressIndicatorOne.progress = 100
                    mBinding.progressIndicatorTwo.progress = 100
                }
                3 -> {
                    isClicked = false
                    MparticleUtils.logEvent(
                        "Onboarding_CVP_Three_Clicked",
                        "User clicks on the CTA to continue to the next screen",
                        "Unique",
                        "Continue",
                        SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_CVP_THREE_CLICKED),
                        this@OnBoardingActivity
                    )
                    progressAnimatorOne.cancel()
                    progressAnimatorTwo.cancel()
                    progressAnimatorThree.cancel()
                    mBinding.progressIndicatorOne.progress = 100
                    mBinding.progressIndicatorTwo.progress = 100
                    mBinding.progressIndicatorThree.progress = 100
                }
            }
        }



        onBoardingViewModel.onBoardingItemObserver.observe(this) {
            setUpViewPager(it)
        }
        mBinding.viewPrevious.setOnClickListener {
            isClicked = true
            if (screenChangeLow >= 1) {
                screenChangeLow--
            }
            when (screenChangeLow) {
                2 -> {
                    println("Screen Change Count Previous $screenChangeLow")
                    mBinding.progressIndicatorThree.progress = 0
                    mBinding.progressIndicatorTwo.progress = 0
                    progressAnimatorThree.cancel()
                    mBinding.vpOnBoarding.setCurrentItem(1, true)
                    mBinding.viewPrevious.visibility = View.VISIBLE
                    MparticleUtils.logEvent(
                        "Onboarding_CVP_Three_BackClicked",
                        "User clicks on the left part of the screen to go back to the previous CVP story",
                        "Unique",
                        "Back",
                        SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_CVP_THREE_BACK_CLICKED),
                        this@OnBoardingActivity
                    )
                }
                1 -> {
                    println("Screen Change Count Previous $screenChangeLow")
                    progressAnimatorTwo.cancel()
                    progressAnimatorThree.cancel()
                    progressAnimatorOne.cancel()
                    mBinding.progressIndicatorThree.progress = 0
                    mBinding.progressIndicatorTwo.progress = 0
                    mBinding.progressIndicatorOne.progress = 0
                    mBinding.vpOnBoarding.setCurrentItem(0, true)
                    mBinding.viewPrevious.visibility = View.GONE
                    MparticleUtils.logEvent(
                        "Onboarding_CVP_Two_BackClicked",
                        "User clicks on the left part of the screen to go back to the previous CVP story",
                        "Unique",
                        "Back",
                        SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_CVP_TWO_BACK_CLICKED),
                        this@OnBoardingActivity
                    )
                }
            }

        }

        mBinding.viewPrevious.setOnLongClickListener{
            if (progressAnimatorOne.isRunning){
                progressAnimatorOne.pause()
            }
            return@setOnLongClickListener false
        }


    }

    private fun cancelIfRunning() {
        if ( progressAnimatorOne.isRunning)
            progressAnimatorOne.cancel()
        if ( progressAnimatorTwo.isRunning)
            progressAnimatorTwo.cancel()
        if (progressAnimatorThree.isRunning)
            progressAnimatorThree.cancel()
    }

    private fun setUpViewPager(it: ArrayList<OnBoardingItemModel>?) {
        onOnBoardingAdapter = OnBoardingAdapter(this, it!!)
        mBinding.vpOnBoarding.adapter = onOnBoardingAdapter
        mBinding.vpOnBoarding.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if (position == 0) {

                    mBinding.tvDesc.text = getText(R.string.on_boarding_screen_1)
                    setUpProgressIndicatorOne(mBinding.progressIndicatorOne)
                    screenChangeCountMax = 1

                    isFirst = true
                    mBinding.tvSkip.visibility = View.VISIBLE
                }
                if (position == 1) {
                    mBinding.tvDesc.text = getText(R.string.on_boarding_screen_2)
                    setUpIndicatorTwo(mBinding.progressIndicatorTwo)
                    screenChangeLow = 2
                    isFirst = false
                    mBinding.viewPrevious.visibility = View.VISIBLE
                    screenChangeCountMax = 2
                    mBinding.tvSkip.visibility = View.VISIBLE
                }
                if (position == 2) {
                    mBinding.tvDesc.text = getText(R.string.on_boarding_screen_3)
                    setUpIndicatorThree(mBinding.progressIndicatorThree)
                    screenChangeLow = 3
                    screenChangeCountMax = 3
                    isFirst = false
                    mBinding.tvSkip.visibility = View.INVISIBLE
                }
            }
        })
    }

    private fun navigateToApp() {
        when {
            /*!PermissionUtils.checkRequiredPermission(this) -> {
                startActivity(Intent(this, PermissionActivity::class.java))
            }*/
            SharePrefs.getInstance(this)?.getBoolean(SharePrefsKeys.QUICK_LOGIN_AVAILABLE) == true -> {
                startActivity(Intent(this, ExistingUserActivity::class.java))
            }
            else -> {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
        finish()
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun setUpProgressIndicatorOne(view: View) {
        screenCount = 1
        MparticleUtils.logCurrentScreen(
            "/onboarding/CVP/one",
            "The screen educates the new user on our first key value proposition, that of allow credit tracking in a single app",
            "",
            "",
            "",
            "",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CVP_ONE),
            this@OnBoardingActivity
        )
        MparticleUtils.logEvent(
            "Onboarding_CVP_One_Clicked",
            "User clicks on the screen to move to next CVP story",
            "Unique",
            "Continue",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_CVP_ONE_CLICK),
            this@OnBoardingActivity
        )

        // mBinding.animationOne.visibility = View.VISIBLE
        //mBinding.tvDescOne.visibility = View.VISIBLE
        mBinding.tvSkip.visibility = View.VISIBLE
        progressAnimatorOne.duration = 5200
        progressAnimatorOne.interpolator = LinearInterpolator()
        isStoryOneFinished = progressAnimatorOne.isRunning
        println("isRunning ${progressAnimatorOne.isRunning}")
        progressAnimatorOne.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {
                isClicked = false
            }

            override fun onAnimationEnd(p0: Animator) {
                if (!isClicked)
                    mBinding.vpOnBoarding.setCurrentItem(1, true)
            }

            override fun onAnimationCancel(p0: Animator) {

            }

            override fun onAnimationRepeat(p0: Animator) {

            }

        })
        progressAnimatorOne.start()
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun setUpIndicatorTwo(view: View) {
        screenCount = 2
        MparticleUtils.logCurrentScreen(
            "/onboarding/CVP/two",
            "The screen educates the new user on our second key value proposition, that of allowing repayments of all credit on the app",
            "",
            "",
            "",
            "",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CVP_TWO),
            this@OnBoardingActivity
        )


        progressAnimatorTwo.duration = 5300
        progressAnimatorTwo.interpolator = LinearInterpolator()
        isStoryTwoFinished = progressAnimatorTwo.isRunning
        progressAnimatorTwo.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {
                isClicked = false
            }

            override fun onAnimationEnd(p0: Animator) {
                if (!isClicked)
                    mBinding.vpOnBoarding.setCurrentItem(2, true)
            }

            override fun onAnimationCancel(p0: Animator) {

            }

            override fun onAnimationRepeat(p0: Animator) {

            }

        })
        progressAnimatorTwo.start()

    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun setUpIndicatorThree(view: View) {
        screenCount = 3
        MparticleUtils.logCurrentScreen(
            "/onboarding/CVP/three",
            "The screen educates the new user on our third key value proposition, that of rewarding users with real rewards",
            "",
            "",
            "",
            "",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CVP_THREE),
            this@OnBoardingActivity
        )



        progressAnimatorThree.duration = 5300
        progressAnimatorThree.interpolator = LinearInterpolator()
        isStoryThreeFinished = progressAnimatorThree.isRunning
        progressAnimatorThree.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {
                isClicked = false
                /* mBinding.animationThree.visibility = View.VISIBLE
                 mBinding.tvDescThree.visibility = View.VISIBLE
                 mBinding.tvDescOne.visibility = View.GONE
                 mBinding.tvDescTwo.visibility = View.GONE
                 //mBinding.animationOne.visibility = View.GONE
                 mBinding.animationTwo.visibility = View.GONE*/
            }

            override fun onAnimationEnd(p0: Animator) {
                if (!isClicked)
                    navigateToApp()
            }

            override fun onAnimationCancel(p0: Animator) {

            }

            override fun onAnimationRepeat(p0: Animator) {

            }

        })
        progressAnimatorThree.start()
    }


}