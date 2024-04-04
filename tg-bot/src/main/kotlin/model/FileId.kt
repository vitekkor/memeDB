package com.vitekkor.memeDB.model

import org.springframework.data.annotation.Id

data class FileId(
    val fileId: String,
    @Id
    val id: String,
    val type: String
)
