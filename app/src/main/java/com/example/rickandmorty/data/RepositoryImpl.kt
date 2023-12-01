package com.example.rickandmorty.data

import androidx.annotation.VisibleForTesting
import com.example.rickandmorty.data.database.ResultDao
import com.example.rickandmorty.data.network.RickAndMortyApi
import com.example.rickandmorty.models.Characters
import com.example.rickandmorty.models.Locations
import com.example.rickandmorty.models.Result
import com.example.rickandmorty.views.logError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import retrofit2.Response

class RepositoryImpl(
    private val api: RickAndMortyApi,
    private val resultDao: ResultDao,
) :
    Repository {
    private var currentPaginatedPage = 1
    private val mutex = Mutex()
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override suspend fun getCharactersWithName(characterQueryName: String): Flow<ResultResponse<Result>> =
        getCharacterWithQueryNameFromNetwork(characterQueryName).shareIn(
            scope = coroutineScope,
            replay = 1,
            started = SharingStarted.WhileSubscribed()
        )

    private suspend fun getLocalCharacterResults(characterQueryName: String): ResultResponse<Result> =
        ResultResponse.SuccessList(resultDao.getCharacters("%$characterQueryName%"))

    private suspend fun getCharacterWithQueryNameFromNetwork(characterQueryName: String): Flow<ResultResponse<Result>> =
        flow {
            try {
                val response =
                    api.getCharactersWithQueryName(currentPaginatedPage++, characterQueryName)
                when (val networkResponse = getResultResponse(response)) {
                    is ResultResponse.Error -> emit(getLocalCharacterResults(characterQueryName))
                    else -> emit(networkResponse)
                }
            } catch (e: Exception) {
                logError(e.message)
                emit(getLocalCharacterResults(characterQueryName))
            }
        }.flowOn(Dispatchers.IO)

    override suspend fun getCharacters(): Flow<ResultResponse<Result>> = flow {
        try {
            when (val netWorkResponse = getCharactersFromNetwork()) {
                is ResultResponse.SuccessList -> emit(netWorkResponse)
                else -> emit(getCharactersFromLocal())
            }
        } catch (e: Exception) {
            logError(e.message)
            emit(getCharactersFromLocal())
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getCharactersFromNetwork(): ResultResponse<Result> {
        var result: ResultResponse<Result>
        withContext(Dispatchers.IO) {
            result = try {
                val networkResponse = api.getCharacters(currentPaginatedPage++)
                getResultResponse(networkResponse)
            } catch (e: Exception) {
                logError(e.message)
                ResultResponse.Error(e.message)
            }
        }
        return result
    }

    private suspend fun getCharactersFromLocal(): ResultResponse<Result> {
        return ResultResponse.SuccessList(resultDao.getCharacters())
    }

    @VisibleForTesting
    fun getResultResponse(response: Response<Characters>): ResultResponse<Result> {
        return when (response.isSuccessful) {
            true -> {
                response.body()?.let {
                    insertResultsIntoDatabase(it.results)
                    ResultResponse.SuccessList(it.results)
                } ?: ResultResponse.SuccessList(emptyList())
            }

            else -> {
                val errorBody = response.errorBody()
                ResultResponse.Error(errorBody?.toString())
            }
        }
    }

    @VisibleForTesting
    fun insertResultsIntoDatabase(response: List<Result>) {
        coroutineScope.launch {
            mutex.withLock {
                resultDao.addCharacterResult(response)
            }
        }
    }

    override suspend fun getCharactersLocations(id: Int): Response<Locations> {
        return api.getCharactersLocations(id)
    }

    override fun resetPage() {
        currentPaginatedPage = 1
    }
}