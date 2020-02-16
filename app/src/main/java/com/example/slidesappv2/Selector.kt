/**
 * Group 3 Android Demo (CS3301, 02/2020)
 * @author 170006583
 */

package com.example.slidesappv2

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.lang.ref.WeakReference

class Selector(private val mainActivity: MainActivity, private val view: View) {

    private val longDuration = 5000 // five seconds
    private val moduleRegex = Regex("\"\\w{2}\\d{4}\"") // "WWDDDD" - ex. CS3301
    private val lectureRegex =
        Regex("\"(\\w|\\d|\\s|\\(|\\)|_|-)*.pdf\"") // "(W|D|S|_|-)*.pdf" - ex. L02-Android.pdf
    private var alertDialog = AlertDialog.Builder(mainActivity) // select window

    // select a current CS module from studRes
    internal fun selectModule(modulesUrl: String) {
        alertDialog.setTitle("Select a Module")

        // create http request to scrape modules
        val stringRequest = StringRequest(
            Request.Method.GET,
            modulesUrl,
            // called on http successful response
            Response.Listener<String> { res ->

                // use module regex to find modules
                val matches = moduleRegex.findAll(res).map { it.value.trim('\"') }
                val array = sequenceToArray(matches)

                // set modules as options
                alertDialog.setItems(array) { _, which ->

                    // called upon selection of a module
                    val lecturesUrl = modulesUrl + array[which] + "/Lectures/"

                    // try to select a lecture for this module
                    selectLecture(lecturesUrl)
                }
                alertDialog.show()
            },
            Response.ErrorListener {
                mainActivity.showToast("Failed to load modules: $it")
            }
        )

        // add http request to queue
        Volley.newRequestQueue(mainActivity).add(stringRequest)
    }

    // select a lecture for a particular CS module
    private fun selectLecture(lecturesUrl: String) {
        alertDialog.setTitle("Select a Lecture")

        // create http request to scrape lectures
        val stringRequest = StringRequest(
            Request.Method.GET,
            lecturesUrl,
            Response.Listener<String> { res ->

                // use lecture regex to find lectures
                val matches = lectureRegex.findAll(res).map { it.value.trim('\"') }
                val array = sequenceToArray(matches)

                // check if there are any lectures
                // note: we are only looking within Lectures/, not recursively in the file system
                if (array.isEmpty()) {
                    mainActivity.showToast("No Lectures Found")
                    return@Listener
                }

                // set lectures as options
                alertDialog.setItems(array) { _, which ->

                    // called upon selection of a lecture
                    val selected = array[which]
                    val pdfURL = lecturesUrl + selected

                    // start downloading the selected lecture
                    Downloader(WeakReference(mainActivity), view).downloadPDF(
                        pdfURL,
                        selected.toString()
                    )

                    // show snackbar to display selected module, option to open in browser
                    mainActivity.showSnackbar(view, "Selected: $selected", longDuration,
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

        // add http request to queue
        Volley.newRequestQueue(mainActivity).add(stringRequest)

    }

    // convert a sequence to an array
    // note: tried generics but Array<T> is not allowed in Kotlin
    private fun sequenceToArray(sequence: Sequence<String>): Array<CharSequence?> {
        val list = ArrayList<CharSequence>()
        list.addAll(sequence)
        val array = arrayOfNulls<CharSequence>(list.size)
        list.toArray(array)
        return array
    }


    companion object {

        // select an image
        internal fun selectImage(imageView: ImageView, mainActivity: WeakReference<MainActivity>) {
            mainActivity.get()?.enableShareButton(imageView)                // selected, so enable share button
            imageView.setColorFilter(Color.LTGRAY, PorterDuff.Mode.DARKEN)  // selected, so "select" the image with a filter
            imageView.invalidate()
        }

        // deselect an image
        internal fun deselectImage(
            imageView: ImageView,
            mainActivity: WeakReference<MainActivity>
        ) {
            mainActivity.get()?.disableShareButton()    // deselected, so disable share button
            imageView.clearColorFilter()                // remove filter that shows image is selected
            imageView.invalidate()
        }

    }

}