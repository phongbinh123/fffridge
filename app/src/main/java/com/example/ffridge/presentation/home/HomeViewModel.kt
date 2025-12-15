package com.example.ffridge.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ffridge.domain.model.Food
import com.example.ffridge.domain.usecase.food.DeleteFoodUseCase
import com.example.ffridge.domain.usecase.food.GetFoodsUseCase
import com.example.ffridge.domain.usecase.food.SearchFoodUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getFoodsUseCase: GetFoodsUseCase,
    private val deleteFoodUseCase: DeleteFoodUseCase,
    private val searchFoodUseCase: SearchFoodUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    val foodListState: StateFlow<List<Food>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                getFoodsUseCase()
            } else {
                searchFoodUseCase(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun deleteFood(food: Food) {
        viewModelScope.launch {
            deleteFoodUseCase(food)
        }
    }
}