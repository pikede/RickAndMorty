package com.example.rickandmorty.data

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.rickandmorty.data.database.AppDataBase
import com.example.rickandmorty.data.database.ResultDao
import com.example.rickandmorty.data.network.RickAndMortyApi
import com.example.rickandmorty.models.Characters
import com.example.rickandmorty.models.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONArray
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.Response
import java.net.HttpURLConnection

@RunWith(AndroidJUnit4::class)
class RepositoryImplTest {
    @Mock
    lateinit var api: RickAndMortyApi
    var db: AppDataBase? = null
    var resultDao: ResultDao? = null
    private lateinit var repositoryImpl: RepositoryImpl
    val characterName = "Rick Morty"

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        val context =
            InstrumentationRegistry.getInstrumentation().context
        db = Room.inMemoryDatabaseBuilder(context, AppDataBase::class.java).build()
        resultDao = db?.getResultDao()
        repositoryImpl = RepositoryImpl(api, resultDao!!)
    }

    @After
    fun tearDown() {
        db?.close()
    }

    @Test
    fun getCharacter_networkSuccess_returnsResultResponseSuccessList() {
        runBlocking {
            val page = 1
            val characters = getEmptyTestCharacters()
            val response = getCharactersSuccessResponse(characters)

            Mockito.`when`(api.getCharacters(page)).thenReturn(response)
            val values = repositoryImpl.getCharactersWithName(characterName)

            delay(1000)

            values.collect {
                assertEquals(ResultResponse.SuccessList(characters.results), it)
                assertEquals(0, (it as ResultResponse.SuccessList<Result>).data.size)
            }
        }
    }

    @Test
    fun getCharacter_networkSuccess_InsertsCharactersIntoDatabase() {
        runBlocking {
            val page = 1
            val characters = getEmptyTestCharacters()
            val response = getCharactersSuccessResponse(characters)

            Mockito.`when`(api.getCharacters(page)).thenReturn(response)
            repositoryImpl.getCharactersWithName(characterName)

            delay(1000)

            assertEquals(characters.results, resultDao?.getCharacters())
        }
    }

    @Test
    fun getCharacterWithQueryName_networkSuccess_returnsResultResponseSuccessList() {
        runBlocking {
            val page = 1
            val characters = getEmptyTestCharacters()
            val response = getCharactersSuccessResponse(characters)

            Mockito.`when`(api.getCharactersWithQueryName(page, characterName)).thenReturn(response)
            val values = repositoryImpl.getCharactersWithName(characterName)

            delay(1000)

            values.collect {
                assertEquals(ResultResponse.SuccessList(characters.results), it)
                assertEquals(0, (it as ResultResponse.SuccessList<Result>).data.size)
            }
        }
    }

    @Test
    fun getCharacterWithQueryName_networkSuccess_insertsResultsIntoDatabase() =
        runTest {
            val currentPage = 1
            val characters = getEmptyTestCharacters()
            val response = getCharactersSuccessResponse(characters)

            Mockito.`when`(api.getCharactersWithQueryName(currentPage, characterName))
                .thenReturn(response)
            repositoryImpl.getCharactersWithName(characterName)

            delay(1000)

            assertEquals(characters.results, resultDao?.getCharacters())
        }

    @Test
    fun getCharacterWithQueryName_networkErrorEmptyDatabase_returnsEmptyDatabaseResponse() {
        runBlocking {
            val currentPage = 1
            val errorMessage = "server unavailable"
            val response = getCharactersErrorResponse(errorMessage)

            Mockito.`when`(api.getCharactersWithQueryName(currentPage, characterName))
                .thenReturn(response)
            val charactersResultResponse = repositoryImpl.getCharactersWithName(characterName)

            delay(1000)

            charactersResultResponse.collect {
                val successList = (it as ResultResponse.SuccessList).data
                val characters = getEmptyTestCharacters()
                assertEquals(successList, characters.results)
            }
        }
    }

    @Test
    fun getCharacterWithQueryName_networkErrorPopulatedDatabase_returnsCharactersInDatabase() {
        runBlocking {
            val currentPage = 1
            val errorMessage = "server unavailable"
            val response = getCharactersErrorResponse(errorMessage)
            val characters = getSampleTestCharacters(characterName)
            val characterResults = characters.results

            Mockito.`when`(api.getCharactersWithQueryName(currentPage, characterName))
                .thenReturn(response)
            resultDao?.addCharacterResult(characterResults)
            val charactersResultResponse = repositoryImpl.getCharactersWithName(characterName)

            delay(1000)

            charactersResultResponse.collect {
                val successList = (it as ResultResponse.SuccessList).data
                assertEquals(resultDao?.getCharacters(characterName), successList)
            }
        }
    }

    @Test
    fun getCharacterWithQueryName_networkError_doesNotInsertResultsIntoDatabase() =
        runTest {
            val currentPage = 1
            val errorMessage = "server error"
            val response = getCharactersErrorResponse(errorMessage)

            Mockito.`when`(api.getCharactersWithQueryName(currentPage, characterName))
                .thenReturn(response)
            repositoryImpl.getCharactersWithName(characterName)

            delay(1000)

            assertEquals(0, resultDao?.getCharacters()?.size)
        }

    @Test
    fun getCharacterWithQueryName_networkError_returnsItemsInDatabase() {
        val currentPage = 1
        val errorMessage = "server error"
        val response = getCharactersErrorResponse(errorMessage)
        val characterResult = getSampleResult(characterName)

        runBlocking {
            Mockito.`when`(api.getCharactersWithQueryName(currentPage, characterName))
                .thenReturn(response)
            resultDao?.addCharacterResult(characterResult)
            val characterResponse = repositoryImpl.getCharactersWithName(characterName)

            delay(1000)

            assertEquals(1, resultDao?.getCharacters()?.size)
            characterResponse.collect { resultResponse ->
                val character = (resultResponse as ResultResponse.SuccessList<Result>).data
                assertEquals(resultDao?.getCharacters(), character)
            }
        }
    }

    @Test
    fun getCharacterWithQueryName_multipleCharactersInDatabase_returnsMatchingCharactersInDatabase() {
        val currentPage = 1
        val errorMessage = "server error"
        val response = getCharactersErrorResponse(errorMessage)
        val characterResult = getSampleTestCharacters(characterName)

        runBlocking {
            Mockito.`when`(api.getCharactersWithQueryName(currentPage, characterName))
                .thenReturn(response)
            resultDao?.addCharacterResult(characterResult.results)
            val characterResponse = repositoryImpl.getCharactersWithName(characterName)

            delay(1000)

            val dbCharacters = resultDao?.getCharacters(characterName)
            assertEquals(1, dbCharacters?.size)
            characterResponse.collect { resultResponse ->
                val character = (resultResponse as ResultResponse.SuccessList<Result>).data
                assertEquals(character, dbCharacters)
            }
        }
    }

    @Test
    fun getResponse_successNetworkResponse_returnsResultResponseSuccessList() {
        val characters = getSampleTestCharacters(characterName)
        val response = getCharactersSuccessResponse(characters)

        val resultResponse = repositoryImpl.getResultResponse(response)

        assertTrue(resultResponse is ResultResponse.SuccessList)
        val name = (resultResponse as ResultResponse.SuccessList).data[0].name
        assertEquals(characterName, name)
    }

    @Test
    fun getResponse_errorNetworkResponse_returnsResultResponseError() {
        val errorMessage = "Invalid Request"
        val response = getCharactersErrorResponse(errorMessage)

        val resultResponse = repositoryImpl.getResultResponse(response)

        assertTrue(resultResponse is ResultResponse.Error)
        val message = (resultResponse as ResultResponse.Error).errorMessage
        assertEquals(response.errorBody().toString(), message)
    }

    private fun getCharactersSuccessResponse(characters: Characters): Response<Characters> {
        return Response.success(characters)
    }

    private fun getCharactersErrorResponse(errorMessage: String): Response<Characters> {
        val errorCode = HttpURLConnection.HTTP_UNAVAILABLE
        val errorKey = "error"
        val errorJson = JSONObject().also {
            it.put(errorKey, JSONArray().put(errorMessage))
        }
        val body = errorJson.toString().toResponseBody("application/json".toMediaType())
        return Response.error(errorCode, body)
    }
}

/*
* -test clear page list returns from first page
* */


