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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

        controller = TransactionController(this) // ✅ Ahora pasa context

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
            loadTransactionForEdit(intent.getIntExtra("transaction_id", -1))
        }

        // Image selector
        btnSelectImage.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        // Save button
        btnSave.setOnClickListener {
            saveTransaction(isEditMode)
        }

        btnBackForm.setOnClickListener {
            finish()
        }
    }

    // ✅ Cargar transacción para editar
    private fun loadTransactionForEdit(transactionId: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val transactions = controller.getAllTransactions()
                editingTransaction = transactions.find { it.id == transactionId }

                editingTransaction?.let { t ->
                    etAmount.setText(t.amount.toString())
                    etDescription.setText(t.description)
                    etDate.setText(t.date)

                    val categories = arrayOf("Salary", "Food", "Entertainment", "Transport", "Other")
                    val index = categories.indexOf(t.category)
                    if (index != -1) spCategory.setSelection(index)

                    selectedImage = t.image
                    imgPreview.setImageBitmap(t.image)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@AddTransactionActivity, "Error loading transaction", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveTransaction(isEditMode: Boolean) {
        val amountText = etAmount.text.toString().trim()
        val category = spCategory.selectedItem.toString()
        val date = etDate.text.toString().trim()
        val description = etDescription.text.toString().trim()

        if (amountText.isEmpty() || description.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, getString(R.string.msg_fill_fields), Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount < 0) {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            return
        }

        btnSave.isEnabled = false
        btnSave.text = "Saving..."

        val type = intent.getStringExtra("type") ?: "income"

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val success = if (isEditMode && editingTransaction != null) {
                    // Update
                    val updated = editingTransaction!!.copy(
                        amount = amount,
                        category = category,
                        description = description,
                        date = date,
                        image = selectedImage
                    )
                    controller.updateTransaction(updated)
                } else {
                    // Create new
                    val newTransaction = TransactionEntity(
                        id = 0, // El servidor asignará el ID
                        amount = amount,
                        category = category,
                        type = type,
                        description = description,
                        date = date,
                        image = selectedImage
                    )
                    controller.addTransaction(newTransaction)
                }

                withContext(Dispatchers.Main) {
                    if (success) {
                        Toast.makeText(
                            this@AddTransactionActivity,
                            if (isEditMode) getString(R.string.msg_updated) else getString(R.string.msg_saved),
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(this@AddTransactionActivity, "Error saving transaction", Toast.LENGTH_SHORT).show()
                        btnSave.isEnabled = true
                        btnSave.text = "Save"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    e.printStackTrace()
                    Toast.makeText(this@AddTransactionActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    btnSave.isEnabled = true
                    btnSave.text = "Save"
                }
            }
        }
    }

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