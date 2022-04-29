package com.example.shoppinglisttesting.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.shoppinglisttesting.data.local.ShoppingItem
import com.example.shoppinglisttesting.data.remote.responses.ImageResponse
import com.example.shoppinglisttesting.other.Resource

class FakeShoppingRepository: ShoppingRepository {

    private val shoppingItems = mutableListOf<ShoppingItem>()

    private val observableShoppingItems = MutableLiveData<List<ShoppingItem>>(shoppingItems)
    private val observableTotalPrice = MutableLiveData<Float>()

    private var shouldReturnNetworkError = false

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    private fun getTotalPrice(): Float {
        return shoppingItems.sumByDouble { (it.amount * it.price).toDouble() }.toFloat()
    }

    private fun refreshLiveData() {
        observableShoppingItems.postValue(shoppingItems)
        observableTotalPrice.postValue(getTotalPrice())
    }

    override suspend fun insertShoppingItem(shoppingItem: ShoppingItem) {
        shoppingItems.add(shoppingItem)
        refreshLiveData()
    }

    override suspend fun deleteShoppingItem(shoppingItem: ShoppingItem) {
        shoppingItems.remove(shoppingItem)
        refreshLiveData()
    }

    override fun observeAllShoppingItems(): LiveData<List<ShoppingItem>> {
        return observableShoppingItems
    }

    override fun observeTotalPrice(): LiveData<Float> {
        return observableTotalPrice
    }

    override suspend fun searchForImage(searchQuery: String): Resource<ImageResponse> {
        return if(shouldReturnNetworkError) {
            Resource.error("error", null)
        } else {
            Resource.success(ImageResponse(listOf(), 0, 0))
        }
    }
}