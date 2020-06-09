package com.arianto.messaging.chat.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arianto.messaging.R
import com.arianto.messaging.chat.model.Message
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatAdapter(private val userId:String) : RecyclerView.Adapter<ChatAdapter.Holder>() {

    val items = ArrayList<Message>()

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var message : TextView? = null
        var time : TextView? = null
        init {
            message = itemView.findViewById(R.id.txt_message)
            time    = itemView.findViewById(R.id.txt_time)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (userId == items[position].source) {
            return 2
        }
        return 1
    }
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return if (viewType == 1) {
            Holder(LayoutInflater.from(parent.context).inflate(R.layout.chat_adapter_left, parent, false))
        } else {
            Holder(LayoutInflater.from(parent.context).inflate(R.layout.chat_adapter_right, parent, false))
        }
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val model = items[position]
        holder.message?.text = model.text
        holder.time?.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(model.time)
    }
}