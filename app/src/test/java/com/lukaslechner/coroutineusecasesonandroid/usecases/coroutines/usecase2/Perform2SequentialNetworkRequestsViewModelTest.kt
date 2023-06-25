package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase2

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lukaslechner.coroutineusecasesonandroid.mock.mockAndroidVersions
import com.lukaslechner.coroutineusecasesonandroid.mock.mockVersionFeaturesAndroid10
import com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase2.UiState
import com.lukaslechner.coroutineusecasesonandroid.utils.MainCoroutineScopeRule
import org.junit.*

import org.junit.rules.TestRule

class Perform2SequentialNetworkRequestsViewModelTest {

    @get:Rule
    val mainCoroutineScopeRule = MainCoroutineScopeRule()

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    private val receivedUiStates = mutableListOf<UiState>()

    @Test
    fun `should return Success when both network request are successful`() {

        //Arrange
        val fakeApi = FakeSuccessApi()
        val viewModel =
            Perform2SequentialNetworkRequestsViewModel(
                fakeApi
            )

        observeViewModel(viewModel)

        //Act
        viewModel.perform2SequentialNetworkRequest()

        //Assert
        Assert.assertEquals(
            listOf(
                UiState.Loading,
                UiState.Success(mockVersionFeaturesAndroid10)
            ), receivedUiStates
        )

    }

    @Test
    fun `Should return Error when 1st network request fails`(){

        //Arrange
        val fakeApi = FakeVersionErrorApi()
        val viewModel =
            Perform2SequentialNetworkRequestsViewModel(
                fakeApi
            )

        observeViewModel(viewModel)

        //Act
        viewModel.perform2SequentialNetworkRequest()

        //Assert
        Assert.assertEquals(
            listOf(
                UiState.Loading,
                UiState.Error("Network Request Failed!")
            ), receivedUiStates
        )
    }

    @Test
    fun `Should return Error when 2nd network request fails`(){

        //Arrange
        val fakeApi = FakeFeaturesErrorApi()
        val viewModel =
            Perform2SequentialNetworkRequestsViewModel(
                fakeApi
            )

        observeViewModel(viewModel)

        //Act
        viewModel.perform2SequentialNetworkRequest()

        //Assert
        Assert.assertEquals(
            listOf(
                UiState.Loading,
                UiState.Error("Network Request Failed!")
            ), receivedUiStates
        )
    }

    private fun observeViewModel(viewModel: Perform2SequentialNetworkRequestsViewModel) {
        viewModel.uiState().observeForever { uiState ->
            if (uiState != null) {
                receivedUiStates.add(uiState)
            }
        }
    }

}