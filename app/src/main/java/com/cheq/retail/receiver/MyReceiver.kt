package com.cheq.retail.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.cheq.retail.utils.Utils.Companion.getConnectivityStatusString

class MyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        var status = getConnectivityStatusString(context)

        if (status != null) {
            if (status.isEmpty() || status == "No internet is available" || status == "No Internet Connection") {
                status = "No Internet Connection"
            }else if (status.isEmpty() ||status=="Wifi enabled"){
                status = "Internet Connection Stabled"
            }
        }
        Toast.makeText(context, status, Toast.LENGTH_SHORT).show()

    }
}