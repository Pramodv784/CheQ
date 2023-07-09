package com.cheq.retail.ui.billPayments

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ComponentName
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.*
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.Observable.OnPropertyChangedCallback
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.base.FirebaseRemoteConfigUtils
import com.cheq.retail.constants.AFConstants
import com.cheq.retail.constants.AFConstants.CAPTURED
import com.cheq.retail.constants.AFConstants.CAPTURED_SMALL
import com.cheq.retail.constants.AFConstants.CC
import com.cheq.retail.constants.AFConstants.CREATED_SMALL
import com.cheq.retail.constants.AFConstants.LOAN
import com.cheq.retail.constants.AFConstants.NET_BANKING
import com.cheq.retail.constants.AFConstants.PROCESSING
import com.cheq.retail.databinding.ActivityPaymentMethodsBinding
import com.cheq.retail.databinding.BottomSheetPaymentFailedBinding
import com.cheq.retail.databinding.BottomSheetPaymentPendingBinding
import com.cheq.retail.extensions.bankMasterUrl
import com.cheq.retail.extensions.loanMasterUrl
import com.cheq.retail.extensions.paymentDowntime
import com.cheq.retail.extensions.setAllEnabled
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.accountSettings.webView.CommonWebViewActivity
import com.cheq.retail.ui.activateCard.PaymentWebViewActivity
import com.cheq.retail.ui.activateCard.viewModel.ProductActivationViewModel
import com.cheq.retail.ui.billPayments.PaymentMethodsActivity.SupportedUPIApps.Companion.DEBIT_CARD
import com.cheq.retail.ui.billPayments.PaymentMethodsActivity.SupportedUPIApps.Companion.DEFAULT_PERCENTAGE
import com.cheq.retail.ui.billPayments.PaymentMethodsActivity.SupportedUPIApps.Companion.FRAGMENT_TAG
import com.cheq.retail.ui.billPayments.PaymentMethodsActivity.SupportedUPIApps.Companion.PAYMENT_METHOD
import com.cheq.retail.ui.billPayments.PaymentMethodsActivity.SupportedUPIApps.Companion.REWARD
import com.cheq.retail.ui.billPayments.PaymentMethodsActivity.SupportedUPIApps.Companion.SAVED_CARD
import com.cheq.retail.ui.billPayments.PaymentMethodsActivity.SupportedUPIApps.Companion.UPI
import com.cheq.retail.ui.billPayments.adapter.BanksListAdapter
import com.cheq.retail.ui.billPayments.adapter.PreferredMethodAdapter
import com.cheq.retail.ui.billPayments.adapter.SavedCardAdapter
import com.cheq.retail.ui.billPayments.adapter.UPIAppAdapter
import com.cheq.retail.ui.billPayments.custom_view.CopyEditText
import com.cheq.retail.ui.billPayments.custom_view.GoEditTextListener
import com.cheq.retail.ui.billPayments.model.*
import com.cheq.retail.ui.billPayments.paymentsInterface.PaymentOptionInterface
import com.cheq.retail.ui.billPayments.viewModel.BillPaymentsViewModel
import com.cheq.retail.ui.billSummary.BillSummaryListener
import com.cheq.retail.ui.billSummary.adapter.BillSummaryAdapter
import com.cheq.retail.ui.billSummary.bottomSheetFragment.BillSummaryFragment
import com.cheq.retail.ui.billSummary.model.*
import com.cheq.retail.ui.billSummary.viewModel.BillSummaryViewModel
import com.cheq.retail.ui.login.modelv2.productv1.ProductV2
import com.cheq.retail.ui.main.MainActivity
import com.cheq.retail.ui.main.helper.MonthlyEarnRule
import com.cheq.retail.ui.rewards.util.State
import com.cheq.retail.utils.*
import com.cheq.retail.utils.ImageUtils.loadSvg
import com.cheq.retail.utils.Utils.Companion.roundupAmount
import com.freshchat.consumer.sdk.Freshchat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.*
import com.razorpay.Razorpay
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.math.roundToInt


