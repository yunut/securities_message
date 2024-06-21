package com.catches.securities_message.retrofit.api.request

import kotlinx.serialization.Serializable

@Serializable
data class UserBondCreateRequest(
    val userId: String,
    val bondId: String,
)