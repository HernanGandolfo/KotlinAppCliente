package com.example.appclient.cart

import com.example.appclient.entities.Product

interface OnCartListener {
    fun setQuantity(product: Product)
    fun showTotal(total: Double)
}