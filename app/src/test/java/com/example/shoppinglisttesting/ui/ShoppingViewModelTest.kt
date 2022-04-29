package com.example.shoppinglisttesting.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.shoppinglisttesting.MainCoroutineRule
import com.example.shoppinglisttesting.getOrAwaitValueTest
import com.example.shoppinglisttesting.other.Constants
import com.example.shoppinglisttesting.other.Status
import com.example.shoppinglisttesting.repositories.FakeShoppingRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ShoppingViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var repository: FakeShoppingRepository
    private lateinit var viewModel: ShoppingViewModel

    @Before
    fun setup() {
        repository = FakeShoppingRepository()
        viewModel = ShoppingViewModel(repository)
    }

    @Test
    fun `set current image, returns success`() {
        val url = "TEST"

        viewModel.setCurrentImageUrl(url)

        assertThat(viewModel.curImageUrl.getOrAwaitValueTest()).isEqualTo(url)
    }

    @Test
    fun `insert shopping item with empty name, returns error`() {
        viewModel.insertShoppingItem("", "5", "3.0")

        val value = viewModel.insertShoppingItemStatus.getOrAwaitValueTest()
        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert shopping item with empty amount, returns error`() {
        viewModel.insertShoppingItem("name", "", "3.0")

        val value = viewModel.insertShoppingItemStatus.getOrAwaitValueTest()
        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert shopping item with empty price, returns error`() {
        viewModel.insertShoppingItem("name", "5", "")

        val value = viewModel.insertShoppingItemStatus.getOrAwaitValueTest()
        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert shopping item with too long name, returns error`() {
        val name = buildString {
            for(i in 1..Constants.MAX_NAME_LENGTH + 1) {
                append(1)
            }
        }

        viewModel.insertShoppingItem(name, "5", "3.0")

        val value = viewModel.insertShoppingItemStatus.getOrAwaitValueTest()

        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert shopping item with too long price, returns error`() {
        val priceString = buildString {
            for(i in 1..Constants.MAX_PRICE_LENGTH + 1) {
                append(1)
            }
        }

        viewModel.insertShoppingItem("name", "5", priceString)

        val value = viewModel.insertShoppingItemStatus.getOrAwaitValueTest()

        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert shopping item with too high amount, returns error`() {
        viewModel.insertShoppingItem("name", "999999999999", "3.0")

        val value = viewModel.insertShoppingItemStatus.getOrAwaitValueTest()

        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert shopping item with valid input, returns success`() {
        viewModel.insertShoppingItem("name", "5", "3.0")

        val value = viewModel.insertShoppingItemStatus.getOrAwaitValueTest()

        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }


    @Test
    fun `insert shopping item with valid input, sets current image to empty string`() {
        viewModel.insertShoppingItem("name", "5", "3.0")

        val value = viewModel.insertShoppingItemStatus.getOrAwaitValueTest()

        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
        assertThat(viewModel.curImageUrl.getOrAwaitValueTest()).isEqualTo("")
    }

    @Test
    fun `searching for images, returns error`() {
        repository.setShouldReturnNetworkError(true)
        viewModel.searchForImage("TEST")

        val value = viewModel.images.getOrAwaitValueTest()

        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `searching for images, returns success`() {
        viewModel.searchForImage("TEST")

        val value = viewModel.images.getOrAwaitValueTest()

        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }
}