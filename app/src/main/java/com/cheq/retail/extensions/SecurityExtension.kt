package com.cheq.retail.extensions

import android.app.Activity
import com.scottyab.rootbeer.RootBeer
import kotlin.system.exitProcess

/*
* Check if the device is rooted, exit from the application
*/
fun Activity.checkDeviceRoot(){
    val rootBeer = RootBeer(this)
    if (rootBeer.isRooted) {
      closeApp()
    }
}

fun Activity.closeApp(){
      moveTaskToBack(true);
      exitProcess(-1)
}

