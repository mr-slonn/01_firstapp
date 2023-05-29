package ru.netology.nmedia.entity

import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Attachment


data class AttachmentEntity(
    @PrimaryKey(autoGenerate = true)
    val url: String,
    val type: String,
    val description: String
) {
    fun toDto() = Attachment(
        url,
        type,
        description,
    )

    companion object {
        fun fromDto(dto: Attachment) =
            AttachmentEntity(
                dto.url,
                dto.type,
                dto.description
            )
    }
}
