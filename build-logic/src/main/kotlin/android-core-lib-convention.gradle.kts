plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    baseAndroidConfig()
}

dependencies {
    api(libs.retrofit)
    api(libs.converter.gson)
    api(libs.plain.gson)
}
