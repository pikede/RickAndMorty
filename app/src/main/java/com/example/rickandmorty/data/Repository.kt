package com.example.rickandmorty.data

import com.example.rickandmorty.data.network.RickAndMortyApi
import com.example.rickandmorty.models.Characters
import com.example.rickandmorty.models.Locations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class Repository(private val api: RickAndMortyApi) {
    private var currentPaginatedPage = 1

    suspend fun getCharacterWithQueryNameResponse(characterQueryName: String): Flow<Response<Characters>> =
        flow {
            emit(api.getCharactersWithQueryName(currentPaginatedPage++, characterQueryName))
        }.flowOn(Dispatchers.IO)

    suspend fun getCharacters(): Flow<Response<Characters>> = flow {
        emit(api.getCharacters(currentPaginatedPage++))
    }.flowOn(Dispatchers.IO)

    suspend fun getCharactersWithQueryName(page: Int, name: String): Response<Characters> {
        return api.getCharactersWithQueryName(page, name)
    }

    suspend fun getCharactersLocations(id: Int): Response<Locations> {
        return api.getCharactersLocations(id)
    }

    fun resetPage() {
        currentPaginatedPage = 1
    }
}