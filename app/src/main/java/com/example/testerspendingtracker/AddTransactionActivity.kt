package com.example.testerspendingtracker

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.example.myspendingtracker.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.log
import android.util.Log
import kotlinx.coroutines.CoroutineScope

class AddTransactionActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Room.databaseBuilder(this, AppDatabase::class.java, "categories").build()
        setContentView(R.layout.activity_add_transaction)

        val rootView: View = findViewById(R.id.main)
        rootView.setOnClickListener {
            this.window.decorView.clearFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }


        val labelLayout: TextInputLayout = findViewById(R.id.labelLayout)
        val labelInput: TextInputEditText = findViewById(R.id.labelInput)
        val amountInput: TextInputEditText = findViewById(R.id.amountInput)


        labelInput.addTextChangedListener {
            if (it!!.count() > 0) {
                labelLayout.error = null
            }
        }

        amountInput.addTextChangedListener {
            if (it!!.count() > 0) {
                labelLayout.error = null
            }
        }

        val autoCompleteTextView: AutoCompleteTextView = findViewById(R.id.autoCompleteTextView)
        autoCompleteTextView.inputType = InputType.TYPE_NULL
        GlobalScope.launch(Dispatchers.IO) {
            val all = db.categoryDao().getAll()
            withContext(Dispatchers.Main) {
                // Update UI with the result
                val allString = all.toString()
                val options = allString.substring(1 until allString.length - 1).split(",");
                val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                    this@AddTransactionActivity,
                    android.R.layout.simple_dropdown_item_1line,
                    options
                )
                autoCompleteTextView.isFocusable = true
                autoCompleteTextView.isFocusableInTouchMode = true
                autoCompleteTextView.setAdapter(adapter)
            }
        }

        autoCompleteTextView.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                (v as AutoCompleteTextView).showDropDown()
            }
        }


        enableEdgeToEdge()


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun addNewTransaction(view: View) {
        val labelInput: TextInputEditText = findViewById(R.id.labelInput)
        val amountInput: TextInputEditText = findViewById(R.id.amountInput)
        val labelLayout: TextInputLayout = findViewById(R.id.labelLayout)
        val amountLayout: TextInputLayout = findViewById(R.id.amountLayout)
        val categoryInput: AutoCompleteTextView = findViewById(R.id.autoCompleteTextView)

        val label = labelInput.text.toString()
        val amount = amountInput.text.toString().toDoubleOrNull()

        if (label.isEmpty()) {
            labelLayout.error = "Please enter a valid label"
        } else if (amount == null) {
            amountLayout.error = "Please enter a valid amount"
        } else if (categoryInput.text.isEmpty()) {
            categoryInput.error = "Please enter a valid category or add new one"
        } else {
            GlobalScope.launch(Dispatchers.IO) {
                val transaction =
                    Transaction(0, label, amount, categoryInput.text.toString())

                withContext(Dispatchers.Main) {
                    insert(transaction)
                }
            }
        }
    }

    fun transferToMainActivity(view: View) {
        finish()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun insert(transaction: Transaction) {
        val db = Room.databaseBuilder(
            this, AppDatabase::class.java, "transactions"
        ).build()


        GlobalScope.launch {
            db.transactionDao().insert(transaction)
        }

        finish()
    }

}