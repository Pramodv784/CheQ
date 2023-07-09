package com.cheq.cache.sharedprefs


/**
 * Created by Akash Khatkale on 16th May, 2023.
 * akash.k@cheq.one
 */
interface SharedPrefs {
    fun getString(key: String, defaultValue: String = ""): String
    fun putString(key: String, value: String)
    fun putBoolean(key: String, value: Boolean)
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean
    fun clearPrefs()
}