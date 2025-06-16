package com.example.recipe_app

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RecipeDao {
    @Insert
    suspend fun insertRecipes(recipes: List<RecipeEntity>)

    @Query("SELECT * FROM recipes")
    suspend fun getAllRecipes(): List<RecipeEntity>

    @Query("SELECT DISTINCT type FROM recipes")
    suspend fun getAllRecipeTypes(): List<String>

    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    suspend fun getRecipeById(recipeId: Int): RecipeEntity?

    @Insert
    suspend fun insertRecipeTypes(types: List<RecipeTypeEntity>)
    @Insert
    suspend fun insertRecipeType(type: RecipeTypeEntity)

    @Query("SELECT recipeTypes FROM recipe_types")
    suspend fun getAllStoredRecipeTypes(): List<String>


    @Query("SELECT COUNT(*) FROM recipes WHERE type = :type")
    suspend fun countRecipesByType(type: String): Int

    @Delete
    suspend fun deleteRecipe(recipe: RecipeEntity): Int
    @Delete
    suspend fun deleteRecipeType(type: RecipeTypeEntity)

    @Update
    suspend fun updateRecipe(recipe: RecipeEntity)

    @Query("DELETE FROM recipes")
    suspend fun deleteAllRecipes() //Delete all records from the recipes table

    @Query("DELETE FROM recipe_types")
    suspend fun deleteAllRecipeTypes() //Delete all records from the recipes table
}
