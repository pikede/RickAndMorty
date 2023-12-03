package com.example.rickandmorty.data

sealed interface ResultResponse<out Data> {
    data class Error(val errorMessage: String?) : ResultResponse<Nothing>
    data class SuccessList<Data>(val data : List<Data>) : ResultResponse<Data>
}