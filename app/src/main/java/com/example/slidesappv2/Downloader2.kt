package com.example.slidesappv2

import android.app.AlertDialog
import android.os.AsyncTask
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.lang.ref.WeakReference
import android.os.Environment.getExternalStorageDirectory
import android.R
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.ProgressBar
import java.io.BufferedInputStream
import java.io.FileOutputStream
import java.net.URL


//import com.example.slidesappv2.MainActivity



class Downloader2(private val mainActivity: WeakReference<MainActivity>, private val view: View)  {

//    private val studRes = "https://studres.cs.st-andrews.ac.uk/"
//    private val moduleRegex = Regex("\"\\w{2}\\d{4}\"") // "WWDDDD" - ex. CS3301
//    private val lectureRegex =
//        Regex("\"(\\w|\\d|\\s|\\(|\\)|_|-)*.pdf\"") // "(W|D|S)*.pdf" - ex. L02-Android.pdf
//    private lateinit var alertDialog: AlertDialog.Builder
//
//
//    //    var view: View
//    var progressBar: ProgressBar? = null
//    var progressTextView: TextView? = null
//
//    override fun onPreExecute() {
//        super.onPreExecute()
//        Log.v("MainActivityLog", "Begin Download")
////        progressBar = view.findViewById(R.id.progressBar) as ProgressBar
////        progressTextView = view.findViewById(R.id.progressTextView) as TextView
//
//    }
//
//    override fun doInBackground(vararg f_url: String?): String {
//                var count: Int
//        try {
//            val url = URL(f_url[0])
//            val connection = url.openConnection()
//            connection.connect()
//            val lengthOfFile = connection.getContentLength()
//
//            val input = BufferedInputStream(url.openStream(), 8192)
//
//            val externalStorage = Environment.getExternalStorageDirectory()
//            val output = null //FileOutputStream(externalStorage.toString() + directory + zipfile)
//
//            val data = ByteArray(1024)
//            var total: Long = 0
//
////            while ((count = input.read(data)) != -1) {
////                total += count.toLong()
////                publishProgress("" + (total * 100 / lengthOfFile).toInt())
////
////                output.write(data, 0, count)
////            }
//
////            output.flush()
////            output.close()
//            input.close()
//        } catch (e: Exception) {
//            Log.e("Error: ", e.message)
//        }
//
//        return null
//    }
//
//
//    override fun onProgressUpdate(vararg progress: String?) {
//                super.onProgressUpdate(*progress)
//        Log.v("MainActivityLog", Integer.parseInt(progress[0]).toString() + " %")
//        progressBar.setProgress(Integer.parseInt(progress[0]))
//        progressTextView.setText(progress[0] + " %")
////    }
//    }
////
////    }
//
//    override fun onPostExecute(file_url: String?) {
//        super.onPostExecute(file_url)
//        // Hide the progress bar
//        progressBar!!.setVisibility(View.INVISIBLE)
//        // Show text
//        progressTextView!!.setText("Download Finished.")
//    }
//
//    override fun onCancelled(result: String?) {
//        super.onCancelled(result)
//    }
//
//    override fun onCancelled() {
//        super.onCancelled()
//    }
}