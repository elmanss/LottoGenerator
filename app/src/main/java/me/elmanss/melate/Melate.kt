package me.elmanss.melate

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import logcat.AndroidLogcatLogger
import logcat.LogPriority

@HiltAndroidApp
class Melate : Application() {

  override fun onCreate() {
    super.onCreate()
    AndroidLogcatLogger.installOnDebuggableApp(this, minPriority = LogPriority.VERBOSE)
  }
}
