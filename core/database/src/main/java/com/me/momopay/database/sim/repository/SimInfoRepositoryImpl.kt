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
package com.me.momopay.database.sim.repository

import com.hover.sdk.sims.SimInfo
import com.hover.sdk.sims.SimInfoDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class SimInfoRepositoryImpl(
    private val simInfoDao: SimInfoDao
) : SimInfoRepository {

    override fun getAll(): List<SimInfo> = simInfoDao.all

    override fun getPresentSims(): List<SimInfo> = simInfoDao.present

    override val flowAll: Flow<MutableList<SimInfo>>
        get() = flowOf(simInfoDao.all)
}