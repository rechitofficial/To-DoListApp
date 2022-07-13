package com.dicoding.todoapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dicoding.todoapp.R
import com.dicoding.todoapp.data.Task
import com.dicoding.todoapp.data.TaskRepository
import com.dicoding.todoapp.ui.detail.DetailTaskActivity
import com.dicoding.todoapp.utils.DateConverter
import com.dicoding.todoapp.utils.NOTIFICATION_CHANNEL_ID
import com.dicoding.todoapp.utils.TASK_ID

class NotificationWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    private val channelName = inputData.getString(NOTIFICATION_CHANNEL_ID)

    private fun getPendingIntent(task: Task): PendingIntent? {
        val intent = Intent(applicationContext, DetailTaskActivity::class.java).apply {
            putExtra(TASK_ID, task.id)
        }
        return TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    override fun doWork(): Result {
        //TODO 14 : If notification preference on, get nearest active task from repository and show notification with pending intent

        val sh = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        val completed = sh.getBoolean(
            applicationContext.getString(R.string.pref_key_notify),
            false
        )

        if(completed) {
            val taskRepository = TaskRepository.getInstance(applicationContext)
            val nearestActiveTask = taskRepository.getNearestActiveTask()
            val pendingIntent = getPendingIntent(nearestActiveTask)

            val notificationManagerCompat = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val builder = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentIntent(pendingIntent)
                .setContentTitle(nearestActiveTask.title)
                .setContentText(
                    String.format(
                        applicationContext.getString(R.string.notify_content),
                        DateConverter.convertMillisToString(nearestActiveTask.dueDateMillis)
                    )
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setPriority(NotificationCompat.DEFAULT_ALL)
                .setColor(
                    ContextCompat.getColor(
                        applicationContext,
                        android.R.color.transparent
                    )
                )
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                .setSound(alarmSound)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT)

                channel.enableVibration(true)
                channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)

                builder.setChannelId(NOTIFICATION_CHANNEL_ID)

                notificationManagerCompat.createNotificationChannel(channel)
            }

            val notification = builder.build()

            notificationManagerCompat.notify(100, notification)
        }


        return Result.success()
    }

}
