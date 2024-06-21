package com.merkost.suby

import android.app.Application
import android.util.Log
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.util.DebugLogger
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class SubyApplication : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .crossfade(true)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.20)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(5 * 1024 * 1024)
                    .build()
            }
            .logger(logger = DebugLogger(level = if (BuildConfig.DEBUG) Log.DEBUG else Log.ERROR))
            .respectCacheHeaders(false)
            .build()
    }

    private inner class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
                return
            }
            if (priority == Log.ERROR || priority == Log.WARN) {
                val crashlytics = Firebase.crashlytics
                if (t != null) {
                    crashlytics.recordException(t)
                } else {
                    val category = when (priority) {
                        Log.ERROR -> "E"
                        Log.WARN -> "W"
                        else -> throw IllegalStateException()
                    }
                    // https://firebase.google.com/docs/crashlytics/upgrade-sdk?platform=android
                    // To log a message to a crash report, use the following syntax:
                    // crashlytics.log("E/TAG: my message")
                    crashlytics.log("$category/$tag: $message")
                }
            }
        }
    }

}