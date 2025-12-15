// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // 1. Android Application Plugin
    id("com.android.application") version "8.13.2" apply false

    // 2. Kotlin Android Plugin (Sửa lỗi 'jetbrains')
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false

    // 3. KSP (Kotlin Symbol Processing) Plugin
    id("com.google.devtools.ksp") version "2.0.0-1.0.22" apply false
}