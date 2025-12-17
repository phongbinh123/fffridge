package com.example.ffridge.data.remote

import android.graphics.Bitmap
import com.example.ffridge.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class GeminiService @Inject constructor() {

    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    suspend fun analyzeFood(bitmap: Bitmap): FoodAnalysisResult {
        val prompt = """
            Analyze this food image and return ONLY a JSON object (no markdown, no extra text):
            {
                "name": "food name in English (e.g., Apple, Banana, Chicken)",
                "amount": "estimated quantity with unit (e.g., 2 pcs, 1 kg, 500g, 3 items)",
                "calories": estimated calories per serving (number only),
                "freshness_days": estimated days until spoilage from today (number only, e.g., 7 for fresh apple, 2 for ripe banana, 3 for chicken)
            }
            
            If no food detected, return: {"name": "Unknown", "amount": "1 pcs", "calories": 0, "freshness_days": 7}
        """.trimIndent()

        val inputContent = content {
            image(bitmap)
            text(prompt)
        }

        val response = model.generateContent(inputContent)
        val jsonText = response.text?.trim()?.removePrefix("``````")?.trim()

        return parseResponse(jsonText ?: "{}")
    }

    private fun parseResponse(jsonText: String): FoodAnalysisResult {
        return try {
            val json = JSONObject(jsonText)

            // Tính ngày hết hạn dựa trên freshness_days
            val freshnessDays = json.optInt("freshness_days", 7)
            val expiryCalendar = Calendar.getInstance()
            expiryCalendar.add(Calendar.DAY_OF_YEAR, freshnessDays)
            val expiryDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(expiryCalendar.time)

            FoodAnalysisResult(
                name = json.optString("name", "Unknown"),
                amount = json.optString("amount", "1 pcs"),
                calories = json.optDouble("calories", 0.0),
                expiryDate = expiryDate,
                imageUri = null
            )
        } catch (e: Exception) {
            // Default: 7 days from now
            val defaultCalendar = Calendar.getInstance()
            defaultCalendar.add(Calendar.DAY_OF_YEAR, 7)
            val defaultDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(defaultCalendar.time)

            FoodAnalysisResult(
                name = "Unknown",
                amount = "1 pcs",
                calories = 0.0,
                expiryDate = defaultDate,
                imageUri = null
            )
        }
    }
}
