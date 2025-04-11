package com.example.sale

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var gridView: GridView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var products: MutableList<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sharedPreferences = getSharedPreferences("ecom_prefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "") ?: ""
        findViewById<TextView>(R.id.welcomeText).text = "Hi $username"

        gridView = findViewById(R.id.productGrid)
        products = Product.getSampleProducts()
        val adapter = ProductAdapter(this, products)
        gridView.adapter = adapter

        findViewById<Button>(R.id.viewCartButton).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }
}