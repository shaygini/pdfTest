package com.example.testpdf.pdfranderer

import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import java.io.File


class PdfCreateThumbnails() {

    var tumnailBitmp: Bitmap? = null
    val thumbnailsSize = 200
    val NUMBER_OF_COLUMNS = 5

    fun createTumnailSprite(file: File): Bitmap? {

        // create a new renderer
        val renderer = PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY))

        // let us just render all pages
        val itemsCount = renderer.pageCount

        val bitmapRect = getBitmapRect(itemsCount)
        val bitmap = Bitmap.createBitmap(
            bitmapRect.width(),
            bitmapRect.height(),
            Bitmap.Config.ARGB_8888)

        for (i in 0 until itemsCount) {
            val page: PdfRenderer.Page = renderer.openPage(i)

            val destClip = getThumbnailRect(i)

            // say we render for showing on the screen
            page.render(bitmap, destClip, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            // do stuff with the bitmap

            // close the page
            page.close()

            Log.d("PdfCreateThumbnails", "createTumnailSprite: $i")
        }

        // close the renderer
        renderer.close()

        return bitmap
    }

    fun getBitmapRect(itemsCount: Int): Rect {
        val numberOfRows: Int = (itemsCount / NUMBER_OF_COLUMNS) + 1
        return Rect(0,
            0,
            thumbnailsSize * NUMBER_OF_COLUMNS,
            thumbnailsSize * numberOfRows
        )
    }

    fun getThumbnailRect(currentIndex: Int): Rect {
        val row = currentIndex / NUMBER_OF_COLUMNS
        val column = currentIndex % NUMBER_OF_COLUMNS
        return Rect(
            column * thumbnailsSize,
            row * thumbnailsSize,
            (column + 1) * thumbnailsSize,
            (row + 1) * thumbnailsSize
        )
    }

    fun saveBitmapToPdf() {

    }

    fun getTumnailBitmap(): Bitmap? {
        return tumnailBitmp
    }

}