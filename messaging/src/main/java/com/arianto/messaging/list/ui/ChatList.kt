package com.arianto.messaging.list.ui

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.arianto.messaging.R
import com.arianto.messaging.chat.model.Room
import com.arianto.messaging.chat.ui.ChatActivity
import com.arianto.messaging.list.core.ListContract
import com.arianto.messaging.list.model.Item
import com.arianto.messaging.preview.core.ListPresenter
import kotlinx.android.synthetic.main.chat_list.view.*

class ChatList(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs), ListContract.View {

    private var mPresenter: ListContract.Presenter = ListPresenter(this)
    private var mAdapter: ChatListAdapter? = null
    private var mUserId:String? = null

    init {
        inflate(context, R.layout.chat_list, this)

        mAdapter = ChatListAdapter(object : ChatListAdapter.Action {
            override fun onItemClick(item: Item) {
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("roomType",   item.roomType)
                intent.putExtra("userId",     mUserId)
                intent.putExtra("oppositeId", item.oppositeId)
                context.startActivity(intent)
            }
        })
        rcvList.adapter = mAdapter
    }

    override fun setUserId(userId: String) {
        mUserId = userId
        mPresenter.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mPresenter.stop()
    }


    override fun getUserId(): String {
        return mUserId ?: ""
    }


    override fun onGetItems(items: List<Item>) {
        mAdapter?.let {
            it.data.clear()
            it.data.addAll(items)
            it.notifyDataSetChanged()
        }
    }

    override fun onGetItem(item: Item) {
        mAdapter?.let {
            var updated = false
            for ((index, value) in it.data.withIndex()) {
                if (item.roomType == value.roomType && item.oppositeId == value.oppositeId) {
                    if (index == 0) {
                        it.data[index] = item
                        mAdapter?.notifyItemChanged(index)
                    }
                    else {
                        it.data.removeAt(index)
                        mAdapter?.notifyItemRemoved(index)
                        it.data.add(0, item)
                        mAdapter?.notifyItemInserted(0)
                    }
                    updated = true
                    break
                }
            }
            if (!updated) {
                it.data.add(0, item)
                mAdapter?.notifyItemInserted(0)
            }
        }
    }

    override fun onFailure(error: String?) {
    }

}
