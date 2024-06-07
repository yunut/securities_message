package com.catches.securities_message.retrofit.api.response

import kotlinx.serialization.Serializable

@Serializable
data class BondResponseBody<T>(
    val meta: MetaBody,
    val data: T? = null,
)

@Serializable
data class MetaBody(
    val code: Int,
    val message: String? = "",
)

@Serializable
data class BondListData(
    val grade: String,
    val bondList: List<BondListResponseBody>,
)

@Serializable
data class BondListResponseBody(
    val name: String,
    val surfaceInterestRate: Double,
    val expiredDate: String,
    val grade: String,
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