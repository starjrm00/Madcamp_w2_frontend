package com.example.madcamp_w2_frontend.Third_Page

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.android1.Third_Page.MapActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import kotlinx.android.synthetic.main.fragment_3.*


class Fragment3 : Fragment(), OnMapReadyCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /*get map button*/
        btn_Location.setOnClickListener{
            var radius = et_input_radius.text.toString()
            var radiusInt: Int = Integer.parseInt(radius);
            var intent = Intent(it.context, MapActivity::class.java)
            intent.putExtra("radius", radiusInt)
            startActivity(intent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_3, container, false)
        return rootView
    }

    //override fun onMapReady(p0: GoogleMap?) {}

}