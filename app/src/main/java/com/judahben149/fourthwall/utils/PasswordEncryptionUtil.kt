package com.judahben149.fourthwall.utils

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object PasswordEncryptionUtil {
    private const val ALGORITHM = "AES/CBC/PKCS7Padding"
    private const val KEY_ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val ITERATIONS = 10000
    private const val KEY_LENGTH = 256

    fun encrypt(password: String, secretKey: String): String {
        val salt = generateSalt()
        val iv = generateIv()
        val cipher = Cipher.getInstance(ALGORITHM)
        val secretKeySpec = generateSecretKeySpec(secretKey, salt)
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, IvParameterSpec(iv))
        val encryptedBytes = cipher.doFinal(password.toByteArray())

        // Combine salt, IV, and encrypted data
        val combined = salt + iv + encryptedBytes
        return Base64.encodeToString(combined, Base64.DEFAULT)
    }

    fun decrypt(encryptedPassword: String, secretKey: String): String {
        val combined = Base64.decode(encryptedPassword, Base64.DEFAULT)

        // Extract salt, IV, and encrypted data
        val salt = combined.sliceArray(0 until 16)
        val iv = combined.sliceArray(16 until 32)
        val encryptedBytes = combined.sliceArray(32 until combined.size)

        val cipher = Cipher.getInstance(ALGORITHM)
        val secretKeySpec = generateSecretKeySpec(secretKey, salt)
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, IvParameterSpec(iv))
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes)
    }

    private fun generateSecretKeySpec(secretKey: String, salt: ByteArray): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance(KEY_ALGORITHM)
        val spec = PBEKeySpec(secretKey.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        val tmp = factory.generateSecret(spec)
        return SecretKeySpec(tmp.encoded, "AES")
    }

    private fun generateSalt(): ByteArray {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        return salt
    }

    private fun generateIv(): ByteArray {
        val iv = ByteArray(16)
        SecureRandom().nextBytes(iv)
        return iv
    }

    /**
     * Dummy encryption and decryption simulation methods
     */
    fun String.encrypt(): String {
        return this.padStart(3, '0')
    }

    fun String.decrypt(): String {
        return this.padStart(3, '0')
    }
}