package com.arianto.messaging.chat.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.arianto.messaging.R
import com.arianto.messaging.chat.core.ChatContract
import com.arianto.messaging.chat.model.Message
import com.arianto.messaging.chat.core.ChatPresenter
import kotlinx.android.synthetic.main.chat_activity.*

class ChatActivity : AppCompatActivity(), ChatContract.View {

    private var mPresenter:ChatContract.Presenter = ChatPresenter(this)
    private var mAdapter:ChatAdapter? = null
    private var mUserId:String = ""
    private var mOppositeId:String = ""
    private var mRoomType:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_activity)

        intent.getStringExtra("userId")?.let {mUserId = it.toLowerCase()}
        intent.getStringExtra("oppositeId")?.let {mOppositeId = it.toLowerCase()}
        intent.getLongExtra("roomType", 0)?.let {mRoomType = it}

        val actionbar = supportActionBar
        actionbar!!.title = mOppositeId
        actionbar.setDisplayHomeAsUpEnabled(true)

        mAdapter = ChatAdapter(mUserId)
        rcv_messages.adapter = mAdapter
        btn_send.setOnClickListener {
            mPresenter.sendMessage(edt_message.text.toString())
            edt_message.text.clear()
        }

        mPresenter.start()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.stop()
    }

    override fun getUserId(): String {
        return mUserId
    }

    override fun getOppositeId(): String {
        return mOppositeId
    }

    override fun getRoomType(): Long {
        return mRoomType
    }

    override fun onGetMessages(messages: List<Message>?) {
        if (messages != null) {
            mAdapter?.let {
                it.items.addAll(messages)
                it.notifyDataSetChanged()
                rcv_messages.scrollToPosition(messages.size - 1)
            }
        }
    }
    override fun onGetMessage(message: Message?) {
        if (message != null) {
            mAdapter?.let {
                it.items.add(message)
                it.notifyItemInserted(it.itemCount - 1)
                rcv_messages.scrollToPosition(it.itemCount - 1)
            }
        }
    }
    override fun onUpdateMessage(message: Message?) {
        mAdapter?.let {
            val index = it.items.indexOf(message)
            if (index != -1) {
                it.notifyItemChanged(index)
            }
        }
    }

    override fun onFailure(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        if (message != null) {
            edt_message.text.append(message)
        }
    }

}
