package com.example.rickandmorty.network

import com.example.rickandmorty.models.Characters
import com.example.rickandmorty.models.Locations
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RickAndMortyApi {
    @GET("character")
    suspend fun getCharacters() : Response<Characters>

    @GET("location/{id}")
    suspend fun getCharactersLocations(@Path("id") id: Int) : Response<Locations>
}