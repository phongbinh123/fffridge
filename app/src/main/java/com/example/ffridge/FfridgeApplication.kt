package com.example.ffridge

import android.app.Application
import com.example.ffridge.data.local.AppDatabase

class FfridgeApplication : Application() {
    // Khởi tạo database lười (lazy) - chỉ tạo khi cần dùng
    val database by lazy { AppDatabase.getDatabase(this) }
}