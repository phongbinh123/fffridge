package com.example.ffridge.presentation.recipe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.ffridge.R
import com.example.ffridge.databinding.ItemRecipeBinding
import com.example.ffridge.domain.model.Recipe

class RecipeAdapter(
    private val onRecipeClick: (Recipe) -> Unit
) : ListAdapter<Recipe, RecipeAdapter.RecipeViewHolder>(RecipeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecipeViewHolder(
        private val binding: ItemRecipeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: Recipe) {
            binding.apply {
                // Tên món
                tvRecipeTitle.text = recipe.title

                // Metadata: thời gian
                val timeText = if (recipe.cookingTime.isNotEmpty()) {
                    "⏱ ${recipe.cookingTime}"
                } else {
                    "⏱ N/A"
                }
                tvRecipeMeta.text = timeText

                // Số nguyên liệu
                tvIngredientCount.text = "🥘 ${recipe.ingredients.size} ingredients"

                // Load ảnh từ URL (internet) bằng Glide
                Glide.with(itemView.context)
                    .load(recipe.imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .transform(CenterCrop())
                    .into(ivRecipeImage)

                // Click vào item
                root.setOnClickListener {
                    onRecipeClick(recipe)
                }
            }
        }
    }

    private class RecipeDiffCallback : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem == newItem
        }
    }
}
