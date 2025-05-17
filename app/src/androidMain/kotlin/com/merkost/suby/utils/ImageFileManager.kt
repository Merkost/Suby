package com.merkost.suby.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Manages image storage for custom service icons and logos.
 *
 * Images are stored in app-specific external storage which:
 * - Persists until app uninstallation
 * - Is not automatically cleared by system
 * - Maintains user privacy as files are app-private
 */
class ImageFileManager(private val context: Context) {

    companion object {
        private const val DIRECTORY_NAME = "custom_service_images"
        private const val IMAGE_QUALITY = 90
    }

    /**
     * Saves an image for a custom service with compression.
     *
     * @param uri Source image URI
     * @param serviceName Name of the service to use in filename
     * @return Path to the saved image or null if saving failed
     */
    suspend fun saveCustomServiceImageToInternalStorage(uri: Uri, serviceName: String): String? =
        withContext(Dispatchers.IO) {
            try {
                val contentResolver = context.contentResolver
                val mimeType = contentResolver.getType(uri)
                val fileExtension = getFileExtension(mimeType)
                    ?: getMimeTypeFromUri(uri)
                    ?: return@withContext null

                val safeServiceName = sanitizeFileName(serviceName)

                val now = Clock.System.now()
                val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
                val date = formatDateTime(localDateTime)

                val fileName = "${safeServiceName}_$date.$fileExtension"
                val file = getOutputFile(fileName)

                file.parentFile?.mkdirs()

                contentResolver.openInputStream(uri)?.use { inputStream ->
                    val bitmap = BitmapFactory.decodeStream(inputStream)

                    bitmap?.let {
                        FileOutputStream(file).use { outputStream ->
                            val compressFormat = when (fileExtension.lowercase()) {
                                "jpg", "jpeg" -> Bitmap.CompressFormat.JPEG
                                "png" -> Bitmap.CompressFormat.PNG
                                else -> Bitmap.CompressFormat.JPEG
                            }

                            it.compress(compressFormat, IMAGE_QUALITY, outputStream)
                            outputStream.flush()
                        }
                        it.recycle()
                        return@withContext file.absolutePath
                    }
                }

                return@withContext null
            } catch (e: IOException) {
                Timber.tag("ImageFileManager").e(e, "Failed to save image")
                return@withContext null
            } catch (e: Exception) {
                Timber.tag("ImageFileManager").e(e, "Unexpected error saving image")
                return@withContext null
            }
        }

    /**
     * Format a LocalDateTime into a string with pattern yyyyMMdd_HHmmss
     */
    private fun formatDateTime(dateTime: kotlinx.datetime.LocalDateTime): String {
        return buildString {
            append(dateTime.year.toString().padStart(4, '0'))
            append(dateTime.monthNumber.toString().padStart(2, '0'))
            append(dateTime.dayOfMonth.toString().padStart(2, '0'))
            append("_")
            append(dateTime.hour.toString().padStart(2, '0'))
            append(dateTime.minute.toString().padStart(2, '0'))
            append(dateTime.second.toString().padStart(2, '0'))
        }
    }

    /**
     * Gets a URI for a saved image that can be used with image loading libraries.
     *
     * @param imagePath Path to the saved image
     * @return URI for the image or null if the path is invalid
     */
    fun getImageUri(imagePath: String): Uri? {
        return try {
            val file = File(imagePath)
            if (!file.exists()) return null

            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            Timber.tag("ImageFileManager").e(e, "Failed to get image URI")
            null
        }
    }

    /**
     * Deletes an image from storage.
     *
     * @param oldImageUrl Path to the image to delete
     * @return true if deletion was successful, false otherwise
     */
    fun deleteCustomServiceImageFromInternalStorage(oldImageUrl: String): Boolean {
        return kotlin.runCatching {
            val file = File(oldImageUrl)
            if (file.exists()) {
                file.delete()
            } else {
                false
            }
        }.getOrElse {
            Timber.tag("ImageFileManager").e(it, "Failed to delete image from storage")
            false
        }
    }

    /**
     * Cleans up orphaned images that are no longer associated with any service.
     *
     * @param activeImagePaths List of currently used image paths
     * @return Number of deleted files
     */
    fun cleanupOrphanedImages(activeImagePaths: List<String>): Int {
        var deletedCount = 0
        try {
            val directory = getImageDirectory()
            if (directory.exists()) {
                directory.listFiles()?.forEach { file ->
                    if (file.isFile && !activeImagePaths.contains(file.absolutePath)) {
                        if (file.delete()) {
                            deletedCount++
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.tag("ImageFileManager").e(e, "Error cleaning up orphaned images")
        }
        return deletedCount
    }

    private fun getImageDirectory(): File {
        return File(context.filesDir, DIRECTORY_NAME)
    }

    private fun getOutputFile(fileName: String): File {
        return File(getImageDirectory(), fileName)
    }

    private fun getFileExtension(mimeType: String?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
    }

    private fun getMimeTypeFromUri(uri: Uri): String? {
        val fileName = uri.lastPathSegment ?: return null
        val dotIndex = fileName.lastIndexOf('.')
        if (dotIndex >= 0 && dotIndex < fileName.length - 1) {
            return fileName.substring(dotIndex + 1)
        }
        return null
    }

    private fun sanitizeFileName(name: String): String {
        return name
            .trim()
            .replace(Regex("\\s+"), "_")
            .replace(Regex("[^a-zA-Z0-9_\\-]"), "")
            .take(64)
    }
}