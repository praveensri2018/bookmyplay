plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // ^-- Plugin declarations for Android and Kotlin
}

android {
    namespace = "com.praveen.bookmyplay"
    compileSdk = 35 // <-- SDK version used for compiling the app

    defaultConfig {
        applicationId = "com.praveen.bookmyplay" // <-- App ID
        minSdk = 24 // <-- Minimum Android version supported
        targetSdk = 35 // <-- Target Android version
        versionCode = 1 // <-- Version code for internal tracking
        versionName = "1.0" // <-- User-visible version name

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" // <-- Instrumentation runner for UI tests
    }

    buildTypes {
        release {
            isMinifyEnabled = false // <-- No code shrinking for release
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro" // <-- Custom ProGuard rules
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11 // <-- Java 11 source compatibility
        targetCompatibility = JavaVersion.VERSION_11 // <-- Java 11 target compatibility
    }

    kotlinOptions {
        jvmTarget = "11" // <-- Kotlin JVM bytecode target
    }

    buildFeatures {
        viewBinding = true // <-- Enable ViewBinding
    }

    packagingOptions {
        exclude("META-INF/NOTICE.md") // <-- Fix for duplicate NOTICE.md in packaging
        exclude("META-INF/LICENSE.md")
        // ^-- Place here to prevent build errors due to conflicting metadata
    }
}

dependencies {
    // Android core and compatibility libraries
    implementation(libs.androidx.core.ktx) // <-- Core Kotlin extensions for Android
    implementation(libs.androidx.appcompat) // <-- AppCompat for backward compatibility

    // CardView support for material glass-like container UI
    implementation("androidx.cardview:cardview:1.0.0") // <-- CardView support library

    // Material Design Components
    implementation(libs.material) // <-- Material components (use M3 if applicable)

    // Activity and ConstraintLayout support
    implementation(libs.androidx.activity) // <-- Support for modern Activity APIs
    implementation(libs.androidx.constraintlayout) // <-- ConstraintLayout for complex layouts

    // JavaMail API for email support
    implementation("com.sun.mail:android-mail:1.6.7") // <-- Email library
    implementation("com.sun.mail:android-activation:1.6.7") // <-- Activation support

    // Optional: Material Icons for Compose (not required unless using Compose)
    implementation("androidx.compose.material:material-icons-extended:1.6.0") // <-- Optional, remove if not using Compose
    implementation("com.google.android.material:material:1.10.0")
    // Retrofit for networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0") // <-- Retrofit core
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // <-- Retrofit Gson converter

    // Navigation Component support
    implementation(libs.androidx.navigation.fragment.ktx) // <-- Fragment navigation
    implementation(libs.androidx.navigation.ui.ktx) // <-- UI support for navigation

    // Unit Testing
    testImplementation(libs.junit) // <-- Local unit tests

    // Android Instrumented Tests
    androidTestImplementation(libs.androidx.junit) // <-- Android JUnit extensions
    androidTestImplementation(libs.androidx.espresso.core) // <-- Espresso UI testing

    // Optional: Uncomment below if you migrate to Material 3
    // implementation("androidx.compose.material3:material3:<latest_version>") // <-- Material 3 components
}
