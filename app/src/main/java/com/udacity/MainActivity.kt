package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private var selectedDownloadOption: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        createChannel(channelId = CHANNEL_ID, channelName = getString(R.string.notification_channel))

        binding.content.rd1.tag = URL_GLIDE
        binding.content.rd2.tag = URL_LOAD_APP
        binding.content.rd3.tag = URL_RETROFIT

        binding.content.customButton.setOnClickListener {
            if (selectedDownloadOption != null) {
                binding.content.customButton.setLoading()
                download(url = selectedDownloadOption!!)
            } else {
                Toast.makeText(this, R.string.select_download, Toast.LENGTH_LONG).show()
            }
        }

        binding.content.rgDownloadOptions.setOnCheckedChangeListener { _, i ->
            val selectedRadioButton = binding.root.findViewById<RadioButton>(i)
            val selectedValue = selectedRadioButton.tag.toString()
            selectedDownloadOption = selectedValue
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (downloadID == id) {
                context?.let {
                    Toast.makeText(
                        context,
                        context.getString(R.string.download_complete),
                        Toast.LENGTH_LONG
                    ).show()
                    sendNotification(status = DownloadStatus.SUCCESS)
                }

//
//                val downloadManager = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//                val query = DownloadManager.Query().setFilterById(id)
//                val cursor = downloadManager.query(query)
//
//                if (cursor.moveToFirst()) {
//                    val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
//
//                    when (status) {
//                        DownloadManager.STATUS_SUCCESSFUL -> {
//                            Toast.makeText(
//                                context,
//                                context.getString(R.string.download_complete),
//                                Toast.LENGTH_LONG
//                            ).show()
//                            sendNotification(status = DownloadStatus.SUCCESS)
//                        }
//                        DownloadManager.STATUS_FAILED -> {
//                            sendNotification(status = DownloadStatus.FAIL)
//                        }
//                    }
//                }
//                cursor.close()
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.channel_description)

            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun sendNotification(status: DownloadStatus) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(DOWNLOAD_NAME, selectedDownloadOption)
            putExtra(DOWNLOAD_STATUS, status.value)
        }

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_assistant_black_24dp) // replace with your app icon
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_description))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setOngoing(false)
            .addAction(0, getString(R.string.notification_button), pendingIntent)
            .build()

        val notificationManager = ContextCompat.getSystemService(this, NotificationManager::class.java) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun download(url: String) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
        const val NOTIFICATION_ID = 1
        private const val URL_GLIDE = "https://github.com/bumptech/glide"
        private const val URL_LOAD_APP =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
        private const val URL_RETROFIT = "https://github.com/square/retrofit"
        const val DOWNLOAD_NAME = "downloadName"
        const val DOWNLOAD_STATUS = "downloadStatus"
    }
}