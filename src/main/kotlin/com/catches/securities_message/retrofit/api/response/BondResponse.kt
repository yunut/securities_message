package com.catches.securities_message.retrofit.api.response

import kotlinx.serialization.Serializable

@Serializable
data class BondResponseBody(
    val meta: MetaBody,
    val data: BondDetailData? = null,
)

@Serializable
data class MetaBody(
    val code: Int,
    val message: String? = "",
)

@Serializable
data class BondDetailData(
    val bondName: String,
    val surfaceInterestRate: Double,
    val issuerName: String,
    val issueDate: String,
    val expiredDate: String,
    val interestChange: String,
    val interestType: String,
    val price: Int,
    val priceDate: String,
)