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

import androidx.lifecycle.LiveData
import com.me.momopay.database.AppDatabase

class ScheduleRepo(db: AppDatabase) {
    private val scheduleDao: ScheduleDao = db.scheduleDao()

    val futureTransactions: LiveData<List<Schedule>>
        get() = scheduleDao.liveFuture

    fun getFutureTransactions(channelId: Int): LiveData<List<Schedule>> {
        return scheduleDao.getLiveFutureByChannelId(channelId)
    }

    fun getSchedule(id: Int): Schedule? {
        return scheduleDao.get(id)
    }

    fun insert(schedule: Schedule?) {
        AppDatabase.databaseWriteExecutor.execute { schedule?.let { scheduleDao.insert(schedule) } }
    }

    fun update(schedule: Schedule?) {
        AppDatabase.databaseWriteExecutor.execute { scheduleDao.updateSchedule(schedule) }
    }

    fun delete(schedule: Schedule?) {
        AppDatabase.databaseWriteExecutor.execute { scheduleDao.deleteSchedule(schedule) }
    }
}