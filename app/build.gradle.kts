import java.util.Properties

// Kode untuk membaca local.properties
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}
val mapboxAccessToken: String = localProperties.getProperty("MAPBOX_ACCESS_TOKEN") ?: ""

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.infinite_track"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.infinite_track"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Add Mapbox access token to manifest
        manifestPlaceholders["MAPBOX_ACCESS_TOKEN"] = mapboxAccessToken
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.lottie)
    implementation("com.airbnb.android:lottie-compose:5.0.3") // Lottie Compose


    implementation(libs.androidx.core.splashscreen)

    implementation(libs.kotlin.stdlib)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.datastore.preferences.core.jvm)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //DataStore
    implementation(libs.androidx.datastore.preferences)

    //Navigation
    implementation(libs.coil.compose.v230)
    implementation(libs.androidx.navigation.compose)

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    //Camera X
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.camera2)

    //coil
    implementation(libs.coil.kt.coil.compose)

    //Icon
    implementation(libs.androidx.material.icons.extended)

    //Activity
    implementation(libs.androidx.activity.compose)

    // Gambar Bitmap
    implementation(libs.ui)
    implementation(libs.androidx.exifinterface)

    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.library)


    implementation(libs.logging.interceptor)


    // Jetpack Compose core libraries
    implementation(libs.androidx.ui.v150)
    implementation(libs.material3)
    implementation(libs.ui.tooling.preview)

    // Google Maps Composec
    implementation(libs.maps.compose)
    implementation(libs.play.services.maps)
    implementation(libs.places)
    implementation(libs.play.services.location)

    // Accompanist Permissions (untuk handling permissions di Compose)
    implementation(libs.accompanist.permissions)

    // Mapbox
    implementation(libs.mapboxMapsSdk)
    implementation(libs.mapboxMapsComposeExtension) // Added Mapbox Compose Extension
}