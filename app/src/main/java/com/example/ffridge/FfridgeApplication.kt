package com.example.ffridge

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp // <-- QUAN TRỌNG: Dòng này kích hoạt Hilt
class FfridgeApplication : Application()