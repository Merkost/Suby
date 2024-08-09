package com.merkost.suby.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ImageFileManager(private val context: Context) {

    fun saveImageToInternalStorage(uri: Uri, serviceName: String): String? {
        val contentResolver = context.contentResolver
        val mimeType = contentResolver.getType(uri)
        val fileExtension = getFileExtension(mimeType) ?: return null

        val safeServiceName = serviceName
            .trim()
            .replace(Regex("\\s+"), "_")
            .replace(Regex("[^a-zA-Z0-9_\\-]"), "")

        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val date = dateFormat.format(Date())

        val fileName = "${safeServiceName}_$date.$fileExtension"
        val file = File(context.filesDir, fileName)

        return try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun getFileExtension(mimeType: String?): String? {
        return when (mimeType) {
            "image/jpeg" -> "jpg"
            "image/png" -> "png"
            else -> null
        }
    }
}