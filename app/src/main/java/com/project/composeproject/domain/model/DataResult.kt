package com.project.composeproject.domain.model

sealed interface DataResult<out T> {
    data class Success<out T>(val data: T) : DataResult<T>

    data class Error(val exception: Exception) : DataResult<Nothing>

    data object Loading : DataResult<Nothing>
}