package com.example.shoppinglisttesting.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ShoppingItem::class],
    exportSchema = false,
    version = 1
)
abstract class ShoppingItemDatabase: RoomDatabase() {

    abstract fun shoppingDao(): ShoppingDao
}