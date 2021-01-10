package com.example.madcamp_w2_frontend

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class GetImage: AppCompatActivity() {

    @Suppress("DEPRECATION")
    class JSONTask(uniqueID : String, mContext : Context?) : AsyncTask<String?, String?, String?>() {
        var uniqueID = uniqueID
        var mContext = mContext
        lateinit var result_gallery : ArrayList<String>

        override fun doInBackground(vararg params: String?): String? {
            try {
                var jsonObject = JSONObject()
                //입력해둔 edittext의 id와 pw값을 받아와 put해줍니다 : 데이터를 json형식으로 바꿔 넣어주었습니다.
                jsonObject.accumulate("userID", uniqueID)
                var con: HttpURLConnection? = null
                var reader: BufferedReader? = null
                try {
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
                    return buffer.toString()
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                } catch (e: IOException) {
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
            val jObject = JSONObject(result)
            if (jObject.getString("Success") == "Success") {
                var gallery = jObject.get("gallery") as ArrayList<String>
                result_gallery = gallery
            }
        }
    }
}