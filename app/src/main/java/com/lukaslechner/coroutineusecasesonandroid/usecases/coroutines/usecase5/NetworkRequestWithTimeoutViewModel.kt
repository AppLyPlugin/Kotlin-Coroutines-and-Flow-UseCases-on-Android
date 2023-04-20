package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase5

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import java.sql.Time

class NetworkRequestWithTimeoutViewModel(
    private val api: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun performNetworkRequest(timeout: Long) {
        uiState.value = UiState.Loading

        //usingWithTimeout(timeout)
        usingWithTimeoutOrNull(timeout)
    }

    private fun usingWithTimeout(timeout: Long) {
        viewModelScope.launch {
            try {
                val recentAndroidVersion = withTimeout(timeout) {
                    api.getRecentAndroidVersions()
                }
                if (recentAndroidVersion != null) {
                    uiState.value = UiState.Success(recentAndroidVersion)
                } else {
                    uiState.value = UiState.Error("Network Request Failed!")
                }
            } catch (timeoutCancellationException: TimeoutCancellationException) {
                Timber.e(timeoutCancellationException)
                uiState.value = UiState.Error("Network Request Timeout!")
            } catch (exception: Exception) {
                Timber.e(exception)
                uiState.value = UiState.Error("Network Request Failed!")
            }
        }
    }

    private fun usingWithTimeoutOrNull(timeout: Long) {
        viewModelScope.launch {
            try {
                val recentAndroidVersion = withTimeoutOrNull(timeout) {
                    api.getRecentAndroidVersions()
                }
                if (recentAndroidVersion != null) {
                    uiState.value = UiState.Success(recentAndroidVersion)
                } else {
                    uiState.value = UiState.Error("Network Request Failed!")
                }
            } catch (exception: Exception) {
                Timber.e(exception)
                uiState.value = UiState.Error("Network Request Failed!")
            }
        }
    }

}
