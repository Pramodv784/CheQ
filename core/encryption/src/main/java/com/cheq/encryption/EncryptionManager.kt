package com.cheq.encryption

interface EncryptionManager {
    fun encrypt(text: String): ByteArray
    fun decrypt(): String?
}