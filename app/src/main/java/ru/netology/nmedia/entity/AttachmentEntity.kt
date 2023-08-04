package ru.netology.nmedia.entity

import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.enumeration.AttachmentType


data class AttachmentEntity(
    @PrimaryKey(autoGenerate = true)
    val url: String,
    val type: AttachmentType,
    val description: String? = null
) {
    fun toDto() = Attachment(
        url,
        type,
        description,
    )

    companion object {
        fun fromDto(dto: Attachment?) =

            dto?.let {
                AttachmentEntity(
                    dto.url,
                    dto.type,
                    dto.description
                )
            }
    }
}


