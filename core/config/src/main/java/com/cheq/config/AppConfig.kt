package com.cheq.config

/**
 * Created by Akash Khatkale on 24th May, 2023.
 * akash.k@cheq.one
 */
interface AppConfig {
    fun getVersionName(): String
    fun isDebugApp(): Boolean
    fun apiEndPoint(): String
    fun firebaseDatabaseUrl(): String
    fun getDeviceId(): String
    fun getGoogleRequestIdToken(): String
    fun getGoogleRequestServerAuthCode(): String
}