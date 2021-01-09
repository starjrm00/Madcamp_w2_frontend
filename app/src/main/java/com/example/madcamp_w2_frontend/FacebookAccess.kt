package com.example.madcamp_w2_frontend

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.facebook.FacebookSdk.getApplicationContext
import com.google.gson.JsonObject
import org.json.JSONObject
import org.xml.sax.Parser
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class FacebookAccess {

    @Suppress("DEPRECATION")
    class JSONTask(userId : String, userName : String) : AsyncTask<String?, String?, String?>() {
        var id = userId
        var name = userName

        override fun doInBackground(vararg params: String?): String? {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.

                val jsonObject = JSONObject()
                jsonObject.accumulate("user_id", id)
                jsonObject.accumulate("user_name", name)
                var con: HttpURLConnection? = null
                var reader: BufferedReader? = null

                try {
                    val url = URL(params[0])
                    //연결을 함
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

                    //서버로 보내기위해서 스트림 만듬
                    val outStream = con.outputStream
                    //버퍼를 생성하고 넣음
                    val writer =
                        BufferedWriter(OutputStreamWriter(outStream))
                    writer.write(jsonObject.toString())
                    writer.flush()
                    writer.close() //버퍼를 받아줌

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
            Log.d("onPostExecute", result!!)
            val jObject = JSONObject(result)
            Log.d("onPostExecute", jObject.getString("loginSuccess"))
            if (jObject.getString("loginSuccess") == "Success") {
                Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_LONG).show()
                val intent = Intent(getApplicationContext(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                var bundle = Bundle()
                bundle.putString("UniqueID", jObject.getString("uniqueId"))
                intent.putExtras(bundle)
                ContextCompat.startActivity(getApplicationContext(), intent, null)
            }
        }
    }
}