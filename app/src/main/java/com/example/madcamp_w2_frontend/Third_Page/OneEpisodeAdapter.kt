package com.example.madcamp_w2_frontend.Third_Page

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.madcamp_w2_frontend.R


class OneEpisodeAdapter(val imageList:MutableList<String>, context: Context):

    RecyclerView.Adapter<OneEpisodeAdapter.viewHolder>(){

    val mContext = context

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        //val imageInEpi = itemView.findViewById(R.id.image_in_episode) as ImageView
        //val imageInEpi = itemView.findViewById(R.id.image_in_episode) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.episode, parent, false)

        return viewHolder(view)
    }



    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        //holder.imageInEpi.setText(imageList.get(position))
        //TODO(server에서 episode 사진 가져오기)
        if (mContext != null) {
            //Glide.with(mContext).load(imageList.get(position)).into(holder.imageInEpi)
        }

    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}