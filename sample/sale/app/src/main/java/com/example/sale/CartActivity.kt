package com.example.sale

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class CartActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: android.content.SharedPreferences
    private lateinit var cartList: LinearLayout
    private lateinit var totalText: TextView
    private var totalAmount = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        sharedPreferences = getSharedPreferences("ecom_prefs", Context.MODE_PRIVATE)
        cartList = findViewById(R.id.cartList)
        totalText = findViewById(R.id.totalText)

        loadCart()

        findViewById<Button>(R.id.orderButton).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Confirm Order")
                .setMessage("Is the total bill of $$totalAmount correct?")
                .setPositiveButton("Yes") { _, _ ->
                    startActivity(Intent(this, OrderPlacedActivity::class.java))
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    private fun loadCart() {
        cartList.removeAllViews()
        totalAmount = 0.0
        val keys = sharedPreferences.all.keys

        for (key in keys) {
            if (key.endsWith("_qty")) {
                val name = key.removeSuffix("_qty")
                val qty = sharedPreferences.getInt(key, 0)
                val price = sharedPreferences.getFloat("${name}_price", 0.0f)
                if (qty > 0) {
                    val item = TextView(this)
                    val cost = qty * price
                    item.text = "$name - $qty pcs x $$price = $${"%.2f".format(cost)}"
                    cartList.addView(item)
                    totalAmount += cost
                }
            }
        }

        totalText.text = "Total: $${"%.2f".format(totalAmount)}"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            sharedPreferences.edit().clear().apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}