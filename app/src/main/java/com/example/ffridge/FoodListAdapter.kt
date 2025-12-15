package com.example.ffridge

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ffridge.data.local.FoodItem
import com.example.ffridge.databinding.ItemFoodBinding

class FoodListAdapter(private val onDeleteClick: (FoodItem) -> Unit) :
    ListAdapter<FoodItem, FoodListAdapter.FoodViewHolder>(FoodComparator()) {

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
        fun bind(food: FoodItem) {
            binding.tvName.text = food.name
            binding.tvAmount.text = food.amount
            binding.tvDate.text = "Mua ngày: ${food.storedDate}"

            binding.btnDelete.setOnClickListener {
                onDeleteClick(food)
            }
        }
    }

    class FoodComparator : DiffUtil.ItemCallback<FoodItem>() {
        override fun areItemsTheSame(oldItem: FoodItem, newItem: FoodItem): Boolean {
            return oldItem === newItem
        }
        override fun areContentsTheSame(oldItem: FoodItem, newItem: FoodItem): Boolean {
            return oldItem.name == newItem.name && oldItem.amount == newItem.amount
        }
    }
}