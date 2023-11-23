package com.example.rickandmorty.views.location

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmorty.data.network.RickAndMortyApi
import com.example.rickandmorty.models.Locations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class LocationsViewModel(private val api: RickAndMortyApi) : ViewModel() {
    val errorMessage = MutableLiveData<String>()
    val characterLocation: LiveData<Locations?> get() = _characterLocation
    private val _characterLocation = MutableLiveData<Locations?>()

    fun getLocations(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val playerResponse = api.getCharactersLocations(id)
                handleResponse(playerResponse)
            } catch (e: Exception) {
                errorGettingLocations(e.message ?: e.toString())
            }
        }
    }

    private fun handleResponse(playerResponse: Response<Locations>) {
        if (playerResponse.isSuccessful) {
            updateSuccessfulLocations(playerResponse)
        } else {
            playerResponse.errorBody()?.let { errorGettingLocations(it.toString()) }
        }
    }

    private fun updateSuccessfulLocations(playerResponse: Response<Locations>) {
        playerResponse.body()?.let {
            _characterLocation.postValue(it)
        }
    }

    private fun errorGettingLocations(errorDisplayMessage: String) {
        _characterLocation.postValue(null)
        errorMessage.postValue(errorDisplayMessage)
    }
}