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
import androidx.core.content.ContextCompat.startActivity
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.signin.*
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import kotlin.reflect.typeOf


class Signin: AppCompatActivity() {
    var login_success = false
    private var callbackManager: CallbackManager? = null
    val serverip = "http://192.249.18.242:3000"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(this.applicationContext)
        setContentView(R.layout.signin)

        val ID = findViewById<EditText>(R.id.text_id)
        val PW = findViewById<EditText>(R.id.text_pw)

        val btn_sign_in = findViewById<Button>(R.id.btn_signin)
        btn_sign_in!!.setOnClickListener {
            Log.d("touch", "try to signin")
            try_login(ID.text.toString(), PW.text.toString())
        }

        val btn_sign_up = findViewById<Button>(R.id.btn_signup)
        btn_sign_up!!.setOnClickListener {

            val intent = Intent(this@Signin, Signup::class.java)
            startActivity(intent)
            Log.d("touch","finish signup")
        }

        callbackManager = CallbackManager.Factory.create()

        facebook_login_btn.setReadPermissions(
            Arrays.asList(
                "public_profile",
                "email"
            )
        )
        facebook_login_btn.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                var accessToken = loginResult.accessToken

                //token으로 이름 등 정보 받는 법
                val graphRequest = GraphRequest.newMeRequest(
                    loginResult.accessToken
                ) { `object`, response ->
                    var userName = `object`.get("name") as String
                    FacebookAccess.JSONTask(accessToken.userId, userName)
                        .execute(serverip+"/facebookLogin")
                }
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
        getPermission()
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
            Log.d("try_login", "in the try")
            var jsonTask = com.example.madcamp_w2_frontend.Signin.JSONTask(id, pw, applicationContext)
            Log.d("try_login", "let's execute jsonTask")
            Log.d("try_login", "url is "+serverip+"/login")
            jsonTask.execute(serverip+"/login")
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
                Log.d("JSONTask", "in 1st try")

                try {
                    Log.d("JSONTask", "in 2nd try")
                    val url = URL(params[0])
                    con = url.openConnection() as HttpURLConnection
                    Log.d("url", url.toString())
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
            Log.d("onPostExecute", result!!)
            val jObject = JSONObject(result)
            Log.d("onPostExecute", jObject.getString("loginSuccess"))
            if (jObject.getString("loginSuccess") == "Success") {
                success = true
                Toast.makeText(mContext, "로그인 성공", Toast.LENGTH_LONG).show()
                val intent = Intent(mContext, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                var bundle = Bundle()
                bundle.putString("UniqueID", jObject.getString("uniqueId"))
                intent.putExtras(bundle)
                startActivity(mContext, intent, null)
            }
        }
    }
    private fun getPermission() {
        var permission: PermissionListener = object: PermissionListener {
            override fun onPermissionGranted() {
                Toast.makeText(applicationContext,"권한이 허용되었습니다", Toast.LENGTH_SHORT).show()
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(applicationContext,"권한이 거부되었습니다", Toast.LENGTH_SHORT).show()
            }
        }

        TedPermission.with(this)
            .setPermissionListener(permission)
            .setRationaleMessage("카메라 사용을 위해 권한을 허용해주세요")
            .setDeniedMessage("권한을 거부하였습니다.")
            .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA, android.Manifest.permission.CALL_PHONE, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.READ_CONTACTS, android.Manifest.permission.WRITE_CONTACTS)
            .check()
    }
}