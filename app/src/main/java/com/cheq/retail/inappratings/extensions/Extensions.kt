package com.cheq.retail.inappratings.extensions

import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import androidx.databinding.BindingAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup


/**
 * Created by Sanket Mendon on 18,June,2023.
 * sanket@cheq.one
 */

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}


@BindingAdapter("createChips")
fun ChipGroup.createChips(listOfOptions: List<String>?) {
    if (listOfOptions?.isNotEmpty() == true) {
        this.removeAllViewsInLayout()
        for (item in listOfOptions) {
            val chip = Chip(context).apply { text = item }
            this.addView(chip)
        }
    }
}