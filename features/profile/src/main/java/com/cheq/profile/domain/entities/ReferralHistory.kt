package com.cheq.profile.domain.entities

/**
 * Created by Akash Khatkale on 22nd May, 2023.
 * akash.k@cheq.one
 */
data class ReferralHistory(
    val httpStatus: Int,
    val history: List<HistoryItem>
) {
    data class HistoryItem(
        val name: String,
        val creationDate: String,
        val message: List<String>,
        val status: HistoryItemStatus,
        val type: HistoryItemType
    ) {
        enum class HistoryItemStatus(val value: String) {
            SUCCESS("SUCCESS"),
            NONE("NONE");

            companion object {
                fun find(value: String): HistoryItemStatus = values().firstOrNull { it.value.equals(value, ignoreCase = true) } ?: NONE
            }
        }

        enum class HistoryItemType(val value: String) {
            ONE_SIDED("ONE_SIDED"),
            TWO_SIDED("TWO_SIDED");

            companion object {
                fun find(value: String): HistoryItemType {
                    return values().find { it.value.equals(value, true) } ?: ONE_SIDED
                }
            }
        }
    }
}