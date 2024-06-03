package com.example.walletease.api

import com.example.walletease.components.CurrencyConverterComponent.dataclasses.CurrencyConvertResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiClient {
    @GET("latest")
    suspend fun getLatestRates(
        @Query("access_key") apiKey: String,
        @Query("base") base: String
    ): CurrencyConvertResponse

    companion object {
        private const val BASE_URL = "https://api.fxratesapi.com/"

        fun create(): ApiClient {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiClient::class.java)
        }
    }
}