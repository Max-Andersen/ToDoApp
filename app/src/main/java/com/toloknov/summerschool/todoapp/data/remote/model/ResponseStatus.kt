package com.toloknov.summerschool.todoapp.data.remote.model

sealed interface ResponseStatus {

    data object Idle : ResponseStatus

    data object InProgress : ResponseStatus

    data object Success : ResponseStatus

    data object Error : ResponseStatus

    data object NetworkUnavailable: ResponseStatus
}