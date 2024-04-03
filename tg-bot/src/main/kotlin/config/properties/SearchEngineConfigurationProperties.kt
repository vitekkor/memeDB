package com.vitekkor.memeDB.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "search-engine")
data class SearchEngineConfigurationProperties(
    val url: String
)
