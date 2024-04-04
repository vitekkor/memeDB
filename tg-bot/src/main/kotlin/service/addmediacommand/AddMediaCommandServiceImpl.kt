package com.vitekkor.memeDB.service.addmediacommand

import com.vitekkor.memeDB.config.properties.SearchEngineConfigurationProperties
import com.vitekkor.memeDB.misc.FileIdRepository
import com.vitekkor.memeDB.model.FileData
import com.vitekkor.memeDB.model.FileId
import com.vitekkor.memeDB.model.Media
import com.vitekkor.memeDB.repository.FileDataRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging.logger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class AddMediaCommandServiceImpl(
    @Qualifier("ktorClient")
    private val ktorClient: HttpClient,
    private val fileDataRepository: FileDataRepository,
    searchEngineConfigurationProperties: SearchEngineConfigurationProperties,
    private val fileIdRepository: FileIdRepository,
) : AddMediaCommandService {
    private val log = logger {}
    private val url = searchEngineConfigurationProperties.url.removeSuffix("/")

    override fun addMedia(mediaData: Media, file: ByteArray) = runBlocking {
        val id: String = ktorClient.request<String>("$url/image") {
            parameter("description", mediaData.description)
            method = HttpMethod.Post
            body = MultiPartFormDataContent(
                formData {
                    append(
                        "file",
                        file,
                        Headers.build {
                            append(HttpHeaders.ContentType, "images/*")
                            append(HttpHeaders.ContentDisposition, "filename=${mediaData.fileId}")
                        }
                    )
                }
            )
        }
        log.info { "Saved id $id" }
        fileIdRepository.save(FileId(mediaData.fileId, id, "image"))
        return@runBlocking id
    }

    override fun addFileBytes(fileData: FileData, fileBytes: ByteArray) = runBlocking(Dispatchers.IO) {
//        val id: String = ktorClient.request<String>("$url/create_caption") {
//            method = HttpMethod.Post
//            body = MultiPartFormDataContent(
//                formData {
//                    append(
//                        "file",
//                        fileBytes,
//                        Headers.build {
//                            append(HttpHeaders.ContentType, "images/*")
//                            append(HttpHeaders.ContentDisposition, "filename=${fileData.fileId}")
//                        }
//                    )
//                }
//            )
//        }
        val id = "1234567890"
        val newFileData = FileData(
            id = id,
            chatId = fileData.chatId,
            messageId = fileData.messageId,
            fileId = fileData.fileId
        )

        fileDataRepository.save(newFileData)

        log.info { id }

        return@runBlocking
    }
}