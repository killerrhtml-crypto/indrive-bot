plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.app.bot"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.app.bot"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/DEPENDENCIES"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.drawerlayout:drawerlayout:1.2.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    
    // Fragments
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    
    // Networking
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    
    // JSON
    implementation("org.json:json:20231013")
    
    // UI/Animations
    implementation("com.airbnb.android:lottie:6.0.0")
    implementation("io.github.sceneview:sceneview:1.2.1") {
        exclude(group = "androidx.compose.foundation")
        exclude(group = "androidx.compose.ui")
        exclude(group = "androidx.compose.animation")
        exclude(group = "androidx.compose.runtime")
    }
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Testing (opcional pero recomendado)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
