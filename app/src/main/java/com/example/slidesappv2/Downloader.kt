package com.example.slidesappv2

import android.app.AlertDialog
import android.os.AsyncTask
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.lang.ref.WeakReference

//import com.example.slidesappv2.MainActivity



class Downloader(private val mainActivity: WeakReference<MainActivity>) : AsyncTask<MainActivity, Unit, Unit>() {

    private val studRes = "https://studres.cs.st-andrews.ac.uk/"
    private val moduleRegex = Regex("\"\\w{2}\\d{4}\"") // "WWDDDD" - ex. CS3301
    private val lectureRegex = Regex("\"(\\w|\\d|\\s|\\(|\\)|_|-)*.pdf\"") // "(W|D|S)*.pdf" - ex. L02-Android.pdf
    private lateinit var alertDialog: AlertDialog.Builder

    override fun onProgressUpdate(vararg values: Unit?) {
        super.onProgressUpdate(*values)
    }

    override fun onPostExecute(result: Unit?) {
        super.onPostExecute(result)

        alertDialog.setTitle("Choose an animal")

        val animals = arrayOf("horse", "cow", "camel", "sheep", "goat")
        alertDialog.setItems(animals) { dialog, which ->
            when (which) {
                0 -> { /* horse */ }
                1 -> { /* cow   */ }
                2 -> { /* camel */ }
                3 -> { /* sheep */ }
                4 -> { /* goat  */ }
            }
        }

        val dialog = alertDialog.create()
        dialog.show()

    }

    override fun doInBackground(vararg params: MainActivity?) {
        println("hello")

    }

    override fun onCancelled(result: Unit?) {
        super.onCancelled(result)
    }

    override fun onCancelled() {
        super.onCancelled()
    }

    override fun onPreExecute() {
        super.onPreExecute()
        alertDialog = AlertDialog.Builder(mainActivity.get())

    }

    fun requestWebsiteText(url: String) {
        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            Response.Listener<String> { res ->
                println("RESULTS")
                val matches = moduleRegex.findAll(res).map { it.value.trim('\"') }
                matches.forEach { result -> println(result) }
                println("done")
                mainActivity.get()?.showToast("hello")
            },
            Response.ErrorListener {
                mainActivity.get()?.showToast(it.toString())
            }
        )
        Volley.newRequestQueue(mainActivity.get()).add(stringRequest)
        //return null
    }

}