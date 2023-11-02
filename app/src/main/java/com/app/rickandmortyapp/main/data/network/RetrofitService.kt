package com.app.rickandmortyapp.main.data.network

import com.app.rickandmortyapp.main.data.network.response.RetrofitResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {
    @GET("character")
    suspend fun getCharacters(@Query("page") page: Int): RetrofitResponse

    @GET("character")
    suspend fun searchCharacter(@Query("name") name: String?): RetrofitResponse
}