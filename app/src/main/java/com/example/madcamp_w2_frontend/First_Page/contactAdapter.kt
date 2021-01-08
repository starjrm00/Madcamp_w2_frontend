package com.example.madcamp_w2_frontend

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp_w2_frontend.R
import kotlinx.android.synthetic.main.list_item.view.*
import java.util.*
class contactAdapter(val JsonList:ArrayList<list_item>): RecyclerView.Adapter<contactAdapter.ViewHolder>(), Filterable{


    private var filteredList: ArrayList<list_item> = JsonList
    private var unfilterList: ArrayList<list_item> = JsonList


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name = itemView.findViewById<TextView>(R.id.tv_name)
        var number = itemView.findViewById<TextView>(R.id.tv_number)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): contactAdapter.ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return contactAdapter.ViewHolder(inflatedView).apply {

            /*Toast message*/
            /*itemView.setOnLongClickListener(object :View.OnLongClickListener{
                    override fun onLongClick(v: View?): Boolean {
                        val curPos: Int = adapterPosition
                        var item: list_item = JsonList.get(curPos)

                        //JsonList.removeAt(curPos)
                        Toast.makeText(parent.context,
                            "${curPos}\n ${item.name}\n ${item.number}",
                            Toast.LENGTH_SHORT
                        ).show()
                        return true
                    }

                })*/

            /* call */
            itemView.iv_call.setOnClickListener {
                val curPos: Int = adapterPosition
                var item: list_item = JsonList.get(curPos)
                val intent_call = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + item.number))
                startActivity(inflatedView.context, intent_call, Bundle())
            }

            /* message */
            itemView.iv_mms.setOnClickListener {
                val curPos: Int = adapterPosition
                var item: list_item = JsonList.get(curPos)
                val intent_mms = Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + item.number))
                startActivity(inflatedView.context, intent_mms, Bundle())
            }

            /* delete */
            itemView.setOnLongClickListener(object : View.OnLongClickListener {
                override fun onLongClick(view: View?): Boolean {
                    Log.d("delete", "delete")
                    val curPos: Int = adapterPosition
                    //var item: list_item = JsonList.get(curPos)
                    //다이얼로그 생성
                    var builder = AlertDialog.Builder(view?.context)
                    val inflater = LayoutInflater.from(view?.context)
                    val dialogView = inflater.inflate(R.layout.custom_dialog, null)
                    val dialogText = dialogView.findViewById<TextView>(R.id.dg_content)
                    dialogText.setText("${JsonList.get(curPos).name}\n${JsonList.get(curPos).number}\n삭제하시겠습니까?")
                    builder.setView(dialogView)
                        .setTitle("연락처 삭제")
                        //.setMessage(dialogText.text.toString())
                        .setPositiveButton("OK") { dialogInterface, i ->
                            //builder.setTitle(dialogText.text.toString())
                            JsonList.remove(JsonList.get(curPos))
                            notifyItemRemoved(curPos)
                            notifyItemRangeChanged(curPos, JsonList.size)
                        }
                        .setNegativeButton("CANCEL") { dialogInterface, i ->
                        }
                        .show()
                    return true
                }
            })

        }
    }


    override fun getItemCount(): Int {
        return this.JsonList.size
    }

    override fun onBindViewHolder(holder: contactAdapter.ViewHolder, position: Int) {
        holder.name.setText((JsonList[position].name))
        holder.number.text = (JsonList[position].number)
    }


        /* for search */
        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence): FilterResults {

                    val charString = constraint.toString()
                    //filteredList.clear()
                    filteredList = if (charString.isEmpty()) {
                        unfilterList
                    } else {
                        var FilteringList = ArrayList<list_item>()
                        val filterPattern = charString.toLowerCase()

                        for (item in unfilterList) {
                            if (item.name.toLowerCase().contains(filterPattern)){
                                FilteringList.add(item)
                            }
                        }
                        FilteringList
                    }


                    val filterResults = FilterResults()
                    filterResults.values = filteredList
                    Log.d("filterResult", filterResults.values.toString())

                    return filterResults
                }

                override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                    Log.d("Filter", "change the list elements?")
                    Log.d("Filter", filteredList.toString())
                    //filteredList = results?.values as ArrayList<list_item>
                    notifyDataSetChanged()
                    Log.d("Filter", filteredList.toString())
                    //filteredList.clear()

                }


            }
        }
    }









