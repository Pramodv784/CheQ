package com.cheq.retail.constants

import android.content.Context
import com.appsflyer.AFInAppEventParameterName
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.cheq.retail.BuildConfig
import com.cheq.retail.constants.AFConstants.TEMPLATE_ID


object AppsFlyEvents {

    fun logEventFly(
        context: Context,
        _Requirement: String,
        Event: String,
        CustomEventNamesAF: String
    ) {

        val eventValues = HashMap<String, Any>()
        AppsFlyerLib.getInstance().start(context)
        eventValues.put("Requirement", _Requirement)
        eventValues.put("Event", Event)
        eventValues.put("CustomEventNamesAF", CustomEventNamesAF)
        eventValues.put(AFInAppEventParameterName.CONTENT_ID, "1")

        AppsFlyerLib.getInstance().setAppId(BuildConfig.APPLICATION_ID)
        //AppsFlyerLib.getInstance().setCustomerUserId(userId)

        AppsFlyerLib.getInstance().setAppInviteOneLink(TEMPLATE_ID)


        AppsFlyerLib.getInstance().logEvent(
            context,
            Event,
            eventValues,
            object : AppsFlyerRequestListener {
                override fun onSuccess() {

                }

                override fun onError(p0: Int, p1: String) {

                }
            })
    }

    fun setCurrentUserForAF(context: Context, checkUserId: String) {
        AppsFlyerLib.getInstance().start(context)
        AppsFlyerLib.getInstance().setAppId(BuildConfig.APPLICATION_ID)
        AppsFlyerLib.getInstance().setCustomerUserId(checkUserId)

        AppsFlyerLib.getInstance().setAppInviteOneLink(TEMPLATE_ID)
        //Pass Cheq User Id In Firebase
      //  setCurrentUserForFirebase(context, checkUserId)
    }

    /* fun firstCall() {

         val ioScope = CoroutineScope(Dispatchers.IO + Job())
         ioScope.launch {
             val client = OkHttpClient()
             val mediaType = "application/json".toMediaTypeOrNull()
             val body = RequestBody.create(
                 mediaType,
                 "{\"timestamp\":1625576172928,\"request_id\":\"71dad7da-7926-40d8-9b15-b94a6d46e15a\"," +
                         "\"ip\":\"35.244.183.10\",\"user_agent\":\"Roku/DVP-9.21 (AE9.21E04090A)\",\"device_os_version\":\"9.3.0\"," +
                         "\"device_model\":\"${Build.MODEL}\",\"device_ids\":[{\"type\":\"rida\",\"value\":\"fa73d67d-f55c-5af3-883a-726253dc7d0e\"}," +
                         "{\"type\":\"custom\",\"value\":\"045f4137-57b7-45e6-8a45-c303101a086a\"}],\"limit_ad_tracking\":true,\"customer_user_id\":" +
                         "\"15667737-366d-4994-ac8b-653fe6b2be4a\",\"app_version\":\"${BuildConfig.VERSION_NAME}\"}"
             )
             val request = Request.Builder()
                 .url("https://events.appsflyer.com/v1.0/s2s/first_open/app/platform/${BuildConfig.APPLICATION_ID}")
                 .post(body)
                 .addHeader("accept", "application/json")
                 .addHeader("content-type", "application/json")
                 .build()

             val response = client.newCall(request).execute()

         }

     }*/
}