class PaymentMethodsActivity : BaseActivity(), UPIAppAdapter.onUPIClickListener,
    BillSummaryFragment.BillSummaryCall, PaymentOptionInterface, BillSummaryListener {

    enum class SupportedUPIApps constructor(val packageName: String) {
        GOOGLE_PAY("com.google.android.apps.nbu.paisa.user"), PHONE_PE("com.phonepe.app"), PAYTM("net.one97.paytm");

        companion object {
            const val PAYMENT_METHOD = "payment_method"
            val packageNames: List<String>
                get() = values().map { it.packageName }
            const val UPI = "UPI"
            const val DEBIT_CARD = "DEBIT_CARD"
            const val REWARD = "REWARD"
            const val SAVED_CARD = "SAVED_CARD"
            const val DEFAULT_PERCENTAGE = 1.0
            private val TAG = PaymentMethodsActivity::class.java.simpleName
            val FRAGMENT_TAG = "$TAG.FRAGMENT_TAG"
        }
    }

    val TAG = "PaymentMethodActivity"
    private var upiName: String = ""
    private var upiLogo: String = ""
    lateinit var binding: ActivityPaymentMethodsBinding
    private var bankName: String? = null
    private var amount: String? = null
    private var upiPackageName: String = ""
    private var netBankList: ArrayList<PaymentOptionsResponse.BanksItem> = ArrayList()
    var allNetBankList: ArrayList<PaymentOptionsResponse.BanksItem> = ArrayList()
    var upiList: ArrayList<UPIAppModelClass> = ArrayList()
    var allUpiList: ArrayList<UPIAppModelClass> = ArrayList()
    var billStatus = ""
    var isSecure = true
    var startTXNID = ""
    var comingFrom = ""
    var payOutMode = ""
    var productType = ""
    var payoutNarration = ""
    var localBankId = ""
    var lastFour = ""
    private var payableAmount = 0

    private var preferredMethodsItemList: List<PaymentOptionsResponse.PreferredMethodsItem?> =
        ArrayList()
    private var viewModel: BillPaymentsViewModel? = null
    private var productActivationViewModel: ProductActivationViewModel? = null
    lateinit var billSummaryViewModel: BillSummaryViewModel
    var ifsc: String = ""
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var pendingBottomSheetDialog: Dialog
    private lateinit var upiBottomSheetDialog: BottomSheetDialog
    private lateinit var paymentReviewDialog: Dialog
    private lateinit var billSummaryFragment: BillSummaryFragment
    private lateinit var llError: LinearLayoutCompat
    private lateinit var tvError: AppCompatTextView
    var showBottomSheet: Boolean = true
    var bankLogo = ""
    var paymentModeName = ""
    var paymentModeLogo = ""
    var razorpayPaymentId = ""
    var debitCardNumber = ""
    var debitNetwork = ""
    var debitType = ""
    var debitSubType = ""
    var debitIssuerCode = ""
    var debitIssuerName = ""
    var debitBankMasterId = ""
    var debitLogo = ""
    var debitInternational: Boolean? = null
    var isDebitCard = ObservableBoolean(false)
    var progress = ObservableBoolean(false)
    var isDebitCardValid = false
    var isCardExpiryFilled = false
    var isCVVFilled = false
    var date: String = ""
    var cvv: String = ""
    var isNameValid = false
    var btnProceedToPay: AppCompatButton? = null
    var paymentType = ""
    var debitCardHolderName = ""
    var debitCardNetwork = ""
    var debitCardExpiryMonth = ""
    var debitCardExpiryYear = ""
    var debitCardCvv = ""
    var rewardsPoint = 0
    var localRewardPoint = 0
    var isPayTogether = false
    var rewardsUsed = false
    var chipUsed = 0
    var chipUsedLocal = 0
    var amountForRewardsLocal = 0
    var chipAmountLocal = 0
    var totalChips = 0
    var chipRemaining = 0
    private var rewardPointConversionRate = 0.0
    private var totalChipAmount = 0
    var payByRewards = false
    var amountForRewards = 0
    var scBankName = ""
    var scToken = ""
    var paymentInstrument = ""
    var selectedProduct: ArrayList<ProductV2> = ArrayList()
    var selectedProductList: ArrayList<ProductsItem> = ArrayList()
    var productItemList: ArrayList<ProductItem> = ArrayList()
    var dcBankMasterID = ""
    var dcBankName = ""
    lateinit var prefs: SharePrefs
    private var rewardsCanAssign = 0
    private var rewardsAssigned = 0
    private var rewardsAssignUpto = 0
    var isCard = false
    private var mFirebaseDatabase: DatabaseReference? = null
    private var mFirebaseInstance: FirebaseDatabase? = null
    private var logo: String? = null
    private var localBankName: String? = null
    private var debitIIN: String? = null
    private var debitEntity: String? = null
    private var afBankName : String? = null  /** this variable holds the value of bank name in case of CC and billerName in cas of loan **/
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        catchIntent()
        mFirebaseInstance =
            FirebaseDatabase.getInstance(com.cheq.retail.BuildConfig.firebaseDataBaseURL)
        mFirebaseDatabase = mFirebaseInstance?.reference
        setUpUi()
        setupViewModel()
        setupObserver()
        viewModel?.getPaymentOptions()
        paymentDowntime()

    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[BillPaymentsViewModel::class.java]
        productActivationViewModel = ViewModelProvider(this)[ProductActivationViewModel::class.java]
        billSummaryViewModel = ViewModelProvider(this)[BillSummaryViewModel::class.java]
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupObserver() {

        viewModel?.statusObserver?.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        viewModel?.progressObserver?.observe(this) {
            if (it) {
                Utils.getUserClickBehaviour(false, this)
                progress.set(true)
            } else {
                Utils.getUserClickBehaviour(true, this)
                progress.set(false)
            }
        }

        productActivationViewModel?.debitcardObserver?.observe(this) {
            try {
                if (debitCardNumber.length >= 6) {
                    if (it.iin != null && it.status != null && it.status) {
                        llError.visibility = View.GONE
                        isDebitCard.set(true)
                        debitType = it.iin.type ?: ""
                        debitIIN = it.iin.toString()
                        debitEntity = it.iin.entity
                        debitSubType = it.iin.subType ?: ""
                        debitIssuerCode = it.iin.issuerCode ?: ""
                        debitIssuerName = it.bank?.originalBankName ?: ""
                        debitCardNetwork = it.iin.network ?: ""
                        debitInternational = it.iin.international
                        debitBankMasterId = it.bank?.id ?: ""
                        debitLogo = it.bank?.id ?: ""
                        dcBankName = it.bank?.originalBankName ?: ""
                    } else if (!(it.status ?: return@observe)) {
                        llError.visibility = View.VISIBLE
                        tvError.text = it.userErrorMessage ?: it.message.toString()
                    } else if (it.iin?.type.equals("credit", true)) {
                        isDebitCard.set(false)
                        llError.visibility = View.VISIBLE
                    }

                }
            } catch (e: Exception) {

            }
        }



        viewModel?.paymentOptionsObserver?.observe(this) {
            if (it?.status != null && it.status) {
                if ((it.rewardPoints?.rewardToRupeesConversion != null) && (it.rewardPoints?.getRewardPoints
                        ?: 0) > 0
                ) {
                    rewardPointConversionRate = it.rewardPoints?.rewardToRupeesConversion
                    totalChips = it.rewardPoints?.getRewardPoints ?: 0
                    binding.llRewardsPay.visibility = View.VISIBLE
                    setCorrespondAmount()
                } else {
                    binding.llRewardsPay.visibility = View.GONE
                }
                if (it.preferredMethods.isNotEmpty()) {
                    binding.llPreferredMethod.visibility = View.VISIBLE
                    preferredMethodsItemList = it.preferredMethods
                    val preferredMethodAdapter =
                        PreferredMethodAdapter(preferredMethodsItemList, this, this)
                    binding.rvPreferredMethod.apply {
                        adapter = preferredMethodAdapter
                        hasFixedSize()
                    }
                }
                if (it.savedCards.isNotEmpty()) {
                    binding.rvSavedCard.visibility = View.VISIBLE
                    val savedCardAdapter = SavedCardAdapter(it.savedCards, this, this)
                    binding.rvSavedCard.apply {
                        adapter = savedCardAdapter
                        hasFixedSize()
                    }
                } else {
                    binding.rvSavedCard.visibility = View.GONE
                }
                if (it.netBanking.top6.isNotEmpty()) {

                    binding.rvNetbanking.layoutManager = LinearLayoutManager(this)
                    allNetBankList.clear()
                    allNetBankList.addAll(it.netBanking.banks)
                    allNetBankList.sortedBy {
                        it.bankName
                    }
                    if (it.netBanking.top6.size > 3) {
                        for (i in 0 until 3) {

                            netBankList.add(
                                PaymentOptionsResponse.BanksItem(
                                    it.netBanking.top6[i].bureauBankName,
                                    it.netBanking.top6[i].logoWithName,
                                    it.netBanking.top6[i].bankName,
                                    it.netBanking.top6[i].isActive,
                                    it.netBanking.top6[i].IFSC_Prefix,
                                    it.netBanking.top6[i].ocrBankName,
                                    it.netBanking.top6[i].originalBankName,
                                    it.netBanking.top6[i].logo,
                                    it.netBanking.top6[i].rank.toString(),
                                    it.netBanking.top6[i]._id,
                                    it.netBanking.top6[i].updatedAt
                                )
                            )


                        }
                    } else {
                        for (i in it.netBanking.top6.indices) {
                            netBankList.add(
                                PaymentOptionsResponse.BanksItem(
                                    it.netBanking.top6[i].bureauBankName,
                                    it.netBanking.top6[i].logoWithName,
                                    it.netBanking.top6[i].bankName,
                                    it.netBanking.top6[i].isActive,
                                    it.netBanking.top6[i].IFSC_Prefix,
                                    it.netBanking.top6[i].ocrBankName,
                                    it.netBanking.top6[i].originalBankName,
                                    it.netBanking.top6[i].logo,
                                    it.netBanking.top6[i].rank.toString(),
                                    it.netBanking.top6[i]._id,
                                    it.netBanking.top6[i].updatedAt
                                )
                            )
                        }


                    }
                    binding.rvNetbanking.adapter = BanksListAdapter(this, netBankList, this)

                    binding.rvSavedCard.visibility = View.VISIBLE
                    val savedCardAdapter = SavedCardAdapter(it.savedCards, this, this)
                    binding.rvSavedCard.apply {
                        adapter = savedCardAdapter
                        hasFixedSize()
                    }
                }
            }

        }
        viewModel?.startPaymentObserver?.observe(this) {
            try {
                if (it != null && it.status == true) {
                    startTXNID = it.txnId.toString()
                    if (it.data != null) {
                        if(billSummaryFragment.dialog?.isShowing == true){
                            billSummaryFragment.dialog?.dismiss()
                        }

                        if (paymentType != UPI) {
                            razorpayPaymentId = it.razorpay_payment_id.toString()
                            startActivity(Intent(
                                this, PaymentWebViewActivity::class.java
                            ).apply {
                                putExtra("url", it.data.toString())
                            })
                            Utils.hideProgressDialog()

                        } else {
                            razorpayPaymentId = it.razorpay_payment_id.toString()
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data =
                                Uri.parse(it.data.toString()) //uri from the create S2S payment response
                            intent.setPackage(upiPackageName) //package name from the `upi://pay` intent response
                            val name: ComponentName = intent.resolveActivity(packageManager)
                            val flags = intent.flags
                            if (flags and Intent.FLAG_GRANT_READ_URI_PERMISSION == 0 && flags and Intent.FLAG_GRANT_WRITE_URI_PERMISSION == 0 && SupportedUPIApps.packageNames.contains(
                                    name.packageName
                                )
                            ) {
                                upiResult.launch(intent)
                            }
                        }


                    } else {

                        if (it.txn_completed == true) {
                            Utils.hideProgressDialog()
                            if (paymentType == REWARD) {
                                startTXNID = it.txnId.toString()
                                viewModel?.getTXNStatus(startTXNID, razorpayPaymentId, "")

                            }
                        }
                    }
                }
            } catch (e: Exception) {

            }
        }
        viewModel?.getTXNStatusObserver?.observe(this) {
            if (it != null) {

                payOutMode = it.payout_mode ?: ""
                productType = it.product_type ?: ""
                payoutNarration = it.payout_ect_narration_v2 ?: ""
                if (paymentType == UPI) {
                    if ((it.UPI_Payment_status != null && it.UPI_Payment_status == CAPTURED_SMALL)) {
                        bottomSheetDialog.dismiss()
                        goToSuccessfulScreen(it)
                    } else if ((it.UPI_Payment_status != null && it.UPI_Payment_status == CREATED_SMALL)) {
                        bottomSheetDialog.dismiss()
                        it.txn?.created_at?.let { it1 -> openPendingBottomSheet(amount, it1) }
                    } else {
                        bottomSheetDialog.dismiss()
                        openPaymentFailedBottomSheet(it)

                    }
                } else {
                    when (it.txn?.txn_status) {
                        CAPTURED -> {
                            if (paymentType != REWARD) {
                                bottomSheetDialog.dismiss()
                            }
                            goToSuccessfulScreen(it)
                        }
                        PROCESSING -> {
                            if (paymentType != REWARD) {
                                bottomSheetDialog.dismiss()
                            }
                            openPendingBottomSheet(amount, it.txn.created_at)
                        }
                        else -> {
                            bottomSheetDialog.dismiss()
                            openPaymentFailedBottomSheet(it)
                        }
                    }

                }
            } else {
                bottomSheetDialog.dismiss()
                openPaymentFailedBottomSheet(it)
            }
        }

        viewModel?.getBillSummary?.observe(this) {
            when (it) {
                is State.Error -> {
                    Utils.hideProgressDialog()

                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }

                is State.Loading -> {
                    Utils.showProgressDialog(this)
                }

                is State.Success -> {
                    Utils.hideProgressDialog()
                    billSummaryFragment = BillSummaryFragment.newInstance(
                        paymentType = paymentType,
                        productItemList = productItemList,
                        rewardsCanAssign = rewardsCanAssign,
                        rewardsAssigned = rewardsAssigned,
                        rewardsAssignUpto = rewardsAssignUpto,
                        logo = logo ?: "",
                        originalBankName = localBankName ?: "",
                        billSummaryResponse = it.data,
                        isRewardUsed = chipUsed > 0,
                        bankName = afBankName,
                        lastFour = lastFour,
                        payinAmount = amount,
                        productType = if (isCard) CC else LOAN,
                        paymentMethodName = paymentType,
                    )
                    try {
                        if (it.data?.footers?.get(0)?.key == BillSummaryAdapter.BILL.PAYABLE_AMOUNT.key) {
                            payableAmount = it.data.footers[0]?.value ?: 0
                        }
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)

                    }

                    billSummaryFragment.show(supportFragmentManager, FRAGMENT_TAG)
                }

                else -> {
                    Utils.hideProgressDialog()
                    Toast.makeText(this, AFConstants.SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    }


    private fun setCorrespondAmount(): Int {
        val chipCorrespondAmount = (totalChips * rewardPointConversionRate).toInt()
        if (chipCorrespondAmount <= (amount?.toDouble()?.toInt() ?: 0)) {
            binding.tvCoinText.text = "Save upto ₹${chipCorrespondAmount} "
        } else {
            binding.tvCoinText.text = "Save upto ₹${amount?.toDouble()?.toInt()} "
        }
        return chipCorrespondAmount
    }

    @SuppressLint("SetTextI18n")
    private fun catchIntent() {
        amount = intent.getStringExtra(IntentKeys.TOTAL_AMOUNT)
        billStatus = intent.getStringExtra(IntentKeys.BILL_STATUS).toString()
        comingFrom = intent.getStringExtra(IntentKeys.COMING_FROM).toString()
        rewardsAssignUpto = intent.getIntExtra(IntentKeys.REWARDS_ASSIGN_UPTO, 0)
        rewardsCanAssign = intent.getIntExtra(IntentKeys.REWARDS_CAN_ASSIGN, 0)
        rewardsAssigned = intent.getIntExtra(IntentKeys.REWARDS_ASSIGNED, 0)
        selectedProduct =
            intent.getSerializableExtra(IntentKeys.PRODUCT_LIST) as ArrayList<ProductV2>
        isPayTogether = intent.getBooleanExtra(IntentKeys.IS_PAY_TOGETHER, false)
        if (selectedProduct.isNotEmpty()) {
            for (product in selectedProduct) {
                rewardsPoint += product.rewardsPoint ?: 0
                selectedProductList?.add(
                    ProductsItem(
                        product.bankmaster_id,
                        product.customAmount?.toString(),
                        product.id,
                        product.institution_master_id,
                        product.billStatus,
                        product.bill?.id,
                        false,
                        "card",
                        product.product_type
                    )
                )
                productItemList.add(
                    ProductItem(
                        product.customAmount?.toString(),
                        product.id
                    )
                )
            }


            isCard = selectedProduct[0].product_type == "CC"
        } else
            finish()

    }


    private fun setUpUi() {
        prefs = SharePrefs.getInstance(this)!!
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_methods)
        if (isCard) {
            MparticleUtils.logCurrentScreen(
                "/cc-payment/select-payment-method",
                "The customer chooses the payment method for payment of the selected due amount",
                "amount",
                "$amount",
                "",
                "",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Select_Payment_Method),
                this
            )
        } else {
            MparticleUtils.logCurrentScreen(
                "/loan-payment/select-payment-method",
                "The customer chooses the payment method for payment of the selected due amount",
                "amount",
                "$amount",
                "",
                "",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.LOAN_Payment_Select_PAYMENT_METHOD),
                this
            )
        }



        pendingBottomSheetDialog =
            Utils.showCustomDialogBottum(this, R.layout.bottom_sheet_payment_pending)
        Utils.hideSoftKeyboard(this)
        prefs.putBoolean(SharePrefsKeys.IS_PAYMENT_FAILED, false)
        Utils.setLightStatusBar(this)
        binding.ivBack.setOnClickListener {
            if (comingFrom == "cardDetails") {
                prefs.putBoolean(SharePrefsKeys.IS_BACK_PRESSED, true)
                onBackPressed()
            } else {
                onBackPressed()
                prefs.putBoolean(SharePrefsKeys.IS_BACK_PRESSED, true)
            }

        }
        if (chipAmountLocal > 0) {
            binding.llRewardsPay.visibility = View.VISIBLE
        } else {
            binding.llRewardsPay.visibility = View.GONE
        }
        binding.tvPayingAmount.text = setAmount(amount)
        binding.loanCard.tvLoanAmount.text = setAmount(amount)
        binding.tvSingleAmount.text = setAmount(amount)
        if (selectedProduct.size > 1) {
            binding.tvPayTogetherProductCount.text = "${selectedProduct.size} PRODUCTS VIA"
            binding.llPayTogether.visibility = View.VISIBLE
            binding.llSingleCard.visibility = View.GONE
            binding.llLoanCard.visibility = View.GONE

            if (selectedProduct[0].product_type == "CC") {
                binding.ivBankLogoOne.loadSvg("${prefs.bankMasterUrl}${selectedProduct[0].bankMasterRecord?.id}-logo.svg")
            } else {
                binding.ivBankLogoOne.loadSvg("${prefs.loanMasterUrl}${selectedProduct[0].bankMasterRecord?.id}-logo.svg")
            }
            if (selectedProduct[1].product_type == "CC") {
                binding.ivBankLogoTwo.loadSvg("${prefs.bankMasterUrl}${selectedProduct[1].bankMasterRecord?.id}-logo.svg")
            } else {
                binding.ivBankLogoTwo.loadSvg("${prefs.loanMasterUrl}${selectedProduct[1].bankMasterRecord?.id}-logo.svg")
            }


            binding.flTotalProductList.visibility = View.VISIBLE
            if (selectedProduct.size > 2) {
                binding.llCardCount.visibility = View.VISIBLE
                binding.tvCardCount.text = "+${selectedProduct.size - 2}"
            } else {
                binding.llCardCount.visibility = View.GONE
            }
        } else {
            localBankId = selectedProduct[0].bankMasterRecord?.id ?: ""
            Utils.initFirebase(this)

            rewardsPoint = roundupAmount(
                amount?.toDouble() ?: DEFAULT_PERCENTAGE,
                DEFAULT_PERCENTAGE,
                null,
                selectedProduct[0].bankMasterRecord?.id ?: "",
                Utils.itemList
            )
            billStatus = selectedProduct[0].billStatus
            rewardsPoint = selectedProduct[0].rewardsPoint ?: 0
            binding.flTotalProductList.visibility = View.GONE
            binding.llPayTogether.visibility = View.GONE
            if (isCard) {
                binding.llSingleCard.visibility = View.VISIBLE
                binding.llLoanCard.visibility = View.GONE
            } else {
                binding.llLoanCard.visibility = View.VISIBLE
                binding.llSingleCard.visibility = View.GONE
                setLoanCard(selectedProduct[0])
            }

            if (selectedProduct[0].card_network == "MasterCard") {
                binding.ivSingleCardType.setImageResource(R.drawable.ic_mastercard)
            }
            if (selectedProduct[0].card_network == "Visa") {
                binding.ivSingleCardType.setImageResource(R.drawable.visa)
            }
            if (selectedProduct[0].card_network == "Diners Club") {
                binding.ivSingleCardType.setImageResource(R.drawable.ic_dinner_icon)
            }
            if (selectedProduct[0].card_network == "American Express") {
                binding.ivSingleCardType.setImageResource(R.drawable.ic_american_icon)
            }
            if (selectedProduct[0].card_network == "RuPay") {
                binding.ivSingleCardType.setImageResource(R.drawable.ic_rupay_card_icon)
            }

            binding.tvSingleBankName.text = selectedProduct[0].bankMasterRecord?.bankName ?: ""
            afBankName = selectedProduct[0].bankMasterRecord?.bankName
            binding.tvSingleCardNumber.text = "··· " + (selectedProduct[0].last4)
            lastFour = selectedProduct[0].last4
            if (selectedProduct[0].bankMasterRecord?.ui_config != null) {
                GradientUtils.setBoarderStroke(
                    selectedProduct[0].bankMasterRecord?.ui_config?.cardColor ?: "#FFFFFF",
                    selectedProduct[0].bankMasterRecord?.ui_config?.opacity_border ?: "#FFFFFF",
                    true,
                    binding.llSingleCard
                )
                GradientUtils.setBackGround(
                    selectedProduct[0].bankMasterRecord?.ui_config?.cardColor ?: "#FFFFFF",
                    "",
                    selectedProduct[0].bankMasterRecord?.ui_config?.opacity_topLeft ?: "#FFFFFF",
                    selectedProduct[0].bankMasterRecord?.ui_config?.opacity_bottomRight
                        ?: "#FFFFFF",
                    binding.llCardSolidBackGround
                )
            }
        }
        if (selectedProduct[0].product_type == "LOAN") {
            binding.tvSingleBankName.text = selectedProduct[0].billerName
            afBankName = selectedProduct[0].billerName
            binding.ivSingleCardType.visibility = View.GONE
        }
        amount?.toDouble()?.let {
            if (it > FirebaseRemoteConfigUtils.getUpiLimit().toDouble()) {
                binding.tvSeeAllUPI.visibility = View.GONE
                binding.viewUpi.visibility = View.GONE
                binding.llUpiError.visibility = View.VISIBLE
                binding.tvUpiError.text = FirebaseRemoteConfigUtils.getUpiLimitExceedText()
            } else {
                fetchUpiApp(false)
            }
        }
        binding.linearUseCheqChips.setOnClickListener {
            if (binding.cbOne.isChecked) {
                binding.cbOne.isChecked = false
                binding.llPaywithReward.visibility = View.GONE
                binding.llChecReward.visibility = View.GONE
                binding.tvCoinText.visibility = View.VISIBLE
                binding.tvRewardsUsed.visibility = View.GONE
                rewardsUsed = false
                rewardsPoint = (amount?.toDouble()?.let {
                    roundupAmount(
                        it, DEFAULT_PERCENTAGE, null, localBankId, Utils.itemList
                    )
                } ?: return@setOnClickListener)
                binding.llRemainingAmount.visibility = View.GONE
                payByRewards = false
                binding.flPayTogether.visibility = View.GONE
                binding.llBlur.alpha = 1.0F
                binding.llBlur.apply {
                    setAllEnabled(true)
                }
                chipUsed = 0

            } else {
                if (binding.llPaywithReward.isVisible) {
                    binding.llPaywithReward.visibility = View.GONE
                    binding.tvCoinText.visibility = View.VISIBLE
                    binding.llChecReward.visibility = View.GONE
                } else {
                    binding.llPaywithReward.visibility = View.VISIBLE
                    binding.tvCoinText.visibility = View.GONE
                    binding.llChecReward.visibility = View.VISIBLE
                }


                totalChipAmount = ((totalChipAmount * rewardPointConversionRate).roundToInt())
                val chipCorrespondAmount = setCorrespondAmount()
                if (chipCorrespondAmount <= (amount?.toDouble()?.toInt() ?: 0)) {
                    binding.etCoin.setText("" + chipCorrespondAmount)
                } else {
                    binding.etCoin.setText("" + (amount?.toDouble()?.toInt() ?: 0))
                }
            }
        }

        binding.btnApply.setOnClickListener {
            amountForRewards = amountForRewardsLocal
            chipUsed = chipUsedLocal
            binding.cbOne.isChecked = true
            binding.llPaywithReward.visibility = View.GONE
            binding.llChecReward.visibility = View.GONE
            binding.tvCoinText.visibility = View.GONE
            binding.tvRewardsUsed.text = "₹$amountForRewards applied"
            binding.tvRewardsUsed.visibility = View.VISIBLE
            rewardsUsed = true
            rewardsPoint = (amount?.toDouble()?.let {
                roundupAmount(
                    it.minus(amountForRewards.toDouble()),
                    DEFAULT_PERCENTAGE,
                    null,
                    localBankId,
                    Utils.itemList
                )
            } ?: return@setOnClickListener)
            chipRemaining = totalChips - chipUsed
            val remainingAmount =
                (amount?.toDouble()?.toInt() ?: return@setOnClickListener) - amountForRewards
            if (remainingAmount > 0) {
                binding.llRemainingAmount.visibility = View.VISIBLE
                binding.tvRemainingAmount.text =
                    "Select method below to pay pending ₹${Utils.getFormattedDecimal(remainingAmount.toDouble())}"
            } else {
                binding.llRemainingAmount.visibility = View.GONE
            }


            if (amount != null && amountForRewards == amount!!.toDouble().toInt()) {
                payByRewards = true
                binding.llBlur.alpha = 0.20F
                binding.flPayTogether.visibility = View.VISIBLE
                binding.llBlur.apply {
                    setAllEnabled(false)
                }

            } else {

                payByRewards = false
                binding.flPayTogether.visibility = View.GONE
            }
            Utils.hideSoftKeyboard(this)
        }

        binding.btnProceedToPay.setOnClickListener {
            paymentType = REWARD
            Utils.showProgressDialog(this)
            startPayment(upiPackageName)

        }
        binding.etCoin.setOnFocusChangeListener { _, b ->
            if (b) {
                binding.llBtmAPPLY.setBackgroundResource(R.drawable.ic_cvv_btm_focused)
            } else {
                binding.llBtmAPPLY.setBackgroundResource(R.drawable.ic_cvv_btm_un_focused)
            }

        }
        binding.etCoin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty() && s.toString().toInt() > 0) {
                    amountForRewardsLocal = s.toString().toInt()
                    chipAmountLocal = (amountForRewardsLocal / rewardPointConversionRate).toInt()
                    chipUsedLocal = (amountForRewardsLocal / rewardPointConversionRate).toInt()
                    if (chipAmountLocal <= totalChips && amountForRewardsLocal <= (amount?.toDouble()
                            ?.toInt() ?: 0)
                    ) {
                        binding.btnApply.isEnabled = true
                        binding.llChecReward.visibility =
                            if (binding.llPaywithReward.isVisible) View.VISIBLE else View.GONE
                        binding.tvCheqRewardText.visibility = View.VISIBLE
                        binding.tvCheqRewardText.text = "$chipUsedLocal CheQ Chips will be used"
                        binding.ivInfo.visibility = View.VISIBLE
                    } else {
                        binding.tvCheqRewardText.visibility = View.GONE
                        binding.btnApply.isEnabled = false
                        binding.llChecReward.visibility = View.GONE

                    }
                } else {
                    binding.btnApply.isEnabled = false
                    binding.llChecReward.visibility = View.VISIBLE
                    binding.tvCheqRewardText.visibility = View.VISIBLE
                    binding.tvCheqRewardText.text = "0 CheQ Chips will be used"
                    binding.ivInfo.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        binding.tvSeeAllNetBanking.setOnClickListener {
            openAllBankBottomSheet()

            if (isCard) {
                MparticleUtils.logEvent(
                    "CC_Payment_NetBanking_SeeAll_Clicked",
                    "User opts to view all the available Net Banking options for bill payment",
                    "Unique",
                    "Continue",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_NetBanking_SeeAll_Clicked),
                    this@PaymentMethodsActivity
                )

                MparticleUtils.logCurrentScreen(
                    "/cc-payment/all-net-banking",
                    "The customer chooses to view all banks available for payment via net banking",
                    "",
                    "",
                    "",
                    "",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_ALL_NetBanking_SeeAll_Clicked),
                    this
                )
            } else {
                MparticleUtils.logCurrentScreen(
                    "/loan-payment/all-net-banking",
                    "The customer chooses to view all banks available for payment via net banking",
                    "",
                    "",
                    "",
                    "",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.LOAN_Payment_ALL_NetBanking_SeeAll_Clicked),
                    this
                )
            }


        }
        binding.tvSeeAllUPI.setOnClickListener {


            MparticleUtils.logEvent(
                "CC_Payment_UPI_SeeAll_Clicked",
                "User opts to view all the available UPI options for bill payment",
                "Unique",
                "Continue",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_UPI_SeeAll_Clicked),
                this@PaymentMethodsActivity
            )

            MparticleUtils.logCurrentScreen(
                "/cc-payment/all-upi",
                "The customer chooses to view all possible UPI methods identified by the app",
                "",
                "",
                "",
                "",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_UPI_ALL_SeeAll_Clicked),
                this
            )
            fetchUpiApp(false)
            //  openAllUPIAPPBottomSheet()
            openUPIListBottomSheet()
        }

        binding.llAddDebitcard.setOnClickListener {
            openAddDebitCardBottomSheet(false)
        }


    }

    private fun setAmount(amount: String?): String {
        if (!amount.isNullOrEmpty()) {
            return getString(R.string.str_rupee_symbol) + amount.toDouble()
                .let { Utils.getFormattedDecimal(it) }
        }

        return ""
    }

    private fun setLoanCard(productV2: ProductV2) {
        binding.loanCard.tvBankName.text = productV2.bankMasterRecord?.billerName
        binding.loanCard.tvCardNumber.text = "··· " + (productV2.last4)
        lastFour = productV2.last4
        if (productV2.bankMasterRecord?.ui_config != null) {
            binding.loanCard.flSolidColor.setBackgroundResource(R.drawable.loan_bottom_shap)
            val bg: Drawable = binding.loanCard.flSolidColor.background
            bg.setTint(Color.parseColor(productV2.bankMasterRecord.ui_config.cardColor))

        }
    }

    private fun fetchUpiApp(allUPI: Boolean) {
        upiList.clear()
        allUpiList.clear()
        Razorpay.getAppsWhichSupportUpi(this) {
            if (it.isNotEmpty()) {
                if (it.size > 3) {
                    binding.tvSeeAllUPI.visibility = View.GONE
                } else {
                    binding.tvSeeAllUPI.visibility = View.GONE
                }


                binding.llUPIHeader.visibility = View.VISIBLE
                if (!allUPI) {
                    for (i in it.indices) {

                        if (it[i].packageName == "com.google.android.apps.nbu.paisa.user" || it[i].packageName == "com.phonepe.app" || it[i].packageName == "net.one97.paytm") {
                            upiList.add(
                                UPIAppModelClass(
                                    it[i].appName, it[i].packageName, it[i].iconBase64
                                )
                            )
                        }
                    }

                    /*for (i in it.indices) {

                        val item = UPIAppModelClass(
                            it!![i].appName, it[i].packageName, it[i].iconBase64
                        )
                        if (!upiList.contains(item)) {
                            upiList.add(
                                item
                            )
                        }

                    }*/

                    val upiAppAdapter = UPIAppAdapter(upiList, this, this, allUPI)
                    binding.rvUpiApps.apply {
                        adapter = upiAppAdapter
                        hasFixedSize()
                    }

                } else {
                    for (i in it.indices) {

                        allUpiList.add(
                            UPIAppModelClass(
                                it[i].appName, it[i].packageName, it[i].iconBase64
                            )
                        )
                    }


                }


            } else {
                binding.llUPIHeader.visibility = View.GONE
                binding.viewUpi.visibility = View.GONE
            }
        }
    }

    override fun onUPIClick(appName: String, upiPackage: String, logo: String) {

        upiLogo = logo
        upiName = appName
        upiPackageName = upiPackage
        paymentType = UPI
        paymentInstrument = appName
        if (!payByRewards) {
            showBottomSheet = true
            fetchBillSummaryData(upiLogo, appName)
        }
        MparticleUtils.logEvent(
            "CC_Payment_UPI_Clicked",
            "User selects an available UPI method for bill payment",
            "Unique",
            "Continue",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_UPI_Clicked),
            this@PaymentMethodsActivity
        )
    }


    private fun fetchBillSummaryData(localLogo: String? = null, bankName: String? = null) {
        val request = BillSummaryRequest(
            amount, paymentType, PaymentModeInstrument(
                upiPackageName,
                dcBankMasterID,
                ifsc,
                debitCardNetwork,
                DcBin(
                    iin = debitIIN,
                    entity = debitEntity,
                    debitCardNetwork,
                    debitType,
                    debitIssuerCode,
                    debitIssuerName
                ),
                scToken,
                debitBankMasterId
            ), chipUsed, (selectedProduct.size > 1), productItemList, chipUsed > 0
        )

        viewModel?.getBillSummary(request)
        logo = localLogo
        localBankName = bankName
    }

    private fun openAllBankBottomSheet() {
        val bankBottomSheetDialog =
            Utils.showCustomDialogBottum(this, R.layout.bottom_sheet_all_banks)

        val width = (resources.displayMetrics.widthPixels * DEFAULT_PERCENTAGE).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.98).toInt()
        bankBottomSheetDialog.window?.setLayout(width, height)

        bankBottomSheetDialog.setCancelable(false)

        val ivCancel = bankBottomSheetDialog.findViewById<AppCompatImageView>(R.id.ivCancel)
        val tvAmt = bankBottomSheetDialog.findViewById<TextView>(R.id.tvTotalAmountBs)
        val llSearch = bankBottomSheetDialog.findViewById<LinearLayoutCompat>(R.id.llSearch)

        ivCancel?.setOnClickListener {
            bankBottomSheetDialog.dismiss()
        }

        val rvAllBanks = bankBottomSheetDialog.findViewById<RecyclerView>(R.id.rvAllBanks)
        val etSearch = bankBottomSheetDialog.findViewById<EditText>(R.id.et_search)
        rvAllBanks?.layoutManager = LinearLayoutManager(this)

        if (amount != null) {
            tvAmt?.text = "To pay ₹" + Utils.getFormattedDecimal(amount!!.toDouble())
        }

        rvAllBanks.apply {
            adapter = BanksListAdapter(
                this@PaymentMethodsActivity, allNetBankList, this@PaymentMethodsActivity
            )
        }

        etSearch?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocused ->
            if (hasFocused) {
                llSearch?.setBackgroundResource(R.drawable.et_btm_bg)
            } else {
                llSearch?.setBackgroundResource(R.drawable.et_btm_bg_un_focused)
            }
        }
        etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (s != null && s.isEmpty()) {
                    rvAllBanks.adapter = BanksListAdapter(
                        this@PaymentMethodsActivity, allNetBankList, this@PaymentMethodsActivity
                    )


                } else {
                    val tempList: ArrayList<PaymentOptionsResponse.BanksItem> = ArrayList()
                    for (element in allNetBankList) {
                        if (element.originalBankName != null && element.originalBankName.lowercase()
                                .contains(s.toString().lowercase())
                        ) {
                            tempList.add(element)
                        }
                    }

                    if (tempList.isNotEmpty()) {
                        rvAllBanks.adapter = BanksListAdapter(
                            this@PaymentMethodsActivity, tempList, this@PaymentMethodsActivity
                        )
                    }

                    MparticleUtils.logEvent(
                        "CC_Payment_NetBanking_Search_Entered",
                        "User searches from amongst the available Net Banking options using the search option",
                        "Unique",
                        "Input Field",
                        SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_NetBanking_Search_Entered),
                        this@PaymentMethodsActivity
                    )
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        bankBottomSheetDialog.show()
    }

    private fun openUPIListBottomSheet() {
        upiBottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialog)
        upiBottomSheetDialog.setContentView(R.layout.bottom_sheet_upi)
        upiBottomSheetDialog.setCancelable(false)

        val tvTitle = upiBottomSheetDialog.findViewById<AppCompatTextView>(R.id.tvTotalAmountBs)
        val rvAllUPI = upiBottomSheetDialog.findViewById<RecyclerView>(R.id.rvAllBanks)
        val ivClose = upiBottomSheetDialog.findViewById<AppCompatImageView>(R.id.bs_iv_cancel)
        ivClose?.setOnClickListener {
            fetchUpiApp(false)
            upiBottomSheetDialog.dismiss()
        }
        rvAllUPI?.layoutManager = LinearLayoutManager(this)

        tvTitle!!.text = "To pay ₹" + Utils.getFormattedDecimal(amount!!.toDouble())
        allUpiList.sortBy { it.appName }
        rvAllUPI?.apply {
            adapter = UPIAppAdapter(
                allUpiList, this@PaymentMethodsActivity, this@PaymentMethodsActivity, true
            )
        }
        if (upiList.size > 6) {
            upiBottomSheetDialog.behavior.maxHeight = 2000
            upiBottomSheetDialog.behavior.peekHeight = 2000
        }


        upiBottomSheetDialog.show()
    }

    @SuppressLint("SetTextI18n")
    fun openProcessingBottomSheet() {

        if (isCard) {
            MparticleUtils.logCurrentScreen(
                "/cc-payment/loading",
                "The customer awaits the terminal status of the credit card payment on the loading screen",
                "",
                "",
                "",
                "",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_LOADING),
                this
            )
        } else {
            MparticleUtils.logCurrentScreen(
                "/loan-payment/loading",
                "The customer awaits the terminal status of the loan payment on the loading screen",
                "",
                "",
                "",
                "",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.LOAN_Payment_LOADING),
                this
            )
        }



        bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialog)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_common)
        bottomSheetDialog.setCancelable(false)

        val tvTitle = bottomSheetDialog.findViewById<AppCompatTextView>(R.id.bs_tv_title)
        val tvMessage = bottomSheetDialog.findViewById<AppCompatTextView>(R.id.bs_tv_message)
        val tvRetryButton = bottomSheetDialog.findViewById<AppCompatButton>(R.id.bs_btn_retry)
        val ivClose = bottomSheetDialog.findViewById<AppCompatImageView>(R.id.bs_iv_cancel)

        val lottieAnimation =
            bottomSheetDialog.findViewById<LottieAnimationView>(R.id.animationProcessing)
        val animationFailed =
            bottomSheetDialog.findViewById<LottieAnimationView>(R.id.animationFailed)
        val doNotPressClose =
            bottomSheetDialog.findViewById<AppCompatTextView>(R.id.doNotPressClose)
        val llAnimation = bottomSheetDialog.findViewById<LinearLayoutCompat>(R.id.llAnimation)
        val llCreditCard = bottomSheetDialog.findViewById<LinearLayoutCompat>(R.id.llCreditCard)
        tvMessage?.text = getString(R.string.str_wait_payment_processing)
        tvTitle?.text = getString(R.string.str_completing_payment)

        ivClose?.visibility = View.GONE
        llAnimation?.visibility = View.VISIBLE
        doNotPressClose?.visibility = View.VISIBLE
        llCreditCard?.visibility = View.GONE
        tvRetryButton?.visibility = View.GONE
        animationFailed?.visibility = View.GONE
        llCreditCard?.visibility = View.GONE
        var isFirst = true



        lottieAnimation?.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
                try {
                    if (isFirst) {
                        if (paymentType == UPI) {
                            viewModel?.getTXNStatus(startTXNID, razorpayPaymentId, UPI)
                        } else {
                            viewModel?.getTXNStatus(startTXNID, razorpayPaymentId, "")
                        }
                        isFirst = false
                    }
                } catch (ex: Exception) {
                    ex.toString()
                }
            }
        })

        bottomSheetDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    fun openPendingBottomSheet(amount: String?, createdAt: String) {

        if (isCard) {
            MparticleUtils.logCurrentScreen(
                "/cc-payment/pending",
                "The customer is shown a pending screen, while the awaits final confirmation on the payment status",
                "",
                "",
                "",
                "",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Pending),
                this
            )
        } else {
            MparticleUtils.logCurrentScreen(
                "/loan-payment/pending",
                "The customer is shown a pending screen, while the awaits final confirmation on the payment status",
                "",
                "",
                "",
                "",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.LOAN_Payment_Pending),
                this
            )
        }


        pendingBottomSheetDialog =
            Utils.showCustomDialogBottum(this, R.layout.bottom_sheet_payment_pending)

        val bindingSheet = DataBindingUtil.inflate<BottomSheetPaymentPendingBinding>(
            layoutInflater, R.layout.bottom_sheet_payment_pending, null, false
        )

        bindingSheet.tvTxnId.text = startTXNID
        pendingBottomSheetDialog.setContentView(bindingSheet.root)
        pendingBottomSheetDialog.setCancelable(false)
        localRewardPoint = roundupAmount(
            payableAmount.toDouble(),
            SupportedUPIApps.DEFAULT_PERCENTAGE,
            null,
            productItemList[0].id,
            Utils.itemList
        )
        rewardsPoint = MonthlyEarnRule.getLimit(
            rewardsCanAssign,
            rewardsAssignUpto,
            localRewardPoint,
            null,
            null,
        )
        bindingSheet.tvAmount.text = getString(
            R.string.str_payment_awaiting,
            Utils.getFormattedDecimal(payableAmount.toDouble())
        )
        if (selectedProduct.size > 1) {
            bindingSheet.llRewards.visibility = View.GONE
        } else {
            if (rewardsPoint > 0) {
                bindingSheet.llRewards.visibility = View.VISIBLE
                val text =
                    "You will earn <b>$rewardsPoint</b> CheQ Chips if the payment is successfully received!"
                bindingSheet.tvRewards.setAsHtml(text)
            } else {
                bindingSheet.llRewards.visibility = View.GONE
            }
        }
        if (rewardsUsed) {
            bindingSheet.llPayRewards.visibility = View.VISIBLE
        } else {
            bindingSheet.llPayRewards.visibility = View.GONE
        }
        if (payByRewards) {
            bindingSheet.llPayRewards.visibility = View.VISIBLE
            bindingSheet.llBankDetails.visibility = View.GONE
        }
        if (upiName.isNotEmpty()) {
            bindingSheet.tvPaymentModeName.text = upiName
        } else {
            bindingSheet.tvPaymentModeName.text = localBankName
        }
        if (upiLogo.isNotEmpty()) {
            val decodedString: ByteArray = Base64.decode(upiLogo, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            bindingSheet.ivPaymentModeLogo.setImageBitmap(decodedByte)
        } else {
            bindingSheet.ivPaymentModeLogo.loadSvg("${prefs.bankMasterUrl}${logo}-logo.svg")
        }

        bindingSheet.tvDetails.text = getTime(createdAt)

        bindingSheet.btnOkay.setOnClickListener {
            pendingBottomSheetDialog.dismiss()
            startActivity(
                Intent(this, MainActivity::class.java).putExtra(
                    PAYMENT_METHOD, PAYMENT_METHOD
                )
            )
        }
        pendingBottomSheetDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTime(time: String): String {
        if (time.isNotEmpty()) {
            val s = Instant.parse(time).atOffset(ZoneOffset.UTC)
            ZoneId.of("Asia/Kolkata")
            var month = "" + s.month
            month = month.lowercase()
            val formatedMonth: String = month.substring(0, 1).uppercase() + month.substring(1)
            return "${s.dayOfMonth}-${
                formatedMonth.subSequence(
                    0, 3
                )
            } at ${Utils.convertTimeToAmPm("${s.hour}:${s.minute}:${s.second}")}"
        } else {
            return ""
        }

    }

    @SuppressLint("SetTextI18n")
    private fun openAddDebitCardBottomSheet(filled: Boolean) {

        if (isCard) {
            MparticleUtils.logCurrentScreen(
                "/cc-payment/add-debit-card",
                "The customer chooses to add a debit, is asked to enter the card details, incl card number, CVV, Expiry, and Cardholder Name, and is asked to provide consent to secure",
                "",
                "",
                "",
                "",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_DebitCard_AddNew_Clicked),
                this
            )
        } else {
            MparticleUtils.logCurrentScreen(
                "/loan-payment/add-debit-card",
                "The customer chooses to add a debit, is asked to enter the card details, incl card number, CVV, Expiry, and Cardholder Name, and is asked to provide consent to secure",
                "",
                "",
                "",
                "",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.LOAN_Payment_DebitCard_AddNew_Clicked),
                this
            )
        }


        upiLogo = ""
        upiName = ""
        val bottomSheetDialogAddDebitCard =
            Utils.showCustomDialogBottum(this, R.layout.bottom_sheet_add_debit_card)

        bottomSheetDialogAddDebitCard.setCancelable(false)
        bottomSheetDialogAddDebitCard.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        val ivClose = bottomSheetDialogAddDebitCard.findViewById<AppCompatImageView>(R.id.ivClose)
        val tvAmount = bottomSheetDialogAddDebitCard.findViewById<AppCompatTextView>(R.id.tvAmount)
        val tvLearnMore =
            bottomSheetDialogAddDebitCard.findViewById<AppCompatTextView>(R.id.tvLearnMore)
        val etCardNo = bottomSheetDialogAddDebitCard.findViewById<CopyEditText>(R.id.etCardNo)
        val etCardExpiry = bottomSheetDialogAddDebitCard.findViewById<EditText>(R.id.etCardExpiry)
        val etCvv = bottomSheetDialogAddDebitCard.findViewById<EditText>(R.id.etCvv)
        val etName = bottomSheetDialogAddDebitCard.findViewById<EditText>(R.id.etName)
        val llCardNumberBtm =
            bottomSheetDialogAddDebitCard.findViewById<LinearLayoutCompat>(R.id.llCardNumberBtm)
        val llCardExpiryBtm =
            bottomSheetDialogAddDebitCard.findViewById<LinearLayoutCompat>(R.id.llCardExpiryBtm)
        val llCvvBtm = bottomSheetDialogAddDebitCard.findViewById<LinearLayoutCompat>(R.id.llCvvBtm)
        val llNameBtm =
            bottomSheetDialogAddDebitCard.findViewById<LinearLayoutCompat>(R.id.llNameBtm)
        llError = bottomSheetDialogAddDebitCard.findViewById(R.id.llError)
        tvError = bottomSheetDialogAddDebitCard.findViewById(R.id.tv_error)

        val cbSecure = bottomSheetDialogAddDebitCard.findViewById<AppCompatCheckBox>(R.id.cbSecure)
        cbSecure.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                isSecure = true
                MparticleUtils.logEvent(
                    "CC_Payment_DebitCard_Checkbox_Selected",
                    "User selects the checkbox for securing the debit card",
                    "Unique",
                    "Checkbox",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_DebitCard_Checkbox_Selected),
                    this@PaymentMethodsActivity
                )
            } else {
                isSecure = false
                MparticleUtils.logEvent(
                    "CC_Payment_DebitCard_Checkbox_Deselected",
                    "User deselects the checkbox for securing the debit card",
                    "Unique",
                    "Checkbox",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_DebitCard_Checkbox_Deselected),
                    this@PaymentMethodsActivity
                )
            }
        }
        if (filled) {

            val formattedText = debitCardNumber.replace("  ", "").chunked(4).joinToString("  ")
            etCardNo.setText("" + formattedText)
            etCardExpiry.setText("$debitCardExpiryMonth/$debitCardExpiryYear")
            etCvv.setText(debitCardCvv)
            etName.setText(debitCardHolderName)

        } else {
            isDebitCardValid = false
            isNameValid = false
            isCVVFilled = false
            isDebitCard.set(false)
        }

        etCardNo.onFocusChangeListener = onFocused(llCardNumberBtm)
        etCvv.onFocusChangeListener = onFocusedCVV(llCvvBtm)
        etName.onFocusChangeListener = onFocused(llNameBtm)
        etCardExpiry.onFocusChangeListener = onFocusedExpiry(llCardExpiryBtm)
        btnProceedToPay = bottomSheetDialogAddDebitCard.findViewById(R.id.btnProceedToPay)

        ivClose?.setOnClickListener {
            MparticleUtils.logEvent(
                "CC_Payment_DebitCard_AddNew_BackClicked",
                "User cancels the add new debit card flow by closing the view",
                "Unique",
                "Back",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_DebitCard_AddNew_BackClicked),

                this@PaymentMethodsActivity
            )
            bottomSheetDialogAddDebitCard.dismiss()
        }


        etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 != null) {
                    isNameValid = p0.toString().length > 3
                    enableButton()
                    MparticleUtils.logEvent(
                        "CC_Payment_DebitCard_Name_Entered",
                        "User enters the cardholder name for the debit card",
                        "Unique",
                        "Input Field",
                        SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_DebitCard_Name_Entered),
                        this@PaymentMethodsActivity
                    )
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })


        tvAmount?.text = "To pay ₹${
            amount?.toDouble()?.minus(amountForRewards)?.let { Utils.getFormattedDecimal(it) }
        }"

        etCardNo.addListener(object : GoEditTextListener {
            override fun onUpdate() {

                debitCardNumber = etCardNo.text.toString().replace(" ", "")
                if (debitCardNumber.length == 16) {

                    validateCardWhenCopyPaste(debitCardNumber.substring(0, 6))
                }

            }
        })
        etCardNo?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {


            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().length < 6) {
                    llError.visibility = View.GONE
                }
                if (p0 != null) {
                    debitCardNumber = p0.toString().replace(" ", "")
                    validateCard()
                }
            }


            override fun afterTextChanged(p0: Editable?) {
                val formattedText = p0.toString().replace("  ", "").chunked(4).joinToString("  ")
                if (formattedText != p0.toString()) {
                    etCardNo.setText(formattedText)
                    etCardNo.setSelection(etCardNo.length())
                }
            }

        })


        var isSlashAdded = false
        var isMonthValid = true

        etCardExpiry?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                enableButton()
                try {
                    isCardExpiryFilled = false
                    val str: String = p0.toString()
                    val textLength: Int = etCardExpiry.text.toString().length

                    if (textLength == 1 && str.toInt() > 1 && str.toInt() < 10) {
                        etCardExpiry.setText("0" + etCardExpiry.text.toString())
                        etCardExpiry.setSelection(etCardExpiry.text.length)
                    }

                    if (etCardExpiry.text.toString().length == 2) {

                        println("EXECUTED ++++++++++ " + CardExpiryUtils.checkMonthValidity(str.toInt()))

                        if (!CardExpiryUtils.checkMonthValidity(str.toInt())) {
                            isMonthValid = false
                            Toast.makeText(
                                this@PaymentMethodsActivity,
                                "Invalid expiry date",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            isMonthValid = true

                        }
                    }

                    if (textLength == 2) {
                        if (!isSlashAdded) {
                            etCardExpiry.setText(etCardExpiry.text.toString() + "/")
                            etCardExpiry.setSelection(etCardExpiry.text.length)
                            isSlashAdded = true
                        } else {
                            isSlashAdded = false
                        }
                    }

                    if (textLength == 3) {
                        if (!str.contains("/")) {
                            etCardExpiry.setText(
                                StringBuilder(
                                    etCardExpiry.text.toString()
                                ).insert(str.length - 1, "/").toString()
                            )
                            etCardExpiry.setSelection(etCardExpiry.text.length)
                        }
                    }


                    if (textLength == 5 && isMonthValid) {
                        val index = str.indexOf('/')
                        val month = str.substring(0, index)
                        val year = str.substring(index + 1)
                        if (!CardExpiryUtils.isValid(month, year)) {
                            Toast.makeText(
                                this@PaymentMethodsActivity,
                                "Invalid expiry date",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            etCvv.requestFocus()
                            date = str
                            isCardExpiryFilled = true
                        }
                        enableButton()
                    } else {
                        isCardExpiryFilled = false
                        enableButton()
                    }

                } catch (e: Exception) {

                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        etCardNo.requestFocus()

        etCardExpiry.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (etCardExpiry.text.length == 2) {
                    etCardExpiry.setText(etCardExpiry.text.toString()[0].toString())
                    etCardExpiry.setSelection(etCardExpiry.text.length)
                }
            }
            false
        }
        etCvv?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().length == 3) {
                    isCVVFilled = true
                    cvv = p0.toString()
                } else {
                    isCVVFilled = false
                }
                enableButton()
                MparticleUtils.logEvent(
                    "CC_Payment_DebitCard_Expiry_Entered",
                    "User enters the month and year of expiry for the debit card",
                    "Unique",
                    "Input Field",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_DebitCard_CVV_Entered),
                    this@PaymentMethodsActivity
                )
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
        etCvv.transformationMethod = AsteriskTransformationMethod()
        btnProceedToPay?.setOnClickListener {

            debitCardCvv = etCvv.text.toString()
            debitCardExpiryMonth = etCardExpiry.text.take(2).toString()
            debitCardExpiryYear = etCardExpiry.text.takeLast(2).toString()
            debitCardHolderName = etName.text.toString()
            paymentType = DEBIT_CARD
            paymentInstrument = "$debitIssuerName Card"
            showBottomSheet = true
            fetchBillSummaryData(debitLogo, debitIssuerName)
            bottomSheetDialogAddDebitCard.dismiss()
            Utils.hideKeyBoard()

            MparticleUtils.logEvent(
                "CC_Payment_DebitCard_AddNew_Clicked",
                "User opts to add a new debit card for bill payment",
                "Unique",
                "Continue",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_DebitCard_AddNew_Clicked),
                this@PaymentMethodsActivity
            )

        }
        tvLearnMore.setOnClickListener {
            openLearnMoreBottomSheet()
        }
        bottomSheetDialogAddDebitCard.show()

    }

    private fun openLearnMoreBottomSheet() {
        MparticleUtils.logEvent(
            "CC_Payment_DebitCard_KnowMore_Clicked",
            "User clicks on Know More to learn more about securing the card",
            "Unique",
            "Continue",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_DebitCard_KnowMore_Clicked),
            this@PaymentMethodsActivity
        )
        val bottomSheetDialog = BottomSheetDialog(this)

        bottomSheetDialog.setContentView(R.layout.bottom_sheet_rbi_guidelines)
        bottomSheetDialog.setCancelable(false)

        val ivClose = bottomSheetDialog.findViewById<AppCompatImageView>(R.id.ivClose)
        val tvRbiGuidelines =
            bottomSheetDialog.findViewById<AppCompatTextView>(R.id.tvRbiGuidelines)
        tvRbiGuidelines?.setOnClickListener {
            startActivity(
                Intent(this, CommonWebViewActivity::class.java).putExtra(
                    "URL", "https://m.rbi.org.in/scripts/FAQView.aspx?Id=129"
                )
            )
        }


        ivClose?.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        Utils.hideKeyboard(this)
        bottomSheetDialog.show()
    }

    private fun validateCard() {

        if (debitCardNumber.length == 6) {
            productActivationViewModel?.fetchDebitCardDetails(debitCardNumber)
        } else if (debitCardNumber.length == 16 && (!CreditCardUtils.check(debitCardNumber) || !isDebitCard.get())) {
            isDebitCardValid = false
            llError.visibility = View.VISIBLE
        } else {
            isDebitCardValid =
                debitCardNumber.length == 16 && CreditCardUtils.check(debitCardNumber) && isDebitCard.get()

        }
        enableButton()
    }

    private fun validateCardWhenCopyPaste(copyDebitcard: String) {


        productActivationViewModel?.fetchDebitCardDetails(copyDebitcard)

        isDebitCard.addOnPropertyChangedCallback(object : OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                isDebitCardValid = isDebitCard.get() && CreditCardUtils.check(debitCardNumber)
                enableButton()
                if (!isDebitCardValid) {
                    llError.visibility = View.VISIBLE
                }
            }

        })
    }

    internal fun enableButton() {
        btnProceedToPay?.isEnabled = isDebitCardValid && isCardExpiryFilled && isCVVFilled
    }

    private fun onFocused(view: View): View.OnFocusChangeListener {
        return View.OnFocusChangeListener { _, hasFocused ->
            if (hasFocused) {
                view.setBackgroundResource(R.drawable.et_btm_bg)

            } else {
                view.setBackgroundResource(R.drawable.et_btm_bg_un_focused)

            }
        }
    }

    private fun onFocusedExpiry(view: View): View.OnFocusChangeListener {
        return View.OnFocusChangeListener { _, hasFocused ->
            if (hasFocused) {
                view.setBackgroundResource(R.drawable.et_btm_bg_focused_expiry)

            } else {
                view.setBackgroundResource(R.drawable.et_btm_bg_un_focused_expiry)

            }
        }
    }

    private fun onFocusedCVV(view: View): View.OnFocusChangeListener {
        return View.OnFocusChangeListener { _, hasFocused ->
            if (hasFocused) {
                view.setBackgroundResource(R.drawable.ic_cvv_btm_focused)
            } else {
                view.setBackgroundResource(R.drawable.ic_cvv_btm_un_focused)

            }
        }
    }


    override fun onPreferredMethodClicked(
        preferredMethod: String,
        id: String,
        logo: String,
        originalBankName: String,
        ifscPrefix: String,
        lastFour: String,
        cvv: String,
        bankingName: String,
        dcToken: String,
        bankMasterId: String
    ) {
        upiLogo = ""
        upiName = ""
        debitCardCvv = cvv
        ifsc = ifscPrefix
        scToken = dcToken
        scBankName = bankingName
        dcBankMasterID = bankMasterId
        if (!payByRewards) {
            showBottomSheet = true
            paymentType = preferredMethod
            paymentInstrument = originalBankName
            fetchBillSummaryData(logo, originalBankName)
        }

    }

    private fun startPayment(upiPackageName: String) {


        when (paymentType) {
            DEBIT_CARD -> {
                MparticleUtils.logCurrentScreen(
                    "/cc-payment/review-details",
                    "The customer is displayed the amount and payment method selections for review",
                    "pay-method",
                    "debit-card",
                    "",
                    "",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Review_Detail),
                    this@PaymentMethodsActivity
                )
                viewModel?.startPayment(
                    StartPaymentRequestNew(
                        rewardsUsed,
                        debitCardCvv,
                        debitCardNetwork,
                        upiPackageName,
                        paymentType,
                        chipRemaining,
                        amountForRewards,
                        debitCardExpiryYear,
                        null,
                        selectedProductList,
                        debitCardNumber,
                        debitCardExpiryMonth,
                        amount,
                        debitCardHolderName,
                        (selectedProduct.size > 1),
                        chipUsed,
                        debitBankMasterId,
                        isSecure,
                        "",
                        "",
                        paymentInstrument
                    )
                )
            }

            NET_BANKING -> {
                MparticleUtils.logEvent(
                    "CC_Payment_NetBanking_Clicked",
                    "User selects an available Net Banking method for bill payment",
                    "Unique",
                    "Continue",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_NetBanking_Clicked),
                    this@PaymentMethodsActivity
                )
                viewModel?.startPayment(
                    StartPaymentRequestNew(
                        rewardsUsed,
                        debitCardCvv,
                        debitCardNetwork,
                        upiPackageName,
                        paymentType,
                        chipRemaining,
                        amountForRewards,
                        debitCardExpiryYear,
                        NetBankingObject(
                            ifsc, bankLogo, bankName, dcBankMasterID
                        ),
                        selectedProductList,
                        debitCardNumber,
                        debitCardExpiryMonth,
                        amount,
                        debitCardHolderName,
                        (selectedProduct.size > 1),
                        chipUsed,
                        "",
                        payment_instrument = "$paymentInstrument Net Banking"
                    )
                )
            }

            UPI -> {
                MparticleUtils.logEvent(
                    "CC_Payment_UPI_Clicked",
                    "User selects an available UPI method for bill payment",
                    "Unique",
                    "Continue",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_UPI_Clicked),
                    this@PaymentMethodsActivity
                )
                viewModel?.startPayment(
                    StartPaymentRequestNew(
                        rewardsUsed,
                        "",
                        "",
                        upiPackageName,
                        paymentType,
                        chipRemaining,
                        amountForRewards,
                        "",
                        null,
                        selectedProductList,
                        "",
                        "",
                        amount,
                        "",
                        (selectedProduct.size > 1),
                        chipUsed,
                        "",
                        payment_instrument = paymentInstrument
                    )
                )
            }

            REWARD -> {
                viewModel?.startPayment(
                    StartPaymentRequestNew(
                        rewardsUsed,
                        "",
                        "",
                        upiPackageName,
                        paymentType,
                        chipRemaining,
                        amountForRewards,
                        "",
                        null,
                        selectedProductList,
                        "",
                        "",
                        amount,
                        "",
                        (selectedProduct.size > 1),
                        chipUsed,
                        "",

                        )
                )
            }

            SAVED_CARD -> {

                MparticleUtils.logEvent(
                    "CC_Payment_DebitCard_Clicked",
                    "User opts to select one of the previously saved debit cards",
                    "Unique",
                    "Continue",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_DebitCard_Clicked),
                    this@PaymentMethodsActivity
                )
                viewModel?.startPayment(
                    StartPaymentRequestNew(
                        rewardsUsed,
                        debitCardCvv,
                        debitCardNetwork,
                        upiPackageName,
                        paymentType,
                        chipRemaining,
                        amountForRewards,
                        debitCardExpiryYear,
                        null,
                        selectedProductList,
                        debitCardNumber,
                        debitCardExpiryMonth,
                        amount,
                        debitCardHolderName,
                        (selectedProduct.size > 1),
                        chipUsed,
                        dcBankMasterID,
                        isSecure,
                        scToken,
                        scBankName,
                        payment_instrument = "$paymentInstrument Card"
                    )
                )
            }
        }


    }

    /**
     * handled the success scenario for payment by UPI
     */
    val upiResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (paymentType == UPI) {
            paymentModeName = UPI
            openProcessingBottomSheet()
        }
    }

    override fun onResume() {
        super.onResume()
        Utils.hideProgressDialog()
        if (SharePrefs.instance?.getBoolean(SharePrefsKeys.IS_CARD_ACTIVATE) == true) {
            Utils.hideProgressDialog()
            openProcessingBottomSheet()
            SharePrefs.instance?.putBoolean(SharePrefsKeys.IS_CARD_ACTIVATE, false)
            SharePrefs.instance?.putBoolean(SharePrefsKeys.IS_PAYMENT_FAILED, false)
        }
        if (SharePrefs.instance?.getBoolean(SharePrefsKeys.IS_PAYMENT_FAILED) == true) {
            Utils.hideProgressDialog()
            openProcessingBottomSheet()
            SharePrefs.instance?.putBoolean(SharePrefsKeys.IS_CARD_ACTIVATE, false)
            SharePrefs.instance?.putBoolean(SharePrefsKeys.IS_PAYMENT_FAILED, false)
        }


    }

    fun openPaymentFailedBottomSheet(newTranX: NewTranX?) {

        if (isCard) {
            MparticleUtils.logCurrentScreen(
                "/cc-payment/failed",
                "The customer views the failed status of the credit card payment along with the error description",
                "",
                "",
                "",
                "",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Failed),
                this
            )
        } else {
            MparticleUtils.logCurrentScreen(
                "/loan-payment/failed",
                "The customer views the failed status of the loan payment along with the error description",
                "",
                "",
                "",
                "",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.LOAN_Payment_Failed),
                this
            )
        }
        if (pendingBottomSheetDialog.isShowing) {
            pendingBottomSheetDialog.dismiss()
        }
        val failedDialog = BottomSheetDialog(this, R.style.BottomSheetDialog)
        val bindingSheet = DataBindingUtil.inflate<BottomSheetPaymentFailedBinding>(
            layoutInflater, R.layout.bottom_sheet_payment_failed, null, false
        )
        bindingSheet.tvFailedText.text = newTranX?.user_err_msg ?: getString(R.string.failed_desc)
        failedDialog.setContentView(bindingSheet.root)
        failedDialog.setCancelable(false)
        bindingSheet.btnRetry.setOnClickListener {

            MparticleUtils.logEvent(
                "CC_Payment_Retry",
                "User chooses to retry credit card payment on failure",
                "Unique",
                "Continue",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Retry),
                this@PaymentMethodsActivity
            )
            failedDialog.dismiss()


        }
        bindingSheet.ivHelp.setOnClickListener {
            Utils.createFreshChatUser()
            Freshchat.showConversations(applicationContext)
        }
        bindingSheet.ivCancel.setOnClickListener {

            MparticleUtils.logEvent(
                "CC_Payment_Failed_BackClicked",
                "User chooses to exit the payment flow by clicking cancel on the failed screen",
                "Unique",
                "Back",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Failed_BackClicked),
                this@PaymentMethodsActivity
            )
            failedDialog.dismiss()
            onBackPressed()
        }
        failedDialog.show()
    }

    override fun onBackPressed() {
        if (comingFrom == IntentKeys.CARD_DETAILS) {
            prefs.putBoolean(SharePrefsKeys.IS_BACK_PRESSED, true)
            super.onBackPressed()
        } else {
            super.onBackPressed()
        }
    }


    private fun goToSuccessfulScreen(testModel: NewTranX) {
        startActivity(
            Intent(
                this, BillPaymentSuccessfulActivity::class.java
            ).putExtra(IntentKeys.AMOUNT, amount)
                .putExtra(IntentKeys.PAYMENT_MODE_NAME, localBankName)
                .putExtra(IntentKeys.PAYMENT_MODE_LOGO, logo)
                .putExtra(IntentKeys.TXN_ID, startTXNID)
                .putExtra(IntentKeys.DATE, testModel.txn?.created_at)
                .putExtra(IntentKeys.UPI_LOGO, upiLogo).putExtra(IntentKeys.UPI_NAME, upiName)
                .putExtra(IntentKeys.PRODUCT_LIST, selectedProduct)
                .putExtra(IntentKeys.CHIP_USED, rewardsUsed)
                .putExtra(IntentKeys.PAY_BY_REWARDS, payByRewards)
                .putExtra(IntentKeys.REWARDS_POINT, rewardsPoint)
                .putExtra(IntentKeys.BILL_STATUS, billStatus)
                .putExtra(IntentKeys.PAYOUT_MODE, payOutMode)
                .putExtra(IntentKeys.PRODUCT_TYPE, productType)
                .putExtra(IntentKeys.PAYOUT_NARRATION, payoutNarration)
                .putExtra(IntentKeys.REWARDS_ASSIGN_UPTO, rewardsAssignUpto)
                .putExtra(IntentKeys.REWARDS_CAN_ASSIGN, rewardsCanAssign)
                .putExtra(IntentKeys.REWARDS_ASSIGNED, rewardsAssigned)
        )
        finish()
    }

    override fun payCallBack() {
        startPayment(upiPackageName)
    }

    override fun dismiss() {
        billSummaryFragment.dialog?.dismiss()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }


}