package com.cheq.retail.ui.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cheq.retail.R
import com.cheq.retail.databinding.BottomSheetRemoveCardBinding
import com.cheq.retail.databinding.LayoutProductsListBinding
import com.cheq.retail.extensions.bankMasterUrl
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsReferral
import com.cheq.retail.ui.activateCard.CardDetailsActivityNew
import com.cheq.retail.ui.login.modelv2.productv1.ProductV2
import com.cheq.retail.ui.main.fragment.ProductAdapterListener
import com.cheq.retail.ui.main.model.SelectOfferResponseItem
import com.cheq.retail.utils.DateTimeUtils
import com.cheq.retail.utils.GradientUtils
import com.cheq.retail.utils.ImageUtils.loadSvg
import com.cheq.retail.utils.SwipeLayout
import com.cheq.retail.utils.Utils
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*


class ProductsAdapterNew(
    private val activity: Context,
    private val productInterface: ProductAdapterListener,
    private var list: MutableList<ProductV2>,
    private var fbRewardsRateList: List<SelectOfferResponseItem>? = null
) : RecyclerView.Adapter<ProductsAdapterNew.ViewHolder>() {
    private var binding: LayoutProductsListBinding? = null
    private var layoutInflater: LayoutInflater? = null
    lateinit var prefs: SharePrefs
    private var prevSwipedLayout: SwipeLayout? = null
    private val ANIM_DURATION = 2000L
    private val ZERO_AXIS = 0.0f
    private val TO_Y_DELTA = 20.0f
    private val REPEAT_MODE = 2

    private var mFirebaseDatabase: DatabaseReference? = null
    private var mFirebaseInstance: FirebaseDatabase? = null
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        prefs = SharePrefs.getInstance(activity)!!
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.context)
        }
        binding = DataBindingUtil.inflate(
            layoutInflater!!, R.layout.layout_products_list, viewGroup, false
        )

        return ViewHolder(binding!!)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        try {
            setSwipeListener(viewHolder)

            val item = list[position]
            val colorMatrix = ColorMatrix()
            val imageUrl = "${prefs.bankMasterUrl}${item.bankMasterRecord?.id}-logo-with-name.svg"
            val imageUrlBack = "${prefs.bankMasterUrl}${item.bankMasterRecord?.id}-card-image.svg"
            if (item.bankMasterRecord?.ui_config != null) {
                GradientUtils.setBoarderStroke(
                    item.bankMasterRecord.ui_config.cardColor,
                    item.bankMasterRecord.ui_config.opacity_border,
                    true,
                    viewHolder.binding.clStroke
                )

                GradientUtils.setBackGround(
                    item.bankMasterRecord.ui_config.cardColor,
                    "",
                    item.bankMasterRecord.ui_config.opacity_topLeft,
                    item.bankMasterRecord.ui_config.opacity_bottomRight,
                    viewHolder.binding.llCardBackGround
                )

                GradientUtils.setBackGroundGradient(
                    "#FFFFFF",
                    "#FFFFFF",
                    "#FFFFFF",
                    viewHolder.binding.flShadow
                )
            }
            viewHolder.binding.ivMenu.setOnClickListener {
                clearAnimation(viewHolder)
                hideAnimationViews(viewHolder)
                viewHolder.binding.swipeLayout.open()
            }
            viewHolder.binding.linearRemove.setOnClickListener {
                openRemoveCardBottomSheet(item, viewHolder, position)
            }
            if (SharePrefsReferral.getInstance(activity)
                    ?.isCardShown(SharePrefsKeys.IS_CARD_SHOWN) == false && position == 0
            ) {
                SharePrefsReferral.getInstance(activity)?.setCardShown(SharePrefsKeys.IS_CARD_SHOWN,true)
                showAnimationViews(viewHolder)
                startAnimation(viewHolder)
            } else {
                hideAnimationViews(viewHolder)
            }
            viewHolder.binding.txtGotIt.setOnClickListener {
                clearAnimation(viewHolder)
                hideAnimationViews(viewHolder)
            }
            viewHolder.binding.tvCardNumber.text = "··· ${item.last4}"
            viewHolder.binding.ivBankImage.loadSvg(imageUrl)
            viewHolder.binding.ivLogoBack.loadSvg(imageUrlBack)
            viewHolder.binding.tvBankName.text = item.bankMasterRecord?.bankName
            if (item.customer_name.isNotEmpty()) {
                viewHolder.binding.tvCardHolderName.text = item.customer_name
            } else {
                viewHolder.binding.tvCardHolderName.text = SharePrefs.getInstance(activity)
                    ?.getString(SharePrefsKeys.FIRST_NAME) + " " + SharePrefs.getInstance(activity)
                    ?.getString(SharePrefsKeys.LAST_NAME)
            }

            if (item.card_network != null) {
                viewHolder.binding.icFrame.visibility = View.VISIBLE
                when (item.card_network) {
                    CardDetailsActivityNew.MASTER_CARD -> {
                        viewHolder.binding.ivCardType.setImageResource(R.drawable.ic_mastercard)
                    }
                    CardDetailsActivityNew.VISA -> {
                        viewHolder.binding.ivCardType.setImageResource(R.drawable.visa)
                    }
                    CardDetailsActivityNew.RUPAY -> {
                        viewHolder.binding.ivCardType.setImageResource(R.drawable.ic_rupay_card_icon)
                    }
                    CardDetailsActivityNew.DINERS_CLUB -> {
                        viewHolder.binding.ivCardType.setImageResource(R.drawable.ic_dinner_icon)
                    }
                    CardDetailsActivityNew.AMERICAN_EXPRESS -> {
                        viewHolder.binding.ivCardType.setImageResource(R.drawable.ic_american_icon)
                    }
                }

            } else {
                viewHolder.binding.icFrame.visibility = View.GONE
            }
            if (item.productStatus == ProductStatus.ProductStatusOne.status || item.productStatus == ProductStatus.ProductStatusDefault.status) {
                viewHolder.binding.llCaseFive.visibility = View.GONE
                viewHolder.binding.llCaseTwo.visibility = View.GONE
                viewHolder.binding.llCaseFour.visibility = View.GONE
                viewHolder.binding.llCaseOne.visibility = View.VISIBLE
                viewHolder.binding.llCaseThree.visibility = View.GONE
                viewHolder.binding.tvEarnOne.text = "Earn ${item.rewardRate}%"
                viewHolder.binding.llConfirm.setOnClickListener {
                    productInterface.onActiveClick(
                        item.id,
                        item.last4,
                        item.bankMasterRecord?.id,
                        item.bankMasterRecord?.bankName ?: "",
                        "",
                        "",
                        "",
                        item
                    )
                }
                viewHolder.binding.tvPayAndEarnCaseOne.text = "Pay & earn ${item.rewardRate}%\nCheQ Chips"
            }


            if (item.productStatus == ProductStatus.ProductStatusTwo.status) {
                viewHolder.binding.llCaseTwo.visibility = View.VISIBLE
                viewHolder.binding.llCaseFour.visibility = View.GONE
                viewHolder.binding.llCaseOne.visibility = View.GONE
                viewHolder.binding.llCaseThree.visibility = View.GONE
                viewHolder.binding.llCaseFive.visibility = View.GONE
                viewHolder.binding.tvEarnTwo.text = "Earn ${item.rewardRate}%"
                if (item.bill != null) {
                    val c = Calendar.getInstance().time
                    val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val formattedDate = df.format(c)
                    val remainingDays = DateTimeUtils.getDaysBetweenDates(
                        formattedDate, item.bill.due_date.replaceRange(
                            10, item.bill.due_date.length, ""
                        )
                    ).toString().toInt()

                    when {
                        remainingDays < -1 -> {
                            viewHolder.binding.tvDueDate.text =
                                "Overdue by ${remainingDays.toString().replace("-", "")} Days"
                            viewHolder.binding.tvDueDate.setTextColor(activity.getColor(R.color.red))
                        }
                        remainingDays == -1 -> {
                            viewHolder.binding.tvDueDate.text =
                                activity.getString(R.string.str_over_due_by_one)
                            viewHolder.binding.tvDueDate.setTextColor(activity.getColor(R.color.red))
                        }
                        remainingDays == 0 -> {
                            viewHolder.binding.tvDueDate.text =
                                activity.getString(R.string.str_due_today)
                            viewHolder.binding.tvDueDate.setTextColor(activity.getColor(R.color.red))
                        }
                        remainingDays == 1 -> {
                            viewHolder.binding.tvDueDate.text =
                                activity.getString(R.string.str_due_in_one_day)
                            viewHolder.binding.tvDueDate.setTextColor(activity.getColor(R.color.red))
                        }
                        remainingDays in 2..7 -> {
                            viewHolder.binding.tvDueDate.text =
                                activity.getString(R.string.str_due_in_days, remainingDays)
                            viewHolder.binding.tvDueDate.setTextColor(activity.getColor(R.color.red))
                        }

                        else -> {
                            viewHolder.binding.tvDueDate.text =
                                activity.getString(R.string.str_due_in_days, remainingDays)
                            viewHolder.binding.tvDueDate.setTextColor(activity.getColor(R.color.yellow))
                        }
                    }
                    viewHolder.binding.tvAmount.text =
                        "₹${Utils.getFormattedDecimal(item.bill?.bill_due_amount?:item.bill.total_due)}"

                }

                viewHolder.binding.llConfirmCaseTwo.setOnClickListener {
                    productInterface.onActiveAndPayClick(
                        item.id,
                        item.last4,
                        item.bankMasterRecord?.id,
                        item.bankMasterRecord?.bankName ?: "", "", "", "", item
                    )
                }
            }


            if (item.productStatus == ProductStatus.ProductStatusThree.status) {
                viewHolder.binding.llCaseTwo.visibility = View.GONE
                viewHolder.binding.llCaseFour.visibility = View.GONE
                viewHolder.binding.llCaseOne.visibility = View.GONE
                viewHolder.binding.llCaseThree.visibility = View.VISIBLE
                viewHolder.binding.llCaseFive.visibility = View.GONE
                viewHolder.binding.tvEarnThree.text = "Earn ${item.rewardRate}%"
                if (item.bill != null) {
                    val c = Calendar.getInstance().time
                    val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val formattedDate = df.format(c)
                    val remainingDays = DateTimeUtils.getDaysBetweenDates(
                        formattedDate, item.bill.due_date.replaceRange(
                            10, item.bill.due_date.length, ""
                        )
                    ).toString().toInt()
                    viewHolder.binding.tvAmountTwo.text =
                        "₹${Utils.getFormattedDecimal((item.bill.bill_due_amount?:item.bill.total_due))}"
                    when {
                        remainingDays < -1 -> {
                            viewHolder.binding.tvDueDateTwo.text =
                                "Overdue by ${remainingDays.toString().replace("-", "")} Days"
                            viewHolder.binding.tvDueDateTwo.setTextColor(activity.getColor(R.color.red))
                        }
                        remainingDays == -1 -> {
                            viewHolder.binding.tvDueDateTwo.text =
                                activity.getString(R.string.str_over_due_by_one)
                            viewHolder.binding.tvDueDateTwo.setTextColor(activity.getColor(R.color.red))
                        }
                        remainingDays == 0 -> {
                            viewHolder.binding.tvDueDateTwo.text =
                                activity.getString(R.string.str_due_today)
                            viewHolder.binding.tvDueDateTwo.setTextColor(activity.getColor(R.color.red))
                        }
                        remainingDays == 1 -> {
                            viewHolder.binding.tvDueDateTwo.text =
                                activity.getString(R.string.str_due_in_one_day)
                            viewHolder.binding.tvDueDateTwo.setTextColor(activity.getColor(R.color.red))
                        }
                        remainingDays in 2..7 -> {
                            viewHolder.binding.tvDueDateTwo.text =
                                activity.getString(R.string.str_due_in_days, remainingDays)
                            viewHolder.binding.tvDueDateTwo.setTextColor(activity.getColor(R.color.red))
                        }

                        else -> {
                            viewHolder.binding.tvDueDateTwo.text =
                                activity.getString(R.string.str_due_in_days, remainingDays)
                            viewHolder.binding.tvDueDateTwo.setTextColor(activity.getColor(R.color.yellow))
                        }
                    }

                    viewHolder.binding.btnPayNow.setOnClickListener {
                        productInterface.onPayNowCLick(item)

                    }

                }
            }


            if (item.productStatus == ProductStatus.ProductStatusFour.status) {
                viewHolder.binding.llCaseTwo.visibility = View.GONE
                viewHolder.binding.llCaseFour.visibility = View.VISIBLE
                viewHolder.binding.llCaseOne.visibility = View.GONE
                viewHolder.binding.llCaseThree.visibility = View.GONE
                viewHolder.binding.llCaseFive.visibility = View.GONE
                viewHolder.binding.tvPayAndEarn.text = "Pay & earn ${item.rewardRate}%\nCheQ Chips"
                viewHolder.binding.tvEarnFour.text = "Earn ${item.rewardRate}%"

                viewHolder.binding.btnCustomBillPay.setOnClickListener {
                    productInterface.onPayMoreCLick(item)
                }
            }


            if (item.productStatus == ProductStatus.ProductStatusFive.status) {
                viewHolder.binding.llCaseFive.visibility = View.VISIBLE
                viewHolder.binding.llCaseTwo.visibility = View.GONE
                viewHolder.binding.llCaseFour.visibility = View.GONE
                viewHolder.binding.llCaseOne.visibility = View.GONE
                viewHolder.binding.llCaseThree.visibility = View.GONE
                if (item.bill?.bill_due_amount==0.0 && item.bill.overpaid_amount==0.0) {
                    viewHolder.binding.tvYourLastBill.text =
                        "Your last bill was ₹${Utils.getFormattedDecimal((item.bill?.total_due ?: 0.0))}"
                    viewHolder.binding.fullyPaidText.text=viewHolder.itemView.context.getString(R.string.fully_paid)
                } else if((item.bill?.bill_due_amount == 0.0) && ((item.bill.overpaid_amount
                        ?: 0.0) > 0.0)
                ){
                    viewHolder.binding.tvYourLastBill.text =
                        "You Paid ₹${Utils.getFormattedDecimal(item.bill.amount_paid?:0.0)}"
                    viewHolder.binding.fullyPaidText.text=binding?.root?.context?.getString(R.string.paid_exrta)
                }
                viewHolder.binding.tvEarnFive.text = "Earn ${item.rewardRate}%"
                viewHolder.binding.btnPayFullyPaid.setOnClickListener {
                    productInterface.onPayMoreCLick(item)
                }
            }
            viewHolder.binding.ivBankImage.colorFilter = ColorMatrixColorFilter(colorMatrix)
            viewHolder.binding.ivCardType.colorFilter = ColorMatrixColorFilter(colorMatrix)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun dummyList(item: Array<SelectOfferResponseItem>) {

    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun setSwipeListener(viewHolder: ViewHolder) {
        viewHolder.binding.swipeLayout.apply {
            showMode = SwipeLayout.ShowMode.LayDown
            addDrag(SwipeLayout.DragEdge.Right, viewHolder.binding.wrapperRight)
            var swipeListener: SwipeLayout.SwipeListener = object : SwipeLayout.SwipeListener {
                override fun onClose(layout: SwipeLayout?) {}
                override fun onUpdate(layout: SwipeLayout?, leftOffset: Int, topOffset: Int) {}
                override fun onStartOpen(layout: SwipeLayout?) {
                    if (viewHolder.binding.linearToolTip.animation != null && viewHolder.binding.linearToolTip.animation.hasStarted()) {
                        clearAnimation(viewHolder)
                        hideAnimationViews(viewHolder)
                    }
                }

                override fun onOpen(layout: SwipeLayout) {
                    if (prevSwipedLayout != null && layout !== prevSwipedLayout) {
                        prevSwipedLayout?.close()
                    }
                    prevSwipedLayout = layout
                }

                override fun onStartClose(layout: SwipeLayout?) {}
                override fun onHandRelease(layout: SwipeLayout?, xvel: Float, yvel: Float) {}
            }
            addSwipeListener(swipeListener)
        }
    }

    private fun clearAnimation(viewHolder: ViewHolder) {
        viewHolder.binding.linearToolTip.clearAnimation()
        viewHolder.binding.imgCircle.clearAnimation()
    }

    private fun hideAnimationViews(viewHolder: ViewHolder) {
        viewHolder.binding.linearToolTip.visibility = View.GONE
        viewHolder.binding.imgCircle.visibility = View.GONE
    }

    private fun showAnimationViews(viewHolder: ViewHolder) {
        viewHolder.binding.linearToolTip.visibility = View.VISIBLE
        viewHolder.binding.imgCircle.visibility = View.VISIBLE
    }

    private fun startAnimation(viewHolder: ViewHolder) {
        viewHolder.binding.imgCircle.startAnimation(
            AnimationUtils.loadAnimation(
                activity,
                R.anim.zoom_in
            )
        )
        viewHolder.binding.linearToolTip.startAnimation(
            TranslateAnimation(
                ZERO_AXIS,
                ZERO_AXIS,
                ZERO_AXIS,
                TO_Y_DELTA
            ).apply {
                duration = ANIM_DURATION
                repeatCount = Animation.INFINITE
                repeatMode = REPEAT_MODE
            })
    }

    fun closeSwipeLayout() {
        prevSwipedLayout?.close()
    }

    private fun openRemoveCardBottomSheet(
        productV2: ProductV2,
        viewHolder: ViewHolder,
        position: Int
    ) {
        val dialog = Utils.showCustomDialogBottum(activity, R.layout.bottom_sheet_remove_card)
        val bindingSheet = DataBindingUtil.inflate<BottomSheetRemoveCardBinding>(
            layoutInflater!!, R.layout.bottom_sheet_remove_card, null, false
        )

        if(productV2.product_status== ProductStatus.ProductStatusOne.status || productV2.product_status==ProductStatus.ProductStatusTwo.status){
            bindingSheet.txtCardLinked.text = activity.getString(R.string.cannot_be_linked_another_acnt)
            bindingSheet.txtContine.visibility = View.VISIBLE
        }else{
            bindingSheet.txtCardLinked.text = activity.getString(R.string.do_you_wish_to_continue)
            bindingSheet.txtContine.visibility = View.GONE
        }

        bindingSheet.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        bindingSheet.btnRemove.setOnClickListener {
            dialog.dismiss()
            viewHolder.binding.linearRemove.visibility = View.GONE
            viewHolder.binding.linearRemoving.visibility = View.VISIBLE
            productInterface.onRemovedProduct(productV2,viewHolder.layoutPosition)
        }
        dialog.setContentView(bindingSheet.root)
        activity.let { Utils.showKeyboard(it as AppCompatActivity) }

        dialog.show()
    }

    class ViewHolder(var binding: LayoutProductsListBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    /**
     * enum for managing the Product status
     */
    enum class ProductStatus(val status: String) {
        /**
         * If we can't find the Product status
         */
        ProductStatusDefault("0"),

        /**
         * Unsecured without bill
         */
        ProductStatusOne("1"),

        /**
         *
         */
        ProductStatusTwo("2"),

        /**
         * secured with bill
         */
        ProductStatusThree("3"),

        /**
         * secured without bill
         */
        ProductStatusFour("4"),

        /**
         * fully paid
         */
        ProductStatusFive("5")
    }


}