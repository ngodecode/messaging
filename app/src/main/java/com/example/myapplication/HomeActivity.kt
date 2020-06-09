package com.example.myapplication

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.arianto.messaging.chat.ui.ChatActivity
import kotlinx.android.synthetic.main.home_activity.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)

        title = "Home"

        UserData.userId?.let {
            chatList.setUserId(it)
        }

        fab.setOnClickListener {
            startActivityForResult(Intent(this, CrateChatActivity::class.java), 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                var roomType   = data?.extras?.getLong("roomType")
                var oppositeId = data?.extras?.getString("oppositeId")
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra("roomType", roomType)
                intent.putExtra("userId",     UserData.userId)
                intent.putExtra("oppositeId", oppositeId)
                startActivity(intent)
            }
        }
    }
}
