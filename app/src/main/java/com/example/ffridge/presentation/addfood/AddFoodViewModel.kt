package com.example.ffridge.presentation.addfood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ffridge.domain.model.Food
import com.example.ffridge.domain.usecase.food.AddFoodUseCase
import com.example.ffridge.domain.usecase.food.SearchFoodNutritionUseCase
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
    private val searchFoodNutritionUseCase: SearchFoodNutritionUseCase
) : ViewModel() {

    data class AddFoodState(
        val isLoading: Boolean = false,
        val foundCalories: Double = 0.0,
        val foundImageUri: String? = null,
        val userSelectedImageUri: String? = null
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
        if (query.isBlank()) {
            viewModelScope.launch {
                _uiEvent.send(UiEvent.ShowMessage("Vui lòng nhập tên thực phẩm"))
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            searchFoodNutritionUseCase(query)
                .onSuccess { nutritionInfo ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            foundCalories = nutritionInfo.calories,
                            foundImageUri = nutritionInfo.imageUri
                        )
                    }
                    _uiEvent.send(UiEvent.ShowMessage("Đã tìm thấy: ${nutritionInfo.calories.toInt()} kcal"))
                }
                .onFailure { exception ->
                    _state.update { it.copy(isLoading = false) }
                    _uiEvent.send(UiEvent.ShowMessage(exception.message ?: "Không tìm thấy thông tin"))
                }
        }
    }

    fun setUserSelectedImage(uri: String) {
        _state.update { it.copy(userSelectedImageUri = uri) }
    }

    fun saveFood(name: String, amount: String, expiryDate: Date) {
        viewModelScope.launch {
            try {
                val finalImageUri = _state.value.userSelectedImageUri ?: _state.value.foundImageUri

                val newFood = Food(
                    id = 0,
                    name = name,
                    amount = amount,
                    storedDate = expiryDate,
                    calories = _state.value.foundCalories,
                    imageUri = finalImageUri
                )
                addFoodUseCase(newFood)
                _uiEvent.send(UiEvent.SaveSuccess)
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.ShowMessage(e.message ?: "Lỗi khi lưu"))
            }
        }
    }
}
