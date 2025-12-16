package com.example.ffridge.presentation.addfood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ffridge.domain.model.Food
import com.example.ffridge.domain.usecase.food.AddFoodUseCase
import com.example.ffridge.domain.usecase.food.GetFoodInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddFoodViewModel @Inject constructor(
    private val addFoodUseCase: AddFoodUseCase,
    private val getFoodInfoUseCase: GetFoodInfoUseCase
) : ViewModel() {

    data class AddFoodState(
        val isLoading: Boolean = false,
        val foundCalories: Double = 0.0,
        val foundImageUri: String? = null
    )

    sealed class UiEvent {
        object SaveSuccess : UiEvent()
        data class ShowMessage(val message: String) : UiEvent()
    }

    private val _state = MutableStateFlow(AddFoodState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun searchFoodInfo(query: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getFoodInfoUseCase(query)
                .onSuccess { food ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            foundCalories = food.calories,
                            foundImageUri = food.imageUri
                        )
                    }
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false) }
                    _uiEvent.send(UiEvent.ShowMessage(it.message ?: "Lỗi không xác định"))
                }
        }
    }

    fun saveFood(name: String, amount: String, expiryDate: Date) {
        viewModelScope.launch {
            try {
                val newFood = Food(
                    id = 0,
                    name = name,
                    amount = amount,
                    storedDate = expiryDate,
                    calories = _state.value.foundCalories,
                    imageUri = _state.value.foundImageUri
                )
                addFoodUseCase(newFood)
                _uiEvent.send(UiEvent.SaveSuccess)
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.ShowMessage(e.message ?: "Lỗi khi lưu"))
            }
        }
    }
}