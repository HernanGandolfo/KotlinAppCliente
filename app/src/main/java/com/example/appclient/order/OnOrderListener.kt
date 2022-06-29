package com.example.appclient.order

import com.example.appclient.entities.Order


interface OnOrderListener {
    fun onTrack(order: Order)
    fun onStartChat(order: Order)
}