package com.cheq.retail.application

import KeystoreEncryption
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.StrictMode
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.multidex.MultiDexApplication
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.deeplink.DeepLinkListener
import com.appsflyer.deeplink.DeepLinkResult
import com.cheq.retail.BuildConfig
import com.cheq.retail.R
import com.cheq.retail.api.RestClient
import com.cheq.retail.base.FirebaseRemoteConfigUtils
import com.cheq.retail.constants.AFConstants
import com.cheq.retail.constants.AFConstants.AF_DOMAIN
import com.cheq.retail.constants.Constant
import com.cheq.retail.constants.FirebaseLog
import com.cheq.retail.constants.TraceConstants
import com.cheq.retail.sharePreferences.SharePrefCheqUserId
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsKeys.Companion.HOST
import com.cheq.retail.sharePreferences.SharePrefsKeys.Companion.REFERRAL_KEY
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.sharePreferences.SharePrefsReferral
import com.cheq.retail.ui.main.model.SslPinning
import com.cheq.retail.ui.referral.repository.ReferralRepository
import com.cheq.retail.utils.EncryptionPass
import com.cheq.retail.utils.KeystoreDecryption
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.Utils
import com.freshchat.consumer.sdk.Event
import com.freshchat.consumer.sdk.Freshchat
import com.freshchat.consumer.sdk.FreshchatConfig
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.perf.ktx.performance
import com.google.firebase.perf.metrics.Trace
import com.moengage.core.DataCenter
import com.moengage.core.LogLevel
import com.moengage.core.MoEngage
import com.moengage.core.config.LogConfig
import com.moengage.core.config.NotificationConfig
import com.mparticle.MParticle
import com.mparticle.MParticleOptions
import dagger.hilt.android.HiltAndroidApp
import com.smsapi.ProcessSMS
import kotlinx.coroutines.*
import org.json.JSONObject
import java.security.GeneralSecurityException
import java.util.concurrent.Executors


/**
 * Application class for the app manage dao,context etc.
 */
@HiltAndroidApp
class MainApplication : MultiDexApplication() {
    var conversionData: Map<String, Any>? = null
    var encryption_key_fetch_time: Trace? = null
    val referralRepository: ReferralRepository by lazy {
        ReferralRepository(RestClient.getInstance(this).referralApi, this)
    }
    private lateinit var firebaseDatabase: FirebaseDatabase
    var receiver: BroadcastReceiver
    override fun onTerminate() {

        super.onTerminate()
        getLocalBroadcastManager()?.unregisterReceiver(receiver);
    }

    init {
        instance = this
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent == null || intent.extras == null) {
                    return
                }
                val event: Event? = Freshchat.getEventFromBundle(intent.extras!!)
                if (event != null) {

                    if (event.eventName.name == "FCEventCsatSubmit") {  // create freshchat user
                        Freshchat.resetUser(context);
                        Utils.createFreshChatUser()
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        private var instance: MainApplication? = null
        lateinit var finartProcessInstance: ProcessSMS

        fun getApplicationContext(): Context {
            return instance!!.applicationContext
        }

        private fun getFinartInstance(): ProcessSMS {
            if (isFinartInitialized()) {
                return finartProcessInstance
            } else {
                finartProcessInstance = ProcessSMS(getApplicationContext(), null, "")
                return finartProcessInstance
            }
        }
        fun isFinartInitialized(): Boolean = this::finartProcessInstance.isInitialized
        fun finartLogout() {
            val instance = getFinartInstance()
            instance.logout()
        }
    }

    override fun onCreate() {
        super.onCreate()

        initEncryptPass()
        initMparticle()
        initFirebaseData()
        enableStrictMode()
        intiMoreEngage()
        initFreshChat()
        initAppsFlyer()
        FirebaseRemoteConfigUtils.init()

    }


    private fun initEncryptPass() {
        firebaseDatabase = FirebaseDatabase.getInstance(BuildConfig.firebaseDataBaseURL)
        encryption_key_fetch_time = Firebase.performance.newTrace(TraceConstants.ENCRYPTION_KEY_FETCH_TIME)
        encryption_key_fetch_time?.start()
        firebaseDatabase.setPersistenceEnabled(true)
            var encryptionDb = firebaseDatabase.getReference(Constant.ENCRYPT_KEY)
            encryptionDb.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.value?.let {
                        encryption_key_fetch_time?.stop()
                        try {
                            KeystoreEncryption.encryptText("CHEQ_ALIAS", it.toString())
                            EncryptionPass.key = it.toString()
                        } catch (e: Exception) {
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    encryption_key_fetch_time?.stop()
                    FirebaseLog.FireBaseLogEvent(
                        applicationContext,
                        AFConstants.EncryptionPassApplication,
                        AFConstants.EncryptionPassApplication,
                        error.details
                    )
                }

            })

    }


