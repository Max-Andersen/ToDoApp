package ru.yandex.shmr24.practice

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class SizeApkTask : DefaultTask() {

    @get:InputDirectory
    abstract val apkDir: DirectoryProperty

    @get:InputFile
    abstract val maxSize: RegularFileProperty

    @get:OutputFile
    abstract val fileSize: RegularFileProperty

    @TaskAction
    fun execute() {
        val file = apkDir.get().asFile.listFiles()
            ?.filter { it.name.endsWith(".apk") }
            ?.firstOrNull() ?: throw GradleException("APK file not found")

        val size = file.length() / 1024 / 1024
        println("APK Size: $size bytes  max: ")
        if (size > maxSize.get().asFile.readText().toLong()) {
            throw GradleException(
                "Max apk size - ${
                    maxSize.get().asFile.readText().toLong()
                } MB  (current - $size )"
            )
        }
        // Ensure the directory exists
        val fileOut = fileSize.asFile.get()
        val parentFile = fileOut.parentFile
        if (!parentFile.exists()) {
            parentFile.mkdirs()
        }
        fileSize.asFile.get().writeText(size.toString())
    }
}