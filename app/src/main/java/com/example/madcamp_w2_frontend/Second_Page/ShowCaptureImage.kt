package com.example.madcamp_w2_frontend.Second_Page

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp_w2_frontend.R

class ShowCaptureImage: AppCompatActivity() {

    lateinit var uriRecycler : RecyclerView
    lateinit var uriList : ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.capture_image)
        uriList = intent.getStringArrayListExtra("captureUriList")!!
        uriRecycler = findViewById(R.id.capture_image_recycler) as RecyclerView
        uriRecycler.layoutManager = GridLayoutManager(this, 3)
        uriRecycler.adapter = CaptureAdapter(uriList, applicationContext)
    }
}