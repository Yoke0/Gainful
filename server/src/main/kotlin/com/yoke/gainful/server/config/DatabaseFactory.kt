package com.yoke.gainful.server.config

import com.yoke.gainful.server.db.Transactions
import com.yoke.gainful.server.db.UserSessions
import com.yoke.gainful.server.db.Users
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils
import java.io.File

object DatabaseFactory {
    fun init(config: DatabaseConfig) {
        if (config.url.startsWith("jdbc:h2:file:")) {
            val path = config.url.substringAfter("jdbc:h2:file:").substringBefore(";")
            File(path).parentFile?.mkdirs()
        }

        Database.connect(
            url = config.url,
            driver = config.driver,
            user = config.user,
            password = config.password,
        )

        transaction {
            MigrationUtils.statementsRequiredForDatabaseMigration(Users, Transactions, UserSessions, withLogs = true)
        }
    }
}
