package com.example.appclient.chat

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.appclient.R
import com.example.appclient.databinding.ItemChatBinding
import com.example.appclient.entities.Message

class ChatAdapter(private val messageList: MutableList<Message>, private val listener: OnChatListener)
    : RecyclerView.Adapter<ChatAdapter.ViewHolder>(){

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messageList[position]

        holder.setListener(message)
        // Cliente a la Derecha
        var gravity = Gravity.END
        var background = ContextCompat.getDrawable(context, R.drawable.background_chat_client)
        var textColor = ContextCompat.getColor(context, R.color.colorOnSecondary)

        //Contexto de Margen para cliente,Configuro el limite de mensaje en la View
        val marginHorizontal = context.resources.getDimensionPixelSize(R.dimen.chat_margin_horizontal)
        val params = holder.binding.tvMessage.layoutParams as ViewGroup.MarginLayoutParams
        params.marginStart = marginHorizontal
        params.marginEnd = 0
        params.topMargin = 0

        //Compruebo que no sea enviado por el mismo Id de Usuario, NO USAR CON MISMOS USUARIOS
        if (position > 0 && message.isSentByMe() != messageList[position - 1].isSentByMe()){
            params.topMargin = context.resources.getDimensionPixelSize(R.dimen.common_padding_min)
        }

        // Soporte a la Izq
        if (!message.isSentByMe()){
            gravity = Gravity.START
            background = ContextCompat.getDrawable(context, R.drawable.background_chat_support)
            textColor = ContextCompat.getColor(context, R.color.colorOnPrimary)
            params.marginStart = 0
            params.marginEnd = marginHorizontal
        }

        holder.binding.root.gravity = gravity

        holder.binding.tvMessage.layoutParams = params
        holder.binding.tvMessage.setBackground(background)
        holder.binding.tvMessage.setTextColor(textColor)
        holder.binding.tvMessage.text = message.message
    }

    override fun getItemCount(): Int = messageList.size

    //Si no contiene lo agrego
    fun add(message: Message){
        if (!messageList.contains(message)){
            messageList.add(message)
            notifyItemInserted(messageList.size - 1)
        }
    }
    // Actualiza el mensaje
    fun update(message: Message){
        val index = messageList.indexOf(message)
        if (index != -1){
            messageList.set(index, message)
            notifyItemChanged(index)
        }
    }

    //Borro el mensaje como item
    fun delete(message: Message){
        val index = messageList.indexOf(message)
        if (index != -1){
            messageList.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    //Tomo los controles de la Main
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val binding = ItemChatBinding.bind(view)

        fun setListener(message: Message){
            binding.tvMessage.setOnLongClickListener {
                listener.deleteMessage(message)
                true
            }
        }
    }
}