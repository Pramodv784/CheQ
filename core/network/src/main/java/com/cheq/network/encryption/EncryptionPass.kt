package com.cheq.network.encryption

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Random

/**
 * Created by Akash Khatkale on 1st June, 2023.
 * akash.k@cheq.one
 */
object EncryptionPass {
    fun sha256(base: String): String? {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(base.toByteArray(charset("UTF-8")))
            val hexString = java.lang.StringBuilder()
            for (i in hash.indices) {
                val hex = Integer.toHexString(0xff and hash[i].toInt())
                if (hex.length == 1) hexString.append('0')
                hexString.append(hex)
            }
            hexString.toString()
        } catch (ex: java.lang.Exception) {
            throw RuntimeException(ex)
        }
    }

    fun randomHash(length: Int): String {
        var alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        val random: Random = SecureRandom()
        val result = CharArray(length)
        for (i in result.indices) {
            // picks a random index out of character set > random character
            val randomCharIndex = random.nextInt(alphabet.length)
            result[i] = alphabet.get(randomCharIndex)
        }
        return String(result)
    }
}