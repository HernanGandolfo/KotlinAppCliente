package com.example.appclient.chat

import com.example.appclient.entities.Message

interface OnChatListener {
    fun deleteMessage(message: Message)
}