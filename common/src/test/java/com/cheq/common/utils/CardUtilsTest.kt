package com.cheq.common.utils

import com.cheq.common.R
import com.cheq.common.models.CardType
import org.junit.Assert
import org.junit.Test

/**
 * Created by Akash Khatkale on 23rd May, 2023.
 * akash.k@cheq.one
 */
class CardUtilsTest {

    @Test
    fun `When card type is valid, return image`() {
        val master = CardUtils.getCardImage(CardType.MASTERCARD)
        val rupay = CardUtils.getCardImage(CardType.RUPAY_CARD)
        val americanCard = CardUtils.getCardImage(CardType.AMERICAN_CARD)
        val diners = CardUtils.getCardImage(CardType.DINERS_CARD)
        val visa = CardUtils.getCardImage(CardType.VISA)

        Assert.assertEquals(R.drawable.ic_mastercard, master)
        Assert.assertEquals(R.drawable.ic_rupay_card_icon, rupay)
        Assert.assertEquals(R.drawable.ic_american_icon, americanCard)
        Assert.assertEquals(R.drawable.ic_dinner_icon, diners)
        Assert.assertEquals(R.drawable.visa, visa)
    }

    @Test
    fun `When card type is a valid string, return image`() {
        val master = CardUtils.getCardImage(CardType.find("MasterCard"))
        val rupay = CardUtils.getCardImage(CardType.find("RuPay"))
        val diners = CardUtils.getCardImage(CardType.find("Diners Club"))
        val americanCard = CardUtils.getCardImage(CardType.find("American Express"))
        val visa = CardUtils.getCardImage(CardType.find("Visa"))

        Assert.assertEquals(R.drawable.ic_mastercard, master)
        Assert.assertEquals(R.drawable.ic_rupay_card_icon, rupay)
        Assert.assertEquals(R.drawable.ic_american_icon, americanCard)
        Assert.assertEquals(R.drawable.ic_dinner_icon, diners)
        Assert.assertEquals(R.drawable.visa, visa)
    }

    @Test
    fun `When card type is invalid, return mastercard`() {
        val type = CardUtils.getCardImage(CardType.find("abc"))
        val noneType = CardUtils.getCardImage(CardType.NONE)

        Assert.assertEquals(R.drawable.ic_mastercard, type)
        Assert.assertEquals(R.drawable.ic_mastercard, noneType)
    }
}