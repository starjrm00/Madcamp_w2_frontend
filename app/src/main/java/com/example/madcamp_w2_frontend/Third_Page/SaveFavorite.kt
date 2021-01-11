package com.example.madcamp_w2_frontend.Third_Page

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp_w2_frontend.R


class SaveFavorite: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //TODO(favorite webtoon db에 저장하기)

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        finish()
        return true
    }
}