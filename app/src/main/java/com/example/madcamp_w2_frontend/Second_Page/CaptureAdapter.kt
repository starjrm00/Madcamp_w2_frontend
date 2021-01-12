package com.example.madcamp_w2_frontend.Second_Page

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp_w2_frontend.*

class CaptureAdapter(uriList:ArrayList<String>, mContext: Context) :
    RecyclerView.Adapter<CaptureAdapter.viewHolder>() {

    val serverip = "http://192.249.18.212:3000"
    val captureUriList = uriList
    val context = mContext

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<ImageView>(R.id.gallery)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CaptureAdapter.viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)

        return CaptureAdapter.viewHolder(view).apply {
            itemView.setOnClickListener { v ->
                val curPos: Int = adapterPosition
                var item: String = captureUriList.get(curPos)
                var intent = Intent(v?.context, ShowBigCapture::class.java)
                intent.putExtra("captureUri", item)
                v?.context?.startActivity(intent)
            }

            //TODO(longClick Listener로 사진 삭제)

        }
    }


    override fun onBindViewHolder(holder: CaptureAdapter.viewHolder, position: Int) {
        var captureUri = Uri.parse(captureUriList[position])
        holder.image?.setImageURI(captureUri)
    }

    override fun getItemCount(): Int {
        return captureUriList.size
    }
}