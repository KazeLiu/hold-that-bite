plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.holdthatbite"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.holdthatbite"
        minSdk = 26
        targetSdk = 35
        versionCode = 10
        versionName = "0.5.5"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.02.02"))
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.runtime:runtime-saveable")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.umeng.umsdk:common:9.9.1")
    implementation("com.umeng.umsdk:asms:1.8.7.2")
    debugImplementation("androidx.compose.ui:ui-tooling")
    testImplementation("org.json:json:20240303")
    testImplementation("junit:junit:4.13.2")
}
