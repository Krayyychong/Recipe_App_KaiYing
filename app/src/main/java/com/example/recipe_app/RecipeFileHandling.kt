package com.example.recipe_app

import android.content.Context
import com.google.gson.reflect.TypeToken
import com.google.gson.Gson
import java.io.File

object RecipeFileHandling {
    private const val RECIPE_FILE = "recipes.json"
    private const val RECIPETYPE_FILE = "recipetypes.json"

    fun loadRecipes(context: Context): MutableList<RecipeData> {
        val file = File(context.filesDir, RECIPE_FILE)
        return if (file.exists()) {
            val json = file.readText()
            val type = object : TypeToken<MutableList<RecipeData>>() {}.type
            Gson().fromJson(json, type)
        } else {
            mutableListOf()
        }
    }

    fun saveRecipes(context: Context, recipes: List<RecipeData>) {
        val file = File(context.filesDir, RECIPE_FILE)
        file.writeText(Gson().toJson(recipes))
    }

    fun loadRecipeTypes(context: Context): List<String> {
        val file = File(context.filesDir, RECIPETYPE_FILE)
        return if (file.exists()) {
            val json = file.readText()
            val typeList = Gson().fromJson(json, RecipeTypeData::class.java)
            typeList.recipeTypes
        } else {
            emptyList()
        }
    }
}