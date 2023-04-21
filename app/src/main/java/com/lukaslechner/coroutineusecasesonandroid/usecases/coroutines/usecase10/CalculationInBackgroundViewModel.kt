package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase10

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.math.BigInteger
import kotlin.system.measureTimeMillis

class CalculationInBackgroundViewModel : BaseViewModel<UiState>() {

    fun performCalculation(factorialOf: Int) {
        uiState.value = UiState.Loading

        viewModelScope.launch {
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
}