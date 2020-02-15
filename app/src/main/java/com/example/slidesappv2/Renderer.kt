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
import java.lang.ref.WeakReference
import android.R.attr.button



class Renderer(private val mainActivity: WeakReference<MainActivity>, private val view: View) {

    private val unselected = -1
    private val undo = "UNDO"

    internal fun render(parcelFileDescriptor: ParcelFileDescriptor) {

        val logo = mainActivity.get()?.findViewById<ImageView>(R.id.logo)
        val parent = logo?.parent as ViewGroup?
        parent?.removeView(logo)

        val renderer = PdfRenderer(parcelFileDescriptor)
        val scrollPane = mainActivity.get()?.findViewById<HorizontalScrollView>(R.id.scroll)!!
        val options = LinearLayout(mainActivity.get())
        val pageCount = renderer.pageCount
        val images = ArrayList<ImageView>()
        var indexSelected = unselected

        for (i in 0 until pageCount) {
            val page = renderer.openPage(i)
            val imageView = ImageView(mainActivity.get())
            imageView.id = i
            imageView.setPadding(10, 0, 10, 0)

            if (page.width == 0 || page.height == 0 || scrollPane.width == 0 || scrollPane.height == 0) {
                continue // ignore
            }

            val scale = (scrollPane.width.toDouble() / page.width).coerceAtMost(scrollPane.height.toDouble() / page.height)

            val bitmap = Bitmap.createBitmap(
                (page.width * scale).toInt(),
                (page.height * scale).toInt(),
                Bitmap.Config.ARGB_8888
            )

            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            imageView.setImageBitmap(bitmap)
            images.add(imageView)
            imageView.invalidate() // redraw

            page.close() // done with page

            imageView.setOnClickListener {
                val tempIndex = indexSelected
                if (indexSelected == i) {
                    // already selected, deselect
                    indexSelected = unselected
                    deselectImage(imageView)
                    mainActivity.get()?.showSnackbar(view, "Deselected slide $i.", null, undo,
                        View.OnClickListener {
                            selectImage(imageView)
                            indexSelected = tempIndex
                        }
                    )
                    return@setOnClickListener
                }
                if (indexSelected != unselected) {
                    images[indexSelected].clearColorFilter() // clear previous selection
                }
                indexSelected = i
                selectImage(imageView)
                mainActivity.get()?.showSnackbar(view, "Selected slide $i.", null, undo,
                    View.OnClickListener {
                        deselectImage(imageView)
                        indexSelected = tempIndex
                        if (indexSelected != unselected) {
                            selectImage(images[indexSelected])
                        }
                    }
                )
            }
            options.addView(imageView)
        }
        renderer.close()
        scrollPane.removeAllViews()
        scrollPane.addView(options)
        scrollPane.scrollTo(0, 0) // reset to beginning
        scrollPane.invalidate()
    }

    private fun selectImage(imageView: ImageView) {
        imageView.setColorFilter(Color.LTGRAY, PorterDuff.Mode.DARKEN)
        imageView.invalidate()
    }

    private fun deselectImage(imageView: ImageView) {
        imageView.clearColorFilter()
        imageView.invalidate()
    }


}