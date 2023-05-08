/*
 * Copyright 2023 Stax
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
package com.me.momopay.database.user.repository

import com.me.momopay.database.user.dao.UserDao
import com.me.momopay.database.user.entity.StaxUser
import kotlinx.coroutines.flow.Flow

internal class StaxUserRepositoryImpl(
    private val userDao: UserDao
) : StaxUserRepository {

    override fun getUserAsync(): Flow<StaxUser> = userDao.getUserAsync()

    override suspend fun saveUser(user: StaxUser) = userDao.insertAsync(user)

    override suspend fun deleteUser(user: StaxUser) = userDao.delete(user)
}