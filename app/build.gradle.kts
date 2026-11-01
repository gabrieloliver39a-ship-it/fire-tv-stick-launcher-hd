plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.firetvlauncher.hd"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.firetvlauncher.hd"
        // Fire OS 7 = Android 9 (API 28); Fire OS 8 = Android 11 (API 30)
        minSdk = 28
        targetSdk = 30
        versionCode = 1
        versionName = "1.0.0"
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

    lint {
        // targetSdk 30 é proposital (Fire OS 8); sem isso o lint reclama de SDK "antigo".
        abortOnError = false
        checkReleaseBuilds = false
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
}
