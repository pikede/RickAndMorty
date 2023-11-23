package com.example.rickandmorty.views.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmorty.data.Repository
import com.example.rickandmorty.models.Characters
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
import org.koin.java.KoinJavaComponent.inject
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class CharacterViewModel() : ViewModel(), CoroutineScope {
    sealed interface CharactersState {
        data class Success(val characters: List<Result>) : CharactersState
        data class Error(val errorMessage: String) : CharactersState
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + SupervisorJob()
    private val repository: Repository by inject(Repository::class.java)
    private var characterResults = mutableListOf<Result>()
    private val _charactersUIState =
        MutableSharedFlow<CharactersState>()
    val charactersUIState: SharedFlow<CharactersState> = _charactersUIState
    private var job: Job? = null
    private var characterQueryName = ""


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
                repository.getCharacterWithQueryNameResponse(characterQueryName)
            } else {
                repository.getCharacters()
            }
            handleResponse(playerResponse)
        }
    }

    private fun updateName(characterName: String) {
        val changed = characterName != characterQueryName
        if (changed) {
            characterQueryName = characterName
            clearList()
        }
    }

    private suspend fun handleResponse(playerResponse: Flow<Response<Characters>>) {
        playerResponse
            .catch {
                errorGettingCharacters(it.message ?: it.toString())
            }
            .collect {
                when (it.isSuccessful) {
                    true -> updateSuccessfulCharacters(it)
                    else -> it.errorBody()?.let { errorGettingCharacters(it.toString()) }
                }
            }
    }

    private fun updateSuccessfulCharacters(playerResponse: Response<Characters>) {
        playerResponse.body()?.results?.let {
            characterResults.addAll(it)
            launch {
                _charactersUIState.emit(CharactersState.Success(characterResults))
            }
        }
    }

    private fun clearList() {
        repository.resetPage()
        characterResults.clear()
        launch {
            _charactersUIState.emit(CharactersState.Success(characterResults))
        }
    }

    private fun errorGettingCharacters(errorMessage: String) {
        launch {
            _charactersUIState.emit(CharactersState.Error(errorMessage))
        }
    }
}