package com.cheq.retail.utils

import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.ContextCompat
import com.cheq.retail.application.MainApplication
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.ui.main.model.SmsPostModel
import java.util.*

class SmsUtils {
    companion object {

        fun getLatestDeviceSms(): MutableList<SmsPostModel.SmsData> {
            val lstSms: MutableList<SmsPostModel.SmsData> = ArrayList()

            if (ContextCompat.checkSelfPermission(
                    MainApplication.getApplicationContext(),
                    "android.permission.READ_SMS"
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val cursor = MainApplication.getApplicationContext().contentResolver.query(
                    Uri.parse("content://sms/inbox"),
                    null,
                    "date" + ">?",
                    arrayOf("" + Date(System.currentTimeMillis() - 30L * 24 * 3600 * 1000).time),
                    "date asc"
                )

                while (cursor!!.moveToNext()) {
                    val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                    val body = cursor.getString(cursor.getColumnIndexOrThrow("body"))
                    val senderId = cursor.getString(cursor.getColumnIndexOrThrow("ADDRESS"))

                    when {
                        SharePrefs.getInstance(MainApplication.getApplicationContext())!!
                            .getString(SharePrefsKeys.LAST_SYNC_TIME).isNullOrEmpty() -> {
                            SharePrefs.getInstance(MainApplication.getApplicationContext())!!
                                .putString(SharePrefsKeys.LAST_SYNC_TIME, date)
                        }
                    }

                    if (!date.isNullOrEmpty() && !SharePrefs.getInstance(MainApplication.getApplicationContext())?.getString(SharePrefsKeys.LAST_SYNC_TIME).isNullOrEmpty()
                    ) {

                        if (!SharePrefs.getInstance(MainApplication.getApplicationContext())!!
                                .getString(SharePrefsKeys.LAST_SYNC_TIME).equals("null")
                        ) {
                            if (date.toLong() > SharePrefs.getInstance(MainApplication.getApplicationContext())!!
                                    .getString(SharePrefsKeys.LAST_SYNC_TIME)!!.toLong()
                            ) {
                                if (body.contains("Statement") || body.contains("statement") || body.contains(
                                        "E-Statement"
                                    ) || body.contains("E-stmt") || body.contains("Stmt") || body.contains(
                                        "statement".lowercase()
                                    )
                                ) {
                                    lstSms.add(SmsPostModel.SmsData(date, body, senderId))
                                }
                            }
                        }


                    }
                }
            }

            return lstSms
        }
    }
}