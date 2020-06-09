package com.arianto.messaging.list.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arianto.messaging.R
import com.arianto.messaging.chat.model.Room
import com.arianto.messaging.chat.ui.ChatActivity
import com.arianto.messaging.list.model.Item
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatListAdapter(val mAction:Action?) : RecyclerView.Adapter<ChatListAdapter.Holder>() {

    interface Action {
        fun onItemClick(item:Item)
    }
    val data = ArrayList<Item>()

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtMessage : TextView? = null
        var txtTime : TextView? = null
        var txtSource : TextView? = null
        var txtThumb : TextView? = null
        init {
            txtMessage = itemView.findViewById(R.id.txtMessage)
            txtTime    = itemView.findViewById(R.id.txtTime)
            txtSource  = itemView.findViewById(R.id.txtSource)
            txtThumb   = itemView.findViewById(R.id.txtThumb)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_adapter, parent, false)
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val model = data[position]
        holder.txtSource?.text  = model.oppositeId
        holder.txtMessage?.text = model.text
        holder.txtThumb?.text   = model.oppositeId.substring(0, 1)
        holder.txtTime?.text    = SimpleDateFormat("HH:mm", Locale.getDefault()).format(model.time)
        holder.itemView.setOnClickListener {
           mAction?.onItemClick(model)
        }
    }
}