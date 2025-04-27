plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.expimp.chess_game"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.expimp.chess_game"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        resValue("string", "versionName", versionName.toString())
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.android.colorpickerpreference)
    implementation(libs.androidx.preference)
    implementation(libs.jummania.chess.game)
}