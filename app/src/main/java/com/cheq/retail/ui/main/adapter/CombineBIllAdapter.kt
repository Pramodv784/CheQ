package com.cheq.retail.ui.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cheq.retail.R
import com.cheq.retail.databinding.LayoutLoanCombineBillPaymentToggleBinding
import com.cheq.retail.databinding.LayoutProductsCombineBillPaymentToggleBinding
import com.cheq.retail.extensions.bankMasterUrl
import com.cheq.retail.extensions.loanMasterUrl
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.ui.login.modelv2.productv1.ProductV2
import com.cheq.retail.ui.main.fragment.ProductAdapterListener
import com.cheq.retail.utils.DateTimeUtils
import com.cheq.retail.utils.GradientUtils
import com.cheq.retail.utils.ImageUtils.loadSvg
import com.cheq.retail.utils.Utils
import java.text.SimpleDateFormat
import java.util.*


class CombineBIllAdapter(
    private val activity: Context,
    private val productInterface: ProductAdapterListener,
    private val list: ArrayList<ProductV2>,
    private val productListener: ProductClickListener

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val THE_FIRST_VIEW = 1
        const val THE_SECOND_VIEW = 2
    }

    private var binding: LayoutProductsCombineBillPaymentToggleBinding? = null
    private var layoutInflater: LayoutInflater? = null
    lateinit var prefs: SharePrefs
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        prefs = SharePrefs.getInstance(activity)!!
        when (i) {
            THE_FIRST_VIEW -> {
                return ViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(activity),
                        R.layout.layout_products_combine_bill_payment_toggle,
                        viewGroup,
                        false
                    )
                )
            }
            THE_SECOND_VIEW -> {
                return LoanViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(activity),
                        R.layout.layout_loan_combine_bill_payment_toggle,
                        viewGroup,
                        false
                    )
                )
            }
            else -> {
                return ViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(activity),
                        R.layout.layout_loan_combine_bill_payment_toggle,
                        viewGroup,
                        false
                    )
                )
            }
        }
    }

    fun getSelectedProduct(): ArrayList<ProductV2> {
        val selectedProduct: ArrayList<ProductV2> = ArrayList()
        for (product in list) {
            if (product.isChecked) {
                selectedProduct.add(
                    product
                )
            }
        }
        println("selectedProduct ${selectedProduct}")
        return selectedProduct
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position].theView) {
            1 -> THE_FIRST_VIEW
            2 -> THE_SECOND_VIEW
            else -> 3
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (list[position].theView == THE_FIRST_VIEW) {
            (viewHolder as ViewHolder).bind(position)
        } else if (list[position].theView == THE_SECOND_VIEW) {
            (viewHolder as LoanViewHolder).bind(position)
        }

    }


    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(var binding: LayoutProductsCombineBillPaymentToggleBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        @RequiresApi(Build.VERSION_CODES.M)
        fun bind(position: Int) {
            try {
                val item = list[position]
                val colorMatrix = ColorMatrix()
                if (item.last4 == "XXXX") {
                    binding.tvCardNumber.text = "··· "
                } else {
                    binding.tvCardNumber.text = "··· " + (item.last4)
                }
                val imageUrl =
                    "${prefs.bankMasterUrl}${item.bankMasterRecord?.id}-logo-with-name.svg"
                val imageUrlBack =
                    "${prefs.bankMasterUrl}${item.bankMasterRecord?.id}-card-image.svg"
                if (item.bankMasterRecord?.ui_config != null) {
                    GradientUtils.setBoarderStroke(
                        item.bankMasterRecord.ui_config.cardColor,
                        item.bankMasterRecord.ui_config.opacity_border,
                        true,
                        binding.clStroke
                    )
                    GradientUtils.setBackGround(
                        item.bankMasterRecord.ui_config.cardColor,
                        "",
                        item.bankMasterRecord.ui_config.opacity_topLeft,
                        item.bankMasterRecord.ui_config.opacity_bottomRight,
                        binding.llCardBackGround
                    )
                }
                binding.tvBankName.text = item.bankMasterRecord?.bankName ?: ""
                if (!item.customer_name.isNullOrEmpty()) {
                    binding.tvCardHolderName.text = item.customer_name
                } else {
                    binding.tvCardHolderName.text = SharePrefs.getInstance(activity)!!
                        .getString(SharePrefsKeys.FIRST_NAME) + " " + SharePrefs.getInstance(
                        activity
                    )!!.getString(SharePrefsKeys.LAST_NAME)
                }
                binding.ivChip.setImageDrawable(activity.getDrawable(R.drawable.ic_chip))
                colorMatrix.setSaturation(1f)

                if (item.card_network != null) {
                    binding.ivCardType.visibility = View.VISIBLE
                    if (item.card_network == "MasterCard") {
                       binding.ivCardType.setImageResource(R.drawable.ic_mastercard)
                    }

                    if (item.card_network == "Visa") {
                        binding.ivCardType.setImageResource(R.drawable.visa)
                    }

                    if (item.card_network == "Diners Club") {
                        binding.ivCardType.setImageResource(R.drawable.ic_dinner_icon)
                    }
                    if (item.card_network == "American Express") {
                       binding.ivCardType.setImageResource(R.drawable.ic_american_icon)
                    }
                    if (item.card_network == "RuPay") {
                        binding.ivCardType.setImageResource(R.drawable.ic_rupay_card_icon)
                    }
                } else {
                    binding.ivCardType.visibility = View.GONE
                }

                binding.ivBankImage.loadSvg(imageUrl)
                binding.ivLogoBack.loadSvg(imageUrlBack)
                binding.ivBankImage.colorFilter = ColorMatrixColorFilter(colorMatrix)
                binding.ivCardType.colorFilter = ColorMatrixColorFilter(colorMatrix)
                if (item.isChecked) {
                    binding.clStroke.alpha = 1f
                } else {
                    binding.clStroke.alpha = 0.7f
                }

                if (item.productStatus == "4") {
                    if (item.customAmount != 0.0) {
                        binding.tvDueDate.text = "Bill not found"
                        binding.tvDueDate.setTextColor(activity.getColor(R.color.colorText))
                        binding.ivEditAmount.setImageDrawable(activity.getDrawable(R.drawable.ic_edit_new))
                        binding.ivEditAmount.setOnClickListener {
                            productInterface.onEditAmountClick(item, position)
                        }
                    } else {
                        binding.tvDueDate.text = "Bill not found"
                        binding.tvDueDate.setTextColor(activity.getColor(R.color.colorTextRed))
                    }


                } else {
                    if (item.customAmount != 0.0) {
                        binding.ivEditAmount.setImageDrawable(activity.getDrawable(R.drawable.ic_edit_new))
                        binding.ivEditAmount.setOnClickListener {
                            productInterface.onEditAmountClick(item, position)
                        }
                    } else {
                        binding.ivEditAmount.setImageDrawable(activity.getDrawable(R.drawable.ic_edite_new_disabled))
                    }
                    if (item.bill != null) {
                        val c = Calendar.getInstance().time
                        val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val formattedDate = df.format(c)
                        val remainingDays = DateTimeUtils.getDaysBetweenDates(
                            formattedDate, item.bill!!.due_date.replaceRange(
                                10, item.bill.due_date.length, ""
                            )
                        ).toString().toInt()
                        println("totaldue++++ ${item.bill.total_due}")

                        when {
                            remainingDays < -1 -> {
                                binding.tvDueDate.text =
                                    activity.getString(R.string.overdue_by_days,remainingDays.toString().replace("-", ""))
                                binding.tvDueDate.setTextColor(activity.getColor(R.color.red))
                            }
                            remainingDays == -1 -> {
                                binding.tvDueDate.text = activity.getString(R.string.overdue_by_one_day)
                                binding.tvDueDate.setTextColor(activity.getColor(R.color.red))
                            }
                            remainingDays == 0 -> {
                                binding.tvDueDate.text = activity.getString(R.string.due_today)
                                binding.tvDueDate.setTextColor(activity.getColor(R.color.red))
                            }
                            remainingDays == 1 -> {
                                binding.tvDueDate.text =  activity.getString(R.string.due_in_one_day)
                                binding.tvDueDate.setTextColor(activity.getColor(R.color.red))
                            }
                            remainingDays in 2..7 -> {
                                binding.tvDueDate.text = activity.getString(R.string.due_in_days,remainingDays.toString())
                                binding.tvDueDate.setTextColor(activity.getColor(R.color.red))
                            }

                            remainingDays > 7 -> {
                                binding.tvDueDate.text = activity.getString(R.string.due_in_days,remainingDays.toString())
                                binding.tvDueDate.setTextColor(activity.getColor(R.color.yellow))
                            }
                        }

                    } else {
                        println("totaldue++++ }")
                    }
                }
                binding.ivEditAmount.setOnClickListener {
                    if (item.isChecked) {
                        productInterface.onEditAmountClick(item, position)
                    }
                }

                if (item.customAmount != 0.0) {
                    binding.tvAmount.text =
                        "₹" + Utils.getFormattedDecimal(item.customAmount ?: 0.0)
                } else {
                    binding.tvAmount.text = "₹0"
                }

                binding.ivMenu.setOnClickListener {
                    productInterface.onMenuClicked(item)
                }
                binding.cbCheck.isChecked = item.isChecked
                binding.cbCheck.setOnCheckedChangeListener { _, isChecked ->
                    item.isChecked = isChecked

                    productInterface.onSwitchedChecked(item, position, isChecked)
                    if (!isChecked) {
                        binding.ivEditAmount.setImageDrawable(activity.getDrawable(R.drawable.ic_edite_new_disabled))
                        binding.ivEditAmount.isEnabled = false
                        binding.clStroke.alpha = 0.7f
                    } else {
                        binding.ivEditAmount.isEnabled = true
                        binding.clStroke.alpha = 1f
                        if (item.productStatus == "4") {
                            productInterface.onEditAmountClick(item, position)
                        }
                        binding.ivEditAmount.setImageDrawable(activity.getDrawable(R.drawable.ic_edit_new))

                        // viewHolder.binding.ivEditAmount.setImageDrawable(activity.getDrawable(R.drawable.ic_edit_new))
                    }
                    if (getSelectedProduct().size == 0) {
                        productListener.onProductAction(false)
                    } else {
                        productListener.onProductAction(true)
                    }
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    inner class LoanViewHolder(var loanCombineBinding: LayoutLoanCombineBillPaymentToggleBinding) :
        RecyclerView.ViewHolder(loanCombineBinding.root) {
        @RequiresApi(Build.VERSION_CODES.M)
        fun bind(position: Int) {
            try {
                val item = list[position]
                val colorMatrix = ColorMatrix()
                if (item.last4 == "XXXX") {
                    loanCombineBinding.tvCardNumber.text = "··· "
                } else {
                    loanCombineBinding.tvCardNumber.text = "··· " + (item.last4)
                }
                val imageUrl =
                    "${prefs.loanMasterUrl}${item.bankMasterRecord?.id}-logo-with-name.svg"
                val imageUrlBack =
                    "${prefs.loanMasterUrl}${item.bankMasterRecord?.id}-card-image.svg"

                loanCombineBinding.tvBankName.text = item.bankMasterRecord?.billerName ?: ""
                if (item.isChecked) {
                    binding?.clStroke?.alpha = 1f
                } else {
                    binding?.clStroke?.alpha = 0.7f
                }

                colorMatrix.setSaturation(1f)
                loanCombineBinding.ivBankImage.loadSvg(imageUrl)
                loanCombineBinding.ivLogoBack.loadSvg(imageUrlBack)
                loanCombineBinding.ivBankImage.colorFilter = ColorMatrixColorFilter(colorMatrix)
                if (item.productStatus == "4") {
                    if (item.customAmount != 0.0 && item.customAmount?.toInt()?:0 != 0) {
                        loanCombineBinding.tvDueDate.text = activity.getString(R.string.bill_not_found)
                        loanCombineBinding.tvDueDate.setTextColor(activity.getColor(R.color.colorText))
                        loanCombineBinding.ivEditAmount.setImageDrawable(activity.getDrawable(R.drawable.ic_edit_new))
                        loanCombineBinding.ivEditAmount.setOnClickListener {
                            productInterface.onEditAmountClick(item, position)
                        }
                    } else {
                        loanCombineBinding.tvDueDate.text = activity.getString(R.string.bill_not_found)
                        loanCombineBinding.tvDueDate.setTextColor(activity.getColor(R.color.colorTextRed))
                    }


                } else {
                    if (item.customAmount != 0.0 ) {
                        loanCombineBinding.ivEditAmount.setImageDrawable(activity.getDrawable(R.drawable.ic_edit_new))
                        loanCombineBinding.ivEditAmount.setOnClickListener {
                            productInterface.onEditAmountClick(item, position)
                        }
                        if (item.bill?.due_date != null) {
                            val c = Calendar.getInstance().time
                            val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val formattedDate = df.format(c)
                            val remainingDays = DateTimeUtils.getDaysBetweenDates(
                                formattedDate, item.bill!!.due_date.replaceRange(
                                    10, item.bill.due_date.length, ""
                                )
                            ).toString().toInt()

                            when {
                                remainingDays < -1 -> {
                                    loanCombineBinding.tvDueDate.text =
                                        activity.getString(R.string.overdue_by_days,remainingDays.toString().replace("-", ""))
                                    loanCombineBinding.tvDueDate.setTextColor(activity.getColor(R.color.red))
                                }
                                remainingDays == -1 -> {
                                    loanCombineBinding.tvDueDate.text = activity.getString(R.string.overdue_by_one_day)
                                    loanCombineBinding.tvDueDate.setTextColor(activity.getColor(R.color.red))
                                }
                                remainingDays == 0 -> {
                                    loanCombineBinding.tvDueDate.text = activity.getString(R.string.due_today)
                                    loanCombineBinding.tvDueDate.setTextColor(activity.getColor(R.color.red))
                                }
                                remainingDays == 1 -> {
                                    loanCombineBinding.tvDueDate.text = activity.getString(R.string.due_in_one_day)
                                    loanCombineBinding.tvDueDate.setTextColor(activity.getColor(R.color.red))
                                }
                                remainingDays in 2..7 -> {
                                    loanCombineBinding.tvDueDate.text =
                                        activity.getString(R.string.due_in_days,remainingDays.toString())

                                    loanCombineBinding.tvDueDate.setTextColor(activity.getColor(R.color.red))
                                }

                                remainingDays > 7 -> {
                                    loanCombineBinding.tvDueDate.text =
                                        activity.getString(R.string.due_in_days,remainingDays.toString())
                                    loanCombineBinding.tvDueDate.setTextColor(activity.getColor(R.color.yellow))
                                }
                            }

                        } else {
                        }
                    } else {

                        loanCombineBinding.ivEditAmount.setImageDrawable(activity.getDrawable(R.drawable.ic_edite_new_disabled))
                        loanCombineBinding.tvDueDate.text = activity.getString(R.string.fully_paid_)
                        loanCombineBinding.tvDueDate.setTextColor(activity.getColor(R.color.colorPrimary))
                    }

                }
                loanCombineBinding.ivEditAmount.setOnClickListener {
                    if (item.isChecked) {
                        productInterface.onEditAmountClick(item, position)
                    }
                }
                if (item.customAmount != 0.0 && (item.customAmount?.toInt() ?: 0) > 0) {
                    loanCombineBinding.tvLoanAmount.text =
                        "₹" + Utils.getFormattedDecimal(item.customAmount ?: 0.0)
                } else {
                    loanCombineBinding.tvLoanAmount.text = "₹0"
                }

                loanCombineBinding.ivMenu.setOnClickListener {
                    productInterface.onMenuClicked(item)
                }
                loanCombineBinding.cbCheck.isChecked = item.isChecked
                loanCombineBinding.cbCheck.setOnCheckedChangeListener { _, isChecked ->
                    item.isChecked = isChecked
                    productInterface.onSwitchedChecked(item, position, isChecked)
                    if (!isChecked) {
                        loanCombineBinding.ivEditAmount.setImageDrawable(activity.getDrawable(R.drawable.ic_edite_new_disabled))
                        loanCombineBinding.ivEditAmount.isEnabled = false
                          loanCombineBinding.clStroke.alpha = 0.7f
                    } else {
                        loanCombineBinding.ivEditAmount.isEnabled = true
                        loanCombineBinding.clStroke.alpha = 1f
                        if (item.productStatus == "4" || item.productStatus == "3") {
                            productInterface.onEditAmountClick(item, position)
                        }
                        loanCombineBinding.ivEditAmount.setImageDrawable(activity.getDrawable(R.drawable.ic_edit_new))

                    }
                    if (getSelectedProduct().size == 0) {
                        productListener.onProductAction(false)
                    } else {
                        productListener.onProductAction(true)
                    }
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    interface ProductClickListener {
        fun onProductAction(isSelected: Boolean?)
    }

}