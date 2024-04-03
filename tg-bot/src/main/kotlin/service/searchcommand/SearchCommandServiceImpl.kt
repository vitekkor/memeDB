package com.vitekkor.memeDB.service.searchcommand

import com.vitekkor.memeDB.config.properties.SearchEngineConfigurationProperties
import com.vitekkor.memeDB.model.MemDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.http.HttpMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging.logger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Files

@Service
@EnableScheduling
class SearchCommandServiceImpl(
    @Qualifier("ktorClient")
    private val ktorClient: HttpClient,
    searchEngineConfigurationProperties: SearchEngineConfigurationProperties,
) : SearchCommandService {
    private val log = logger {}
    private val url = searchEngineConfigurationProperties.url.removeSuffix("/")
    private val tmpDir = Files.createTempDirectory("media")

    override fun findMemes(searchText: String): List<Pair<File, String>> = runBlocking(Dispatchers.IO) {
        val memeIds: List<MemDto> = ktorClient.request<List<MemDto>>("$url/image/search") {
            method = HttpMethod.Get
            parameter(DESCRIPTION_PARAMETER, searchText)
        }

        log.info { memeIds }
        return@runBlocking memeIds.map { (id, type) ->
            val file = tmpDir.resolve(id).toFile()
            file.writeBytes(ktorClient.get<ByteArray>("$url/image/$id"))
            file to type
        }
    }

    companion object {
        private const val DESCRIPTION_PARAMETER = "description"
    }
}
