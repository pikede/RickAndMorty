package com.example.rickandmorty.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RickAndMortyService {
    private const val BASE_URL = "https://rickandmortyapi.com/api/"
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getPlayerApi(): RickAndMortyApi = retrofit.create(RickAndMortyApi::class.java)
}