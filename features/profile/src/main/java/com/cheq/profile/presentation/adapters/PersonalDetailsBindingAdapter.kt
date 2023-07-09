package com.cheq.profile.presentation.adapters

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.cheq.profile.domain.entities.PersonalDetails


/**
 * Created by Sanket Mendon on 19,May,2023.
 * sanket@cheq.one
 */

@BindingAdapter("setData")
fun TextView.setData(personalDetails: PersonalDetails?) {
    var data: String? = null
    personalDetails?.let {
        when (this.tag) {
            "iv_initials" -> {
                data = personalDetails.initials
            }

            "tv_fullname" -> {
                data = personalDetails.fullname
            }

            "tv_mobile" -> {
                data = personalDetails.mobile
            }

            "tv_email" -> {
                data = personalDetails.email
            }
        }
        this.text = data
    }
}