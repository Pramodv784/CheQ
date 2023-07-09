package com.cheq.retail.ui.referral.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class CheckShorUrlRequest ( val cheqUserId: String? = null,

                            ) : Parcelable