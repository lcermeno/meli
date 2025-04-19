package com.qiubo.meli.ui.dashboard

import androidx.paging.PagingData
import app.cash.turbine.test
import com.qiubo.meli.common.UiFeedback
import com.qiubo.meli.data.auth.AuthTokenProvider
import com.qiubo.meli.data.remote.model.UserResponse
import com.qiubo.meli.domain.repository.ProductRepository
import com.qiubo.meli.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    val userRepository = mockk<UserRepository>(relaxed = true)

    val productRepository = mockk<ProductRepository>(relaxed = true)

    val authManager = mockk<AuthTokenProvider>(relaxed = true)

    private lateinit var viewModel: DashboardViewModel

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = DashboardViewModel(userRepository, productRepository, authManager)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `logout should call authManager logout`() = runTest {
        // When
        viewModel.logout()
        advanceUntilIdle()

        // Then
        coVerify { authManager.logout() }
    }

    @Test
    fun `updateSearchQuery should update searchQuery state`() {
        // Given
        val query = "Samsung"

        // When
        viewModel.updateSearchQuery(query)

        // Then
        assertEquals(query, viewModel.searchQuery.value)
    }

    @Test
    fun `clearUiState should set uiFeedback to Idle`() {
        // Given
        viewModel.clearUiState()

        // Then
        assertEquals(UiFeedback.Idle, viewModel.uiState.value)
    }

    @Test
    fun `retry should emit RETRY event`() = runTest {
        val retryEventFlow = viewModel.pagingEventFlow

        retryEventFlow.test {
            // When
            viewModel.retry()

            // Then
            assertEquals(DashboardViewModel.PagingEvent.RETRY, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun `should emit error if repository fails`() = runTest {
        // Given
        coEvery { userRepository.getCurrentUser() } throws RuntimeException("User not found")

        // Start collecting pagedProducts to trigger the flow logic
        val pagedJob = launch { viewModel.pagedProducts.collect() }

        // Then
        viewModel.uiState.test {
            // Skip initial Idle
            skipItems(1)

            // Expect an error state
            assertTrue(awaitItem() is UiFeedback.Error)

            cancelAndIgnoreRemainingEvents()
        }

        // Clean up
        pagedJob.cancel()
    }



    @Test
    fun `should emit username when repository succeeds`() = runTest {
        // Given
        val fakeUser = UserResponse(id = 1, nickname = "qiubo")
        coEvery { userRepository.getCurrentUser() } returns fakeUser
        coEvery { productRepository.getUserItemsWithDetails(any()) } returns flowOf(PagingData.empty())

        // Start collecting pagedProducts to trigger the flow logic
        val pagedJob = launch { viewModel.pagedProducts.collect() }

        // When
        viewModel.retry()
        advanceUntilIdle()

        // Then
        assertEquals("qiubo", viewModel.username.value)

        // Clean up
        pagedJob.cancel()
    }
}
