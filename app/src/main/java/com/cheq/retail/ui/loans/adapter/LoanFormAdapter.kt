package com.cheq.retail.ui.loans.adapter


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.text.Editable
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView

import com.cheq.retail.R
import com.cheq.retail.databinding.BottomSheetLoanListBinding

import com.cheq.retail.databinding.LoanFormItemBinding
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.loans.DetailsForLoanActivity
import com.cheq.retail.ui.loans.model.CustomerParam
import com.cheq.retail.utils.DialogUtils
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.Utils
import com.cheq.retail.utils.Utils.Companion.openDatePicker
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


class LoanFormAdapter(
    private var LoanList: List<CustomerParam>,
    var context: DetailsForLoanActivity,

) :
    RecyclerView.Adapter<LoanFormAdapter.ViewHolder>() {
    private lateinit var bottomSheetDialog: Dialog
    private var binding: LoanFormItemBinding? = null
    private var layoutInflater: LayoutInflater? = null
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): LoanFormAdapter.ViewHolder {

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.context)
        }
        binding = DataBindingUtil.inflate(
            layoutInflater!!, R.layout.loan_form_item, viewGroup, false
        )
        return ViewHolder(binding!!)
    }

    override fun onBindViewHolder(viewHolder: LoanFormAdapter.ViewHolder, position: Int) {
        try {

            viewHolder.binding.LoanFieldtitle.text =
                Utils.makeFirstCapital(LoanList.get(viewHolder.adapterPosition).paramName)

            if (LoanList.get(viewHolder.adapterPosition).paramName.contains("Date")) {
                viewHolder.binding.llBtm.visibility = View.GONE
                viewHolder.binding.tvDate.visibility = View.VISIBLE
                viewHolder.binding.etName.visibility = View.GONE
                viewHolder.binding.tvLoanOption.visibility = View.GONE
                viewHolder.binding.llOption.visibility = View.GONE
                viewHolder.binding.tvDate.setOnClickListener {
                    openDatePicker(viewHolder.itemView.context, viewHolder.binding.tvDate)
                }
                if (LoanList.get(viewHolder.adapterPosition).value != null && LoanList.get(
                        viewHolder.adapterPosition
                    ).value != ""
                ) {
                    viewHolder.binding.tvDate.setText(LoanList.get(viewHolder.adapterPosition).value)

                }


            } else if (LoanList.get(viewHolder.adapterPosition).values != null) {
                viewHolder.binding.llBtm.visibility = View.GONE
                viewHolder.binding.tvDate.visibility = View.GONE
                viewHolder.binding.etName.visibility = View.GONE
                viewHolder.binding.tvLoanOption.visibility = View.VISIBLE
                viewHolder.binding.llOption.visibility = View.VISIBLE
                val values = LoanList.get(viewHolder.adapterPosition).values!!.split(",")
                var interfaceList: CustomListAdapter.CustomListAdapterInterface? = null
                binding?.tvLoanOption?.setText(values[0])
                viewHolder.binding.tvLoanOption.setOnClickListener {
                    openSheet(values, interfaceList)
                }
                interfaceList = object : CustomListAdapter.CustomListAdapterInterface {
                    override fun onItemSelected(item: String) {
                        bottomSheetDialog.dismiss()
                        viewHolder.binding.tvLoanOption.setText(item)
                    }
                }


                // viewHolder.binding.tvLoanOption.setAdapter(adapter);


            } else {
                viewHolder.binding.llBtm.visibility = View.VISIBLE
                viewHolder.binding.tvLoanOption.visibility = View.GONE
                viewHolder.binding.llOption.visibility = View.GONE
                viewHolder.binding.tvDate.visibility = View.GONE
                viewHolder.binding.etName.visibility = View.VISIBLE
                viewHolder.binding.etName.minEms =
                    LoanList.get(viewHolder.adapterPosition).minLength
                viewHolder.binding.etName.maxEms =
                    LoanList.get(viewHolder.adapterPosition).maxLength
                viewHolder.binding.etName.filters += LengthFilter(LoanList.get(viewHolder.adapterPosition).maxLength)
                if (LoanList.get(viewHolder.adapterPosition).value != null && LoanList.get(
                        viewHolder.adapterPosition
                    ).value != ""
                ) {
                    viewHolder.binding.etName.setText(LoanList.get(viewHolder.adapterPosition).value)
                    LoanList.get(viewHolder.adapterPosition).value =
                        viewHolder.binding.etName.text.toString()
                }

                viewHolder.binding.etName.setOnFocusChangeListener { view, b ->

                    if (b) {
                        viewHolder.binding.llBtm.setBackgroundResource(R.drawable.et_btm_bg)
                    } else {
                        viewHolder.binding.llBtm.setBackgroundResource(R.drawable.et_btm_bg_un_focused)
                    }

                }
                viewHolder.binding.etName.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable) {

                    }

                    override fun beforeTextChanged(
                        s: CharSequence, start: Int,
                        count: Int, after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {

                        try {


                            if (LoanList.get(viewHolder.adapterPosition).regex ==null || (s.toString().length > 0 && Pattern.matches(
                                    LoanList.get(
                                        viewHolder.adapterPosition
                                    ).regex, s.toString()
                                ))
                            ) {

                                LoanList.get(viewHolder.adapterPosition).value = s.toString()

                            } else {

                                LoanList.get(viewHolder.adapterPosition).value = ""
                            }
                            if(s.toString().isNotEmpty()){
                                context.isButtonEnabled(true)
                            }else{
                                context.isButtonEnabled(false)
                            }


                            //setMparticleParam(LoanList.get(viewHolder.adapterPosition).paramName)


                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                })
                when (LoanList.get(viewHolder.adapterPosition).dataType) {
                    "NUMERIC" -> {
                        viewHolder.binding.etName.inputType = InputType.TYPE_CLASS_NUMBER
                    }
                    "ALPHANUMERIC" -> {
                        viewHolder.binding.etName.inputType = InputType.TYPE_CLASS_TEXT
                    }
                    else -> {
                        viewHolder.binding.etName.inputType = InputType.TYPE_CLASS_TEXT
                    }
                }

            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setMparticleParam(paramName: String) {
        when (paramName) {
            "Loan Number" -> {
                MparticleUtils.logCurrentScreen(
                    "/loan-activation/enter-details",
                    "The customer enters the details as required for a bill fetch on the lender",
                    "",
                    "",
                    "",
                    "",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Loan_Activation_Enter_Detail),
                    context
                )

                MparticleUtils.logEvent(
                    "/loan-activation/enter-details",
                    "User inputs the loan account number",
                    "Unique",
                    "Input Field",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Loan_Activation_Enter_Detail),
                    context
                )
            }
            "Loan no" -> {

            }
            "Loan no" -> {

            }
            "Loan Account Number" -> {
                MparticleUtils.logEvent(
                    "Loan_Activation_LoanAccountNumber_Entered",
                    "User inputs the mobile number",
                    "Unique",
                    "Input Field",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Loan_Activation_LoanAccountNumber_Entered),
                    context
                )
            }
            "Application ID" -> {

            }
            "LAN" -> {

            }
            "Loan ID" -> {

            }
            "Mobile Number" -> {
                MparticleUtils.logEvent(
                    "Loan_Activation_MobileNumber_Entered",
                    "User inputs the mobile number",
                    "Unique",
                    "Input Field",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Loan_Activation_MobileNumber_Entered),
                    context
                )
            }
            "Date of Birth (YYYY-MM-DD)" -> {
                MparticleUtils.logEvent(
                    "Loan_Activation_DOB_Entered",
                    "User inputs the date of birth",
                    "Unique",
                    "Input Field",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Loan_Activation_DOB_Entered),
                    context
                )
            }


        }

    }


    private fun openSheet(
        values: List<String>,
        interfaceList: CustomListAdapter.CustomListAdapterInterface?
    ) {
        bottomSheetDialog = Utils.showCustomDialogBottum(
            context,
            R.layout.bottom_sheet_loan_list
        )
        val bindingSheet = DataBindingUtil.inflate<BottomSheetLoanListBinding>(
            layoutInflater!!,
            R.layout.bottom_sheet_loan_list,
            null,
            false
        )
        bindingSheet.rvAllLoans.adapter = CustomListAdapter(context, values, interfaceList!!)

        bindingSheet.ivCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(bindingSheet.root)
        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.show()
    }


    override fun getItemCount(): Int {
        return LoanList.size
    }

    inner class ViewHolder(var binding: LoanFormItemBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    @SuppressLint("SimpleDateFormat")
    fun onDatePick() {

        val myCalendar = Calendar.getInstance()
        myCalendar.add(Calendar.YEAR, -18)
        val date =
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)

                var dob = SimpleDateFormat("dd MMM yyyy").format(myCalendar.time)
                binding?.tvDate?.text = dob

            }

        val dialog = binding?.root?.let {
            it.context?.let { it1 ->
                DatePickerDialog(
                    it1, date,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH),
                )
            }
        }

        dialog?.datePicker?.maxDate = myCalendar.timeInMillis
        dialog?.show()
    }

    interface ButtonEnabled {
        fun isButtonEnabled(isEnabled: Boolean=true)
    }
}