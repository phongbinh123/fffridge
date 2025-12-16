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
    private val onRootClick: (Food) -> Unit
) : ListAdapter<Food, FoodListAdapter.FoodViewHolder>(FoodComparator()) {

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
            // Cập nhật để hiển thị calories
            binding.tvAmount.text = "Qty: ${food.amount} | Cal: ${food.calories}"
            binding.tvDate.text = android.text.format.DateFormat.format("yyyy-MM-dd", food.storedDate)

            // Sử dụng Coil để tải ảnh
            binding.imgFood.load(food.imageUri) {
                crossfade(true)
                placeholder(R.mipmap.ic_launcher) // Ảnh mặc định trong lúc tải
                error(R.mipmap.ic_launcher)       // Ảnh mặc định khi lỗi
                fallback(R.mipmap.ic_launcher)    // Ảnh mặc định nếu imageUri là null
            }

            binding.btnDelete.setOnClickListener {
                onDeleteClick(food)
            }

            binding.root.setOnClickListener {
                onRootClick(food)
                binding.radioSelect.isChecked = !binding.radioSelect.isChecked
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