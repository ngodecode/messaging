package com.arianto.messaging.list.core

import android.util.Log
import com.arianto.messaging.chat.model.Room
import com.arianto.messaging.list.model.Item
import com.google.firebase.firestore.*

object ListFirestore {
    private const val COLLECTION_ROOMS    = "ROOMS"
    fun getItems(userId:String, callback:ListContract.ListInquiryCallback) {
        FirebaseFirestore.getInstance()
        .collection(COLLECTION_ROOMS)
        .whereArrayContains("members", userId)
        .whereGreaterThan("previewTime", 0)
        .orderBy("previewTime", Query.Direction.DESCENDING)
        .limit(30)
        .get()
            .addOnSuccessListener { snapshoot ->
                val list = ArrayList<Item>()
                for (document in snapshoot.documents) {
                    document.toObject(Room::class.java)?.let { room ->
                        when (room.type) {
                            Room.Type.PRIVATE -> {
                                room.members.remove(userId)
                                list.add(Item(room.type, room.members[0], room.previewMessage[1], room.previewTime))
                            }
                            Room.Type.GROUP -> {
                                list.add(Item(room.type, room.label ?: "", room.previewMessage.joinToString(": "), room.previewTime))
                            }
                            else -> {}
                        }
                    }
                }
                callback.onSuccess(list)
            }
            .addOnFailureListener {
                callback.onFailure(it.message)
            }
    }

    private var mListener:ListenerRegistration? = null
    fun listen(userId:String, listener: ListContract.ItemListenerCallback) {
        mListener = FirebaseFirestore.getInstance()
            .collection(COLLECTION_ROOMS)
            .whereArrayContains("members", userId)
            .whereGreaterThan("previewTime", 0)
            .orderBy("previewTime", Query.Direction.DESCENDING)
            .limit(30)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("SNAPSHOT", "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshots != null) {
                    for (dc in snapshots.documentChanges) {
                        Log.w("SNAPSHOT", "dc.type " + dc.type.name)
                        if (dc.type == DocumentChange.Type.ADDED || dc.type == DocumentChange.Type.MODIFIED) {
                            dc.document.toObject(Room::class.java).let { room ->
                                when (room.type) {
                                    Room.Type.PRIVATE -> {
                                        room.members.remove(userId)
                                        listener.onReceive(Item(room.type, room.members[0], room.previewMessage[1], room.previewTime))
                                    }
                                    Room.Type.GROUP -> {
                                        listener.onReceive(Item(room.type, room.label ?: "", room.previewMessage.joinToString(": "), room.previewTime))
                                    }
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