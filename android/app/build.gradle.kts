import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

// Signing credentials written by scripts/setup-android-signing.ps1 — never committed.
val localProperties = Properties().also { props ->
    rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use(props::load)
}

android {
    namespace = "com.pphi.thetoweranalyzer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.pphi.thetoweranalyzer"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "1.0.1"
    }

    signingConfigs {
        val storeFilePath = localProperties.getProperty("signing.storeFile")
        val storePass     = localProperties.getProperty("signing.storePassword")
        val alias         = localProperties.getProperty("signing.keyAlias")
        val keyPass       = localProperties.getProperty("signing.keyPassword")

        if (storeFilePath != null && storePass != null && alias != null && keyPass != null) {
            create("release") {
                storeFile     = file(storeFilePath)
                storePassword = storePass
                keyAlias      = alias
                keyPassword   = keyPass
            }
        }
    }

    buildTypes {
        debug {
            // Only the debug build (the one you install on your own phone) carries a
            // dev endpoint. Point this at your dev API Gateway stage.
            buildConfigField(
                "String",
                "DEV_ENDPOINT",
                "\"https://9g1lg3jas3.execute-api.us-west-2.amazonaws.com/prod/reports\"",
            )
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Empty → no dev option, and the dev URL is physically absent from the APK
            // that everyone else installs.
            buildConfigField("String", "DEV_ENDPOINT", "\"\"")
            // Null when local.properties has no signing block (e.g. debug-only machines).
            signingConfig = signingConfigs.findByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.kotlinx.coroutines.android)
}
