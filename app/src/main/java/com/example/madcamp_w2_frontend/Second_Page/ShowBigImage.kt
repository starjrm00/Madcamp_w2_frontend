package com.example.madcamp_w2_frontend

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.davemorrissey.labs.subscaleview.ImageSource
import com.example.madcamp_w2_frontend.R
import kotlinx.android.synthetic.main.big_image.*

class ShowBigImage: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.big_image)
        var uri:Uri = intent.getParcelableExtra("photo" )!!
        //item?.photo = Uri.parse(intent.getStringExtra("photo" ))
        iv_detail.setImage(ImageSource.uri(uri))
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        finish()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}