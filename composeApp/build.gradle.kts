import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

val releaseProperties = Properties().apply {
    val file = rootProject.file("release.properties")
    if (file.isFile) file.inputStream().use { load(it) }
}

fun releaseProperty(name: String): String? {
    return releaseProperties.getProperty(name)?.trim()?.takeIf(String::isNotEmpty)
        ?: System.getenv("ASH_$name")?.trim()?.takeIf(String::isNotEmpty)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            binaryOption("bundleId", "com.lyheang.ash.shared")
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation("androidx.core:core-splashscreen:1.2.0")
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.animation)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.kotlinx.datetime)
            implementation(compose.materialIconsExtended)
            implementation("com.russhwolf:multiplatform-settings-no-arg:1.3.0")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.lyheang.ash"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.lyheang.ash"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = releaseProperty("VERSION_CODE")?.toIntOrNull() ?: 1
        versionName = releaseProperty("VERSION_NAME") ?: "1.0.0"
    }
    signingConfigs {
        create("release") {
            val keystorePath = releaseProperty("ANDROID_KEYSTORE_PATH")
            if (keystorePath != null) {
                storeFile = rootProject.file(keystorePath)
                storePassword = releaseProperty("ANDROID_STORE_PASSWORD")
                keyAlias = releaseProperty("ANDROID_KEY_ALIAS")
                keyPassword = releaseProperty("ANDROID_KEY_PASSWORD")
            }
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            if (releaseProperty("ANDROID_KEYSTORE_PATH") != null) {
                signingConfig = signingConfigs.getByName("release")
            }
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
}

val requiredReleaseProperties = listOf(
    "ANDROID_KEYSTORE_PATH",
    "ANDROID_STORE_PASSWORD",
    "ANDROID_KEY_ALIAS",
    "ANDROID_KEY_PASSWORD"
)

val verifyStoreRelease by tasks.registering {
    group = "distribution"
    description = "Checks the Android upload signing configuration."
    doLast {
        val missing = requiredReleaseProperties.filter { releaseProperty(it).isNullOrBlank() }
        check(missing.isEmpty()) {
            "Missing release settings: ${missing.joinToString()}. " +
                "Create release.properties from release.properties.example."
        }
        val keystore = rootProject.file(requireNotNull(releaseProperty("ANDROID_KEYSTORE_PATH")))
        check(keystore.isFile) { "Android keystore was not found at ${keystore.absolutePath}." }
    }
}

tasks.register("bundleStoreRelease") {
    group = "distribution"
    description = "Builds the signed, optimized Android App Bundle for Google Play."
    dependsOn(verifyStoreRelease, "bundleRelease")
}

tasks.matching { it.name == "bundleRelease" }.configureEach {
    mustRunAfter(verifyStoreRelease)
}

dependencies {
    debugImplementation(compose.uiTooling)
}
