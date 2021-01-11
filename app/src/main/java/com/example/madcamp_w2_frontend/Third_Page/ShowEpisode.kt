package com.example.madcamp_w2_frontend.Third_Page

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp_w2_frontend.R
import com.example.madcamp_w2_frontend.Second_Page.Fragment2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.*
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ShowEpisode: AppCompatActivity() {
    //한 회차를 보여주는 페이지

    lateinit var oneEpisodeRecycler : RecyclerView
    lateinit var UniqueID : String
    var imageList : MutableList<String> = ArrayList<String>()
    val serverip = "http://192.249.18.212:3000"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.one_episode)
        var site : String = intent.getStringExtra("site")!!
        var link : String = intent.getStringExtra("link" )!!
        UniqueID = intent.getStringExtra("uniqueID")!!
        if (site == "Naver") {
            getNaverEpisode().execute(link)
        }

        oneEpisodeRecycler = findViewById(R.id.episode_image_list) as RecyclerView
        var captureButton = findViewById(R.id.btn_capture) as FloatingActionButton
        captureButton.setOnClickListener {
            var rootView: View = getWindow().getDecorView() //전체 화면
            var screenBitmap : Bitmap = shot(rootView)
            var screenShot = ScreenShot(screenBitmap)
            rootView.isDrawingCacheEnabled = false
            if (screenShot != null) {
                sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(screenShot)))
            }
            Toast.makeText(applicationContext, "현재 화면을 캡처하였습니다.", Toast.LENGTH_SHORT).show()
            //TODO(캡처 화면 DB에 저장하기)
            //saveImageinMongod(screenBitmap)
        }

    }

    fun saveImageinMongod(bitmap : Bitmap) {
        val bitmapString : String? = BitmapToString(bitmap)
        var jsonTask = SaveImageJSONTask(bitmapString, UniqueID, applicationContext)
        Log.d("try_login", "let's execute jsonTask")
        jsonTask.execute(serverip+"/saveImage")
    }

    fun BitmapToString(bitmap: Bitmap): String? {
        val baos =
            ByteArrayOutputStream() //바이트 배열을 차례대로 읽어 들이기위한 ByteArrayOutputStream클래스 선언
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos) //bitmap을 압축 (숫자 70은 70%로 압축한다는 뜻)
        val bytes = baos.toByteArray() //해당 bitmap을 byte배열로 바꿔준다.
        return Base64.encodeToString(bytes, Base64.DEFAULT) //String을 retrurn
    }

    fun shot(view:View) : Bitmap {
        view.isDrawingCacheEnabled = true
        var screenBitmap : Bitmap = view.getDrawingCache()
        return screenBitmap
    }

    fun ScreenShot(screenBitmap : Bitmap) : File? {
        val currentDateTime = Calendar.getInstance().time
        var dateFormat : String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA).format(currentDateTime)
        var filename : String = dateFormat + ".png"
        var file = File(Environment.getExternalStorageDirectory().toString()+"/Pictures", filename)
        var os : FileOutputStream? = null
        try{
            os = FileOutputStream(file)
            screenBitmap.compress(Bitmap.CompressFormat.PNG, 90, os)
            os.close()
        } catch(e:IOException) {
            e.printStackTrace()
            return null
        }
        return file
    }

    @Suppress("DEPRECATION")
    inner class getNaverEpisode : AsyncTask<String?, Void?, String?>() {

        // String 으로 값을 전달받은 값을 처리하고, Boolean 으로 doInBackground 결과를 넘겨준다.
        override fun doInBackground(vararg params: String?) : String? {
            try {
                val document: Document = Jsoup.connect(params[0]).get()
                //image 불러오기
                val imageElements : Elements = document.select("div.wt_viewer img")
                for (i in imageElements) {
                    val imageUrl : String = i.attr("src")
                    imageList.add(imageUrl)
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            oneEpisodeRecycler.layoutManager = LinearLayoutManager(applicationContext)
            oneEpisodeRecycler.adapter = OneEpisodeAdapter(imageList, applicationContext)
        }
    }

    @Suppress("DEPRECATION")
    inner class SaveImageJSONTask(bitmapString : String?, UniqueID : String, mContext : Context?) : AsyncTask<String?, String?, String?>() {
        var bitmapString = bitmapString
        var UniqueID = UniqueID
        var mContext = mContext

        override fun doInBackground(vararg params: String?): String? {
            try {
                var jsonObject = JSONObject()
                //입력해둔 edittext의 id와 pw값을 받아와 put해줍니다 : 데이터를 json형식으로 바꿔 넣어주었습니다.
                jsonObject.accumulate("_id", UniqueID)
                jsonObject.accumulate("imageBitmap", bitmapString)
                var con: HttpURLConnection? = null
                var reader: BufferedReader? = null
                Log.d("JSONTask", "in 1st try")

                try {
                    Log.d("JSONTask", "in 2nd try")
                    val url = URL(params[0])
                    con = url.openConnection() as HttpURLConnection
                    con.requestMethod = "POST" //POST방식으로 보냄
                    con!!.setRequestProperty("Cache-Control", "no-cache") //캐시 설정
                    con.setRequestProperty(
                        "Content-Type",
                        "application/json"
                    ) //application JSON 형식으로 전송
                    con.setRequestProperty("Accept", "text/html") //서버에 response 데이터를 html로 받음
                    con.doOutput = true //Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.doInput = true //Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect()
                    Log.i("test", "set stream")

                    //서버로 보내기위해서 스트림 만듬
                    val outStream = con.outputStream
                    //버퍼를 생성하고 넣음
                    val writer =
                        BufferedWriter(OutputStreamWriter(outStream))
                    writer.write(jsonObject.toString())
                    writer.flush()
                    writer.close() //버퍼를 받아줌
                    Log.i("test", "get buffer")

                    //서버로 부터 데이터를 받음
                    val stream = con.inputStream
                    reader = BufferedReader(InputStreamReader(stream))
                    val buffer = StringBuffer()
                    var line: String? = ""
                    while (reader.readLine().also { line = it } != null) {
                        buffer.append(line)
                    }

                    Log.i("CaptureLoad", "finish")
                    return buffer.toString()
                } catch (e: MalformedURLException) {
                    Log.i("test", "error1")
                    e.printStackTrace()
                } catch (e: IOException) {
                    Log.i("test", "error2")
                    e.printStackTrace()
                } finally {
                    con?.disconnect()
                    try {
                        reader?.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result == "Fail") {
                Toast.makeText(mContext, "DB 업로드에 실패하였습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }
}