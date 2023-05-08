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
package com.me.momopay.domain.repository

import com.hover.sdk.actions.HoverAction
import com.me.momopay.database.channel.entity.Channel
import com.me.momopay.domain.model.ChannelBounties
import com.me.momopay.transactions.StaxTransaction
import kotlinx.coroutines.flow.Flow

interface BountyRepository {

    val bountyActions: List<HoverAction>

    fun getCountryList(): Flow<List<String>>

    suspend fun makeBounties(
        actions: List<HoverAction>,
        transactions: List<StaxTransaction>?,
        channels: List<Channel>
    ): List<ChannelBounties>
}