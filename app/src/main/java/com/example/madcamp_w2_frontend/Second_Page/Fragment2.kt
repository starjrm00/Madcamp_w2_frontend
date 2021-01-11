package com.example.madcamp_w2_frontend.Second_Page

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp_w2_frontend.ImageAdapter
import com.example.madcamp_w2_frontend.R
import com.example.madcamp_w2_frontend.image_item
import com.example.madcamp_w2_frontend.list_item
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import kotlinx.android.synthetic.main.fragment_1.*
import kotlinx.android.synthetic.main.fragment_2.*
import org.json.JSONObject
import java.io.*
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class Fragment2(UniqueID: String) : Fragment() {
    private lateinit var callback: OnBackPressedCallback
    lateinit var recyclerView2 : RecyclerView
    var image_list = ArrayList<String>()
    private val pickImage = 100
    private val capturePhoto = 101
    private var imageUri:Uri? = null
    var isFabOpen = false
    val UniqueID = UniqueID
    val serverip = "http://192.249.18.212:3000"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_2, container, false)
        recyclerView2 = rootView.findViewById(R.id.rv_image)as RecyclerView
        recyclerView2.layoutManager = GridLayoutManager(this.context,3)
        getImageFromDB()
        recyclerView2.adapter = ImageAdapter(image_list, UniqueID)
        recyclerView2.setHasFixedSize(true)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        image_add_button.setOnClickListener {
            toggleFab()
        }
        btn_camera.setOnClickListener{
            toggleFab()
            takePicture()
        }

        btn_gallery.setOnClickListener{
            toggleFab()
            loadImage()
        }
    }

    private fun loadImage(){
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, pickImage)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == RESULT_OK) {
            /* gallery load */
            if (requestCode == pickImage) {
                imageUri = data?.data
                saveImageinMongod(imageUri)
                val bitmap =
                    MediaStore.Images.Media.getBitmap(context?.contentResolver, imageUri)
                var bitmapString : String? = BitmapToString(bitmap)
                if (bitmapString != null) {
                    image_list.add(bitmapString)
                }
            }
            /* camera load */
            if (requestCode == capturePhoto) {
                var bundle: Bundle? = data?.getExtras()
                var bitmap: Bitmap = bundle?.get("data") as Bitmap
                var changedUri: Uri = BitmapToUri(this.requireContext(), bitmap)
                saveImageinMongod(changedUri)
                var bitmapString : String? = BitmapToString(bitmap)
                if (bitmapString != null) {
                    image_list.add(bitmapString)
                }
            }

            refreshFragment(this, parentFragmentManager)
        }
    }
    fun BitmapToUri(context: Context, bitmap: Bitmap): Uri {
        var bytes =  ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path.toString())
    }

    private fun takePicture() {
        var capture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(capture, capturePhoto)
    }

    fun refreshFragment(fragment: Fragment, fragmentManager: FragmentManager){
        var ft: FragmentTransaction = fragmentManager.beginTransaction()
        ft.detach(fragment).attach(fragment).commit()
    }

    fun toggleFab() {
        if (isFabOpen) {
            val fab_close = AnimationUtils.loadAnimation(context, R.anim.fab_close)

            image_add_button.setImageResource(R.drawable.ic_baseline_add_24)
            btn_camera.startAnimation(fab_close)
            btn_gallery.startAnimation(fab_close)
            btn_camera.setClickable(false)
            btn_gallery.setClickable(false)
            isFabOpen = false
        } else {
            val fab_open = AnimationUtils.loadAnimation(context, R.anim.fab_open)

            image_add_button.setImageResource(R.drawable.ic_baseline_close_24)
            btn_camera.startAnimation(fab_open)
            btn_gallery.startAnimation(fab_open)
            btn_camera.setClickable(true)
            btn_gallery.setClickable(true)
            isFabOpen = true
        }
    }

    fun saveImageinMongod(uri : Uri?) {
        val bitmap : Bitmap =
            MediaStore.Images.Media.getBitmap(context?.contentResolver, uri)
        val bitmapString : String? = BitmapToString(bitmap)
        var jsonTask = com.example.madcamp_w2_frontend.SaveImage.JSONTask(bitmapString, UniqueID, context)
        Log.d("try_login", "let's execute jsonTask")
        jsonTask.execute(serverip+"/saveImage")
    }

    fun BitmapToString(bitmap: Bitmap): String? {
        val baos =
            ByteArrayOutputStream() //바이트 배열을 차례대로 읽어 들이기위한 ByteArrayOutputStream클래스 선언
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos) //bitmap을 압축 (숫자 70은 70%로 압축한다는 뜻)
        val bytes = baos.toByteArray() //해당 bitmap을 byte배열로 바꿔준다.
        return Base64.encodeToString(bytes, Base64.DEFAULT) //String을 retrurn
    }

    fun getImageFromDB() {
        var jsonTask = JSONTask_get_image(UniqueID, context)
        jsonTask.execute(serverip + "/getImages")
    }

    @Suppress("DEPRECATION")
    inner class JSONTask_get_image(UniqueID: String, mContext : Context?) : AsyncTask<String?, String?, String>(){
        var mContext = mContext
        var UniqueID = UniqueID
        override fun doInBackground(vararg params: String?): String? {
            try{
                var jsonObject = JSONObject()
                jsonObject.accumulate("_id", UniqueID)
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
            Log.i("test", "1")
            val jObject = JSONObject(result)
            Log.d("result log", result!!)
            val jArray = jObject.getJSONArray("gallery")
            image_list.clear()

            for(i in 0 until jArray.length()){
                val obj = jArray.getJSONObject(i)
                image_list.add(obj.getString("imageBitmap"))
            }

            recyclerView2.adapter?.notifyDataSetChanged()
            recyclerView2.setHasFixedSize(true)
        }
    }

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