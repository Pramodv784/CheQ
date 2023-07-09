package com.cheq.profile.domain.entities

/**
 * Created by Akash Khatkale on 24th May, 2023.
 * akash.k@cheq.one
 */
enum class EmailLinkingStatus(val value: String) {
    IN_PROGRESS("IN_PROGRESS"),
    SUCCESS("SUCCESS"),
    FAILED("FAILED");
    companion object {
        fun find(value: String): EmailLinkingStatus
                = values().firstOrNull { it.value == value } ?: IN_PROGRESS
    }
}