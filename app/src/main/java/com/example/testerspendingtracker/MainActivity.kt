package com.example.testerspendingtracker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.myspendingtracker.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var transactions: List<Transaction>
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = Room.databaseBuilder(this, AppDatabase::class.java, "transactions").build()
        transactions = arrayListOf()
        transactionAdapter = TransactionAdapter(transactions)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview_main)
        recyclerView.apply {
            adapter = transactionAdapter
        }

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.nav_home
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_transactions -> {
                    val intent = Intent(this, TransactionActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_categories -> {
                    val intent = Intent(this, CategoriesActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateDashBoard() {
        val totalAmount = transactions.map { it.amount }.sum()
        val budgetAmount = transactions.filter { it.amount > 0 }.map { it.amount }.sum()
        val expenseAmount = totalAmount - budgetAmount

        val balance: TextView = findViewById(R.id.balance)
        val budget: TextView = findViewById(R.id.budget)
        val expense: TextView = findViewById(R.id.expense)

        balance.text = "$ %.2f".format(totalAmount)
        budget.text = "$ %.2f".format(budgetAmount)
        expense.text = "$ %.2f".format(expenseAmount)

        val categoryBalances =
            transactions.groupBy { it.categoryName }.mapValues { (_, transactions) ->
                transactions.sumByDouble { it.amount }
            }

        val categoryBalancesLayout: LinearLayout = findViewById(R.id.category_balances)
        categoryBalancesLayout.removeAllViews()

        categoryBalances.forEach { (category, balance) ->
            val textView = TextView(this)
            textView.text = "Balance for category $category: ${"%.2f".format(balance)}"
            textView.setPadding(8, 8, 8, 8)
            categoryBalancesLayout.addView(textView)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchAll() {
        GlobalScope.launch {

            transactions = db.transactionDao().getAll()

            runOnUiThread {
                transactionAdapter.setData(transactions)
                updateDashBoard()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fetchAll()
        updateDashBoard();
    }

}