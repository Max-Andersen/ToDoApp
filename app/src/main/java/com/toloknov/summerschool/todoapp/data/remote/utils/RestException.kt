package com.toloknov.summerschool.todoapp.data.remote.utils

import java.io.IOException

// Наследуется от IOException, иначе okhttp будет троить
sealed class RestException(val code: Int) : IOException() {
    data object BadCredentials : RestException(400)

    data object NotAuthorize : RestException(401)

    data object NotFound : RestException(404)

    data class InternalServerError(override val message: String?) : RestException(500)

    data class UnexpectedRest(override val message: String?) : RestException(1000)
}