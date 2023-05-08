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
package com.me.momopay.presentation.bounties.components

import android.text.method.LinkMovementMethod
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.res.ResourcesCompat.getFont
import androidx.core.text.HtmlCompat
import androidx.core.widget.TextViewCompat
import com.me.momopay.R

@Composable
fun SpannableImageTextView(
    @DrawableRes drawable: Int,
    @StringRes stringRes: Int,
    modifier: Modifier = Modifier
) {
    val text = HtmlCompat.fromHtml(stringResource(id = stringRes), HtmlCompat.FROM_HTML_MODE_LEGACY).toString()

    Row(horizontalArrangement = Arrangement.Start, modifier = modifier) {
        Image(
            painter = painterResource(id = drawable),
            contentDescription = null,
            modifier = Modifier.align(Alignment.CenterVertically),
        )
        // only workaround available for handling html text in compose textviews
        AndroidView(
            factory = { context ->
                TextView(context).apply {
                    setText(text)
                    setTextColor(getColor(context, R.color.offWhite))
                    TextViewCompat.setTextAppearance(this, android.R.style.TextAppearance_Material_Caption)
                    layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    movementMethod = LinkMovementMethod.getInstance()
                    gravity = Gravity.CENTER_VERTICAL
                    typeface = getFont(context, R.font.brutalista_regular)
                }
            },
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = dimensionResource(id = R.dimen.margin_8))
        )
    }
}