package com.qiuyou.tennis

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TennisApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        android.util.Log.e("TennisApp", "Application: onCreate started")
        
        /*
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val file = java.io.File(filesDir, "crash_log.txt")
                java.io.FileWriter(file, true).use { writer ->
                    writer.append("Crash on thread ${thread.name}: ${throwable.message}\n")
                    throwable.printStackTrace(java.io.PrintWriter(writer))
                    writer.append("\n----------------\n")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(10)
        }
        */
    }
}
