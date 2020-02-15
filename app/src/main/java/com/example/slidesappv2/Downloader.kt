package com.example.slidesappv2

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.lang.ref.WeakReference
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.Environment
import android.os.ParcelFileDescriptor
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.net.toFile
import java.io.File
import java.io.FileDescriptor
import java.net.URI
import android.R.attr.data
import android.graphics.pdf.PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.pdf.PdfRenderer.Page.RENDER_MODE_FOR_PRINT
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.widget.ImageView


class Downloader(private val mainActivity: WeakReference<MainActivity>, private val view: View) {

    internal fun downloadPDF(url: String, title: String) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(title)
        val manager = mainActivity.get()?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val id = manager.enqueue(request)

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