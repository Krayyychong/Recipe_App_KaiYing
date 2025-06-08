package com.example.recipe_app

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class AddRecipeFragment : Fragment() {
    private lateinit var recipeNameEditText: EditText
    private lateinit var recipeTypeEditText: EditText
    private lateinit var ingredientsEditText: EditText
    private lateinit var stepsEditText: EditText
    private lateinit var addButton: Button

    private val fileName = "recipes.json"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_recipe, container, false)

        recipeNameEditText = view.findViewById(R.id.textAddRecipeName)
        recipeTypeEditText = view.findViewById(R.id.textRecipeType)
        ingredientsEditText = view.findViewById(R.id.textAddIngredients)
        stepsEditText = view.findViewById(R.id.textAddSteps)
        addButton = view.findViewById(R.id.addRecipeButton)

        addButton.setOnClickListener {
            saveRecipe()

            // Navigate back to HomeActivity and finish this fragment
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.putExtra("navigateTo", "home")
            startActivity(intent)
            requireActivity().finish()
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

        val newRecipe = RecipeData(
            name = name,
            type = type,
            image = "food", //doesnt allowed to upload image
            ingredients = ingredientsList,
            steps = stepsList
        )

        val recipes = RecipeFileHandling.loadRecipes(requireContext())
        recipes.add(newRecipe)
        RecipeFileHandling.saveRecipes(requireContext(), recipes)

        Toast.makeText(requireContext(), "Recipe added", Toast.LENGTH_SHORT).show()
    }



}