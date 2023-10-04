package com.example.rickandmorty.views.characters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmorty.models.Characters
import com.example.rickandmorty.models.Result
import com.example.rickandmorty.network.RickAndMortyService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class CharacterViewModel(private val rickAndMortyService: RickAndMortyService) : ViewModel() {
    val errorMessage = MutableLiveData<String>()
    val rickAndMortyCharacters: LiveData<List<Result>> get() = _rickAndMortyCharacters
    private val _rickAndMortyCharacters = MutableLiveData<List<Result>>()
    private val api by lazy { rickAndMortyService.getPlayerApi() }

    fun getCharacters() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val playerResponse = api.getCharacters()
                handleResponse(playerResponse)
            } catch (e: Exception) {
                errorGettingCharacters(e.message ?: e.toString())
            }
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
            _rickAndMortyCharacters.postValue(it)
        }
    }

    private fun errorGettingCharacters(errorDisplayMessage: String) {
        _rickAndMortyCharacters.postValue(emptyList())
        errorMessage.postValue(errorDisplayMessage)
    }
}