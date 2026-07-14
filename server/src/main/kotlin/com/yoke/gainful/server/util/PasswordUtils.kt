package com.yoke.gainful.server.util

import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordUtils {
    private const val ITERATIONS = 100_000
    private const val KEY_LENGTH = 256
    private const val SALT_LENGTH = 16
    private const val DELIMITER = ":"

    fun hashPassword(password: String): String {
        val salt = ByteArray(SALT_LENGTH)
        SecureRandom().nextBytes(salt)

        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val hash = factory.generateSecret(spec).encoded

        return "$ITERATIONS" +
            DELIMITER + Base64.getEncoder().encodeToString(salt) +
            DELIMITER + Base64.getEncoder().encodeToString(hash)
    }

    fun verifyPassword(password: String, stored: String): Boolean {
        val parts = stored.split(DELIMITER)
        if (parts.size != 3) return false

        val iterations = parts[0].toIntOrNull() ?: return false
        val salt = Base64.getDecoder().decode(parts[1])
        val expectedHash = Base64.getDecoder().decode(parts[2])

        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, expectedHash.size * 8)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val actualHash = factory.generateSecret(spec).encoded

        return actualHash.contentEquals(expectedHash)
    }
}
