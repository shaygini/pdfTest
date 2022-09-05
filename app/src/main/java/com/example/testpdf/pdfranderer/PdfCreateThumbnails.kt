package com.example.testpdf.pdfranderer

import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import com.tom_roush.pdfbox.pdmodel.PDDocument
import java.io.*


class PdfCreateThumbnails() {

    var tumnailBitmp: Bitmap? = null
    val thumbnailsSize = 200
    val NUMBER_OF_COLUMNS = 5

    fun getBitmapWithPdfBox(inputFile: File): Bitmap? {
//        val inputStream = getpdfBoxpdfInputStream(inputFile)
//        val fileDescriptor = getFileDescriptorFromStream(inputStream)
        val fileDescriptor = getpdfBoxpdfInputStreamToFileDescriptor(inputFile)
        return createTumnailSprite(fileDescriptor)
    }


    fun getpdfBoxpdfInputStream(inputFile: File): InputStream {

//        val pd = PDDocument.load(inputFile, "your_password")
        val pd = PDDocument.load(inputFile)

//        pd.setAllSecurityToBeRemoved(true)
        val outputStream = ByteArrayOutputStream(70_000_000)
        pd.save(outputStream)

        val inputStream = PipedInputStream(70_000_000)
        val out = PipedOutputStream(inputStream)

        Thread {
            outputStream.writeTo(out)
        }.start()

        return inputStream
    }

    fun getpdfBoxpdfInputStreamToFileDescriptor(inputFile: File): ParcelFileDescriptor {

//      val pd = PDDocument.load(inputFile, "your_password")
        val pd = PDDocument.load(inputFile)

//      pd.setAllSecurityToBeRemoved(true)
        val outputStream = ByteArrayOutputStream(70_000_000)

        pd.save(outputStream)

        val dataToWrite = outputStream.toByteArray()

        return getFileDescriptor(dataToWrite)!!

    }

    @Throws(IOException::class)
    private fun getFileDescriptor(fileData: ByteArray): ParcelFileDescriptor? {
        Log.d("PDF", "Found " + fileData.size + " bytes of data")
        val pipe = ParcelFileDescriptor.createPipe()

        // Stream the file data to our ParcelFileDescriptor output stream
        val inputStream: InputStream = ByteArrayInputStream(fileData)
        val outputStream = ParcelFileDescriptor.AutoCloseOutputStream(pipe[1])
        var len: Int
        var index = 0
        var size = 0
        var buffer = ByteArray(2048)
        while (inputStream.read(buffer).also { len = it } >= 0) {
            size += len
            index ++
            Log.d("PDF", "getFileDescriptor: len: $len size:$size index: $index")
            outputStream.write(buffer,0, len)
            Log.d("PDF", "after write")

        }
        inputStream.close()
        outputStream.flush()
        outputStream.close()

        // Return the ParcelFileDescriptor input stream to the calling activity in order to read
        // the file data.
        return pipe[0]
    }


    fun createTumnailSprite(file: File): Bitmap? {
        val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        return createTumnailSprite(fileDescriptor)
    }

    fun createTumnailSprite(fileDescriptor: ParcelFileDescriptor): Bitmap? {

        // create a new renderer
        val renderer = PdfRenderer(fileDescriptor)

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