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
package com.me.momopay.database.di

import com.me.momopay.database.channel.repository.ChannelRepository
import com.me.momopay.database.channel.repository.ChannelRepositoryImpl
import com.me.momopay.database.sim.repository.SimInfoRepository
import com.me.momopay.database.sim.repository.SimInfoRepositoryImpl
import com.me.momopay.database.user.repository.StaxUserRepository
import com.me.momopay.database.user.repository.StaxUserRepositoryImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val databaseModule = module {
    singleOf(::StaxUserRepositoryImpl) { bind<StaxUserRepository>() }
    singleOf(::ChannelRepositoryImpl) { bind<ChannelRepository>() }
    singleOf(::SimInfoRepositoryImpl) { bind<SimInfoRepository>() }
}