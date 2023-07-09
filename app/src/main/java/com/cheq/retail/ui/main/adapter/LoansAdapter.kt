package com.cheq.retail.ui.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cheq.retail.R
import com.cheq.retail.databinding.LayoutLoanListNewBinding
import com.cheq.retail.extensions.loanMasterUrl
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.ui.login.modelv2.productv1.ProductV2
import com.cheq.retail.ui.main.fragment.ProductAdapterListener
import com.cheq.retail.utils.DateTimeUtils
import com.cheq.retail.utils.ImageUtils.loadSvg
import com.cheq.retail.utils.Utils
import java.text.SimpleDateFormat
import java.util.*


class LoansAdapter(
    private val activity: Context,
    private val productInterface: ProductAdapterListener,
    private val list: List<ProductV2>
) : RecyclerView.Adapter<LoansAdapter.ViewHolder>() {
    private var binding: LayoutLoanListNewBinding? = null
    private var layoutInflater: LayoutInflater? = null
    private lateinit var prefs: SharePrefs
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        prefs = SharePrefs.getInstance(activity)!!
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.context)
        }
        binding = layoutInflater?.let {
            DataBindingUtil.inflate(
                it, R.layout.layout_loan_list_new, viewGroup, false
            )
        }
        return ViewHolder(binding!!)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        try {
            val item = list[position]
            val colorMatrix = ColorMatrix()
            val imageUrl =
                "${prefs.loanMasterUrl}${item.bankMasterRecord?.id}-logo-with-name.svg"
            val imageUrlBack =
                "${prefs.loanMasterUrl}${item.bankMasterRecord?.id}-card-image.svg"
            viewHolder.binding.ivBankImage.loadSvg(imageUrl)
            viewHolder.binding.ivLogoBack.loadSvg(imageUrlBack)
            println("imageUrl $imageUrl")
//            if (item.productType == "CC") {
//                viewHolder.binding.tvCardType.text = "Cards"
//            } else {
//                viewHolder.binding.tvCardType.text = "Loans"
//            }

            if (position == 0) {
                viewHolder.binding.tvCardType.visibility = View.GONE
            } else {
                viewHolder.binding.tvCardType.visibility = View.GONE
            }
            if (item.last4 == "XXXX") {
                viewHolder.binding.tvCardNumber.text = " "
            } else {
                viewHolder.binding.tvCardNumber.text = "··· " + (item.last4)
            }

            var billerName =
                if (item?.institutionName != null) item.institutionName else item.bankMasterRecord?.billerName
            viewHolder.binding.tvBankName.text = billerName
            viewHolder.binding.llBillDue.visibility = View.GONE
            viewHolder.binding.llFullyPaid.visibility = View.GONE
            viewHolder.binding.llPayNow.visibility = View.GONE
            colorMatrix.setSaturation(1f)
            viewHolder.binding.llDueDate.visibility = View.GONE

            if (item.bill != null) {
                setBillView(viewHolder, item, false)
                viewHolder.binding.btnCheckBill.visibility = View.GONE
            } else {
                viewHolder.binding.llPayNow.visibility = View.VISIBLE
                viewHolder.binding.btnCommon.visibility = View.GONE
                viewHolder.binding.btnCheckBill.visibility = View.VISIBLE

            }

            viewHolder.binding.ivBankImage.colorFilter = ColorMatrixColorFilter(colorMatrix)

            if (item.bankMasterRecord?.ui_config != null) {
                binding?.flSolidColor?.setBackgroundResource(R.drawable.loan_bottom_shap)
                val bg: Drawable? = binding?.flSolidColor?.background
                bg?.setTint(Color.parseColor(item.bankMasterRecord.ui_config.cardColor))
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setBillView(
        viewHolder: ViewHolder, item: ProductV2, isSourceSms: Boolean
    ) {
        val c = Calendar.getInstance().time

        val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = df.format(c)
//
        viewHolder.binding.btnCommon.setOnClickListener {
            var billerName =
                if (item?.institutionName != null) item.institutionName else item.bankMasterRecord?.billerName
            productInterface.onLoanPayClick(
                item.product_number,
                billerName,
                item.product_type,
                "",
                0.0,
                item.bill?.bill_due_amount?.toInt(),
                item.institution_master_id,
                item.id,
                0.0,
                item.last4,
                "",
                "",
                "",
                item
            )

        }

        if (item.amountExactness?.equals(binding?.root?.context?.getString(R.string.adhoc)) == true) {
            viewHolder.binding.btnCommon.visibility = View.VISIBLE
        } else if ((item.productStatus.equals(ProductsAdapterNew.ProductStatus.ProductStatusFour.status) || item.productStatus.equals(ProductsAdapterNew.ProductStatus.ProductStatusThree.status))) {
            viewHolder.binding.btnCommon.visibility = View.VISIBLE
        } else {
            viewHolder.binding.btnCommon.visibility = View.GONE
        }
        if (item.bill?.bill_due_amount != null) {
            if (item.bill.bill_due_amount < 0) {
                viewHolder.binding.llFullyPaid.visibility = View.VISIBLE
                viewHolder.binding.llBillDue.visibility = View.GONE



                viewHolder.binding.fullyPaidText.text = "Paid Extra"
                viewHolder.binding.tvLastBillAmt.text =
                    "You paid ₹" + Utils.getFormattedDecimal(item.bill.bill_due_amount)
                        .replace("-", "") + " extra"


            } else {
                viewHolder.binding.llBillDue.visibility = View.VISIBLE
                viewHolder.binding.tvDueAmount.text =
                    "₹" + Utils.getFormattedDecimal(item.bill.bill_due_amount)

                if (item.bill.due_date != null) {
                    val remainingDays = DateTimeUtils.getDaysBetweenDates(
                        formattedDate, item.bill.due_date.replaceRange(
                            10, item.bill.due_date.length, ""
                        )
                    ).toString().toInt()
                    if (item.bill.bill_due_amount == 0.0) {
                        viewHolder.binding.tvDueDate.text =
                            binding?.root?.context?.getString(R.string.no_amount_due)
                        viewHolder.binding.tvDueDate.setTextColor(Color.parseColor("#858989"))
                    } else {
                        when {
                            remainingDays < -1 -> {
                                viewHolder.binding.tvDueDate.text =
                                    "Overdue by ${remainingDays.toString().replace("-", "")} Days"
                                viewHolder.binding.tvDueDate.setTextColor(activity.getColor(R.color.red))

                            }
                            remainingDays == -1 -> {
                                viewHolder.binding.tvDueDate.text =  activity.getString(R.string.str_over_due_by_one)
                                viewHolder.binding.tvDueDate.setTextColor(activity.getColor(R.color.red))
                            }
                            remainingDays == 0 -> {
                                viewHolder.binding.tvDueDate.text = activity.getString(R.string.str_due_today)
                                viewHolder.binding.tvDueDate.setTextColor(activity.getColor(R.color.red))
                            }
                            remainingDays == 1 -> {
                                viewHolder.binding.tvDueDate.text =  activity.getString(R.string.str_due_in_one_day)
                                viewHolder.binding.tvDueDate.setTextColor(activity.getColor(R.color.red))
                            }
                            remainingDays in 2..7 -> {
                                viewHolder.binding.tvDueDate.text = activity.getString(R.string.str_due_in_days, remainingDays)
                                viewHolder.binding.tvDueDate.setTextColor(activity.getColor(R.color.red))
                            }

                            remainingDays > 7 -> {
                                viewHolder.binding.tvDueDate.text = activity.getString(R.string.str_due_in_days, remainingDays)
                                viewHolder.binding.tvDueDate.setTextColor(activity.getColor(R.color.yellow))

                            }


                        }
                    }


                } else {
                    if (item.bill.bill_due_amount == 0.0) {
                        viewHolder.binding.tvDueDate.text =
                            binding?.root?.context?.getString(R.string.no_amount_due)

                    } else {
                        viewHolder.binding.tvDueDate.text =
                            binding?.root?.context?.getString(R.string.no_due_date_found)
                    }
                    viewHolder.binding.tvDueDate.setTextColor(activity.getColor(R.color.loan_due_date))
                }


            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(var binding: LayoutLoanListNewBinding) : RecyclerView.ViewHolder(
        binding.root
    )


}