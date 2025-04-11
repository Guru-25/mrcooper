package com.example.sale


data class Product(val name: String, val price: Double, var quantity: Int = 0) {
    companion object {
        fun getSampleProducts(): MutableList<Product> {
            return mutableListOf(
                Product("Apple", 1.0),
                Product("Banana", 0.5),
                Product("Orange", 0.8),
                Product("Milk", 1.2),
                Product("Bread", 2.0),
                Product("Eggs", 3.0)
            )
        }
    }
}