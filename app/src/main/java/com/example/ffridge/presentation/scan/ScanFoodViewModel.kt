package com.example.ffridge.presentation.scan

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ffridge.data.remote.FoodAnalysisResult
import com.example.ffridge.data.remote.GeminiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanFoodViewModel @Inject constructor(
    private val geminiService: GeminiService
) : ViewModel() {

    private val _analysisResult = MutableStateFlow<FoodAnalysisResult?>(null)
    val analysisResult = _analysisResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun analyzeFood(bitmap: Bitmap) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = geminiService.analyzeFood(bitmap)
                _analysisResult.value = result
            } catch (e: Exception) {
                val defaultCalendar = java.util.Calendar.getInstance()
                defaultCalendar.add(java.util.Calendar.DAY_OF_YEAR, 7)
                val defaultDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(defaultCalendar.time)

                _analysisResult.value = FoodAnalysisResult(
                    name = "Error: ${e.message}",
                    amount = "0",
                    calories = 0.0,
                    expiryDate = defaultDate
                )
            } finally {
                _isLoading.value = false
            }
        }
    }
}
