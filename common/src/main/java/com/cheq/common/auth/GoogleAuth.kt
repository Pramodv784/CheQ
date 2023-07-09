package com.cheq.common.auth

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher

/**
 * Created by Akash Khatkale on 24th May, 2023.
 * akash.k@cheq.one
 */
interface GoogleAuth {

    companion object {
        const val USER_CANCEL = 12501
        const val FAILED = 12500
    }

    fun init(context: Context)

    fun signIn(launcher: ActivityResultLauncher<Intent>)

    fun signOut()

}