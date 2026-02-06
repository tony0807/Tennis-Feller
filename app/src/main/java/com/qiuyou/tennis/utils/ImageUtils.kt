package com.qiuyou.tennis.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.math.min

object ImageUtils {
    
    /**
     * Compress image to specified max size
     */
    fun compressImage(
        context: Context,
        uri: Uri,
        maxWidth: Int = 1080,
        maxHeight: Int = 1080,
        quality: Int = 85
    ): File? {
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            
            // Calculate scale
            val scale = min(
                maxWidth.toFloat() / bitmap.width,
                maxHeight.toFloat() / bitmap.height
            )
            
            val newWidth = (bitmap.width * scale).toInt()
            val newHeight = (bitmap.height * scale).toInt()
            
            // Resize bitmap
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
            
            // Compress to file
            val outputFile = File(context.cacheDir, "compressed_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(outputFile)
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.flush()
            outputStream.close()
            
            bitmap.recycle()
            resizedBitmap.recycle()
            
            return outputFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    
    /**
     * Get file size in MB
     */
    fun getFileSizeMB(file: File): Double {
        return file.length() / (1024.0 * 1024.0)
    }
}
