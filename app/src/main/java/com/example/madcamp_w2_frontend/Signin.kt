package com.example.madcamp_w2_frontend

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*


class Signin: AppCompatActivity() {

    val url: String = "http://192.249.18.212:3000"
    var login_success = false
    private var callbackManager: CallbackManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(this.applicationContext)
        setContentView(R.layout.signin)

        val ID = findViewById<EditText>(R.id.text_id)
        val PW = findViewById<EditText>(R.id.text_pw)

        val btn_sign_in = findViewById<Button>(R.id.btn_signin)
        btn_sign_in!!.setOnClickListener {
            if (try_login(ID.text.toString(), PW.text.toString())) {
                val intent = Intent(this@Signin, MainActivity::class.java)
                startActivity(intent)
            }
        }
        val btn_sign_up = findViewById<Button>(R.id.btn_signup)
        btn_sign_up!!.setOnClickListener {

            val intent = Intent(this@Signin, Signup::class.java)
            startActivity(intent)
        }

        callbackManager = CallbackManager.Factory.create()

        val loginButton =
            findViewById<View>(R.id.facebook_login_btn) as LoginButton
        loginButton.setReadPermissions(
            Arrays.asList(
                "public_profile",
                "email"
            )
        )
        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                val graphRequest = GraphRequest.newMeRequest(
                    loginResult.accessToken
                ) { `object`, response -> Log.v("result", `object`.toString()) }
                val parameters = Bundle()
                parameters.putString("fields", "id,name,email,gender,birthday")
                graphRequest.parameters = parameters
                graphRequest.executeAsync()
            }

            override fun onCancel() {}
            override fun onError(error: FacebookException) {
                Log.e("LoginErr", error.toString())
            }
        })
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }

    fun try_login(id: String, pw: String) : Boolean {
        try {
            var jsonTask = com.example.madcamp_w2_frontend.Signin.JSONTask(id, pw, applicationContext)
            jsonTask.execute("http://192.249.18.212:3000/login")
            return jsonTask.success
        } catch (e: JSONException) {
            e.printStackTrace()
            Toast.makeText(applicationContext, "로그인 실패", Toast.LENGTH_LONG).show()
            login_success = false
            return false
        }
    }

    @Suppress("DEPRECATION")
    class JSONTask(id: String, pw: String, mContext : Context) : AsyncTask<String?, String?, String?>() {
        var id = id
        var pw = pw
        var mContext = mContext
        var success = false

        override fun doInBackground(vararg params: String?): String? {
            try {
                var jsonObject = JSONObject()
                //입력해둔 edittext의 id와 pw값을 받아와 put해줍니다 : 데이터를 json형식으로 바꿔 넣어주었습니다.
                jsonObject.accumulate("id", id)
                jsonObject.accumulate("password", pw)
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

                    Log.i("Signin test", buffer.toString())
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
            if (result == "Success") {
                success = true
                Toast.makeText(mContext, "로그인 성공", Toast.LENGTH_LONG).show()
            }
        }
    }
}