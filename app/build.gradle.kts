plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.praveen.bookmyplay"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.praveen.bookmyplay"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    packagingOptions {
        // Exclude duplicate license/notice files to fix build error
        exclude("META-INF/NOTICE.md")
        exclude("META-INF/LICENSE.md")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/NOTICE.txt")
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Android core and compatibility libraries
    implementation(libs.androidx.core.ktx) // Kotlin extensions
    implementation(libs.androidx.appcompat) // AppCompat (for backwards compatibility)

    // CardView — required for the glass UI container
    implementation("androidx.cardview:cardview:1.0.0")

    // Material Design components
    implementation(libs.material) // Make sure this is Material 3 if you want modern styling

    // Activity and layout support
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    implementation("com.sun.mail:android-mail:1.6.7")
    implementation("com.sun.mail:android-activation:1.6.7")

    implementation("androidx.compose.material:material-icons-extended:1.6.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Unit Testing
    testImplementation(libs.junit)

    // Android Instrumented Testing
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Optional: If you're using Material 3, add this explicitly:
    // implementation 'androidx.compose.material3:material3:<latest_version>'
}
