package com.cheq.retail.newarch.firebase

import com.cheq.common.firebase.CheqFirebase
import com.cheq.retail.BuildConfig
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Created by Akash Khatkale on 26th May, 2023.
 * akash.k@cheq.one
 */
class CheqFirebaseImpl @Inject constructor(): CheqFirebase {

    private var mFirebasedatabase: FirebaseDatabase? = null

    init {
        mFirebasedatabase = FirebaseDatabase.getInstance(BuildConfig.firebaseDataBaseURL)
    }

    override suspend fun <T> getValue(
        reference: String,
        classOf: Class<T>
    ): Result<T?> {
        return try {
            val response = mFirebasedatabase?.getReference(reference)?.get()?.await()
            Result.success(response?.getValue(classOf))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}