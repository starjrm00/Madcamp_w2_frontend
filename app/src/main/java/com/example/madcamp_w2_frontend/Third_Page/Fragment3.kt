package com.example.madcamp_w2_frontend

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.madcamp_w2_frontend.R
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import kotlinx.android.synthetic.main.fragment_3.*


class Fragment3(UniqueID: String) : Fragment(), OnMapReadyCallback {

    private lateinit var callback: OnBackPressedCallback

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
        return inflater.inflate(R.layout.fragment_3, container, false)
    }

    override fun onMapReady(p0: GoogleMap?) {
        TODO("Not yet implemented")
    }

    //override fun onMapReady(p0: GoogleMap?) {}
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                var logout_dialog = Dialog(context)
                logout_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                val inflater = LayoutInflater.from(context)
                val dialogView = inflater.inflate(R.layout.logout, null)
                logout_dialog.setContentView(dialogView)
                logout_dialog.show()
                var logout_btn: Button = dialogView.findViewById(R.id.logout_accept)
                var cancel_btn: Button = dialogView.findViewById(R.id.logout_denied)

                logout_btn.setOnClickListener{
                    if(AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut()
                    }
                    activity?.finish()
                }
                cancel_btn.setOnClickListener{
                    logout_dialog.dismiss()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}