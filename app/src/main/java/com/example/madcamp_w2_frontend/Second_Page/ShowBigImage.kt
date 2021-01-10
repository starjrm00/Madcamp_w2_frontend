package com.example.madcamp_w2_frontend

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.MotionEvent
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.davemorrissey.labs.subscaleview.ImageSource
import com.example.madcamp_w2_frontend.R
import kotlinx.android.synthetic.main.big_image.*

class ShowBigImage: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.big_image)
        var bitmapString : String = intent.getStringExtra("photo" )!!
        //item?.photo = Uri.parse(intent.getStringExtra("photo" ))
        var bigImageView = findViewById(R.id.iv_detail) as ImageView
        bigImageView.setImageBitmap(StringToBitmap(bitmapString))
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        finish()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun StringToBitmap(encodedString: String?): Bitmap? {
        return try {
            val encodeByte = Base64.decode(
                encodedString,
                Base64.DEFAULT
            ) // String 화 된 이미지를  base64방식으로 인코딩하여 byte배열을 만듬
            BitmapFactory.decodeByteArray(
                encodeByte,
                0,
                encodeByte.size
            ) //만들어진 bitmap을 return
        } catch (e: Exception) {
            e.message
            null
        }
    }
}