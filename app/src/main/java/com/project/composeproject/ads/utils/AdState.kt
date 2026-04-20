package com.project.composeproject.ads.utils

sealed interface AdState {
    object Empty : AdState
    object Loading : AdState
    data class Loaded(val placement: String) : AdState
    data class Error(val message: String) : AdState
}