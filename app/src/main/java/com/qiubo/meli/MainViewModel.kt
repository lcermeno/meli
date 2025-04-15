package com.qiubo.meli

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qiubo.meli.common.UiFeedback
import com.qiubo.meli.data.auth.AuthTokenProvider
import com.qiubo.meli.routes.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authManager: AuthTokenProvider
) : ViewModel(), MainViewProvider {

    private val _navigationFlow = MutableStateFlow<Route>(Route.Splash)
    override val navigationFlow: StateFlow<Route> get() = _navigationFlow

    private val _uiState = MutableStateFlow<UiFeedback>(UiFeedback.Idle)
    val uiState: StateFlow<UiFeedback> get() = _uiState

    private val handler = CoroutineExceptionHandler { _, throwable ->
        Timber.e("Unexpected error: ${throwable.message}", throwable)
    }

    init {
        viewModelScope.launch(handler) {
            try {
                authManager.loadTokenIntoMemory()
                authManager.isLoggedIn.collectLatest { loggedIn ->
                    _navigationFlow.value = if (loggedIn) {
                        Route.Dashboard
                    } else {
                        Route.Login
                    }
                }
            } catch (e: Exception) {
                _uiState.value = UiFeedback.Error(R.string.generic_error)
            }
        }
    }

    fun onAuthorizationCodeReceived(code: String) {
        viewModelScope.launch(handler) {
            try {
                authManager.fetchAccessToken(code)
                authManager.loadTokenIntoMemory()
                _navigationFlow.value = Route.Dashboard
            } catch (e: Exception) {
                _uiState.value = UiFeedback.Error(R.string.login_error)
            }
        }
    }

    fun clearUiState() {
        _uiState.value = UiFeedback.Idle
    }
}
