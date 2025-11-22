package com.bwalker.presupex

import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bwalker.presupex.controller.TransactionController
import com.bwalker.presupex.data.TransactionEntity
import com.bwalker.presupex.manager.DataProvider
import java.util.*

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var etAmount: EditText
    private lateinit var spCategory: Spinner
    private lateinit var etDate: EditText
    private lateinit var etDescription: EditText
    private lateinit var btnSave: Button
    private lateinit var btnBackForm: Button
    private lateinit var tvAddTitle: TextView

    private lateinit var imgPreview: ImageView
    private lateinit var btnSelectImage: Button

    private lateinit var controller: TransactionController
    private var editingTransaction: TransactionEntity? = null

    private var selectedImage: Bitmap? = null

    // Gallery launcher
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            selectedImage = bitmap
            imgPreview.setImageBitmap(bitmap)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        controller = TransactionController(DataProvider.sharedDataManager)

        // Link UI components
        etAmount = findViewById(R.id.etAmount)
        spCategory = findViewById(R.id.spCategory)
        etDate = findViewById(R.id.etDate)
        etDescription = findViewById(R.id.etDescription)
        btnSave = findViewById(R.id.btnSave)
        btnBackForm = findViewById(R.id.btnBackForm)
        tvAddTitle = findViewById(R.id.tvAddTitle)

        imgPreview = findViewById(R.id.imgPreview)
        btnSelectImage = findViewById(R.id.btnSelectImage)

        // Spinner categories
        val categories = arrayOf("Salary", "Food", "Entertainment", "Transport", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCategory.adapter = adapter

        // Date picker
        etDate.setOnClickListener { showDatePickerDialog() }

        // Edit mode check
        val isEditMode = intent.hasExtra("transaction_id")
        if (isEditMode) {
            tvAddTitle.text = getString(R.string.edit_transaction)

            val id = intent.getIntExtra("transaction_id", -1)
            val allTransactions = controller.getAllTransactions()
            editingTransaction = allTransactions.find { it.id == id }

            editingTransaction?.let { t ->
                etAmount.setText(t.amount.toString())
                etDescription.setText(t.description)
                etDate.setText(t.date)

                val index = categories.indexOf(t.category)
                if (index != -1) spCategory.setSelection(index)

                selectedImage = t.image
                imgPreview.setImageBitmap(t.image)
            }
        }

        // Image selector
        btnSelectImage.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        // Save button
        btnSave.setOnClickListener {
            val amountText = etAmount.text.toString().trim()
            val category = spCategory.selectedItem.toString()
            val date = etDate.text.toString().trim()
            val description = etDescription.text.toString().trim()

            if (amountText.isEmpty() || description.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, getString(R.string.msg_fill_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountText.toDoubleOrNull()
            if (amount == null || amount < 0) {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val type = intent.getStringExtra("type") ?: "income"

            if (isEditMode && editingTransaction != null) {
                // Update transaction
                val updated = editingTransaction!!.copy(
                    amount = amount,
                    category = category,
                    description = description,
                    date = date,
                    image = selectedImage
                )
                controller.updateTransaction(updated)
                Toast.makeText(this, getString(R.string.msg_updated), Toast.LENGTH_SHORT).show()
            } else {
                // Create new transaction
                val newTransaction = TransactionEntity(
                    id = Random().nextInt(100000),
                    amount = amount,
                    category = category,
                    type = type,
                    description = description,
                    date = date,
                    image = selectedImage
                )
                controller.addTransaction(newTransaction)
                Toast.makeText(this, getString(R.string.msg_saved), Toast.LENGTH_SHORT).show()
            }

            finish()
        }

        btnBackForm.setOnClickListener {
            finish()
        }
    }

    // Date Picker
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(
            this,
            { _, y, m, d ->
                val formattedDate = String.format("%02d/%02d/%04d", d, m + 1, y)
                etDate.setText(formattedDate)
            },
            year, month, day
        )
        dialog.show()
    }
}
