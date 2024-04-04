package com.vitekkor.memeDB.service.addmediacommand

import com.vitekkor.memeDB.model.FileData
import com.vitekkor.memeDB.model.Media

interface AddMediaCommandService {

    fun addMedia(mediaData: Media, file: ByteArray)

    fun addFileBytes(fileData: FileData, fileBytes: ByteArray)
}