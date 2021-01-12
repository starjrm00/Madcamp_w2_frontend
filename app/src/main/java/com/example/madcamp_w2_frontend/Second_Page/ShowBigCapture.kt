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
import com.davemorrissey.labs.subscaleview.ImageSource
import com.example.madcamp_w2_frontend.R
import kotlinx.android.synthetic.main.big_image.*

class ShowBigCapture: AppCompatActivity() {

    lateinit var captureUri : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.big_image)
        var captureUriString : String = intent.getStringExtra("captureUri")!!
        var bigImageView = findViewById(R.id.iv_detail) as ImageView
        captureUri = Uri.parse(captureUriString)
        bigImageView.setImageURI(captureUri)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        finish()
        return true
    }

    override fun onCreateOptionsMenu(menu : Menu) : Boolean {
        menuInflater.inflate(R.menu.image_item_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) : Boolean {
        val selected = item.itemId
        if (selected == R.id.navigation_image_share) {
            try {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_STREAM, captureUri)
                intent.setPackage("com.kakao.talk")
                this.startActivity(intent)
                return true
            } catch(e : Exception) {
                //kakaotalk 미설치 에러
                var kakaoAlertBuilder : AlertDialog.Builder = AlertDialog.Builder(this)
                kakaoAlertBuilder.setTitle("공유할 수 없음")
                kakaoAlertBuilder.setMessage("이 디바이스에 Kakao Talk 이 설치되어있지 않습니다.\n설치하시겠습니까?")
                kakaoAlertBuilder.setPositiveButton("예", object: DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        val installIntent = Intent(Intent.ACTION_VIEW)
                        installIntent.addCategory(Intent.CATEGORY_DEFAULT)
                        installIntent.data = Uri.parse("market://details?id=com.kakao.talk")
                        startActivity(installIntent)
                    }
                })
                kakaoAlertBuilder.setNegativeButton("아니오", object: DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        Toast.makeText(applicationContext,"Pressed Cancel", Toast.LENGTH_SHORT).show()
                    }
                })
                kakaoAlertBuilder.create().show()
                return true
            }
        }
        return true
    }
}