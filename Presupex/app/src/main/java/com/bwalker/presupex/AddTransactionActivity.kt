package com.bwalker.presupex

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var etAmount: EditText
    private lateinit var spCategory: Spinner
    private lateinit var etDate: EditText
    private lateinit var etDescription: EditText
    private lateinit var btnSave: Button
    private lateinit var btnBackForm: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        // Link the XML components

        etAmount = findViewById(R.id.etAmount)
        spCategory = findViewById(R.id.spCategory)
        etDate = findViewById(R.id.etDate)
        etDescription = findViewById(R.id.etDescription)
        btnSave = findViewById(R.id.btnSave)
        btnBackForm = findViewById(R.id.btnBackForm)

        // Configure Spinner with simple categories

        val categories = arrayOf("Salary", "Food", "Entertainment", "Transport")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spCategory.adapter = adapter

        // Save button (without database logic yet)

        btnSave.setOnClickListener {
            val amountText = etAmount.text.toString()
            val description = etDescription.text.toString()

            if (amountText.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Transaction saved", Toast.LENGTH_SHORT).show()
                finish() // Regresa a la pantalla anterior
            }
        }

        // Back button

        btnBackForm.setOnClickListener {
            finish()
        }
    }
}
