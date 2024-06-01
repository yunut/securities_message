package com.catches.securities_message.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "http")
data class HttpProperty(
    val securitiesApi: UrlEntity,
)

data class UrlEntity(
    val scheme: String,
    val host: String,
)