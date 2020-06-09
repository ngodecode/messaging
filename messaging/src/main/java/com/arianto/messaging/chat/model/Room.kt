package com.arianto.messaging.chat.model

data class Room(var roomId:String, var type:Long, var label:String?, var members:ArrayList<String>) {
    var previewMessage:ArrayList<String> = arrayListOf("", "")
    var previewTime:Long = 0
    constructor() : this("", 0, null, ArrayList())
    constructor(roomId:String, type:Long) : this(roomId, type, null, ArrayList())

    object Type {
        const val PRIVATE:Long = 1
        const val GROUP:Long   = 2
    }

}