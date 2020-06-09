package com.arianto.messaging.chat.core

import com.arianto.messaging.chat.model.Message
import com.arianto.messaging.chat.model.Room

interface ChatContract {

    interface View {
        fun getUserId() : String
        fun getOppositeId() : String
        fun getRoomType() : Long
        fun onGetMessages(messages : List<Message>?)
        fun onGetMessage(message: Message?)
        fun onUpdateMessage(message: Message?)
        fun onFailure(message: String?)
    }

    interface Presenter {
        fun sendMessage(text:String)
        fun loadMessages(timeLessThan:Long)
        fun start()
        fun stop()
    }

    interface ExecutionCallback {
        fun onSuccess()
        fun onFailure(error:String?)
    }

    interface MessageInquiryCallback {
        fun onSuccess(result:List<Message>)
        fun onFailure(error:String?)
    }

    interface RoomInquiryCallback {
        fun onExist(room: Room)
        fun onNotFound(roomId:String)
        fun onFailure(error:String?)
    }

    interface MessageListenerCallback {
        fun onReceiveMessage(chat: Message)
    }

}