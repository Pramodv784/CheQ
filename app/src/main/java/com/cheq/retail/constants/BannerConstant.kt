package com.cheq.retail.constants
import com.cheq.retail.application.MainApplication
import com.cheq.retail.sharePreferences.SharePrefCheqUserId
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.ui.main.OffersItem

object BannerConstant {
    fun isVisible(item: OffersItem?, screen:String): Boolean {
        var cheqUserId = SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
            ?.getString(SharePrefsKeys.CHEQ_USER_ID)
        var status = false
        if(item?.visibility == true){
            run loop@{
                item?.rule?.screen?.forEach {
                    if (it.equals(screen,true)) {
                        status = true
                        return@loop

                    }
                }
            }
            if(status){
                if (item?.rule?.whitelist?.turn_on == true) {
                    status =false
                    run loop@{
                        item.rule.whitelist.userId?.forEach {
                            if (it.equals(cheqUserId)) {
                                status = true
                                return@loop
                            }
                        }
                    }

                }
            }

        }
        return status
    }
}