plugins {
    id("android-app-convention")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("telegram-reporter")
}

tgReporter {
    token.set(providers.environmentVariable("TG_TOKEN"))
    chatId.set(providers.environmentVariable("TG_CHAT"))
}

maximSizeOfApk{
    maxSizeMB.set(12)
}


android {
    namespace = "com.toloknov.summerschool.todoapp"

    defaultConfig {
        applicationId = "com.toloknov.summerschool.todoapp"
        versionCode = 1
        versionName = "1.0"
        // Выписан ручками на oauth.yandex.ru
        manifestPlaceholders["YANDEX_CLIENT_ID"] = "e81a1ac8f52f4a6fbe4980692627dc0e"
    }
    lint {
        baseline = file("lint-baseline.xml")
    }
}



dependencies {
    implementation(projects.domain)
    implementation(projects.core.database)
    implementation(projects.core.datastore)
    implementation(projects.core.network)
    implementation(projects.coreImpl)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Compose Navigation
    implementation(libs.androidx.navigation.compose)


    // WorkManager for periodic or single work
    implementation(libs.androidx.work.runtime.ktx)

    ksp(libs.hilt.compiler)
    ksp(libs.google.hilt.compiler)

    // Debug
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // OAuth
    implementation(libs.authsdk)

    // Splash Screen API
    implementation(libs.androidx.core.splashscreen)


    // DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    kspAndroidTest(libs.google.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.work.runtime.ktx)

}