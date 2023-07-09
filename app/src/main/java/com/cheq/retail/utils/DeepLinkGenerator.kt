package com.cheq.retail.utils

import android.content.Context
import com.appsflyer.share.LinkGenerator

class DeepLinkGenerator {

    fun createDeepLink(context: Context,page:String) {
        val check = LinkGenerator("OPEN_PRODUCT")
            .addParameter("page",page)
//        check.generateLink()

        check.generateLink(context,object :LinkGenerator.AFa1xSDK{
            override fun onResponse(p0: String?) {
            }

            override fun onResponseError(p0: String?) {
            }
        })

    }
}