package ru.yandex.shmr24.practice

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.internal.tasks.factory.dependsOn
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.create
import java.io.File

class TelegramReporterPlugin : Plugin<Project> {


    override fun apply(project: Project) {
        val androidComponents =
            project.extensions.findByType(AndroidComponentsExtension::class.java)
                ?: throw GradleException("Android not found")

        val extension = project.extensions.create("tgReporter", TelegramExtension::class)
        val maxSizeExtension = project.extensions.create("maximSizeOfApk", MaxSizeExtension::class)
        val telegramApi = TelegramApi(HttpClient(OkHttp))

        androidComponents.onVariants { variant ->
            val artifacts = variant.artifacts.get(SingleArtifact.APK)
            // Register validateApkSizeFor* task and set up dependencies

            val validateTask = project.tasks.register(
                "validateApkSizeFor${variant.name.capitalize()}",
                SizeApkTask::class.java
            ) {

                val maxSizeFile = File("${project.buildDir}/outputs/maximum-size.txt")
                maxSizeFile.writeText(maxSizeExtension.maxSizeMB.get().toString())

                maxSize.set(maxSizeFile)
                apkDir.set(artifacts)
                fileSize.set(File("${project.buildDir}/apk-size.txt"))
            }
            validateTask.configure {
                dependsOn(artifacts)
            }

            val telegramTask = project.tasks.register(
                "reportTelegramApkFor${variant.name.capitalize()}",
                TelegramReporterTask::class.java,
                telegramApi
            )

            telegramTask.configure {
                apkDir.set(artifacts)
                token.set(extension.token)
                chatId.set(extension.chatId)
                apkSize.set(validateTask.get().fileSize)

                val versionCodeFile = File("${project.buildDir}/outputs/versionCode.txt")
                versionCodeFile.writeText(project.version.toString())

                // project.android .defaultConfig.versionCode.toString()
                val buildVariantFile = File("${project.buildDir}/outputs/buildVariantFile.txt")
                buildVariantFile.writeText(variant.name)

                versionCode.set(versionCodeFile)
                buildVariant.set(buildVariantFile)
            }


            telegramTask.dependsOn(validateTask)
        }
    }
}

interface TelegramExtension {
    val chatId: Property<String>
    val token: Property<String>
}

interface MaxSizeExtension {
    val maxSizeMB: Property<Int>
}

