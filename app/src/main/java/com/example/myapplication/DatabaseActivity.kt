package com.example.myapplication

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Activity for demonstrating SQLite database operations.
 * Allows user to add, update, delete, and view records.
 */
class DatabaseActivity : AppCompatActivity() {

    // Database objects
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var database: SQLiteDatabase

    // UI components
    private lateinit var nameInput: EditText
    private lateinit var ageInput: EditText
    private lateinit var addButton: Button
    private lateinit var updateButton: Button
    private lateinit var deleteButton: Button
    private lateinit var viewButton: Button
    private lateinit var resultText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_database)

        // Initialize database
        initializeDatabase()

        // Initialize UI components
        initializeViews()

        // Set click listeners
        setClickListeners()
    }

    /**
     * Initialize database objects.
     */
    private fun initializeDatabase() {
        dbHelper = DatabaseHelper(this)
        database = dbHelper.writableDatabase
    }

    /**
     * Initialize UI component references.
     */
    private fun initializeViews() {
        nameInput = findViewById(R.id.nameInput)
        ageInput = findViewById(R.id.ageInput)
        addButton = findViewById(R.id.addButton)
        updateButton = findViewById(R.id.updateButton)
        deleteButton = findViewById(R.id.deleteButton)
        viewButton = findViewById(R.id.viewButton)
        resultText = findViewById(R.id.resultText)
    }

    /**
     * Set click listeners for buttons.
     */
    private fun setClickListeners() {
        addButton.setOnClickListener { addData() }
        updateButton.setOnClickListener { updateData() }
        deleteButton.setOnClickListener { deleteData() }
        viewButton.setOnClickListener { viewData() }
    }

    /**
     * Add new user data to the database.
     */
    private fun addData() {
        val name = nameInput.text.toString().trim()
        val ageText = ageInput.text.toString().trim()

        if (validateInput(name, ageText)) {
            val age = ageText.toInt()

            val values = ContentValues().apply {
                put(DatabaseHelper.COLUMN_NAME, name)
                put(DatabaseHelper.COLUMN_AGE, age)
            }

            val newRowId = database.insert(DatabaseHelper.TABLE_USERS, null, values)

            if (newRowId != -1L) {
                showToast("Data Inserted Successfully")
                clearInputFields()
            } else {
                showToast("Error Inserting Data")
            }
        }
    }

    /**
     * Update existing user data in the database.
     */
    private fun updateData() {
        val name = nameInput.text.toString().trim()
        val ageText = ageInput.text.toString().trim()

        if (validateInput(name, ageText)) {
            val age = ageText.toInt()

            val values = ContentValues().apply {
                put(DatabaseHelper.COLUMN_AGE, age)
            }

            val selection = "${DatabaseHelper.COLUMN_NAME} = ?"
            val selectionArgs = arrayOf(name)

            val count = database.update(
                DatabaseHelper.TABLE_USERS,
                values,
                selection,
                selectionArgs
            )

            if (count > 0) {
                showToast("$count Record(s) Updated")
            } else {
                showToast("No Records Found to Update")
            }
        }
    }

    /**
     * Delete user data from the database.
     */
    private fun deleteData() {
        val name = nameInput.text.toString().trim()

        if (name.isEmpty()) {
            showToast("Please Enter a Name")
            return
        }

        val selection = "${DatabaseHelper.COLUMN_NAME} = ?"
        val selectionArgs = arrayOf(name)

        val deletedRows = database.delete(
            DatabaseHelper.TABLE_USERS,
            selection,
            selectionArgs
        )

        if (deletedRows > 0) {
            showToast("$deletedRows Record(s) Deleted")
            clearInputFields()
        } else {
            showToast("No Records Found to Delete")
        }
    }

    /**
     * Query and display all user data from the database.
     */
    private fun viewData() {
        val cursor = querySortedUsers()
        displayQueryResults(cursor)
        cursor.close()
    }

    /**
     * Query all users sorted by ID.
     * @return Cursor containing query results
     */
    private fun querySortedUsers(): Cursor {
        return database.query(
            DatabaseHelper.TABLE_USERS,   // Table name
            null,                         // All columns
            null,                         // No WHERE clause
            null,                         // No selection args
            null,                         // No grouping
            null,                         // No having
            "${DatabaseHelper.COLUMN_ID} ASC"  // Order by ID ascending
        )
    }

    /**
     * Display query results in the UI.
     * @param cursor Database query results cursor
     */
    private fun displayQueryResults(cursor: Cursor) {
        val resultBuilder = StringBuilder()

        if (cursor.count > 0) {
            resultBuilder.append("USER RECORDS:\n\n")

            val idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)
            val nameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)
            val ageIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_AGE)

            while (cursor.moveToNext()) {
                val id = if (idIndex != -1) cursor.getInt(idIndex) else "N/A"
                val name = if (nameIndex != -1) cursor.getString(nameIndex) else "N/A"
                val age = if (ageIndex != -1) cursor.getInt(ageIndex) else 0

                resultBuilder.append("ID: $id | Name: $name | Age: $age\n")
            }
        } else {
            resultBuilder.append("No records found in database.")
        }

        resultText.text = resultBuilder.toString()
    }

    /**
     * Validate user input for database operations.
     * @param name User name input
     * @param ageText User age input
     * @return True if input is valid, false otherwise
     */
    private fun validateInput(name: String, ageText: String): Boolean {
        if (name.isEmpty()) {
            showToast("Please Enter a Name")
            return false
        }

        if (ageText.isEmpty()) {
            showToast("Please Enter an Age")
            return false
        }

        return try {
            ageText.toInt()
            true
        } catch (e: NumberFormatException) {
            showToast("Age Must Be a Number")
            false
        }
    }

    /**
     * Clear input fields after operations.
     */
    private fun clearInputFields() {
        nameInput.text.clear()
        ageInput.text.clear()
    }

    /**
     * Show a toast message.
     * @param message Message to display
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}