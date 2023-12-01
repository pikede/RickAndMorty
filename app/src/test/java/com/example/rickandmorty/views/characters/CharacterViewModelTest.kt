package com.example.rickandmorty.views.characters

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.rickandmorty.data.Repository
import com.example.rickandmorty.data.ResultResponse
import com.example.rickandmorty.data.getEmptyResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import java.lang.Thread.sleep

@RunWith(MockitoJUnitRunner.Silent::class)
class CharacterViewModelTest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: CharacterViewModel
    @Mock
    private lateinit var repository: Repository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = CharacterViewModel(repository)
    }

    @Test
    fun getCharacters_withCharacterNameChanged_updatesCharacterQueryNameAndRepositoryPageCount() =
        runTest {
            val newCharacterName = "Rick"

            viewModel.getCharacters(newCharacterName)
            sleep(1000)

            assertEquals(newCharacterName, viewModel.characterQueryName)
            verify(repository).getCharactersWithName(newCharacterName)
            verify(repository, times(1)).resetPage()
        }

    @Test
    fun getCharacters_withCharacterNameEmpty_doesNotChangeQueryName() = runTest {
        assertEquals("", viewModel.characterQueryName)
        verify(repository).getCharacters()
        verify(repository, times(0)).resetPage()
    }

    @Test
    fun getCharacters_withCharacterNameEmpty_characterResultsIsSuccessFul() = runTest {
        val characterName = ""
        val result = getEmptyResult()
        val characterResult = listOf(result)

        delay(1000)

        assertEquals(characterName, viewModel.characterQueryName)
        launch {
            viewModel.charactersUIState.collect {
                assertEquals(it, CharacterViewModel.CharactersState.Success(characterResult))
            }
        }.cancel()
        verify(repository, times(1)).getCharacters()
        verify(repository, times(0)).resetPage()
    }

    @Test
    fun getCharacters_repositoryResultResponseIsSuccess_returnsSuccessCharacterUiState() =
        runTest {
            val characterName = "Rick"
            val result = getEmptyResult()
            val characterResult = listOf(result)
            val resultResponse = ResultResponse.SuccessList(characterResult)

            `when`(repository.getCharactersWithName(characterName)).thenReturn(
                flow { emit(resultResponse) }
            )
            viewModel.getCharacters(characterName)
            delay(1000)

            assertEquals(characterName, viewModel.characterQueryName)
            verify(repository).getCharactersWithName(characterName)
            verify(repository, times(1)).resetPage()
            launch {
                viewModel.charactersUIState.collect {
                    assertEquals(it, CharacterViewModel.CharactersState.Success(characterResult))
                }
            }.cancel()
        }

    @Test
    fun getCharacters_repositoryResultResponseIsError_returnsErrorCharacterUiState() = runTest {
        val characterName = ""
        val errorMessage = "error"
        val resultResponseError = ResultResponse.Error(errorMessage)

        `when`(repository.getCharactersWithName(characterName)).thenReturn(
            flow { emit(resultResponseError) }
        )
        delay(1000)

        assertEquals(characterName, viewModel.characterQueryName)
        verify(repository, times(1)).getCharacters()
        verify(repository, times(0)).resetPage()
        launch {
            viewModel.charactersUIState.collect {
                assertEquals(it, CharacterViewModel.CharactersState.Error(errorMessage))
            }
        }.cancel()
    }
}