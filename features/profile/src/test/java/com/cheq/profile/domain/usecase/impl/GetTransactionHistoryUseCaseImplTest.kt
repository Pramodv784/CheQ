package com.cheq.profile.domain.usecase.impl

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.cheq.common.mock
import com.cheq.common.whenever
import com.cheq.profile.domain.entities.TransactionHistory
import com.cheq.profile.domain.repository.TransactionHistoryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

/**
 * Created by Akash Khatkale on 19th May, 2023.
 * akash.k@cheq.one
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GetTransactionHistoryUseCaseImplTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val transactionHistoryRepository = mock<TransactionHistoryRepository>()

    private val getTransactionHistoryUseCase by lazy { GetTransactionHistoryUseCaseImpl(transactionHistoryRepository) }

    private val mockedData = mock<TransactionHistory>()

    @Test
    fun `When request is made, fetch success`() = runTest {
        whenever(transactionHistoryRepository.getTransactionHistory())
            .thenReturn(Result.success(mockedData))

        val result = getTransactionHistoryUseCase.invoke()
        Assert.assertEquals(mockedData, result.getOrNull())
    }

    @Test
    fun `When request is made, fetch failure`() = runTest {
        val exception = java.lang.Exception()
        whenever(transactionHistoryRepository.getTransactionHistory())
            .thenReturn(Result.failure(exception))

        val result = getTransactionHistoryUseCase.invoke()
        Assert.assertEquals(exception, result.exceptionOrNull())
    }
}