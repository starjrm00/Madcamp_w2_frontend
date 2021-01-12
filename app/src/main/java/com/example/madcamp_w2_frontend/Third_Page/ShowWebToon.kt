package com.example.madcamp_w2_frontend.Third_Page

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.MotionEvent
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp_w2_frontend.R
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.TextNode
import org.jsoup.select.Elements
import java.io.IOException

class ShowWebToon: AppCompatActivity() {
    //episode list 보여주는 페이지
    lateinit var episodeRecycler : RecyclerView
    var epiList : MutableList<Episode> = ArrayList()
    lateinit var uniqueID : String
    lateinit var webToonTitle : String
    lateinit var webToonThumbnail : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.one_webtoon_list)
        var site : String = intent.getStringExtra("site")!!
        var link : String = intent.getStringExtra("link" )!!
        webToonTitle = intent.getStringExtra("webToonTitle")!!
        uniqueID = intent.getStringExtra("uniqueID")!!
        webToonThumbnail = intent.getStringExtra("webToonThumbnail")!!

        if (site == "Naver") {
            getNaverEpiList().execute(link)
        }

        episodeRecycler = findViewById(R.id.episode_list) as RecyclerView

    }

    @Suppress("DEPRECATION")
    inner class getNaverEpiList : AsyncTask<String?, Void?, String?>() {

        // String 으로 값을 전달받은 값을 처리하고, Boolean 으로 doInBackground 결과를 넘겨준다.
        override fun doInBackground(vararg params: String?) : String? {
            try {
                val document: Document = Jsoup.connect(params[0]).get()
                //episode title 불러오기
                val titleElements : Elements = document.select("div.webtoon table.viewList tbody tr td.title a")
                var titleList : MutableList<String> = ArrayList()
                var linkList : MutableList<String> = ArrayList()
                for (e in titleElements) {
                    val text : String = (e.childNode(0) as TextNode).wholeText
                    titleList.add(text)
                    val link : String = e.attr("href")
                    linkList.add("https://comic.naver.com" + link)
                }
                //episode thumbnail 불러오기
                val imageElements : Elements = document.select("div.webtoon table.viewList tbody tr td a img")
                var imageList : MutableList<String> = ArrayList()
                for (i in imageElements) {
                    val imageUrl : String = i.attr("src")
                    imageList.add(imageUrl)
                }

                for (index in titleList.indices) {
                    epiList.add(Episode(titleList[index], imageList[index], linkList[index], "Naver", webToonTitle, webToonThumbnail))
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            episodeRecycler.layoutManager = LinearLayoutManager(applicationContext)
            episodeRecycler.adapter = EpisodeAdapter(epiList, applicationContext, uniqueID)
        }
    }
}