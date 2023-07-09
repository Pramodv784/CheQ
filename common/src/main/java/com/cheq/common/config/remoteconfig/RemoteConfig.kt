package com.cheq.common.config.remoteconfig


/**
 * Created by Akash Khatkale on 24th May, 2023.
 * akash.k@cheq.one
 */
interface RemoteConfig {

    companion object {
        const val CHEQ_SAFE_PRIMARY_CTA = "cheq_safe_primary_cta"
        const val CHEQ_SAFE_SECONDARY_CTA = "cheq_safe_secondary_cta"
        const val CHEQ_SAFE_TITLE = "cheq_safe_title"
        const val CHEQ_SAFE_SUB_TITLE = "cheq_safe_subtitle"
        const val NEW_ARCH_ENABLED = "enable_new_arch"
    }

    fun getString(key: String): String
    fun getBoolean(key: String): Boolean

}