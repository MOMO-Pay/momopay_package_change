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

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.ArrayList

class Converters {

    @TypeConverter
    fun fromArray(value: ArrayList<String>?): String? = Json.encodeToString(value)
//    fun fromArray(strings: ArrayList<String>?): String? {
//        if (strings == null) return null
//        val string = StringBuilder()
//        for (s in strings) string.append(s).append(",")
//        return string.toString()
//    }

    @TypeConverter
    fun toArray(value: String?) = value?.let { Json.decodeFromString<ArrayList<String>>(it) }
//    fun toArray(concatenatedStrings: String?): ArrayList<String> {
//        return if (concatenatedStrings != null) ArrayList(
//            listOf(
//                *concatenatedStrings.split(",").toTypedArray()
//            )
//        ) else ArrayList()
//    }
}