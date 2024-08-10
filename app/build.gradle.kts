import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlinParcelize)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.secrets)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.firebaseCrashlytics)
    alias(libs.plugins.firebasePerformance)
}

android {
    namespace = "com.merkost.suby"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.merkost.suby"
        minSdk = 26
        targetSdk = 35
        versionCode = 4
        versionName = "0.1.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")

            buildConfigField("String", "SUPABASE_API_KEY", findProperty("SUPABASE_API_KEY").toString())
            buildConfigField("String", "SUPABASE_ID", findProperty("SUPABASE_ID").toString())
        }
        debug {
            versionNameSuffix = ".debug"
            applicationIdSuffix = ".debug"
            buildConfigField("String", "SUPABASE_API_KEY", findProperty("SUPABASE_API_KEY").toString())
            buildConfigField("String", "SUPABASE_ID", findProperty("SUPABASE_ID").toString())
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
        enableStrongSkippingMode = true
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
            (this as? BaseVariantOutputImpl)?.outputFileName = "Suby_${versionName}($versionCode).apk"
        }
    }
}

dependencies {
    implementation(libs.splashscreen)
    implementation(libs.kotlin.datetime)
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(libs.coil)
    implementation(libs.coil.svg)
    implementation(libs.lottie)

    implementation(platform(libs.compose.bom))
    implementation(libs.icons.extended)
    implementation(libs.navigation)
    implementation(libs.ui)
    implementation(libs.ui.util)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.systemUiController)
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
    implementation(libs.firebase.perf)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation)
    ksp(libs.hilt.compiler)
    ksp(libs.hilt.kapt)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}