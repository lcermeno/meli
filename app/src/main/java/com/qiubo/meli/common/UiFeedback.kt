package com.qiubo.meli.common

import androidx.annotation.StringRes

sealed class UiFeedback {
    data object Idle : UiFeedback()
    data object Loading : UiFeedback()
    data class Error(@StringRes val message: Int) : UiFeedback()
    data class Feedback(@StringRes val message: Int) : UiFeedback()
}