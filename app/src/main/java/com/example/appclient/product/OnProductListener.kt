package com.example.appclient.product

import com.example.appclient.entities.Product


interface OnProductListener {
    fun onClick(product: Product)
}