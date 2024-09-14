package com.judahben149.fourthwall.utils

import android.util.Base64
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
    private const val SALT = "FourthWallSaltPP"
    private const val IV = "FourthWallVector"

    fun encrypt(password: String, secretKey: String): String {
        val cipher = Cipher.getInstance(ALGORITHM)
        val iv = IvParameterSpec(IV.toByteArray())

        val secretKeySpec = generateSecretKeySpec(secretKey)
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv)

        val encryptedBytes = cipher.doFinal(password.toByteArray())
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    fun decrypt(encryptedPassword: String, secretKey: String): String {
        val cipher = Cipher.getInstance(ALGORITHM)
        val iv = IvParameterSpec(IV.toByteArray())

        val secretKeySpec = generateSecretKeySpec(secretKey)
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv)

        val encryptedBytes = Base64.decode(encryptedPassword, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes)
    }

    private fun generateSecretKeySpec(secretKey: String): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance(KEY_ALGORITHM)
        val spec = PBEKeySpec(secretKey.toCharArray(), SALT.toByteArray(), ITERATIONS, KEY_LENGTH)

        val tmp = factory.generateSecret(spec)
        return SecretKeySpec(tmp.encoded, "AES")
    }
}