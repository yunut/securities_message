package com.catches.securities_message.retrofit.api

import com.catches.securities_message.retrofit.api.request.UserBondCreateRequest
import com.catches.securities_message.retrofit.api.response.BondDetailData
import com.catches.securities_message.retrofit.api.response.BondListData
import com.catches.securities_message.retrofit.api.response.BondResponseBody
import kotlinx.serialization.json.JsonElement
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SecuritiesApiInterface {

    // TODO 채권 등급별 리스트 가져오는 API
    @GET("/bond/list")
    fun getBondList(
    ): Call<BondResponseBody<List<BondListData>>>

    // TODO 채권 상세 정보 가져오는 API
    @GET("/bond")
    fun getBondDetail(
        @Query("name") bondName: String,
    ): Call<BondResponseBody<BondDetailData>>

    // TODO 채권 이름으로 리스트 검색하는 API
    @GET("/bond/search")
    fun searchBondList(
        @Query("name") bondName: String,
    ): Call<BondResponseBody<List<BondDetailData>>>

    // TODO 사용자가 등록한 채권 리스트 가져오는 API

    // TODO 사용자 자신의 채권 정보를 등록하는 API
    @POST("/user/bond/create")
    fun createUserBond(
        @Body userBondCreateRequest: UserBondCreateRequest,
    ): Call<BondResponseBody<JsonElement>>

    // TODO 사용자가 등록한 채권을 삭제하는 API

}