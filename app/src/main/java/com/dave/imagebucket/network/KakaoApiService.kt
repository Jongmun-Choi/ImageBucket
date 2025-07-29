package com.dave.imagebucket.network

import com.dave.imagebucket.BuildConfig
import com.dave.imagebucket.data.model.ImageResponse
import com.dave.imagebucket.data.model.VideoDocument
import com.dave.imagebucket.data.model.VideoResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoApiService {

    @GET("v2/search/image")
    suspend fun searchImages(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("size") size: Int = 15
    ): ImageResponse

    @GET("v2/search/vclip")
    suspend fun searchVideos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("size") size: Int = 15
    ): VideoResponse

}