    private fun initFirebaseData() {
        if (this::firebaseDatabase.isInitialized) {
            val prefs = SharePrefsReferral.getInstance(applicationContext)
            val sslReference = firebaseDatabase.getReference(Constant.SSL_PINNING)
            sslReference.keepSynced(true)
            val sslListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val _data = snapshot.getValue(SslPinning::class.java)
                    _data?.let { it1 ->
                        it1.enabled?.let { prefs?.putBoolean(SharePrefsKeys.SSL_ENABLED, it) }
                        it1.keys?.let { prefs?.putList(SharePrefsKeys.SSL_KEYS, it) }
                        it1.host?.let { prefs?.putString(HOST, it) }
                        prefs?.putBoolean(SharePrefsKeys.SSL_SAVED, true)
                    }
                }


                override fun onCancelled(error: DatabaseError) {
                    FirebaseLog.FireBaseLogEvent(
                        applicationContext,
                        AFConstants.FBEvent_SSL_PINNING,
                        AFConstants.FBEvent_SSL_PINNING_ERROR,
                        error.details
                    )
                }

            }

            sslReference.addValueEventListener(sslListener)
            //sslDelayReference.addValueEventListener(sslDelayListener)
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }


    private fun initMparticle() {
        val options = MParticleOptions.builder(this).environment(MParticle.Environment.Production)
            .credentials(
                BuildConfig.mParticleApiKey, BuildConfig.mParticleApiSecret
            ).build()

        MParticle.start(options)
    }

    private fun initFreshChat() {
        val config = FreshchatConfig(
            "af952598-624e-483f-bbdc-8eec41d6d707", "2c1d5d77-1300-46af-9e29-39a2183c34c4"
        )
        config.domain = "msdk.in.freshchat.com"
        config.isCameraCaptureEnabled = true
        config.isGallerySelectionEnabled = true
        config.isResponseExpectationEnabled = true
        config.isResponseExpectationEnabled = true
        Freshchat.getInstance(applicationContext).init(config)
        registerReceiver()

    }

    fun getLocalBroadcastManager(): LocalBroadcastManager? {
        return LocalBroadcastManager.getInstance(applicationContext)
    }

    fun registerReceiver() {

        val userActionsIntentFilter: IntentFilter = IntentFilter(Freshchat.FRESHCHAT_EVENTS)
        getLocalBroadcastManager()?.registerReceiver(
            receiver,
            userActionsIntentFilter
        )
    }

    private fun intiMoreEngage() {
        val moEngage = MoEngage.Builder(this, "51HO1IWYGNDYKLXJL7LJJP10")
            .enableEncryption()
            .setDataCenter(DataCenter.DATA_CENTER_3).configureNotificationMetaData(
                NotificationConfig(
                    R.mipmap.ic_launcher, R.mipmap.ic_launcher
                )
            ).configureLogs(LogConfig(LogLevel.VERBOSE, false)).build()

        MoEngage.initialiseDefaultInstance(moEngage)
    }

    private fun initAppsFlyer() {
        val appsflyer = AppsFlyerLib.getInstance()
        appsflyer.setOneLinkCustomDomain(AF_DOMAIN)

        val conversionListener: AppsFlyerConversionListener = object : AppsFlyerConversionListener {
            /* Returns the attribution data. Note - the same conversion data is returned every time per install */
            override fun onConversionDataSuccess(conversionData: Map<String, Any>) {
                for (attrName in conversionData.keys) {


                }
            }

            override fun onConversionDataFail(errorMessage: String) {
                logDeepLinkEvents(
                    AFConstants.AF_CONVERSION_DATA_FAILED,
                    errorMessage,
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.AF_CONVERSION_DATA_FAILED),
                    mapOf(AFConstants.CURRENT_USER_CHEQ_ID to checkForCheqUserId())
                )
            }

            /* Called only when a Deep Link is opened */
            override fun onAppOpenAttribution(conversionData: Map<String, String>) {
                for (attrName in conversionData.keys) {
                }
            }

            override fun onAttributionFailure(errorMessage: String) {
                logDeepLinkEvents(
                    AFConstants.AF_ATTRIBUTION_FAILED,
                    errorMessage,
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.AF_ATTRIBUTION_FAILED),
                    mapOf(AFConstants.CURRENT_USER_CHEQ_ID to checkForCheqUserId())
                )
            }
        }

        appsflyer.init(AFConstants.AF_DEV_KEY, conversionListener, this)
        appsflyer.setAppId(BuildConfig.APPLICATION_ID)
        appsflyer.getAppsFlyerUID(applicationContext)
        appsflyer.setAppInviteOneLink(AFConstants.TEMPLATE_ID)
        appsflyer.start(applicationContext)


        AppsFlyerLib.getInstance().subscribeForDeepLink(DeepLinkListener { deepLinkResult ->
            val dlStatus = deepLinkResult.status
            if (dlStatus == DeepLinkResult.Status.FOUND) {
            } else if (dlStatus == DeepLinkResult.Status.NOT_FOUND) {
                logDeepLinkEvents(
                    AFConstants.DEEPLINK_NOT_FOUND,
                    "",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.DEEP_LINK_NOT_FOUND),
                    mapOf(AFConstants.CURRENT_USER_CHEQ_ID to checkForCheqUserId())
                )
                return@DeepLinkListener
            } else {
                val dlError = deepLinkResult.error
                logDeepLinkEvents(
                    AFConstants.DEEPLINK_ERROR,
                    dlError.toString(),
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.DEEPLINK_ERROR),
                    mapOf(AFConstants.CURRENT_USER_CHEQ_ID to checkForCheqUserId())
                )
                return@DeepLinkListener
            }
            val deepLinkObj = deepLinkResult.deepLink

            if (deepLinkObj.isDeferred != null && deepLinkObj.isDeferred == true) {
            } else {
            }
            try {
                var referrerId: String?
                val dlData: JSONObject = deepLinkObj.clickEvent
                if (dlData.has(AFConstants.DEEP_LINK_SUB1) && dlData.getString(AFConstants.DEEP_LINK_SUB1)
                        .equals(AFConstants.INVITE, true)
                ) {
                    referrerId = deepLinkObj.getStringValue(AFConstants.DEEP_LINK_SUB2)
                    if (!referrerId.isNullOrEmpty())
                        SharePrefsReferral.getInstance(applicationContext)
                            ?.setReferralId(REFERRAL_KEY, referrerId)
                    else {
                        logDeepLinkEvents(
                            AFConstants.REFERRER_ID_NOT_FOUND,
                            "${deepLinkObj.deepLinkValue}",
                            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.REFERRER_ID_NOT_FOUND),
                            mapOf(AFConstants.CURRENT_USER_CHEQ_ID to checkForCheqUserId())
                        )
                    }
                }

            } catch (e: Exception) {
                logDeepLinkEvents(
                    AFConstants.DEEPLINK_SUBSCRIPTION_FAILED,
                    "${e.message}",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.DEEPLINK_SUBSCRIPTION_FAILED),
                    mapOf(AFConstants.CURRENT_USER_CHEQ_ID to checkForCheqUserId())
                )
                return@DeepLinkListener
            }
        })
    }

    private fun checkForCheqUserId(): String {
        SharePrefCheqUserId.getInstance(applicationContext)
            ?.getString(SharePrefsKeys.CHEQ_USER_ID).let {
                return if (it.isNullOrEmpty()) getString(R.string.cheq_user_id_not_found) else it
            }
    }

    private fun logDeepLinkEvents(
        eventName: String,
        eventDesc: String,
        logEvent: Boolean?,
        otherAttributes: Map<String, String>?
    ) {
        MparticleUtils.logEvent(
            eventName,
            eventDesc,
            "",
            "",
            logEvent,
            applicationContext,
            otherAttributes
        )
    }

    private fun enableStrictMode() {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                m.invoke(null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}