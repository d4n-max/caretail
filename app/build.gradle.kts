import org.gradle.api.GradleException
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("org.jetbrains.kotlin.plugin.compose")
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties().apply {
    if (keystorePropertiesFile.exists()) {
        keystorePropertiesFile.inputStream().use(::load)
    }
}
val releaseSigningKeys = listOf("storeFile", "storePassword", "keyAlias", "keyPassword")
val hasReleaseSigningConfig = releaseSigningKeys.all { key ->
    !keystoreProperties.getProperty(key).isNullOrBlank()
}
val releaseSigningMessage = """
    Release signing is not configured.

    Create keystore.properties in the project root with:
    storeFile=caretail-upload-key.jks
    storePassword=YOUR_STORE_PASSWORD
    keyAlias=caretail
    keyPassword=YOUR_KEY_PASSWORD

    See docs/RELEASE_SIGNING.md for the full Google Play Internal Testing AAB workflow.
""".trimIndent()

android {
    namespace = "com.caretail.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.caretail.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 3
        versionName = "0.1.2"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    signingConfigs {
        create("release") {
            if (hasReleaseSigningConfig) {
                storeFile = rootProject.file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            if (hasReleaseSigningConfig) {
                signingConfig = signingConfigs.getByName("release")
            }
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
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.compose.ui:ui:1.6.6")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.6")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.material:material-icons-extended:1.6.6")
    implementation("androidx.navigation:navigation-compose:2.8.5")
    implementation("androidx.room:room-runtime:2.8.4")
    implementation("androidx.room:room-ktx:2.8.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.3")
    implementation("com.android.billingclient:billing:8.0.0")
    kapt("androidx.room:room-compiler:2.8.4")

    debugImplementation("androidx.compose.ui:ui-tooling:1.6.6")
}

gradle.taskGraph.whenReady {
    val requestedReleaseBuild = allTasks.any { task ->
        task.name in listOf("bundleRelease", "assembleRelease", "packageReleaseBundle")
    }
    if (requestedReleaseBuild && !hasReleaseSigningConfig) {
        throw GradleException(releaseSigningMessage)
    }
}
