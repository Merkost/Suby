import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlinParcelize)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.secrets)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.firebaseCrashlytics)
    alias(libs.plugins.firebasePerformance)
    alias(libs.plugins.sentry)
}

sentry {
    findProperty("sentry.auth.token")?.let {
        authToken = it.toString()
    }
}

android {
    namespace = "com.merkost.suby"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.merkost.suby"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.appVersionCode.get().toInt()
        versionName = libs.versions.appVersionName.get().toString()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String", "QONVERSION_API_KEY", findProperty("QONVERSION_API_KEY").toString()
        )
        buildConfigField(
            "String", "AMPLITUDE_API_KEY", findProperty("AMPLITUDE_API_KEY").toString()
        )
        buildConfigField(
            "String", "SUPABASE_API_KEY", findProperty("SUPABASE_API_KEY").toString()
        )
        buildConfigField(
            "String", "SUPABASE_ID", findProperty("SUPABASE_ID").toString()
        )
        buildConfigField(
            "String", "SENTRY_DSN", findProperty("SENTRY_DSN").toString()
        )

    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
        debug {
            versionNameSuffix = ".debug"
            applicationIdSuffix = ".debug"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeCompiler {
        reportsDestination = layout.buildDirectory.dir("compose_compiler")
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }

    applicationVariants.configureEach {
        outputs.configureEach {
            (this as? BaseVariantOutputImpl)?.outputFileName =
                "Suby_${versionName}($versionCode).apk"
        }
    }
}

dependencies {
    implementation(libs.compose.multiplatform.android)

    implementation(libs.material)
    implementation(libs.analytics.android)
    implementation(libs.amplitude.session.replay)
    implementation(libs.qonversion.sdk)

    implementation(libs.koin.compose)
    implementation(libs.koin)

    implementation(libs.splashscreen)
    implementation(libs.kotlin.datetime)
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(libs.coil)
    implementation(libs.coil.svg)
    implementation(libs.coil.ktor)
    implementation(libs.lottie)
    implementation(libs.constraintlayout)

    implementation(platform(libs.compose.bom))
    implementation(libs.icons.extended)
    implementation(libs.navigation)
    implementation(libs.ui)
    implementation(libs.ui.util)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.datastore)
    implementation(libs.kotlin.serialization)
    implementation(libs.bundles.ktor)
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.database)
    implementation(libs.supabase.storage)
    implementation(libs.timber)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.config)
    implementation(libs.firebase.perf)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}

sentry {
    org.set("merkost")
    projectName.set("suby")

    includeSourceContext.set(true)
}