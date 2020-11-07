package com.example.focusstart7

import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var handler: Handler
    private lateinit var mainHandler: Handler
    private lateinit var timer: Runnable
    private var count: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val handlerThread = HandlerThread("Timer")
        handlerThread.start()

        handler = Handler(handlerThread.looper)

        mainHandler = Handler(Looper.getMainLooper())

        timer = Runnable {
            mainHandler.post {
                timeTextView.text = count.toString()
            }
            count++
            handler.postDelayed(timer, 1000)
        }

        startButton.setOnClickListener {
            Log.e("handler", handler.looper.thread.toString())
            handler.removeCallbacks(timer)
            count = 0
            handler.postDelayed(timer, 1000)
        }
    }

    override fun onDestroy() {
        Log.e("activity", "Destroyed")
        val countData = workDataOf("count" to count)

        val notificationWork = OneTimeWorkRequestBuilder<NotifyWork>()
            .setInitialDelay(5000, TimeUnit.MILLISECONDS)
            .setInputData(countData)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            "Timer",
            ExistingWorkPolicy.REPLACE, notificationWork
        )
        super.onDestroy()
    }
}