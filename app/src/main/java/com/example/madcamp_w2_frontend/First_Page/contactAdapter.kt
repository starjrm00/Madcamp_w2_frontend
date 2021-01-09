package com.example.madcamp_w2_frontend

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp_w2_frontend.R
import kotlinx.android.synthetic.main.list_item.view.*
import org.json.JSONObject
import java.io.*
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*
class contactAdapter(val JsonList:ArrayList<list_item>): RecyclerView.Adapter<contactAdapter.ViewHolder>(), Filterable{


    private var filteredList: ArrayList<list_item> = JsonList
    private var unfilterList: ArrayList<list_item> = JsonList
    val serverip = "http://192.249.18.242:3000"

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name = itemView.tv_name
        var number = itemView.tv_number
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): contactAdapter.ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(inflatedView).apply {

            /*Toast message*/
            itemView.setOnClickListener {
                val curPos: Int = adapterPosition
                var item: list_item = JsonList[curPos]

                //JsonList.removeAt(curPos)
                Toast.makeText(
                    parent.context,
                    "${curPos}\n ${item.name}\n ${item.number}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            /* call */
            itemView.iv_call.setOnClickListener {
                val curPos: Int = adapterPosition
                var item: list_item = JsonList[curPos]
                startActivity(inflatedView.context, Intent(Intent.ACTION_CALL, Uri.parse("tel:" + item.number)), Bundle())
            }

            /* message */
            itemView.iv_mms.setOnClickListener {
                val curPos: Int = adapterPosition
                var item: list_item = JsonList[curPos]
                startActivity(inflatedView.context, Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + item.number)), Bundle())
            }

            /* delete */
            itemView.setOnLongClickListener { view ->
                Log.d("delete", "delete")
                val curPos: Int = adapterPosition
                //var item: list_item = JsonList.get(curPos)
                //다이얼로그 생성
                var builder = AlertDialog.Builder(view?.context)
                val inflater = LayoutInflater.from(view?.context)
                val dialogView = inflater.inflate(R.layout.custom_dialog, null)
                val dialogText = dialogView.findViewById<TextView>(R.id.dg_content)
                dialogText.text = "${JsonList[curPos].name}\n${JsonList[curPos].number}\n삭제하시겠습니까?"
                builder.setView(dialogView)
                    .setTitle("연락처 삭제")
                    //.setMessage(dialogText.text.toString())
                    .setPositiveButton("OK") { _, _ ->
                        //builder.setTitle(dialogText.text.toString())
                        var jsonTask = JSONTask_delete_contact(JsonList[curPos], parent.context)
                        jsonTask.execute(serverip+"/delete_contact")
                        JsonList.remove(JsonList[curPos])
                        notifyItemRemoved(curPos)
                        notifyItemRangeChanged(curPos, JsonList.size)
                    }
                    .setNegativeButton("CANCEL") { _, _ ->
                    }
                    .show()
                true
            }
        }
    }


    override fun getItemCount(): Int {
        Log.d("size of recyclerview", JsonList.size.toString())
        return JsonList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Log.d("setting recyclerview", "set name = ${JsonList[position].name}, number = ${JsonList[position].number}")
        Log.d("holder", holder.toString())
        holder.name.text = (JsonList[position].name)
        holder.number.text = (JsonList[position].number)
        Log.d("onBindViewHolder","name is ${JsonList[position].name} and number is ${JsonList[position].number}")
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

    @Suppress("DEPRECATION")
    class JSONTask_delete_contact(item: list_item, mContext : Context) : AsyncTask<String?, String?, String>(){
        var item = item
        var mContext = mContext
        override fun doInBackground(vararg params: String?): String? {
            try{
                var jsonObject = JSONObject()
                jsonObject.accumulate("name", item.name)
                jsonObject.accumulate("number", item.number)
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
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            val jObject = JSONObject(result)
            if(jObject.getString("Success") == "Success"){
                Toast.makeText(mContext, "삭제 성공", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(mContext, "삭제 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }
}