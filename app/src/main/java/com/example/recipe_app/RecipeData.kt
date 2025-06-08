package com.example.recipe_app

import kotlinx.serialization.Serializable

@Serializable
data class RecipeData(
    var name: String,
    val type: String,
    val image: String,
    var ingredients: List<String>,
    var steps: List<String>
)
