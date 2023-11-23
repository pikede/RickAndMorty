package com.example.rickandmorty.dependencyInjection

import com.example.rickandmorty.data.Repository
import org.koin.dsl.module

val repositoryModule = module {
    single { Repository(get()) }
}