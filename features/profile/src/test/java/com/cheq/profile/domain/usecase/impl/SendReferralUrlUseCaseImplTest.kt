package com.cheq.profile.domain.usecase.impl

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.cheq.common.mock
import com.cheq.common.whenever
import com.cheq.profile.domain.entities.ReferralUrl
import com.cheq.profile.domain.repository.ReferralRepository
import com.cheq.profile.domain.usecase.SendReferralUrlUseCase
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
class SendReferralUrlUseCaseImplTest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val referralRepository = mock<ReferralRepository>()

    private val sendReferralUrlUseCase by lazy { SendReferralUrlUseCaseImpl(referralRepository) }

    private val mockedData = mock<ReferralUrl>()

    @Test
    fun `When request is made, fetch success`() = runTest{
        whenever(referralRepository.sendReferral("123", "www.cheq.one"))
            .thenReturn(Result.success(mockedData))

        val response = sendReferralUrlUseCase.invoke(SendReferralUrlUseCase.SendReferralUrlInput("123", "www.cheq.one"))
        assertEquals(mockedData, response.getOrNull())
    }

    @Test
    fun `When request is made, fetch failure`() = runTest{
        val exception = Exception()
        whenever(referralRepository.sendReferral("123", "www.cheq.one"))
            .thenReturn(Result.failure(exception))

        val response = sendReferralUrlUseCase.invoke(SendReferralUrlUseCase.SendReferralUrlInput("123", "www.cheq.one"))
        assertEquals(exception, response.exceptionOrNull())
    }
}