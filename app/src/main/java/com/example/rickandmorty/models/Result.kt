package com.example.rickandmorty.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//@Entity(tableName = "RickAndMortyCharacters")
data class Result(
    val created: String,
    val episode: List<String>,
    val gender: String,
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,
    val image: String?,
    val location: Location,
    val name: String,
    val origin: Origin,
    val species: String,
    val status: String,
    val type: String,
    val url: String
)