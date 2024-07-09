package com.toloknov.summerschool.todoapp.data.local.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.toloknov.summerschool.todoapp.NetworkPreferences
import java.io.InputStream
import java.io.OutputStream

class NetworkPreferencesSerializer : Serializer<NetworkPreferences> {
    override val defaultValue: NetworkPreferences =
        NetworkPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): NetworkPreferences {
        try {
            return NetworkPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: NetworkPreferences, output: OutputStream) =
        t.writeTo(output)
}