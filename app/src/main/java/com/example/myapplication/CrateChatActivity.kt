package com.example.myapplication

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.arianto.messaging.chat.model.Room
import kotlinx.android.synthetic.main.crate_chat_activity.*

class CrateChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.crate_chat_activity)

        val actionbar = supportActionBar
        actionbar!!.title = "New Chat"
        actionbar.setDisplayHomeAsUpEnabled(true)

        ArrayAdapter.createFromResource(
            this,
            R.array.roomType,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spn_roomType.adapter = adapter
        }

        btn_chat.setOnClickListener {
            val intent = Intent()
            intent.putExtra("roomType", if (spn_roomType.selectedItemPosition == 0) Room.Type.PRIVATE else Room.Type.GROUP)
            intent.putExtra("oppositeId", edt_opposite.text.toString())
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
