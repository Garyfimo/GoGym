import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}
dependencies {
    implementation(projects.shared)

    implementation(libs.androidx.activity.compose)

    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)
    implementation(libs.koin.android)
}

android {
    namespace = "com.garyfimo.gogym"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.garyfimo.gogym"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        buildConfig = true
    }
    flavorDimensions += "environment"
    productFlavors {
        create("mock") {
            dimension = "environment"
            applicationIdSuffix = ".mock"
            buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8080\"")
            buildConfigField("String", "ENV_NAME", "\"MOCK\"")
        }
        create("qa") {
            dimension = "environment"
            applicationIdSuffix = ".qa"
            buildConfigField("String", "BASE_URL", "\"https://qa.api.gogym.com\"")
            buildConfigField("String", "ENV_NAME", "\"QA\"")
        }
        create("prd") {
            dimension = "environment"
            buildConfigField("String", "BASE_URL", "\"https://api.gogym.com\"")
            buildConfigField("String", "ENV_NAME", "\"PRD\"")
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}