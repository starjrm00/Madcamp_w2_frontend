package com.example.madcamp_w2_frontend

import android.os.AsyncTask
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp_w2_frontend.Third_Page.WebToon
import com.example.madcamp_w2_frontend.Third_Page.WebToonAdapter
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.TextNode
import org.jsoup.select.Elements
import java.io.IOException
import android.view.Window
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import com.example.madcamp_w2_frontend.R
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import kotlinx.android.synthetic.main.fragment_3.*


class Fragment3(UniqueID: String) : Fragment() {
    var webToonList : MutableList<WebToon> = ArrayList()
    lateinit var imageRecycler : RecyclerView
    val UniqueID = UniqueID

    private lateinit var callback: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /*get map button*/
        //btn_Location.setOnClickListener{ }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_3, container, false)
        val naverComic = "https://comic.naver.com/webtoon/weekday.nhn"
        getNaverData().execute(naverComic)
        /*
        val week : Array<String> = arrayOf("mon", "tue", "wed", "thu", "fri", "sat", "sun")
        val daumComic = "http://webtoon.daum.net/#day="
        for (day in week) {
            val daumUrl = daumComic + day + "&tab=day"
            getDaumData().execute(daumUrl)
        }*/

        imageRecycler = rootView.findViewById(R.id.webtoon_image) as RecyclerView
        imageRecycler.layoutManager = GridLayoutManager(this.context, 3)
        imageRecycler.adapter = WebToonAdapter(webToonList, UniqueID, context)
        imageRecycler.setHasFixedSize(true)
        return rootView
    }


    @Suppress("DEPRECATION")
    inner class getNaverData : AsyncTask<String?, Void?, String?>() {

        // String 으로 값을 전달받은 값을 처리하고, Boolean 으로 doInBackground 결과를 넘겨준다.
        override fun doInBackground(vararg params: String?) : String? {
            try {
                val document: Document = Jsoup.connect(params[0]).get()
                //title 읽어오기
                val titleElements : Elements = document.select("div.col_inner ul li a.title")
                var titleList : MutableList<String> = ArrayList()
                for (e in titleElements) {
                    val text : String = (e.childNode(0) as TextNode).wholeText
                    titleList.add(text)
                }
                //썸네일 이미지 url 읽어오기
                var imageList : MutableList<String> = ArrayList()
                val imageElements : Elements = document.select("div.col_inner ul li a img")
                for (i in imageElements) {
                    val imageUrl : String = i.attr("src")
                    imageList.add(imageUrl)
                }

                for (index in titleList.indices) {
                    webToonList.add(WebToon("Naver", titleList[index], imageList[index]))
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            imageRecycler.layoutManager = GridLayoutManager(context, 3)
            imageRecycler.adapter = WebToonAdapter(webToonList, UniqueID, context)
        }
    }

<<<<<<< HEAD
    /*
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

                for (index in titleList.indices) {
                    webToonList.add(WebToon("Naver", titleList[index], imageList[index]))
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }
    }*/
=======
    //override fun onMapReady(p0: GoogleMap?) {}
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                var logout_dialog = Dialog(context)
                logout_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                val inflater = LayoutInflater.from(context)
                val dialogView = inflater.inflate(R.layout.logout, null)
                logout_dialog.setContentView(dialogView)
                logout_dialog.show()
                var logout_btn: Button = dialogView.findViewById(R.id.logout_accept)
                var cancel_btn: Button = dialogView.findViewById(R.id.logout_denied)
>>>>>>> e6231496b6f3bbe0d81220033d30303c6c4c0691

                logout_btn.setOnClickListener{
                    if(AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut()
                    }
                    activity?.finish()
                }
                cancel_btn.setOnClickListener{
                    logout_dialog.dismiss()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}