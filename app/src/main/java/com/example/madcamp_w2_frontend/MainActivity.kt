package com.example.madcamp_w2_frontend

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.madcamp_w2_frontend.Second_Page.Fragment2
import kotlinx.android.synthetic.main.activity_main.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException


class MainActivity : AppCompatActivity() {
    lateinit var UniqueID:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i("test", "1")
        val adapter = PageAdapter(supportFragmentManager)

        var bundle = intent.extras
        UniqueID = bundle!!.getString("UniqueID")!!
        Log.d("get UniqueID", UniqueID)

        adapter.addFragment(Fragment1(UniqueID), "Tab1")
        adapter.addFragment(Fragment2(UniqueID), "Tab2")
        adapter.addFragment(Fragment3(UniqueID), "Tab3")

        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }
}