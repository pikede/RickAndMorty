package com.example.rickandmorty.dependencyInjection

val appModules = listOf(
    networkModule,
    databaseModule,
    daoModules,
    viewModelModules,
    repositoryModule
)