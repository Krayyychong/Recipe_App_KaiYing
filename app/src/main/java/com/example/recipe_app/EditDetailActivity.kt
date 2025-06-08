package com.example.recipe_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.example.recipe_app.RecipeFileHandling



class EditDetailActivity : AppCompatActivity() {

    private lateinit var recipe: RecipeData
    private lateinit var recipeImage: ImageView
    private lateinit var recipeName: EditText
    private lateinit var recipeIngredients: EditText
    private lateinit var recipeSteps: EditText
    private lateinit var updateButton: Button
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_detail)

        recipeImage = findViewById(R.id.imageRecipeDetail)
        recipeName = findViewById(R.id.recipeName)
        recipeIngredients = findViewById(R.id.textIngredients)
        recipeSteps = findViewById(R.id.textSteps)
        updateButton = findViewById(R.id.updateRecipeButton)
        backButton = findViewById(R.id.backDetailButton)

        // Get recipe data from intent
        val recipeJson = intent.getStringExtra("RECIPE_DATA")
        recipe = Gson().fromJson(recipeJson, RecipeData::class.java)

        //populate fields from json
        recipeImage.setImageResource(
            resources.getIdentifier(recipe.image, "drawable", packageName)
        )
        recipeName.setText(recipe.name)
        recipeIngredients.setText(recipe.ingredients.joinToString("\n"))
        recipeSteps.setText(recipe.steps.joinToString("\n"))

        updateButton.setOnClickListener {
            recipe.name = recipeName.text.toString()
            recipe.ingredients = recipeIngredients.text.toString().split("\n")
            recipe.steps = recipeSteps.text.toString().split("\n")

            //load all recipes
            val allRecipes = RecipeFileHandling.loadRecipes(this)

            //replace old recipe with the updated one
            val recipeList = RecipeFileHandling.loadRecipes(this)
            val index = recipeList.indexOfFirst { it.name == recipe.name }
            if (index != -1) {
                allRecipes[index] = recipe
                //save updated list to JSON
                RecipeFileHandling.saveRecipes(this, allRecipes)
                Toast.makeText(this, "Recipe updated!", Toast.LENGTH_SHORT).show()
            }

            val resultIntent = Intent()
            resultIntent.putExtra("UPDATED_RECIPE", Gson().toJson(recipe))
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        backButton.setOnClickListener {
            val intent = Intent(this, RecipeDetailActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}