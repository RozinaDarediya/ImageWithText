package com.theta.imagewithtext

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Environment
import android.view.View
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ashish on 30/12/17.
 */
class Global {

    companion object {
        private val JPEG_FILE_PREFIX = "IMG_"
        private val JPEG_FILE_SUFFIX = ".jpg"

        private val imageFolderName = "Images"


        //create bitmap from view and returns it
        fun getBitmapFromView(view: View): Bitmap {
            //Define a bitmap with the same size as the view
            val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            //Bind a canvas to it
            val canvas = Canvas(returnedBitmap)
            //Get the view's background
            val bgDrawable = view.background
            if (bgDrawable != null) {
                //has background drawable, then draw it on the canvas
                bgDrawable.draw(canvas)
            } else {
                //does not have background drawable, then draw white background on the canvas
                canvas.drawColor(Color.WHITE)
            }
            // draw the view on the canvas
            view.draw(canvas)
            //return the bitmap
            return returnedBitmap
        }

        //createImageFile in internal storage
        @Throws(IOException::class)
        fun createImageFile(context: Context): File {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val imageFileName = JPEG_FILE_PREFIX + timeStamp + "_"

            val instanceRecordDirectory = File(Environment.getExternalStorageDirectory().toString() + File.separator + context.getString(R.string.app_name) + File.separator + imageFolderName)

            if (!instanceRecordDirectory.exists()) {
                try {
                    instanceRecordDirectory.mkdirs()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            val instanceRecord = File(instanceRecordDirectory.absolutePath + File.separator + imageFileName + JPEG_FILE_SUFFIX)
            if (!instanceRecord.exists()) {
                try {
                    instanceRecord.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }

            return instanceRecord
        }
    }
}