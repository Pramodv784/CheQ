package com.cheq.retail.utils


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.cheq.retail.R
import com.cheq.retail.application.MainApplication
import com.cheq.retail.ui.splash.SplashActivity
import com.freshchat.consumer.sdk.Freshchat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.moengage.core.LogLevel
import com.moengage.core.internal.logger.Logger
import com.moengage.firebase.MoEFireBaseHelper
import com.moengage.pushbase.MoEPushHelper
import java.util.*

class MessagingServiceUtils : FirebaseMessagingService() {
    val tag="PUSH====="
    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        //if the message contains data payload
        //It is a map of custom keyvalues
        //we can read it easily

        if (Freshchat.isFreshchatNotification(remoteMessage)) {
            Freshchat.handleFcmMessage(applicationContext, remoteMessage);
        } else {
            // Handle notifications with data payload for your app

            try {
                val pushPayload = remoteMessage.data
                if (MoEPushHelper.getInstance().isFromMoEngagePlatform(pushPayload)) {
                    Logger.print { "$tag onMessageReceived() : Will try to show push" }
                    MoEFireBaseHelper.getInstance().passPushPayload(applicationContext, pushPayload)
                } else {
                    Logger.print { "$tag onMessageReceived() : Not a MoEngage Payload." }
                    if (remoteMessage.data.isNotEmpty()) {
                        //handle the data message here
                        val title = Objects.requireNonNull(remoteMessage.notification)?.title
                        val body = remoteMessage.notification!!.body
                        sendNotification(title, body)
                    } else if (remoteMessage.notification != null) {
                        //handle the data message here
                        val title = remoteMessage.notification!!.title
                        val body = remoteMessage.notification!!.body
                        sendNotification(title, body)
                    }
                }
            } catch (e: Exception) {
                Logger.print(LogLevel.ERROR, e) { "$tag onMessageReceived() : " }
            }

//            //getting the title and the body
//            val title = Objects.requireNonNull(remoteMessage.notification)?.title
//            val body = remoteMessage.notification!!.body
            //then here we can use the title and body to build a notification
        }


    }


    override fun onNewToken(s: String) {
//        try {
//            Logger.print { "$tag onNewToken() : Push Token $s" }
//            TokenRegistrationHandler.processPushToken(applicationContext, s)
//        } catch (e: Exception) {
//            Logger.print(LogLevel.ERROR, e) { "$tag onNewToken() : " }
//        }
         Freshchat.getInstance(this).setPushRegistrationToken(s)
         super.onNewToken(s)
    }

    companion object {
        @RequiresApi(api = Build.VERSION_CODES.N)
        fun sendNotification(title: String?, body: String?) {
            try {

                /*  final RemoteViews remoteViews = new RemoteViews(MyApp.getInstance().getPackageName(), R.layout.remote_layout);
            remoteViews.setImageViewResource(R.id.remoteview_notification_icon, R.mipmap.ic_launcher);
            remoteViews.setTextViewText(R.id.remoteview_notification_headline, pushDataBean.getTitle());
            remoteViews.setTextViewText(R.id.remoteview_notification_short_message, pushDataBean.getMessage());
          */


                if (title != null) {
                    if (title .contains("Login OTP :") ) {
                        val notificationManager = MainApplication.getApplicationContext()
                            .getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                        val notificationId = 1
                        val channelId = "Provider"
                        val channelName = "Test"
                        val importance = NotificationManager.IMPORTANCE_HIGH
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            val mChannel = NotificationChannel(
                                channelId, channelName, importance
                            )
                            notificationManager.createNotificationChannel(mChannel)
                        }
                        val time = System.currentTimeMillis()
                        val mNotificationManager = MainApplication.getApplicationContext()
                            .getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                        var contentIntent: PendingIntent? = null
                        val notificationIntent = Intent(
                            MainApplication.getApplicationContext(),
                            SplashActivity::class.java
                        )
                        notificationIntent.flags =
                            Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        contentIntent = PendingIntent.getActivity(
                            MainApplication.getApplicationContext(),
                            time.toInt(), notificationIntent, PendingIntent.FLAG_IMMUTABLE
                        )
                        val defaultSoundUri =
                            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                        val notificationBuilder = NotificationCompat.Builder(
                            MainApplication.getApplicationContext(), channelId
                        )
                            .setSmallIcon(notificationIconNew)
                            .setLargeIcon(
                                BitmapFactory.decodeResource(
                                    MainApplication.getApplicationContext().resources,
                                    R.mipmap.ic_launcher
                                )
                            )
                            .setContentTitle(title)
                            .setContentText(body)
                            .setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setColor(Color.BLACK)
                            .setSound(defaultSoundUri)
                            .setContentIntent(contentIntent)
                        val notification =
                            notificationBuilder.setStyle(
                                NotificationCompat.BigTextStyle().bigText(body)
                            )
                                .build()
                        //  notification.bigContentView = remoteViews;
                        notification.flags = Notification.FLAG_AUTO_CANCEL
                        notification.defaults = Notification.DEFAULT_ALL
                        val random = Random()
                        val m = random.nextInt(9999 - 1000) + 1000
                      //  mNotificationManager.notify(m, notification)
                    }
                }

                /* NotificationTarget notificationTarget = new NotificationTarget(
                    MainApplication.getInstance().getApplicationContext(),
                    R.id.remoteview_notification_icon,
                    remoteViews,
                    notification,
                    m);
            Glide
                    .with(MainApplication.getInstance().getApplicationContext())
                    .asBitmap()
                    .load(pushDataBean.getUrl())
                    .into(notificationTarget);*/
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private val notificationIconNew: Int
            get() {
                val useWhiteIcon = true
                return if (useWhiteIcon) R.mipmap.ic_launcher else R.mipmap.ic_launcher
            }
    }
}