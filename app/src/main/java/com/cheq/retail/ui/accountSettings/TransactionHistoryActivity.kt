package com.cheq.retail.ui.accountSettings

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.constants.AFConstants
import com.cheq.retail.constants.AFConstants.AMERICAN_CARD
import com.cheq.retail.constants.AFConstants.CAPTURED
import com.cheq.retail.constants.AFConstants.CC
import com.cheq.retail.constants.AFConstants.CREATED
import com.cheq.retail.constants.AFConstants.DINERS_CARD
import com.cheq.retail.constants.AFConstants.FAILED
import com.cheq.retail.constants.AFConstants.LOAN
import com.cheq.retail.constants.AFConstants.MASTER_CARD
import com.cheq.retail.constants.AFConstants.PROCESSING
import com.cheq.retail.constants.AFConstants.REFUNDED
import com.cheq.retail.constants.AFConstants.RUPAY_CARD
import com.cheq.retail.constants.AFConstants.SEPARATOR
import com.cheq.retail.constants.AFConstants.VISA
import com.cheq.retail.databinding.ActivityTransactionHistoryBinding
import com.cheq.retail.extensions.bankMasterUrl
import com.cheq.retail.extensions.loanMasterUrl
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.accountSettings.model.ReqHistoryDetail
import com.cheq.retail.ui.accountSettings.model.TxnLists
import com.cheq.retail.ui.accountSettings.viewmodel.AccountSettingsViewModel
import com.cheq.retail.ui.accountSettings.webView.adapter.TransactionDetailAdapter
import com.cheq.retail.utils.*
import com.cheq.retail.utils.ImageUtils.loadSvg
import com.cheq.retail.utils.Utils.Companion.createFreshChatUser
import com.freshchat.consumer.sdk.Freshchat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import java.io.*
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class TransactionHistoryActivity : BaseActivity() {
    private var mLastClickTime: Long = 0
    private val delayTime: Int = 1500
    lateinit var prefs: SharePrefs
    lateinit var mBinding: ActivityTransactionHistoryBinding
    private var accountSettingsViewModel: AccountSettingsViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
        setUi()
        setUpObserver()
        observeTransactionDetail()
        //  MparticleUtils.logCurrentScreen(this::class.java.simpleName)
    }

    private fun setUpObserver() {
        accountSettingsViewModel?.transactionListResponseObserver?.observe(this) {
            if (it != null) {
                if (it.txnLists?.isNotEmpty() == true){
                    var transactionDetailAdapter = TransactionDetailAdapter(this, it.txnLists)
                    mBinding.rvTxnHistory.visibility = View.VISIBLE
                    mBinding.flNoTxnHistory.visibility = View.GONE
                    mBinding.rvTxnHistory.apply {
                        adapter = transactionDetailAdapter
                        hasFixedSize()
                    }
                    transactionDetailAdapter.onItemClick = { it1 ->
                        if (SystemClock.elapsedRealtime() - mLastClickTime > delayTime) {
                            mBinding.loading.visibility=View.VISIBLE
                            val request = ReqHistoryDetail(it1?.id, it1?.cheqUniTxnId)
                            accountSettingsViewModel?.getTransactionDetail(request)
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                    }
                }else{
                    mBinding.rvTxnHistory.visibility = View.GONE
                    mBinding.flNoTxnHistory.visibility = View.VISIBLE
                }

            }

        }
    }

    private fun observeTransactionDetail() {
        accountSettingsViewModel?.transactionDetailResponseObserver?.observe(this) {
            mBinding.loading.visibility=View.GONE
            if (it?.txnLists != null) {
                showBottomSheetDialog(it.txnLists)
            }

        }
    }

    lateinit var rltvPaidSuccess: RelativeLayout
    lateinit var rltvPaidFailed: RelativeLayout
    lateinit var ivShare: AppCompatImageView
    lateinit var ivFailed: AppCompatImageView
    lateinit var txtFailed: AppCompatTextView
    lateinit var txtBillStatus: AppCompatTextView
    lateinit var txtCreditedInLbl: AppCompatTextView
    var txtCreditedIn: AppCompatTextView? = null
    var txtMayTake: AppCompatTextView? = null

    @SuppressLint("SetTextI18n")
    private fun showBottomSheetDialog(transaction: TxnLists) {
        try {
            val bottomSheetDialog = BottomSheetDialog(this)
            bottomSheetDialog.setContentView(R.layout.bottom_sheet_trans_history)

            val txtBillAmount = bottomSheetDialog.findViewById<AppCompatTextView>(R.id.txtBillAmount)
            val llPrarent = bottomSheetDialog.findViewById<LinearLayoutCompat>(R.id.ll_parent)
            val txtEarned = bottomSheetDialog.findViewById<AppCompatTextView>(R.id.txtEarned)
            txtBillStatus = bottomSheetDialog.findViewById<AppCompatTextView>(R.id.txtBillStatus)!!
            val txtCompletedOn = bottomSheetDialog.findViewById<AppCompatTextView>(R.id.txtCompletedOn)
            txtCreditedIn = bottomSheetDialog.findViewById(R.id.txtCreditedIn)
            txtMayTake = bottomSheetDialog.findViewById(R.id.txtMayTake)
            txtCreditedInLbl = bottomSheetDialog.findViewById<AppCompatTextView>(R.id.txtCreditedInLbl)!!
            val txtTransactionId = bottomSheetDialog.findViewById<AppCompatTextView>(R.id.txtTransactionId)
            rltvPaidFailed = bottomSheetDialog.findViewById<RelativeLayout>(R.id.rltv_paid_failed)!!
            rltvPaidSuccess = bottomSheetDialog.findViewById<RelativeLayout>(R.id.rltv_paid_success)!!
            val linearCheqChips = bottomSheetDialog.findViewById<LinearLayoutCompat>(R.id.linear_cheq_chips)
            val linearBankAmount = bottomSheetDialog.findViewById<LinearLayoutCompat>(R.id.linear_bank_amount)
            val rltvAdjusted = bottomSheetDialog.findViewById<RelativeLayout>(R.id.rltvAdjusted)
            ivShare = bottomSheetDialog.findViewById<AppCompatImageView>(R.id.ivShare)!!
            ivFailed = bottomSheetDialog.findViewById<AppCompatImageView>(R.id.ivFailed)!!
            val txtBillChipAmount = bottomSheetDialog.findViewById<AppCompatTextView>(R.id.txtBillChipAmount)
            val txtBillCashAmount = bottomSheetDialog.findViewById<AppCompatTextView>(R.id.txtBillCashAmount)
            txtFailed = bottomSheetDialog.findViewById<AppCompatTextView>(R.id.txtFailedReason)!!
            val txtBillPayMethod = bottomSheetDialog.findViewById<AppCompatTextView>(R.id.txtBillPayMethod)
            val txtAdjusted = bottomSheetDialog.findViewById<AppCompatTextView>(R.id.txtAdjusted)
            val txtBankName = bottomSheetDialog.findViewById<AppCompatTextView>(R.id.txtBankName)
            val tvBankName = bottomSheetDialog.findViewById<AppCompatTextView>(R.id.tvBankName)
            val txtCardNumber = bottomSheetDialog.findViewById<AppCompatTextView>(R.id.txtCardNumber)
            val tvCardNumber = bottomSheetDialog.findViewById<AppCompatTextView>(R.id.tvCardNumber)
            val txtCompletedOnLbl = bottomSheetDialog.findViewById<AppCompatTextView>(R.id.txtCompletedOnLbl)
            val txtTransactionIdLbl = bottomSheetDialog.findViewById<AppCompatTextView>(R.id.txtTransactionIdLbl)
            // val cardViewStroke = bottomSheetDialog.findViewById<CardView>(R.id.cardViewStroke)
            val frameLoan = bottomSheetDialog.findViewById<FrameLayout>(R.id.frameLoan)
            val ivBankImage = bottomSheetDialog.findViewById<AppCompatImageView>(R.id.iv_bank_image_cardview)
            val ivEarned = bottomSheetDialog.findViewById<AppCompatImageView>(R.id.ivEarned)
            val ivCredited = bottomSheetDialog.findViewById<AppCompatImageView>(R.id.ivCredited)
            val ivClose = bottomSheetDialog.findViewById<AppCompatImageView>(R.id.ivClose)
            val ivCardTypeCard =
                bottomSheetDialog.findViewById<AppCompatImageView>(R.id.ivCardTypeCard)
            val ivbankImageLoan =
                bottomSheetDialog.findViewById<AppCompatImageView>(R.id.iv_bank_image)
            val ivBbpsLogo = bottomSheetDialog.findViewById<AppCompatImageView>(R.id.iv_bbps_logo)
            val ivPaymentTypeLogo =
                bottomSheetDialog.findViewById<AppCompatImageView>(R.id.ivPaymentTypeLogo)
            val flSolidColor = bottomSheetDialog.findViewById<ImageView>(R.id.flSolidColor)
            val flStroke = bottomSheetDialog.findViewById<FrameLayout>(R.id.flStroke)
            val layoutCreditCard =
                bottomSheetDialog.findViewById<FrameLayout>(R.id.layoutCreditCard)
            val llCardSolidBackGround =
                bottomSheetDialog.findViewById<LinearLayoutCompat>(R.id.llCardSolidBackGround)
            val txtProcessingFees =
                bottomSheetDialog.findViewById<AppCompatTextView>(R.id.txtProcessingFees)
            val llRRN = bottomSheetDialog.findViewById<LinearLayoutCompat>(R.id.llRRN)
            val llBBPS = bottomSheetDialog.findViewById<LinearLayoutCompat>(R.id.llBBPS)
            val llCC = bottomSheetDialog.findViewById<LinearLayoutCompat>(R.id.llCC)
            val txtRRNLbl = bottomSheetDialog.findViewById<AppCompatTextView>(R.id.txtRRNLbl)
            val txtRRNId = bottomSheetDialog.findViewById<AppCompatTextView>(R.id.txtRRNId)
            val ivCopyTxnId = bottomSheetDialog.findViewById<AppCompatImageView>(R.id.ivCopyTxnId)
            val ivCopyRRNId = bottomSheetDialog.findViewById<AppCompatImageView>(R.id.ivCopyRRNId)
            val ivCopyBBPSId = bottomSheetDialog.findViewById<AppCompatImageView>(R.id.ivCopyBBPSId)
            val txtBBPSTransactionId =
                bottomSheetDialog.findViewById<AppCompatTextView>(R.id.txtBBPSTransactionId)

            val item = transaction.productDetails?.instrumentMaster;
            var imageUrl = ""

            if ((item?.uiConfig?.cardColor != null) && (item.uiConfig.opacityBorder != null) && (item.uiConfig.opacityTopLeft != null) && (item.uiConfig.opacityBottomRight != null)) {
                GradientUtils.setBoarderStroke(
                    item?.uiConfig?.cardColor ?: "#FFFFFF",
                    item?.uiConfig?.opacityBorder ?: "#FFFFFF",
                    true,
                    flStroke!!
                )
                GradientUtils.setBackGround(
                    item?.uiConfig?.cardColor ?: "#FFFFFF",
                    "",
                    item?.uiConfig?.opacityTopLeft ?: "#FFFFFF",
                    item?.uiConfig?.opacityBottomRight ?: "#FFFFFF",
                    llCardSolidBackGround!!)

                /*val gfgGradient = GradientDrawable(GradientDrawable.Orientation.TL_BR,
                    intArrayOf(
                        0X00AE282E.toInt(),
                        0X1FAE282E.toInt(),
                        0X1FAE282E.toInt(),
                        0X1FAE282E.toInt()
                    ))
                gfgGradient.setStroke(5,0X4DAE282E)
                rltvStroke!!.background = gfgGradient*/


            }
            try {
                if (transaction.rewardEarnRewardPoints != null && transaction.rewardEarnRewardPoints != 0) {
                    txtEarned!!.text =
                        "You earned " + transaction.rewardEarnRewardPoints + " CheQ Chips"
                } else {
                    txtEarned!!.text = "You earned " + 0 + " CheQ Chips"
                    /* txtEarned!!.visibility = View.GONE
                     ivEarned!!.visibility = View.GONE*/
                }
            } catch (e: Exception) {
            }
            if (transaction.summaryTxnStartedAt != null) {
                txtCompletedOn!!.text =
                    formattedDateFromDate(transaction.summaryTxnStartedAt ?: "") + " at " + getTimeFromDate(
                        transaction.summaryTxnStartedAt
                    )
            }

            if (transaction.productDetails?.productType != null && transaction.productDetails.productType.equals(LOAN, true)) {
                layoutCreditCard!!.visibility = View.GONE
                frameLoan!!.visibility = View.VISIBLE
                imageUrl = "${prefs.loanMasterUrl}${item?.id}-logo-with-name.svg"
                ivbankImageLoan!!.loadSvg(imageUrl)
                if (item?.billerName != null) tvBankName!!.text = item.billerName
                tvCardNumber!!.text = "··· " + transaction.productDetails?.last4
                if (item?.uiConfig != null && item.uiConfig.cardColor != null) {
                    flSolidColor?.setBackgroundResource(R.drawable.loan_bottom_shap)
                    val bg: Drawable? = flSolidColor?.background
                    bg?.setTint(Color.parseColor(item.uiConfig.cardColor))

                }
                llCC?.visibility = View.GONE
                llBBPS?.visibility = View.VISIBLE
                txtBBPSTransactionId?.text = transaction.billCheqUniTxnId
                ivCopyBBPSId?.setOnClickListener {
                    Utils.getClipboardText(txtBBPSTransactionId?.text.toString())
                }

            } else {
                layoutCreditCard!!.visibility = View.VISIBLE
                frameLoan!!.visibility = View.GONE
                txtCardNumber!!.text = "··· " + transaction.productDetails?.last4
                imageUrl = "${prefs.bankMasterUrl}${item?.id}-logo-with-name.svg"
                txtBankName!!.text = item?.bankName
                ivBankImage?.loadSvg(imageUrl)
                ivCardTypeCard?.setImageResource(cardTypeImage(transaction))
                txtTransactionIdLbl!!.text = getString(R.string.transactional_id)
                ivBbpsLogo!!.visibility = View.GONE
            }

            txtBillAmount!!.text =
                "${getString(R.string.rupee_symbol)}${transaction.billDisplayAmount.toString()}"

            if (transaction.billPayinStatus?.uppercase() == REFUNDED && (transaction.billPayoutStatus == null || transaction.billPayoutStatus.uppercase() == FAILED || transaction.billPayoutStatus == "" || transaction.billPayoutStatus == "null")) {
                setFailureView(
                    R.drawable.ic_refunded_info,
                    transaction.narration ?: getString(R.string.money_debited_has_been_refunded),
                    ContextCompat.getColor(context, R.color.golden_color),
                    getString(R.string.refunded)
                )

                if (transaction.refundRRN != null && transaction.productDetails?.productType.equals(CC, true)) {
                    llRRN?.visibility = View.VISIBLE
                    txtRRNId?.text = transaction.refundRRN
                }
            } else if (transaction.billPayinStatus?.uppercase() == PROCESSING && (transaction.billPayoutStatus == null || transaction.billPayoutStatus == "" || transaction.billPayoutStatus == "null")) {
                setFailureView(
                    R.drawable.ic_pending_info,
                    transaction.narration?:   getString(R.string.attempting_to_get_latest_status),
                    ContextCompat.getColor(context, R.color.golden_color),
                    getString(R.string.pending)
                )
            } else if (transaction.billPayinStatus?.uppercase() == FAILED || transaction.billPayoutStatus?.uppercase() == FAILED) {
                setFailureView(
                    R.drawable.ic_payment_failed,
                    transaction.narration?:    getString(R.string.money_debited_will_be_refunded),
                    ContextCompat.getColor(context, R.color.amount_red),
                    getString(R.string.failed))
            } else if (transaction.billPayinStatus?.uppercase() == CAPTURED && (transaction.billPayoutStatus == null || transaction.billPayoutStatus.uppercase() == CREATED || transaction.billPayoutStatus.uppercase() == PROCESSING)) {
                ivCredited?.setImageResource(R.drawable.ic_pay_time_green)
                setSuccessView(transaction)
            } else {
                ivCredited?.setImageResource(R.drawable.ic_check_right)
                setSuccessView(transaction)
                if (transaction.payoutUtr != null && transaction.productDetails?.productType.equals(CC, true)) {
                    llRRN?.visibility = View.VISIBLE
                    txtRRNId?.text = transaction.payoutUtr
                    txtRRNLbl?.text = getString(R.string.utr)
                }
            }
            if (transaction.billPaidTogether == 1) {
                rltvAdjusted!!.visibility = View.VISIBLE
                txtBillStatus.text = getString(R.string.paid_together)
                txtAdjusted!!.text =
                    "Adjusted from ${getString(R.string.rupee_symbol)}${transaction.summaryAmount} Paid Together on " + formattedDateFromDate(
                        transaction.summaryTxnStartedAt!!
                    )
            } else
                rltvAdjusted!!.visibility = View.GONE

            if (transaction.billChipAmount != null && transaction.billChipAmount > 0) {
                txtBillChipAmount!!.visibility = View.VISIBLE
                linearCheqChips!!.visibility = View.VISIBLE
                txtBillChipAmount!!.text =
                    getString(R.string.rupee_symbol) + " " + transaction.billChipAmount.toString()
            } else {
                txtBillChipAmount!!.visibility = View.GONE
                linearCheqChips!!.visibility = View.GONE
            }
            if (transaction.billCashAmount != null && transaction.billCashAmount > 0) {
                txtBillCashAmount!!.visibility = View.VISIBLE
                linearBankAmount!!.visibility = View.VISIBLE
                txtBillCashAmount!!.text =
                    getString(R.string.rupee_symbol) + " " + transaction.billCashAmount.toString()
            } else {
                txtBillCashAmount!!.visibility = View.GONE
                linearBankAmount!!.visibility = View.GONE
            }

            txtBillPayMethod!!.text = transaction.billPaymentMethod.toString()
            txtTransactionId!!.text = transaction.billCheqUniTxnId

            if(!transaction.feeNaration.isNullOrEmpty()) {
                txtProcessingFees?.visibility = View.VISIBLE
                txtProcessingFees?.text = transaction.feeNaration
            }else{
                txtProcessingFees?.visibility = View.GONE
            }

            /* if (transaction.billPayinFailureReason != null && transaction.billPayoutFailureReason != null)
                 txtFailedReason!!.text = transaction.billPayinFailureReason.toString() + " " + transaction.billPayoutFailureReason.toString()
             else if (transaction.billPayinFailureReason != null)
                 txtFailedReason!!.text = transaction.billPayinFailureReason.toString()
             else if (transaction.billPayoutFailureReason != null)
                 transaction.billPayoutFailureReason.toString()*/

            ivClose?.setOnClickListener {
                bottomSheetDialog.dismiss()
            }

            ivShare.setOnClickListener {
                try {
                    shareImage(
                        getBitmapFromView(llPrarent!!)!!,
                        transaction.billBillAmount ?: 0,
                        transaction.rewardEarnRewardPoints ?: 0
                    )
                } catch (e: Exception) {
                    Toast.makeText(context, "No App Available", Toast.LENGTH_SHORT).show()
                }
            }
            ivCopyTxnId?.setOnClickListener {
                Utils.getClipboardText(txtTransactionId.text.toString())
            }
            ivCopyRRNId?.setOnClickListener {
                Utils.getClipboardText(txtRRNId?.text.toString())
            }


            bottomSheetDialog.show()
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun setSuccessView(transaction: TxnLists) {
        rltvPaidSuccess.visibility = View.VISIBLE
        rltvPaidFailed.visibility = View.GONE
        ivShare.visibility = View.VISIBLE

        if (!transaction.payout_ect_narration.isNullOrEmpty() && transaction.payout_ect_narration.contains(SEPARATOR)) {
            try{
            val payOutNarration = transaction.payout_ect_narration.split(SEPARATOR)
            if (payOutNarration.isNotEmpty()) {
                try{
                val hours = getNumericValue(payOutNarration[0])
                if(hours!=Char.MIN_VALUE){
                val narration = payOutNarration[0].split(hours)
                if (narration.isNotEmpty())
                    txtCreditedInLbl.text = narration[0]
                txtCreditedIn?.text = payOutNarration[0].substring(payOutNarration[0].indexOf(hours))
                if (payOutNarration.size>1 && payOutNarration[1].isNotEmpty() && !transaction.summaryTxnStartedAt.isNullOrEmpty())
                    txtMayTake?.text = payOutNarration[1].replace(AFConstants.DATE_TAG, formattedDateFromDate(transaction.summaryTxnStartedAt ?: "")).trim()
                }
                }catch(exception:Exception) {
                    txtCreditedInLbl.text = payOutNarration[0]
                    if (payOutNarration.size>1 && payOutNarration[1].isNotEmpty() && !transaction.summaryTxnStartedAt.isNullOrEmpty())
                        txtMayTake?.text = payOutNarration[1].replace(AFConstants.DATE_TAG, formattedDateFromDate(transaction.summaryTxnStartedAt ?: "")).trim()
                }
            }
            }catch(exception:Exception) {

            }
        }
    }

    private fun getNumericValue(str:String):Char{
        for (i in str.indices) {
            if(Character.isDigit(str[i])){
                return str[i]
            }
        }
        return Char.MIN_VALUE

    }
    private fun setFailureView(
        ivFailedIcon: Int,
        failedMessage: String, color: Int, billStatus: String
    ) {
        rltvPaidSuccess.visibility = View.GONE
        rltvPaidFailed.visibility = View.VISIBLE
        ivShare.visibility = View.GONE
        ivFailed.setImageResource(ivFailedIcon)
        txtFailed.text = failedMessage
        txtFailed.setTextColor(color)
        txtBillStatus.text = billStatus
    }

    private fun cardTypeImage(transaction: TxnLists): Int {
        return when (transaction.productDetails?.cardNetwork) {
            MASTER_CARD -> R.drawable.ic_mastercard
            VISA -> R.drawable.visa
            DINERS_CARD -> R.drawable.ic_dinner_icon
            AMERICAN_CARD -> R.drawable.ic_american_icon
            RUPAY_CARD -> R.drawable.ic_rupay_card_icon
            else -> R.drawable.ic_mastercard
        }
    }

    fun createBitmap(view: View, amount: Int, chips: Int) {
        try {
            view.isDrawingCacheEnabled = true
            val bitmap = Bitmap.createBitmap(view.drawingCache)
            shareImage(bitmap, amount, chips)

        } catch (io: FileNotFoundException) {
            io.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


    fun getBitmapFromView(view: View): Bitmap? {
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) bgDrawable.draw(canvas) else canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return returnedBitmap
    }

    //contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
    fun shareImage(bitmap: Bitmap, amount: Int, chips: Int) {


        val contentUri: Uri
        contentUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        val contentResolver = applicationContext.contentResolver
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "filename")
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);
        val imageContentUri = contentResolver.insert(contentUri, contentValues)
        try {
            contentResolver.openFileDescriptor(imageContentUri!!, "w", null).use { fileDescriptor ->
                val fd: FileDescriptor = fileDescriptor!!.fileDescriptor
                val outputStream: OutputStream = FileOutputStream(fd)
                val bufferedOutputStream = BufferedOutputStream(outputStream)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bufferedOutputStream)
                bufferedOutputStream.flush()
                bufferedOutputStream.close()
            }
        } catch (e: IOException) {
        }


        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_STREAM, imageContentUri)
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Wohoo! I just paid ${getString(R.string.rupee_symbol)}$amount on CheQ and earned $chips CheQ Chips. Join CheQ now and earn CheQ Chips"
        )
        sendIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        sendIntent.type = "image/*"
        val shareIntent = Intent.createChooser(sendIntent, "Share with")
        startActivity(shareIntent)
    }

    private val REQUEST_EXTERNAL_STORAGe = 1
    private val permissionstorage = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    // verifying if storage permission is given or not
    fun verifystoragepermissions(activity: Activity?) {
        val permissions = ActivityCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        // If storage permission is not given then request for External Storage Permission
        if (permissions != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, permissionstorage, REQUEST_EXTERNAL_STORAGe)
        }
    }

    fun formattedDateFromDate(ServerDate: String): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val odt = OffsetDateTime.parse(ServerDate);
            val dtf = DateTimeFormatter.ofPattern("dd-MMM", Locale.ENGLISH);
            return dtf.format(odt)
        }
        return ""
    }

    fun getTimeFromDate(dateFormat: String?): String {
        try {
            val date = OffsetDateTime.parse(dateFormat)
            val dtf = DateTimeFormatter.ofPattern("hh:mma");
            return dtf.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    private fun setUpViewModel() {
        accountSettingsViewModel = ViewModelProvider(this)[AccountSettingsViewModel::class.java]
    }

    private fun setUi() {
        prefs = SharePrefs.getInstance(this)!!;
        Utils.setLightStatusBar(this)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_transaction_history)
        accountSettingsViewModel?.getTransactionList()
        mBinding.ivBack.setOnClickListener {
            onBackPressed()
        }
        mBinding.ivHelp.setOnClickListener {
            createFreshChatUser()
            Freshchat.showConversations(applicationContext)
        }

        MparticleUtils.logCurrentScreen(
            "/transaction-history",
            "The customer views the transaction history screen",
            "",
            "",
            "",
            "",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.TRANSACTION_HISTORY),
            this
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigateToParent()
    }

}