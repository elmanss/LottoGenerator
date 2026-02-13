import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.cash.sqldelight)
  // alias(libs.plugins.kotlin.kapt)
  alias(libs.plugins.ksp)
  alias(libs.plugins.hilt.plugin)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.kotlin.serialization)
}

android {
  compileSdk = 36
  defaultConfig {
    applicationId = "me.elmanss.melate"
    minSdk = 28
    //noinspection EditedTargetSdkVersion
    targetSdk = 36
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

  buildFeatures {
    viewBinding = true
    compose = true
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
  }

  tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_21)
      freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
    }
  }

  namespace = "me.elmanss.melate"
}

dependencies {
  implementation(libs.appcompat)
  implementation(libs.material)
  implementation(libs.core.ktx)
  implementation(libs.fragment.ktx)
  implementation(libs.android.driver)
  implementation(libs.coroutines.extensions)
  implementation(libs.logcat)
  implementation(libs.lifecycle.runtime.ktx)
  implementation(libs.activity.compose)
  implementation(platform(libs.compose.bom))
  implementation(libs.ui)
  implementation(libs.ui.graphics)
  implementation(libs.ui.tooling.preview)
  implementation(libs.material3)
  implementation(libs.materialIconsExtended)
  testImplementation(libs.junit)
  androidTestImplementation(libs.ext.junit)
  androidTestImplementation(libs.espresso.core)

  // Android Jetpack
  // For Kotlin use navigation-fragment-ktx
  // For Kotlin use navigation-ui-ktx

  implementation(libs.hilt.android)
  androidTestImplementation(platform(libs.compose.bom))
  androidTestImplementation(libs.ui.test.junit4)
  debugImplementation(libs.ui.tooling)
  debugImplementation(libs.ui.test.manifest)
  ksp(libs.hilt.compiler)
  // https://mvnrepository.com/artifact/androidx.hilt/hilt-navigation-compose
  implementation(libs.hilt.navigation.compose)
  implementation(libs.constraintlayout.compose)
  implementation(libs.androidx.navigation3.ui)

  implementation(libs.retrofit)
  implementation(libs.retrofit.converter.gson)
  implementation(libs.logging.interceptor)
}

sqldelight {
  databases { create("Database") { packageName.set("me.elmanss.melate") } }
}

// kapt { correctErrorTypes = true }
