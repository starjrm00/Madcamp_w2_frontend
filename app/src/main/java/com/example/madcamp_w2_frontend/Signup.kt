package com.example.madcamp_w2_frontend

import android.content.Context
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
    val serverip = "http://192.249.18.242:3000"
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("onCreate", "signup oncreate")
        var intent = getIntent()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)

        val ID = findViewById<EditText>(R.id.Signup_id)
        val PW = findViewById<EditText>(R.id.Signup_pw)
        val Name = findViewById<EditText>(R.id.Signup_name)
        val btn = findViewById<Button>(R.id.btn_signup_page)

        btn!!.setOnClickListener{
            try_sign_up(ID.text.toString(), PW.text.toString(), Name.text.toString())

            finish()
        }
    }
    fun try_sign_up(id:String, pw:String, name:String){
        try {
            com.example.madcamp_w2_frontend.Signup.JSONTask(id, pw, name, applicationContext)
                .execute(serverip+"/signup")
        }catch (e: JSONException) {
            e.printStackTrace()
            Toast.makeText(applicationContext, "회원가입에 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show()
        }
    }

    @Suppress("DEPRECATION")
    class JSONTask(id: String, pw: String, name : String, mContext : Context) : AsyncTask<String?, String?, String?>() {
        var id = id
        var pw = pw
        var name = name
        var mContext = mContext

        override fun doInBackground(vararg params: String?): String? {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                val jsonObject = JSONObject()
                jsonObject.accumulate("id", id)
                jsonObject.accumulate("password", pw)
                jsonObject.accumulate("name", name)
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
            if (result == "Success") {
                Toast.makeText(mContext, "회원가입이 완료되었습니다.", Toast.LENGTH_LONG).show()
            } else if (result == "Fail/duplicate"){
                Toast.makeText(mContext, "이미 존재하는 id입니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show()
            }
        }
    }
}