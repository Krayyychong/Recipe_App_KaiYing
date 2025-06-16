package com.example.recipe_app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class AddRecipeFragment : Fragment() {
    private lateinit var recipeNameEditText: EditText
    private lateinit var recipeTypeEditText: EditText
    private lateinit var ingredientsEditText: EditText
    private lateinit var stepsEditText: EditText
    private lateinit var recipeImage: ImageView
    private lateinit var addButton: Button
    private lateinit var addImageButton: FloatingActionButton
    private var selectedImageUri: Uri? = null
    private lateinit var recipeDao: RecipeDao

    //Activity Result Launcher for picking an image
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){ uri: Uri? ->
        if(uri != null) {
            selectedImageUri = uri
            recipeImage.setImageURI(uri)

            //Take persistable URI permission
            try {
                requireContext().contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: SecurityException) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Failed to persist URI permissions", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_recipe, container, false)

        val recipeDatabase = RecipeDatabase.getDatabase(requireContext())
        recipeDao = recipeDatabase.recipeDao()

        recipeImage = view.findViewById(R.id.imageAddRecipe)
        recipeNameEditText = view.findViewById(R.id.textAddRecipeName)
        recipeTypeEditText = view.findViewById(R.id.textRecipeType)
        ingredientsEditText = view.findViewById(R.id.textAddIngredients)
        stepsEditText = view.findViewById(R.id.textAddSteps)
        addButton = view.findViewById(R.id.addRecipeButton)
        addImageButton = view.findViewById(R.id.addImageButton)

        addButton.setOnClickListener {
            saveRecipe()

            // Navigate back to HomeActivity and finish this fragment
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.putExtra("navigateTo", "home")
            startActivity(intent)
            requireActivity().finish()
        }



/*        val imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) {uri: Uri? ->
            if (uri != null) {
                recipeImage?.setImageURI(uri)
            } else {
                //Toast.makeText(this@AddRecipeFragment, "No media selected",Toast.LENGTH_SHORT).show()
            }
        }*/

        addImageButton.setOnClickListener{
/*            val builder = PickVisualMediaRequest.Builder()
            val instance = builder.setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)*/
            imagePickerLauncher.launch("image/*")
        }

        return view

    }

    private fun saveRecipe() {
        val name = recipeNameEditText.text.toString().trim()
        val type = recipeTypeEditText.text.toString().trim()
        val ingredients = ingredientsEditText.text.toString().trim()
        val steps = stepsEditText.text.toString().trim()

        if (name.isEmpty() || type.isEmpty() || ingredients.isEmpty() || steps.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val ingredientsList = ingredients.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
        val stepsList = steps.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
        val imageUri = selectedImageUri?.toString() ?: getDefaultDrawableUri()

        val newRecipe = RecipeEntity(
            name = name,
            type = type,
            image = imageUri,
            ingredients = ingredientsList,
            steps = stepsList
        )

        // Insert the recipe into the database
        lifecycleScope.launch {
            recipeDao.insertRecipes(listOf(newRecipe))
            Toast.makeText(requireContext(), "Recipe added", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getDefaultDrawableUri(): String {
        // Convert the drawable resource ID to a URI string
        val defaultImageResId = R.drawable.food
        return Uri.parse("android.resource://${requireContext().packageName}/$defaultImageResId").toString()
    }



}