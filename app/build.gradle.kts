plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("app.cash.sqldelight")
  id("androidx.navigation.safeargs.kotlin")
  id("kotlin-kapt")
  id("dagger.hilt.android.plugin")
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
  implementation(fileTree(mapOf("dir" to "libs", "include" to arrayOf("*.jar"))))
  implementation("androidx.appcompat:appcompat:1.7.0")
  implementation("com.google.android.material:material:1.12.0")
  implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
  implementation("androidx.core:core-ktx:1.15.0")
  implementation("androidx.fragment:fragment-ktx:1.8.6")
  implementation("androidx.constraintlayout:constraintlayout:2.2.1")
  implementation("app.cash.sqldelight:android-driver:2.0.2")
  implementation("app.cash.sqldelight:coroutines-extensions:2.0.2")
  implementation("com.squareup.logcat:logcat:0.1")
  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.2.1")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

  // Android Jetpack
  implementation("androidx.navigation:navigation-fragment-ktx:2.8.8")
  // For Kotlin use navigation-fragment-ktx
  implementation("androidx.navigation:navigation-ui-ktx:2.8.8")
  // For Kotlin use navigation-ui-ktx

  implementation("com.google.dagger:hilt-android:2.55")
  kapt("com.google.dagger:hilt-compiler:2.55")
}

repositories {
  google()
  mavenCentral()
}

sqldelight { databases { create("Database") { packageName.set("me.elmanss.melate") } } }

kapt { correctErrorTypes = true }
