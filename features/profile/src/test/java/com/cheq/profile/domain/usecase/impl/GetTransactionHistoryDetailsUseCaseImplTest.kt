package com.cheq.profile.domain.usecase.impl

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.cheq.common.mock
import com.cheq.common.whenever
import com.cheq.profile.domain.entities.TransactionHistoryDetail
import com.cheq.profile.domain.repository.TransactionHistoryRepository
import com.cheq.profile.domain.usecase.GetTransactionHistoryDetailsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

/**
 * Created by Akash Khatkale on 22nd May, 2023.
 * akash.k@cheq.one
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GetTransactionHistoryDetailsUseCaseImplTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val transactionHistoryRepository = mock<TransactionHistoryRepository>()

    private val getTransactionHistoryDetailUseCase by lazy { GetTransactionHistoryDetailsUseCaseImpl(transactionHistoryRepository) }

    private val mockedData = mock<TransactionHistoryDetail>()
    private val mockedInputData = mock<GetTransactionHistoryDetailsUseCase.GetTransactionHistoryDetailsInput>()


    @Test
    fun `When request is made, fetch success`() = runTest {
        whenever(transactionHistoryRepository.getTransactionDetails(mockedInputData.billId, mockedInputData.cheqUniTransactionId))
            .thenReturn(Result.success(mockedData))

        val result = getTransactionHistoryDetailUseCase.invoke(
            mockedInputData
        )
        Assert.assertEquals(mockedData, result.getOrNull())
    }

    @Test
    fun `When request is made, fetch failure`() = runTest {
        val exception = java.lang.Exception()
        whenever(transactionHistoryRepository.getTransactionDetails(mockedInputData.billId, mockedInputData.cheqUniTransactionId))
            .thenReturn(Result.failure(exception))

        val result = getTransactionHistoryDetailUseCase.invoke(
            mockedInputData
        )
        Assert.assertEquals(exception, result.exceptionOrNull())
    }

}