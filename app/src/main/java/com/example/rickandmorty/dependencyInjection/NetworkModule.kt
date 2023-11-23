package com.example.rickandmorty.dependencyInjection

import com.example.rickandmorty.data.network.RickAndMortyApi
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://rickandmortyapi.com/api/"

val networkModule = module {
    factory {
        provideRetrofit()
    }
    single {
        provideRickAndMortyApi(get())
    }
}

fun provideRetrofit(): Retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

fun provideRickAndMortyApi(retrofit: Retrofit): RickAndMortyApi =
    retrofit.create(RickAndMortyApi::class.java)