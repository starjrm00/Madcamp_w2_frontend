package com.example.madcamp_w2_frontend

import android.os.AsyncTask
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp_w2_frontend.Third_Page.WebToonAdapter_for_favorite
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.TextNode
import org.jsoup.select.Elements
import android.view.Window
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.madcamp_w2_frontend.Third_Page.WebToon
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import kotlinx.android.synthetic.main.fragment_3.*
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


class Fragment3_for_favorite() : AppCompatActivity() {
    val serverip = "http://192.249.18.212:3000"
    var webToonList : MutableList<WebToon> = ArrayList()
    var favorite = ArrayList<String>()
    lateinit var imageRecycler : RecyclerView
    lateinit var UniqueID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var bundle = intent.extras
        UniqueID = bundle!!.getString("UniqueID").toString()

        JSONTask_get_favorite(applicationContext, UniqueID).execute(serverip+"/get_favorite")
        setContentView(R.layout.fragment_3_for_favorite)

        val naverComic = "https://comic.naver.com/webtoon/weekday.nhn"
        getNaverData().execute(naverComic)

        imageRecycler = findViewById(R.id.webtoon_image_for_favorite)
        imageRecycler.layoutManager = GridLayoutManager(applicationContext, 3)
        imageRecycler.adapter = WebToonAdapter_for_favorite(webToonList, UniqueID, applicationContext)
        imageRecycler.setHasFixedSize(true)
    }

    @Suppress("DEPRECATION")
    inner class getNaverData : AsyncTask<String?, Void?, String?>() {

        // String 으로 값을 전달받은 값을 처리하고, Boolean 으로 doInBackground 결과를 넘겨준다.
        override fun doInBackground(vararg params: String?) : String? {
            try {
                val document: Document = Jsoup.connect(params[0]).get()
                //title 읽어오기 + link 읽어오기
                val titleElements : Elements = document.select("div.col_inner ul li a.title")
                var titleList : MutableList<String> = ArrayList()
                var linkList : MutableList<String> = ArrayList()
                for (e in titleElements) {
                    val text : String = (e.childNode(0) as TextNode).wholeText
                    val link : String = e.attr("href")
                    titleList.add(text)
                    linkList.add("https://comic.naver.com" + link)
                }
                //썸네일 이미지 url 읽어오기
                var imageList : MutableList<String> = ArrayList()
                val imageElements : Elements = document.select("div.col_inner ul li a img")
                for (i in imageElements) {
                    val imageUrl : String = i.attr("src")
                    imageList.add(imageUrl)
                }
                /*
                for (i in imageElements) {
                    val imageUrl : String = i.attr("src")
                    imageList.add(imageUrl)
                }*/

                for (index in titleList.indices) {
                    if(titleList[index] in favorite){
                        webToonList.add(WebToon("Naver", titleList[index], imageList[index], linkList[index], true))
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            imageRecycler.layoutManager = GridLayoutManager(applicationContext, 3)
            imageRecycler.adapter = WebToonAdapter_for_favorite(webToonList, UniqueID, applicationContext)
        }
    }

    @Suppress("DEPRECATION")
    inner class getDaumData : AsyncTask<String?, Void?, String?>() {

        // String 으로 값을 전달받은 값을 처리하고, Boolean 으로 doInBackground 결과를 넘겨준다.
        override fun doInBackground(vararg params: String?) : String? {
            try {
                val document: Document = Jsoup.connect(params[0]).get()
                //title 읽어오기
                Log.i("daumTest", document.toString())
                val titleElements: Elements = document.select("div.wrap_webtoon")
                Log.i("daumTestDiv", titleElements.toString())
                var titleList: MutableList<String> = ArrayList()
                for (e in titleElements) {
                    val text: String = (e.childNode(0) as TextNode).wholeText
                    titleList.add(text)
                }
                //썸네일 이미지 url 읽어오기
                var imageList: MutableList<String> = ArrayList()
                val imageElements: Elements = document.select("div.col_inner ul li a img")
                for (i in imageElements) {
                    val imageUrl: String = i.attr("src")
                    imageList.add(imageUrl)
                }
                //세부 링크 url 읽어오기
                var linkList : MutableList<String> = ArrayList()
                val linkElements : Elements = document.select("div.col_inner ul li a")
                Log.i("NaverLink", linkElements.toString())
                for (index in titleList.indices) {
                    if(titleList[index] in favorite){
                        webToonList.add(WebToon("Daum", titleList[index], imageList[index], linkList[index], true))
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }
    }

    @Suppress("DEPRECATION")
    inner class JSONTask_get_favorite(mContext : Context, UniqueID: String) : AsyncTask<String?, String?, String>(){
        var mContext = mContext
        var UniqueID = UniqueID
        override fun doInBackground(vararg params: String?): String? {
            try{
                var jsonObject = JSONObject()
                jsonObject.accumulate("_id", UniqueID)
                var con: HttpURLConnection? = null
                var reader: BufferedReader? = null
                try{
                    var url = URL(params[0])
                    con = url.openConnection() as HttpURLConnection
                    con.requestMethod = "POST"
                    con!!.setRequestProperty("Cache-Control", "no-cache")
                    con.setRequestProperty(
                        "Content-Type",
                        "application/json"
                    )
                    con.setRequestProperty("Accept", "text/html")
                    con.doInput = true
                    con.doOutput = true
                    con.connect()

                    val outStream = con.outputStream
                    val writer =
                        BufferedWriter(OutputStreamWriter(outStream))
                    writer.write(jsonObject.toString())
                    writer.flush()
                    writer.close()


                    val stream = con.inputStream
                    reader = BufferedReader(InputStreamReader(stream))
                    val buffer = StringBuffer()
                    var line: String? = ""
                    while(reader.readLine().also{line = it} != null){
                        buffer.append(line)
                    }

                    return buffer.toString()
                }catch(e: MalformedURLException){
                    e.printStackTrace()
                }catch(e: IOException){
                    e.printStackTrace()
                }finally {
                    con?.disconnect()
                    try{
                        reader?.close()
                    }catch(e: IOException){
                        e.printStackTrace()
                    }
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
            return null
        }
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            val jObject = JSONObject(result)
            val jArray = jObject.getJSONArray("favorite")

            for(i in 0 until jArray.length()){
                val obj = jArray.getJSONObject(i)
                favorite.add(obj.getString("title"))
            }

            Log.d("favorite List", favorite.toString())
        }
    }
}