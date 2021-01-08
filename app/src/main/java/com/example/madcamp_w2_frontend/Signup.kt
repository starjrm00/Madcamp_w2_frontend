package com.example.madcamp_w2_frontend

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class Signup:AppCompatActivity() {
    var signup_success = false
    val url:String = "http://192.249.18.242:3000"
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.signup)
        val ID = findViewById<EditText>(R.id.Signup_id)
        val PW = findViewById<EditText>(R.id.Signup_pw)
        val btn = findViewById<Button>(R.id.btn_signup_page)
        btn!!.setOnClickListener{
            try_sign_up(ID.text.toString(), PW.text.toString())
            if(signup_success){
                //성공 메세지 토스트
            }
            else{
                //실패 메세지 토스트
            }
            finish()
        }
    }
    fun try_sign_up(id:String, pw:String){
        try {
            val json = JSONObject()

            json.put("request", "signup")
            json.put("id", id)
            json.put("pw", pw)
            val jsonString = json.toString()
            val requestQueue = Volley.newRequestQueue(this@Signup)
            val jsonObjectRequest =
                JsonObjectRequest(Request.Method.POST, url, json, { response ->

                    //데이터 전달을 끝내고 이제 그 응답을 받을 차례입니다.
                    try {
                        println("데이터전송 성공")

                        //받은 json형식의 응답을 받아
                        val jsonObject = JSONObject(response.toString())

                        signup_success = jsonObject.getString("result").equals("true")

                    } catch (e: Exception) {
                        signup_success = false
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
        }catch (e: JSONException) {
            e.printStackTrace()
            signup_success = false
        }
    }
}