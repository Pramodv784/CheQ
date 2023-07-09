package com.cheq.retail.ui.dashboard.view.adapter


import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cheq.retail.R
import com.cheq.retail.databinding.LayoutHomeProductBinding
import com.cheq.retail.extensions.bankMasterUrl
import com.cheq.retail.extensions.loanMasterUrl
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.ui.login.modelv2.productv1.ProductV2
import com.cheq.retail.utils.DateTimeUtils
import com.cheq.retail.utils.ImageUtils.loadSvg
import com.cheq.retail.utils.Utils
import java.text.SimpleDateFormat
import java.util.*


class HomeProductAdapter(var products: ArrayList<ProductV2>, var updateOverDue: UpdateOverDue) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var binding: LayoutHomeProductBinding? = null
    private var layoutInflater: LayoutInflater? = null
    private var prefs: SharePrefs? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        prefs = SharePrefs.getInstance(parent.context)
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        binding = layoutInflater?.let {
            DataBindingUtil.inflate(
                it, R.layout.layout_home_product, parent, false
            )
        }
        return ViewHolder(binding!!)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (products.size > 3 && position == 2) {
            binding?.tvKeyYOurProductHeadingOne?.text = "and ${(products.size - 2)} more"
            var moreCardType = ""
            var moreLoanType = ""





            for (index in products.indices) {
                if (index > 1) {
                    if (products[index].product_type == "CC") {
                        moreCardType =
                            binding?.root?.context?.getString(R.string.credit_card_small).toString()
                    } else {
                        moreLoanType = binding?.root?.context?.getString(R.string.loan).toString()
                    }


                }
            }

            var totalDueForMoreCard = 0.0
            var letestDueDate = 0
            for (index in products.indices) {
                if (index > 1) {

                    if (products[index].bill?.bill_due_amount != null) {
                        totalDueForMoreCard += products[index].bill?.bill_due_amount ?: 0.0
                    }

                }
            }


            if (totalDueForMoreCard == 0.0) {
                binding?.productCardAmountOneDueDate?.visibility =
                    View.VISIBLE
                binding?.productCardAmountOne?.visibility = View.GONE
                binding?.productCardAmountOneDueDate?.text =
                    binding?.root?.context?.getString(R.string.no_bill_found)

                binding?.productCardAmountOneDueDate?.setTextColor(
                    R.color.grey_text
                )
            } else if (totalDueForMoreCard < 0.0) {
                binding?.productCardAmountOneDueDate?.visibility =
                    View.VISIBLE
                binding?.productCardAmountOne?.visibility = View.GONE
                binding?.productCardAmountOneDueDate?.text =
                    binding?.root?.context?.getString(R.string.fully_paid)
                binding?.productCardAmountOneDueDate?.setTextColor(
                    Color.parseColor("#00C197")
                )

            } else {
                binding?.productCardAmountOneDueDate?.visibility =
                    View.VISIBLE
                binding?.productCardAmountOne?.visibility = View.VISIBLE
                setUpTotalDueText(
                    binding?.productCardAmountOne,
                    totalDueForMoreCard,
                    22,
                    ""
                )
            }


            binding?.tvKeyProductOne?.text =
                if (moreCardType.isNotEmpty() && moreLoanType.isNotEmpty()) {
                    "$moreCardType,$moreLoanType"
                } else if (moreCardType.isNotEmpty()) {
                    moreCardType
                } else {
                    moreLoanType
                }

        } else {
            binding?.tvKeyYOurProductHeadingOne?.text =
                products[position].bankMasterRecord?.bankName ?: ""

            if (products[position].product_type == "CC") {
                binding?.tvKeyYOurProductHeadingOne?.text =
                    products[position].bankMasterRecord?.bankName ?: ""
                binding?.tvKeyProductOne?.text =
                    binding?.root?.context?.getString(R.string.credit_card_small)
                binding?.ivKeyYOurProductOne?.loadSvg("${prefs?.bankMasterUrl}${products[position].bankMasterRecord?.id}-logo.svg")


            } else {
                binding?.tvKeyYOurProductHeadingOne?.text =
                    products[position].bankMasterRecord?.billerName ?: ""
                binding?.tvKeyProductOne?.text = binding?.root?.context?.getString(R.string.loan)
                binding?.ivKeyYOurProductOne?.loadSvg("${prefs?.loanMasterUrl}${products[position].bankMasterRecord?.id}-logo.svg")


            }


            if (products[position].bill != null) {

                if (products[position].bill?.bill_due_amount !=null && products[position].bill?.bill_due_amount?.toInt()!! <= 0) {
                    binding?.productCardAmountOneDueDate?.visibility =
                        View.VISIBLE
                    binding?.productCardAmountOne?.visibility = View.GONE
                    binding?.productCardAmountOneDueDate?.text =
                        binding?.root?.context?.getString(R.string.fully_paid)
                    binding?.productCardAmountOneDueDate?.setTextColor(
                        Color.parseColor("#00C197")
                    )

                } else {
                    getDueDate(products[position].bill?.due_date)
                    //
                    setUpTotalDueText(
                        binding?.productCardAmountOne,
                        products[position].bill?.bill_due_amount,
                        22,
                        ""
                    )
                }


            } else {
                binding?.productCardAmountOne?.visibility = View.GONE
                binding?.productCardAmountOneDueDate?.text =
                    binding?.root?.context?.getString(R.string.no_bill_found)
                binding?.productCardAmountOneDueDate?.setTextColor(
                    R.color.grey_text
                )

            }

        }

    }

    fun getRemaingDays(dueDate: String?): Int {
        if (dueDate != null) {
            val c = Calendar.getInstance().time
            val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = df.format(c)
            val remainingDays = DateTimeUtils.getDaysBetweenDates(
                formattedDate, dueDate.replaceRange(
                    10, dueDate.substringBefore("T").length, ""
                )
            ).toString().toInt()
            println("totaldue++++ ${dueDate}")
            return remainingDays
        } else {
            return 0
        }
    }

    fun getDueDate(dueDate: String?) {
        var dueDays = binding?.root?.context?.getString(R.string.no_due_date_found)

        if (dueDate != null) {
            val c = Calendar.getInstance().time
            val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = df.format(c)
            val remainingDays = DateTimeUtils.getDaysBetweenDates(
                formattedDate, dueDate.replaceRange(
                    10, dueDate.substringBefore("T").length, ""
                )
            ).toString().toInt()
            println("totaldue++++ ${dueDate}")
            when {
                remainingDays < -1 -> {
                    dueDays = "Overdue by ${remainingDays.toString().replace("-", "")} Days"
                    //  mBinding?.layoutTotalDue?.ProductBack?.setBackgroundResource(R.drawable.overdue_back_on_home_and_dashboard)
                    updateOverDue.isOverDue(true)

                }
                remainingDays == -1 -> {
                    dueDays = binding?.root?.context?.getString(R.string.str_over_due_by_one)
                    //  mBinding?.layoutTotalDue?.ProductBack?.setBackgroundResource(R.drawable.overdue_back_on_home_and_dashboard)
                    updateOverDue.isOverDue(true)
                }
                remainingDays == 0 -> {
                    dueDays =  binding?.root?.context?.getString(R.string.str_due_today)

                }
                remainingDays == 1 -> {
                    dueDays =  binding?.root?.context?.getString(R.string.str_due_in_one_day)

                }
                remainingDays in 2..7 -> {
                    dueDays = binding?.root?.context?.getString(R.string.str_due_in_days, remainingDays)

                }

                else -> {
                    dueDays = binding?.root?.context?.getString(R.string.str_due_in_days, remainingDays)
                    binding?.productCardAmountOneDueDate?.setTextColor(
                        Color.parseColor("#ECB15A")
                    )


                }

            }
        } else {
            binding?.productCardAmountOneDueDate?.setTextColor(
                Color.parseColor("#858989")
            )
        }
        binding?.productCardAmountOneDueDate?.text = dueDays


    }


    override fun getItemCount(): Int {
        return if (products.size > 3) 3 else products.size
    }

    inner class ViewHolder(var binding: LayoutHomeProductBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    fun setUpTotalDueText(textView: TextView?, amount: Double?, fontSize: Int, smallText: String) {
        textView?.text = "₹" + amount?.let { Utils.getFormattedDecimal(it) }

//        if (smallText.isNotEmpty()) {
//            val sizingSpannableText = SpannableString(amount)
//
//            val startIndex = amount.indexOf(".")
//            val amountOfCharacters = smallText.length
//
//            sizingSpannableText.setSpan(
//                AbsoluteSizeSpan(fontSize),
//                startIndex,
//                startIndex + amountOfCharacters,
//                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//            )
//            textView?.text = "₹"+
//        } else {
//            textView?.text = "₹"+amount
//        }
    }

    interface UpdateOverDue {
        fun isOverDue(isOverDue: Boolean = false)
    }

}