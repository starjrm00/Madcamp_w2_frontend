package com.example.madcamp_w2_frontend

import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp_w2_frontend.R

class ImageAdapter(val imageList:ArrayList<image_item>):
    RecyclerView.Adapter<ImageAdapter.viewHolder>(){

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val image = itemView.findViewById<ImageView>(R.id.gallery)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)

        return viewHolder(view).apply{
            itemView.setOnClickListener { v ->
                val curPos: Int = adapterPosition
                var item: image_item = imageList.get(curPos)
                var intent = Intent(v?.context, ShowBigImage::class.java)
                intent.putExtra("photo", item.photo)
                Log.d("putExtra", intent.putExtra("photo", item.photo).toString())

                v?.context?.startActivity(intent)
            }

            itemView.setOnLongClickListener { view ->
                val curPos: Int = adapterPosition
                var image: image_item = imageList[curPos]

                //다이얼로그 생성
                var builder = AlertDialog.Builder(view?.context)
                val inflater = LayoutInflater.from(view?.context)
                val dialogView = inflater.inflate(R.layout.custom_dialog, null)
                val dialogText = dialogView.findViewById<TextView>(R.id.dg_content)
                dialogText.setText("이미지를 삭제하시겠습니까?")
                builder.setView(dialogView)
                    .setTitle("이미지 삭제")
                    .setPositiveButton("확인") { dialogInterface, i ->
                        builder.setTitle(dialogText.text.toString())
                        imageList.remove(imageList.get(curPos))
                        notifyItemRemoved(curPos)
                        notifyItemRangeChanged(curPos, imageList.size)
                    }
                    .setNegativeButton("취소") { dialogInterface, i ->
                    }
                    .show()
                true
            }
        }
    }


    override fun onBindViewHolder(holder: viewHolder, position: Int) {
            holder.image?.setImageURI(imageList[position].photo)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

}