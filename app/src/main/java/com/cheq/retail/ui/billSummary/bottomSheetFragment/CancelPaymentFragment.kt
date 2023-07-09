package com.cheq.retail.ui.billSummary.bottomSheetFragment

import android.content.Context
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cheq.retail.R
import com.cheq.retail.base.FirebaseRemoteConfigUtils
import com.cheq.retail.databinding.FragmentCancelPaymentBinding
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.billPayments.PaymentMethodsActivity
import com.cheq.retail.ui.billSummary.BillSummaryListener
import com.cheq.retail.utils.MparticleUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class CancelPaymentFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentCancelPaymentBinding
    private var callback: BillSummaryListener? = null

    var netEarnings: Int = 0
    var subTitle : String? = null
    var isPaytogether: Boolean = false
    companion object {
        private const val NET_EARNINGS = "NET_EARNINGS"
        private const val PAY_TOGETHER = "PAY_TOGETHER"
        private const val SUB_TITLE = "SUB_TITLE"
        fun newInstance(netEarnings: Int, isPaytogether: Boolean, subTitle: String?): CancelPaymentFragment {
            return CancelPaymentFragment().apply {
                val bundle = Bundle()
                bundle.apply {
                    putInt(NET_EARNINGS, netEarnings)
                    putBoolean(PAY_TOGETHER, isPaytogether)
                    putString(SUB_TITLE, subTitle)
                }
                arguments = bundle
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCancelPaymentBinding.inflate(layoutInflater, container, false)
        dialog?.setCancelable(false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            netEarnings = it.getInt(NET_EARNINGS)
            isPaytogether = it.getBoolean(PAY_TOGETHER)
            subTitle = it.getString(SUB_TITLE)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun onCancelClick() {
        //remove dismiss later and call create transaction
        dialog?.dismiss()
        callback?.dismiss()
        MparticleUtils.logEvent(
            "CC_Payment_BillSummary_Cancel_Secondary_Clicked",
            "User views the nudge bottomsheet after clicking cross CTA on bill summary, and clicks on secondary CTA to cancel payment",
            "Not Unique",
            "Continue",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Review_Clicked),
            requireContext()
        )
    }

    fun onMakePaymentClick() {
        dialog?.dismiss()
        MparticleUtils.logEvent(
            "CC_Payment_BillSummary_Cancel_Primary_Clicked",
            "User views the nudge bottomsheet after clicking cross CTA on bill summary, and clicks on primary CTA to continue payment",
            "Not Unique",
            "Continue",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Review_Clicked),
            requireContext()
        )
    }

    fun init() {
        binding.earnings = netEarnings
        binding.callback = this@CancelPaymentFragment


        if (subTitle!=null && !isPaytogether) {
            val title = requireContext().getString(
                R.string.you_can_earn_upto,
                subTitle,
                FirebaseRemoteConfigUtils.getNetEarningsTitleText()
            )
            binding.tvTitle.text = Html.fromHtml(title, 0)
        } else {
            binding.tvTitle.text = FirebaseRemoteConfigUtils.getNonNetEarningsTitleText()
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as BillSummaryListener
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

}