package com.vitekkor.memeDB.service.addmediacommand

import com.vitekkor.memeDB.config.properties.SearchEngineConfigurationProperties
import com.vitekkor.memeDB.misc.MediaRepository
import com.vitekkor.memeDB.model.FileData
import com.vitekkor.memeDB.model.Media
import com.vitekkor.memeDB.repository.FileDataRepository
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import mu.KotlinLogging.logger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class AddMediaCommandServiceImpl(
    @Qualifier("ktorClient")
    private val ktorClient: HttpClient,
    private val mediaRepository: MediaRepository,
    private val fileDataRepository: FileDataRepository,
    searchEngineConfigurationProperties: SearchEngineConfigurationProperties,
) : AddMediaCommandService {
    private val log = logger {}
    private val url = searchEngineConfigurationProperties.url.removeSuffix("/")

    override fun addMedia(meduiaData: Media) = runBlocking {
        val response: HttpResponse = withContext(Dispatchers.IO) {
            ktorClient.request("https://ktor.io/")
        }

        mediaRepository.save(meduiaData)

//        val response: HttpResponse = withContext(Dispatchers.IO) {
//            ktorClient.post("https://ktor.io/") {
//                contentType(ContentType.Application.Json)
//                body = meduiaData
//            }
//        }
        log.info { response.status }

        return@runBlocking
    }

    override fun addFileBytes(fileData: FileData, fileBytes: ByteArray) = runBlocking(Dispatchers.IO) {
//        val id: String = ktorClient.request<String>("$url/create_caption/${fileData.fileId}") {
//            method = HttpMethod.Post
//            body = MultiPartFormDataContent(
//                formData {
//                    append(
//                        "fileBytes",
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