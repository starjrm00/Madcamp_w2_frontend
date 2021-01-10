package com.example.madcamp_w2_frontend

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp_w2_frontend.R

class ImageAdapter(val imageList:ArrayList<String>):
    RecyclerView.Adapter<ImageAdapter.viewHolder>(){

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val image = itemView.findViewById<ImageView>(R.id.gallery)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)

        return viewHolder(view).apply{
            itemView.setOnClickListener { v ->
                val curPos: Int = adapterPosition
                var item: String = imageList.get(curPos)
                var intent = Intent(v?.context, ShowBigImage::class.java)
                intent.putExtra("photo", item)

                v?.context?.startActivity(intent)
            }

            itemView.setOnLongClickListener { view ->
                val curPos: Int = adapterPosition
                var image: String = imageList[curPos]

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
            holder.image?.setImageBitmap(StringToBitmap(imageList[position]))
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    fun StringToBitmap(encodedString: String?): Bitmap? {
        return try {
            val encodeByte = Base64.decode(
                encodedString,
                Base64.DEFAULT
            ) // String 화 된 이미지를  base64방식으로 인코딩하여 byte배열을 만듬
            BitmapFactory.decodeByteArray(
                encodeByte,
                0,
                encodeByte.size
            ) //만들어진 bitmap을 return
        } catch (e: Exception) {
            e.message
            null
        }
    }

}