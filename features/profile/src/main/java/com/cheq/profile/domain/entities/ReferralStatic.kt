package com.cheq.profile.domain.entities

/**
 * Created by Akash Khatkale on 20th May, 2023.
 * akash.k@cheq.one
 */
data class ReferralStatic(
    val offerValid: Boolean,
    val youGetValue: Int,
    val youGetMessage: String,
    val httpStatus: Int,
    val type: ReferralStaticType,
    val defaultMessage: String,
    val friendsGetValue: Int,
    val chips: Int,
    val friendsGetMessage: String,
    val steps: List<String>,
    val whatsappMessage: String,
    val daysLeft: String
) {
    enum class ReferralStaticType(val value: String) {
        ONE_SIDED("ONE_SIDED"),
        TWO_SIDED("TWO_SIDED");

        companion object {
            fun find(value: String): ReferralStaticType {
                return values().find { it.value.equals(value, true) } ?: ONE_SIDED
            }
        }
    }
}