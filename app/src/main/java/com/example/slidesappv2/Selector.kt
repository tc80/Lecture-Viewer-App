package com.example.slidesappv2

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.view.View
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import java.lang.ref.WeakReference
import java.net.URI

class Selector(private val mainActivity: WeakReference<MainActivity>, private val view: View) {

    private val studRes = "https://studres.cs.st-andrews.ac.uk/"
    private val moduleRegex = Regex("\"\\w{2}\\d{4}\"") // "WWDDDD" - ex. CS3301
    private val lectureRegex = Regex("\"(\\w|\\d|\\s|\\(|\\)|_|-)*.pdf\"") // "(W|D|S)*.pdf" - ex. L02-Android.pdf
    private lateinit var alertDialog: AlertDialog.Builder

    fun selectPDF() {
        alertDialog = AlertDialog.Builder(mainActivity.get())
        selectModule(studRes)
    }

    private fun selectModule(modulesUrl: String) {
        alertDialog.setTitle("Select a Module")
        val stringRequest = StringRequest(
            Request.Method.GET,
            modulesUrl,
            Response.Listener<String> { res ->
                val matches = moduleRegex.findAll(res).map { it.value.trim('\"') }
                val array = sequenceToArray(matches)
                alertDialog.setItems(array) { _, which ->
                    val lecturesUrl = modulesUrl + array[which] + "/Lectures/"
                    selectLecture(lecturesUrl)
                }
                alertDialog.show()
            },
            Response.ErrorListener {
                mainActivity.get()?.showToast(it.toString())
            }
        )
        Volley.newRequestQueue(mainActivity.get()).add(stringRequest)
    }

    // note will only look within Lectures/, not recursively in file system
    private fun selectLecture(lecturesUrl: String) {
        alertDialog.setTitle("Select a Lecture")
        val stringRequest = StringRequest(
            Request.Method.GET,
            lecturesUrl,
            Response.Listener<String> { res ->
                val matches = lectureRegex.findAll(res).map { it.value.trim('\"') }
                val array = sequenceToArray(matches)
                if (array.isEmpty()) {
                    mainActivity.get()?.showToast("No Lectures Found")
                    return@Listener
                }
                alertDialog.setItems(array) { _, which ->
                    val selected = array[which]
                    val pdfURL = lecturesUrl + selected
                    mainActivity.get()?.showSnackbar(view, "Selected: $selected", 5000,
                        "VIEW IN BROWSER", View.OnClickListener {
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfURL))
                            mainActivity.get()?.startActivity(browserIntent)
                    })
                }
                alertDialog.show()
            },
            Response.ErrorListener {
                mainActivity.get()?.showToast(it.toString())
            }
        )
        Volley.newRequestQueue(mainActivity.get()).add(stringRequest)

    }

    // tried generics but Array<T> is not allowed
    fun sequenceToArray(sequence: Sequence<String>): Array<CharSequence?> {
        val list = ArrayList<CharSequence>()
        list.addAll(sequence)
        val array = arrayOfNulls<CharSequence>(list.size)
        list.toArray(array)
        return array
    }

}