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
package com.me.momopay.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserUpdateDto(
    @SerialName("stax_user")
    val staxUser: UpdateDto
)

@Serializable
data class UpdateDto(
    @SerialName("is_mapper")
    val isMapper: Boolean? = null,
    @SerialName("marketing_opted_in")
    val marketingOptedIn: Boolean? = null,
    @SerialName("email")
    val email: String
)