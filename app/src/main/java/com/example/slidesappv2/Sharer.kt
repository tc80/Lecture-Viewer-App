/**
 * Group 3 Android Demo (CS3301, 02/2020)
 * @author 170006583
 */

package com.example.slidesappv2

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import java.lang.ref.WeakReference

class Sharer(private val mainActivity: WeakReference<MainActivity>) {

    private val exportType = "image/png"

    fun shareSlide(title: String, bitmap: Bitmap) {
        val contentResolver = mainActivity.get()?.contentResolver

        // create share intent
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = exportType

        // set display name and mime type of content
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, title)
            put(MediaStore.Images.Media.MIME_TYPE, exportType)
        }

        // insert content values into external content row
        val uri =
            contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                ?: return

        // write bitmap to output stream
        val out = contentResolver.openOutputStream(uri) ?: return
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) // write to output stream
        out.close()

        // share using chooser
        val resultIntent = Intent.createChooser(shareIntent, null)

        // add data associated with intent stored at uri
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)

        // start new activity
        mainActivity.get()?.startActivity(resultIntent)
    }

}