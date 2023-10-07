package com.example.rickandmorty.views.characters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmorty.models.Characters
import com.example.rickandmorty.models.Result
import com.example.rickandmorty.network.RickAndMortyService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Response

class CharacterViewModel(private val rickAndMortyService: RickAndMortyService) : ViewModel() {
    val errorMessage = MutableLiveData<String>()
    val rickAndMortyCharacters: LiveData<List<Result>> get() = _rickAndMortyCharacters
    private val _rickAndMortyCharacters = MutableLiveData<List<Result>>()
    private val api by lazy { rickAndMortyService.getPlayerApi() }
    private var currentPaginatedPage = 1
    private var job: Job? = null
    private var characterResults = mutableListOf<Result>()
    private var characterQueryName = ""

    init {
        getCharacters()
    }

    fun getCharacters(characterName: String = characterQueryName) {
        if (job != null && job?.isCompleted != true) {
            return
        }
        job = viewModelScope.launch(Dispatchers.IO) {
            try {
                val playerResponse = getCharacterResponse(characterName)
                handleResponse(playerResponse)
            } catch (e: Exception) {
                errorGettingCharacters(e.message ?: e.toString())
            }
        }
    }

    private suspend fun getCharacterResponse(characterName: String): Response<Characters> {
        val changed = characterName != characterQueryName
        if(changed) {
            characterQueryName = characterName
            clearList()
        }
        val isNotEmpty = characterQueryName.isNotEmpty() && characterQueryName.isNotBlank()
        return if (isNotEmpty) {
            api.getCharactersWithQueryName(currentPaginatedPage++, characterQueryName)
        } else {
            api.getCharacters(currentPaginatedPage++)
        }
    }

    private fun handleResponse(playerResponse: Response<Characters>) {
        if (playerResponse.isSuccessful) {
            updateSuccessfulCharacters(playerResponse)
        } else {
            playerResponse.errorBody()?.let { errorGettingCharacters(it.toString()) }
        }
    }

    private fun updateSuccessfulCharacters(playerResponse: Response<Characters>) {
        playerResponse.body()?.results?.let {
            characterResults.addAll(it)
            _rickAndMortyCharacters.postValue(characterResults)
        }
    }

    private fun clearList() {
        currentPaginatedPage = 1
        characterResults.clear()
        _rickAndMortyCharacters.postValue(characterResults)
    }

    private fun errorGettingCharacters(errorDisplayMessage: String) {
        _rickAndMortyCharacters.postValue(emptyList())
        errorMessage.postValue(errorDisplayMessage)
    }
}