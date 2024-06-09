package com.example.testerspendingtracker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.myspendingtracker.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CategoriesActivity : AppCompatActivity() {
    private lateinit var deletedCategories: Category
    private lateinit var categories: List<Category>
    private lateinit var oldCategories: List<Category>
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_categories)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.categories)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = Room.databaseBuilder(this, AppDatabase::class.java, "categories")
            .build()

        categories = arrayListOf()
        categoryAdapter = CategoryAdapter(categories)
        linearLayoutManager = LinearLayoutManager(this)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_categories)
        recyclerView.apply {
            adapter = categoryAdapter
            layoutManager = linearLayoutManager
        }

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation_categories)
        bottomNavigation.selectedItemId = R.id.nav_categories
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_transactions -> {
                    val intent = Intent(this, TransactionActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }


        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteTransaction(categories[viewHolder.adapterPosition])
            }

        }

        val swipeHelper = ItemTouchHelper(itemTouchHelper)
        swipeHelper.attachToRecyclerView(recyclerView)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun deleteTransaction(category: Category) {
        deletedCategories = category
        oldCategories = categories

        GlobalScope.launch {
            db.categoryDao().delete(category)

            categories = categories.filter { it.id != category.id }
            runOnUiThread {
                categoryAdapter.setData(categories)
                showSnackBar()
            }

        }
    }

    private fun showSnackBar() {

        val view = findViewById<View>(R.id.categories)
        val snackbar = Snackbar.make(view, "Category deleted", Snackbar.LENGTH_LONG)
        snackbar.setAction("Undo") {
            undoDelete()
        }
            .setActionTextColor(ContextCompat.getColor(this, R.color.red))
            .setTextColor(ContextCompat.getColor(this, R.color.red))
            .show()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun undoDelete() {
        GlobalScope.launch { db.categoryDao().insertAll(deletedCategories) }

        categories = oldCategories

        runOnUiThread {
            categoryAdapter.setData(categories)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchAll() {
        GlobalScope.launch {

            categories = db.categoryDao().getAll()

            runOnUiThread {
                categoryAdapter.setData(categories)
            }
        }
    }

    fun transferToAddCategoryActivity(view: View) {
        val intent = Intent(this, AddCategoryActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        fetchAll()
    }

}