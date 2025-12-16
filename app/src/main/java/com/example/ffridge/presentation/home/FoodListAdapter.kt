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
    private val onEditClick: (Food) -> Unit, // <--- THÊM DÒNG NÀY (Callback Sửa)
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
            // Gán dữ liệu
            binding.tvName.text = food.name
            binding.tvAmount.text = "Qty: ${food.amount}"
            binding.tvDate.text = android.text.format.DateFormat.format("yyyy-MM-dd", food.storedDate)

            // Load ảnh (code cũ của bạn)
            binding.imgFood.load(food.imageUri) {
                crossfade(true)
                placeholder(R.mipmap.ic_launcher)
                error(R.mipmap.ic_launcher)
            }

            // --- XỬ LÝ SỰ KIỆN NÚT BẤM ---

            // 1. Nút Xóa
            binding.btnDelete.setOnClickListener {
                onDeleteClick(food)
            }

            // 2. Nút Sửa (Mới)
            binding.btnEdit.setOnClickListener {
                onEditClick(food)
            }

            // 3. Click vào cả thẻ (Xem chi tiết)
            binding.root.setOnClickListener {
                onRootClick(food)
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