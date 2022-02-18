package com.example.trackerapp.utils

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.lang.Exception

class ImageManager {
    fun saveMapImage(context: Context, image: Bitmap, imageName: String) {
        val directory = ContextWrapper(context).getDir(MAP_IMAGES_DIR_NAME, Context.MODE_APPEND)
        if (!directory.exists()) directory.mkdir()
        val path = File(directory, imageName)

        try {
            FileOutputStream(path).let {
                image.compress(Bitmap.CompressFormat.PNG, 100, it)
                it.close()
            }
        } catch (e: Exception) {
            Log.e("SAVE_IMAGE", e.message, e)
        }
    }

    fun getMapImage(context: Context, imageName: String): Bitmap? {
        return try {
            val cw = ContextWrapper(context)
            val path = cw.getDir(MAP_IMAGES_DIR_NAME, Context.MODE_PRIVATE)
            val file = File(path, imageName)
            BitmapFactory.decodeStream(FileInputStream(file))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        }
    }
}