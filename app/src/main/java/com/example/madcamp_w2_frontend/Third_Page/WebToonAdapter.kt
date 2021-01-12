package com.example.madcamp_w2_frontend.Third_Page

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.madcamp_w2_frontend.Fragment3
import com.example.madcamp_w2_frontend.R
import com.example.madcamp_w2_frontend.list_item
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import kotlin.reflect.jvm.internal.impl.util.Check

class WebToonAdapter(val WebToonList:MutableList<WebToon>, val UniqueID : String, val context : Context?):
    RecyclerView.Adapter<WebToonAdapter.viewHolder>(){

    val mContext = context
    val serverip = "http://192.249.18.212:3000"

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val image = itemView.findViewById<ImageView>(R.id.webtoon_thumbnail)
        val title = itemView.findViewById<TextView>(R.id.webtoon_title)
        val favorite_btn: CheckBox = itemView.findViewById(R.id.favorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.webtoon_item, parent, false)

        return viewHolder(view).apply{
            itemView.setOnClickListener(object:View.OnClickListener{
                override fun onClick(v: View?) {
                    val curPos: Int = adapterPosition
                    var item: WebToon = WebToonList.get(curPos)
                    var intent: Intent = Intent(v?.context, ShowWebToon::class.java)
                    intent.putExtra("link", item.link)
                    intent.putExtra("site", item.site)
                    intent.putExtra("uniqueID", UniqueID)
                    intent.putExtra("webToonTitle", item.title)
                    intent.putExtra("webToonThumbnail", item.ImageUrl)
                    v?.context?.startActivity(intent)
                }
            })
            favorite_btn.setOnClickListener { v ->
                val curPos: Int = adapterPosition
                val item: WebToon = WebToonList.get(curPos)
                if (v is CheckBox) {
                    if (v.isChecked) {
                        val jsonTask = JSONTask_add_favorite(item.title, v.context, UniqueID)
                        jsonTask.execute(serverip+"/add_favorite")
                    } else {
                        val jsonTask = JSONTask_remove_favorite(item.title, v.context, UniqueID)
                        jsonTask.execute(serverip+"/remove_favorite")
                    }
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    class JSONTask_add_favorite(title: String, mContext : Context, UniqueID: String) : AsyncTask<String?, String?, String>(){
        val title = title
        var mContext = mContext
        var UniqueID = UniqueID
        override fun doInBackground(vararg params: String?): String? {
            try{
                var jsonObject = JSONObject()
                jsonObject.accumulate("_id", UniqueID)
                jsonObject.accumulate("title", title)
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
    }

    @Suppress("DEPRECATION")
    class JSONTask_remove_favorite(title: String, mContext : Context, UniqueID: String) : AsyncTask<String?, String?, String>(){
        val title = title
        var mContext = mContext
        var UniqueID = UniqueID
        override fun doInBackground(vararg params: String?): String? {
            try{
                var jsonObject = JSONObject()
                jsonObject.accumulate("_id", UniqueID)
                jsonObject.accumulate("title", title)
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
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        if (mContext != null) {
            Glide.with(mContext).load(WebToonList[position].ImageUrl).into(holder.image)
        }
        holder.title?.setText(WebToonList[position].title)
        if(WebToonList[position].favorite){
            holder.favorite_btn.isChecked = true
        }
    }

    override fun getItemCount(): Int {
        return WebToonList.size
    }

}