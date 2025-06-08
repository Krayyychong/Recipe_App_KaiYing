package com.example.recipe_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemRecipeAdapter (private var recipes: List<RecipeData>, private val onRecipeClick: (RecipeData) -> Unit) : RecyclerView.Adapter<ItemRecipeAdapter.RecipeViewHolder>(){
    class RecipeViewHolder(view: View): RecyclerView.ViewHolder(view){
        val name: TextView = view.findViewById(R.id.recipeName)
        val image: ImageView = view.findViewById(R.id.recipeImage)
        val ingredients: TextView = view.findViewById(R.id.recipeIngredients)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.name.text = recipe.name
        holder.ingredients.text = recipe.ingredients.joinToString(", ")
        holder.image.setImageResource(
            holder.itemView.context.resources.getIdentifier(recipe.image,"drawable",holder.itemView.context.packageName)
        )
        holder.itemView.setOnClickListener{
            onRecipeClick(recipe) //pass clicked recipe to handler
        }
    }

    override fun getItemCount(): Int = recipes.size

    fun updateData(newRecipes: List<RecipeData>){
        recipes = newRecipes
        notifyDataSetChanged()
    }

}
