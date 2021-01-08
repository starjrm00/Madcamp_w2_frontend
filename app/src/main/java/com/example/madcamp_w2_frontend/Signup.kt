package com.example.madcamp_w2_frontend

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class Signup:AppCompatActivity() {
    private var signup_success = false
    private val url:String = "http://192.249.18.212:3000"

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("onCreate", "signup oncreate")
        var intent = getIntent()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)

        val ID = findViewById<EditText>(R.id.Signup_id)
        val PW = findViewById<EditText>(R.id.Signup_pw)
        val btn = findViewById<Button>(R.id.btn_signup_page)

        btn!!.setOnClickListener{
            try_sign_up(ID.text.toString(), PW.text.toString())
            if(signup_success){
                Toast.makeText(applicationContext, "회원가입이 완료되었습니다.", Toast.LENGTH_LONG )

            }
            else{
                //실패 메세지 토스트
            }
            finish()
        }
    }
    fun try_sign_up(id:String, pw:String){
        try {
            com.example.madcamp_w2_frontend.Signup.JSONTask(id, pw)
                .execute("http://192.249.18.212:3000/post")
        }catch (e: JSONException) {
            e.printStackTrace()
            signup_success = false
        }
    }

    @Suppress("DEPRECATION")
    class JSONTask(id: String, pw: String) : AsyncTask<String?, String?, String?>() {
        var id = id
        var pw = pw

        override fun doInBackground(vararg params: String?): String? {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                val jsonObject = JSONObject()
                jsonObject.accumulate("id", id)
                jsonObject.accumulate("password", pw)
                var con: HttpURLConnection? = null
                var reader: BufferedReader? = null
                Log.i("test", "save json")
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
                    return buffer.toString() //서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임
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
            Log.i("Signup", "finish!")//서버로 부터 받은 값을 출력해주는 부분
        }
    }
}