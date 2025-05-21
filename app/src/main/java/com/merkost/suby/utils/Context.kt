package com.merkost.suby.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.net.toUri
import com.merkost.suby.BuildConfig
import java.util.Locale

fun Context.sendSupportEmail() {
    val versionName = BuildConfig.VERSION_NAME
    val versionCode = BuildConfig.VERSION_CODE
    val androidVersion = "${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})"
    val device = "${Build.MANUFACTURER} ${Build.MODEL}"
    val locale = Locale.getDefault().toString()


    val body = """
        Please provide your feedback below:
        


        —————————————————————————
        App Version : $versionName ($versionCode)
        Android     : $androidVersion
        Device      : $device
        Locale      : $locale
        —————————————————————————
    """.trimIndent()

    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
        data = "mailto:".toUri()
        putExtra(Intent.EXTRA_EMAIL, arrayOf("merkostdev+suby@gmail.com"))
        putExtra(Intent.EXTRA_SUBJECT, "Suby App Feedback")
        putExtra(Intent.EXTRA_TEXT, body)
    }

    startActivity(Intent.createChooser(emailIntent, "Send email with…"))
}