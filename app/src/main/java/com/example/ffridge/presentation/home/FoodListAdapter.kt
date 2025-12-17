package com.example.ffridge.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.ffridge.R
import com.example.ffridge.databinding.ItemFoodBinding
import com.example.ffridge.domain.model.Food

class FoodListAdapter(
    private val onDeleteClick: (Food) -> Unit,
    private val onEditClick: (Food) -> Unit
) : ListAdapter<Food, FoodListAdapter.FoodViewHolder>(FoodComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FoodViewHolder(private val binding: ItemFoodBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(food: Food) {
            binding.tvName.text = food.name

            // Hiển thị Qty + Calories
            val caloriesText = if (food.calories > 0) {
                "Qty: ${food.amount} | Cal: ${food.calories.toInt()} kcal"
            } else {
                "Qty: ${food.amount}"
            }
            binding.tvAmount.text = caloriesText

            binding.tvDate.text = android.text.format.DateFormat.format("yyyy-MM-dd", food.storedDate)

            // Load ảnh hoặc icon mặc định
            if (!food.imageUri.isNullOrEmpty()) {
                binding.imgFood.load(food.imageUri) {
                    crossfade(true)
                    placeholder(R.drawable.ic_default_food)
                    error(R.drawable.ic_default_food)
                }
            } else {
                binding.imgFood.setImageResource(R.drawable.ic_default_food)
            }

            binding.btnDelete.setOnClickListener { onDeleteClick(food) }
            binding.btnEdit.setOnClickListener { onEditClick(food) }
        }
    }

    class FoodComparator : DiffUtil.ItemCallback<Food>() {
        override fun areItemsTheSame(oldItem: Food, newItem: Food) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Food, newItem: Food) = oldItem == newItem
    }
}
