package com.cheq.retail.ui.activateCard

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.databinding.ActivityCardDetailsBinding
import com.cheq.retail.databinding.BottomSheetBillSecureSuccessBinding
import com.cheq.retail.databinding.BottomSheetDialogRiskCardBinding
import com.cheq.retail.databinding.BottomSheetDialogSecureCardBinding
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.activateCard.model.AddCardRequest
import com.cheq.retail.ui.activateCard.model.CardDetailTypeResponse
import com.cheq.retail.ui.activateCard.viewModel.ProductActivationViewModel
import com.cheq.retail.ui.billPayments.PaymentMethodsActivity
import com.cheq.retail.ui.login.modelv2.productv1.ProductV2
import com.cheq.retail.ui.main.MainActivity
import com.cheq.retail.utils.*
import com.cheq.retail.utils.GradientUtils.Companion.setBackGroundGradient
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip


class CardDetailsActivity : BaseActivity() {
    lateinit var mBinding: ActivityCardDetailsBinding
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
    var international: Boolean = false
    var isCardExpiryFilled = ObservableBoolean(false)
    var isCVVFilled = ObservableBoolean(false)
    var productId = ""
    var isExisting = false
    var bankMasterId = ""
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
    var billIdNew = ""
    var bankLogoNew = ""
    var isComingFrom = false
    private val blockCharacterSet = "~#^|$%&*!"
    var isFromHome = ""
    var isForActivate = false

    private var productItem: ProductV2? = null
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            println("RESULT ++++++++++++  " + result.resultCode)

            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                razorpayPaymentId = data!!.getStringExtra("RP_PAYMENT").toString()

