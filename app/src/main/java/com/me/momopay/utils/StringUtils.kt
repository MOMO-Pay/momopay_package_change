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
package com.me.momopay.utils

import android.text.TextUtils
import java.util.*

fun String.splitCamelCase(): String {
    return StringUtils.splitCamelCase(this)
}

fun String.toHni(): String {
    return this.replace("[", "").replace("]", "").replace("\"", "")
}

fun String.toFilteringStandard(): String {
    return this.lowercase().replace(" ", "")
}

fun String.isAbsolutelyEmpty(): Boolean {
    return TextUtils.isEmpty(this.replace(" ", ""))
}

private object StringUtils {
    fun splitCamelCase(s: String): String {
        val camelCased: String = s.replace(
            String.format(
                "%s|%s|%s",
                "(?<=[A-Z])(?=[A-Z][a-z])",
                "(?<=[^A-Z])(?=[A-Z])",
                "(?<=[A-Za-z])(?=[^A-Za-z])"
            ).toRegex(),
            " "
        )
        return capitalize(camelCased)
    }

    private fun capitalize(str: String): String {
        return if (str.isEmpty()) { str } else str.substring(0, 1).uppercase(Locale.ROOT) + str.substring(1).lowercase(Locale.ROOT)
    }
}