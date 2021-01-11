package com.example.madcamp_w2_frontend.Third_Page

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.madcamp_w2_frontend.R
import com.example.madcamp_w2_frontend.ShowBigImage
import com.example.madcamp_w2_frontend.image_item
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class EpisodeAdapter(val epiList:MutableList<Episode>, context: Context):

    RecyclerView.Adapter<EpisodeAdapter.viewHolder>(){

    val mContext = context

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val thumbnail = itemView.findViewById(R.id.epi_thumbnail) as ImageView
        val title = itemView.findViewById(R.id.epi_title) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.episode_list_item, parent, false)

        return viewHolder(view).apply{
            itemView.setOnClickListener { v ->
                val curPos: Int = adapterPosition
                var item: Episode = epiList.get(curPos)
                var intent = Intent(v?.context, ShowEpisode::class.java)
                intent.putExtra("link", item.link)
                intent.putExtra("site", item.site)

                v?.context?.startActivity(intent)
            }
        }
    }



    override fun onBindViewHolder(holder: viewHolder, position: Int) {

        if (mContext != null) {
            Glide.with(mContext).load(epiList.get(position).ImageUrl).into(holder.thumbnail)
        }
        holder.title?.setText(epiList.get(position).title)
    }

    override fun getItemCount(): Int {
        return epiList.size
    }
}