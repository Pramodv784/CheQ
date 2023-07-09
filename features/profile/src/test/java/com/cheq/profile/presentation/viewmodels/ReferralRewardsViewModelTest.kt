package com.cheq.profile.presentation.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.cheq.common.mock
import com.cheq.common.models.UIState
import com.cheq.common.whenever
import com.cheq.profile.MainDispatcherRule
import com.cheq.profile.domain.entities.ReferralHistory
import com.cheq.profile.domain.repository.ReferralRepository
import com.cheq.profile.domain.usecase.GetReferralHistoryUseCase
import com.cheq.profile.domain.usecase.impl.GetReferralHistoryUseCaseImpl
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.*

/**
 * Created by Akash Khatkale on 23rd May, 2023.
 * akash.k@cheq.one
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ReferralRewardsViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private val referralRepository = mock<ReferralRepository>()
    private val getReferralHistoryUseCase: GetReferralHistoryUseCase = GetReferralHistoryUseCaseImpl(referralRepository)
    private val viewModel = ReferralRewardsViewModel(getReferralHistoryUseCase, testDispatcher)

    private val mockedReferralHistory = mock<ReferralHistory>()

    @Test
    fun `When viewmodel is instantiated, then loading state is true`() = runTest {
        assertTrue(viewModel.referralHistoryData.value is UIState.Loading)
    }

    @Test
    fun `When Referral history fetching is success, then return data`() = runTest {
        whenever(referralRepository.getReferralHistory("123"))
            .thenReturn(Result.success(mockedReferralHistory))

        viewModel.getReferralHistory("123")
        assertTrue(viewModel.referralHistoryData.value is UIState.Success)
        assertEquals(UIState.Success(mockedReferralHistory), viewModel.referralHistoryData.value)
    }

    @Test
    fun `When Referral history fetching is failed, then return error`() = runTest {
        whenever(referralRepository.getReferralHistory("234"))
            .thenReturn(Result.failure(Exception("Something went wrong")))

        viewModel.getReferralHistory("234")
        assertTrue(viewModel.referralHistoryData.value is UIState.Error)
        assertEquals("Something went wrong", (viewModel.referralHistoryData.value as UIState.Error).error.localizedMessage)
    }
}