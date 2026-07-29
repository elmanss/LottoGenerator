@file:Suppress("DEPRECATION")

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.cash.sqldelight)
  alias(libs.plugins.ksp)
  alias(libs.plugins.hilt.plugin)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.kotlin.serialization)
}

android {
  compileSdk = 37
  defaultConfig {
    applicationId = "me.elmanss.melate"
    minSdk = 28
    targetSdk = 37
    versionCode = 1
    versionName = "1.0"
    testInstrumentationRunner = "me.elmanss.melate.HiltTestRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  buildFeatures { compose = true }

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

  testOptions { unitTests.all { it.useJUnitPlatform() } }

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

  androidTestImplementation(libs.ext.junit)
  androidTestImplementation(libs.espresso.core)
  androidTestImplementation(platform(libs.compose.bom))
  androidTestImplementation(libs.ui.test.junit4)
  androidTestImplementation(libs.hilt.testing)
  kspAndroidTest(libs.hilt.compiler)

  implementation(libs.hilt.android)
  ksp(libs.hilt.compiler)

  debugImplementation(libs.ui.tooling)
  debugImplementation(libs.ui.test.manifest)

  implementation(libs.hilt.navigation.compose)
  implementation(libs.constraintlayout.compose)
  implementation(libs.androidx.navigation3.ui)

  implementation(libs.retrofit)
  implementation(libs.retrofit.converter.gson)
  implementation(libs.logging.interceptor)

  testImplementation(libs.coroutines.test)
  testImplementation(libs.junit.jupiter.api)
  testImplementation(libs.mockk)
  testImplementation(libs.strikt.core)
  testImplementation(libs.turbine)
  testRuntimeOnly(libs.junit.jupiter.engine)
}

sqldelight {
  databases { create("Database") { packageName.set("me.elmanss.melate") } }
}
