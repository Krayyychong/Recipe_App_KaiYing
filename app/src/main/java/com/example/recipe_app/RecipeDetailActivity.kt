package com.example.recipe_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var recipe: RecipeData
    private lateinit var recipeList: MutableList<RecipeData>
    private lateinit var recipeImage: ImageView
    private lateinit var recipeName: TextView
    private lateinit var recipeIngredients: TextView
    private lateinit var recipeSteps: TextView
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button
    private lateinit var backButton: Button
    private lateinit var editRecipeLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        recipeImage = findViewById(R.id.imageRecipeDetail)
        recipeName = findViewById(R.id.recipeName)
        recipeIngredients = findViewById(R.id.textIngredients)
        recipeSteps = findViewById(R.id.textSteps)
        editButton = findViewById(R.id.editRecipeButton)
        deleteButton = findViewById(R.id.deleteRecipeButton)
        backButton = findViewById(R.id.backButton)
        recipeList = RecipeFileHandling.loadRecipes(this)


        // Get recipe data from intent
        val recipeJson = intent.getStringExtra("RECIPE_DATA")
        recipe = Gson().fromJson(recipeJson, RecipeData::class.java)
        recipeList = RecipeFileHandling.loadRecipes(this)

        //populate fields from json
        recipeImage.setImageResource(
            resources.getIdentifier(recipe.image, "drawable", packageName)
        )
        recipeName.text = recipe.name
        //separate ingredients per line
        recipeIngredients.text = recipe.ingredients.joinToString("\n")
        //show steps per line with numbering
        recipeSteps.text = recipe.steps.mapIndexed{ index, step -> "${index+1}. $step"}.joinToString("\n")

        editRecipeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val updatedRecipeJson = result.data?.getStringExtra("UPDATED_RECIPE")
                if (updatedRecipeJson != null) {
                    val updatedRecipe = Gson().fromJson(updatedRecipeJson, RecipeData::class.java)

                    //Load current list
                    recipeList = RecipeFileHandling.loadRecipes(this)

                    //Update recipe in the list
                    val index = recipeList.indexOfFirst { it.name == recipe.name }
                    if (index != -1) {
                        recipeList[index] = updatedRecipe
                        RecipeFileHandling.saveRecipes(this, recipeList)
                    }

                    // Update frontend
                    recipe = updatedRecipe
                    recipeName.text = recipe.name
                    recipeIngredients.text = recipe.ingredients.joinToString("\n")
                    recipeSteps.text = recipe.steps.mapIndexed { i, step -> "${i + 1}. $step" }.joinToString("\n")
                }
            }
        }

        // edit recipe
        editButton.setOnClickListener {
            val intent = Intent(this, EditDetailActivity::class.java)
            intent.putExtra("RECIPE_DATA",Gson().toJson(recipe))
            editRecipeLauncher.launch(intent)

        }

        // delete recipe
        deleteButton.setOnClickListener {
            //show warn dialog
            val dialog = android.app.AlertDialog.Builder(this)
                .setTitle("Delete Recipe")
                .setMessage("Are you sure you want to delete this recipe?")
                .setPositiveButton("Yes") {_, _ ->
                    //remote current recipe from json file
                    val index = recipeList.indexOfFirst{it.name == recipe.name}
                    if(index != -1) {
                        recipeList.removeAt(index)
                        RecipeFileHandling.saveRecipes(this, recipeList)
                        Toast.makeText(this, "Recipe deleted!", Toast.LENGTH_SHORT).show()
                    }
                    //return to HomeActivity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("No") {dialogInterface, _ -> dialogInterface.dismiss()} //do nothing
                .create()

            dialog.show()
        }

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() //return to HomeActivity
        }

    }

    override fun onResume() {
        super.onResume()
    }

}