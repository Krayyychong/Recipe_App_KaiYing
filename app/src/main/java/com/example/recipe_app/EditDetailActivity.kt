package com.example.recipe_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch


class EditDetailActivity : AppCompatActivity() {

    private lateinit var recipe: RecipeEntity  // Ensure this is a RecipeEntity
    private lateinit var recipeImage: ImageView
    private lateinit var recipeName: EditText
    private lateinit var recipeIngredients: EditText
    private lateinit var recipeSteps: EditText
    private lateinit var updateButton: Button
    private lateinit var backButton: Button
    private lateinit var editImageButton: FloatingActionButton
    private var selectedImageUri: Uri? = null

    // Activity Result Launcher for picking an image
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            recipeImage.setImageURI(uri)

            // Take persistable URI permission
            try {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: SecurityException) {
                e.printStackTrace()
                Toast.makeText(this, "Failed to persist URI permissions", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_detail)

        recipeImage = findViewById(R.id.imageRecipeDetail)
        recipeName = findViewById(R.id.recipeName)
        recipeIngredients = findViewById(R.id.textIngredients)
        recipeSteps = findViewById(R.id.textSteps)
        updateButton = findViewById(R.id.updateRecipeButton)
        backButton = findViewById(R.id.backDetailButton)
        editImageButton = findViewById(R.id.editImageButton)

        // Get recipe data from intent
        val recipeId = intent.getIntExtra("RECIPE_ID", -1)

        // Check if the recipeId is valid
        if (recipeId != -1) {
            loadRecipe(recipeId)
        } else {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show()
            finish() // Finish the activity if no valid recipeId
        }

        // Set up button listeners
        setupButtonListeners()

        editImageButton.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }
    }

    private fun loadRecipe(recipeId: Int) {
        lifecycleScope.launch {
            val recipeDao = RecipeDatabase.getDatabase(this@EditDetailActivity).recipeDao()
            recipe = recipeDao.getRecipeById(recipeId) ?: run {
                runOnUiThread {
                    Toast.makeText(this@EditDetailActivity, "Recipe not found", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }
            runOnUiThread {
                displayRecipe(recipe)
            }
        }
    }

    private fun displayRecipe(recipe: RecipeEntity) {
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

        recipeName.setText(recipe.name)
        recipeIngredients.setText(recipe.ingredients.joinToString("\n"))
        recipeSteps.setText(recipe.steps.joinToString("\n"))
    }

    private fun setupButtonListeners() {
        updateButton.setOnClickListener {
            recipe.name = recipeName.text.toString()
            recipe.ingredients = recipeIngredients.text.toString().split("\n")
            recipe.steps = recipeSteps.text.toString().split("\n")
            //Only update the image URI if the user picked a new image
            recipe.image = selectedImageUri?.toString() ?: recipe.image

            //Update the recipe in the database
            lifecycleScope.launch {
                val recipeDao = RecipeDatabase.getDatabase(this@EditDetailActivity).recipeDao()

                recipeDao.updateRecipe(recipe)
                val updatedRecipe = recipeDao.getRecipeById(recipe.id)
                //Update the UI with the updated data
                runOnUiThread {
                    updatedRecipe?.let {
                        displayRecipe(it)
                    }
                }
                Toast.makeText(this@EditDetailActivity, "Recipe updated!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        backButton.setOnClickListener {
            finish()
        }
    }
}