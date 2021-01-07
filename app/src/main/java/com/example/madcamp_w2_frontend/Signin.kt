package com.example.madcamp_w2_frontend

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Signin: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin)
        val btn_sign_in = findViewById<Button>(R.id.btn_signin)
        btn_sign_in!!.setOnClickListener {
            //TODO("do login logic")
            val intent = Intent(this@Signin, MainActivity::class.java)
            startActivity(intent)
        }
        val btn_sign_up = findViewById<Button>(R.id.btn_signup)
        btn_sign_up!!.setOnClickListener {
            val intent = Intent(this@Signin, Signup::class.java)
            startActivity(intent)
        }
    }

}