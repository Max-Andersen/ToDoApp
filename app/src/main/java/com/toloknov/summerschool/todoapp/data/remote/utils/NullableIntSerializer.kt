package com.toloknov.summerschool.todoapp.data.remote.utils

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element

object NullableIntSerializer : KSerializer<Int?> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("NullableIntSerializer") {
        element<Int>("revision", isOptional = true)
    }

    override fun serialize(encoder: Encoder, value: Int?) {
        if (value != null) {
            encoder.encodeStructure(descriptor) {
                encodeIntElement(descriptor, 0, value)
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): Int? {
        return decoder.decodeStructure(descriptor) {
            var value: Int? = null
            if (decodeSequentially()) {
                value = decodeIntElement(descriptor, 0)
            } else {
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> value = decodeIntElement(descriptor, 0)
                        -1 -> break
                        else -> throw SerializationException("Unexpected index: $index")
                    }
                }
            }
            value
        }
    }
}

