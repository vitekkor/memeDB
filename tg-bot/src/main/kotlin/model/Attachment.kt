package com.vitekkor.memeDB.model

sealed interface Attachment {
    fun isEmpty(): Boolean
}

data class TelegramAttachment(
    val photoId: String?
) : Attachment {
    override fun isEmpty(): Boolean {
        return photoId.isNullOrEmpty() == null
    }
}

fun Attachment?.isNullOrEmpty() = this == null || isEmpty()