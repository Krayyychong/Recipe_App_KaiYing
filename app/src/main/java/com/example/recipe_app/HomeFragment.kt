package com.example.recipe_app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipe_app.databinding.FragmentHomeBinding
import com.google.gson.Gson
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var recipeDatabase: RecipeDatabase
    private lateinit var recipeDao: RecipeDao

    private var allRecipes: List<RecipeEntity> = emptyList()
    private var filteredRecipes: List<RecipeEntity> = emptyList()
    private var recipeTypes: List<String> = emptyList()

    //receive message to refresh recipe type spinner
    private val recipeDeletedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            //Refresh the recipe types after a recipe is deleted
            loadDataAndSetupSpinner()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Initialize Room database
        recipeDatabase = RecipeDatabase.getDatabase(requireContext())
        recipeDao = recipeDatabase.recipeDao()

        setupRecyclerView()
        loadDataAndSetupSpinner()
    }

    private fun setupRecyclerView() {
        binding.itemRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.itemRecyclerView.adapter = ItemRecipeAdapter(emptyList()) { recipe ->
            val intent = Intent(requireContext(), RecipeDetailActivity::class.java).apply {
                putExtra("RECIPE_ID", recipe.id)
            }
            startActivity(intent)
        }
    }

    private fun loadDataAndSetupSpinner() {
        lifecycleScope.launch {
            // Load recipes and types
            allRecipes = recipeDao.getAllRecipes()

            val recipes = recipeDao.getAllRecipes()

            // Log each recipe to view it in Logcat
            for (recipe in recipes) {
                Log.d("RecipeData", "Recipe ID: ${recipe.id}, Image URI: ${recipe.image},Name: ${recipe.name}, Type: ${recipe.type}, Ingredients: ${recipe.ingredients.joinToString(", ")}, Steps: ${recipe.steps.joinToString(", ")}")
            }

            // Combine stored types with types from recipes
            val storedTypes = recipeDao.getAllStoredRecipeTypes()
            val recipeTypesFromRecipes = recipeDao.getAllRecipeTypes()
            recipeTypes = listOf("All") + (storedTypes + recipeTypesFromRecipes).distinct()

            setupSpinner()
            filterRecipes("All")
        }
    }

    private fun setupSpinner() {
        val arrayAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            recipeTypes
        )
        binding.spinnerRecipetypes.adapter = arrayAdapter

        binding.spinnerRecipetypes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                filterRecipes(recipeTypes[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                filterRecipes("All")
            }
        }
    }

    private fun filterRecipes(type: String) {
        filteredRecipes = if (type == "All") {
            allRecipes
        } else {
            allRecipes.filter { it.type == type }
        }
        updateRecyclerView()
    }

    private fun updateRecyclerView() {
        (binding.itemRecyclerView.adapter as? ItemRecipeAdapter)?.updateData(filteredRecipes)
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            // Refresh data when returning to fragment
            allRecipes = recipeDao.getAllRecipes()
            filterRecipes(binding.spinnerRecipetypes.selectedItem?.toString() ?: "All")
            loadDataAndSetupSpinner()
        }
    }
}