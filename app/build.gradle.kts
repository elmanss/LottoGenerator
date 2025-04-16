plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android) 
  alias(libs.plugins.cash.sqldelight) 
  alias(libs.plugins.safeargs.kotlin) 
  alias(libs.plugins.kotlin.kapt) 
  alias(libs.plugins.hilt.plugin) 
}

android {
  compileSdk = 35
  defaultConfig {
    applicationId = "me.elmanss.melate"
    minSdk = 28
    //noinspection EditedTargetSdkVersion
    targetSdk = 35
    versionCode = 1
    versionName = "1.0"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  buildFeatures { viewBinding = true }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
  }
  kotlinOptions { jvmTarget = "21" }

  namespace = "me.elmanss.melate"
}

dependencies {
  implementation(libs.appcompat)
  implementation(libs.material)
  implementation(libs.swiperefreshlayout)
  implementation(libs.core.ktx)
  implementation(libs.fragment.ktx)
  implementation(libs.constraintlayout)
  implementation(libs.android.driver)
  implementation(libs.coroutines.extensions)
  implementation(libs.logcat)
  testImplementation(libs.junit)
  androidTestImplementation(libs.ext.junit)
  androidTestImplementation(libs.espresso.core)

  // Android Jetpack
  implementation(libs.navigation.fragment.ktx)
  // For Kotlin use navigation-fragment-ktx
  implementation(libs.navigation.ui.ktx)
  // For Kotlin use navigation-ui-ktx

  implementation(libs.hilt.android)
  kapt(libs.hilt.compiler)
}

sqldelight { databases { create("Database") { packageName.set("me.elmanss.melate") } } }

kapt { correctErrorTypes = true }
