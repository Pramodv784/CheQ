package com.cheq.retail.ui.activateCard

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.constants.AFConstants
import com.cheq.retail.constants.AFConstants.BANK_NAME
import com.cheq.retail.constants.AFConstants.CC_VERIFICATION_ERROR_NAME
import com.cheq.retail.constants.AFConstants.CC_VERIFICATION_SCREEN_NAME
import com.cheq.retail.constants.AFConstants.CUSTOM
import com.cheq.retail.constants.AFConstants.FULL
import com.cheq.retail.constants.AFConstants.IDENTIFIED_CARD
import com.cheq.retail.constants.AFConstants.LAST_FOUR
import com.cheq.retail.constants.AFConstants.MANUAL_CARD
import com.cheq.retail.constants.AFConstants.MIN
import com.cheq.retail.constants.AFConstants.SCREEN_TYPE
import com.cheq.retail.databinding.*
import com.cheq.retail.extensions.bankMasterUrl
import com.cheq.retail.extensions.faqsBaseUrl
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.accountSettings.webView.CommonWebViewActivity
import com.cheq.retail.ui.activateCard.model.AddCardRequest
import com.cheq.retail.ui.activateCard.model.BinCheckBodyRequest
import com.cheq.retail.ui.activateCard.model.CardDetailTypeResponse
import com.cheq.retail.ui.activateCard.model.GetProductByIdRequest
import com.cheq.retail.ui.activateCard.viewModel.ProductActivationViewModel
import com.cheq.retail.ui.billPayments.PaymentMethodsActivity
import com.cheq.retail.ui.billPayments.PaymentMethodsActivity.SupportedUPIApps.Companion.DEFAULT_PERCENTAGE
import com.cheq.retail.ui.login.modelv2.productv1.ProductV2
import com.cheq.retail.ui.login.modelv2.productv1.UI
import com.cheq.retail.ui.main.MainActivity
import com.cheq.retail.ui.main.fragment.ProductFragment
import com.cheq.retail.ui.main.fragment.ProductFragment.Companion.ACTIVITY_INTENT_CODE
import com.cheq.retail.ui.main.helper.MonthlyEarnRule
import com.cheq.retail.utils.*
import com.cheq.retail.utils.ImageUtils.loadSvg
import com.cheq.retail.utils.Utils.Companion.roundupAmount
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class CardDetailsActivityNew : BaseActivity() {

    companion object {
        fun getStartIntent(context: Context, productId: String, lastFour: String): Intent {
            return Intent(
                context, CardDetailsActivityNew::class.java
            ).putExtra(IntentKeys.PRODUCT_ID, productId).putExtra(IntentKeys.LAST_FOUR, lastFour)

        }

        const val MASTER_CARD = "MasterCard"
        const val VISA = "Visa"
        const val RUPAY = "RuPay"
        const val DINERS_CLUB = "Diners Club"
        const val AMERICAN_EXPRESS = "American Express"
    }

    var isSecured = false
    lateinit var mBinding: ActivityCardDetailsNewBinding
    var prefs: SharePrefs? = null
    private var mFirebaseDatabase: DatabaseReference? = null
    private var mFirebaseInstance: FirebaseDatabase? = null
    var viewModel: ProductActivationViewModel? = null
    var isCardDetailsFocused = ObservableBoolean(false)
    var isDetailsFilled = ObservableBoolean(false)
    var isCVVFocused = ObservableBoolean(false)
    var isEditNameFocused = ObservableBoolean(false)
    var lastFour: String = ""
    var cvv: String = ""
    var name: String = ""
    var date: String = ""
    var isCardValid = ObservableBoolean(false)
    var isCreditCard = ObservableBoolean(false)
    var isFront = true
    var network: String = ""
    var type: String = ""
    var subType: String = ""
    var issuerCode: String = ""
    var issuerName: String = ""
    var NEW_ID = ""
    var BIN_CHECK_ID = ""
    var international: Boolean = false
    var isCardExpiryFilled = ObservableBoolean(false)
    var isCVVFilled = ObservableBoolean(false)
    var progress = ObservableBoolean(false)
    var productId = ""
    var txnId = ""
    var isExisting = false
    var productBankMasterId = ""
    var binBankMasterId = ""
    var logo = ""
    var cardNumber: String = ""
    var isActivateFlow: Boolean = false
    var orderId: String = ""
    var email = ""
    var razorpayPaymentId = ""
    var startColor = ""
    var endColor = ""
    var middleColor = ""
    var cardTypeCC = ""
    var cardDetailResponse: CardDetailTypeResponse? = null
    private lateinit var bottomSheetDialog: BottomSheetDialog
    var existingProductList: ArrayList<ProductV2> = ArrayList()
    var accountNumberNew = ""
    var cardTypeNew = ""
    var amountNew = ""
    var bankNameNew = ""
    var bankNameLocal = ""
    var billIdNew = ""
    var bankLogoNew = ""
    var isComingFrom = false
    private val blockCharacterSet = "~#^|$%&*!"
    var isFromHome = ""
    var isForActivate = false
    var isCardFocused = ObservableBoolean(false)
    var callStatus: Boolean = true
    var failedSecureStatus: Boolean = false
    var ActiveAndPay = false
    var showCustomPayment = false
    var bankId = ""
    var billStatus = ""
    var rewardsPoint = 0
    var verificationSupported = false
    var selectedProduct: ArrayList<ProductV2> = ArrayList()
    private var rewardsCanAssign = 0
    private var rewardsAssigned = 0
    private var rewardsAssignUpto = 0
    private var productItem: ProductV2? = null
    var ui_config: UI? = null
    var enteredfullcardNumber = false
    private lateinit var totalDueDialog: Dialog
    private lateinit var customPayDialog: Dialog

    private var amount: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        mFirebaseInstance =
            FirebaseDatabase.getInstance(com.cheq.retail.BuildConfig.firebaseDataBaseURL)
        mFirebaseDatabase = mFirebaseInstance?.reference
        catchIntent()
        setupViewModel()
        setupObserver()
        viewModel?.getDashboardData()


    }

    private fun catchIntent() {
        productId = intent.getStringExtra(IntentKeys.PRODUCT_ID) ?: ""
        productBankMasterId = intent.getStringExtra(IntentKeys.NEW_ID) ?: ""
        isExisting = intent.getBooleanExtra(IntentKeys.EXISTING, false)
        ActiveAndPay = intent.getBooleanExtra(IntentKeys.ACTIVE_AND_PAY, false)
        lastFour = intent.getStringExtra(IntentKeys.LAST_FOUR).toString().takeLast(2)
        rewardsAssignUpto = intent.getIntExtra(IntentKeys.REWARDS_ASSIGN_UPTO, 0)
        rewardsCanAssign = intent.getIntExtra(IntentKeys.REWARDS_CAN_ASSIGN, 0)
        rewardsAssigned = intent.getIntExtra(IntentKeys.REWARDS_ASSIGNED, 0)
        if (intent.getStringExtra(IntentKeys.LAST_FOUR).toString() == "XXXX") {
            lastFour = intent.getStringExtra(IntentKeys.LAST_FOUR).toString()
        } else if (intent.getStringExtra(IntentKeys.LAST_FOUR).toString().take(2) == "XX") {
            lastFour = intent.getStringExtra(IntentKeys.LAST_FOUR).toString().takeLast(2)
        } else {
            lastFour = intent.getStringExtra(IntentKeys.LAST_FOUR).toString()
        }
        isFromHome = intent.getStringExtra(IntentKeys.IS_FROM_HOME).toString()
        showCustomPayment = intent.getBooleanExtra(IntentKeys.SHOW_CUSTOM_PAYMENT, false)
        if (ActiveAndPay) {
            amount = intent.getStringExtra(IntentKeys.TOTAL_AMOUNT)
        }
        if (intent.getSerializableExtra(IntentKeys.PRODUCT_ITEM) != null) {
            productItem = intent.getSerializableExtra(IntentKeys.PRODUCT_ITEM) as ProductV2

        }
        if (productItem?.bill != null) {
            isComingFrom = true
        }
        isActivateFlow = when {
            lastFour.lowercase() == "XXXX" -> {
                false
            }
            lastFour.isNotEmpty() && lastFour != "null" -> {
                mBinding.tvLastCardNumber.visibility = View.VISIBLE
                mBinding.tvLastCardNumber.text = "··· $lastFour"
                setClickOfEt(lastFour)
                true
            }
            else -> {
                false
            }
        }
        if (intent.getStringExtra(IntentKeys.BANK_NAME) != null) {

            setCardInput(
                intent.getStringExtra(IntentKeys.BANK_NAME).toString(),
                intent.getStringExtra(IntentKeys.BANK_LOGO).toString(),
                ""
            )

        }

    }

    private fun setupObserver() {
        viewModel?.cardTypeObserver?.observe(this) {
            try {

                if (it.status == true) {
                    verificationSupported = it.verification_supported ?: false
                    type = it?.iin?.network.toString()
                    if (productItem?.card_network.isNullOrEmpty()) {
                        productItem?.card_network = it?.iin?.network ?: ""
                    }
                    BIN_CHECK_ID = it.bank?.id.toString()
                    mBinding.tvError.visibility = View.GONE
                    when {
                        it.iin != null && it.status == true -> {
                            cardDetailResponse = it
                            isCreditCard.set(true)
                            mBinding.tvError.visibility = View.GONE
                            network = it.iin.network.toString()
                            type = it.iin.type.toString()
                            ui_config = it.bank?.ui_config
                            setBackground()
                            cardTypeCC = "Other"
                            mBinding.etCvv.filters = arrayOf<InputFilter>(
                                InputFilter.LengthFilter(
                                    3
                                )
                            )
                            mBinding.etCvv.hint = "\u2217" + "\u2217" + "\u2217"
                            issuerCode = it.iin.issuerCode.toString()
                            issuerName = it.bank?.bankName.toString()
                            binBankMasterId = it.bank?.id.toString()
                            logo = it.bank?.logoWithName.toString()

                            MparticleUtils.logCurrentScreen(
                                "/cc-verification/expiry-and-cvv",
                                "The customer enters or confirms other card details required to validate the card, including name, CVV, and card expiry",
                                "card-network",
                                it.iin.network.toString(),
                                "",
                                "",
                                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Activation_Other_Card_Detail),
                                this@CardDetailsActivityNew
                            )

                            MparticleUtils.logCurrentScreen(
                                "/cc-verification/expiry-and-cvv",
                                "The customer enters or confirms other card details required to validate the card, including name, CVV, and card expiry",
                                "bank-name",
                                it.bank.toString(),
                                "",
                                "",
                                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Activation_Other_Card_Detail),
                                this@CardDetailsActivityNew
                            )

                            MparticleUtils.logCurrentScreen(
                                "/cc-verification/expiry-and-cvv",
                                "The customer enters or confirms other card details required to validate the card, including name, CVV, and card expiry",
                                "card-network",
                                it.iin.network.toString(),
                                "",
                                "",
                                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Activation_Other_Card_Detail),
                                this@CardDetailsActivityNew
                            )

                            setCardInput(
                                it?.bank?.bankName.toString(),
                                it?.bank?.id ?: "",
                                it?.iin.network.toString()
                            )
                            val colorMatrix = ColorMatrix()
                            colorMatrix.setSaturation(1f)
                            mBinding.ivLogo.colorFilter = ColorMatrixColorFilter(colorMatrix);

                            if (enteredfullcardNumber) {
                                enteredfullcardNumber = false
                                backcardView()
                            }

                            if (it.iin.network.equals(AMERICAN_EXPRESS)) {
                                cardTypeCC = AMERICAN_EXPRESS
                                mBinding.etCvv.filters =
                                    arrayOf<InputFilter>(InputFilter.LengthFilter(4))
                                mBinding.etCvv.hint = "\u2217" + "\u2217" + "\u2217" + "\u2217"
                            } else if (it.iin.network.equals(DINERS_CLUB)) {

                                cardTypeCC = DINERS_CLUB
                                mBinding.etCvv.filters =
                                    arrayOf<InputFilter>(InputFilter.LengthFilter(3))
                                mBinding.etCvv.hint = "\u2217" + "\u2217" + "\u2217"
                            } else {
                                cardTypeCC = "Other"
                                mBinding.etCvv.filters = arrayOf<InputFilter>(
                                    InputFilter.LengthFilter(
                                        3
                                    )
                                )
                            }
                        }
                        it.iin == null -> {
                            mBinding.tvError.visibility = View.VISIBLE
                            isCreditCard.set(false)
                            setBackground()
                        }
                        it.iin.type.equals("debit", true) -> {
                            isCreditCard.set(false)
                            setBackground()
                            mBinding.tvError.visibility = View.VISIBLE
                            mBinding.tvError.text = getString(R.string.str_debit_card_not_supported)
                        }

                    }

                } else {

                    mBinding.tvError.isVisible = mBinding
                        .etCardView.rawText.trim().isNotEmpty()
                            && (it.userErrorMessage ?: it.message)?.isNotEmpty() == true

                    mBinding.tvError.text = it.userErrorMessage ?: it.message
                    verificationSupported = it.verification_supported ?: false
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        viewModel?.dashboardObserver?.observe(this) {
            if (it?.products != null) {
                existingProductList = it.products
            }
        }

        viewModel?.statusObserver?.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        viewModel?.productByIdObserver?.observe(this) {
            if (it?.status != null && it.status ) {
                bottomSheetDialog.dismiss()
                openSucessfullBottomSheet(cardNumber.takeLast(4))
            } else {
                bottomSheetDialog.dismiss()

                openFailedBottomSheet(it.userErrorMessage?:getString(R.string.check_card_detail))
                if (::totalDueDialog.isInitialized && totalDueDialog.isShowing) {
                    totalDueDialog.dismiss()
                }
                if (::customPayDialog.isInitialized && customPayDialog.isShowing) {
                    customPayDialog.dismiss()
                }
            }


        }

        viewModel?.addcardObserver?.observe(this) {
            Utils.hideProgressDialog()
            if (it != null) {
                productId = it?.productId ?: ""
                txnId = it?.txn_id ?: ""
                if (it.status == true) {
                    if (it.txn_completed != true) {
                        var urlString = Utils.decodeString(it.paymentLink.toString())
                        startActivity(Intent(this, WebViewActivity::class.java).apply {
                            putExtra("url", urlString)
                        })
                    } else {
                        if (selectedProduct.size == 0) {
                            openSucessfullBottomSheet(cardNumber.takeLast(4))
                        } else {
                            goToPaymentScreen(
                                accountNumberNew,
                                cardTypeNew,
                                amountNew.replace(",", ""),
                                bankNameNew,
                                billIdNew,
                                productId,
                                bankLogoNew,
                                startColor,
                                middleColor,
                                endColor,
                                lastFour ?: "",
                                ""
                            )
                        }
                    }

                } else {
                    Utils.hideProgressDialog()
                    Toast.makeText(
                        this,
                        it.userErrorMessage ?: it.apiMessage?:getString(R.string.some_thing_went_wrong),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }

        viewModel!!.progressObserver.observe(this) {
            if (it) {
                Utils.getUserClickBehaviour(false, this)
                progress.set(true)
            } else {
                progress.set(false)
                Utils.getUserClickBehaviour(true, this)
            }
        }
    }

    private fun addErrorEvent() {
        val properties: HashMap<String, String> = HashMap()
        properties["errormessage"] = mBinding.tvError.text.toString()
        MparticleUtils.logCardErrorScreen(CC_VERIFICATION_ERROR_NAME, properties)
    }

    fun setCardInput(name: String, image: String, network: String) {
        var imageUrl = "${prefs?.bankMasterUrl}${image}-logo-with-name.svg"

        var cardInput = mBinding.etCardView.rawText.toString()
        if (intent.getStringExtra("bankName") == null) {
            when {
                network.equals("visa", true) -> {
                    mBinding.etCardView.mask = "0000 0000 0000 0000"
                    mBinding.etCardView.setText(cardInput)
                    mBinding.ivCardType.setImageDrawable(
                        AppCompatResources.getDrawable(
                            this, R.drawable.visa
                        )
                    )
                    mBinding.ivCardType2.setImageDrawable(
                        AppCompatResources.getDrawable(
                            this, R.drawable.visa
                        )
                    )

                }
                network.equals(RUPAY, true) -> {
                    mBinding.etCardView.mask = "0000 0000 0000 0000"
                    mBinding.etCardView.setText(cardInput)
                    mBinding.ivCardType.setImageDrawable(
                        AppCompatResources.getDrawable(
                            this, R.drawable.ic_rupay_card_icon
                        )
                    )
                    mBinding.ivCardType2.setImageDrawable(
                        AppCompatResources.getDrawable(
                            this, R.drawable.ic_rupay_card_icon
                        )
                    )

                }
                network.equals("mastercard", true) -> {
                    mBinding.etCardView.mask = "0000 0000 0000 0000"
                    mBinding.etCardView.setText(cardInput)
                    mBinding.ivCardType.setImageDrawable(
                        AppCompatResources.getDrawable(
                            this, R.drawable.ic_mastercard
                        )
                    )
                    mBinding.ivCardType2.setImageDrawable(
                        AppCompatResources.getDrawable(
                            this, R.drawable.ic_mastercard
                        )
                    )
                }
                network.equals(DINERS_CLUB, true) -> {
                    mBinding.etCardView.mask = "0000 000000 0000"
                    mBinding.etCardView.setText(cardInput)
                    mBinding.ivCardType.setImageDrawable(
                        AppCompatResources.getDrawable(
                            this, R.drawable.ic_diners
                        )
                    )
                    mBinding.ivCardType2.setImageDrawable(
                        AppCompatResources.getDrawable(
                            this, R.drawable.ic_diners
                        )
                    )

                }
                network.equals(AMERICAN_EXPRESS, true) -> {
                    mBinding.etCardView.mask = "0000 000000 00000"
                    mBinding.etCardView.setText(cardInput)
                    mBinding.ivCardType.setImageDrawable(
                        AppCompatResources.getDrawable(
                            this, R.drawable.ic_amex
                        )
                    )
                    mBinding.ivCardType2.setImageDrawable(
                        AppCompatResources.getDrawable(
                            this, R.drawable.ic_amex
                        )
                    )
                }
            }
        } else {
            // mBinding.etCardView.hint = "0000000000000000"
            when {
                network.equals("visa", true) -> {
                    if (lastFour != "XXXX") {
                        val firstTwo = lastFour.take(2)
                        println("firstTwo $firstTwo")

                        if (lastFour.length == 4) {
                            mBinding.tvCardfour.visibility = View.VISIBLE
                            mBinding.tvCardfour.text = lastFour
                            mBinding.etCardView.mask = "0000 0000 0000"
                            mBinding.etCardView.hint = "000000000000000"


                        } else {
                            mBinding.tvCardfour.visibility = View.VISIBLE
                            mBinding.etCardView.mask = "0000 0000 0000 00"
                            mBinding.tvCardfour.text = lastFour
                        }

                    } else {
                        mBinding.etCardView.mask = "0000 0000 0000 0000"
                    }
                    mBinding.etCardView.setText(cardInput)
                    mBinding.ivCardType.setImageDrawable(
                        AppCompatResources.getDrawable(
                            this, R.drawable.visa
                        )
                    )
                    mBinding.ivCardType2.setImageDrawable(
                        AppCompatResources.getDrawable(
                            this, R.drawable.visa
                        )
                    )

                }
                network.equals(RUPAY, true) -> {
                    if (lastFour != "XXXX") {
                        if (lastFour.length == 4) {
                            mBinding.tvCardfour.visibility = View.VISIBLE
                            mBinding.tvCardfour.text = lastFour
                            mBinding.etCardView.mask = "0000 0000 0000"
                            mBinding.etCardView.hint = "000000000000000"
                        } else {
                            mBinding.tvCardfour.visibility = View.VISIBLE
                            mBinding.etCardView.mask = "0000 0000 0000 00"
                            mBinding.tvCardfour.text = lastFour
                        }

                    } else {
                        mBinding.etCardView.mask = "0000 0000 0000 0000"
                    }
                    mBinding.etCardView.setText(cardInput)
                    mBinding.ivCardType.setImageDrawable(
                        AppCompatResources.getDrawable(
                            this, R.drawable.ic_rupay_card_icon
                        )
                    )
                    mBinding.ivCardType2.setImageDrawable(
                        AppCompatResources.getDrawable(
                            this, R.drawable.ic_rupay_card_icon
                        )
                    )
                }
                network.equals("mastercard", true) -> {
                    if (lastFour != "XXXX") {

                        if (lastFour.length == 4) {
                            mBinding.tvCardfour.visibility = View.VISIBLE
                            mBinding.tvCardfour.text = lastFour
                            mBinding.etCardView.mask = "0000 0000 0000"
                            mBinding.etCardView.hint = "000000000000000"
                        } else {
                            mBinding.tvCardfour.visibility = View.VISIBLE
                            mBinding.etCardView.mask = "0000 0000 0000 00"
                            mBinding.tvCardfour.text = lastFour
                        }

                    } else {
                        mBinding.etCardView.mask = "0000 0000 0000 0000"
                    }
                    mBinding.etCardView.setText(cardInput)
                    mBinding.ivCardType.setImageDrawable(getDrawable(R.drawable.ic_mastercard))
                    mBinding.ivCardType2.setImageDrawable(getDrawable(R.drawable.ic_mastercard))
                }
                network.equals(DINERS_CLUB, true) -> {
                    if (lastFour != "XXXX") {

                        if (lastFour.length == 4) {
                            mBinding.tvCardfour.visibility = View.VISIBLE
                            mBinding.tvCardfour.text = lastFour

                            mBinding.etCardView.mask = "0000 000000"
                            mBinding.etCardView.hint = "000000000000"

                            /* (mBinding.tvCardfour.layoutParams as ConstraintLayout.LayoutParams)
                                 .apply {
                                     marginStart=-30
                                 }*/
                        } else {
                            mBinding.tvCardfour.visibility = View.VISIBLE
                            mBinding.etCardView.mask = "0000 000000 00"
                            mBinding.tvCardfour.text = lastFour
                        }
                    } else {
                        mBinding.etCardView.mask = "\"0000 000000 0000"
                    }
                    mBinding.etCardView.setText(cardInput)
                    mBinding.ivCardType.setImageDrawable(
                        AppCompatResources.getDrawable(
                            this, R.drawable.ic_diners
                        )
                    )
                    mBinding.ivCardType2.setImageDrawable(
                        AppCompatResources.getDrawable(
                            this, R.drawable.ic_diners
                        )
                    )
                }
                network.equals(AMERICAN_EXPRESS, true) -> {
                    if (lastFour != "XXXX") {
                        if (lastFour.length == 4) {
                            mBinding.tvCardfour.visibility = View.VISIBLE
                            mBinding.tvCardfour.text = lastFour
                            mBinding.etCardView.mask = "0000 000000 0"
                            mBinding.etCardView.hint = "00000000000"
                        } else {
                            mBinding.tvCardfour.visibility = View.VISIBLE

                            mBinding.etCardView.mask = "0000 000000 000"
                            mBinding.tvCardfour.text = lastFour
                        }

                    } else {
                        mBinding.etCardView.mask = "0000 000000 00000"
                    }
                    mBinding.etCardView.setText(cardInput)
                    mBinding.ivCardType.setImageDrawable(
                        AppCompatResources.getDrawable(
                            this, R.drawable.ic_amex
                        )
                    )
                    mBinding.ivCardType2.setImageDrawable(
                        AppCompatResources.getDrawable(
                            this, R.drawable.ic_amex
                        )
                    )
                }
            }
        }
        if (name != "" && image != "") {
            bankNameLocal = name
            mBinding.tvBankName.text = name
            mBinding.tvBankName2.text = name
            mBinding.tvBankName.setTextColor(Color.parseColor("#131414"))
            mBinding.tvBankName2.setTextColor(Color.parseColor("#131414"))

            if (isCardValid.get()) {
                mBinding.ivLogo.loadSvg(imageUrl)
                mBinding.ivLogo2.loadSvg(imageUrl)

            } else {
                mBinding.ivLogo.loadSvg(imageUrl)
                mBinding.ivLogo2.loadSvg(imageUrl)
            }
            setViewStroke(true,ui_config)
        } else {
            setViewStroke(false,ui_config)
            mBinding.tvBankName.setTextColor(Color.parseColor("#A8B1BD"))
            mBinding.tvBankName2.setTextColor(Color.parseColor("#A8B1BD"))
            mBinding.tvBankName.text = getString(R.string.card_name)
            mBinding.tvBankName2.text = getString(R.string.card_name)
            mBinding.ivCardType2.setImageDrawable(
                AppCompatResources.getDrawable(
                    this, R.drawable.card_image2
                )
            )
            mBinding.ivCardType.setImageDrawable(
                AppCompatResources.getDrawable(
                    this, R.drawable.card_image2
                )
            )

            mBinding.ivLogo.setImageDrawable(
                ContextCompat.getDrawable(
                    this, R.drawable.ic_bank_logo_extended
                )
            )
        }

    }


    fun setBackground() {
        if (isCreditCard.get()) {
            mBinding.ivChip.setImageDrawable(
                ContextCompat.getDrawable(
                    this, R.drawable.ic_chip
                )
            )
            mBinding.tvBankName.setTextColor(Color.parseColor("#131414"))
            mBinding.tvBankName2.setTextColor(Color.parseColor("#131414"))
            setViewStroke(true, ui_config)


        } else {
            setViewStroke(false, ui_config)
            mBinding.ivChip.setImageDrawable(
                ContextCompat.getDrawable(
                    this, R.drawable.ic_chip
                )
            )
            mBinding.ivLogo.setImageDrawable(
                ContextCompat.getDrawable(
                    this, R.drawable.ic_bank_logo_extended
                )
            )

            mBinding.tvBankName.setTextColor(Color.parseColor("#A8B1BD"))
            mBinding.tvBankName2.setTextColor(Color.parseColor("#A8B1BD"))


        }
    }

    internal fun validateCard() {
        if (lastFour.isNotEmpty() && !lastFour.equals("null") && !lastFour.equals("XXXX")) {
            cardNumber = mBinding.etCardView.rawText + "" + lastFour
        } else {
            cardNumber = mBinding.etCardView.rawText.toString()
        }

        when {


            mBinding.etCardView.rawText.length < 6 -> {

                if (mBinding.etCardView.rawText.length == 5 ) {
                    callStatus = true
                    isCreditCard.set(false)
                    setBackground()
                }
                isCardValid.set(false)

                mBinding.tvError.visibility = View.GONE
                var bankName = ""
                var bankLogo = ""
                if (intent.getStringExtra(IntentKeys.BANK_NAME) != null) {

                    bankName = intent.getStringExtra(IntentKeys.BANK_NAME).toString()
                    bankLogo = intent.getStringExtra(IntentKeys.BANK_LOGO).toString()
                }
                setCardInput(bankName, bankLogo, "")
                setViewStroke(false,ui_config)
            }
            mBinding.etCardView.rawText.length == 6 -> {
                if (callStatus) {
                    callStatus = false
                    viewModel?.fetchCardDetailsType(
                        BinCheckBodyRequest(
                            mBinding.etCardView.rawText.toString(), productBankMasterId, productId
                        )
                    )
                    isCardValid.set(false)
                    mBinding.tvError.visibility = View.GONE
                }
                MparticleUtils.logEvent(
                    "CC_Verification_CardNumber_Entered",
                    "User enters all or remaining digits of the 14 to 16-digit card number",
                    "Not Unique",
                    "Input Field",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Activation_Card_Number_Entered),
                    this@CardDetailsActivityNew
                )

            }
            mBinding.etCardView.rawText.length == 7 -> {
                callStatus = true
            }
            getVAlidaCardNumber() && !CreditCardUtils.check(cardNumber) || !isCreditCard.get() -> {
                isCardValid.set(false)
                mBinding.tvError.visibility = View.VISIBLE
                addErrorEvent()
            }
            getVAlidaCardNumber() && CreditCardUtils.check(cardNumber) && isCreditCard.get() -> {
                isCardValid.set(true)
                mBinding.tvError.visibility = View.GONE
                checkExistingCard()
            }
            else -> {
                isCardValid.set(false)
            }

        }
    }

    fun getVAlidaCardNumber(): Boolean {
        if (lastFour.isNotEmpty() && !lastFour.equals("null") && !lastFour.equals("XXXX")) {
            cardNumber = mBinding.etCardView.rawText + "" + lastFour
            if (cardDetailResponse?.iin?.network.equals(AMERICAN_EXPRESS) && cardNumber.length == 15) {
                return true
            } else if (cardDetailResponse?.iin?.network.equals(DINERS_CLUB) && cardNumber.length == 14) {
                return true
            } else return cardNumber.length == 16
        } else {
            if (cardDetailResponse?.iin?.network.equals(AMERICAN_EXPRESS) && mBinding.etCardView.rawText.length == 15) {
                return true
            } else if (cardDetailResponse?.iin?.network.equals(DINERS_CLUB) && mBinding.etCardView.rawText.length == 14) {
                return true
            } else return mBinding.etCardView.rawText.length == 16
        }

    }

    private fun checkExistingCard() {
        var lastFourNumbers = ""
        if (lastFour.isNotEmpty() && lastFour != "null" && lastFour != "XXXX") {
            lastFourNumbers = (mBinding.etCardView.rawText.toString() + "" + lastFour).takeLast(4)
        } else {
            lastFourNumbers = mBinding.etCardView.rawText.toString().takeLast(4)
        }
        if (existingProductList.isNotEmpty()) {
            for (elements in existingProductList) {
                when {
                    elements.last4 == lastFourNumbers -> {
                        productId = elements.id
                        isCardValid.set(true)
                        mBinding.tvError.visibility = View.GONE
                        break
                    }
                    else -> {
                        isCardValid.set(true)
                        mBinding.ivChip.setImageDrawable(
                            ContextCompat.getDrawable(
                                this, R.drawable.ic_chip
                            )
                        )
                        mBinding.tvError.visibility = View.GONE
                    }
                }
            }
        } else {
            isCardValid.set(true)
            mBinding.tvError.visibility = View.GONE
        }

        addScreenEvent()
    }

    private fun addScreenEvent() {
        var screentype = if (intent.getStringExtra(IntentKeys.BANK_NAME) != null) IDENTIFIED_CARD else MANUAL_CARD
        var propertyMap = HashMap<String, String>()
        propertyMap[BANK_NAME] = bankNameNew
        propertyMap[LAST_FOUR] = lastFour
        propertyMap[SCREEN_TYPE] = screentype
        MparticleUtils.logCardEnterScreen(CC_VERIFICATION_SCREEN_NAME, propertyMap)
    }


    private fun onCardFocused(): View.OnFocusChangeListener {
        return View.OnFocusChangeListener { _, hasFocused ->
            if (hasFocused) {
                isCardFocused.set(true)
                mBinding.llBtm.setBackgroundResource(R.drawable.et_btm_bg)

            } else {
                mBinding.llBtm.setBackgroundResource(R.drawable.et_btm_bg_un_focused)

                isCardFocused.set(false)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupUI() {
        prefs = SharePrefs.getInstance(this)!!
        bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialog)
        prefs?.putBoolean(SharePrefsKeys.IS_CARD_SECURE, false)
        prefs?.putBoolean(SharePrefsKeys.IS_BACK_PRESSED, false)
        Utils.initFirebase(this)
        val filter = InputFilter { source, start, end, dest, dstart, dend ->
            if (source != null && blockCharacterSet.contains("" + source)) {
                ""
            } else null
        }


        prefs?.putBoolean(SharePrefsKeys.IS_CARD_SECURE, false)
        Utils.setLightStatusBar(this)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_card_details_new)
        mBinding.activity = this
        mBinding.nextButton.text = getString(R.string.next)
        mBinding.etName.filters = arrayOf(filter)



        mBinding.tvCardHolderName.text =
            prefs?.getString(SharePrefsKeys.FIRST_NAME).toString() + " " + prefs?.getString(
                SharePrefsKeys.LAST_NAME
            ).toString()

        name = prefs?.getString(SharePrefsKeys.FIRST_NAME).toString() + " " + prefs?.getString(
            SharePrefsKeys.LAST_NAME
        ).toString()

        mBinding.etName.setText(name)
        mBinding.etCardView.onFocusChangeListener = onCardFocused()


        mBinding.etCardView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, position: Int, before: Int, action: Int) {

                cardNumber = mBinding.etCardView.rawText.toString()
                validateCard()

                if (cardNumber.isNullOrEmpty()) {
                    mBinding.tvError.visibility = View.GONE


                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
        mBinding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                MparticleUtils.logEvent(
                    "CC_Verification_EditName_Entered",
                    "User inputs the new cardholder name. This name may match the one that was pre-filled.",
                    "Unique",
                    "Input Field",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Activation_EditName_Entered),
                    this@CardDetailsActivityNew
                )
                name = p0.toString()
                onCardDetailsFilled()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        mBinding.etCardExpiry.onFocusChangeListener = onCardDetailsFocused()
        mBinding.etCvv.onFocusChangeListener = onCVVFocused()
        mBinding.etCvv.transformationMethod = AsteriskTransformationMethod()
        mBinding.etName.onFocusChangeListener = onEditNameFocused()

        mBinding.etCvv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                MparticleUtils.logEvent(
                    "CC_Verification_CVV_Entered",
                    "User enters the three-digit CVV of the card being activated",
                    "Not Unique",
                    "Input Field",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Activation_CVV_Entered),
                    this@CardDetailsActivityNew
                )
                if (p0.toString().length == 3) {
                    if (cardTypeCC == DINERS_CLUB || cardTypeCC == "Other") {
                        isCVVFilled.set(true)
                        cvv = p0.toString()
                        mBinding.btnCvv.setBackgroundResource(R.drawable.ic_cvv_btm_un_focused)
                    } else {
                        isCVVFilled.set(false)
                    }

                } else if (p0.toString().length == 4) {
                    if (cardTypeCC == AMERICAN_EXPRESS) {
                        isCVVFilled.set(true)
                        cvv = p0.toString()
                        mBinding.btnCvv.setBackgroundResource(R.drawable.ic_cvv_btm_un_focused)

                    } else {
                        isCVVFilled.set(false)
                    }

                } else {
                    isCVVFilled.set(false)
                }
                onCardDetailsFilled()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
        var isSlashAdded = false
        var isMonthValid = true

        mBinding.etCardExpiry.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                try {

                    mBinding.nestedScroll.scrollTo(0, mBinding.nestedScroll.bottom)
                    isCardExpiryFilled.set(false)
                    val str: String = p0.toString()
                    val textLength: Int = mBinding.etCardExpiry.text.toString().length
                    if (textLength == 1 && str.toInt() > 1 && str.toInt() < 10) {
                        mBinding.etCardExpiry.setText("0" + mBinding.etCardExpiry.text.toString())
                        mBinding.etCardExpiry.setSelection(mBinding.etCardExpiry.text.length);
                    }

                    if (textLength < 2) {
                        mBinding.tvError.visibility = View.GONE
                    }

                    if (mBinding.etCardExpiry.text.toString().length == 2) {
                        if (!CardExpiryUtils.checkMonthValidity(str.toInt())) {
                            mBinding.nestedScroll.scrollTo(0, mBinding.nestedScroll.bottom)
                            isMonthValid = false
                            mBinding.tvError.visibility = View.VISIBLE
                            mBinding.tvError.text = getString(R.string.str_check_the_expiry)
                            addErrorEvent()
                            scrollTobottom()
                        } else {
                            isMonthValid = true
                            mBinding.tvError.visibility = View.GONE
                        }
                    }

                    if (textLength == 2) {
                        if (!isSlashAdded) {
                            mBinding.etCardExpiry.setText(mBinding.etCardExpiry.text.toString() + "/")
                            mBinding.etCardExpiry.setSelection(mBinding.etCardExpiry.text.length)
                            isSlashAdded = true
                        } else {
                            isSlashAdded = false
                        }
                    }

                    if (textLength == 3) {
                        if (!str.contains("/")) {
                            mBinding.etCardExpiry.setText(
                                StringBuilder(
                                    mBinding.etCardExpiry.text.toString()
                                ).insert(str.length - 1, "/").toString()
                            )
                            mBinding.etCardExpiry.setSelection(mBinding.etCardExpiry.text.length)
                        }
                    }

                    if (textLength < 5 && isMonthValid) {
                        mBinding.tvError.visibility = View.GONE
                    }
                    if (textLength == 5 && isMonthValid) {
                        val index = str.indexOf('/')
                        val month = str.substring(0, index)
                        val year = str.substring(index + 1)
                        if (!CardExpiryUtils.isValid(month, year)) {
                            mBinding.tvError.visibility = View.VISIBLE
                            mBinding.nestedScroll.scrollTo(0, mBinding.nestedScroll.bottom)
                            mBinding.tvError.text = getString(R.string.str_check_the_expiry)
                            addErrorEvent()
                            scrollTobottom()
                        } else {
                            mBinding.tvError.visibility = View.GONE
                            date = str
                            isCardExpiryFilled.set(true)
                            mBinding.etCvv.requestFocus()
                        }
                    }

                    onCardDetailsFilled()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        mBinding.etCardExpiry.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etCardExpiry.text.length == 2) {
                    mBinding.etCardExpiry.setText(mBinding.etCardExpiry.text.toString()[0].toString())
                    mBinding.etCardExpiry.setSelection(mBinding.etCardExpiry.text.length);
                }
            }
            false
        }

        mBinding.llCVVKnowMore.setOnClickListener {
            startActivity(Intent(this, CommonWebViewActivity::class.java).apply {
                putExtra(
                    ProductFragment.URL, "${prefs?.faqsBaseUrl}${getString(R.string.secure_card)}"
                )
            })
        }
    }

    fun closeScreen() {
        val intent = Intent()
        setResult(ACTIVITY_INTENT_CODE, intent)
        finish()
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }

    private fun onCardDetailsFocused(): View.OnFocusChangeListener {
        return View.OnFocusChangeListener { _, hasFocused ->
            isCardDetailsFocused.set(hasFocused)
        }
    }

    fun onCardDetailsFilled() {
        if (name.isNotEmpty() && isCardExpiryFilled.get() && isCVVFilled.get()) {
            MparticleUtils.logEvent(
                "CC_Verification_Expiry_Entered",
                "User enters the expiry month and year of the card being activated",
                "Not Unique",
                "Input Field",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Activation_Expiry_Entered),
                this@CardDetailsActivityNew
            )
            isDetailsFilled.set(true)
        } else {
            isDetailsFilled.set(false)
        }
    }

    private fun onCVVFocused(): View.OnFocusChangeListener {
        return View.OnFocusChangeListener { _, hasFocused ->
            isCVVFocused.set(hasFocused)
        }
    }

    private fun onEditNameFocused(): View.OnFocusChangeListener {
        return View.OnFocusChangeListener { _, hasFocused ->
            isEditNameFocused.set(hasFocused)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[ProductActivationViewModel::class.java]
    }

    @SuppressLint("SetTextI18n")
    fun checkActionAndExecute() {
        logEvent(
            AFConstants.EVENT_CC_VERIFICATION_CARDNUMBER_CLICKED,
            AFConstants.DESC_CC_VERIFICATION_CARDNUMBER_CLICKED
        )
        enteredfullcardNumber = true
        var cardNo = cardNumber.take(6)
        viewModel?.fetchCardDetailsType(
            BinCheckBodyRequest(
                cardNo.toString(), productBankMasterId, productId
            )

        )


    }

    private fun logEvent(eventName: String, description: String) {
        val propertyMap = hashMapOf(AFConstants.PROPERTY_DESCRIPTION to description)
        MparticleUtils.logBlockerEvent(eventName, propertyMap)
    }


    fun setVisibility() {
        MparticleUtils.logEvent(
            "CC_Verification_EditName_Clicked",
            "User chooses to edit the pre-filled cardholder name",
            "Unique",
            "Continue",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Activation_EditName_Clicked),
            this@CardDetailsActivityNew
        )
        mBinding.llCarHolderName.visibility = View.GONE
        mBinding.llCardHolderNameInput.visibility = View.VISIBLE

        mBinding.btnSecureCard.text = getString(R.string.next)
        mBinding.etName.setSelection(mBinding.etName.text.length)
        mBinding.etName.requestFocus()
        Utils.showKeyboard()
    }

    fun openSucessfullBottomSheet(lastFour: String) {
        var properties = ""
        if (intent.getStringExtra(IntentKeys.BANK_NAME) != null) {
            properties = "verified-identified-card"
        } else {
            properties = "added-manually"
        }
        MparticleUtils.logCurrentScreen(
            "/cc-verification/success",
            "The customer views the credit card verification as success",
            "success-type",
            properties,
            "",
            "",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Activation_Pending),
            this@CardDetailsActivityNew
        )

        val bottomSheetSuccessDialog = Utils.showCustomDialogBottum(
            this,
            R.layout.bottom_sheet_bill_secure_success,
        )
        val bindingSheet = DataBindingUtil.inflate<BottomSheetBillSecureSuccessBinding>(
            layoutInflater, R.layout.bottom_sheet_bill_secure_success, null, false
        )
        MparticleUtils.logEvent(
            "CC_Verification_Success_Clicked",
            "User chooses to proceed to Home tab or clicks Pay Now to complete their credit card payment",
            "Unique",
            "Continue",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Activation_Success_Clicked),
            this@CardDetailsActivityNew
        )
        bindingSheet.ivClose.setOnClickListener {
            bottomSheetSuccessDialog.dismiss()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            isSecured = true
        }
        if (isFromHome == "doToken") {
            bindingSheet.bsBtnRetry.text = getString(R.string.pay_now)
        }
        bindingSheet.llAnimation.visibility = View.VISIBLE
        if (verificationSupported) {
            if (isFromHome == "doToken") {
                bindingSheet.bsTvTitle.text = "$bankNameLocal ...$lastFour Is Verified"
            } else {
                bindingSheet.bsTvTitle.text = "$bankNameLocal ...$lastFour Is Successfully Added"
            }

        } else {
            if (isFromHome == "doToken") {
                bindingSheet.bsTvTitle.text = "$bankNameLocal ...$lastFour Is Verified"
            } else {
                bindingSheet.bsTvTitle.text = "$bankNameLocal ...$lastFour Is Successfully Added"
            }

        }
        bindingSheet.bsBtnRetry.setOnClickListener {
            Utils.showProgressDialog(this)
            bottomSheetSuccessDialog.dismiss()
            isSecured = true
            if (isComingFrom) {
                goToPaymentScreen(
                    accountNumberNew,
                    cardTypeNew,
                    amountNew.replace(",", ""),
                    bankNameNew,
                    billIdNew,
                    productId,
                    bankLogoNew,
                    startColor,
                    middleColor,
                    endColor,
                    lastFour ?: "",
                    ""
                )
            } else if (isFromHome == "doToken") {
                goToPaymentScreen(
                    accountNumberNew,
                    cardTypeNew,
                    amountNew.replace(",", ""),
                    bankNameNew,
                    billIdNew,
                    productId,
                    bankLogoNew,
                    startColor,
                    middleColor,
                    endColor,
                    lastFour ?: "",
                    ""
                )

            } else {
                bottomSheetSuccessDialog.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
        }
        val rewardsRate = "${Utils.getRewardRate(binBankMasterId, Utils.itemList)}%"
        bindingSheet.tvRewards.text = getString(
            R.string.pay_now_amp_earn_upto_1_of_the_bill_amount_in_cheq_chips, rewardsRate
        )
        bottomSheetSuccessDialog.setContentView(bindingSheet.root)
        bottomSheetSuccessDialog.setCancelable(false)
        bottomSheetSuccessDialog.show()
    }

    fun addCardApiCall(isToken: Boolean) {
        progress.set(true)
        if (!cardDetailResponse?.iin?.network.isNullOrBlank()) {
            if (!verificationSupported) {
                Utils.showProgressDialog(this)
            }
            viewModel?.addCard(
                AddCardRequest(
                    isToken,
                    binBankMasterId,
                    if (productId == "null") "" else productId,
                    name,
                    date.takeLast(2),
                    date.take(2),
                    "credit",
                    cardNumber,
                    cvv,
                    "android",
                    cardDetailResponse?.iin?.network
                )
            )
        } else {
            mBinding.tvError.visibility = View.VISIBLE
            mBinding.tvError.text = getString(R.string.str_card_net_not_found)
            addErrorEvent()

        }

    }

    fun openOptOutBottomSheet() {
        val bottomSheetDialog = Utils.showCustomDialogBottum(
            this,
            R.layout.bottom_sheet_dialog_risk_card,
        )
        val bindingSheet = DataBindingUtil.inflate<BottomSheetDialogRiskCardBinding>(
            layoutInflater, R.layout.bottom_sheet_dialog_risk_card, null, false
        )


        bottomSheetDialog.setContentView(bindingSheet.root)
        bindingSheet.btnRetry.setOnClickListener {
            addCardApiCall(true)
            bottomSheetDialog.dismiss()
        }
        bindingSheet.tvYesOptOut.setOnClickListener {
            addCardApiCall(false)
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.show()
    }

    fun openSecureBottomSheet() {
        val bottomSheetDialog = Utils.showCustomDialogBottum(
            this,
            R.layout.bottom_sheet_dialog_secure_card,
        )
        val bindingSheet = DataBindingUtil.inflate<BottomSheetDialogSecureCardBinding>(
            layoutInflater, R.layout.bottom_sheet_dialog_secure_card, null, false
        )

        MparticleUtils.logCurrentScreen(
            "/cc-verification/consent-to-secure",
            "The customer views the bottomsheet nudging and detailing the benefit of verification",
            "",
            "",
            "",
            "",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_VERIFICATION_CONSENT_TO_SECURE),
            this@CardDetailsActivityNew
        )
        bindingSheet.btnRetry.setOnClickListener {
            MparticleUtils.logEvent(
                "CC_Verification_Consent_Clicked",
                "User chooses to continue after reading the benefits of verifying",
                "Not Unique",
                "Continue",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Verification_Consent_Clicked),
                this
            )

            addCardApiCall(true)
            Utils.showProgressDialog(this)
            bottomSheetDialog.dismiss()
        }
        bindingSheet.tvOptOut.setOnClickListener {
            openOptOutBottomSheet()
            bottomSheetDialog.dismiss()
        }
        bindingSheet.tvKnowMore.setOnClickListener {
            openLearnMoreBottomSheet()
        }
        val rewardsRate = "${Utils.getRewardRate(binBankMasterId, Utils.itemList)}%"
        bindingSheet.tvRewards.text = getString(
            R.string.str_earn_cheq_chips, rewardsRate
        )
        bottomSheetDialog.setContentView(bindingSheet.root)
        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.show()
    }

    private fun openLearnMoreBottomSheet() {
        MparticleUtils.logEvent(
            "CC_Verification_KnowMore_Clicked",
            "User chooses to click Know More to learn about how card verification works",
            "Unique",
            "Continue",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Rewards_KnowMore_Clicked),
            this
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

        ivClose!!.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        Utils.hideKeyboard(this)
        bottomSheetDialog.show()
    }

    fun checkCardProcess() {
        if (isComingFrom) {
            onPayNowCLick(
                mBinding.etCardView.rawText.toString(),
                productItem?.bankMasterRecord?.bankName ?: "",
                productItem?.card_network,
                "",
                productItem?.bill?.min_due,
                (productItem?.bill?.amount_paid?.let { productItem?.bill?.total_due?.minus(it) }),
                productItem?.bill?.id,
                productItem?.id,
                productItem?.bill?.amount_paid,
                productItem?.last4,
                productItem?.bankMasterRecord?.outerGridGradientColors?.gOne ?: "",
                productItem?.bankMasterRecord?.outerGridGradientColors?.gTwo ?: "",
                productItem?.bankMasterRecord?.outerGridGradientColors?.gThree ?: "",
            )
        } else {
            if (isFromHome == "doToken") {
                openCustomPaymentBottomSheet(productItem!!, 0)
            } else if (isFromHome == "optOut") {
                if (verificationSupported) {
                    openSecureBottomSheet()
                } else {
                    addCardApiCall(true)
                }

            } else {
                if (verificationSupported) {
                    openSecureBottomSheet()
                } else {
                    addCardApiCall(true)
                }
            }

        }
    }

    fun setClickOfEt(lastFourLocal: String) {
        if (!lastFourLocal.isNullOrEmpty() && lastFourLocal != "XXXX") {
            when (lastFourLocal.length) {
                4 -> {
                    mBinding.tvCardfour.visibility = View.VISIBLE
                    mBinding.tvCardfour.text = lastFourLocal

                    mBinding.etCardView.mask = "0000 0000 0000"
                    mBinding.etCardView.hint = "000000000000000"
                }
                2 -> {
                    mBinding.tvCardfour.visibility = View.VISIBLE
                    mBinding.etCardView.mask = "0000 0000 0000 00"
                    mBinding.tvCardfour.text = "$lastFourLocal"

                }
                else -> {
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (prefs?.getBoolean(SharePrefsKeys.IS_CARD_ACTIVATE) == true) {
            openProcessingCardActivationBottomSheet()
        }

        if (prefs?.getBoolean(SharePrefsKeys.IS_CARD_SECURE) == true) {
            openProcessingCardActivationBottomSheet()
            prefs?.putBoolean(SharePrefsKeys.IS_CARD_SECURE, false)
        }
        if (prefs?.getBoolean(SharePrefsKeys.IS_BACK_PRESSED) == true) {
            selectedProduct.clear()
            Utils.hideProgressDialog()
        }
    }

    fun onPayNowCLick(
        accountNumber: String?,
        bankName: String?,
        cardType: String?,
        bankLogo: String?,
        minDue: Double?,
        totalDue: Double?,
        billId: String?,
        productId: String?,
        paidAmount: Double?,
        lastFour: String?,
        startColor: String,
        middleColor: String,
        endColor: String
    ) {

        totalDueDialog = Utils.showCustomDialogBottum(this, R.layout.bottom_sheet_total_due)
        totalDueDialog.setCancelable(false)
        val ivCancel = totalDueDialog.findViewById<FrameLayout>(R.id.ivCancel)
        ivCancel.setOnClickListener {
            if (prefs?.getBoolean(SharePrefsKeys.IS_BACK_PRESSED) == true) {
                val intent = Intent()
                setResult(ACTIVITY_INTENT_CODE, intent)
                onBackPressed()
            }
            totalDueDialog.dismiss()
            Utils.hideKeyboard(this)
        }

        val iv_Card_Type = totalDueDialog.findViewById<AppCompatImageView>(R.id.ivCardType)
        val iv_bank_image = totalDueDialog.findViewById<AppCompatImageView>(R.id.iv_bank_image)
        val tvInfo = totalDueDialog.findViewById<AppCompatTextView>(R.id.tvInfo)
        val ivMore = totalDueDialog.findViewById<AppCompatImageView>(R.id.ivMore)
        ivMore.setOnClickListener {
            openKnowMoreDialog()
            this?.let { it1 ->
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
        val tvCardNumber = totalDueDialog.findViewById<AppCompatTextView>(R.id.tvCardNumber)
        tvCardNumber.text = "··· ${cardNumber.takeLast(4)}"
        val tvBankName = totalDueDialog.findViewById<AppCompatTextView>(R.id.tvBankName)
        tvBankName.text = bankName

        val etAmount = totalDueDialog.findViewById<EditText>(R.id.etAmount)
        etAmount.setText(Utils.getFormattedDecimal(totalDue!!.toDouble()))
        val tvRewardPercentage = totalDueDialog.findViewById<AppCompatTextView>(R.id.tvRewardPercentage)
        val tvAtTheRate = totalDueDialog.findViewById<AppCompatTextView>(R.id.tvAtTheRate)
        val tvCaption = totalDueDialog.findViewById<AppCompatTextView>(R.id.tvCaption)
        val chipTotalDue = totalDueDialog.findViewById<Chip>(R.id.chipTotalDue)
        val chipMinimumDue = totalDueDialog.findViewById<Chip>(R.id.chipMinimumDue)
        val customChip = totalDueDialog.findViewById<Chip>(R.id.chipCustom)
        val llRewards = totalDueDialog.findViewById<LinearLayoutCompat>(R.id.llRewards)
        val llMessageMinimumDue = totalDueDialog.findViewById<LinearLayoutCompat>(R.id.llMessageMinimumDue)
        val llMessageTotalDue = totalDueDialog.findViewById<LinearLayoutCompat>(R.id.llMessageTotalDue)

        val llMessageError = totalDueDialog.findViewById<LinearLayoutCompat>(R.id.llMessageError)
        val tvError = totalDueDialog.findViewById<TextView>(R.id.tvError)
        val ivMessageType = totalDueDialog.findViewById<AppCompatImageView>(R.id.ivMessageType)
        val btnOkay = totalDueDialog.findViewById<AppCompatButton>(R.id.btnOkay)
        val tvRewards = totalDueDialog.findViewById<AppCompatTextView>(R.id.tvRewards)
        val flStroke = totalDueDialog.findViewById<FrameLayout>(R.id.flStroke)
        val tvRewardEarned = totalDueDialog.findViewById<AppCompatTextView>(R.id.tvRewardEarned)
        val llCardSolidBackGround =
            totalDueDialog.findViewById<LinearLayoutCompat>(R.id.llCardSolidBackGround)
        val imageUrl =
            "${prefs?.bankMasterUrl}${productItem?.bankMasterRecord?.id}-logo-with-name.svg"
        iv_bank_image.loadSvg(imageUrl)
        GradientUtils.setBoarderStroke(
            productItem?.bankMasterRecord?.ui_config?.cardColor ?: "#FFFFFF",
            productItem?.bankMasterRecord?.ui_config?.opacity_border ?: "#FFFFFF",
            true,
            flStroke
        )
        GradientUtils.setBackGround(
            productItem?.bankMasterRecord?.ui_config?.cardColor ?: "#FFFFFF",
            "",
            productItem?.bankMasterRecord?.ui_config?.opacity_topLeft ?: "#FFFFFF",
            productItem?.bankMasterRecord?.ui_config?.opacity_bottomRight ?: "#FFFFFF",
            llCardSolidBackGround
        )
        if (totalDue >= 100) {
            rewardsPoint = roundupAmount(
                totalDue,
                DEFAULT_PERCENTAGE,
                tvRewardPercentage,
                productItem?.bankMasterRecord?.id,
                Utils.itemList
            )
            llRewards.visibility = View.VISIBLE
            MonthlyEarnRule.setRewardLimit(
                rewardsCanAssign,
                rewardsAssignUpto,
                rewardsPoint,
                tvRewards,
                tvAtTheRate,
                tvRewardPercentage,
                tvRewardEarned,
                this
            )
        } else if (totalDue < 100) {
            llRewards.visibility = View.VISIBLE
            tvRewards.text = getString(R.string.str_pay_total_bill)
        } else {
            llRewards.visibility = View.GONE
        }
        if (rewardsAssignUpto != 0) {
            tvRewardEarned.text =
                "$rewardsAssigned/$rewardsAssignUpto chips have been earned this month!"
        }
        billStatus = FULL
        productItem?.billStatus = FULL
        chipTotalDue.setOnClickListener {
            billStatus = FULL
            productItem?.billStatus = FULL
            etAmount.setText(Utils.getFormattedDecimal(totalDue.toDouble()))
            etAmount.isEnabled = false
            llMessageError.visibility = View.GONE
            llMessageTotalDue.visibility = View.VISIBLE
            ivMessageType.setImageResource(R.drawable.ic_happy_face)
            tvInfo.text = getString(R.string.str_great_move)
            llMessageMinimumDue.visibility = View.GONE
            tvCaption.text = resources.getText(R.string.you_have_to_pay)
            this?.let { it1 ->
                MparticleUtils.logEvent(
                    "CC_Payment_TotalDue_Clicked",
                    "User clicks the total due bubble to view total due amount\n",
                    "Unique",
                    "Continue",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_TotalDue_Clicked),
                    it1
                )
            }
            if (totalDue >= 100) {
                rewardsPoint = roundupAmount(
                    totalDue,
                    DEFAULT_PERCENTAGE,
                    tvRewardPercentage,
                    productItem?.bankMasterRecord?.id,
                    Utils.itemList
                )
                llRewards.visibility = View.VISIBLE
                MonthlyEarnRule.setRewardLimit(
                    rewardsCanAssign,
                    rewardsAssignUpto,
                    rewardsPoint,
                    tvRewards,
                    tvAtTheRate,
                    tvRewardPercentage,
                    tvRewardEarned,
                    this
                )
            } else if ((totalDue ?: 0.0) < 100) {
                llRewards.visibility = View.VISIBLE
                tvRewards.text = getString(R.string.str_pay_total_bill)
            } else {
                llRewards.visibility = View.GONE
            }
        }
        if (productItem?.card_network == MASTER_CARD) {
            iv_Card_Type.setImageResource(R.drawable.ic_mastercard)
        }
        if (productItem?.card_network == VISA) {
            iv_Card_Type.setImageResource(R.drawable.visa)
        }
        if (productItem?.card_network == RUPAY) {
            iv_Card_Type.setImageResource(R.drawable.ic_rupay_card_icon)
        }
        if (productItem?.card_network == DINERS_CLUB) {
            iv_Card_Type.setImageResource(R.drawable.ic_dinner_icon)
        }
        if (productItem?.card_network == AMERICAN_EXPRESS) {
            iv_Card_Type.setImageResource(R.drawable.ic_american_icon)
        }
        etAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isNotEmpty()) {
                    val amount = p0.toString().replace(",", "").toDouble()
                    if (amount > 0 && amount < 100) {
                        tvRewards.text = getString(R.string.str_enter_100)
                        tvRewardPercentage.visibility = View.GONE
                        tvRewardEarned.visibility = View.GONE
                        tvAtTheRate.visibility = View.GONE
                        llRewards.visibility = View.VISIBLE
                    } else {
                        rewardsPoint = roundupAmount(
                            amount,
                            DEFAULT_PERCENTAGE,
                            tvRewardPercentage,
                            productItem?.bankMasterRecord?.id,
                            Utils.itemList
                        )
                        tvRewardPercentage.visibility = View.VISIBLE
                        tvAtTheRate.visibility = View.VISIBLE
                        llRewards.visibility = View.VISIBLE
                        MonthlyEarnRule.setRewardLimit(
                            rewardsCanAssign,
                            rewardsAssignUpto,
                            rewardsPoint,
                            tvRewards,
                            tvAtTheRate,
                            tvRewardPercentage,
                            tvRewardEarned,
                            this@CardDetailsActivityNew
                        )
                    }
                    if (amount > totalDue) {
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
                        llMessageTotalDue.visibility = View.GONE
                    } else {
                        llMessageError.visibility = View.GONE
                        btnOkay.isEnabled = true
                        llMessageTotalDue.visibility = View.VISIBLE
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
                // llAmtView.setBackgroundResource(R.drawable.et_btm_bg)
            } else {
                // llAmtView.setBackgroundResource(R.drawable.et_btm_bg_un_focused)
                this.let {
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

        if (minDue != null) {
            chipMinimumDue.setOnClickListener {
                ivMessageType.setImageResource(R.drawable.ic_warning)
                tvInfo.text = getString(R.string.str_you_will_incur)
                if (minDue >= 100) {
                    rewardsPoint = roundupAmount(
                        minDue,
                        DEFAULT_PERCENTAGE,
                        tvRewardPercentage,
                        productItem?.bankMasterRecord?.id,
                        Utils.itemList
                    )
                    llRewards.visibility = View.VISIBLE
                    MonthlyEarnRule.setRewardLimit(
                        rewardsCanAssign,
                        rewardsAssignUpto,
                        rewardsPoint,
                        tvRewards,
                        tvAtTheRate,
                        tvRewardPercentage,
                        tvRewardEarned,
                        this
                    )
                } else if (minDue < 100) {
                    llRewards.visibility = View.VISIBLE
                    tvRewards.text = getString(R.string.str_pay_total_bill)
                } else {
                    llRewards.visibility = View.GONE
                }
                billStatus = MIN
                productItem?.billStatus = MIN
                etAmount.setText(Utils.getFormattedDecimal(minDue.toDouble()))
                etAmount.isEnabled = false
                llMessageError.visibility = View.GONE
                llMessageTotalDue.visibility = View.VISIBLE
                llMessageMinimumDue.visibility = View.GONE
                tvCaption.text = resources.getText(R.string.you_have_to_pay)
                this.let { it1 ->
                    MparticleUtils.logEvent(
                        "CC_Payment_MinimumDue_Clicked",
                        "User clicks the minimum due bubble to view minimum due amount\n",
                        "Unique",
                        "Continue",
                        SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_MinimumDue_Clicked),
                        it1
                    )
                }
            }
        } else {
            chipMinimumDue.visibility = View.GONE
        }
        customChip.setOnClickListener {
            llRewards.visibility = View.GONE
            billStatus = CUSTOM
            productItem?.billStatus = CUSTOM
            ivMessageType.setImageResource(R.drawable.ic_warning)
            tvInfo.text = getString(R.string.str_you_will_incur)
            etAmount.isEnabled = true
            etAmount.requestFocus()
            etAmount.setText("")
            btnOkay.isEnabled = false
            llMessageTotalDue.visibility = View.VISIBLE
            llMessageMinimumDue.visibility = View.GONE
            tvCaption.text = getString(R.string.str_enter_amount)
            tvRewards.text = getString(R.string.str_you_will_earn_chips)
            this.let { it1 -> Utils.showKeyboard(it1) }
            this.let { it1 ->
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
            if (etAmount.text.toString().replace(",", "").toDouble() < 10) {
                llMessageError.visibility = View.VISIBLE
                tvError.text = getString(R.string.str_minimum_amount)
                btnOkay.isEnabled = false
                llMessageTotalDue.visibility = View.GONE
            } else if (etAmount.text.toString().replace(",", "").toDouble() > 1000000) {
                llMessageError.visibility = View.VISIBLE
                tvError.text = getString(R.string.str_amount_less_1000000)

            } else {
                accountNumberNew = accountNumber.toString()
                cardTypeNew = cardType.toString()
                amountNew = etAmount.text.toString().replace(",", "")
                bankNameNew = bankName.toString()
                billIdNew = billId.toString()
                bankLogoNew = bankLogo.toString()
                amount = amountNew
                productItem?.customAmount = amountNew.toDouble()
                productItem?.rewardsPoint = rewardsPoint
                productItem?.billStatus = billStatus
                if (selectedProduct.size <= 0) {
                    productItem?.let { it1 -> selectedProduct.add(it1) }
                }
                if (isSecured) {
                    goToPaymentScreen(
                        accountNumberNew,
                        cardTypeNew,
                        amountNew.replace(",", ""),
                        bankNameNew,
                        billIdNew,
                        productId,
                        bankLogoNew,
                        startColor,
                        middleColor,
                        endColor,
                        lastFour ?: "",
                        ""
                    )
                } else {
                    if (verificationSupported) {
                        openSecureBottomSheet()
                    } else {
                        addCardApiCall(true)
                    }
                }
                Utils.hideKeyboard(this)

            }

        }
        totalDueDialog.show()
    }

    private fun openKnowMoreDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_know_more)

        val info_Image = bottomSheetDialog.findViewById<ImageView>(R.id.info_Image)
        mFirebaseDatabase?.child("payment_info_icon")?.get()?.addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            val options = RequestOptions().placeholder(R.drawable.button_white_bordered)
                .error(R.drawable.button_white_bordered)



            if (info_Image != null) {
                Glide.with(this).load(it.value).apply(options).into(info_Image)
            }
        }?.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }

        bottomSheetDialog.setCancelable(false)
        val btnSubmit = bottomSheetDialog.findViewById<AppCompatImageView>(R.id.ivCancel)
        btnSubmit?.setOnClickListener {
            bottomSheetDialog.dismiss()
            this.let { it1 -> Utils.showKeyboard(it1) }
        }
        Utils.hideKeyboard(this)
        bottomSheetDialog.show()
    }

    private fun goToPaymentScreen(
        accountNumber: String?,
        cardType: String?,
        amount: String?,
        bankName: String?,
        billId: String?,
        productId: String?,
        bankLogo: String?,
        startColor: String,
        middleColor: String,
        endColor: String,
        lastFour: String,
        bankMasterId: String
    ) {
        if (amount != null) {
            Utils.hideProgressDialog()
            startActivity(
                Intent(this, PaymentMethodsActivity::class.java).putExtra(
                    IntentKeys.PRODUCT_LIST, selectedProduct
                ).putExtra(IntentKeys.IS_PAY_TOGETHER, false)
                    .putExtra(IntentKeys.TOTAL_AMOUNT, amount.trim())
                    .putExtra(IntentKeys.BILL_STATUS, billStatus)
                    .putExtra(IntentKeys.REWARDS_POINT, rewardsPoint)
                    .putExtra(IntentKeys.COMING_FROM, IntentKeys.CARD_DETAILS)
                    .putExtra(IntentKeys.REWARDS_ASSIGN_UPTO, rewardsAssignUpto)
                    .putExtra(IntentKeys.REWARDS_CAN_ASSIGN, rewardsCanAssign)
                    .putExtra(IntentKeys.REWARDS_ASSIGNED, rewardsAssigned)
            )

        }
    }

    fun scrollTobottom() {
        val handler = Handler()
        handler.postDelayed({
            mBinding.nestedScroll.smoothScrollTo(
                0, mBinding.llLayout.getBottom()
            )
        }, 100)
    }


    private fun openProcessingCardActivationBottomSheet() {
        prefs?.putBoolean(SharePrefsKeys.IS_CARD_ACTIVATE, false)
        bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialog)


        val bindingSheet = DataBindingUtil.inflate<BottomSheetCardActivationProcessBinding>(
            layoutInflater, R.layout.bottom_sheet_card_activation_process, null, false
        )

        bottomSheetDialog.setContentView(bindingSheet.root)
        bottomSheetDialog.setCancelable(false)

        MparticleUtils.logCurrentScreen(
            "/cc-verification/pending",
            "The customer awaits the terminal status of the credit card verification  on the loading screen",
            "",
            "",
            "",
            "",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Activation_Pending),
            this@CardDetailsActivityNew
        )


        var isFirst = true
        if (isFromHome != "doToken") {
            bindingSheet.bsTvTitle.text = getString(R.string.str_adding_card)
            bindingSheet.bsTvMessage.text = getString(R.string.str_card_back_warning)
        }
        bindingSheet.processingAnimation.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
                try {
                    if (isFirst) {
                        viewModel?.getProductById(GetProductByIdRequest(productId, txnId))
                        isFirst = false
                    }
                } catch (ex: Exception) {
                    ex.toString()
                }
            }
        })

        bottomSheetDialog.show()
    }

    fun openFailedBottomSheet(message:String) {

        MparticleUtils.logCurrentScreen(
            "/cc-verification/failed",
            "The customer views the failed status of credit card verification along with error description",
            "",
            "",
            "",
            "",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Activation_Failed),
            this
        )
        val failedDialog = BottomSheetDialog(this)
        val bindingSheet = DataBindingUtil.inflate<BottomSheetCardActivationFailedBinding>(
            layoutInflater, R.layout.bottom_sheet_card_activation_failed, null, false
        )
        bindingSheet.bsTvMessage.text=message
        failedDialog.setContentView(bindingSheet.root)
        failedDialog.setCancelable(false)
        bindingSheet.bsBtnRetry.setOnClickListener {
            MparticleUtils.logEvent(
                "CC_Verification_Retry",
                "User chooses to retry CC verification on failure",
                "Unique",
                "Continue",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Retry),
                this
            )
            failedDialog.dismiss()
        }
        bindingSheet.bsIvCancel.setOnClickListener {
            MparticleUtils.logEvent(
                "CC_Verification_Failed_BackClicked",
                "User chooses to exit the verification flow by clicking cancel on the failed screen",
                "Unique",
                "Back",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Failed_BackClicked),
                this
            )
            val intent = Intent()
            setResult(ACTIVITY_INTENT_CODE, intent)
            onBackPressed()
            failedDialog.dismiss()
        }
        failedDialog.show()
    }

    private fun openCustomPaymentBottomSheet(productV2: ProductV2, position: Int) {
        customPayDialog = Utils.showCustomDialogBottum(this, R.layout.bottom_sheet_edit_amount_custom)
        val bindingSheet = DataBindingUtil.inflate<BottomSheetEditAmountCustomBinding>(
            layoutInflater, R.layout.bottom_sheet_edit_amount_custom, null, false
        )
        val imageUrl = "${prefs?.bankMasterUrl}${productV2.bankMasterRecord?.id}-logo-with-name.svg"
        bindingSheet.llCreditCard.ivBankImage.loadSvg(imageUrl)
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
        customPayDialog.setContentView(bindingSheet.root)
        customPayDialog.setCancelable(false)
        Utils.showKeyboard(this)

        bindingSheet.ivCancel.setOnClickListener {

            if (prefs?.getBoolean(SharePrefsKeys.IS_BACK_PRESSED) == true) {
                val intent = Intent()
                setResult(ACTIVITY_INTENT_CODE, intent)
                onBackPressed()
            }
            customPayDialog.dismiss()
            Utils.hideKeyboard(this)
        }
        bindingSheet.llRewards.visibility = View.GONE
        if (rewardsAssignUpto != 0) {
            bindingSheet.tvRewardEarned.text =
                "$rewardsAssigned/$rewardsAssignUpto chips have been earned this month!"
        }
        bindingSheet.llCreditCard.tvCardNumber.text = "··· ${cardNumber.takeLast(4)}"

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

        bindingSheet.llCreditCard.tvBankName.text = productV2.bankMasterRecord?.bankName ?: ""
        bindingSheet.etAmount.setText("")
        bindingSheet.etAmount.requestFocus()

        when (productV2.card_network) {
            MASTER_CARD -> {
                bindingSheet.llCreditCard.ivCardType.setImageResource(R.drawable.ic_mastercard)
            }
            VISA -> {
                bindingSheet.llCreditCard.ivCardType.setImageResource(R.drawable.visa)
            }
            RUPAY -> {
                bindingSheet.llCreditCard.ivCardType.setImageResource(R.drawable.ic_rupay_card_icon)
            }
            DINERS_CLUB -> {
                bindingSheet.llCreditCard.ivCardType.setImageResource(R.drawable.ic_dinner_icon)
            }
            AMERICAN_EXPRESS -> {
                bindingSheet.llCreditCard.ivCardType.setImageResource(R.drawable.ic_american_icon)
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
                        bindingSheet.tvRewardEarned.visibility = View.GONE
                        bindingSheet.llRewards.visibility = View.VISIBLE

                    } else {
                        rewardsPoint = roundupAmount(
                            amountForRewards,
                            DEFAULT_PERCENTAGE,
                            bindingSheet.tvRewardPercentage,
                            productItem?.bankMasterRecord?.id,
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
                            this@CardDetailsActivityNew
                        )
                    }

                    bindingSheet.btnOkay.isEnabled = true
                    val amount = p0.toString().replace(",", "").toDouble()
                    if (amount > 1000000) {
                        bindingSheet.llMessageError.visibility = View.VISIBLE
                        bindingSheet.tvError.text = getString(R.string.str_amount_less_1000000)
                        bindingSheet.btnOkay.isEnabled = false
                    } else {
                        bindingSheet.llMessageError.visibility = View.GONE
                        bindingSheet.btnOkay.isEnabled = true
                    }
                } else {
                    bindingSheet.llMessageError.visibility = View.GONE
                    bindingSheet.btnOkay.isEnabled = false
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        bindingSheet.btnOkay.setOnClickListener {
            Utils.hideProgressDialog()
            billStatus = CUSTOM
            if (bindingSheet.etAmount.text.toString().replace(",", "").toDouble() < 10) {
                bindingSheet.llMessageError.visibility = View.VISIBLE
                bindingSheet.tvError.text = getString(R.string.str_minimum_amount)

            } else {
                accountNumberNew = mBinding.etCardView.rawText.toString()
                cardTypeNew = productV2.product_type
                amountNew = bindingSheet.etAmount.text.toString().replace(",", "")
                bankNameNew = productV2.bankMasterRecord?.bankName ?: ""
                billIdNew = productV2.bill?.id.toString()
                bankLogoNew = ""
                amount = amountNew
                productItem?.customAmount = amountNew.toDouble()
                productItem?.billStatus = billStatus
                productItem?.rewardsPoint = rewardsPoint
                if (selectedProduct.size <= 0) {
                    productItem?.let { it1 -> selectedProduct.add(it1) }
                }
                Utils.hideKeyboard(this)
                if (isSecured) {
                    goToPaymentScreen(
                        accountNumberNew,
                        cardTypeNew,
                        amountNew.replace(",", ""),
                        bankNameNew,
                        billIdNew,
                        productId,
                        bankLogoNew,
                        startColor,
                        middleColor,
                        endColor,
                        lastFour ?: "",
                        ""
                    )
                } else {
                    if (verificationSupported) {
                        openSecureBottomSheet()
                    } else {
                        addCardApiCall(true)
                    }
                }
            }
        }
        customPayDialog.show()
    }


    private fun setViewStroke(isTrue: Boolean, ui: UI?) {
        if (isTrue) {
            GradientUtils.setBoarderStroke(
                ui_config?.cardColor ?: "#858989",
                ui_config?.opacity_border ?: "30",
                true,
                mBinding.llCardDetails
            )
            GradientUtils.setBoarderStroke(
                ui_config?.cardColor ?: "#FFFFFF", ui_config?.opacity_border ?: "30",

                true, mBinding.llCardBackDetails
            )
            GradientUtils.setBackGround(
                ui_config?.cardColor ?: "#FFFFFF",
                "",
                ui_config?.opacity_topLeft ?: "0",
                ui_config?.opacity_bottomRight ?: "12",
                mBinding.llCardBackGround
            )
            GradientUtils.setBackGround(
                ui_config?.cardColor ?: "#CACFCF",
                "",
                ui_config?.opacity_topLeft ?: "0",
                ui_config?.opacity_bottomRight ?: "12",
                mBinding.llCardBackDetailsSolid
            )
        } else {
            GradientUtils.setBoarderStroke(
                "#858989", ui_config?.opacity_border ?: "30", true, mBinding.llCardDetails
            )
            GradientUtils.setBackGround(
                "#FFFFFF",
                "",
                ui_config?.opacity_topLeft ?: "0",
                ui_config?.opacity_bottomRight ?: "12",
                mBinding.llCardBackGround
            )
        }

    }

    private fun backcardView() {
        if (isFront) {
            MparticleUtils.logEvent(
                "CC_Verification_CardNumber_Clicked",
                "User continues to the next screen on entering a valid card number",
                "Not Unique",
                "Continue",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Activation_CardNumber_Clicked),
                this@CardDetailsActivityNew
            )
            mBinding.etCardView.visibility = View.GONE
            mBinding.etCardExpiry.requestFocus()
            mBinding.etCardExpiry.setSelection(mBinding.etCardExpiry.length())
            mBinding.llSecureButton.visibility = View.VISIBLE
            mBinding.llWidget.visibility = View.GONE
            mBinding.tvProfileDesc.visibility = View.VISIBLE
            mBinding.nextButton.visibility = View.GONE
            mBinding.llRewards.visibility = View.GONE
            mBinding.llCardBackDetails.visibility = View.VISIBLE
            mBinding.llCVVKnowMore.visibility = View.VISIBLE
            mBinding.tvLetStart.text = getString(R.string.str_card_identified)
            mBinding.tvProfileDesc.text = getString(R.string.str_enter_remaining_details)
            Utils.showKeyboard()
            isFront = false
            mBinding.tvCardNumberNew.text = "XXXX  ${cardNumber.takeLast(4)}"
            val buttonLayoutParams: LinearLayoutCompat.LayoutParams =
                LinearLayoutCompat.LayoutParams(
                    LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                    LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                )
            buttonLayoutParams.setMargins(0, 30, 0, 0)
            mBinding.tvError.setLayoutParams(buttonLayoutParams)

        }
    }


}


