/*
 * Copyright 2022 Stax
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.me.momopay.schedules

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.me.momopay.R
import com.me.momopay.contacts.ContactDao
import com.me.momopay.database.AppDatabase
import com.me.momopay.home.MainActivity
import com.me.momopay.schedules.Schedule.REQUEST_TYPE
import java.util.concurrent.TimeUnit
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

const val SCHEDULE_REQUEST = 204

class ScheduleWorker(context: Context, params: WorkerParameters) : Worker(context, params), KoinComponent {

    private val appDb: AppDatabase by inject()

    private var scheduleDao: ScheduleDao = appDb.scheduleDao()
    private var contactDao: ContactDao = appDb.contactDao()

    override fun doWork(): Result {
        val scheduled = scheduleDao.future
        scheduled.filter { it.isScheduledForToday }.forEach { notifyUser(it) }
        return Result.success()
    }

    private fun notifyUser(s: Schedule) {
        val contacts = contactDao[s.recipient_ids.split(",").toTypedArray()]
        val builder = NotificationCompat.Builder(applicationContext, "DEFAULT")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(s.title(applicationContext))
            .setContentText(s.notificationMsg(applicationContext, contacts))
            .setStyle(NotificationCompat.BigTextStyle().bigText(s.notificationMsg(applicationContext, contacts)))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setOnlyAlertOnce(true)
            .setContentIntent(createIntent(s))
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(applicationContext)
        notificationManager.notify(s.id, builder.build())
    }

    private fun createIntent(s: Schedule): PendingIntent {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            action = s.type
            putExtra(Schedule.SCHEDULE_ID, s.id)
            putExtra(REQUEST_TYPE, s.type == REQUEST_TYPE)
        }

        return PendingIntent.getActivity(applicationContext, SCHEDULE_REQUEST, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    companion object {

        fun makeToil(): PeriodicWorkRequest = PeriodicWorkRequest.Builder(ScheduleWorker::class.java, 24, TimeUnit.HOURS).build()

        fun makeWork(): OneTimeWorkRequest = OneTimeWorkRequest.Builder(ScheduleWorker::class.java).build()
    }
}