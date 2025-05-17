import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
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

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.valueOf(libs.versions.jvmVersion.get()))
        }
    }

//    listOf(
//        iosX64(),
//        iosArm64(),
//        iosSimulatorArm64()
//    ).forEach { iosTarget ->
//        iosTarget.binaries.framework {
//            baseName = "composeApp"
//            isStatic = true
//        }
//    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.animationGraphics)
            implementation(compose.material3)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.animation)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.navigation)

            implementation(libs.compose.calendar)
            implementation(libs.bundles.koin)

            implementation(libs.kotlin.datetime)
            implementation(libs.core.ktx)
            implementation(libs.coil)
            implementation(libs.coil.svg)
            implementation(libs.coil.network)
            implementation(libs.datastore)
            implementation(libs.kotlin.serialization)

            implementation(project.dependencies.platform(libs.supabase.bom))
            implementation(libs.supabase.database)
            implementation(libs.supabase.storage)

            implementation(libs.firebase.crashlytics)
            implementation(libs.firebase.analytics)
            implementation(libs.firebase.config)
            implementation(libs.firebase.perf)

            implementation(libs.compottie)

            implementation(libs.androidx.room.runtime)
            implementation(libs.sqlite.bundled)
            implementation(libs.bundles.ktor)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.lifecycle.runtime.ktx)
            implementation(libs.activity.compose)
            implementation(libs.splashscreen)
            implementation(libs.timber)
            implementation(libs.material)
            implementation(libs.analytics.android)
            implementation(libs.qonversion.sdk)

        }
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
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
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

//    sentry {
//        authToken = findProperty("SENTRY_AUTH_TOKEN").toString()
//    }

    applicationVariants.configureEach {
        outputs.configureEach {
            (this as? BaseVariantOutputImpl)?.outputFileName =
                "Suby_${versionName}($versionCode).apk"
        }
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)

//    kspCommonMainMetadata(libs.androidx.room.compiler)
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosX64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
}

//sentry {
//    org.set("merkost")
//    projectName.set("suby")
//
//    includeSourceContext.set(true)
//}