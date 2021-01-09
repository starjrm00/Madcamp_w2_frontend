package com.example.madcamp_w2_frontend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.madcamp_w2_frontend.Second_Page.Fragment2
import kotlinx.android.synthetic.main.activity_main.tabLayout
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.activity_main.viewPager

class MainActivity : AppCompatActivity() {
    lateinit var UniqueID:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val adapter = PageAdapter(supportFragmentManager)

        adapter.addFragment(Fragment1(), "Tab1")
        adapter.addFragment(Fragment2(), "Tab2")
        adapter.addFragment(Fragment3(), "Tab3")

        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)

        var bundle = intent.extras
        UniqueID = bundle!!.getString("UniqueID")!!
        Log.d("get UniqueID", UniqueID)
    }

}