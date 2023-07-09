package com.cheq.profile.presentation.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.cheq.common.mock
import com.cheq.common.models.UIState
import com.cheq.common.whenever
import com.cheq.network.errors.ErrorConstants.UNKNOWN_MESSAGE
import com.cheq.network.errors.HttpException
import com.cheq.profile.MainDispatcherRule
import com.cheq.profile.domain.entities.TransactionHistory
import com.cheq.profile.domain.entities.TransactionHistoryDetail
import com.cheq.profile.domain.repository.TransactionHistoryRepository
import com.cheq.profile.domain.usecase.impl.GetTransactionHistoryDetailsUseCaseImpl
import com.cheq.profile.domain.usecase.impl.GetTransactionHistoryUseCaseImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

/**
 * Created by Akash Khatkale on 19th May, 2023.
 * akash.k@cheq.one
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TransactionHistoryViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private val transactionHistoryRepository = mock<TransactionHistoryRepository>()
    private val getTransactionHistoryUseCase = GetTransactionHistoryUseCaseImpl(transactionHistoryRepository)
    private val getTransactionHistoryDetailsUseCase = GetTransactionHistoryDetailsUseCaseImpl(transactionHistoryRepository)
    private val viewModel = TransactionHistoryViewModel(
        getTransactionHistoryUseCase,
        getTransactionHistoryDetailsUseCase,
        testDispatcher
    )
    private val mockedTransactionData = mock<TransactionHistory>()
    private val mockedTransactionDetailData = mock<TransactionHistoryDetail>()

    private val billId = "123"
    private val cheqUniId = "456"

    @Test
    fun `When viewmodel is instantiated, then loading state is true`() = runTest {
        Assert.assertTrue(viewModel.transactionHistoryData.value is UIState.Loading)
    }

    @Test
    fun `When viewmodel is instantiated, then initial data is null`() = runTest {
        Assert.assertEquals(null, viewModel.transactionHistoryDetailData.value)
    }

    @Test
    fun `When Transaction history fetching is success, then return data`() = runTest {
        whenever(transactionHistoryRepository.getTransactionHistory())
            .thenReturn(Result.success(mockedTransactionData))

        viewModel.getTransactionHistory()
        Assert.assertTrue(viewModel.transactionHistoryData.value is UIState.Success)
        Assert.assertEquals(UIState.Success(mockedTransactionData), viewModel.transactionHistoryData.value)
    }

    @Test
    fun `When Transaction history fetching is failed, then return error`() = runTest {
        whenever(transactionHistoryRepository.getTransactionHistory())
            .thenReturn(Result.failure(HttpException(404, mapOf())))

        viewModel.getTransactionHistory()
        Assert.assertTrue(viewModel.transactionHistoryData.value is UIState.Error)
        Assert.assertEquals(UNKNOWN_MESSAGE, (viewModel.transactionHistoryData.value as UIState.Error).error.localizedMessage)
    }

    @Test
    fun `When Transaction history details fetching is success, then return data`() = runTest {
        whenever(transactionHistoryRepository.getTransactionDetails(billId, cheqUniId))
            .thenReturn(Result.success(mockedTransactionDetailData))

        Assert.assertEquals(null, viewModel.transactionHistoryDetailData.value)

        viewModel.getTransactionHistoryDetails(billId, cheqUniId)
        Assert.assertTrue(viewModel.transactionHistoryDetailData.value is UIState.Success)
        Assert.assertEquals(UIState.Success(mockedTransactionDetailData), viewModel.transactionHistoryDetailData.value)
    }

    @Test
    fun `When Transaction history details fetching is success, then return error`() = runTest {
        whenever(transactionHistoryRepository.getTransactionDetails(billId, cheqUniId))
            .thenReturn(Result.failure(HttpException(404, mapOf())))

        Assert.assertEquals(null, viewModel.transactionHistoryDetailData.value)

        viewModel.getTransactionHistoryDetails(billId, cheqUniId)
        Assert.assertTrue(viewModel.transactionHistoryDetailData.value is UIState.Error)
        Assert.assertEquals(UNKNOWN_MESSAGE, (viewModel.transactionHistoryDetailData.value as UIState.Error).error.localizedMessage)
    }
}