package com.example.ffridge.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ffridge.domain.model.Food
import com.example.ffridge.databinding.ItemFoodBinding

class FoodListAdapter(private val onDeleteClick: (Food) -> Unit) :
    ListAdapter<Food, FoodListAdapter.FoodViewHolder>(FoodComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    inner class FoodViewHolder(private val binding: ItemFoodBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(food: Food) {
            binding.tvName.text = food.name
            binding.tvAmount.text = food.amount
            binding.tvDate.text = "Mua ngày: ${food.storedDate}"

            binding.btnDelete.setOnClickListener {
                onDeleteClick(food)
            }
        }
    }

    class FoodComparator : DiffUtil.ItemCallback<Food>() {
        override fun areItemsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem == newItem
        }
    }
}