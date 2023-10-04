package com.example.rickandmorty.models

data class Locations(
    val created: String,
    val dimension: String,
    val id: Int,
    val name: String,
    val residents: List<String>?,
    val type: String,
    val url: String
)