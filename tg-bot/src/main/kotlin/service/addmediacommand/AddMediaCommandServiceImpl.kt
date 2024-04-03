package com.vitekkor.memeDB.service.addmediacommand

import com.vitekkor.memeDB.model.Media
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
    private val ktorClient: HttpClient
) : AddMediaCommandService {
    private val log = logger {}

    override fun addMedia(meduiaData: Media) = runBlocking {
        val response: HttpResponse = withContext(Dispatchers.IO) {
            ktorClient.request("https://ktor.io/")
        }

//        val response: HttpResponse = withContext(Dispatchers.IO) {
//            ktorClient.post("https://ktor.io/") {
//                contentType(ContentType.Application.Json)
//                body = meduiaData
//            }
//        }
        log.info { response.status }

        return@runBlocking
    }
}