import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.time.LocalDate
import java.time.format.DateTimeFormatter

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

android {
    namespace = "com.project.composeproject"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.project.composeproject"
        minSdk = 24
        targetSdk = 36
        versionCode = 100
        versionName = "1.0.0"

        val formattedDate = LocalDate
            .now()
            .format(DateTimeFormatter.ofPattern("MM.dd.yyyy"))
        val archivesName = "ComposeProjectBase_v${versionName}_${formattedDate}"
        base.archivesName.set(archivesName)
    }

    bundle {
        language {
            enableSplit = false
        }
    }

    buildTypes {
        debug {
            val appId = "ca-app-pub-3940256099942544~3347511713"
            manifestPlaceholders["app_id"] = appId
            buildConfigField("String", "APP_ID", "\"$appId\"")

            versionNameSuffix = "-dev"
            isMinifyEnabled = false
            isShrinkResources = false

            applicationIdSuffix = ".dev"
        }
        release {
            val appId = "ca-app-pub-7208941695689653~9843350590"
            manifestPlaceholders["app_id"] = appId
            buildConfigField("String", "APP_ID", "\"$appId\"")

            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
        firebaseCrashlytics {
            mappingFileUploadEnabled = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Ads
    implementation(libs.adsnextgen)

    // Firebase
    implementation(platform(libs.firebaseBom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.config)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Navigation
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.lifecycle.viewmodel)
    implementation(libs.kotlinx.serialization.core)

    // Coil
    implementation(libs.landscapist.coil)

    // Sdp, ssp
    implementation(libs.sdp.ssp.compose.multiplatform)

    // Media3
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
}

