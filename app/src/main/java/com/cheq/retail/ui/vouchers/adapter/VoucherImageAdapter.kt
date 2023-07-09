package com.cheq.retail.ui.vouchers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cheq.retail.R
import com.cheq.retail.ui.vouchers.adapter.viewholder.VoucherImageVH
import com.cheq.retail.ui.vouchers.model.VoucherModel

class VoucherImageAdapter: RecyclerView.Adapter<VoucherImageVH>() {
    var voucherImageList: List<VoucherModel> = emptyList()
    set(value) {
        field = value
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoucherImageVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_voucher_images,parent,false)
        return VoucherImageVH(view)
    }

    override fun onBindViewHolder(holder: VoucherImageVH, position: Int) {
        holder.bind(voucherImageList[position])
    }

    override fun getItemCount(): Int {
       return voucherImageList.size
    }
}