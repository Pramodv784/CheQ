package com.cheq.retail.utils

import com.cheq.retail.ui.main.model.SelectOfferResponseItem
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

object InitFirebase {
    private var db = Firebase.database.reference
    private const val TABLE_NAME = "instrument_master"
    private var playerArray: Array<SelectOfferResponseItem>? = null
    fun getRewardRateList(itemList: ((Array<SelectOfferResponseItem>?) -> Unit)) {
        db
            .child(TABLE_NAME)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    playerArray =  Gson().fromJson(
                        it.result.value.toString(), Array<SelectOfferResponseItem>::class.java
                    )
                }

                itemList(playerArray)
            }
    }
}