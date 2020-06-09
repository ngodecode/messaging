package com.arianto.messaging.preview.core

import com.arianto.messaging.list.core.ListContract
import com.arianto.messaging.list.core.ListFirestore
import com.arianto.messaging.list.model.Item

class ListPresenter(val mView:ListContract.View) : ListContract.Presenter {

    override fun start() {
        ListFirestore.getItems(mView.getUserId(), object : ListContract.ListInquiryCallback {
            override fun onSuccess(items: List<Item>) {
                mView.onGetItems(items)
            }
            override fun onFailure(error: String?) {
                mView.onFailure(error)
            }
        })
        ListFirestore.listen(mView.getUserId(), object : ListContract.ItemListenerCallback {
            override fun onReceive(item: Item) {
                mView.onGetItem(item)
            }
        })
    }

    override fun stop() {
        ListFirestore.removeListener()
    }

}