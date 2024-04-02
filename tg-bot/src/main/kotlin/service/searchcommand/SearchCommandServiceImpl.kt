package com.vitekkor.memeDB.service.searchcommand

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import mu.KotlinLogging.logger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.stereotype.Service

@Service
@EnableScheduling
class SearchCommandServiceImpl(
    @Qualifier("ktorClient")
    private val ktorClient: HttpClient
) : SearchCommandService {
    private val log = logger {}

    override fun findMemes(searchText: String): String = runBlocking {
        val response: HttpResponse = withContext(Dispatchers.IO) {
            ktorClient.request("https://ktor.io/")
        }

//        val response: HttpResponse = withContext(Dispatchers.IO) {
//            ktorClient.post("https://ktor.io/") {
//                contentType(ContentType.Application.Json)
//                body = RequestBody(searchText)
//            }
//        }
        log.info { response.status }

        return@runBlocking "OK"
    }
}