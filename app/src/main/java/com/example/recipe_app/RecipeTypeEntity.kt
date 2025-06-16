package com.example.recipe_app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipe_types")
data class RecipeTypeEntity(
    @PrimaryKey
    val recipeTypes: String
)