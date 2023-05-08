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
package com.me.momopay.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.me.momopay.contacts.ContactDao
import com.me.momopay.contacts.StaxContact
import com.me.momopay.data.local.accounts.AccountDao
import com.me.momopay.database.channel.dao.ChannelDao
import com.me.momopay.database.channel.entity.Channel
import com.me.momopay.database.user.dao.UserDao
import com.me.momopay.database.user.entity.StaxUser
import com.me.momopay.domain.model.Account
import com.me.momopay.merchants.Merchant
import com.me.momopay.merchants.MerchantDao
import com.me.momopay.paybill.Paybill
import com.me.momopay.paybill.PaybillDao
import com.me.momopay.requests.Request
import com.me.momopay.requests.RequestDao
import com.me.momopay.schedules.Schedule
import com.me.momopay.schedules.ScheduleDao
import com.me.momopay.transactions.StaxTransaction
import com.me.momopay.transactions.TransactionDao
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Database(
    entities = [
        Channel::class, StaxTransaction::class, StaxContact::class, Request::class, Schedule::class, Account::class, Paybill::class, Merchant::class, StaxUser::class
    ],
    version = 52,
    autoMigrations = [
        AutoMigration(from = 36, to = 37),
        AutoMigration(from = 37, to = 38),
        AutoMigration(from = 38, to = 39),
        AutoMigration(from = 40, to = 41),
        AutoMigration(from = 41, to = 42),
        AutoMigration(from = 43, to = 44),
        AutoMigration(from = 46, to = 47),
        AutoMigration(from = 49, to = 50)
    ]
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun channelDao(): ChannelDao

    abstract fun transactionDao(): TransactionDao

    abstract fun contactDao(): ContactDao

    abstract fun requestDao(): RequestDao

    abstract fun scheduleDao(): ScheduleDao

    abstract fun accountDao(): AccountDao

    abstract fun paybillDao(): PaybillDao

    abstract fun merchantDao(): MerchantDao

    abstract fun userDao(): UserDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        private const val NUMBER_OF_THREADS = 8
        val databaseWriteExecutor: ExecutorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS)

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "stax.db"
                )
                    .enableMultiInstanceInvalidation()
                    .setJournalMode(JournalMode.WRITE_AHEAD_LOGGING)
                    .addMigrations(
                        Migrations.M23_24,
                        Migrations.M24_25,
                        Migrations.M25_26,
                        Migrations.M26_27,
                        Migrations.M27_28,
                        Migrations.M28_29,
                        Migrations.M29_30,
                        Migrations.M30_31,
                        Migrations.M31_32,
                        Migrations.M32_33,
                        Migrations.M33_34,
                        Migrations.M34_35,
                        Migrations.M35_36,
                        Migrations.M39_40,
                        Migrations.M42_43,
                        Migrations.M44_45,
                        Migrations.M45_46,
                        Migrations.M47_48,
                        Migrations.M48_49,
                        Migrations.M50_51,
                        Migrations.M51_52
                    )
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}