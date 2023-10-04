package com.example.rickandmorty.dependencyInjection

import com.example.rickandmorty.views.characters.CharacterViewModel
import com.example.rickandmorty.network.RickAndMortyService
import com.example.rickandmorty.views.location.LocationsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val AppModules = module {
    factory { RickAndMortyService }
    viewModel { CharacterViewModel(get()) }
    viewModel { LocationsViewModel(get()) }
}