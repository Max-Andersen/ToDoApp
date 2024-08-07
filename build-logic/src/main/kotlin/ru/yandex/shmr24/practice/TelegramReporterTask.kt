package ru.yandex.shmr24.practice

import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class TelegramReporterTask @Inject constructor(
    private val telegramApi: TelegramApi
) : DefaultTask() {

    @get:InputDirectory
    abstract val apkDir: DirectoryProperty

    @get:Input
    abstract val token: Property<String>

    @get:Input
    abstract val chatId: Property<String>

    @get:InputFile
    abstract val versionCode: RegularFileProperty

    @get:InputFile
    abstract val buildVariant: RegularFileProperty


    @get:InputFile
    abstract val apkSize: RegularFileProperty

    @TaskAction
    fun report() {
        val token = token.get()
        val chatId = chatId.get()
        apkDir.get().asFile.listFiles()
            ?.filter { it.name.endsWith(".apk") }
            ?.forEach {
                runBlocking {
                    telegramApi.sendMessage(
                        "Apk size = ${
                            apkSize.get().asFile.readText().toLong()
                        }     ${buildVariant.get().asFile.readText()} + ${versionCode.get().asFile.readText()}", token, chatId
                    ).apply {
                        println(bodyAsText())
                    }
                }

                runBlocking {
                    telegramApi.upload(it, token, chatId).apply {
                        println(bodyAsText())
                    }
                }
            }
    }
}