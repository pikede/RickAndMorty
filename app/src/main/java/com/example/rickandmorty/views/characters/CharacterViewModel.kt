package com.example.rickandmorty.views.characters

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmorty.data.Repository
import com.example.rickandmorty.data.ResultResponse
import com.example.rickandmorty.models.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class CharacterViewModel(private val repository: Repository) : ViewModel(), CoroutineScope {
    sealed interface CharactersState {
        data class Success(val characters: List<Result>) : CharactersState
        data class Error(val errorMessage: String) : CharactersState
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + SupervisorJob()
    private var characterResults = mutableListOf<Result>()
    private val _charactersUIState =
        MutableSharedFlow<CharactersState>()
    val charactersUIState: SharedFlow<CharactersState> = _charactersUIState
    private var job: Job? = null

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var characterQueryName = ""


    init {
        getCharacters()
    }

    fun getCharacters(characterName: String = characterQueryName) {
        if (job != null && job?.isCompleted != true) {
            return
        }
        job = viewModelScope.launch(Dispatchers.IO) {
            updateName(characterName)
            val isNotEmpty = characterQueryName.isNotEmpty() && characterQueryName.isNotBlank()
            val playerResponse = if (isNotEmpty) {
                repository.getCharactersWithName(characterQueryName)
            } else {
                repository.getCharacters()
            }
            handleResponse(playerResponse)
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun updateName(characterName: String) {
        val changed = characterName != characterQueryName
        if (changed) {
            characterQueryName = characterName
            launch {
                clearList()
            }
        }
    }

    private suspend fun handleResponse(playerResponse: Flow<ResultResponse<Result>>) {
        playerResponse
            .catch {
                errorGettingCharacters(it.message ?: it.toString())
            }
            .collect {
                when (it) {
                    is ResultResponse.SuccessList -> updateSuccessfulCharacters(it.data)
                    is ResultResponse.Error -> it.errorMessage?.let { errorMessage ->
                        errorGettingCharacters(
                            errorMessage
                        )
                    }
                }
            }
    }

    private suspend fun updateSuccessfulCharacters(characters: List<Result>) {
        characterResults.addAll(characters)
        _charactersUIState.emit(CharactersState.Success(characterResults))
    }

    private suspend fun clearList() {
        repository.resetPage()
        characterResults.clear()
        _charactersUIState.emit(CharactersState.Success(characterResults))
    }

    private suspend fun errorGettingCharacters(errorMessage: String) {
        _charactersUIState.emit(CharactersState.Error(errorMessage))
    }
}