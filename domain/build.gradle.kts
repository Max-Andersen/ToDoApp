plugins {
    id("jvm-convention")
    alias(libs.plugins.jetbrainsKotlinJvm)
}


dependencies{
    implementation(libs.kotlinx.coroutines.core)
}