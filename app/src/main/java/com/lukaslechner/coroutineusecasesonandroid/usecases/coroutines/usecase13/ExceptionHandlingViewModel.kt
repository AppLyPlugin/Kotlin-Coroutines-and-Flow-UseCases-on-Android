package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase13

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import com.lukaslechner.coroutineusecasesonandroid.mock.VersionFeatures
import kotlinx.coroutines.*
import retrofit2.HttpException
import timber.log.Timber

class ExceptionHandlingViewModel(
    private val api: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun handleExceptionWithTryCatch() {
        uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                api.getAndroidVersionFeatures(27)
            } catch (exception: Exception) {
                uiState.value = UiState.Error("Network Request Failed!")
            }
        }
    }

    fun handleWithCoroutineExceptionHandler() {
        uiState.value = UiState.Loading

        val courotineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            uiState.value = UiState.Error("Network Request Failed!")
        }
        viewModelScope.launch(courotineExceptionHandler) {
            api.getAndroidVersionFeatures(27)
        }
    }

    fun showResultsEvenIfChildCoroutineFails() {
        uiState.value = UiState.Loading

        viewModelScope.launch {
            supervisorScope {
                val oreoFeaturesDeferred = async {
                    api.getAndroidVersionFeatures(27)
                }

                val pieFeaturesDeferred = async {
                    api.getAndroidVersionFeatures(28)
                }

                val android10FeaturesDeferred = async {
                    api.getAndroidVersionFeatures(29)
                }
//
//                val oreoFeatures = try {
//                    oreoFeaturesDeferred.await()
//                } catch (exception: Exception) {
//                    Timber.e("Error Loading Oreo Features")
//
//                }
//
//                val pieFeatures = try {
//                    pieFeaturesDeferred.await()
//                } catch (exception: Exception) {
//                    Timber.e("Error Loading Oreo Features")
//                    null
//                }
//
//                val android10Features = try {
//                    android10FeaturesDeferred.await()
//                } catch (exception: Exception) {
//                    Timber.e("Error Loading Oreo Features")
//                    null
//                }

                val versionFeatures = listOfNotNull(
                    oreoFeaturesDeferred,
                    pieFeaturesDeferred,
                    android10FeaturesDeferred
                ).mapNotNull {
                    try {
                        it.await()
                    } catch (exception: Exception) {
                        if(exception is CancellationException){
                            throw exception
                        }

                        Timber.e("Error Network Request")
                        null
                    }
                }

                uiState.value = UiState.Success(versionFeatures)
            }



        }
    }
}
