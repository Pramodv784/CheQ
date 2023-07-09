package com.cheq.retail.ui.billPayments.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cheq.retail.R
import com.cheq.retail.databinding.SavedCardItemLayoutBinding
import com.cheq.retail.extensions.bankMasterUrl
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.ui.activateCard.CardDetailsActivityNew
import com.cheq.retail.ui.billPayments.PaymentMethodsActivity.SupportedUPIApps.Companion.SAVED_CARD
import com.cheq.retail.ui.billPayments.model.PaymentOptionsResponse
import com.cheq.retail.ui.billPayments.paymentsInterface.PaymentOptionInterface
import com.cheq.retail.utils.ImageUtils.loadSvg

class SavedCardAdapter(
    private val savedCardList: List<PaymentOptionsResponse.SavedCardsItem?>,
    private val context: Context,
    private val preferredMethodAdapterInterface: PaymentOptionInterface

) : RecyclerView.Adapter<SavedCardAdapter.SavedCardViewHolder>() {
    var clicked = 1
    var bankMasterId = ""
    private var binding: SavedCardItemLayoutBinding? = null
    private var layoutInflater: LayoutInflater? = null
    lateinit var prefs: SharePrefs
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): SavedCardViewHolder {
        prefs = SharePrefs.getInstance(context)!!
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(context)
        }
        binding = DataBindingUtil.inflate(
            layoutInflater!!, R.layout.saved_card_item_layout, parent, false
        )
        return SavedCardViewHolder(binding!!)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SavedCardViewHolder, position: Int) {
        val item = savedCardList[position]
        holder.mBinding.tvDebitCardNumber.visibility = View.VISIBLE
        holder.mBinding.tvDebitCardNumber.text = " XXXX  ${item?.cardlast4}"
        if (item?.bankMaster != null) {
            holder.mBinding.tvBankName.text = item.bankMaster.originalBankName
            bankMasterId = item.bankMaster.id ?: ""


            if (item.cardNetwork == CardDetailsActivityNew.RUPAY) {
                holder.mBinding.ivBankLogo.setImageDrawable(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_rupay_card_icon
                    )
                )
            }

            if (item.cardNetwork == CardDetailsActivityNew.VISA) {
                holder.mBinding.ivBankLogo.setImageDrawable(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.visa
                    )
                )
            }
            if (item.cardNetwork == CardDetailsActivityNew.MASTER_CARD) {
                holder.mBinding.ivBankLogo.setImageDrawable(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_mastercard
                    )
                )
            }

        }

        holder.itemView.setOnClickListener {

            if (clicked == 1) {
                holder.mBinding.llCVVLayout.visibility = View.VISIBLE
                clicked = 0
            } else {
                holder.mBinding.llCVVLayout.visibility = View.GONE
                clicked = 1
            }
        }
        holder.mBinding.etCvv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                holder.mBinding.btnPayNow.isEnabled = p0?.length == 3
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        holder.mBinding.btnPayNow.setOnClickListener {
            preferredMethodAdapterInterface.onPreferredMethodClicked(
                SAVED_CARD,
                item?.cardToken ?: "",
                item?.bankMaster?.id ?: "",
                item?.bankMaster?.originalBankName + "",
                "",
                item?.cardlast4 ?: "",
                holder.mBinding.etCvv.text.toString(),
                item?.bankMaster?.originalBankName ?: "",
                item?.cardTokenTd ?: "",
                bankMasterId
            )
            holder.mBinding.llCVVLayout.visibility = View.GONE


        }

    }

    override fun getItemCount(): Int {
        return savedCardList.size
    }

    class SavedCardViewHolder(val mBinding: SavedCardItemLayoutBinding) :
        RecyclerView.ViewHolder(mBinding.root)
}