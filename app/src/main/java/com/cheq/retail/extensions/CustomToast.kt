package com.cheq.retail.extensions

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.cheq.retail.R
import com.cheq.retail.constants.AFConstants
import com.cheq.retail.constants.FirebaseLog

fun Activity.customToast(imageIcon: Int, message: String) {
    FirebaseLog.FireBaseLogEvent(
        this,
        AFConstants.FBEvent_USER_WANT_TO_CONVERT_TO_CASH,
        AFConstants.FBEvent_CONVERT_TO_CASH_ERROR,
        AFConstants.FBEvent_CUST_C2C_ERROR
    )
    val li: LayoutInflater = this.getLayoutInflater()
    //Getting the View object as defined in the customtoast.xml file
    //Getting the View object as defined in the customtoast.xml file
    val layout: View = li.inflate(
        R.layout.custom_toast_c2c,
        this.findViewById(R.id.toast_layout_root) as ViewGroup?
    )
    val toast = Toast(this)
    toast.setGravity(Gravity.BOTTOM or Gravity.HORIZONTAL_GRAVITY_MASK, 0, 100)
    toast.setMargin(0.8f, 0f)
    toast.view = layout //setting the view of custom toast layout
    val image = layout.findViewById<ImageView>(R.id.ivRewardsEarned)
    image.setImageResource(imageIcon)
    val txtMessage = layout.findViewById<TextView>(R.id.txtMessage)
    txtMessage.setText(message)
    toast.duration=Toast.LENGTH_LONG
    toast.show()
}

