package com.arianto.messaging.list.core

import com.arianto.messaging.list.model.Item

interface ListContract {

    interface View {
        fun setUserId(userId:String)
        fun getUserId() : String
        fun onGetItems(items : List<Item>)
        fun onGetItem(item: Item)
        fun onFailure(error: String?)
    }

    interface Presenter {
        fun start()
        fun stop()
    }

    interface ListInquiryCallback {
        fun onSuccess(items:List<Item>)
        fun onFailure(error:String?)
    }

    interface ItemListenerCallback {
        fun onReceive(item: Item)
    }

}