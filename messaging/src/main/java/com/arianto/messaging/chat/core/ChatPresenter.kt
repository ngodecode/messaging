package com.arianto.messaging.chat.core

import com.arianto.messaging.chat.core.ChatContract.ExecutionCallback
import com.arianto.messaging.chat.model.Message
import com.arianto.messaging.chat.model.Room
import java.util.*

class ChatPresenter(val mView:ChatContract.View) : ChatContract.Presenter {

    private var mRoom: Room? = null

    override fun start() {
        prepareRoom()
    }

    private fun prepareRoom() {
        val roomId = generateRoomId() ?: return
        ChatFirestore.getRoom(roomId, object : ChatContract.RoomInquiryCallback {

            override fun onFailure(error: String?) {
                mView.onFailure(error)
            }

            override fun onNotFound(roomId: String) {
                when (mView.getRoomType()) {
                    Room.Type.PRIVATE -> {
                        val room = Room(
                            roomId,
                            mView.getRoomType()
                        )
                        room.members.add(mView.getUserId())
                        room.members.add(mView.getOppositeId())
                        saveRoom(room)
                    }
                    Room.Type.GROUP -> {
                        val room = Room(
                            roomId,
                            mView.getRoomType()
                        )
                        room.members.add(mView.getUserId())
                        saveRoom(room)
                    }
                    else -> {
                        mView.onFailure("Can't initiate Room")
                    }
                }
            }

            override fun onExist(room: Room) {
                if (room.members.contains(mView.getUserId().toLowerCase(Locale.ROOT))) {
                    initRoom(room)
                }
                else {
                    room.members.add(mView.getUserId())
                    saveRoom(room)
                }
            }

            private fun saveRoom(room: Room) {
                ChatFirestore.setRoom(room, object : ExecutionCallback {
                    override fun onSuccess() {
                        initRoom(room)
                    }

                    override fun onFailure(error: String?) {
                        mView.onFailure(error)
                    }
                })
            }

        })
    }

    private fun initRoom(room: Room) {
        mRoom = room
        val time   = System.currentTimeMillis()
        ChatFirestore.listenMessage(room.roomId, mView.getUserId(), time, object : ChatContract.MessageListenerCallback {
            override fun onReceiveMessage(chat: Message) {
                mView.onGetMessage(chat)
            }
        })
        loadMessages(time)
    }

    private fun generateRoomId() : String? {
        when (mView.getRoomType()) {
            Room.Type.PRIVATE -> {
                return "PM-" + arrayListOf(mView.getUserId().toLowerCase(Locale.ROOT),
                    mView.getOppositeId().toLowerCase(Locale.ROOT)).sorted().joinToString("").hashCode().toString()

            }
            Room.Type.GROUP -> {
                return "GM-" + mView.getOppositeId().toLowerCase(Locale.ROOT).hashCode().toString()
            }
        }
        return null
    }

    override fun sendMessage(text: String) {
        val roomId = mRoom?.roomId ?: return
        val source = mView.getUserId()
        val messageId = source.hashCode().toString() + roomId.hashCode().toString() + Integer.toHexString(System.currentTimeMillis().toInt())

        val message   = Message(
            messageId,
            roomId,
            source
        )
        message.text      = text
        message.time      = System.currentTimeMillis()
        message.status    = Message.Status.PENDING
        mView.onGetMessage(message)

        message.status    = Message.Status.SENT
        ChatFirestore.setMessage(message, object : ExecutionCallback {
            override fun onSuccess() {
                mView.onUpdateMessage(message)
            }

            override fun onFailure(error: String?) {
                message.status = Message.Status.FAILED
                mView.onUpdateMessage(message)
            }
        })
    }

    override fun loadMessages(timeLessThan:Long) {
        val roomId = mRoom?.roomId ?: return
        ChatFirestore.getMessages(roomId, timeLessThan, object : ChatContract.MessageInquiryCallback {
            override fun onSuccess(result: List<Message>) {
                mView.onGetMessages(result)
            }

            override fun onFailure(error: String?) {
                mView.onFailure(error)
            }
        })
    }

    override fun stop() {
        ChatFirestore.removeListener()
    }

}