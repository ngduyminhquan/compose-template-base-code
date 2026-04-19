import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.time.LocalDate
import java.time.format.DateTimeFormatter

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.firebaseCrashlytics)
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
            versionNameSuffix = "-dev"
            isMinifyEnabled = false
            isShrinkResources = false
        }
        release {
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

    // Ads
    implementation(libs.adsnextgen)

    // Firebase
    implementation(platform(libs.firebaseBom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.config)
}

