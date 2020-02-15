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


class Downloader3(private val mainActivity: WeakReference<MainActivity>, private val view: View) {

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

                Renderer(mainActivity.get()!!, view).render(parcelFileDescriptor)

//                        val renderer = PdfRenderer(parcelFileDescriptor)
//                        mainActivity.get()?.showToast("PAGE COUNT IS " + renderer.pageCount.toString())
//                        val pageCount = renderer.pageCount
//
//                        //val imageView = mainActivity.get()?.findViewById<ImageView>(R.id.my_pdf)
//
//                        val imageView = ImageView(mainActivity.get())
//
//                        val bitmap = Bitmap.createBitmap(imageView.width, imageView.height, Bitmap.Config.ARGB_4444)
//
//                        val matrix = imageView.imageMatrix
//                        val rect = Rect(0, 0, imageView.width, imageView.height)
//
//                        renderer.openPage(0).render(bitmap, rect, matrix, RENDER_MODE_FOR_PRINT);
//
//                        imageView.imageMatrix = matrix;
//                        imageView.setImageBitmap(bitmap);
//                        imageView.invalidate();

                //renderer.close();

//                for (i in 0..pageCount) {
//                    val page = renderer.openPage(i)
//
//                    // say we render for showing on the screen
//                    page.render(mBitmap, null, null, Page.RENDER_MODE_FOR_DISPLAY)
//
//                    // do stuff with the bitmap
//
//                    // close the page
//                    page.close()
//                }
//
//                // close the renderer
//                renderer.close()


            }
        }

        mainActivity.get()?.registerReceiver(broadcastReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

}