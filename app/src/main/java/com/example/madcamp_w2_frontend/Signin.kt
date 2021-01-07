package com.example.madcamp_w2_frontend

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class Signin: AppCompatActivity() {

    val url:String = "192.249.18.242:27017"
    var login_success = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin)

        val ID = findViewById<EditText>(R.id.text_id)
        val PW = findViewById<EditText>(R.id.text_pw)

        val btn_sign_in = findViewById<Button>(R.id.btn_signin)
        btn_sign_in!!.setOnClickListener {
            login_success(ID.text.toString(), PW.text.toString())
            if(login_success) {
                val intent = Intent(this@Signin, MainActivity::class.java)
                startActivity(intent)
            }
        }
        val btn_sign_up = findViewById<Button>(R.id.btn_signup)
        btn_sign_up!!.setOnClickListener {
            val intent = Intent(this@Signin, Signup::class.java)
            startActivity(intent)
        }
    }

    fun login_success(id:String, pw:String) {
        try {

            var json = JSONObject()
            //입력해둔 edittext의 id와 pw값을 받아와 put해줍니다 : 데이터를 json형식으로 바꿔 넣어주었습니다.
            json.put("id", id)
            json.put("password", pw)
            val jsonString = json.toString() //완성된 json 포맷

            //이제 전송해볼까요?
            val requestQueue = Volley.newRequestQueue(this@Signin)
            val jsonObjectRequest =
                    JsonObjectRequest(Request.Method.POST, url, json, { response ->

                        //데이터 전달을 끝내고 이제 그 응답을 받을 차례입니다.
                        try {
                            println("데이터전송 성공")

                            //받은 json형식의 응답을 받아
                            val jsonObject = JSONObject(response.toString())

                            //key값에 따라 value값을 쪼개 받아옵니다.
                            val resultId = jsonObject.getString("approve_id")
                            val resultPassword = jsonObject.getString("approve_pw")

                            //만약 그 값이 같다면 로그인에 성공한 것입니다.
                            login_success = (resultId == "OK") and (resultPassword == "OK")
                        } catch (e: Exception) {
                            login_success = false
                            e.printStackTrace()
                        }
                    } //서버로 데이터 전달 및 응답 받기에 실패한 경우 아래 코드가 실행됩니다.
                    ) { error ->
                        error.printStackTrace()
                        //Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
            jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
            requestQueue.add<JSONObject>(jsonObjectRequest)
        } catch (e: JSONException) {
            e.printStackTrace()
            login_success = false
        }
    }
}