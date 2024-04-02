package com.vitekkor.memeDB.model

sealed interface Attachment {
    fun isEmpty(): Boolean
}

data class TelegramAttachment(
    val photos: List<String>?
) : Attachment {
    override fun isEmpty(): Boolean {
        return photos.isNullOrEmpty() == null
    }
}

fun Attachment?.isNullOrEmpty() = this == null || isEmpty()