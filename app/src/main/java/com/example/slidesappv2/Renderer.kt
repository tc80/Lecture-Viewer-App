/**
 * Group 3 Android Demo (CS3301, 02/2020)
 * @author 170006583
 */

package com.example.slidesappv2

import android.graphics.*
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import java.lang.ref.WeakReference

class Renderer(private val mainActivity: WeakReference<MainActivity>, private val view: View) {

    private val unselected = -1 // index indicating no slide selected
    private val undo = "UNDO"

    // render the pages of a downloaded pdf as bitmaps
    internal fun render(fileName: String, parcelFileDescriptor: ParcelFileDescriptor) {

        // enable reset button
        mainActivity.get()?.enableResetButton()

        // create pdf renderer from parcel fd
        val renderer = PdfRenderer(parcelFileDescriptor)

        // get scroll view, assume non-null
        val scrollView = mainActivity.get()?.getScrollView()!!

        // create new linear layout to hold the bitmaps
        val options = LinearLayout(mainActivity.get())

        val pageCount = renderer.pageCount // page count
        val images = ArrayList<ImageView>() // array list to hold bitmaps
        var indexSelected = unselected  // holds selected index

        // iterate over pages in pdf
        for (i in 0 until pageCount) {
            val page = renderer.openPage(i) // render the page
            val imageView = ImageView(mainActivity.get())
            val slideTitle = "$fileName (Slide ${i+1})"
            imageView.id = i
            imageView.setPadding(10, 10, 10, 10)

            if (page.width == 0 || page.height == 0 || scrollView.width == 0 || scrollView.height == 0) {
                mainActivity.get()?.showToast("Slide $i is invalid, skipping....")
                continue // ignore invalid pages
            }

            // create empty bitmap and determine how to best fit the page into the scrollview
            var bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
            val scale =
                (scrollView.width.toDouble() / page.width).coerceAtMost(scrollView.height.toDouble() / page.height)

            // scale the bitmap for the best look
            bitmap = Bitmap.createScaledBitmap(
                bitmap,
                (scale * page.width).toInt(),
                (scale * page.height).toInt(),
                true
            )

            // render the pdf page onto the bitmap
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            imageView.setImageBitmap(bitmap)
            images.add(imageView)
            imageView.invalidate()
            page.close()

            // logic for selecting/deselecting a particular slide
            imageView.setOnClickListener {
                val tempIndex = indexSelected
                if (indexSelected == i) {
                    // already selected, deselect
                    indexSelected = unselected
                    Selector.deselectImage(imageView, mainActivity)
                    mainActivity.get()?.showSnackbar(view, "Deselected slide ${i + 1}.", null, undo,
                        View.OnClickListener {
                            Selector.selectImage(slideTitle, imageView, mainActivity)
                            indexSelected = tempIndex
                        }
                    )
                    return@setOnClickListener
                }
                if (indexSelected != unselected) {
                    images[indexSelected].clearColorFilter() // clear previous selection
                }
                indexSelected = i
                Selector.selectImage(slideTitle, imageView, mainActivity)
                mainActivity.get()?.showSnackbar(view, "Selected slide ${i + 1}.", null, undo,
                    View.OnClickListener {
                        Selector.deselectImage(imageView, mainActivity)
                        indexSelected = tempIndex
                        if (indexSelected != unselected) {
                            Selector.selectImage(slideTitle, images[indexSelected], mainActivity)
                        }
                    }
                )
            }
            options.addView(imageView)
        }
        renderer.close()

        // update scroll view
        mainActivity.get()?.resetScrollView()
        scrollView.scrollTo(0, 0) // move scroll view to front
        scrollView.addView(options)
        scrollView.invalidate()
    }

}