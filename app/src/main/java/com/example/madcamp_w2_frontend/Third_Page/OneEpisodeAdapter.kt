package com.example.madcamp_w2_frontend.Third_Page

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.madcamp_w2_frontend.R


class OneEpisodeAdapter(val imageList:MutableList<String>, context: Context):

    RecyclerView.Adapter<OneEpisodeAdapter.viewHolder>(){

    val mContext = context

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imageInEpi = itemView.findViewById(R.id.image_in_episode) as ImageView
        //val imageInEpi = itemView.findViewById(R.id.image_in_episode) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.episode, parent, false)

        return viewHolder(view)
    }



    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        //holder.imageInEpi.setText(imageList.get(position))
        //TODO(server에서 episode 사진 가져오기)
        if (mContext != null) {
            Glide.with(mContext).load(imageList.get(position)).into(holder.imageInEpi)
        }

        /*
        var bitmap : Bitmap? = null
        var mThread : Thread = Thread() {
            fun run() {
                try{
                    var url : URL = URL(imageList.get(position))
                    var conn : HttpURLConnection = url?.openConnection() as HttpURLConnection
                    conn.setDoInput(true)
                    conn.connect()

                    var inputStream: InputStream = conn.inputStream
                    bitmap = BitmapFactory.decodeStream(inputStream)
                } catch (e: MalformedURLException) {
                    e.printStackTrace();
                } catch (e: IOException) {
                    e.printStackTrace();
                }
            }
        }
        mThread.start();

        try {
            mThread.join();
            holder.imageInEpi.setImageBitmap(bitmap);
        } catch (e: InterruptedException) {
            e.printStackTrace();
        }*/

        //DownloadFilesTask(holder.imageInEpi).execute(imageList.get(position))

        //ImageDownload().execute(imageList.get(position))
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    /*
    @Suppress("DEPRECATION")
    class ImageDownload : AsyncTask<String?, Void?, Void?>() {

        var fileName: String? = null
        val SAVE_FOLDER = "/save_folder"

        override fun doInBackground(vararg params: String?): Void? {

            //다운로드 경로를 지정
            val savePath: String = Environment.getExternalStorageDirectory().toString().toString() + SAVE_FOLDER
            val dir = File(savePath)

            //상위 디렉토리가 존재하지 않을 경우 생성
            if (!dir.exists()) {
                dir.mkdirs()
            }

            //파일 이름 :날짜_시간
            val day = Date()
            val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA)
            fileName = java.lang.String.valueOf(sdf.format(day))

            //웹 서버 쪽 파일이 있는 경로
            val fileUrl = params[0]

            //다운로드 폴더에 동일한 파일명이 존재하는지 확인
            if (File("$savePath/$fileName").exists() == false) {
            } else {
            }
            val localPath = "$savePath/$fileName.jpg"
            try {
                val imgUrl = URL(fileUrl)
                //서버와 접속하는 클라이언트 객체 생성
                val conn: HttpURLConnection = imgUrl.openConnection() as HttpURLConnection
                val len: Int = conn.getContentLength()
                val tmpByte = ByteArray(len)
                //입력 스트림을 구한다
                val `is`: InputStream = conn.getInputStream()
                val file = File(localPath)
                //파일 저장 스트림 생성
                val fos = FileOutputStream(file)
                var read: Int
                //입력 스트림을 파일로 저장
                while (true) {
                    read = `is`.read(tmpByte)
                    if (read <= 0) {
                        break
                    }
                    fos.write(tmpByte, 0, read) //file 생성
                }
                `is`.close()
                fos.close()
                conn.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
        }
    }*/

    /*
    @Suppress("DEPRECATION")
    private class DownloadFilesTask(imageView : ImageView) : AsyncTask<String?, Void?, Bitmap?>() {

        var imageView = imageView

        override fun doInBackground(vararg params: String?): Bitmap? {
            var bmp: Bitmap? = null
            try {
                val img_url = params[0] //url of the image
                Log.i("imageURL", img_url.toString())
                val url = URL(img_url)
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return bmp
        }

        override fun onPostExecute(result: Bitmap?) {
            // doInBackground 에서 받아온 total 값 사용 장소
            imageView.setImageBitmap(result)
        }
    }*/
}