package com.cheq.retail.extensions

import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys.Companion.BASE_BANK_MASTER
import com.cheq.retail.sharePreferences.SharePrefsKeys.Companion.BASE_BANNER
import com.cheq.retail.sharePreferences.SharePrefsKeys.Companion.BASE_FAQS
import com.cheq.retail.sharePreferences.SharePrefsKeys.Companion.BASE_HTML
import com.cheq.retail.sharePreferences.SharePrefsKeys.Companion.BASE_LOAN_MASTER
import com.cheq.retail.sharePreferences.SharePrefsKeys.Companion.BASE_POLICIES
import com.cheq.retail.sharePreferences.SharePrefsKeys.Companion.BASE_VOUCHER
import com.cheq.retail.sharePreferences.SharePrefsKeys.Companion.BASE_WAITLIST

val SharePrefs.bankMasterUrl
    get() = getBankString(BASE_BANK_MASTER)
val SharePrefs.loanMasterUrl
    get() = getLoanString(BASE_LOAN_MASTER)
val SharePrefs.bannerBaseUrl
    get() = getBannerString(BASE_BANNER)
val SharePrefs.htmlBaseUrl
    get() = getHtmlString(BASE_HTML)
val SharePrefs.policiesBaseUrl
    get() = getPoliciesString(BASE_POLICIES)
val SharePrefs.faqsBaseUrl
    get() = getFaqString(BASE_FAQS)
val SharePrefs.voucherBaseUrl
    get() = getVoucherString(BASE_VOUCHER)
val SharePrefs.waitlistBaseUrl
    get() = getWaitListString(BASE_WAITLIST)



