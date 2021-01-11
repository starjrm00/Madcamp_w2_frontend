package com.example.madcamp_w2_frontend.Third_Page

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.madcamp_w2_frontend.Fragment3
import com.example.madcamp_w2_frontend.R
import java.net.URL

class WebToonAdapter(val WebToonList:MutableList<WebToon>, val UniqueID : String, val context : Context?):
    RecyclerView.Adapter<WebToonAdapter.viewHolder>(){

    val mContext = context

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val image = itemView.findViewById<ImageView>(R.id.webtoon_thumbnail)
        val title = itemView.findViewById<TextView>(R.id.webtoon_title)
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
                    v?.context?.startActivity(intent)
                }
            })
        }
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        if (mContext != null) {
            Glide.with(mContext).load(WebToonList.get(position).ImageUrl).into(holder.image)
        }
        holder.title?.setText(WebToonList.get(position).title)
    }

    override fun getItemCount(): Int {
        return WebToonList.size
    }

}