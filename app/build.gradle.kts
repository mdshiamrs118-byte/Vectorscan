plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.smiam.vectorscan"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.smiam.vectorscan"
        minSdk = 29
        targetSdk = 29
        versionCode = 1
        versionName = "0.1"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
    // Xposed API is provided by the framework at runtime, never bundled.
    // If api.xposed.info is down, drop api-82.jar into app/libs and use:
    // compileOnly(files("libs/api-82.jar"))
    compileOnly("de.robv.android.xposed:api:82")
}
