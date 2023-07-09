package com.cheq.profile.domain.usecase.impl

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.cheq.common.mock
import com.cheq.common.whenever
import com.cheq.profile.domain.entities.ReferralHistory
import com.cheq.profile.domain.repository.ReferralRepository
import com.cheq.profile.domain.usecase.GetReferralHistoryUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

/**
 * Created by Akash Khatkale on 23rd May, 2023.
 * akash.k@cheq.one
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GetReferralHistoryUseCaseImplTest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val referralRepository = mock<ReferralRepository>()

    private val getReferralHistoryUseCase by lazy { GetReferralHistoryUseCaseImpl(referralRepository) }

    private val mockedData = mock<ReferralHistory>()
    private val fakeData = ReferralHistory(
        httpStatus = 200,
        history = listOf(
            ReferralHistory.HistoryItem(
                name = "Akash",
                creationDate = "2023-01-18T13:52:49.107Z",
                message = listOf("You earned", "100", "Your friend earned", "100"),
                status = ReferralHistory.HistoryItem.HistoryItemStatus.SUCCESS,
                type = ReferralHistory.HistoryItem.HistoryItemType.TWO_SIDED,
            ),
            ReferralHistory.HistoryItem(
                name = "Sanket",
                creationDate = "2023-02-17T14:52:49.107Z",
                message = listOf("You earned", "100", "Your friend earned", "100"),
                status = ReferralHistory.HistoryItem.HistoryItemStatus.SUCCESS,
                type = ReferralHistory.HistoryItem.HistoryItemType.TWO_SIDED,
            ),
            ReferralHistory.HistoryItem(
                name = "Sumadhi",
                creationDate = "2023-03-17T18:52:49.107Z",
                message = listOf("Registration done, first payment of Rs 100 or more pending"),
                status = ReferralHistory.HistoryItem.HistoryItemStatus.NONE,
                type = ReferralHistory.HistoryItem.HistoryItemType.ONE_SIDED,
            )
        )
    )

    @Test
    fun `When request is made, fetch success`() = runTest{
        whenever(referralRepository.getReferralHistory("123"))
            .thenReturn(Result.success(mockedData))

        val response = getReferralHistoryUseCase.invoke(GetReferralHistoryUseCase.GetReferralHistoryInput("123"))
        assertEquals(mockedData, response.getOrNull())
    }

    @Test
    fun `When request is made, fetch failure`() = runTest{
        val exception = Exception()
        whenever(referralRepository.getReferralHistory("123"))
            .thenReturn(Result.failure(exception))

        val response = getReferralHistoryUseCase.invoke(GetReferralHistoryUseCase.GetReferralHistoryInput("123"))
        assertEquals(exception, response.exceptionOrNull())
    }


    @Test
    fun `When request is success, validate data`() = runTest{
        whenever(referralRepository.getReferralHistory("123"))
            .thenReturn(Result.success(fakeData))

        val response = getReferralHistoryUseCase.invoke(GetReferralHistoryUseCase.GetReferralHistoryInput("123"))
        response.onSuccess { data ->
            assertNotNull(data)
            assertEquals(fakeData.history.size, data?.history?.size)
            assertEquals(fakeData.history[0].status, data?.history?.get(0)?.status)
            assertEquals(fakeData.history[0].type, data?.history?.get(0)?.type)
        }
    }
}