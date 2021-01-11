package com.example.madcamp_w2_frontend.Third_Page

import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp_w2_frontend.R
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException

class ShowEpisode: AppCompatActivity() {
    //한 회차를 보여주는 페이지

    lateinit var oneEpisodeRecycler : RecyclerView
    var imageList : MutableList<String> = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.one_episode)
        var site : String = intent.getStringExtra("site")!!
        var link : String = intent.getStringExtra("link" )!!
        if (site == "Naver") {
            getNaverEpisode().execute(link)
        }

        oneEpisodeRecycler = findViewById(R.id.episode_image_list) as RecyclerView

    }

    @Suppress("DEPRECATION")
    inner class getNaverEpisode : AsyncTask<String?, Void?, String?>() {

        // String 으로 값을 전달받은 값을 처리하고, Boolean 으로 doInBackground 결과를 넘겨준다.
        override fun doInBackground(vararg params: String?) : String? {
            try {
                val document: Document = Jsoup.connect(params[0]).get()
                //image 불러오기
                val imageElements : Elements = document.select("div.wt_viewer img")
                for (i in imageElements) {
                    val imageUrl : String = i.attr("src")
                    imageList.add(imageUrl)
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            oneEpisodeRecycler.layoutManager = LinearLayoutManager(applicationContext)
            oneEpisodeRecycler.adapter = OneEpisodeAdapter(imageList, applicationContext)
        }
    }
}