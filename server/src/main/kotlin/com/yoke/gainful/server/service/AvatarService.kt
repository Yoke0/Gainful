package com.yoke.gainful.server.service

import com.yoke.gainful.server.config.UploadConfig
import com.yoke.gainful.server.db.Users
import com.yoke.gainful.server.plugins.PayloadTooLargeException
import com.yoke.gainful.server.plugins.UnsupportedMediaTypeException
import io.ktor.http.content.MultiPartData
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import java.io.File
import kotlin.uuid.Uuid

class AvatarService(private val uploadConfig: UploadConfig) {
    companion object {
        private val ALLOWED_TYPES = setOf("image/jpeg", "image/png", "image/webp")
        private val EXTENSION_MAP =
            mapOf(
                "image/jpeg" to "jpg",
                "image/png" to "png",
                "image/webp" to "webp",
            )
    }

    suspend fun uploadAvatar(userId: Uuid, multipart: MultiPartData): String {
        var avatarUrl: String? = null

        multipart.forEachPart { part ->
            if (part is PartData.FileItem) {
                val contentType = part.contentType?.toString() ?: ""
                if (contentType !in ALLOWED_TYPES) {
                    part.dispose()
                    throw UnsupportedMediaTypeException("Only JPEG, PNG, and WEBP images are allowed")
                }

                val extension = EXTENSION_MAP[contentType] ?: "jpg"
                val fileName = "${Uuid.random()}.$extension"
                val uploadDir = File(uploadConfig.dir)
                uploadDir.mkdirs()

                val file = File(uploadDir, fileName)
                val bytes = part.streamProvider().readBytes()

                if (bytes.size > uploadConfig.maxFileSizeBytes) {
                    part.dispose()
                    file.delete()
                    throw PayloadTooLargeException("File size exceeds maximum of ${uploadConfig.maxFileSizeBytes / 1024 / 1024}MB")
                }

                file.writeBytes(bytes)
                avatarUrl = "/avatars/$fileName"

                val oldAvatarUrl: String? =
                    transaction {
                        Users.selectAll().where { Users.id eq userId }.singleOrNull()?.get(Users.avatarUrl)
                    }
                if (oldAvatarUrl != null) {
                    val oldFile = File(uploadConfig.dir, oldAvatarUrl.substringAfterLast("/"))
                    if (oldFile.exists()) oldFile.delete()
                }

                transaction {
                    Users.update({ Users.id eq userId }) {
                        it[Users.avatarUrl] = avatarUrl
                    }
                }
            }
            part.dispose()
        }

        return avatarUrl ?: throw UnsupportedMediaTypeException("No valid image file provided")
    }
}
