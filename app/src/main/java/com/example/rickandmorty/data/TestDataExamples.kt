package com.example.rickandmorty.data

import com.example.rickandmorty.models.Characters
import com.example.rickandmorty.models.Info
import com.example.rickandmorty.models.Location
import com.example.rickandmorty.models.Origin
import com.example.rickandmorty.models.Result

fun getEmptyResult() = Result(
    "",
    null,
    "",
    0,
    "",
    null,
    "",
    null,
    "",
    "",
    "",
    ""
)

fun getSampleResult(characterName: String) = Result(
    "November, 11 2020",
    listOf("1", "2"),
    "Male",
    0,
    "Character image",
    Location("image", "url"),
    characterName,
    Origin("Rick Morty", "url"),
    "specie",
    "status",
    "type",
    "url"
)

fun getEmptyTestInfo() = Info(0, "", 0, "")

fun getEmptyTestCharacters(): Characters {
    val info = getEmptyTestInfo()
    val result = emptyList<Result>()
    return Characters(info, result)
}

fun getSampleTestCharacters(characterName: String): Characters {
    val info = getEmptyTestInfo()
    val firstResult = getSampleResult(characterName)
    val secondResult = firstResult.copy(id = 2, name = "Morty")
    val thirdResult = firstResult.copy(id = 2, name = "Sanchez Ortega", gender = "female")
    val results = listOf(firstResult, secondResult, thirdResult)
    return Characters(info, results)
}