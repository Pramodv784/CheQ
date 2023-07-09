package com.cheq.encryption

interface EncryptionPass {
    var key: String?
    fun getDecryptedText(): String?
}