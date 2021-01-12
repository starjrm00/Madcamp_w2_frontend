package com.example.madcamp_w2_frontend

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp_w2_frontend.Third_Page.WebToon
import com.example.madcamp_w2_frontend.Third_Page.WebToonAdapter
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import kotlinx.android.synthetic.main.fragment_3.*
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.TextNode
import org.jsoup.select.Elements
import java.io.*
import java.lang.Thread.sleep
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


class Fragment3(UniqueID: String) : Fragment() {
    val serverip = "http://192.249.18.212:3000"
    var webToonList : MutableList<WebToon> = ArrayList()
    var favorite = ArrayList<String>()
    lateinit var document: Document
    lateinit var imageRecycler : RecyclerView
    val UniqueID = UniqueID
    lateinit var webView1: WebView
    lateinit var webView2: WebView

    private lateinit var callback: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        JSONTask_get_favorite(requireContext(), UniqueID).execute(serverip + "/get_favorite")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        btn_favorite.setOnClickListener{
            val intent = Intent(requireContext(), Fragment3_for_favorite::class.java)
            val bundle = Bundle()
            bundle.putString("UniqueID", UniqueID)
            intent.putExtras(bundle)
            startActivity(intent)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_3, container, false)
        imageRecycler = rootView.findViewById(R.id.webtoon_image_for_favorite) as RecyclerView
//        webView1 = rootView.findViewById(R.id.crawler1)
//        webView2 = rootView.findViewById(R.id.crawler2)
        //WebView 자바스크립트 활성화
//        webView1.settings.javaScriptEnabled = true
//        webView2.settings.javaScriptEnabled = true
        // 자바스크립트인터페이스 연결
        // 이걸 통해 자바스크립트 내에서 자바함수에 접근할 수 있음.
//        webView1.addJavascriptInterface(crawlingLezhin(), "Android")
        //webView2.addJavascriptInterface(crawlingDaum(), "Android")
        // 페이지가 모두 로드되었을 때, 작업 정의
//        webView1.webViewClient = object : WebViewClient() {
//            override fun onPageFinished(view: WebView, url: String) {
//                super.onPageFinished(view, url)
                // 자바스크립트 인터페이스로 연결되어 있는 getHTML를 실행
                // 자바스크립트 기본 메소드로 html 소스를 통째로 지정해서 인자로 넘김
//                view.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('body')[0].innerHTML);")
//            }
//        }
//        webView2.webViewClient = object : WebViewClient() {
//            override fun onPageFinished(view: WebView, url: String) {
//                super.onPageFinished(view, url)
                // 자바스크립트 인터페이스로 연결되어 있는 getHTML를 실행
                // 자바스크립트 기본 메소드로 html 소스를 통째로 지정해서 인자로 넘김
//                view.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('body')[0].innerHTML);")
//            }
//        }

        val naverComic = "https://comic.naver.com/webtoon/weekday.nhn"
        getNaverData().execute(naverComic)

        //Daum Webtoon
        /*
        val week : Array<String> = arrayOf("mon", "tue", "wed", "thu", "fri", "sat", "sun")
        val daumComic = "http://webtoon.daum.net/#day="
        for (day in week) {
            val daumUrl = daumComic + day + "&tab=day"
            getDaumData().execute(daumUrl)
        }*/

        //Lezhin Webtoon
//        val lezhinComic = "https://www.lezhin.com/ko/scheduled?day=1"
//        getLezhinData(lezhinComic+"day=1")
//
//        try {
//            sleep(10000)
//        }catch(e:InterruptedException){
//            e.printStackTrace()
//        }
        imageRecycler.layoutManager = GridLayoutManager(this.context, 3)
        imageRecycler.adapter = WebToonAdapter(webToonList, UniqueID, context)
        imageRecycler.setHasFixedSize(true)
        Log.d("make recycler", "Now finish make recyclerView")
        return rootView
    }


    @Suppress("DEPRECATION")
    inner class getNaverData : AsyncTask<String?, Void?, String?>() {

        // String 으로 값을 전달받은 값을 처리하고, Boolean 으로 doInBackground 결과를 넘겨준다.
        override fun doInBackground(vararg params: String?) : String? {
            try {
                document = Jsoup.connect(params[0]).get()
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
                        webToonList.add(
                            WebToon(
                                "Naver",
                                titleList[index],
                                imageList[index],
                                linkList[index],
                                true
                            )
                        )
                    }
                    else{
                        webToonList.add(
                            WebToon(
                                "Naver",
                                titleList[index],
                                imageList[index],
                                linkList[index],
                                false
                            )
                        )
                    }
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
                //세부 링크 url 읽어오기
                var linkList : MutableList<String> = ArrayList()
                val linkElements : Elements = document.select("div.col_inner ul li a")
                Log.i("NaverLink", linkElements.toString())
                for (index in titleList.indices) {
                    if(titleList[index] in favorite){
                        webToonList.add(
                            WebToon(
                                "Daum",
                                titleList[index],
                                imageList[index],
                                linkList[index],
                                true
                            )
                        )
                    }
                    else{
                        webToonList.add(
                            WebToon(
                                "Daum",
                                titleList[index],
                                imageList[index],
                                linkList[index],
                                false
                            )
                        )
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }
    }

    fun getLezhinData(link: String) {
        webView1.loadUrl(link)
    }
*/
    @Suppress("DEPRECATION")
    inner class JSONTask_get_favorite(mContext: Context, UniqueID: String) : AsyncTask<String?, String?, String>(){
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
                }catch (e: MalformedURLException){
                    e.printStackTrace()
                }catch (e: IOException){
                    e.printStackTrace()
                }finally {
                    con?.disconnect()
                    try{
                        reader?.close()
                    }catch (e: IOException){
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


    //logout
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
/*
    inner class crawlingLezhin {
        @JavascriptInterface
        fun getHtml(html: String) {
            //위 자바스크립트가 호출되면 여기로 html이 반환됨
            document = Jsoup.parse(html)
            //title 읽어오기
            //Log.i("lezhinTest", document.toString())
            //Log.i("lezhinTest?", "lala")
            val titleElements: Elements = document.select("ul.lzComic__list li a div div.lzComic__title")
            //Log.i("lezhinTestDiv", titleElements.toString())
            var titleList: MutableList<String> = ArrayList()
            for (e in titleElements) {
                val text: String = (e.childNode(0) as TextNode).wholeText
                Log.i("title", text)
                titleList.add(text)
            }
            //썸네일 이미지 url 읽어오기
            var imageList: MutableList<String> = ArrayList()
            val imageElements: Elements = document.select("ul.lzComic__list li a div picture img")
            for (i in imageElements) {
                val imageUrl: String = i.attr("src")
                imageList.add(imageUrl)
            }
            //세부 링크 url 읽어오기
            var linkList : MutableList<String> = ArrayList()
            val linkElements : Elements = document.select("ul.lzComic__list li a")
            for (i in linkElements){
                val link: String = i.attr("href")
                linkList.add("https://www.lezhin.com"+link)
                Log.i("LezhinLink", link)
            }
            //Log.i("LezhinLink", linkElements.toString())
            for (index in titleList.indices) {
                if(titleList[index] in favorite){
                    webToonList.add(WebToon("Lezhin", titleList[index], imageList[index], linkList[index], true ))
                } else{
                    webToonList.add(WebToon("Lezhin", titleList[index], imageList[index], linkList[index], false))
                }
            }
        }
    }
    */
}