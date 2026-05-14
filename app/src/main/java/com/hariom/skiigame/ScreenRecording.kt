package com.hariom.skiigame

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.MediaStore
import androidx.annotation.RequiresApi

class ScreenRecording : Service() {

    private var projection : MediaProjection? = null

    private  var display : VirtualDisplay? = null

    private var recorder : MediaRecorder? = null

    private var videouri : Uri? = null



    @RequiresApi(Build.VERSION_CODES.S)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        setupNotification()

        val resultCode = intent?.getIntExtra("code", -1) ?: -1

        val data = intent?.getParcelableExtra<Intent>("data")

        if (data == null){
            stopSelf()
            return START_NOT_STICKY
        }

        val metrics = resources.displayMetrics

        val file = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, "recxxors ${System.currentTimeMillis()}")
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)
        }

        videouri = contentResolver.insert(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            file
        )

        val descriptor = contentResolver.openFileDescriptor(
            videouri!!, "w"
        )

        val manager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        projection = manager.getMediaProjection(
            resultCode,
            data
        )

        projection?.registerCallback(
            object : MediaProjection.Callback() {},
            Handler(Looper.getMainLooper())
        )

        recorder  = MediaRecorder(this).apply {
            setVideoSource(MediaRecorder.VideoSource.SURFACE)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            setVideoSize(metrics.widthPixels, metrics.heightPixels)
            setVideoFrameRate(60)
            setVideoEncodingBitRate(8*1024*1024)
            setOutputFile(descriptor?.fileDescriptor)
            prepare()
        }

        display = projection?.createVirtualDisplay(
            "rec",
            metrics.widthPixels,
            metrics.heightPixels,
            metrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            recorder!!.surface, null, null
        )

        recorder?.start()


        return START_NOT_STICKY
    }


    private fun setupNotification() {
        (getSystemService(NotificationManager::class.java)).createNotificationChannel(
            NotificationChannel(
                "record",
                "hopap",
                NotificationManager.IMPORTANCE_LOW
            )
        )

        val notification = Notification.Builder(
            this,
            "record"
        ).setContentTitle("Recrding")
            .setSmallIcon(R.drawable.go_skiing)
            .build()

        startForeground(1, notification)
    }

    override fun onDestroy() {
        try {
            recorder?.stop()
        }catch (e: Exception){
            println(e)
        }

        recorder?.release()
        projection?.stop()
        display?.release()

        projection = null
        display = null
        recorder = null

    }


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

}