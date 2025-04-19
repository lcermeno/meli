package com.qiubo.meli.ui.product

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.qiubo.meli.common.UiFeedback
import com.qiubo.meli.data.remote.model.ItemDetail
import com.qiubo.meli.data.remote.model.Picture
import com.qiubo.meli.domain.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class ProductViewModelTest {

    private val productRepository: ProductRepository = mockk()
    private lateinit var viewModel: ProductViewModel
    private lateinit var savedStateHandle: SavedStateHandle

    @BeforeEach
    fun setUp() {
        savedStateHandle = SavedStateHandle(mapOf("productId" to "123"))
    }

    @Test
    fun `fetchProduct should update product state with view data`(): Unit = runTest {
        // Given
        val itemDetail = ItemDetail(
            id = "123",
            siteId = "MLA",
            title = "Test Product",
            price = 999.99,
            currencyId = "ARS",
            availableQuantity = 10,
            soldQuantity = 5,
            condition = "new",
            permalink = "http://example.com/product",
            thumbnail = "http://example.com/thumb.jpg",
            pictures = listOf(
                Picture(
                    "1",
                    "http://example.com/img1.jpg",
                    "http://example.com/img1.jpg"
                )
            ),
            saleTerms = emptyList(),
            attributes = emptyList()
        )

        coEvery { productRepository.getItemById("123") } returns itemDetail

        // When
        viewModel = ProductViewModel(productRepository, savedStateHandle)

        // Then
        val product = viewModel.product.first()
        assertEquals("123", product?.id)
        assertEquals("Test Product", product?.name)
        assertEquals("http://example.com/product", product?.description)
        assertEquals("http://example.com/thumb.jpg", product?.imageUrl)
        assertEquals(1, product?.pictures?.size)
    }

    @Test
    fun `fetchProduct should emit error on exception`() = runTest {
        // Given
        coEvery { productRepository.getItemById("123") } throws RuntimeException("Network error")

        // When
        viewModel = ProductViewModel(productRepository, savedStateHandle)

        // Then
        viewModel.uiFeedback.test {
            assertTrue(awaitItem() is UiFeedback.Error)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
