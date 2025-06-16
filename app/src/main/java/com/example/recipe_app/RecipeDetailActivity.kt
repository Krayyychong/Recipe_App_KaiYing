package com.example.recipe_app

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import kotlinx.coroutines.launch

class RecipeDetailActivity : AppCompatActivity() {
    private lateinit var recipeDatabase: RecipeDatabase
    private lateinit var recipeDao: RecipeDao
    private var recipe: RecipeEntity? = null

    private lateinit var recipeImage: ImageView
    private lateinit var recipeName: TextView
    private lateinit var recipeIngredients: TextView
    private lateinit var recipeSteps: TextView
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        //Initialize Room database
        recipeDatabase = RecipeDatabase.getDatabase(this)
        recipeDao = recipeDatabase.recipeDao()

        recipeImage = findViewById(R.id.imageRecipeDetail)
        recipeName = findViewById(R.id.recipeName)
        recipeIngredients = findViewById(R.id.textIngredients)
        recipeSteps = findViewById(R.id.textSteps)
        editButton = findViewById(R.id.editRecipeButton)
        deleteButton = findViewById(R.id.deleteRecipeButton)
        backButton = findViewById(R.id.backButton)

        // Get recipe ID from intent
        val recipeId = intent.getIntExtra("RECIPE_ID", -1)

        if (recipeId != -1) {
            loadRecipe(recipeId)
        } else {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show()
            finish()
        }

        setupButtonListeners()
    }

    private fun loadRecipe(recipeId: Int) {
        lifecycleScope.launch {
            recipe = recipeDao.getRecipeById(recipeId)
            recipe?.let {
                runOnUiThread {
                    displayRecipe(it)
                }
            } ?: run {
                runOnUiThread {
                    Toast.makeText(this@RecipeDetailActivity, "Recipe not found", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun displayRecipe(recipe: RecipeEntity) {
        // Display image
        if (recipe.image.isNotEmpty()) {
            try {
                if (recipe.image.startsWith("content://")) {
                    val imageUri = Uri.parse(recipe.image)
                    recipeImage.setImageURI(imageUri)
                } else {
                    val context = applicationContext
                    val imageResId = context.resources.getIdentifier(recipe.image, "drawable", packageName)
                    if (imageResId != 0) {
                        recipeImage.setImageResource(imageResId)
                    } else {
                        recipeImage.setImageResource(R.drawable.food)
                    }
                }
            } catch (e: Exception) {
                recipeImage.setImageResource(R.drawable.food)
            }
        } else {
            recipeImage.setImageResource(R.drawable.food)
        }

        recipeName.text = recipe.name
        recipeIngredients.text = recipe.ingredients.joinToString("\n")
        recipeSteps.text = recipe.steps.mapIndexed { index, step ->
            "${index + 1}. $step"
        }.joinToString("\n")
    }

    private fun setupButtonListeners() {
        editButton.setOnClickListener {
            recipe?.let {
                val intent = Intent(this, EditDetailActivity::class.java).apply {
                    putExtra("RECIPE_ID", it.id)
                }
                startActivity(intent)
            }
        }

        deleteButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Recipe")
                .setMessage("Are you sure you want to delete this recipe?")
                .setPositiveButton("Yes") { _, _ ->
                    lifecycleScope.launch {
                        val recipeDao = RecipeDatabase.getDatabase(applicationContext).recipeDao()
                        recipe?.let {
                            recipeDao.deleteRecipe(it)
                            val recipeCount = recipeDao.countRecipesByType(it.type)

                            if (recipeCount == 0) {
                                val recipeTypeEntity = RecipeTypeEntity(it.type)
                                recipeDao.deleteRecipeType(recipeTypeEntity)
                            }

                            //notify HomeFragment to refresh the recipe types spinner
                            val intent = Intent("com.example.recipe_app.RECIPE_DELETED")
                            sendBroadcast(intent)

                            Toast.makeText(
                                this@RecipeDetailActivity,
                                "Recipe deleted!",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                    }
                }
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        val recipeId = intent.getIntExtra("RECIPE_ID", -1)
        if (recipeId != -1) {
            loadRecipe(recipeId)
        }
    }
}