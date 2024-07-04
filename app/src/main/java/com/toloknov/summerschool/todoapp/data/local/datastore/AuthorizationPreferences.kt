package com.toloknov.summerschool.todoapp.data.local.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.toloknov.summerschool.todoapp.AuthorizationPreferences
import java.io.InputStream
import java.io.OutputStream

class AuthorizationPreferencesSerializer : Serializer<AuthorizationPreferences> {
    override val defaultValue: AuthorizationPreferences =
        AuthorizationPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): AuthorizationPreferences {
        try {
            return AuthorizationPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: AuthorizationPreferences, output: OutputStream) =
        t.writeTo(output)
}