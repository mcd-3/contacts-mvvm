package com.matthew.carvalhodagenais.contactsmvvm.helpers

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import java.io.*
import java.lang.Exception
import java.util.*


class ImageDataHelper {

    companion object {

        private const val PROFILE_DIRECTORY = "profileImages"

        const val IMAGE_PATH = "data/data/com.matthew.carvalhodagenais.contactsmvvm/" +
            "app_${PROFILE_DIRECTORY}/"

        /**
         * Saves a bitmap image to internal storage
         *
         * @param bitmap Bitmap image to save
         * @param context Context of the application
         * @return String name of the file saved
         */
        fun saveImage(bitmap: Bitmap, context: Context): String {
            val contextWrapper: ContextWrapper = ContextWrapper(context.applicationContext)
            val directory: File = contextWrapper.getDir("profileImages", Context.MODE_PRIVATE)
            val path = File(directory, "${UUID.randomUUID()}_profile.png")
            var outputStream: FileOutputStream? = null

            try {
                outputStream = FileOutputStream(path)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            } catch (e: Exception) {
                Toast.makeText(context, "Could not save file", Toast.LENGTH_SHORT).show()
            } finally {
                try {
                    outputStream?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return path.name
        }

        /**
         * Reads and returns an image from internal storage
         *
         * @param path String Path to where the image is stored
         * @param name String Name of the image
         * @return Bitmap Image that was read
         */
        fun readImage(path: String, name: String): Bitmap? {
            var bitmap: Bitmap? = null
            try {
                val image = File(path, name)
                bitmap = BitmapFactory.decodeStream(FileInputStream(image))
            } catch (e: FileNotFoundException) {
                bitmap = null
                e.printStackTrace()
            }
            return bitmap
        }

        /**
         * Deletes an image from internal storage
         *
         * @param path String Path to where the image is stored
         * @param name String Name of the image
         * @return Boolean True if the file was deleted, false if it was not deleted
         */
        fun deleteImage(path: String, name: String): Boolean {
            var deleted = true
            try {
                val image = File(path, name)
                image.delete()
            } catch (e: FileNotFoundException) {
                deleted = false
                e.printStackTrace()
            }
            return deleted
        }
    }
}