package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase11

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import timber.log.Timber
import java.math.BigInteger
import kotlin.system.measureTimeMillis

class CooperativeCancellationViewModel(
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {

    private var calculationJob: Job? = null

    fun performCalculation(factorialOf: Int) {
        uiState.value = UiState.Loading

        calculationJob = viewModelScope.launch {
            try {
                var result: BigInteger = BigInteger.ZERO
                val computationDuration = measureTimeMillis {
                    result = calculateFactorialOf(factorialOf)
                }
                var resultString = ""
                var stringConversionDuration = measureTimeMillis {
                    resultString = convertToString(result)
                }
                uiState.value =
                    UiState.Success(resultString, computationDuration, stringConversionDuration)
            } catch (exception: Exception) {
                Timber.e(exception)
                uiState.value = UiState.Error("Calculation Error")
            }
        }

        calculationJob?.invokeOnCompletion { throwable ->
            if (throwable is CancellationException) {
                Timber.d("Calculation was Cancelled!")
            }
        }
    }

    fun cancelCalculation() {
        calculationJob?.cancel()
    }

    private suspend fun calculateFactorialOf(number: Int) =
        withContext(Dispatchers.Default) {
            var factorial = BigInteger.ONE
            for (i in 1..number) {
                factorial = factorial.multiply(BigInteger.valueOf(i.toLong()))
            }
            factorial
        }

    private suspend fun convertToString(number: BigInteger) =
        withContext(Dispatchers.Default) {
            number.toString()
        }


    fun uiState(): LiveData<UiState> = uiState

    private val uiState: MutableLiveData<UiState> = MutableLiveData()
}