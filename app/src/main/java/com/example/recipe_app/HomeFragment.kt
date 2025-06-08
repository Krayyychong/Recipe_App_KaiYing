package com.example.recipe_app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

class HomeFragment : Fragment() {

    private lateinit var itemRecipeRecyclerView: RecyclerView
    private lateinit var itemRecipeAdapter: ItemRecipeAdapter
    private lateinit var recipeTypeSpinner: Spinner
    private lateinit var allRecipes: List<RecipeData>
    private lateinit var filteredRecipes: List<RecipeData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        itemRecipeRecyclerView = view.findViewById(R.id.itemRecyclerView)
        recipeTypeSpinner = view.findViewById(R.id.spinner_recipetypes)

        // Load recipe types and recipes from file
        val recipeTypes = RecipeFileHandling.loadRecipeTypes(requireContext())
        allRecipes = RecipeFileHandling.loadRecipes(requireContext())
        Toast.makeText(requireContext(), "Recipes loaded: ${allRecipes.size}", Toast.LENGTH_SHORT).show()
        filteredRecipes = allRecipes

        // Setup Spinner
        val arrayAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            recipeTypes
        )
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        recipeTypeSpinner.adapter = arrayAdapter

        // Setup RecyclerView
        itemRecipeAdapter = ItemRecipeAdapter(filteredRecipes) { recipe ->
            val intent = Intent(requireContext(), RecipeDetailActivity::class.java)
            intent.putExtra("RECIPE_DATA", Gson().toJson(recipe))
            startActivity(intent)
        }
        itemRecipeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        itemRecipeRecyclerView.adapter = itemRecipeAdapter

        recipeTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedType = recipeTypes[position]
                filterRecipes(selectedType)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                filterRecipes("All")
            }
        }

        return view
    }

    private fun filterRecipes(type: String) {
        filteredRecipes = if (type == "All") allRecipes else allRecipes.filter { it.type == type }
        itemRecipeAdapter.updateData(filteredRecipes)
    }

    override fun onResume() {
        super.onResume()
        allRecipes = RecipeFileHandling.loadRecipes(requireContext())
        filterRecipes(recipeTypeSpinner.selectedItem?.toString() ?: "All")
    }
}
