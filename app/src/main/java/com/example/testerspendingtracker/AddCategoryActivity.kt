package com.example.testerspendingtracker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import com.example.myspendingtracker.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddCategoryActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_category)

        val rootView: View = findViewById(R.id.main)
        rootView.setOnClickListener {
            this.window.decorView.clearFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

    fun addNewCategory(view: View) {
        val labelInput: TextInputEditText = findViewById(R.id.categoryInput)
        val labelLayout: TextInputLayout = findViewById(R.id.labelLayoutCategories)

        val name = labelInput.text.toString()

        if (name.isEmpty()) {
            labelLayout.error = "Please enter a valid label"
        } else {
            val category = Category(0, name)
            insert(category)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun insert(category: Category) {
        val db = Room.databaseBuilder(
            this,
            AppDatabase::class.java, "categories"
        )
            .build()

        GlobalScope.launch {
            db.categoryDao().insertAll(category)
            finish()
        }
    }

    fun transferToCategoriesActivity(view: View) {
        val intent = Intent(this, CategoriesActivity::class.java)
        startActivity(intent)
    }

    fun transferToMainActivity(view: View) {
        finish()
    }
}