plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
}

android {
  namespace = "com.example.wearapp"
  compileSdk = 36

  defaultConfig {
    applicationId = "com.aistudio.soundmax.wvnk"
    minSdk = 30
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
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

dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.core.ktx)
  implementation(libs.play.services.wearable)
  implementation("androidx.wear:wear:1.3.0")
  implementation("androidx.wear.compose:compose-material:1.4.1")
  implementation("androidx.wear.compose:compose-foundation:1.4.1")
  implementation("androidx.wear.tiles:tiles:1.4.1")
  implementation("androidx.wear.tiles:tiles-material:1.4.1")
  implementation("com.google.android.horologist:horologist-compose-layout:0.6.23")
}
