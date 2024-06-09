package com.example.testerspendingtracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CategoryDao {
    @Query("SELECT * from categories")
    fun getAll(): List<Category>

    @Insert
    fun insertAll(vararg category: Category)

    @Query("SELECT * FROM categories WHERE name = :categoryName LIMIT 1")
    fun getCategoryByName(categoryName: String): Category?

    @Update
    fun update(category: Category)

    @Delete
    fun delete(category: Category)


}