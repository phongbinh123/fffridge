package com.example.ffridge.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ffridge.domain.model.Food
import com.example.ffridge.domain.usecase.food.DeleteFoodUseCase
import com.example.ffridge.domain.usecase.food.GetFoodsUseCase
import com.example.ffridge.domain.usecase.food.AddFoodUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getFoodsUseCase: GetFoodsUseCase,
    private val deleteFoodUseCase: DeleteFoodUseCase,
    private val addFoodUseCase: AddFoodUseCase
) : ViewModel() {

    private val _foods = MutableStateFlow<List<Food>>(emptyList())
    val foods = _foods.asStateFlow()

    init {
        loadFoods()
    }

    private fun loadFoods() {
        viewModelScope.launch {
            getFoodsUseCase().collect {
                _foods.value = it
            }
        }
    }

    fun deleteFood(food: Food) {
        viewModelScope.launch {
            deleteFoodUseCase(food)
        }
    }

    fun updateFood(food: Food) {
        viewModelScope.launch {
            addFoodUseCase(food)  // Room sẽ update nếu ID đã tồn tại
        }
    }
}
