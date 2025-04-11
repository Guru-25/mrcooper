package com.example.sale

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class ProductAdapter(private val context: Context, private val productList: MutableList<Product>) :
    BaseAdapter() {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("ecom_prefs", Context.MODE_PRIVATE)

    override fun getCount(): Int = productList.size

    override fun getItem(position: Int): Any = productList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_product, parent, false)
        val product = productList[position]

        val nameView = view.findViewById<TextView>(R.id.productName)
        val priceView = view.findViewById<TextView>(R.id.productPrice)
        val plusBtn = view.findViewById<Button>(R.id.plusButton)
        val minusBtn = view.findViewById<Button>(R.id.minusButton)
        val quantityView = view.findViewById<TextView>(R.id.quantityView)

        nameView.text = product.name
        priceView.text = "$${product.price}"
        quantityView.text = product.quantity.toString()

        plusBtn.setOnClickListener {
            product.quantity++
            quantityView.text = product.quantity.toString()
            saveToCart(product)
        }

        minusBtn.setOnClickListener {
            if (product.quantity > 0) {
                product.quantity--
                quantityView.text = product.quantity.toString()
                if (product.quantity == 0) removeFromCart(product)
                else saveToCart(product)
            }
        }

        return view
    }

    private fun saveToCart(product: Product) {
        val editor = sharedPreferences.edit()
        editor.putInt("${product.name}_qty", product.quantity)
        editor.putFloat("${product.name}_price", product.price.toFloat())
        editor.apply()
    }

    private fun removeFromCart(product: Product) {
        val editor = sharedPreferences.edit()
        editor.remove("${product.name}_qty")
        editor.remove("${product.name}_price")
        editor.apply()
    }
}