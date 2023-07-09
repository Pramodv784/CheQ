package com.cheq.retail.utils

import android.content.Context
import com.cheq.retail.constants.AFConstants.FINART_BACKGROUND_SMS_PROCESSING
import com.cheq.retail.constants.AFConstants.FINART_INVOKE
import com.cheq.retail.constants.AFConstants.FINART_SUCCESS
import com.mparticle.MPEvent
import com.mparticle.MParticle
import com.mparticle.identity.IdentityApiRequest

class MparticleUtils {

    companion object {

        fun setCurrentUser(context: Context, checkUserId: String) {


            val identityRequest = IdentityApiRequest.withEmptyUser()
                .customerId(checkUserId)
                .build()
            MParticle.getInstance()?.Identity()?.login(identityRequest)




        }

        fun logCurrentScreen(
            screenName: String,
            screenDescription: String,
            label: String,
            properties: String,
            label2: String,
            properties2: String,
            logEvent: Boolean?,
            context: Context
        ) {
           // if (logEvent != null && logEvent) {
                val screenInfo: MutableMap<String, String> = HashMap()
                screenInfo["Description"] = screenDescription
                if (label.isNotEmpty()) screenInfo[label] = properties
                if (label2.isNotEmpty()) screenInfo[label2] = properties2
                MParticle.getInstance()?.logScreen(screenName, screenInfo)

//                val properties = Properties()
//                properties.addAttribute("Description", screenDescription)
//                if (label.isNotEmpty()) properties.addAttribute(label, properties)
//                if (label2.isNotEmpty()) properties.addAttribute(label2, properties2)


//                Analytics.with(context)
//                    .track(
//                        screenName, properties
//                    )

                //   MoEAnalyticsHelper.trackEvent(context, screenName, properties)

          //  }

        }

        fun logCurrentScreen(
            screenName: String = "",
            screenDescription: String = "",
            attribute1: String = "",
            value1: String = "",
            attribute2: String = "",
            value2: String = ""
        ) {
            val screenInfo: MutableMap<String, String> = HashMap()
            screenInfo["Description"] = screenDescription
            if (attribute1.isNotEmpty()) screenInfo[attribute1] = value1
            if (attribute2.isNotEmpty()) screenInfo[attribute2] = value2
            MParticle.getInstance()?.logScreen(screenName, screenInfo)
        }

        fun logEvent(
            eventName: String,
            eventDescription: String,
            eventType: String ? = null,
            objectType: String ? = null,
            logEvent: Boolean? = null,
            context: Context? = null,
            otherAttributes: Map<String, String>? = null
        ) {

           // if (logEvent != null && logEvent) {
                val customAttributes = mutableMapOf(
                    "EventDescription" to eventDescription,
                    "EventType" to eventType,
                    "ObjectType" to objectType,
                )

                if(otherAttributes != null) {
                    customAttributes.putAll(otherAttributes)
                }

                val event = MPEvent.Builder(eventName, MParticle.EventType.Other)
                    .customAttributes(customAttributes)
                    .build()

                MParticle.getInstance()?.logEvent(event)

//                val properties = Properties()
//
//                  properties.addAttribute("EventDescription", eventDescription)
//                  properties.addAttribute("eventType", eventType)
//                  properties.addAttribute("objectType", objectType)


                //   MoEAnalyticsHelper.trackEvent(context, eventName, properties)
          //  }
        }
        fun logReferralEvent(
            eventName: String,
            logEvent: Boolean?,
            otherAttributes: Map<String, String>? = null
        ) {
            // if (logEvent != null && logEvent) {
            val event = MPEvent.Builder(eventName, MParticle.EventType.Other)
                .customAttributes(otherAttributes)
                .build()

            MParticle.getInstance()?.logEvent(event)
        }
        fun logBillSummaryEvent(
            eventName: String,
            logEvent: Boolean?,
            otherAttributes: Map<String, Any>? = null
        ) {
            // if (logEvent != null && logEvent) {
            val event = MPEvent.Builder(eventName, MParticle.EventType.Other)
                .customAttributes(otherAttributes)
                .build()

            MParticle.getInstance()?.logEvent(event)
        }
        fun logFinartEvent(finartStatus: String, screen: String) {
            var screenName = ""
            var desc = ""
            val attribute1 = "invokedAt"
            var invokedAt = ""
            when (finartStatus) {
                FINART_INVOKE -> {
                    screenName = "Finart_SDK_Invoked"
                    desc = "CheQ sends Finart a request for fetching SMSes of this user, while the user sees the $screen screen"
                    invokedAt = screen
                }
                FINART_SUCCESS -> {
                    screenName = "Finart_SMS_Found"
                    desc = "Finart found atleast 1 or more SMSes that may have a bill to be updated"
                    invokedAt = screen
                }
                FINART_BACKGROUND_SMS_PROCESSING -> {
                    screenName = "Finart_SMS_Found_In_Background"
                    desc = "Finart found atleast 1 or more SMSes in $screen that may have a bill to be updated"
                    invokedAt = screen
                }
            }
            logCurrentScreen(
                screenName = screenName,
                screenDescription = desc,
                attribute1 = attribute1,
                value1 = invokedAt
            )
        }
        fun logBlockerScreen(screenName: String, propertyMap : HashMap<String, String>) {
            val properties: MutableMap<String?, String?> = HashMap()
            for ((property, value) in propertyMap) {
                properties[property] = value
            }
            MParticle.getInstance()?.logScreen(screenName, properties)
        }
        fun logBlockerEvent(eventName: String, propertyMap : HashMap<String, String>) {
            val event = MPEvent.Builder(eventName, MParticle.EventType.Other)
                .customAttributes(propertyMap)
                .build()
            MParticle.getInstance()?.logEvent(event)
        }


        fun logCardEnterScreen(screenName: String, propertyMap : HashMap<String, String>) {
            val properties: MutableMap<String?, String?> = HashMap()
            for ((property, value) in propertyMap) {
                properties[property] = value
            }
            MParticle.getInstance()?.logScreen(screenName, properties)
        }

        fun logCardErrorScreen(screenName: String, propertyMap : HashMap<String, String>) {
            val properties: MutableMap<String?, String?> = HashMap()
            for ((property, value) in propertyMap) {
                properties[property] = value
            }
            MParticle.getInstance()?.logScreen(screenName, properties)
        }



    }
}