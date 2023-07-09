package com.cheq.profile.domain.usecase.impl

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.cheq.common.mock
import com.cheq.common.whenever
import com.cheq.profile.domain.entities.ReferralStatic
import com.cheq.profile.domain.repository.ReferralRepository
import com.cheq.profile.domain.usecase.GetReferralStaticUseCase
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
class GetReferralStaticUseCaseImplTest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val referralRepository = mock<ReferralRepository>()

    private val getReferralStaticUseCase by lazy { GetReferralStaticUseCaseImpl(referralRepository) }

    private val mockedData = mock<ReferralStatic>()

    @Test
    fun `When request is made, fetch success`() = runTest{
        whenever(referralRepository.getReferralStaticData("123"))
            .thenReturn(Result.success(mockedData))

        val response = getReferralStaticUseCase.invoke(GetReferralStaticUseCase.GetReferralStaticInput("123"))
        assertEquals(mockedData, response.getOrNull())
    }

    @Test
    fun `When request is made, fetch failure`() = runTest{
        val exception = Exception()
        whenever(referralRepository.getReferralStaticData("123"))
            .thenReturn(Result.failure(exception))

        val response = getReferralStaticUseCase.invoke(GetReferralStaticUseCase.GetReferralStaticInput("123"))
        assertEquals(exception, response.exceptionOrNull())
    }
}