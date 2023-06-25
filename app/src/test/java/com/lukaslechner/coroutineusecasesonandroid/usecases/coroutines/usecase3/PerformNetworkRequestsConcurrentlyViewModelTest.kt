package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase3

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lukaslechner.coroutineusecasesonandroid.mock.mockVersionFeaturesAndroid10
import com.lukaslechner.coroutineusecasesonandroid.mock.mockVersionFeaturesOreo
import com.lukaslechner.coroutineusecasesonandroid.mock.mockVersionFeaturesPie
import com.lukaslechner.coroutineusecasesonandroid.utils.MainCoroutineScopeRule
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.Assert.*

import org.junit.rules.TestRule

class PerformNetworkRequestsConcurrentlyViewModelTest {

    @get:Rule
    val mainCoroutineScopeRule = MainCoroutineScopeRule()

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    private val receivedUiStates = mutableListOf<UiState>()

    @Suppress("DEPRECATION")
    @Test
    fun `performNetworkRequestSequentially() should load data sequentially`() = mainCoroutineScopeRule.runBlockingTest{

        //Arrange
        val responseDelay = 1000L
        val fakeApi = FakeSuccessApi(responseDelay)
        val viewModel = PerformNetworkRequestsConcurrentlyViewModel(fakeApi)
        observeViewModel(viewModel)

        //Act
        viewModel.performNetworkRequestsSequentially()
        advanceUntilIdle()

        //Assert
        Assert.assertEquals(
            listOf(
                UiState.Loading,
                UiState.Success(
                    listOf(
                        mockVersionFeaturesOreo,
                        mockVersionFeaturesPie,
                        mockVersionFeaturesAndroid10
                    )
                )
            ), receivedUiStates
        )

        Assert.assertEquals(3000, currentTime)

    }

    @Suppress("DEPRECATION")
    @Test
    fun `performNetworkRequestsConcurrently should load data concurrently`() = mainCoroutineScopeRule.runBlockingTest{

        //Arrange
        val responseDelay = 1000L
        val fakeApi = FakeSuccessApi(responseDelay)
        val viewModel = PerformNetworkRequestsConcurrentlyViewModel(fakeApi)
        observeViewModel(viewModel)

        //Act
        viewModel.performNetworkRequestsConcurrently()
        advanceUntilIdle()

        //Assert
        Assert.assertEquals(
            listOf(
                UiState.Loading,
                UiState.Success(
                    listOf(
                        mockVersionFeaturesOreo,
                        mockVersionFeaturesPie,
                        mockVersionFeaturesAndroid10
                    )
                )
            ), receivedUiStates
        )

        Assert.assertEquals(1000, currentTime)


    }

    private fun observeViewModel(viewModel: PerformNetworkRequestsConcurrentlyViewModel) {
        viewModel.uiState().observeForever { uiState ->
            if (uiState != null) {
                receivedUiStates.add(uiState)
            }

        }
    }
}