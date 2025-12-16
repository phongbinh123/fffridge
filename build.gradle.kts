// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // 1. Android Application Plugin
    id("com.android.application") version "8.13.2" apply false

    // 2. Kotlin Android Plugin (Sửa lỗi 'jetbrains')
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false

    // 3. KSP (Kotlin Symbol Processing) Plugin
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false

    id("com.google.dagger.hilt.android") version "2.48" apply false

    kotlin("kapt") version "1.9.22" apply false
}