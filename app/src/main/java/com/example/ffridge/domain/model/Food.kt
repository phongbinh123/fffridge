package com.example.ffridge.domain.model

import java.util.Date

data class Food(
    val id: Int = 0,
    val name: String,
    val amount: String,
    val storedDate: Date
) {
    // Logic nghiệp vụ: Tính số ngày đã lưu trữ
    fun getDaysStored(): Long {
        val diff = Date().time - storedDate.time
        return diff / (1000 * 60 * 60 * 24)
    }
}