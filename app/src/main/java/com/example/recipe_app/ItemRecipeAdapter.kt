package com.example.recipe_app

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemRecipeAdapter(private var recipes: List<RecipeEntity>, private val onRecipeClick: (RecipeEntity) -> Unit) : RecyclerView.Adapter<ItemRecipeAdapter.RecipeViewHolder>(){
    class RecipeViewHolder(view: View): RecyclerView.ViewHolder(view){
        val name: TextView = view.findViewById(R.id.recipeName)
        val image: ImageView = itemView.findViewById(R.id.recipeImage)
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

        // Load image based on URI or drawable resource
        if (recipe.image.isNotEmpty()) {
            //Check if the image is a URI (from stimulator gallery)
            if (recipe.image.startsWith("content://")) {
                val imageUri = Uri.parse(recipe.image)
                holder.image.setImageURI(imageUri)
            } else {
                val context = holder.itemView.context
                val imageResId = context.resources.getIdentifier(recipe.image, "drawable", context.packageName)
                if (imageResId != 0) {
                    holder.image.setImageResource(imageResId)
                } else {
                    holder.image.setImageResource(R.drawable.food)
                }
            }
        } else {
            holder.image.setImageResource(R.drawable.food)
        }


        holder.itemView.setOnClickListener{
            onRecipeClick(recipe) //pass clicked recipe to handler
        }
    }

    override fun getItemCount(): Int = recipes.size

    fun updateData(newRecipes: List<RecipeEntity>){
        recipes = newRecipes
        notifyDataSetChanged()
    }

}
