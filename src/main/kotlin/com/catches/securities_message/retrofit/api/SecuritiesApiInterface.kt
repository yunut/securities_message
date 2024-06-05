package com.catches.securities_message.retrofit.api

import com.catches.securities_message.retrofit.api.response.BondResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SecuritiesApiInterface {

    // TODO 채권 등급별 리스트 가져오는 API

    // TODO 채권 상세 정보 가져오는 API
    @GET("/bond")
    fun getBondDetail(
        @Query("name") bondName: String,
    ): Call<BondResponseBody>

    // TODO 사용자가 등록한 채권 리스트 가져오는 API

    // TODO 사용자 자신의 채권 정보를 등록하는 API

    // TODO 사용자가 등록한 채권을 삭제하는 API

}