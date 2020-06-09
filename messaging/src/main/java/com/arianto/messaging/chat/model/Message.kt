package com.arianto.messaging.chat.model

data class Message(var messageId:String, var roomId:String, var source:String, var text:String?, var time:Long?, var  status:Long?) {

    constructor() : this("", "", "", null, 0, 0)
    constructor(messageId:String, roomId:String, source:String) : this(messageId, roomId, source, null, 0, 0)

    object Status {
        const val PENDING:Long = 1
        const val SENT:Long = 2
        const val FAILED:Long = 3
    }



}