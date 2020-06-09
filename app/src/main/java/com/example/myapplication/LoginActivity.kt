package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.login_activity.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        title = "Login"

        btnLogin.setOnClickListener {
            UserData.userId = txtUserId.text.toString()
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }


    }
}
