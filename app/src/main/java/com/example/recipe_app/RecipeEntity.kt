package com.example.recipe_app

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "recipes")
@TypeConverters(Converters::class)
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var name: String,
    var image: String,
    var ingredients: List<String>,
    var steps: List<String>,
    val type: String
)