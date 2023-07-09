package com.cheq.encryption

import com.cheq.cache.sharedprefs.SharedPrefs
import com.cheq.cache.sharedprefs.SharedPrefsCheQ
import com.cheq.cache.sharedprefs.SharedPrefsConstants.ENCRYPT_KEY
import com.cheq.config.AppConfig
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.security.GeneralSecurityException
import javax.inject.Inject

class EncryptionPassImpl @Inject constructor(
    @SharedPrefsCheQ private val sharedPrefs: SharedPrefs,
    private val appConfig: AppConfig
) : EncryptionPass {
    override var key: String? = null

    override fun getDecryptedText(): String? {
        val encryptor = sharedPrefs.getString(ENCRYPTOR).let { Base64.decode(it, Base64.DEFAULT) }
        val iv = sharedPrefs.getString(IV).let { Base64.decode(it, Base64.DEFAULT) }
        try {
            when {
                key?.isNotEmpty() == true -> {
                    return key
                }

                encryptor?.isNotEmpty() == true && iv?.isNotEmpty() == true -> {
                    key = KeystoreDecryption().decryptData(
                        "CHEQ_ALIAS",
                        encryptor,
                        iv
                    )
                }

                else -> {
                    initEncryptPass(encryptor, iv)
                }
            }
        } catch (e: GeneralSecurityException) {
        }
        return key
    }

    fun initEncryptPass(encryptor: ByteArray?, iv: ByteArray?) {
        var mFirebasedatabase = FirebaseDatabase.getInstance(appConfig.firebaseDatabaseUrl())
        var encryptionDb = mFirebasedatabase.getReference(ENCRYPT_KEY)
        encryptionDb.keepSynced(true)
        encryptionDb.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.value?.let {
                    key = it.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}