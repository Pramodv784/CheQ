package com.cheq.retail.ui.vouchers.adapter.viewholder

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cheq.retail.R
import com.cheq.retail.ui.vouchers.model.VoucherModel

class VoucherImageVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val voucherIV = itemView.findViewById<ImageView>(R.id.voucherIV)
    fun bind(item: VoucherModel) {
        Glide.with(itemView.context)
            .load(item.imageUrl)
            .into(voucherIV)

    }
}