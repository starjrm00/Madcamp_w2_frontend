package com.example.madcamp_w2_frontend

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import kotlinx.android.synthetic.main.signin.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class Signin: AppCompatActivity() {

    private val url:String = "http://192.249.18.212:3000"
    private var login_success = false
    private var callbackManager: CallbackManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(this.applicationContext)
        setContentView(R.layout.signin)

        val ID = findViewById<EditText>(R.id.text_id)
        val PW = findViewById<EditText>(R.id.text_pw)

        btn_signin!!.setOnClickListener {
            Log.d("touch", "try login")
            login_success(ID.text.toString(), PW.text.toString())
            if(login_success) {
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
            }
        }

        btn_signup!!.setOnClickListener {
            Log.d("touch", "try signup")
            val intent = Intent(applicationContext, Signup::class.java)
            Log.d("touch", intent.toString())
            startActivity(intent)
            Log.d("touch","finish signup")
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

    fun login_success(id:String, pw:String) {
        try {

            var json = JSONObject()
            //입력해둔 edittext의 id와 pw값을 받아와 put해줍니다 : 데이터를 json형식으로 바꿔 넣어주었습니다.
            json.put("request", "signin")
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