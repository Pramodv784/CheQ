package com.cheq.cache.sharedprefs

/**
 * Created by Akash Khatkale on 16th May, 2023.
 * akash.k@cheq.one
 */
enum class SharedPrefsType(val value: String) {
    DEEPLINK("DEEPLINK"),
    REFERRAL("REFERRAL"),
    CHEQUSER("CHEQUSERID"),
    LOG("LogScreen"),
    CHEQ("CHEQ");
}