package com.cheq.profile.presentation.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.cheq.common.mock
import com.cheq.common.models.UIState
import com.cheq.common.whenever
import com.cheq.profile.MainDispatcherRule
import com.cheq.profile.domain.entities.ReferralEarned
import com.cheq.profile.domain.entities.ReferralStatic
import com.cheq.profile.domain.repository.ReferralRepository
import com.cheq.profile.domain.usecase.GetReferralEarnedUseCase
import com.cheq.profile.domain.usecase.GetReferralStaticUseCase
import com.cheq.profile.domain.usecase.SendReferralUrlUseCase
import com.cheq.profile.domain.usecase.impl.GetReferralEarnedUseCaseImpl
import com.cheq.profile.domain.usecase.impl.GetReferralStaticUseCaseImpl
import com.cheq.profile.domain.usecase.impl.SendReferralUrlUseCaseImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

/**
 * Created by Akash Khatkale on 23rd May, 2023.
 * akash.k@cheq.one
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ReferEarnViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private val referralRepository = mock<ReferralRepository>()
    private val getReferralStaticUseCase: GetReferralStaticUseCase = GetReferralStaticUseCaseImpl(referralRepository)
    private val getReferralEarnedUseCase: GetReferralEarnedUseCase = GetReferralEarnedUseCaseImpl(referralRepository)
    private val sendReferralUrlUseCase: SendReferralUrlUseCase = SendReferralUrlUseCaseImpl(referralRepository)
    private val viewModel = ReferEarnViewModel(getReferralStaticUseCase, getReferralEarnedUseCase, sendReferralUrlUseCase, testDispatcher)

    private val mockedReferralStatic = mock<ReferralStatic>()
    private val mockedReferralEarned = mock<ReferralEarned>()

    @Test
    fun `When referral static data is fetched, then return success`() = runTest {
        whenever(referralRepository.getReferralStaticData("123"))
            .thenReturn(Result.success(mockedReferralStatic))

        viewModel.getReferralStaticData("123")
        assertEquals(UIState.Success(mockedReferralStatic), viewModel.referralStaticData.value)
    }

    @Test
    fun `When referral static data fetching fails, then return null`() = runTest {
        val exception = java.lang.Exception()
        whenever(referralRepository.getReferralStaticData("123"))
            .thenReturn(Result.failure(exception))

        viewModel.getReferralStaticData("123")
        assertEquals(UIState.Error<ReferralStatic>(exception), viewModel.referralStaticData.value)
    }

    @Test
    fun `When referral earned data fetching is fetched, then return success`() = runTest {
        whenever(referralRepository.getReferralEarnedAndInvites("123"))
            .thenReturn(Result.success(mockedReferralEarned))

        viewModel.getReferralEarnedData("123")
        assertEquals(mockedReferralEarned, viewModel.referralEarnedData.value)
    }

    @Test
    fun `When referral earned data fetching fails, then return null`() = runTest {
        val exception = java.lang.Exception()
        whenever(referralRepository.getReferralEarnedAndInvites("123"))
            .thenReturn(Result.failure(exception))

        viewModel.getReferralEarnedData("123")
        assertEquals(null, viewModel.referralEarnedData.value)
    }
}