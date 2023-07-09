package com.cheq.retail.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.cheq.retail.R
import com.cheq.retail.databinding.BottomsheetAddActivationBinding
import com.cheq.retail.ui.activateCard.CardDetailsActivityNew
import com.cheq.retail.ui.loans.SetLoansProviderActivity
import com.cheq.retail.ui.loans.model.Loan2
import com.cheq.retail.ui.main.adapter.CreditCardItemAdapter
import com.cheq.retail.ui.main.adapter.TopBankBillersAdapter
import com.cheq.retail.ui.main.model.cardItem
import com.cheq.retail.ui.main.viewModel.ProductBottomViewModel
import com.cheq.retail.utils.IntentKeys
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ProductBottomSheetDialog: BottomSheetDialogFragment() {
    private lateinit var binding: BottomsheetAddActivationBinding
    private var viewmodel: ProductBottomViewModel? = null
    private var topBankList: List<Loan2> = ArrayList()

    private lateinit var activity: Context
    private var rewardsCanAssign: Int = 0
    private var rewardsAssigned: Int = 0
    private var rewardsAssignUpto: Int = 0

    companion object {
        private const val REWARDS_CAN_ASSIGN = "REWARDS_CAN_ASSIGN"
        private const val REWARDS_ASSIGNED = "REWARDS_ASSIGNED"
        private const val REWARDS_ASSIGN_UPTO = "REWARDS_ASSIGN_UPTO"
        fun newInstance(
            context: Context,
            rewardsCanAssign: Int,
            rewardsAssigned: Int,
            rewardsAssignUpto: Int
        ) = ProductBottomSheetDialog().apply {
            setActivity(context)
            val bundle = Bundle().apply {
                putInt(REWARDS_CAN_ASSIGN, rewardsCanAssign)
                putInt(REWARDS_ASSIGNED, rewardsAssigned)
                putInt(REWARDS_ASSIGN_UPTO, rewardsAssignUpto)
            }
            arguments = bundle
        }
    }

    private fun setActivity(context: Context) {
        this.activity = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.bottomsheet_add_activation, container, false)
        iniviews()
        setupObsever()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel = ViewModelProvider(this)[ProductBottomViewModel::class.java]
        dialog?.setCancelable(false)

        arguments?.let {
            rewardsCanAssign = it.getInt(REWARDS_CAN_ASSIGN)
            rewardsAssigned = it.getInt(REWARDS_ASSIGNED)
            rewardsAssignUpto = it.getInt(REWARDS_ASSIGN_UPTO)
        }
    }

    private fun setupObsever() {
        viewmodel?.getTopBillers()

        viewmodel?.topBillersObserver?.observeForever {
            if (it != null)
                topBankList = it.topBillers
            binding.rvTopBank.adapter =
                getActivity()?.let { it1 ->
                    TopBankBillersAdapter(
                        it1, topBankList,
                        rewardsCanAssign,
                        rewardsAssigned,
                        rewardsAssignUpto
                    )
                }

        }
        binding.rvCards.apply {
            adapter = CreditCardItemAdapter(context, cardItem)
        }
    }

    private fun iniviews() {


        binding.ivCancel.setOnClickListener {
            dismiss()
        }

        binding.buttonAddLoans.setOnClickListener {
            dismiss()
            startActivity(
                Intent(activity, SetLoansProviderActivity::class.java)
                    .putExtra(IntentKeys.REWARDS_ASSIGN_UPTO, rewardsAssignUpto)
                    .putExtra(IntentKeys.REWARDS_CAN_ASSIGN, rewardsCanAssign)
                    .putExtra(IntentKeys.REWARDS_ASSIGNED, rewardsAssigned)
            )

        }
        binding.buttonAddCreditCard.setOnClickListener {
            dismiss()
            addCard()
        }


    }


    fun addCard() {

        startActivity(
            Intent(activity, CardDetailsActivityNew::class.java)
                .putExtra(IntentKeys.PRODUCT_ID, "null")
                .putExtra(IntentKeys.REWARDS_ASSIGN_UPTO, rewardsAssignUpto)
                .putExtra(IntentKeys.REWARDS_CAN_ASSIGN, rewardsCanAssign)
                .putExtra(IntentKeys.REWARDS_ASSIGNED, rewardsAssigned)

        )
    }
}