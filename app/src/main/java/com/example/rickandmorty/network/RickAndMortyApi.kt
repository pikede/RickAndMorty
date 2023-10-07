package com.example.rickandmorty.network

import com.example.rickandmorty.models.Characters
import com.example.rickandmorty.models.Locations
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RickAndMortyApi {
    @GET("character")
    suspend fun getCharacters(@Query("page") page: Int): Response<Characters>

    @GET("character")
    suspend fun getCharactersWithQueryName(
        @Query("page") page: Int,
        @Query("name") name: String,
    ): Response<Characters>

    @GET("location/{id}")
    suspend fun getCharactersLocations(@Path("id") id: Int): Response<Locations>
}