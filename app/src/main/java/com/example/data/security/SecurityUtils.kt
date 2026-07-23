package com.example.data.security

import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import android.util.Base64

/**
 * Security utilities for client-side AES-256 encryption of evidence files
 * and immutable SHA-256 tamper-evident hashing for court admissibility.
 */
object SecurityUtils {

    private const val AES_ALGORITHM = "AES/CBC/PKCS5Padding"
    private val DEFAULT_KEY_BYTES = "RESQ_SECURE_CLIENT_KEY_32BYTES!!".toByteArray(Charsets.UTF_8)
    private val DEFAULT_IV = "RESQ_IV_16_BYTES!".toByteArray(Charsets.UTF_8)

    /**
     * Calculates the immutable SHA-256 hash of byte data at capture time.
     */
    fun calculateSha256(data: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(data)
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Calculates SHA-256 hash for a given text string.
     */
    fun calculateSha256Text(text: String): String {
        return calculateSha256(text.toByteArray(Charsets.UTF_8))
    }

    /**
     * Encrypts raw data bytes with client-side AES-256.
     */
    fun encryptAes256(data: ByteArray, customKey: ByteArray? = null): ByteArray {
        val keyBytes = customKey ?: DEFAULT_KEY_BYTES
        val secretKey = SecretKeySpec(keyBytes, "AES")
        val cipher = Cipher.getInstance(AES_ALGORITHM)
        val ivSpec = IvParameterSpec(DEFAULT_IV)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
        return cipher.doFinal(data)
    }

    /**
     * Decrypts AES-256 encrypted byte array back to original plaintext.
     */
    fun decryptAes256(encryptedData: ByteArray, customKey: ByteArray? = null): ByteArray {
        val keyBytes = customKey ?: DEFAULT_KEY_BYTES
        val secretKey = SecretKeySpec(keyBytes, "AES")
        val cipher = Cipher.getInstance(AES_ALGORITHM)
        val ivSpec = IvParameterSpec(DEFAULT_IV)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
        return cipher.doFinal(encryptedData)
    }

    /**
     * Generates a display-friendly short hash snippet (e.g., 0x8a1b...c9d4).
     */
    fun formatShortHash(fullHash: String): String {
        if (fullHash.length <= 12) return fullHash
        return "0x${fullHash.take(6)}...${fullHash.takeLast(6)}"
    }
}
