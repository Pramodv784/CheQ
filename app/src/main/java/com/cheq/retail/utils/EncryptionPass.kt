package com.cheq.retail.utils

import KeystoreEncryption
import android.util.Log
import com.cheq.retail.BuildConfig
import com.cheq.retail.application.MainApplication
import com.cheq.retail.constants.AFConstants
import com.cheq.retail.constants.Constant
import com.cheq.retail.constants.FirebaseLog
import com.cheq.retail.sharePreferences.SharePrefs
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception
import java.security.GeneralSecurityException

object EncryptionPass {
    var key:String? = null

     fun getDecryptedText() : String? {
         val encryptor = SharePrefs.getInstance(MainApplication.getApplicationContext())?.getString(KeystoreEncryption.ENCRYPTOR)
             ?.let { Base64.decode(it,Base64.DEFAULT) }
         val iv =  SharePrefs.getInstance(MainApplication.getApplicationContext())?.getString(KeystoreEncryption.IV)
             ?.let { Base64.decode(it,Base64.DEFAULT)}
         try {
             when {
                 key?.isNotEmpty() == true -> {
                     return key
                 }
                 encryptor?.isNotEmpty() ==true && iv?.isNotEmpty() ==true -> {
                     key = KeystoreDecryption().decryptData(
                         "CHEQ_ALIAS",
                         encryptor,
                         iv)
                 }
                 else -> {
                     initEncryptPass(encryptor,iv)
                 }
             }
          } catch (e: Exception) { }
        return key
    }
     fun initEncryptPass(encryptor: ByteArray?, iv: ByteArray?) {
        var mFirebasedatabase = FirebaseDatabase.getInstance(BuildConfig.firebaseDataBaseURL)
        var encryptionDb = mFirebasedatabase.getReference(Constant.ENCRYPT_KEY)
        encryptionDb.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.value?.let {
                    key = it.toString()

                    FirebaseLog.FireBaseLogEvent(
                        MainApplication.getApplicationContext(),
                        "Firebase_encryption_pass_initialised_post_launch",
                        "Firebase_fetched_second_time",
                        "Firebase_encryption_pass_initialised_post_launch"
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                FirebaseLog.FireBaseLogEvent(
                    MainApplication.getApplicationContext(),
                    AFConstants.EncryptionPassApplication,
                    AFConstants.EncryptionPassApplication,
                    error.details
                )
            }

        })
    }
}