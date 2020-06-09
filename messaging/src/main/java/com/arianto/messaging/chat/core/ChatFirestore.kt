package com.arianto.messaging.chat.core

import android.util.Log
import com.arianto.messaging.chat.model.Message
import com.arianto.messaging.chat.model.Room
import com.google.firebase.firestore.*

object ChatFirestore {
    private const val COLLECTION_ROOMS    = "ROOMS"
    private const val COLLECTION_MESSAGES = "MESSAGES"

    fun setRoom(room: Room, callback:ChatContract.ExecutionCallback) {
        FirebaseFirestore.getInstance()
            .collection(COLLECTION_ROOMS).document(room.roomId)
            .set(room)
            .addOnSuccessListener {
                callback.onSuccess()
            }
            .addOnFailureListener {
                callback.onFailure(it.message)
            }
    }

    fun getRoom(roomId:String, callbackMessage:ChatContract.RoomInquiryCallback) {
        FirebaseFirestore.getInstance()
            .collection(COLLECTION_ROOMS)
            .whereEqualTo("roomId", roomId)
            .get()
            .addOnSuccessListener {snap ->
                if (snap.documents.isNotEmpty()) {
                    snap.documents[0].toObject(Room::class.java)?.let {
                        callbackMessage.onExist(it)
                    }
                }
                else {
                    callbackMessage.onNotFound(roomId)
                }
            }
            .addOnFailureListener {
                callbackMessage.onFailure(it.message)
            }
    }

    fun setMessage(message: Message, callback:ChatContract.ExecutionCallback) {
        val roomRef = FirebaseFirestore.getInstance()
            .collection(COLLECTION_ROOMS).document(message.roomId)
        val messageRef = roomRef
            .collection(COLLECTION_MESSAGES).document(message.messageId)
        FirebaseFirestore.getInstance().runTransaction {transaction ->
            val room = transaction.get(roomRef).toObject(Room::class.java)!!
            room.previewMessage[0] = message.source
            room.previewMessage[1] = message.text ?: ""
            room.previewTime = message.time ?: 0

            transaction.set(messageRef, message)
            transaction.set(roomRef, room)

        }.addOnSuccessListener {
            callback.onSuccess()
        }.addOnFailureListener {e  ->
            Log.w("setMessage", "Transaction failure.", e)
            callback.onFailure(e.message)
        }
    }
    fun getMessages(roomId:String, timeLessThan:Long, callbackMessage:ChatContract.MessageInquiryCallback) {
        FirebaseFirestore.getInstance()
            .collection(COLLECTION_ROOMS).document(roomId)
            .collection(COLLECTION_MESSAGES)
            .whereLessThan("time", timeLessThan)
            .orderBy("time", Query.Direction.DESCENDING)
            .limit(30)
            .get()
            .addOnSuccessListener { snapshoot ->
                val list = ArrayList<Message>()
                for (document in snapshoot.documents) {
                    document.toObject(Message::class.java)?.let {
                        list.add(0, it)
                    }
                }
                callbackMessage.onSuccess(list)
            }
            .addOnFailureListener {
                callbackMessage.onFailure(it.message)
            }
    }

    private var mListener:ListenerRegistration? = null
    fun listenMessage(roomId: String, userId:String, timeGreaterThan: Long, listener: ChatContract.MessageListenerCallback) {
        mListener = FirebaseFirestore.getInstance()
            .collection(COLLECTION_ROOMS).document(roomId)
            .collection(COLLECTION_MESSAGES)
            .whereGreaterThan("time", timeGreaterThan)
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("SNAPSHOT", "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshots != null) {
                    for (dc in snapshots.documentChanges) {
                        Log.w("SNAPSHOT", "dc.type " + dc.type.name)
                        if (dc.type == DocumentChange.Type.ADDED) {
                            dc.document.toObject(Message::class.java).let {
                                if (it.source != userId) {
                                    listener.onReceiveMessage(it)
                                }
                            }
                        }
                    }
                }
            }
    }
    fun removeListener() {
        mListener?.let {
            it.remove()
            mListener = null
        }
    }

}