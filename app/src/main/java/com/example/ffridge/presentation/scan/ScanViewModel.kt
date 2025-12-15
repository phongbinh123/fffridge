package com.example.ffridge.presentation.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ffridge.domain.usecase.food.AddFoodUseCase
import com.example.ffridge.domain.usecase.food.ScanBarcodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val scanBarcodeUseCase: ScanBarcodeUseCase,
    private val addFoodUseCase: AddFoodUseCase
) : ViewModel() {

    sealed class ScanEvent {
        data class Success(val productName: String) : ScanEvent()
        data class Error(val message: String) : ScanEvent()
        object Loading : ScanEvent()
    }

    private val _scanEvent = Channel<ScanEvent>()
    val scanEvent = _scanEvent.receiveAsFlow()

    fun onBarcodeScanned(barcode: String) {
        viewModelScope.launch {
            _scanEvent.send(ScanEvent.Loading)

            scanBarcodeUseCase(barcode)
                .onSuccess { food ->
                    // Khi quét thành công, tự động lưu vào DB luôn (hoặc có thể hiển thị dialog confirm)
                    try {
                        addFoodUseCase(food)
                        _scanEvent.send(ScanEvent.Success(food.name))
                    } catch (e: Exception) {
                        _scanEvent.send(ScanEvent.Error("Lỗi lưu DB: ${e.message}"))
                    }
                }
                .onFailure { error ->
                    _scanEvent.send(ScanEvent.Error("Không tìm thấy SP: ${error.message}"))
                }
        }
    }
}