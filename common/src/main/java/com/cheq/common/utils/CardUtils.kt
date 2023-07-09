package com.cheq.common.utils

import com.cheq.common.R
import com.cheq.common.models.CardType

/**
 * Created by Akash Khatkale on 18th May, 2023.
 * akash.k@cheq.one
 */
object CardUtils {

    fun getCardImage(cardName: CardType): Int {
        return when (cardName) {
            CardType.MASTERCARD -> R.drawable.ic_mastercard
            CardType.VISA -> R.drawable.visa
            CardType.DINERS_CARD -> R.drawable.ic_dinner_icon
            CardType.AMERICAN_CARD -> R.drawable.ic_american_icon
            CardType.RUPAY_CARD -> R.drawable.ic_rupay_card_icon
            else -> R.drawable.bank_icon
        }
    }

}