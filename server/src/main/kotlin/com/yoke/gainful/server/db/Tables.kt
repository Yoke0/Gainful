package com.yoke.gainful.server.db

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.date
import org.jetbrains.exposed.v1.datetime.datetime

object Users : Table("users") {
    val id = uuid("id").autoGenerate()
    val username = varchar("username", 50).uniqueIndex()
    val email = varchar("email", 100).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val nickname = varchar("nickname", 50).nullable()
    val avatarUrl = varchar("avatar_url", 500).nullable()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(id)
}

object Transactions : Table("transactions") {
    val id = uuid("id").autoGenerate()
    val userId = reference("user_id", Users.id, onDelete = ReferenceOption.CASCADE)
    val assetCode = varchar("asset_code", 20)
    val assetName = varchar("asset_name", 100).nullable()
    val type = integer("type")
    val quantity = double("quantity")
    val price = double("price")
    val amount = double("amount")
    val tradeDate = date("trade_date")
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
    val deletedAt = datetime("deleted_at").nullable()

    override val primaryKey = PrimaryKey(id)

    init {
        index(false, userId)
        index(false, tradeDate)
    }
}

object UserSessions : Table("user_sessions") {
    val id = uuid("id").autoGenerate()
    val userId = reference("user_id", Users.id, onDelete = ReferenceOption.CASCADE)
    val deviceInfo = varchar("device_info", 500).nullable()
    val ipAddress = varchar("ip_address", 45).nullable()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val expiresAt = datetime("expires_at")
    val isRevoked = bool("is_revoked").default(false)
    val refreshToken = varchar("refresh_token", 500).nullable()
    val refreshTokenExpiresAt = datetime("refresh_token_expires_at").nullable()

    override val primaryKey = PrimaryKey(id)

    init {
        index(false, userId)
    }
}
