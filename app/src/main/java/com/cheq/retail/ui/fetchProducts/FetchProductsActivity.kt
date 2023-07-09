package com.cheq.retail.ui.fetchProducts

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.databinding.ActivityFetchProductsBinding
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.login.model.Product
import com.cheq.retail.ui.login.model.UpdateProfileResponse
import com.cheq.retail.ui.login.model.loan.Loan
import com.cheq.retail.ui.main.MainActivity
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.Utils
import com.gun0912.tedpermission.provider.TedPermissionProvider

class FetchProductsActivity : BaseActivity() {
    lateinit var binding: ActivityFetchProductsBinding
    var creditResponse: UpdateProfileResponse.CreditResponse? = null

    private var loanList: ArrayList<Loan> = ArrayList()
    private var credCardList: ArrayList<Product> = ArrayList()
    var message = "Congrats! You have"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MparticleUtils.logCurrentScreen(
            "/onboarding/fetching-details",
            "A loading screen is displayed as CheQ fetches the bureau data, scrapes SMS, and performs pay later pulls using customer's data",
            "",
            "",
            "",
            "",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.FETCHING_DETAIL),
            this
        )
        catchIntent()
        setupUI()
        setupAnimation()

    }

    private fun setupAnimation() {
        startAnimation(500, binding.llTitle)

        if (credCardList.isNotEmpty()) {
            Handler(Looper.getMainLooper()).postDelayed({
                binding.llCards.visibility = View.VISIBLE
                startAnimation(500, binding.llCards)
            }, 500)
        }

        if (loanList.isNotEmpty()) {
            Handler(Looper.getMainLooper()).postDelayed({
                binding.llLoans.visibility = View.VISIBLE
                startAnimation(500, binding.llLoans)
            }, 600)
        }

        if (credCardList.isNotEmpty() && loanList.isEmpty()) {
            Handler(Looper.getMainLooper()).postDelayed({
                binding.llMore.visibility = View.VISIBLE
                startAnimation(500, binding.llMore)
            }, 700)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            binding.flBtm.visibility = View.VISIBLE
            startAnimation(500, binding.flBtm)
        }, 800)

        Handler(Looper.getMainLooper()).postDelayed({
            binding.animationConefetti.visibility = View.VISIBLE
        }, 1000)
    }

    private fun catchIntent() {
        if (intent.getSerializableExtra("CREDIT_RESPONSE") == null) {
            return
        }
        creditResponse =
            intent.getSerializableExtra("CREDIT_RESPONSE") as UpdateProfileResponse.CreditResponse

        if (creditResponse != null && creditResponse?.products != null) {
            for (i in creditResponse?.products!!) {
                credCardList.add(
                    Product(
                        // UpdateProfileResponse.SubscriberName(i.Subscriber_Name.value),
                        // UpdateProfileResponse.AccountNumber(i.Account_Number.value)
                        i.isActive,
                        i.isDeleted,
                        i.productType,
                        i.productName,
                        i.bankName,
                        i.holderName,
                        i.insitutionId,
                        i.creditCardType,
                        i.creationSource,
                        i.bureauCreationSource,
                        i.last4,
                        i.product_number,
                        i.encProductNumber,
                        i.expiry,
                        i.userId,
                        i.bankMasterId,
                        i.productMasterId,
                        i.isAutoPayEnabled,
                        i.totalDueEnabled,
                        i._id,
                        i.logo,
                        i.logoWithName,
//                        i.innerGridGradientColors,
//                        i.outerGridGradientColors,
                        i.institutionName,
                        i.bankMasterRecord,
                        i.billDetail,
                        i.outerGridGradientColors,
                        i.innerGridGradientColors
                    )
                )
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupUI() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_fetch_products)
        binding.activity = this
        Utils.setLightStatusBar(this)

        if (credCardList.isNotEmpty()) {
            println("bankMasterNew ${credCardList[0].bankMasterRecord?.id}")
            if(credCardList.size==1){
                message += " ${credCardList.size} card and no stress!"
                binding.tvCards.text="Card"

            }else{
                binding.tvCards.text="Cards"
                message += " ${credCardList.size} cards and no stress!"
            }


            binding.tvBankNameOne.text = credCardList[0].bankMasterRecord?.bankName
            binding.tvCardNumberOne.text =
                "XXXX " + (credCardList[0].product_number?.takeLast(4) ?: "")
            if (credCardList[0].logo != null && credCardList[0].logo?.isNotEmpty()!!) {
                Glide.with(this).load(credCardList[0].logo).into(binding.ivCardOne)
                if (credCardList[0].productType != null) {
                   // binding.ivCardTypeOne.visibility = View.VISIBLE
                    if (credCardList[0].productType == "MasterCard") {
                       // binding.ivCardTypeOne.setImageResource(R.drawable.ic_mastercard)
                    }

                    if (credCardList[0].productType == "Visa") {
                        binding.ivCardTypeOne.setImageResource(R.drawable.visa)
                    }
                } else {
                    binding.ivCardTypeOne.visibility = View.GONE
                }
            }

            if (credCardList.size > 1) {
                binding.llCardTwo.visibility = View.VISIBLE
                binding.tvBankNameTwo.text = credCardList[1].bankMasterRecord?.bankName
                binding.tvCardNumberTwo.text =
                    "XXXX " + (credCardList[1].product_number?.takeLast(4) ?: "")
                if (credCardList[1].logo != null && credCardList[1].logo?.isNotEmpty()!!) {
                    Glide.with(this).load(credCardList[1].logo).into(binding.ivCardTwo)
                }
                if (credCardList[1].productType != null) {
                   // binding.ivCardTypeTwo.visibility = View.VISIBLE
                    if (credCardList[1].productType == "MasterCard") {
                     //   binding.ivCardTypeTwo.setImageResource(R.drawable.ic_mastercard)
                    }

                    if (credCardList[1].productType == "Visa") {
                        binding.ivCardTypeTwo.setImageResource(R.drawable.visa)
                    }
                } else {
                    binding.ivCardTypeTwo.visibility = View.GONE
                }
            } else {
                binding.llCardTwo.visibility = View.GONE
                binding.tvCardCount.text = ""
            }

            if (credCardList.size > 2) {
                try {
                    binding.flCardLogos.visibility = View.VISIBLE
                    binding.tvCardCount.text = "+${(credCardList.size - 2)} more Cards"
                    if (credCardList[2].logo != null && credCardList[2].logo?.isNotEmpty()!!) {
                        Glide.with(this).load(credCardList[2].logo).into(binding.ivCardNextOne)
                        binding.ivCardNextOne.visibility = View.VISIBLE
                    }
                    if (credCardList[3].logo != null && credCardList[3].logo?.isNotEmpty()!!) {
                        Glide.with(this).load(credCardList[3].logo).into(binding.ivCardNextTwo)
                        binding.ivCardNextTwo.visibility = View.VISIBLE
                    }
                    if (credCardList[4].logo != null && credCardList[4].logo?.isNotEmpty()!!) {
                        Glide.with(this).load(credCardList[4].logo).into(binding.ivCardNextThree)
                        binding.ivCardNextThree.visibility = View.VISIBLE
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                binding.flCardLogos.visibility = View.GONE
                binding.tvCardCount.text = ""
            }
            MparticleUtils.logCurrentScreen(
                "/onboarding/success",
                "The customer is displayed an onboarding-success screen, with a snapshot of the products identified",
                "state",
                "${credCardList.size} cc",
                "total-found",
                if (credCardList.size + loanList.size <= 5) "${(({ credCardList.size + loanList.size }))} " else ">5",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.SUCCESS),
                this
            )
            MparticleUtils.logEvent(
                "Onboarding_Success_Continue",
                "User chooses to proceed with product activation on viewing the summary of products identified on the success screen",
                "Unique",
                "Continue",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_SUCCESS_CONTINUE),
                this
            )
        } else if (loanList.isNotEmpty()) {
            binding.llLoans.visibility = View.VISIBLE
            message = message + ", " + loanList.size + "Loans" + "and no stress!"
            binding.tvLoanBankNameOne.text = loanList[0].billerName
            binding.tvLoanNumberOne.text =
                "XXXX " + extractLastFour(loanList[0].productNumber.toString())

            if (loanList.size > 1) {
                binding.llLoanTwo.visibility = View.VISIBLE
                binding.tvLoanBankNameTwo.text = loanList[1].billerName
                binding.tvLoanNumberTwo.text =
                    "XXXX " + extractLastFour(loanList[1].productNumber.toString())
            } else {
                binding.llLoanTwo.visibility = View.GONE
                binding.tvLoansCount.text = ""
            }

            if (loanList.size > 2) {
                binding.flCardLogos.visibility = View.VISIBLE
                binding.tvCardCount.text = "+${(loanList.size - 2)} more Cards"
            } else {
                binding.flCardLogos.visibility = View.GONE
                binding.tvCardCount.text = ""
            }
            MparticleUtils.logCurrentScreen(
                "/onboarding/success",
                "The customer is displayed an onboarding-success screen, with a snapshot of the products identified",
                "state",
                "${credCardList.size} loan",
                "total-found",
                if (credCardList.size + loanList.size <= 5) "${(({ credCardList.size + loanList.size }))} " else ">5",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.SUCCESS),
                this
            )
            MparticleUtils.logEvent(
                "Onboarding_Success_Continue",
                "User chooses to proceed with product activation on viewing the summary of products identified on the success screen",
                "Unique",
                "Continue",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_SUCCESS_CONTINUE),
                this
            )
        } else {

            binding.llLoans.visibility = View.GONE


        }

        binding.tvTitle.text = message
    }

    fun navigateToNextScreen() {
       // createWaitListUser()
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
    }

    private fun extractLastFour(cardNumber: String): String {
        if (cardNumber.length > 4) {
            return cardNumber.takeLast(4)
        }
        return cardNumber
    }
    private fun startAnimation(duration: Long, viewToAnimate: View) {
        val animation: Animation =
            AnimationUtils.loadAnimation(TedPermissionProvider.context, R.anim.slide_up)
        viewToAnimate.startAnimation(animation)
        animation.duration = duration
    }

}