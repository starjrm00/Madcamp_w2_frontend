package com.example.madcamp_w2_frontend.Third_Page

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.madcamp_w2_frontend.R
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class WebToonAdapter_for_favorite(val WebToonList:MutableList<WebToon>, val UniqueID : String, val context : Context?):
    RecyclerView.Adapter<WebToonAdapter_for_favorite.viewHolder>(){

    val mContext = context
    val serverip = "http://192.249.18.212:3000"

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val image = itemView.findViewById<ImageView>(R.id.webtoon_thumbnail)
        val title = itemView.findViewById<TextView>(R.id.webtoon_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.webtoon_item_for_favorite, parent, false)

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
                    v?.context?.startActivity(intent)
                }
            })
        }
    }


    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        if (mContext != null) {
            Glide.with(mContext).load(WebToonList[position].ImageUrl).into(holder.image)
        }
        holder.title?.setText(WebToonList[position].title)
    }

    override fun getItemCount(): Int {
        return WebToonList.size
    }

}