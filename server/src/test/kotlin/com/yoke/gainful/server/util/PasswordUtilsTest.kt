package com.yoke.gainful.server.util

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class PasswordUtilsTest {
    @Test
    fun `hashPassword returns PBKDF2 format`() {
        val hash = PasswordUtils.hashPassword("123456")
        val parts = hash.split(":")
        assertTrue(parts.size == 3)
    }

    @Test
    fun `verifyPassword with correct password returns true`() {
        val hash = PasswordUtils.hashPassword("123456")
        assertTrue(PasswordUtils.verifyPassword("123456", hash))
    }

    @Test
    fun `verifyPassword with wrong password returns false`() {
        val hash = PasswordUtils.hashPassword("123456")
        assertFalse(PasswordUtils.verifyPassword("wrong", hash))
    }

    @Test
    fun `hashPassword produces different hashes for same input`() {
        val hash1 = PasswordUtils.hashPassword("123456")
        val hash2 = PasswordUtils.hashPassword("123456")
        assertNotEquals(hash1, hash2)
    }
}
