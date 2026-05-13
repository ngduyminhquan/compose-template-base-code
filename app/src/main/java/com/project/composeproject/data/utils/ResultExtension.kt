package com.project.composeproject.data.utils

import com.project.composeproject.domain.model.DataResult

fun Throwable.toException(): Exception = this as? Exception ?: Exception(this)

fun <T> Result<T>.toDataResult(): DataResult<T> {
    return fold(
        onSuccess = { DataResult.Success(it) },
        onFailure = { DataResult.Error(it.toException()) },
    )
}

fun Result<Unit>.toUnitDataResult(): DataResult<Unit> {
    return fold(
        onSuccess = { DataResult.Success(Unit) },
        onFailure = { DataResult.Error(it.toException()) },
    )
}

fun now(): Long = System.currentTimeMillis()