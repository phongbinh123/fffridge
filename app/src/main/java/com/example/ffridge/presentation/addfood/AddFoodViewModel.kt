package com.example.ffridge.presentation.addfood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ffridge.domain.model.Food
import com.example.ffridge.domain.usecase.food.AddFoodUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddFoodViewModel @Inject constructor(
    private val addFoodUseCase: AddFoodUseCase
) : ViewModel() {

    // Kênh để gửi sự kiện 1 lần (Event) tới UI (VD: Toast, Finish screen)
    sealed class UiEvent {
        object SaveSuccess : UiEvent()
        data class ShowError(val message: String) : UiEvent()
    }

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun saveFood(name: String, amount: String) {
        viewModelScope.launch {
            try {
                // Tạo object Food từ input (Domain model)
                val newFood = Food(
                    name = name,
                    amount = amount,
                    storedDate = Date()
                )
                // Gọi UseCase
                addFoodUseCase(newFood)
                _uiEvent.send(UiEvent.SaveSuccess)
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.ShowError(e.message ?: "Lỗi không xác định"))
            }
        }
    }
}