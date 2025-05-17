package com.merkost.suby

import android.app.Application
import android.util.Log
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.util.DebugLogger
import com.amplitude.android.Amplitude
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.merkost.suby.di.appModule
import com.merkost.suby.di.databaseModule
import com.merkost.suby.di.databaseRepositoryModule
import com.merkost.suby.di.repositoryModule
import com.merkost.suby.di.useCaseModule
import com.merkost.suby.di.viewModelModule
import com.qonversion.android.sdk.Qonversion
import com.qonversion.android.sdk.QonversionConfig
import com.qonversion.android.sdk.dto.QEnvironment
import com.qonversion.android.sdk.dto.QLaunchMode
import io.sentry.Breadcrumb
import io.sentry.Hint
import io.sentry.SentryEvent
import io.sentry.SentryLevel
import io.sentry.SentryOptions
import io.sentry.android.core.SentryAndroid
import io.sentry.android.core.SentryAndroidOptions
import io.sentry.android.timber.SentryTimberIntegration
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber
import kotlin.time.Duration.Companion.hours

class SubyApplication : Application(), ImageLoaderFactory {

    private val amplitude: Amplitude by inject()

    override fun onCreate() {
        super.onCreate()
        setRemoteConfig()
        initQonversion()
        initSentry()
        initTimber()
        startKoin {
            androidLogger()
            androidContext(this@SubyApplication)
            modules(
                appModule, databaseModule, databaseRepositoryModule,
                repositoryModule, useCaseModule, viewModelModule,
            )
        }

    }

    private fun initSentry() {
        SentryAndroid.init(this) { options: SentryAndroidOptions ->
            options.isEnablePerformanceV2 = true
            options.isDebug = false
            options.environment = if (BuildConfig.DEBUG) "debug" else "production"
            options.beforeSend =
                SentryOptions.BeforeSendCallback { event: SentryEvent, hint: Hint? ->
                    if (event.level == null) return@BeforeSendCallback null
                    else return@BeforeSendCallback event
                }
            options.beforeBreadcrumb =
                SentryOptions.BeforeBreadcrumbCallback { breadcrumb: Breadcrumb, _: Hint? ->
                    // TODO: Set breadcrumb user data
                    breadcrumb
                }
            options.isEnableUserInteractionTracing = true
            options.isEnableUserInteractionBreadcrumbs = true
            options.addIntegration(
                SentryTimberIntegration(
                    minEventLevel = SentryLevel.WARNING,
                    minBreadcrumbLevel = SentryLevel.INFO
                )
            )
        }
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.plant(CrashReportingTree())
        } else {
            Timber.plant(CrashReportingTree())
        }
    }

    private fun initQonversion() {
        val qonversionConfig = QonversionConfig.Builder(
            this,
            BuildConfig.QONVERSION_API_KEY,
            QLaunchMode.SubscriptionManagement
        ).setEnvironment(
            if (BuildConfig.DEBUG) QEnvironment.Sandbox
            else QEnvironment.Production
        )
            .build()
        Qonversion.initialize(qonversionConfig)
    }

    private fun setRemoteConfig() {
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        remoteConfig.setDefaultsAsync(
            mapOf(
                "free_currency_rates_update_days" to 3L,
                "free_max_custom_services" to 3L,
                "free_max_subscriptions" to 3L,
            )
        )
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 6.hours.inWholeSeconds
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                remoteConfig.activate()
            }

            override fun onError(error: FirebaseRemoteConfigException) {}
        })
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
                    .maxSizeBytes(20 * 1024 * 1024)
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