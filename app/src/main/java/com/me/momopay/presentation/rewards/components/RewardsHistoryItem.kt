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
package com.me.momopay.presentation.rewards.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.me.momopay.R
import com.me.momopay.ui.theme.StaxTheme

@Composable
fun RewardsHistoryItem() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(id = R.dimen.margin_16),
                vertical = dimensionResource(id = R.dimen.margin_8)
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = dimensionResource(id = R.dimen.margin_8)),
                text = "Linked your first account",
                style = MaterialTheme.typography.body1
            )

            Text(
                modifier = Modifier.wrapContentWidth(),
                text = "100 points",
                style = MaterialTheme.typography.body1
            )
        }

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_5)))

        Text(
            modifier = Modifier
                .wrapContentWidth()
                .align(Alignment.End),
            text = "20 mins ago",
            style = MaterialTheme.typography.subtitle2
        )
    }
}

@Preview
@Composable
fun RewardsHistoryItemPreview() {
    StaxTheme {
        Surface(modifier = Modifier.wrapContentSize(), color = MaterialTheme.colors.background) {
            RewardsHistoryItem()
        }
    }
}