package com.cheq.common.models

/**
 * Created by Akash Khatkale on 18th May, 2023.
 * akash.k@cheq.one
 */
enum class CardType(val value: String) {
    MASTERCARD("MasterCard"),
    VISA("Visa"),
    AMERICAN_CARD("American Express"),
    DINERS_CARD("Diners Club"),
    RUPAY_CARD("RuPay"),
    NONE("None");

    companion object {
        fun find(value: String): CardType {
            return values().firstOrNull { it.value == value } ?: NONE
        }
    }
}