package com.example.slidesappv2

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Button
import androidx.core.view.isInvisible
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.lang.ref.WeakReference

class Selector(private val mainActivity: MainActivity, private val view: View) {

    private val moduleRegex = Regex("\"\\w{2}\\d{4}\"") // "WWDDDD" - ex. CS3301
    private val lectureRegex = Regex("\"(\\w|\\d|\\s|\\(|\\)|_|-)*.pdf\"") // "(W|D|S)*.pdf" - ex. L02-Android.pdf
    private lateinit var alertDialog: AlertDialog.Builder

    internal fun selectModule(modulesUrl: String) {
        alertDialog = AlertDialog.Builder(mainActivity)
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
                mainActivity.showToast("Failed to load modules: $it")
            }
        )
        Volley.newRequestQueue(mainActivity).add(stringRequest)
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
                    mainActivity.showToast("No Lectures Found")
                    return@Listener
                }
                alertDialog.setItems(array) { _, which ->
                    val selected = array[which]
                    val pdfURL = lecturesUrl + selected
                    val btn = mainActivity.findViewById<Button>(R.id.button)
                    //btn.isClickable = false
                    btn.setOnClickListener{
                        //
                    }

                    Downloader3(WeakReference(mainActivity), view).downloadPDF(pdfURL, selected.toString())

                    mainActivity.showSnackbar(view, "Selected: $selected", 5000,
                        "VIEW IN BROWSER", View.OnClickListener {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfURL))
                        mainActivity.startActivity(browserIntent)
                    })
                }
                alertDialog.show()
            },
            Response.ErrorListener {
                mainActivity.showToast("Failed to load lectures: $it")
            }
        )
        Volley.newRequestQueue(mainActivity).add(stringRequest)

    }

    // tried generics but Array<T> is not allowed
    private fun sequenceToArray(sequence: Sequence<String>): Array<CharSequence?> {
        val list = ArrayList<CharSequence>()
        list.addAll(sequence)
        val array = arrayOfNulls<CharSequence>(list.size)
        list.toArray(array)
        return array
    }

}