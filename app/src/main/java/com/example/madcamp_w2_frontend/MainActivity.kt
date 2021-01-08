package com.example.madcamp_w2_frontend

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val adapter = PageAdapter(supportFragmentManager)

        adapter.addFragment(Fragment1(), "Tab1")
        adapter.addFragment(Fragment2(), "Tab2")
        adapter.addFragment(Fragment3(), "Tab3")

        viewPager.adapter = adapter
        tabLayout.setupWithViewpager(viewPager)
    }
}