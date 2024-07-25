package com.toloknov.summerschool.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.toloknov.summerschool.todoapp.AppThemePreferences
import java.io.InputStream
import java.io.OutputStream

class AppThemePreferencesSerializer : Serializer<AppThemePreferences> {
    override val defaultValue: AppThemePreferences =
        AppThemePreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): AppThemePreferences {
        try {
            return AppThemePreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: AppThemePreferences, output: OutputStream) =
        t.writeTo(output)
}