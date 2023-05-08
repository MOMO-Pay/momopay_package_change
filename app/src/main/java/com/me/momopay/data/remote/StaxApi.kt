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
package com.me.momopay.data.remote

import com.me.momopay.BuildConfig
import com.me.momopay.data.remote.dto.StaxUserDto
import com.me.momopay.data.remote.dto.UserUpdateDto
import com.me.momopay.data.remote.dto.UserUploadDto
import com.me.momopay.data.remote.dto.authorization.AuthRequest
import com.me.momopay.data.remote.dto.authorization.AuthResponse
import com.me.momopay.data.remote.dto.authorization.RevokeTokenRequest
import com.me.momopay.data.remote.dto.authorization.TokenRequest
import com.me.momopay.data.remote.dto.authorization.TokenResponse
import com.me.momopay.ktor.EnvironmentProvider
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url

class StaxApi(
    private val client: HttpClient,
    environmentProvider: EnvironmentProvider
) {

    private val BASE_URL = environmentProvider.get().baseUrl

    suspend fun authorize(authRequest: AuthRequest): AuthResponse =
        client.post {
            url("${BASE_URL}authorize")
            setBody(authRequest)
        }.body()

    suspend fun fetchToken(tokenRequest: TokenRequest): TokenResponse =
        client.post {
            url("${BASE_URL}token")
            setBody(tokenRequest)
        }.body()

    suspend fun revokeToken(revokeToken: RevokeTokenRequest) =
        client.post {
            url("${BASE_URL}revoke")
            setBody(revokeToken)
        }

    suspend fun uploadUserToStax(userDTO: UserUploadDto): StaxUserDto =
        client.post {
            header("X-Stax-Version", BuildConfig.VERSION_NAME)
            url("${BASE_URL}stax_users")
            setBody(userDTO)
        }.body()

    suspend fun updateUser(email: String, userDTO: UserUpdateDto): StaxUserDto =
        client.post {
            url("${BASE_URL}stax_users/$email")
            setBody(userDTO)
        }.body()

    suspend fun getRewardPoints(email: String): StaxUserDto =
        client.post {
            url("${BASE_URL}api/rewards/reward_points/$email")
        }.body()
}