package com.cheq.profile.domain.usecase.impl

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.cheq.common.mock
import com.cheq.common.whenever
import com.cheq.profile.domain.entities.ReferralEarned
import com.cheq.profile.domain.repository.ReferralRepository
import com.cheq.profile.domain.usecase.GetReferralEarnedUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

/**
 * Created by Akash Khatkale on 23rd May, 2023.
 * akash.k@cheq.one
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GetReferralEarnedUseCaseImplTest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val referralRepository = mock<ReferralRepository>()

    private val getReferralEarnedUseCase by lazy { GetReferralEarnedUseCaseImpl(referralRepository) }

    private val mockedData = mock<ReferralEarned>()

    @Test
    fun `When request is made, fetch success`() = runTest{
        whenever(referralRepository.getReferralEarnedAndInvites("123"))
            .thenReturn(Result.success(mockedData))

        val response = getReferralEarnedUseCase.invoke(GetReferralEarnedUseCase.GetReferralEarnedInput("123"))
        Assert.assertEquals(mockedData, response.getOrNull())

    }

    @Test
    fun `When request is made, fetch failure`() = runTest{
        val exception = java.lang.Exception()
        whenever(referralRepository.getReferralEarnedAndInvites("123"))
            .thenReturn(Result.failure(exception))

        val response = getReferralEarnedUseCase.invoke(GetReferralEarnedUseCase.GetReferralEarnedInput("123"))
        Assert.assertEquals(exception, response.exceptionOrNull())
    }

}