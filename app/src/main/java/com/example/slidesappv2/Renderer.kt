package com.example.slidesappv2

import android.graphics.*
import android.graphics.pdf.PdfRenderer
import android.media.Image
import android.os.ParcelFileDescriptor
import android.provider.ContactsContract
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.recyclerview.widget.RecyclerView

class Renderer(private val mainActivity: MainActivity, private val view: View) {

    internal fun render(parcelFileDescriptor: ParcelFileDescriptor) {
        val renderer = PdfRenderer(parcelFileDescriptor)
        val scrollPane = mainActivity.findViewById<HorizontalScrollView>(R.id.scroll)
        val options = LinearLayout(mainActivity)
        val pageCount = renderer.pageCount

        for (i in 0 until pageCount) {
            val page = renderer.openPage(i)
            val imageView = ImageView(mainActivity)
            imageView.id = i
            imageView.setPadding(10, 0, 10, 0)

            val width = if (page.width == 0) 1 else page.width
            val height = if (page.height == 0) 1 else page.height
            val scale = (scrollPane.width.toDouble() / width).coerceAtMost(scrollPane.height.toDouble() / height)

            val bitmap = Bitmap.createBitmap(
                (width * scale).toInt(),
                (height * scale).toInt(),
                Bitmap.Config.ARGB_8888
            )

            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            imageView.setImageBitmap(bitmap)
            imageView.invalidate() // redraw

            page.close() // done with page

            imageView.setOnClickListener {
                mainActivity.showToast(i.toString())
                // check if already selected
                imageView.setColorFilter(Color.LTGRAY, PorterDuff.Mode.DARKEN)

            }
            options.addView(imageView)
        }
        renderer.close()
        scrollPane.removeAllViews()
        scrollPane.addView(options)
        scrollPane.scrollTo(0, 0)
        scrollPane.invalidate()
    }

}