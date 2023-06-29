package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

class FlowUseCase1ViewModel(
    stockPriceDataSource: StockPriceDataSource
) : BaseViewModel<UiState>() {

    val currentStockPriceAsLiveData: LiveData<UiState> = stockPriceDataSource
//        Uses as LiveData
        .latestStockList
        .map { stockList ->
            UiState.Success(stockList) as UiState
        }
        .onStart {
            emit(UiState.Loading)
        }
        .onCompletion {
            Timber.tag("Flow").d("Flow has Completed")
        }
        .asLiveData()
}

//    init {
//        Uses Collect
//        viewModelScope.launch {
//            stockPriceDataSource.latestStockList.collect { stocklist ->
//                Timber.d("Receive Item: ${stocklist.first()}")
//                currentStockPriceAsLiveData.value = UiState.Success(stocklist)
//            }
//        }

//        Uses LaunchIn
//        stockPriceDataSource
//            .latestStockList
//            .map { stockList ->
//                UiState.Success(stockList) as UiState
//            }
//            .onStart {
//                emit(UiState.Loading)
//            }
//            .onEach { uiState ->
//                currentStockPriceAsLiveData.value = uiState
//            }
//            .launchIn(viewModelScope)
//    }