                openProcessingBottomSheet()
            } else {
                BottomSheetUtils.showCommonBottomSheet(
                    this,
                    "Card Activation failed",
                    "Unable to credit account, invalid card details",
                    showCloseButton = true,
                    showRetryButton = true,
                    false,
                    true,
                    false
                )
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        catchIntent()
        setupViewModel()
        setupObserver()
        viewModel?.getDashboardData()

    }

    private fun catchIntent() {
        productId = intent.getStringExtra("productId") ?: ""
        isExisting = intent.getBooleanExtra("EXISTING", false)
        lastFour = intent.getStringExtra("lastFour").toString()
        startColor = intent.getStringExtra("startColor").toString()
        middleColor = intent.getStringExtra("middleColor").toString()
        endColor = intent.getStringExtra("endColor").toString()
        isFromHome = intent.getStringExtra("isFromHome").toString()
        if (intent.getSerializableExtra("productItem") != null) {
            productItem = intent.getSerializableExtra("productItem") as ProductV2
        }

        if (isFromHome.equals("doToken")) {
            mBinding.tvProfileDesc.text = "Card number will be used to secure your card"
            mBinding.tvRewardsNudge.text = "Flat 200 rewards to secure your card"
        } else {
            mBinding.tvProfileDesc.text = "Card number will be used to secure your card"
            mBinding.tvRewardsNudge.text = "Flat 200 rewards to secure your card"
        }
        if (productItem?.bill != null) {
            isComingFrom = true
            mBinding.tvProfileDesc.text = "Card number will be used to complete payment"
            mBinding.tvRewardsNudge.text = "Flat 200 rewards on successful payment"
            println("activateTotalDue ${productItem?.bill?.total_due}")
        }

        if (startColor.isNotEmpty() && middleColor.isNotEmpty() && endColor.isNotEmpty()) {
            setBackGroundGradient(startColor, middleColor, endColor, mBinding.llCardBackDetails)
        } else {
            startColor = "AAC7FF"
            middleColor = "F5F9FF"
            endColor = "497CDF"
            setBackGroundGradient(startColor, middleColor, endColor, mBinding.llCardBackDetails)
        }


        isActivateFlow = when {
            lastFour.lowercase().equals("xxxx") -> {
                false
            }
            lastFour.isNotEmpty() && !lastFour.equals("null") -> {
                setClickOfEt(lastFour)
                true
            }
            else -> {
                false
            }
        }
        if (intent.getStringExtra("bankName") != null) {
            setBankDetails(
                intent.getStringExtra("bankName").toString(),
                intent.getStringExtra("bankLogo").toString(),
                ""
            )
        }

    }

    private fun setupObserver() {
        viewModel?.cardTypeObserver?.observe(this) {
            try {


                when {
                    it.iin != null && it.status == true -> {
                        cardDetailResponse = it
                        isCreditCard.set(true)
                        setBackground()
                        mBinding.tvError.visibility = View.GONE
                        network = it.iin.network.toString()
                        type = it.iin.type.toString()
                        //   subType = it.iin.subType
                        cardTypeCC = "Other"
                        mBinding.etCvv.filters = arrayOf<InputFilter>(
                            InputFilter.LengthFilter(
                                3
                            )
                        )
                        mBinding.etCvv.hint = "***"
                        issuerCode = it.iin.issuerCode.toString()
                        issuerName = it.bank?.bankName.toString()
                        //  international = it.iin.international
                        bankMasterId = it.bank?.id.toString()
                        logo = it.bank?.logoWithName.toString()

                        setBankDetails(
                            it?.bank?.bankName.toString(),
                            if (!it.bank?.logoWithName.toString()
                                    .isNullOrBlank()
                            ) it.bank?.logoWithName.toString() else it.bank?.logo.toString(),
                            it?.iin.network.toString()
                        )
                        val colorMatrix = ColorMatrix()
                        colorMatrix.setSaturation(1f)
                        mBinding.ivLogo.colorFilter = ColorMatrixColorFilter(colorMatrix);


                        if (it.iin.network.equals("American Express")) {
                            print("American Card ****")
                            mBinding.ll1.visibility = View.GONE
                            mBinding.ll2.visibility = View.VISIBLE

                            mBinding.etOne1.setText("${cardNumber[0]}")
                            print("Card Num ${cardNumber}")
                            cardTypeCC = "American Express"
                            mBinding.etCvv.setFilters(
                                arrayOf<InputFilter>(
                                    InputFilter.LengthFilter(
                                        4
                                    )
                                )
                            )
                            mBinding.etCvv.hint = "****"
                            mBinding.etTwo1.setText("${cardNumber[1]}")
                            mBinding.etThree1.setText("${cardNumber[2]}")
                            mBinding.etFour1.setText("${cardNumber[3]}")
                            mBinding.etFive1.setText("${cardNumber[4]}")
                            mBinding.etSix1.setText("${cardNumber[5]}")
                            mBinding.etSeven1.requestFocus()
                            setTextWatchers_1()
                            setBackPressListener_1()
                            setEditTextFocus_1()

                        } else if (it.iin.network.equals("Diners Club")) {
                            mBinding.ll1.visibility = View.GONE
                            mBinding.ll2.visibility = View.GONE
                            mBinding.ll3.visibility = View.VISIBLE


                            mBinding.etOne2.setText("${cardNumber[0]}")
                            print("Card Num ${cardNumber}")
                            cardTypeCC = "Diners Club"
                            mBinding.etCvv.setFilters(
                                arrayOf<InputFilter>(
                                    InputFilter.LengthFilter(
                                        3
                                    )
                                )
                            )
                            mBinding.etCvv.hint = "***"
                            mBinding.etTwo2.setText("${cardNumber[1]}")
                            mBinding.etThree2.setText("${cardNumber[2]}")
                            mBinding.etFour2.setText("${cardNumber[3]}")
                            mBinding.etFive2.setText("${cardNumber[4]}")
                            mBinding.etSix2.setText("${cardNumber[5]}")
                            mBinding.etSeven2.requestFocus()


                            setTextWatchers_2()
                            setBackPressListener_2()
                            setEditTextFocus_2()

                        } else {
                            mBinding.ll1.visibility = View.VISIBLE
                            mBinding.ll2.visibility = View.GONE
                            mBinding.ll3.visibility = View.GONE
                            setTextWatchers()
                            setBackPressListener()
                            setEditTextFocus()

                        }


                    }

                    it.iin == null -> {
                        mBinding.tvError.visibility = View.VISIBLE
                        isCreditCard.set(false)
                        setBackground()
                        mBinding.tvError.text = "Invalid card number"
                        // errorMsgHide()
                    }
                    it.iin.type.equals("debit", true) -> {
                        isCreditCard.set(false)
                        setBackground()
                        mBinding.tvError.visibility = View.VISIBLE
                        mBinding.tvError.text = "Debit cards are not supported"
                        //   errorMsgHide()
                    }
                    /* it.iin.type.equals("credit", true) -> {
                         mBinding.tvError.visibility = View.VISIBLE
                         isCreditCard.set(false)
                         setBackground()
                         mBinding.tvError.text = "Invalid card number"
                     }*/

                    /*it.iin.network.equals("Visa") || it.iin.network.equals("RuPay") ||
                            it.iin.network.equals("Master") || it.iin.network.equals("MasterCard")
                            ->{
                        mBinding.ll1.visibility = View.VISIBLE
                        mBinding.ll2.visibility = View.GONE
                        mBinding.ll3.visibility = View.GONE
                        setTextWatchers()
                        setBackPressListener()
                        setEditTextFocus()
                    }*/


                }





                if (it!!.bank?.outerGridGradientColors != null) {
                    startColor = it.bank?.outerGridGradientColors?.gOne.toString()
                    middleColor = it.bank?.outerGridGradientColors?.gTwo.toString()
                    endColor = it.bank?.outerGridGradientColors?.gThree.toString()

                    setBackGroundGradient(
                        it.bank?.outerGridGradientColors?.gOne.toString(),
                        it.bank?.outerGridGradientColors?.gTwo.toString(),
                        it.bank?.outerGridGradientColors?.gThree.toString(),
                        mBinding.llCardDetails
                    )
                    setBackGroundGradient(
                        it.bank?.outerGridGradientColors?.gOne.toString(),
                        it.bank?.outerGridGradientColors?.gTwo.toString(),
                        it.bank?.outerGridGradientColors?.gThree.toString(),
                        mBinding.llCardBackDetails
                    )
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        viewModel!!.dashboardObserver.observe(this) {
            existingProductList = it.products
        }

        viewModel!!.statusObserver.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        viewModel!!.generateOrderObserver.observe(this) {
            orderId = it.id.toString()
            email = it.customerData.email.toString()

            resultLauncher.launch(
                Intent(this, CardVerificationActivity::class.java).putExtra("NAME", name)
                    .putExtra("DATE", date).putExtra("CVV", cvv).putExtra("CARD_NO", cardNumber)
                    .putExtra("NETWORK", network).putExtra("TYPE", type)
                    .putExtra("SUB_TYPE", subType).putExtra("ISSUER_CODE", issuerCode)
                    .putExtra("ISSUER_NAME", issuerName).putExtra("INTERNATIONAL", international)
                    .putExtra("productId", productId).putExtra("EXISTING", isExisting)
                    .putExtra("BANK_MASTER_ID", bankMasterId).putExtra("BANK_LOGO", logo)
                    .putExtra("ORDER_ID", orderId).putExtra("EMAIL", email)
            )

        }

        /*    viewModel!!.cardStatusObserver.observe(this) {
                bottomSheetDialog.dismiss()

                startActivity(
                    Intent(this, PaymentSuccessfulActivity::class.java).putExtra(
                        "BANK_NAME", issuerName
                    ).putExtra("NAME", name).putExtra("CARD_NO", cardNumber)
                        .putExtra("NETWORK", network).putExtra("BANK_LOGO", logo)
                        .putExtra("START_COLOR", startColor).putExtra("MIDDLE_COLOR", middleColor)
                        .putExtra("END_COLOR", endColor)
                )
                println("colorCodes $startColor $middleColor $endColor")
                finish()
            }*/

        viewModel?.productByIdObserver?.observe(this) {

            /*  startActivity(
                  Intent(this, PaymentSuccessfulActivity::class.java).putExtra(
                      "BANK_NAME", issuerName
                  ).putExtra("NAME", name).putExtra("CARD_NO", cardNumber)
                      .putExtra("NETWORK", network).putExtra("BANK_LOGO", logo)
                      .putExtra("START_COLOR", startColor).putExtra("MIDDLE_COLOR", middleColor)
                      .putExtra("END_COLOR", endColor)
              )*/

            bottomSheetDialog.dismiss()
            openSucessfullBottomSheet(cardNumber.takeLast(4))
            println("colorCodes $startColor $middleColor $endColor")

        }

        viewModel!!.addcardObserver.observe(this) {
            // orderId = it.id.toString()
            //  email = it.customerData.email.toString()
            if (it!=null) {
                Utils.hideProgressDialog()
                productId = it.productId ?: ""

                /* resultLauncher.launch(
                         Intent(this, CardVerificationActivity::class.java)
                             .putExtra("NAME", name)
                             .putExtra("DATE", date)
                             .putExtra("CVV", cvv)
                             .putExtra("CARD_NO", cardNumber)
                             .putExtra("NETWORK", network)
                             .putExtra("TYPE", type)
                             .putExtra("SUB_TYPE", subType)
                             .putExtra("ISSUER_CODE", issuerCode)
                             .putExtra("ISSUER_NAME", issuerName)
                             .putExtra("INTERNATIONAL", international)
                             .putExtra("productId", productId)
                             .putExtra("EXISTING", isExisting)
                             .putExtra("BANK_MASTER_ID", bankMasterId)
                             .putExtra("BANK_LOGO", logo)
                             .putExtra("ORDER_ID", orderId)
                             .putExtra("EMAIL", email)
                     )*/
                if (it?.status == true ) {
                    var urlString = Utils.decodeString(it.paymentLink.toString())

                    print("Decode String ${urlString}")
                    startActivity(Intent(CardDetailsActivity@ this, WebViewActivity::class.java).apply {
                        putExtra("url", urlString)
                    })
                } else {
                    Toast.makeText(this, "${it.userErrorMessage ?: it.apiMessage}", Toast.LENGTH_SHORT).show()
                }
            }


        }

        viewModel!!.progressObserver.observe(this) {
            if (it) {
                Utils.showProgressDialog(this)
            } else {
                Utils.hideProgressDialog()
            }
        }
    }

    inline fun errorMsgHide() {
        var handler = Handler()

        val r: Runnable = object : Runnable {
            override fun run() {
                mBinding.tvError.visibility = View.GONE
                handler.postDelayed(this, 6000)
            }
        }

        handler.postDelayed(r, 6000)
    }

    fun getCardNumberFromTextView() {
        cardNumber = ""


        when {
            isActivateFlow -> {
                cardNumber = mBinding.etOne.text.toString().trim()
                cardNumber += mBinding.etTwo.text.toString().trim()
                cardNumber += mBinding.etThree.text.toString().trim()
                cardNumber += mBinding.etFour.text.toString().trim()
                cardNumber += mBinding.etFive.text.toString().trim()
                cardNumber += mBinding.etSix.text.toString().trim()
                cardNumber += mBinding.etSeven.text.toString().trim()
                cardNumber += mBinding.etEight.text.toString().trim()
                cardNumber += mBinding.etNine.text.toString().trim()
                cardNumber += mBinding.etTen.text.toString().trim()
                cardNumber += mBinding.etEleven.text.toString().trim()
                cardNumber += mBinding.etTwelve.text.toString().trim()

                if (cardNumber.length == 12) {
                    cardNumber += mBinding.etThirteen.text.toString().trim()
                    cardNumber += mBinding.etFourteen.text.toString().trim()
                    cardNumber += mBinding.etFifteen.text.toString().trim()
                    cardNumber += mBinding.etSixteen.text.toString().trim()
                }
            }
            else -> {
                cardNumber = mBinding.etOne.text.toString().trim()
                cardNumber += mBinding.etTwo.text.toString().trim()
                cardNumber += mBinding.etThree.text.toString().trim()
                cardNumber += mBinding.etFour.text.toString().trim()
                cardNumber += mBinding.etFive.text.toString().trim()
                cardNumber += mBinding.etSix.text.toString().trim()
                cardNumber += mBinding.etSeven.text.toString().trim()
                cardNumber += mBinding.etEight.text.toString().trim()
                cardNumber += mBinding.etNine.text.toString().trim()
                cardNumber += mBinding.etTen.text.toString().trim()
                cardNumber += mBinding.etEleven.text.toString().trim()
                cardNumber += mBinding.etTwelve.text.toString().trim()
                cardNumber += mBinding.etThirteen.text.toString().trim()
                cardNumber += mBinding.etFourteen.text.toString().trim()
                cardNumber += mBinding.etFifteen.text.toString().trim()
                cardNumber += mBinding.etSixteen.text.toString().trim()
            }
        }

        validateCard()
        println("CARD $cardNumber")
    }

    fun getCardNumberFromTextView_1() {
        cardNumber = ""

        when {
            isActivateFlow -> {
                cardNumber = mBinding.etOne1.text.toString().trim()
                cardNumber += mBinding.etTwo1.text.toString().trim()
                cardNumber += mBinding.etThree1.text.toString().trim()
                cardNumber += mBinding.etFour1.text.toString().trim()
                cardNumber += mBinding.etFive1.text.toString().trim()
                cardNumber += mBinding.etSix1.text.toString().trim()
                cardNumber += mBinding.etSeven1.text.toString().trim()
                cardNumber += mBinding.etEight1.text.toString().trim()
                cardNumber += mBinding.etNine1.text.toString().trim()
                cardNumber += mBinding.etTen1.text.toString().trim()
                cardNumber += mBinding.etEleven1.text.toString().trim()
                cardNumber += mBinding.etTwelve1.text.toString().trim()

                if (cardNumber.length == 12) {
                    cardNumber += mBinding.etThirteen1.text.toString().trim()
                    cardNumber += mBinding.etFourteen1.text.toString().trim()
                    cardNumber += mBinding.etFifteen1.text.toString().trim()
                    cardNumber += mBinding.etSixteen1.text.toString().trim()
                }
            }
            else -> {
                cardNumber = mBinding.etOne1.text.toString().trim()
                cardNumber += mBinding.etTwo1.text.toString().trim()
                cardNumber += mBinding.etThree1.text.toString().trim()
                cardNumber += mBinding.etFour1.text.toString().trim()
                cardNumber += mBinding.etFive1.text.toString().trim()
                cardNumber += mBinding.etSix1.text.toString().trim()
                cardNumber += mBinding.etSeven1.text.toString().trim()
                cardNumber += mBinding.etEight1.text.toString().trim()
                cardNumber += mBinding.etNine1.text.toString().trim()
                cardNumber += mBinding.etTen1.text.toString().trim()
                cardNumber += mBinding.etEleven1.text.toString().trim()
                cardNumber += mBinding.etTwelve1.text.toString().trim()
                cardNumber += mBinding.etThirteen1.text.toString().trim()
                cardNumber += mBinding.etFourteen1.text.toString().trim()
                cardNumber += mBinding.etFifteen1.text.toString().trim()
                cardNumber += mBinding.etSixteen1.text.toString().trim()
            }
        }
//        if (cardDetailResponse?.iin?.network.equals("American Express")) {
//            validateCard()
//            setTextWatchers_1()
//            setTextWatchers_2()
//        }
        validateCard()
//        println("CARD $cardNumber")
    }

    fun getCardNumberFromTextView_2() {
        cardNumber = ""

        when {
            isActivateFlow -> {
                cardNumber = mBinding.etOne2.text.toString().trim()
                cardNumber += mBinding.etTwo2.text.toString().trim()
                cardNumber += mBinding.etThree2.text.toString().trim()
                cardNumber += mBinding.etFour2.text.toString().trim()
                cardNumber += mBinding.etFive2.text.toString().trim()
                cardNumber += mBinding.etSix2.text.toString().trim()
                cardNumber += mBinding.etSeven2.text.toString().trim()
                cardNumber += mBinding.etEight2.text.toString().trim()
                cardNumber += mBinding.etNine2.text.toString().trim()
                cardNumber += mBinding.etTen2.text.toString().trim()
                cardNumber += mBinding.etEleven2.text.toString().trim()
                cardNumber += mBinding.etTwelve2.text.toString().trim()

                if (cardNumber.length == 12) {
                    cardNumber += mBinding.etThirteen2.text.toString().trim()
                    cardNumber += mBinding.etFourteen2.text.toString().trim()
                    cardNumber += mBinding.etFifteen2.text.toString().trim()
                    cardNumber += mBinding.etSixteen2.text.toString().trim()
                }
            }
            else -> {
                cardNumber = mBinding.etOne2.text.toString().trim()
                cardNumber += mBinding.etTwo2.text.toString().trim()
                cardNumber += mBinding.etThree2.text.toString().trim()
                cardNumber += mBinding.etFour2.text.toString().trim()
                cardNumber += mBinding.etFive2.text.toString().trim()
                cardNumber += mBinding.etSix2.text.toString().trim()
                cardNumber += mBinding.etSeven2.text.toString().trim()
                cardNumber += mBinding.etEight2.text.toString().trim()
                cardNumber += mBinding.etNine2.text.toString().trim()
                cardNumber += mBinding.etTen2.text.toString().trim()
                cardNumber += mBinding.etEleven2.text.toString().trim()
                cardNumber += mBinding.etTwelve2.text.toString().trim()
                cardNumber += mBinding.etThirteen2.text.toString().trim()
                cardNumber += mBinding.etFourteen2.text.toString().trim()
                cardNumber += mBinding.etFifteen2.text.toString().trim()
                cardNumber += mBinding.etSixteen2.text.toString().trim()
            }
        }


        validateCard()
//        println("CARD $cardNumber")
    }

    private fun setBankDetails(name: String, image: String, network: String) {
        mBinding.tvBankName.text = name
        mBinding.tvBankName2.text = name
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        Glide.with(this).load(image).placeholder(R.drawable.ic_group_1366).into(mBinding.ivLogo)
        Glide.with(this).load(image).placeholder(R.drawable.ic_group_1366).into(mBinding.ivLogo2)
        mBinding.ivLogo.colorFilter = ColorMatrixColorFilter(colorMatrix);
        when {
            network.equals("visa", true) -> {
                mBinding.ivCardType.setImageDrawable(getDrawable(R.drawable.visa))
                mBinding.ivCardType2.setImageDrawable(getDrawable(R.drawable.visa))
            }
            network.equals("mastercard", true) -> {
                mBinding.ivCardType.setImageDrawable(getDrawable(R.drawable.ic_mastercard))
                mBinding.ivCardType2.setImageDrawable(getDrawable(R.drawable.ic_mastercard))
            }
            network.equals("Diners Club", true) -> {
                mBinding.ivCardType.setImageDrawable(getDrawable(R.drawable.ic_diners))
                mBinding.ivCardType1.setImageDrawable(getDrawable(R.drawable.ic_diners))
                mBinding.ivCardType2.setImageDrawable(getDrawable(R.drawable.ic_diners))
                mBinding.ivCardType3.setImageDrawable(getDrawable(R.drawable.ic_diners))
            }
            network.equals("American Express", true) -> {


                mBinding.ivCardType.setImageDrawable(getDrawable(R.drawable.ic_amex))
                mBinding.ivCardType1.setImageDrawable(getDrawable(R.drawable.ic_amex))
                mBinding.ivCardType2.setImageDrawable(getDrawable(R.drawable.ic_amex))
                mBinding.ivCardType3.setImageDrawable(getDrawable(R.drawable.ic_amex))

            }
            else -> {
                mBinding.ivCardType.setImageDrawable(getDrawable(R.drawable.card_image))
                mBinding.ivCardType2.setImageDrawable(getDrawable(R.drawable.card_image))
                mBinding.ivCardType3.setImageDrawable(getDrawable(R.drawable.card_image))
            }
        }
    }

    private fun validateCard() {
        when {

            cardNumber.length < 6 -> {

                isCreditCard.set(false)
                setBackground()
                isCardValid.set(false)
                mBinding.ll1.visibility = View.VISIBLE
                mBinding.ll2.visibility = View.GONE
                mBinding.ll3.visibility = View.GONE
                mBinding.tvError.visibility = View.GONE
                var bankName = "Card Name"
                var bankLogo = ""

                if (intent.getStringExtra("bankName") != null) {

                    bankName = intent.getStringExtra("bankName").toString()
                    bankLogo = intent.getStringExtra("bankLogo").toString()

                }
                setBankDetails(bankName, bankLogo, "")

            }
            cardNumber.length == 6 -> {


                //viewModel!!.fetchCardDetailsType(cardNumber)
                isCreditCard.set(false)
                setBackground()
                isCardValid.set(false)

                mBinding.tvError.visibility = View.GONE

            }
            getVAlidaCardNumber() && !CreditCardUtils.check(cardNumber) || !isCreditCard.get() -> {
                setBackground()
                isCardValid.set(false)
                mBinding.tvError.visibility = View.VISIBLE
                mBinding.tvError.text = "Invalid card number"
                //  errorMsgHide()
            }
            getVAlidaCardNumber() && CreditCardUtils.check(cardNumber) && isCreditCard.get() -> {
                setBackground()
                if (intent.getStringExtra("bankName") != null && intent.getStringExtra("bankName")
                        .toString() != mBinding.tvBankName.text.toString()
                ) {
                    mBinding.tvError.visibility = View.VISIBLE
                    mBinding.tvError.text = "Wrong Card"
                    //  errorMsgHide()
                } else {
                    isCardValid.set(true)

                    mBinding.tvError.visibility = View.GONE
                    checkExistingCard()
                }
            }
            else ->{
                isCardValid.set(false)
            }

        }
    }

    fun getVAlidaCardNumber(): Boolean {
        if (cardDetailResponse?.iin?.network.equals("American Express") && cardNumber.length == 15) {
            return true
        } else if (cardDetailResponse?.iin?.network.equals("Diners Club") && cardNumber.length == 14) {
            return true
        } else return cardNumber.length == 16

    }

    private fun checkExistingCard() {
        val lastFourNumbers = cardNumber.takeLast(4)

        if (existingProductList.isNotEmpty()) {
            for (elements in existingProductList) {
                when {
                    elements.last4 == lastFourNumbers -> {
                        if (elements.productStatus == "1") {
                            productId = elements.id
                            isCardValid.set(true)
                            mBinding.tvError.visibility = View.GONE
                            break
                        } else {
                            isCardValid.set(false)
                            mBinding.tvError.visibility = View.VISIBLE
                            mBinding.tvError.text = "This card is already added"
                            //   errorMsgHide()
                            break
                        }
                    }
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

    }

    @SuppressLint("SetTextI18n")
    private fun setupUI() {
        SharePrefs.instance!!.putBoolean(SharePrefsKeys.IS_CARD_SECURE, false)
        val filter = InputFilter { source, start, end, dest, dstart, dend ->
            if (source != null && blockCharacterSet.contains("" + source)) {
                ""
            } else null
        }
        Utils.setLightStatusBar(this)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_card_details)
        mBinding.activity = this
        mBinding.nextButton.text = "Next"
        mBinding.etOne.requestFocus()
        SharePrefs.instance!!.putBoolean(SharePrefsKeys.IS_CARD_SECURE, false)
        mBinding.etName.setFilters(arrayOf<InputFilter>(filter))

        mBinding.tvCardHolderName.text = SharePrefs.instance!!.getString(SharePrefsKeys.FIRST_NAME)
            .toString() + " " + SharePrefs.instance!!.getString(SharePrefsKeys.LAST_NAME).toString()

        name = SharePrefs.instance!!.getString(SharePrefsKeys.FIRST_NAME)
            .toString() + " " + SharePrefs.instance!!.getString(SharePrefsKeys.LAST_NAME).toString()

        mBinding.etName.setText(name)

        mBinding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                name = p0.toString()
                onCardDetailsFilled()
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        mBinding.etCardExpiry.onFocusChangeListener = onCardDetailsFocused()
        mBinding.etCvv.onFocusChangeListener = onCVVFocused()

        mBinding.etName.onFocusChangeListener = onEditNameFocused()

        mBinding.etCvv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (p0.toString().length == 3) {
                    if (cardTypeCC.equals("Diners Club") || cardTypeCC.equals("Other")) {
                        isCVVFilled.set(true)
                        cvv = p0.toString()
                        mBinding.btnCvv.setBackgroundResource(R.drawable.ic_cvv_btm_un_focused)
                    } else {
                        isCVVFilled.set(false)
                    }

                } else if (p0.toString().length == 4) {
                    if (cardTypeCC.equals("American Express")) {
                        isCVVFilled.set(true)
                        cvv = p0.toString()
                        mBinding.btnCvv.setBackgroundResource(R.drawable.ic_cvv_btm_un_focused)
                    } else {
                        isCVVFilled.set(false)
                    }

                } else {
                    isCVVFilled.set(false)
                }


                /* if (p0.toString().length == 3) {
                     if (cardTypeCC.equals("Diners Club")) {
                         isCVVFilled.set(true)
                         cvv = p0.toString()
                         mBinding.btnCvv.setBackgroundResource(R.drawable.ic_cvv_btm_un_focused)
                     } else {
                         isCVVFilled.set(true)
                         cvv = p0.toString()
                         mBinding.btnCvv.setBackgroundResource(R.drawable.ic_cvv_btm_un_focused)
                     }

                 }
                 if (p0.toString().length == 4) {
                     if (cardTypeCC.equals("American Express")) {
                         isCVVFilled.set(true)
                         cvv = p0.toString()
                         mBinding.btnCvv.setBackgroundResource(R.drawable.ic_cvv_btm_un_focused)
                     }

                 } else {
                     isCVVFilled.set(false)
                 }*/
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

                        println("EXECUTED ++++++++++ " + CardExpiryUtils.checkMonthValidity(str.toInt()))

                        if (!CardExpiryUtils.checkMonthValidity(str.toInt())) {
                            mBinding.nestedScroll.scrollTo(0, mBinding.nestedScroll.bottom)
                            isMonthValid = false
                            mBinding.tvError.visibility = View.VISIBLE
                            mBinding.tvError.text = "Invalid expiry date"
                            scrollTobottom()
                            //   errorMsgHide()
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

                            mBinding.tvError.text = "Invalid expiry date"
                            scrollTobottom()
                            //     errorMsgHide()
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
                println("LENGT +++ " + mBinding.etCardExpiry.text.length)
                if (mBinding.etCardExpiry.text.length == 2) {
                    mBinding.etCardExpiry.setText(mBinding.etCardExpiry.text.toString()[0].toString())
                    mBinding.etCardExpiry.setSelection(mBinding.etCardExpiry.text.length);
                }
            }
            false
        }

        setTextWatchers()
        setBackPressListener()
        setEditTextFocus()

        /*   setTextWatchers_1()
           setBackPressListener_1()
           setEditTextFocus_1()
           setTextWatchers_2()
           setBackPressListener_2()
           setEditTextFocus_2()*/

    }

    fun closeScreen() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun setEditTextFocus() {
        mBinding.etOne.onFocusChangeListener = editTextFocusManager(mBinding.viewOne)
        mBinding.etTwo.onFocusChangeListener = editTextFocusManager(mBinding.viewTwo)
        mBinding.etThree.onFocusChangeListener = editTextFocusManager(mBinding.viewThree)
        mBinding.etFour.onFocusChangeListener = editTextFocusManager(mBinding.viewFour)
        mBinding.etFive.onFocusChangeListener = editTextFocusManager(mBinding.viewFive)
        mBinding.etSix.onFocusChangeListener = editTextFocusManager(mBinding.viewSix)
        mBinding.etSeven.onFocusChangeListener = editTextFocusManager(mBinding.viewSeven)
        mBinding.etEight.onFocusChangeListener = editTextFocusManager(mBinding.viewEight)
        mBinding.etNine.onFocusChangeListener = editTextFocusManager(mBinding.viewNine)
        mBinding.etTen.onFocusChangeListener = editTextFocusManager(mBinding.viewTen)
        mBinding.etEleven.onFocusChangeListener = editTextFocusManager(mBinding.viewEleven)
        mBinding.etTwelve.onFocusChangeListener = editTextFocusManager(mBinding.viewTwelve)
        mBinding.etThirteen.onFocusChangeListener = editTextFocusManager(mBinding.viewThirteen)
        mBinding.etFourteen.onFocusChangeListener = editTextFocusManager(mBinding.viewFourteen)
        mBinding.etFifteen.onFocusChangeListener = editTextFocusManager(mBinding.viewFifteen)
        mBinding.etSixteen.onFocusChangeListener = editTextFocusManager(mBinding.viewSixteen)
    }

    private fun setEditTextFocus_1() {
        mBinding.etOne1.onFocusChangeListener = editTextFocusManager(mBinding.viewOne1)
        mBinding.etTwo1.onFocusChangeListener = editTextFocusManager(mBinding.viewTwo1)
        mBinding.etThree1.onFocusChangeListener = editTextFocusManager(mBinding.viewThree1)
        mBinding.etFour1.onFocusChangeListener = editTextFocusManager(mBinding.viewFour1)
        mBinding.etFive1.onFocusChangeListener = editTextFocusManager(mBinding.viewFive1)
        mBinding.etSix1.onFocusChangeListener = editTextFocusManager(mBinding.viewSix1)
        mBinding.etSeven1.onFocusChangeListener = editTextFocusManager(mBinding.viewSeven1)
        mBinding.etEight1.onFocusChangeListener = editTextFocusManager(mBinding.viewEight1)
        mBinding.etNine1.onFocusChangeListener = editTextFocusManager(mBinding.viewNine1)
        mBinding.etTen1.onFocusChangeListener = editTextFocusManager(mBinding.viewTen1)
        mBinding.etEleven1.onFocusChangeListener = editTextFocusManager(mBinding.viewEleven1)
        mBinding.etTwelve1.onFocusChangeListener = editTextFocusManager(mBinding.viewTwelve1)
        mBinding.etThirteen1.onFocusChangeListener = editTextFocusManager(mBinding.viewThirteen1)
        mBinding.etFourteen1.onFocusChangeListener = editTextFocusManager(mBinding.viewFourteen1)
        mBinding.etFifteen1.onFocusChangeListener = editTextFocusManager(mBinding.viewFifteen1)
        mBinding.etSixteen1.onFocusChangeListener = editTextFocusManager(mBinding.viewSixteen1)
    }

    private fun setEditTextFocus_2() {
        mBinding.etOne2.onFocusChangeListener = editTextFocusManager(mBinding.viewOne2)
        mBinding.etTwo2.onFocusChangeListener = editTextFocusManager(mBinding.viewTwo2)
        mBinding.etThree2.onFocusChangeListener = editTextFocusManager(mBinding.viewThree2)
        mBinding.etFour2.onFocusChangeListener = editTextFocusManager(mBinding.viewFour2)
        mBinding.etFive2.onFocusChangeListener = editTextFocusManager(mBinding.viewFive2)
        mBinding.etSix2.onFocusChangeListener = editTextFocusManager(mBinding.viewSix2)
        mBinding.etSeven2.onFocusChangeListener = editTextFocusManager(mBinding.viewSeven2)
        mBinding.etEight2.onFocusChangeListener = editTextFocusManager(mBinding.viewEight2)
        mBinding.etNine2.onFocusChangeListener = editTextFocusManager(mBinding.viewNine2)
        mBinding.etTen2.onFocusChangeListener = editTextFocusManager(mBinding.viewTen2)
        mBinding.etEleven2.onFocusChangeListener = editTextFocusManager(mBinding.viewEleven2)
        mBinding.etTwelve2.onFocusChangeListener = editTextFocusManager(mBinding.viewTwelve2)
        mBinding.etThirteen2.onFocusChangeListener = editTextFocusManager(mBinding.viewThirteen2)
        mBinding.etFourteen2.onFocusChangeListener = editTextFocusManager(mBinding.viewFourteen2)
        mBinding.etFifteen2.onFocusChangeListener = editTextFocusManager(mBinding.viewFifteen2)
        mBinding.etSixteen2.onFocusChangeListener = editTextFocusManager(mBinding.viewSixteen2)
    }

    private fun setTextWatchers() {

        mBinding.etOne.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (mBinding.etOne.text.toString().length == 1) {
                    mBinding.etTwo.requestFocus()
                }
                getCardNumberFromTextView()

            }
        })
        mBinding.etOne.setOnFocusChangeListener { view, b ->
            if (  mBinding.etOne.text.toString().isNotEmpty()) {

                mBinding.etTwo.requestFocus()

            }
        }
        mBinding.etTwo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {

                if (mBinding.etTwo.text.toString().length == 1) {
                    mBinding.etThree.requestFocus()
                }
                getCardNumberFromTextView()

            }
        })
        mBinding.etTwo.setOnFocusChangeListener { view, b ->
            if (  mBinding.etTwo.text.toString().isNotEmpty()) {

                mBinding.etThree.requestFocus()

            }
        }
        mBinding.etThree.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (mBinding.etThree.text.toString().length == 1) {
                    mBinding.etFour.requestFocus()
                }

                getCardNumberFromTextView()

            }
        })
        mBinding.etThree.setOnFocusChangeListener { view, b ->
            if (  mBinding.etThree.text.toString().isNotEmpty()) {

                mBinding.etFour.requestFocus()

            }
        }
        mBinding.etFour.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {

                if (mBinding.etFour.text.toString().length == 1) {
                    mBinding.etFive.requestFocus()
                }
                getCardNumberFromTextView()
            }
        })
        mBinding.etFour.setOnFocusChangeListener { view, b ->
            if (  mBinding.etFour.text.toString().isNotEmpty()) {

                mBinding.etFive.requestFocus()

            }
        }
        mBinding.etFive.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {

                if (mBinding.etFive.text.toString().length == 1) {
                    mBinding.etSix.requestFocus()
                }
                getCardNumberFromTextView()
            }
        })
        mBinding.etFive.setOnFocusChangeListener { view, b ->
            if (  mBinding.etFive.text.toString().isNotEmpty()) {

                mBinding.etSix.requestFocus()

            }
        }
        mBinding.etSix.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (mBinding.etSix.text.toString().length == 1) {
                    mBinding.etSeven.requestFocus()
                }
                getCardNumberFromTextView()
            }
        })
        mBinding.etFive.setOnFocusChangeListener { view, b ->
            if ( mBinding.etFive.text.toString().isNotEmpty()) {

                mBinding.etSix.requestFocus()

            }
        }
        mBinding.etSeven.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (mBinding.etSeven.text.toString().length == 1) {
                    mBinding.etEight.requestFocus()
                }
                getCardNumberFromTextView()
            }
        })
        mBinding.etEight.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (mBinding.etEight.text.toString().length == 1) {
                    mBinding.etNine.requestFocus()
                }
                getCardNumberFromTextView()
            }
        })
        mBinding.etNine.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (mBinding.etNine.text.toString().length == 1) {
                    mBinding.etTen.requestFocus()
                }

                getCardNumberFromTextView()
            }
        })
        mBinding.etTen.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (mBinding.etTen.text.toString().length == 1) {
                    mBinding.etEleven.requestFocus()
                }

                getCardNumberFromTextView()
            }
        })
        mBinding.etEleven.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (mBinding.etEleven.text.toString().length == 1) {
                    mBinding.etTwelve.requestFocus()
                }
                getCardNumberFromTextView()
            }
        })
        mBinding.etTwelve.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (mBinding.etTwelve.text.toString().length == 1) {
                    mBinding.etThirteen.requestFocus()
                }
                getCardNumberFromTextView()
            }
        })
        mBinding.etThirteen.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (mBinding.etThirteen.text.toString().length == 1) {
                    mBinding.etFourteen.requestFocus()
                }
                getCardNumberFromTextView()
            }
        })
        mBinding.etFourteen.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (mBinding.etFourteen.text.toString().length == 1) {
                    mBinding.etFifteen.requestFocus()
                }
                getCardNumberFromTextView()
            }
        })
        mBinding.etFifteen.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (mBinding.etFifteen.text.toString().length == 1) {
                    mBinding.etSixteen.requestFocus()
                }
                getCardNumberFromTextView()
            }
        })
        mBinding.etSixteen.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {

                getCardNumberFromTextView()
            }
        })
    }

    private fun setTextWatchers_1() {

//
        mBinding.etSeven1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (mBinding.etSeven1.text.toString().length == 1) {
                    mBinding.etEight1.requestFocus()
                }
                getCardNumberFromTextView_1()
            }
        })
        mBinding.etEight1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (mBinding.etEight1.text.toString().length == 1) {
                    mBinding.etNine1.requestFocus()
                }
                getCardNumberFromTextView_1()
            }
        })
        mBinding.etNine1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (mBinding.etNine1.text.toString().length == 1) {
                    mBinding.etTen1.requestFocus()
                }
                getCardNumberFromTextView_1()
            }
        })
        mBinding.etTen1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (mBinding.etTen1.text.toString().length == 1) {
                    mBinding.etEleven1.requestFocus()
                }
                getCardNumberFromTextView_1()
            }
        })
        mBinding.etEleven1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (mBinding.etEleven1.text.toString().length == 1) {
                    mBinding.etTwelve1.requestFocus()
                }
                getCardNumberFromTextView_1()
            }
        })
        mBinding.etTwelve1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (mBinding.etTwelve1.text.toString().length == 1) {
                    mBinding.etThirteen1.requestFocus()
                }
                getCardNumberFromTextView_1()
            }
        })
        mBinding.etThirteen1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (mBinding.etThirteen1.text.toString().length == 1) {
                    mBinding.etFourteen1.requestFocus()
                }
                getCardNumberFromTextView_1()
            }
        })
        mBinding.etFourteen1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (mBinding.etFourteen1.text.toString().length == 1) {
                    mBinding.etFifteen1.requestFocus()
                }
                getCardNumberFromTextView_1()
            }
        })
        mBinding.etFifteen1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (mBinding.etFifteen1.text.toString().length == 1) {
                    mBinding.etSixteen1.requestFocus()
                }
                getCardNumberFromTextView_1()
            }
        })
        mBinding.etSixteen1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (mBinding.etSixteen1.text.toString().length == 1) {
                    mBinding.etSixteen1.requestFocus()
                }
                getCardNumberFromTextView_1()
            }
        })
    }

    private fun setTextWatchers_2() {


        mBinding.etSeven2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (mBinding.etSeven2.text.toString().length == 1) {
                    mBinding.etEight2.requestFocus()
                }
                getCardNumberFromTextView_2()
            }
        })
        mBinding.etEight2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (mBinding.etEight2.text.toString().length == 1) {
                    mBinding.etNine2.requestFocus()
                }
                getCardNumberFromTextView_2()
            }
        })
        mBinding.etNine2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (mBinding.etNine2.text.toString().length == 1) {
                    mBinding.etTen2.requestFocus()
                }
                getCardNumberFromTextView_2()
            }
        })
        mBinding.etTen2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (mBinding.etTen2.text.toString().length == 1) {
                    mBinding.etEleven2.requestFocus()
                }
                getCardNumberFromTextView_2()
            }
        })
        mBinding.etEleven2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (mBinding.etEleven2.text.toString().length == 1) {
                    mBinding.etTwelve2.requestFocus()
                }
                getCardNumberFromTextView_2()
            }
        })
        mBinding.etTwelve2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (mBinding.etTwelve2.text.toString().length == 1) {
                    mBinding.etThirteen2.requestFocus()
                }
                getCardNumberFromTextView_2()
            }
        })
        mBinding.etThirteen2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (mBinding.etThirteen2.text.toString().length == 1) {
                    mBinding.etFourteen2.requestFocus()
                }
                getCardNumberFromTextView_2()
            }
        })
        mBinding.etFourteen2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (mBinding.etFourteen2.text.toString().length == 1) {
                    mBinding.etFifteen2.requestFocus()
                }
                getCardNumberFromTextView_2()
            }
        })
        mBinding.etFifteen2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (mBinding.etFourteen2.text.toString().length == 1) {
                    mBinding.etFifteen2.requestFocus()
                }
                getCardNumberFromTextView_2()
            }
        })
        mBinding.etSixteen2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (mBinding.etSixteen2.text.toString().length == 1) {
                    mBinding.etSixteen2.requestFocus()
                }
                getCardNumberFromTextView_2()
            }
        })
    }

    private fun setBackPressListener() {
        mBinding.etOne.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etOne.text.toString().isNullOrEmpty()) {

                    if (intent.getStringExtra("bankName") != null) {
                        setBankDetails(
                            intent.getStringExtra("bankName").toString(),
                            intent.getStringExtra("bankLogo").toString(),
                            ""
                        )
                    }
                }
            }
            false
        }
        mBinding.etTwo.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etTwo.text.toString().isNullOrEmpty()) {
                    mBinding.etTwo.setText("")
                    mBinding.etOne.requestFocus()
                    if (intent.getStringExtra("bankName") != null) {
                        setBankDetails(
                            intent.getStringExtra("bankName").toString(),
                            intent.getStringExtra("bankLogo").toString(),
                            ""
                        )
                    }
                }
            }
            false
        }

        mBinding.etThree.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etThree.text.toString().isNullOrEmpty()) {
                    mBinding.etThree.setText("")
                    //  mBinding.etTwo.setText("")
                    mBinding.etTwo.requestFocus()
                    if (intent.getStringExtra("bankName") != null) {
                        setBankDetails(
                            intent.getStringExtra("bankName").toString(),
                            intent.getStringExtra("bankLogo").toString(),
                            ""
                        )
                    }
                }
            }
            false
        }

        mBinding.etFour.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etFour.text.toString().isNullOrEmpty()) {
                    mBinding.etFour.setText("")
                    //  mBinding.etThree.setText("")
                    mBinding.etThree.requestFocus()
                    if (intent.getStringExtra("bankName") != null) {
                        setBankDetails(
                            intent.getStringExtra("bankName").toString(),
                            intent.getStringExtra("bankLogo").toString(),
                            ""
                        )
                    }
                }
            }
            false
        }

        mBinding.etFive.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etFive.text.toString().isNullOrEmpty()) {
                    mBinding.etFive.setText("")
                    //  mBinding.etFour.setText("")
                    mBinding.etFour.requestFocus()
                    if (intent.getStringExtra("bankName") != null) {
                        setBankDetails(
                            intent.getStringExtra("bankName").toString(),
                            intent.getStringExtra("bankLogo").toString(),
                            ""
                        )
                    }
                }
            }
            false
        }

        mBinding.etSix.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etSix.text.toString().isNullOrEmpty()) {
                    mBinding.etSix.setText("")
                    //   mBinding.etFive.setText("")
                    mBinding.etFive.requestFocus()
                    if (intent.getStringExtra("bankName") != null) {
                        setBankDetails(
                            intent.getStringExtra("bankName").toString(),
                            intent.getStringExtra("bankLogo").toString(),
                            ""
                        )
                    }
                }
            }
            false
        }

        mBinding.etSeven.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etSeven.text.toString().isNullOrEmpty()) {
                    mBinding.etSeven.setText("")
                    //  mBinding.etSix.setText("")
                    mBinding.etSix.requestFocus()
                }
            }
            false
        }

        mBinding.etEight.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etEight.text.toString().isNullOrEmpty()) {
                    mBinding.etEight.setText("")
                    //  mBinding.etSeven.setText("")
                    mBinding.etSeven.requestFocus()
                }
            }
            false
        }

        mBinding.etNine.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etNine.text.toString().isNullOrEmpty()) {
                    mBinding.etNine.setText("")
                    //  mBinding.etEight.setText("")
                    mBinding.etEight.requestFocus()
                }
            }
            false
        }

        mBinding.etTen.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etTen.text.toString().isNullOrEmpty()) {
                    mBinding.etTen.setText("")
                    // mBinding.etNine.setText("")
                    mBinding.etNine.requestFocus()
                }
            }
            false
        }

        mBinding.etEleven.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etEleven.text.toString().isNullOrEmpty()) {
                    mBinding.etEleven.setText("")
                    // mBinding.etTen.setText("")
                    mBinding.etTen.requestFocus()
                }
            }
            false
        }

        mBinding.etTwelve.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etTwelve.text.toString().isNullOrEmpty()) {
                    mBinding.etTwelve.setText("")
                    // mBinding.etEleven.setText("")
                    mBinding.etEleven.requestFocus()
                }
            }
            false
        }

        mBinding.etThirteen.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etThirteen.text.toString().isNullOrEmpty()) {
                    mBinding.etThirteen.setText("")
                    //  mBinding.etTwelve.setText("")
                    mBinding.etTwelve.requestFocus()
                }
            }
            false
        }

        mBinding.etFourteen.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etFourteen.text.toString().isNullOrEmpty()) {
                    mBinding.etFourteen.setText("")
                    //  mBinding.etThirteen.setText("")
                    mBinding.etThirteen.requestFocus()
                }
            }
            false
        }

        mBinding.etFifteen.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etFifteen.text.toString().isNullOrEmpty()) {
                    mBinding.etFifteen.setText("")
                    //  mBinding.etFourteen.setText("")
                    mBinding.etFourteen.requestFocus()
                }
            }
            false
        }

        mBinding.etSixteen.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etSixteen.text.toString().isNullOrEmpty()) {
                    mBinding.etSixteen.setText("")
                    //   mBinding.etFifteen.setText("")
                    mBinding.etFifteen.requestFocus()
                    isCardValid.set(false)
                }
            }
            false
        }
    }

    private fun setBackPressListener_1() {


        mBinding.etSeven1.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etSeven1.text.toString().isNullOrEmpty()) {
//                    mBinding.etSeven1.setText("")
//                    mBinding.etSix1.requestFocus()
                    mBinding.etOne1.setText("")
                    mBinding.etTwo1.setText("")
                    mBinding.etThree1.setText("")
                    mBinding.etFour1.setText("")
                    mBinding.etFive1.setText("")
                    mBinding.etSix1.setText("")
                    mBinding.etSix.setText("")
                    mBinding.etSeven.setText("")
                    mBinding.etSix.requestFocus()
                    mBinding.ll1.visibility = View.VISIBLE
                    mBinding.ll2.visibility = View.GONE

                }
            }
            false
        }

        mBinding.etEight1.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etEight1.text.toString().isNullOrEmpty()) {
                    //    mBinding.etEight1.setText("")
                    mBinding.etSeven1.setText("")
                    mBinding.etSeven1.requestFocus()
                }
            }
            false
        }

        mBinding.etNine1.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etNine1.text.toString().isNullOrEmpty()) {
                    mBinding.etNine1.setText("")
                    //  mBinding.etNine1.setText("")
                    mBinding.etEight1.requestFocus()
                }
            }
            false
        }

        mBinding.etTen1.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etTen1.text.toString().isNullOrEmpty()) {
                    mBinding.etTen1.setText("")
                    //  mBinding.etNine1.setText("")
                    mBinding.etNine1.requestFocus()
                }
            }
            false
        }

        mBinding.etEleven1.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etEleven1.text.toString().isNullOrEmpty()) {
                    mBinding.etEleven1.setText("")
                    //   mBinding.etTen1.setText("")
                    mBinding.etTen1.requestFocus()
                }
            }
            false
        }

        mBinding.etTwelve1.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etTwelve1.text.toString().isNullOrEmpty()) {
                    isCardValid.set(false)
                    mBinding.etTwelve1.setText("")
                    //  mBinding.etEleven1.setText("")
                    mBinding.etEleven1.requestFocus()
                }
            }
            false
        }

        mBinding.etThirteen1.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etThirteen1.text.toString().isNullOrEmpty()) {
                    isCardValid.set(false)
                    mBinding.etThirteen1.setText("")
                    //  mBinding.etTwelve1.setText("")
                    mBinding.etTwelve1.requestFocus()
                }
            }
            false
        }

        mBinding.etFourteen1.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etFourteen1.text.toString().isNullOrEmpty()) {
                    mBinding.nextButton.isEnabled = false
                    isCardValid.set(false)
                    mBinding.etFourteen1.setText("")
                    //  mBinding.etThirteen1.setText("")
                    mBinding.etThirteen1.requestFocus()

                }
            }
            false
        }

        mBinding.etFifteen1.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etFifteen1.text.toString().isNullOrEmpty()) {
                    mBinding.nextButton.isEnabled = false
                    isCardValid.set(false)
                    mBinding.etFifteen1.setText("")
                    //   mBinding.etFourteen1.setText("")
                    mBinding.etFourteen1.requestFocus()

                }
            }
            false
        }

        mBinding.etSixteen1.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etSixteen1.text.toString().isNullOrEmpty()) {
                    mBinding.nextButton.isEnabled = false
                    isCardValid.set(false)
                    mBinding.etSixteen1.setText("")
                    //  mBinding.etFifteen1.setText("")
                    mBinding.etFifteen1.requestFocus()
                    isCardValid.set(false)
                }
            }
            false
        }
    }

    private fun setBackPressListener_2() {


        mBinding.etSeven2.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etSeven2.text.toString().isNullOrEmpty()) {
                    isCardValid.set(false)
                    mBinding.etOne2.setText("")
                    mBinding.etTwo2.setText("")
                    mBinding.etThree2.setText("")
                    mBinding.etFour2.setText("")
                    mBinding.etFive2.setText("")
                    mBinding.etSix2.setText("")
                    mBinding.etSix.setText("")

                    mBinding.etSix.requestFocus()

                    mBinding.ll1.visibility = View.VISIBLE
                    mBinding.ll3.visibility = View.GONE
                }
            }
            false
        }

        mBinding.etEight2.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etEight2.text.toString().isNullOrEmpty()) {
                    isCardValid.set(false)

                    mBinding.etEight2.setText("")
                    //   mBinding.etSeven2.setText("")
                    mBinding.etSeven2.requestFocus()
                }
            }
            false
        }

        mBinding.etNine2.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etNine2.text.toString().isNullOrEmpty()) {
                    isCardValid.set(false)
                    mBinding.etNine2.setText("")
                    //   mBinding.etEight2.setText("")
                    mBinding.etEight2.requestFocus()
                }
            }
            false
        }

        mBinding.etTen2.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etTen2.text.toString().isNullOrEmpty()) {
                    isCardValid.set(false)
                    mBinding.etTen2.setText("")
                    // mBinding.etNine2.setText("")
                    mBinding.etNine2.requestFocus()
                }
            }
            false
        }

        mBinding.etEleven2.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etEleven2.text.toString().isNullOrEmpty()) {
                    isCardValid.set(false)
                    mBinding.etEleven2.setText("")
                    //   mBinding.etTen2.setText("")
                    mBinding.etTen2.requestFocus()
                }
            }
            false
        }

        mBinding.etTwelve2.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etTwelve2.text.toString().isNullOrEmpty()) {
                    isCardValid.set(false)
                    mBinding.etTwelve2.setText("")
                    //    mBinding.etEleven2.setText("")
                    mBinding.etEleven2.requestFocus()
                }
            }
            false
        }

        mBinding.etThirteen2.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etThirteen2.text.toString().isNullOrEmpty()) {
                    isCardValid.set(false)
                    mBinding.etThirteen2.setText("")
                    //    mBinding.etTwelve2.setText("")
                    mBinding.etTwelve2.requestFocus()
                }
            }
            false
        }

        mBinding.etFourteen2.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etFourteen2.text.toString().isNullOrEmpty()) {
                    mBinding.nextButton.isEnabled = false
                    isCardValid.set(false)
                    mBinding.etFourteen2.setText("")
                    //  mBinding.etThirteen2.setText("")
                    mBinding.etThirteen2.requestFocus()
                }
            }
            false
        }

        mBinding.etFifteen2.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etFifteen2.text.toString().isNullOrEmpty()) {
                    mBinding.nextButton.isEnabled = false
                    isCardValid.set(false)
                    mBinding.etFifteen2.setText("")
                    //  mBinding.etFourteen2.setText("")
                    mBinding.etFourteen2.requestFocus()
                }
            }
            false
        }

        mBinding.etSixteen2.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mBinding.etSixteen2.text.toString().isNullOrEmpty()) {
                    mBinding.nextButton.isEnabled = false
                    isCardValid.set(false)
                    mBinding.etSixteen2.setText("")
                    //  mBinding.etFifteen2.setText("")
                    mBinding.etFifteen2.requestFocus()
                }
            }
            false
        }
    }

    private fun onCardDetailsFocused(): View.OnFocusChangeListener {
        return View.OnFocusChangeListener { _, hasFocused ->
            isCardDetailsFocused.set(hasFocused)
        }
    }

    fun onCardDetailsFilled() {
        if (name.isNotEmpty() && isCardExpiryFilled.get() && isCVVFilled.get()) {
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

    override fun onBackPressed() {

    }

    @SuppressLint("SetTextI18n")
    fun checkActionAndExecute() {
        if (isFront) {
            mBinding.etCardExpiry.requestFocus()
            mBinding.etCardExpiry.setSelection(mBinding.etCardExpiry.length())
            mBinding.llSecureButton.visibility = View.VISIBLE
            mBinding.nextButton.visibility = View.GONE
            mBinding.llRewards.visibility = View.GONE
            mBinding.llCardDetails.visibility = View.GONE
            mBinding.llCardBackDetails.visibility = View.VISIBLE
            Utils.showKeyboard()

            mBinding.tvProfileDesc.text = "We will use these details to pay to your card"
            isFront = false

            /*   mBinding.tvCardNumberNew.text = cardNumber.subSequence(0, 4)
                   .toString() + " " + cardNumber.subSequence(4, 8).toString() + " " +
                       cardNumber.subSequence(8, 12)
                           .toString() + " " + cardNumber.subSequence(12, 16).toString()*/

            mBinding.tvCardNumberNew.text = "XXXX  " + cardNumber.takeLast(4)/*cardNumber.subSequence(0, 4)
                .toString() + " " + cardNumber.subSequence(4, 6)
                .toString() + "XX" + " " + "XXXX" + " " + cardNumber.subSequence(12, 16).toString()*/

        }
    }

    fun setVisibility() {
        mBinding.llCarHolderName.visibility = View.GONE
        mBinding.llCardHolderNameInput.visibility = View.VISIBLE

        mBinding.btnSecureCard.text = "Next"
        mBinding.etName.setSelection(mBinding.etName.text.length)
        mBinding.etName.requestFocus()
        Utils.showKeyboard()
    }

    fun openProcessingBottomSheet() {
        SharePrefs.instance!!.putBoolean(SharePrefsKeys.IS_CARD_ACTIVATE, false)
        bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialog)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_common)
        bottomSheetDialog.setCancelable(false)

        val tvTitle = bottomSheetDialog.findViewById<AppCompatTextView>(R.id.bs_tv_title)
        val tvMessage = bottomSheetDialog.findViewById<AppCompatTextView>(R.id.bs_tv_message)
        val tvRetryButton = bottomSheetDialog.findViewById<AppCompatButton>(R.id.bs_btn_retry)
        val doNotPressClose =
            bottomSheetDialog.findViewById<AppCompatTextView>(R.id.doNotPressClose)
        val ivClose = bottomSheetDialog.findViewById<AppCompatImageView>(R.id.bs_iv_cancel)
        val lottieAnimation = bottomSheetDialog.findViewById<LottieAnimationView>(R.id.animationTwo)
        val llAnimation = bottomSheetDialog.findViewById<LinearLayoutCompat>(R.id.llAnimation)
        val llCreditCard = bottomSheetDialog.findViewById<LinearLayoutCompat>(R.id.llCreditCard)
        tvMessage!!.text =
            "Please wait while we process the payment. The amount will be refunded back immediately."
        tvTitle!!.text = "Securing your card"

        ivClose!!.visibility = View.INVISIBLE
        doNotPressClose!!.visibility = View.VISIBLE
        llAnimation!!.visibility = View.VISIBLE
        llCreditCard!!.visibility = View.GONE
        tvRetryButton!!.visibility = View.INVISIBLE

        llAnimation.visibility = View.VISIBLE
        llCreditCard.visibility = View.GONE

        var isFirst = true

        lottieAnimation!!.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
                try {
                    if (isFirst) {
                        /*viewModel!!.postCardToServer(
                            PostNewCardDetails(
                                international,
                                logo,
                                issuerName,
                                issuerCode,
                                subType,
                                type,
                                network,
                                "",
                                orderId,
                                razorpayPaymentId,
                                date,
                                cardNumber.toLong(),
                                productId,
                                name,
                                bankMasterId
                            )
                        )*/
                        //viewModel?.getProductById(productId)
                        isFirst = false
                    }
                } catch (ex: Exception) {
                    ex.toString()
                }
            }


        })

        bottomSheetDialog.show()
    }

    fun openSucessfullBottomSheet(lastFour: String) {

        val bottomSheetSuccessDialog = Utils.showCustomDialogBottum(
            this,
            R.layout.bottom_sheet_bill_secure_success,
        )
        val bindingSheet = DataBindingUtil.inflate<BottomSheetBillSecureSuccessBinding>(
            layoutInflater, R.layout.bottom_sheet_bill_secure_success, null, false
        )

        bindingSheet.ivClose.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            bottomSheetSuccessDialog.dismiss()
            finish()
        }
        bindingSheet.bsTvTitle.text = "Your card ending with $lastFour is successfully secured"
        bindingSheet.bsBtnRetry.setOnClickListener {
            if (isComingFrom) {
                goToPaymentScreen(
                    accountNumberNew,
                    accountNumberNew,
                    amountNew.toString().replace(",", ""),
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
                startActivity(Intent(this, MainActivity::class.java))
                bottomSheetSuccessDialog.dismiss()
                finishAffinity()

            }
        }
        bottomSheetSuccessDialog.setContentView(bindingSheet.root)
        bottomSheetSuccessDialog.setCancelable(false)
        bottomSheetSuccessDialog.show()
    }

   /* fun addCardApiCall(isToken: Boolean) {
        Utils.showProgressDialog(this)
        if(!cardDetailResponse?.iin?.network.isNullOrBlank()){
            viewModel!!.addCard(
                AddCardRequest(
                    isToken,
                    bankMasterId,
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
        }
        else{
            mBinding.tvError.visibility=View.VISIBLE
            mBinding.tvError.setText("Card network not found")
        }

        print(
            "Json Data ${
                AddCardRequest(
                    isToken,
                    bankMasterId,
                    productId,
                    name,
                    date.takeLast(2),
                    date.take(2),
                    "credit",
                    cardNumber,
                    cvv,
                    "android",
                    cardDetailResponse?.iin?.network
                )
            }"
        )
    }*/

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
           // addCardApiCall(true)
            bottomSheetDialog.dismiss()
        }
        bindingSheet.tvYesOptOut.setOnClickListener {
           // addCardApiCall(false)
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
        bindingSheet.btnRetry.setOnClickListener {
           // addCardApiCall(true)
            bottomSheetDialog.dismiss()
        }
        bindingSheet.tvOptOut.setOnClickListener {
            openOptOutBottomSheet()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(bindingSheet.root)
        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.show()
    }

    fun checkCardProcess() {
        if (isComingFrom) {
            onPayNowCLick(
                cardNumber,
                "",
                productItem?.product_type,
                "",
                productItem?.bill!!.min_due,
                (productItem?.bill!!.total_due),
                productItem?.bill!!.id,
                productItem?.id,
                productItem?.bill!!.amount_paid,
                productItem?.last4,
                productItem?.bankMasterRecord?.outerGridGradientColors?.gOne ?: "",
                productItem?.bankMasterRecord?.outerGridGradientColors?.gTwo ?: "",
                productItem?.bankMasterRecord?.outerGridGradientColors?.gThree ?: "",
            )
        } else {
            if (isFromHome == "doToken") {
               // addCardApiCall(true)
            } else if (isFromHome == "optOut") {
               // addCardApiCall(false)
            } else {
                // openSecureBottomSheet()
               // addCardApiCall(true)
            }

        }
    }

    @SuppressLint("ResourceAsColor")
    private fun editTextFocusManager(
        view: View
    ): View.OnFocusChangeListener {
        return View.OnFocusChangeListener { _, hasFocused ->
            if (hasFocused) {
                view.setBackgroundResource(R.color.colorPrimaryGreen)
            } else {
                view.setBackgroundResource(R.color.colorTextGrey)
            }
        }
    }

    fun setClickOfEt(lastFour: String) {
        if (!lastFour.isNullOrEmpty()) {


            mBinding.etTwelve1.isEnabled = false
            mBinding.etThirteen1.isEnabled = false
            mBinding.etFourteen1.isEnabled = false
            mBinding.etFifteen1.isEnabled = false


            mBinding.etTwelve1.setText(lastFour[0].toString())
            mBinding.etThirteen1.setText(lastFour[1].toString())
            mBinding.etFourteen1.setText(lastFour[2].toString())
            mBinding.etFifteen1.setText(lastFour[3].toString())

            mBinding.etEleven2.isEnabled = false
            mBinding.etTwelve2.isEnabled = false
            mBinding.etThirteen2.isEnabled = false
            mBinding.etFourteen2.isEnabled = false

            mBinding.etEleven2.setText(lastFour[0].toString())
            mBinding.etTwelve2.setText(lastFour[1].toString())
            mBinding.etThirteen2.setText(lastFour[2].toString())
            mBinding.etFourteen2.setText(lastFour[3].toString())



            mBinding.etSixteen.isEnabled = false
            mBinding.etFifteen.isEnabled = false
            mBinding.etFourteen.isEnabled = false
            mBinding.etThirteen.isEnabled = false
            mBinding.etThirteen.setText(lastFour[0].toString())
            mBinding.etFourteen.setText(lastFour[1].toString())
            mBinding.etFifteen.setText(lastFour[2].toString())
            mBinding.etSixteen.setText(lastFour[3].toString())
        }
    }


    fun setBackground() {
        if (isCreditCard.get()) {
            mBinding.ivChip.setImageDrawable(
                ContextCompat.getDrawable(
                    this, R.drawable.ic_chip
                )
            )
            setBackGroundGradient(startColor, middleColor, endColor, mBinding.llCardDetails)
        } else {
            mBinding.ivChip.setImageDrawable(
                ContextCompat.getDrawable(
                    this, R.drawable.ic_chip_in_active
                )
            )
            mBinding.llCardDetails.setBackgroundResource(R.drawable.card_bg)
            mBinding.ivLogo.setImageDrawable(
                ContextCompat.getDrawable(
                    this, R.drawable.ic_bank_logo_extended
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()

        if (SharePrefs.instance?.getBoolean(SharePrefsKeys.IS_CARD_ACTIVATE) == true) {
            openProcessingBottomSheet()
        }

        if (SharePrefs.instance?.getBoolean(SharePrefsKeys.IS_CARD_SECURE) == true) {
            BottomSheetUtils.showCommonBottomSheet(
                this,
                "Failed to secure card",
                "This will state the reason for failure",
                showCloseButton = true,
                showRetryButton = true,
                false,
                true,
                false
            )
        }

    }


    //payment bottomsheet
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

        val dialog = Utils.showCustomDialogBottum(this, R.layout.bottom_sheet_total_due)
        dialog.setCancelable(false)
        val ivCancel = dialog.findViewById<AppCompatImageView>(R.id.ivCancel)
        val llCardBackGround = dialog.findViewById<LinearLayoutCompat>(R.id.llCardBackGround)
        val llCreditCard = dialog.findViewById<LinearLayoutCompat>(R.id.llCreditCard)
        setBackGroundGradient(startColor, middleColor, endColor, llCreditCard)
        ivCancel.setOnClickListener {
            dialog.dismiss()
            Utils.hideKeyboard(this)
            //  MparticleUtils.logEvent("PayNowBottomSheet", "Payment flow cancelled")
        }

        val iv_Card_Type = dialog.findViewById<AppCompatImageView>(R.id.ivCardType)
        val iv_bank_image = dialog.findViewById<ImageView>(R.id.iv_bank_image)

        Glide.with(this).load(bankLogo).placeholder(R.drawable.bank_logo_placeholder)
            .into(iv_bank_image)


        if (cardType.equals("LOAN")) {
            llCardBackGround.setBackgroundResource(R.drawable.loan_inner_background)
        }

        val ivMore = dialog.findViewById<AppCompatImageView>(R.id.ivMore)
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
        val tvCardNumber = dialog.findViewById<AppCompatTextView>(R.id.tvCardNumber)
        tvCardNumber.text = accountNumber
        if (cardType != "LOAN") {
            if (accountNumber?.length != 16) {
                tvCardNumber.text = "XXXX XXXX XXXX $lastFour"

            } else {
                tvCardNumber.text =
                    (accountNumber.subSequence(0, 4).toString() + " " + accountNumber.subSequence(
                        4, 8
                    ).toString() + " " + accountNumber.subSequence(8, 12)
                        .toString() + " " + accountNumber.subSequence(12, 16).toString())
            }
        } else {
            tvCardNumber.text = "XXXX  $lastFour"
        }


        val tvBankName = dialog.findViewById<AppCompatTextView>(R.id.tvBankName)
        tvBankName.text = bankName

        val etAmount = dialog.findViewById<EditText>(R.id.etAmount)
        etAmount.setText(Utils.getFormattedDecimal(totalDue!!.toDouble()))


        val tvCaption = dialog.findViewById<AppCompatTextView>(R.id.tvCaption)
        val chipTotalDue = dialog.findViewById<Chip>(R.id.chipTotalDue)
        val chipMinimumDue = dialog.findViewById<Chip>(R.id.chipMinimumDue)
        val customChip = dialog.findViewById<Chip>(R.id.chipCustom)

        val llMessageMinimumDue = dialog.findViewById<LinearLayoutCompat>(R.id.llMessageMinimumDue)
        val llMessageTotalDue = dialog.findViewById<LinearLayoutCompat>(R.id.llMessageTotalDue)
        val llAmtView = dialog.findViewById<LinearLayoutCompat>(R.id.llAmtView)
        val llMessageError = dialog.findViewById<LinearLayoutCompat>(R.id.llMessageError)
        val tvError = dialog.findViewById<TextView>(R.id.tvError)
        val ivLoanError = dialog.findViewById<AppCompatImageView>(R.id.ivLoanError)
        val btnOkay = dialog.findViewById<AppCompatButton>(R.id.btnOkay)
        if (cardType == "LOAN") {
            tvError.setTextColor(Color.BLACK)
            ivLoanError.setImageResource(R.drawable.ic_warning)
        }

        chipTotalDue.setOnClickListener {
            etAmount.setText(Utils.getFormattedDecimal(totalDue.toDouble()))
            etAmount.isEnabled = false
            llMessageError.visibility = View.GONE
            llMessageTotalDue.visibility = View.VISIBLE
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
        }

        if (cardType == "MasterCard") {
            iv_Card_Type.setImageResource(R.drawable.ic_mastercard)
        }
        if (cardType == "Visa") {
            iv_Card_Type.setImageResource(R.drawable.visa)
        }



        etAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (p0.toString().isNotEmpty()) {
                    val amount = p0.toString().replace(",", "").toDouble()
                    if (amount > totalDue) {
                        tvError.text = "Entered amount is more than total due"
                        llMessageError.visibility = View.VISIBLE
                    } else if (amount < 1) {
                        llMessageError.visibility = View.VISIBLE
                        tvError.text = "Minimum amount should be 1"
                        btnOkay.isEnabled = false
                    } else if (amount > 500000) {
                        llMessageError.visibility = View.VISIBLE
                        tvError.text = "Amount should be less than 500000"
                        btnOkay.isEnabled = false
                    } else {
                        llMessageError.visibility = View.GONE
                        btnOkay.isEnabled = true
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
                llAmtView.setBackgroundResource(R.drawable.et_btm_bg)
            } else {
                llAmtView.setBackgroundResource(R.drawable.et_btm_bg_un_focused)
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
                etAmount.setText(Utils.getFormattedDecimal(minDue.toDouble()))
                etAmount.isEnabled = false
                llMessageError.visibility = View.GONE
                llMessageTotalDue.visibility = View.GONE
                llMessageMinimumDue.visibility = View.VISIBLE
                tvCaption.text = resources.getText(R.string.you_have_to_pay)
                this?.let { it1 ->
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
            etAmount.isEnabled = true
            etAmount.requestFocus()
            etAmount.setText("")
            btnOkay.isEnabled = false
            llMessageTotalDue.visibility = View.GONE
            llMessageMinimumDue.visibility = View.GONE
            tvCaption.text = "Enter Amount"
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
            accountNumberNew = accountNumber.toString()
            cardTypeNew = cardType.toString()
            amountNew = etAmount.text.toString().replace(",", "")
            bankNameNew = bankName.toString()
            billIdNew = billId.toString()
            bankLogoNew = bankLogo.toString()
            openSecureBottomSheet()
            Utils.hideKeyboard(this)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun openKnowMoreDialog() {
        //   MparticleUtils.logEvent("PayNowBottomSheet", "Know more button clicked")

        val bottomSheetDialog = BottomSheetDialog(this)

        bottomSheetDialog.setContentView(R.layout.bottom_sheet_know_more)
        bottomSheetDialog.setCancelable(false)

        val btnSubmit = bottomSheetDialog.findViewById<AppCompatButton>(R.id.btnSubmit)

        btnSubmit!!.setOnClickListener {
            bottomSheetDialog.dismiss()
            this?.let { it1 -> Utils.showKeyboard(it1) }
            //  MparticleUtils.logEvent("PayNowBottomSheet", "Know more done button button clicked")
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
        print("payment amount $amount")
        startActivity(
            Intent(this, PaymentMethodsActivity::class.java).putExtra(
                "card_number", accountNumber
            ).putExtra("card_type", cardType).putExtra("amount", amount)
                .putExtra("bank_name", bankName).putExtra("lastFour", lastFour)
                .putExtra("bill_id", billId).putExtra("productId", productId)
                .putExtra("BANK_LOGO", bankLogo).putExtra("START_COLOR", startColor)
                .putExtra("MIDDLE_COLOR", middleColor).putExtra("END_COLOR", endColor)
                .putExtra("BANK_MASTER_ID", bankMasterId)
        )
    }

    fun scrollTobottom() {
        val handler = Handler()
        handler.postDelayed({
            mBinding.nestedScroll.smoothScrollTo(
                0, mBinding.llLayout.getBottom()
            )
        }, 100)
    }

}


