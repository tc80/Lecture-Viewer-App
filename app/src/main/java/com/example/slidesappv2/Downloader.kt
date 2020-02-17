/**
 * Group 3 Android Demo (CS3301, 02/2020)
 * @author 170006583
 */

package com.example.slidesappv2

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.view.View
import java.lang.ref.WeakReference
import android.app.DownloadManager
import android.graphics.Bitmap
import android.graphics.Color
import android.widget.ImageView
import kotlin.concurrent.thread

class Downloader(private val mainActivity: WeakReference<MainActivity>, private val view: View) {

    private val lightGray = "#F0F0F0"

    // download a pdf with a title at a url
    internal fun downloadPDF(url: String, title: String) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(title)
        val manager =
            mainActivity.get()?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val id = manager.enqueue(request)

        // remove or hide elements where the progress bar will be, enable reset
        mainActivity.get()?.resetScrollView()
        mainActivity.get()?.hideLogo()

        // assuming scroll view is non-null
        val scrollView = mainActivity.get()?.getScrollView()!!

        // create template where slides will go when loaded
        val template = ImageView(mainActivity.get())
        template.setImageBitmap(
            Bitmap.createBitmap(
                scrollView.width,
                scrollView.height,
                Bitmap.Config.ARGB_8888
            )
        )
        template.setBackgroundColor(Color.parseColor(lightGray))

        // add to scrollview
        scrollView.addView(template)
        scrollView.invalidate()

        // assuming progress bar is non-null
        val progressBar = mainActivity.get()?.getProgressBar()!!
        mainActivity.get()?.showProgress()

        // show progress bar loading until download is complete
        var downloading = true

        thread {

            while (downloading) {

                // create query for this download
                val query = DownloadManager.Query().setFilterById(id)
                val cursor = manager.query(query)

                if (cursor.moveToFirst()) {
                    // we have a result for the query
                    val downloadedBytes =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                    val totalBytes =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                    val progress = ((downloadedBytes).toDouble() / totalBytes) * 100
                    progressBar.progress = progress.toInt()
                    progressBar.invalidate()
                }

                // close cursor if non-null
                cursor ?: cursor.close()
            }
        }

        // broadcast receiver used when download is complete
        val broadcastReceiver = object : BroadcastReceiver() {

            // receiver has received its event
            override fun onReceive(context: Context?, intent: Intent?) {

                // no longer downloading, hide progress bars
                downloading = false
                mainActivity.get()?.hideProgress()

                // create intent to view downloaded file
                val uri = manager.getUriForDownloadedFile(id)
                val readFileIntent = Intent(Intent.ACTION_VIEW)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    .setDataAndType(uri, "application/pdf")

                // pending intent to give permission to read file
                val pIntent = PendingIntent.getActivity(mainActivity.get(), 0, readFileIntent, 0)

                // download is complete, show notification
                mainActivity.get()?.showNotification(
                    "Download Complete!",
                    "$title has been downloaded from $url.",
                    "View Download", pIntent
                )

                // get parcel fd from uri
                val parcelFileDescriptor =
                    mainActivity.get()?.contentResolver?.openFileDescriptor(uri, "r")

                if (parcelFileDescriptor == null) {
                    mainActivity.get()?.showToast("Cannot find download $title.")
                    return // failed to get parcel fd
                }

                // render downloaded pdf onto scroll view
                Renderer(mainActivity, view).render(title, parcelFileDescriptor)
            }
        }

        // register receiver to action download event
        mainActivity.get()?.registerReceiver(
            broadcastReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }

}