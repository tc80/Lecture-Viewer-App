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
import android.widget.ProgressBar
import android.R.string.no





class Downloader(private val mainActivity: WeakReference<MainActivity>, private val view: View) {

    internal fun downloadPDF(url: String, title: String) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(title)
        val manager = mainActivity.get()?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val id = manager.enqueue(request)

        val progressBar = mainActivity.get()?.findViewById<ProgressBar>(R.id.progress)
//        progressBar?.setProgress(50, true)

//        progressBar.
//
//        Thread(Runnable {
//            var downloading = true
//
//            while (downloading) {
//
//                val q = DownloadManager.Query()
//                q.setFilterById(downloadId)
//
//                val cursor = manager.query(q)
//                cursor.moveToFirst()
//                val bytes_downloaded = cursor.getInt(
//                    cursor
//                        .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
//                )
//                val bytes_total =
//                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
//
//                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) === DownloadManager.STATUS_SUCCESSFUL) {
//                    downloading = false
//                }
//
//                val dl_progress = (bytes_downloaded / bytes_total * 100).toDouble()
//
//                runOnUiThread(Runnable { mProgressBar.progress = dl_progress.toInt() })
//
//                Log.d(Constants.MAIN_VIEW_ACTIVITY, statusMessage(cursor))
//                cursor.close()
//            }
//        }).start()




//        val query = DownloadManager.Query()
//        query.setFilterById(id)
//
//        Thread.sleep(1000)
//        val c = manager.query(query)
//        if (c.moveToFirst()) {
//            val sizeIndex = c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
//            val downloadedIndex = c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
//            val size = c.getInt(sizeIndex)
//            val downloaded = c.getInt(downloadedIndex)
//            var progress = 0.0
//            if (size != -1) progress = downloaded * 100.0 / size
//            println(sizeIndex)
//            println(downloadedIndex)
//            println(size)
//            println(downloaded)
//            println(progress)
//            // At this point you have the progress as a percentage.
//            mainActivity.get()?.showToast(progress.toString())
//        } else {
//            mainActivity.get()?.showToast("sad")
//        }

//        val query = DownloadManager.Query()
//        query.setFilterByStatus(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
//
////        query.
////            // progress bar async
////        manager.query(DownloadManager.Query(
////
////        ))

        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val uri = manager.getUriForDownloadedFile(id)
                val readFileIntent = Intent(Intent.ACTION_VIEW)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    .setDataAndType(uri, "application/pdf")
                val pIntent = PendingIntent.getActivity(mainActivity.get(), 0, readFileIntent, 0)
                mainActivity.get()?.showNotification(
                    "Download Complete!",
                    "$title has been downloaded from $url.",
                    "View Download", pIntent)

                val parcelFileDescriptor = mainActivity.get()?.contentResolver?.openFileDescriptor(uri, "r")

                if (parcelFileDescriptor == null) {
                    mainActivity.get()?.showToast("Cannot find download $title.")
                    return
                }

                Renderer(mainActivity, view).render(parcelFileDescriptor)
            }
        }

        mainActivity.get()?.registerReceiver(broadcastReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

}