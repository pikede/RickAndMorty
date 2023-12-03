package com.example.rickandmorty.dependencyInjection

import com.example.rickandmorty.data.Repository
import com.example.rickandmorty.data.RepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<Repository> { RepositoryImpl(get(), get()) }
}