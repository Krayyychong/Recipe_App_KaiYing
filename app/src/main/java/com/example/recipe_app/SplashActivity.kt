package com.example.recipe_app

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.recipe_app.databinding.ActivitySplashBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    lateinit var binding:ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        lifecycleScope.launch {
            val database = RecipeDatabase.getDatabase(applicationContext)
            //resetDatabase(database) //use it when want to reset the whole app data, remember to comment and uncomment it when neccesary
            if (database.recipeDao().getAllRecipes().isEmpty()) {
                //if database empty, delete all data and repopulate
                populateDatabase(database)
            }
        }

    }

    // Function to reset the database by deleting all data
    private suspend fun resetDatabase(database: RecipeDatabase) {
        database.recipeDao().deleteAllRecipes() // Delete all recipes
        database.recipeDao().deleteAllRecipeTypes() // Delete all recipe types
    }
    private suspend fun populateDatabase(database: RecipeDatabase) {
        val recipesJson = assets.open("recipes.json").bufferedReader().use { it.readText() }
        val recipeTypesJson = assets.open("recipetypes.json").bufferedReader().use { it.readText() }

        val recipes: List<RecipeEntity> = Gson().fromJson(recipesJson, object : TypeToken<List<RecipeEntity>>() {}.type)

        database.recipeDao().insertRecipes(recipes)

        val recipeTypes: List<String> = Gson().fromJson(recipeTypesJson, object : TypeToken<List<String>>() {}.type)

        // Convert to RecipeTypeEntity objects
        val recipeTypeEntities = recipeTypes.map { RecipeTypeEntity(it) }
        database.recipeDao().insertRecipeTypes(recipeTypeEntities)
    }

}