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
package com.me.momopay.inapp_banner

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.me.momopay.databinding.InAppBannerLayoutBinding

class StaxBannerView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    private val binding: InAppBannerLayoutBinding = InAppBannerLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    fun display(banner: Banner) {
        binding.bannerTitle.setText(banner.title)
        banner.desc?.let { binding.bannerDesc.setText(it) }
        binding.cta.setText(banner.cta)
        binding.bannerIcon.setImageResource(banner.iconRes)
        binding.primaryBackground.setBackgroundResource(banner.primaryColor)
        binding.secondayBackground.setBackgroundResource(banner.secondaryColor)
    }
}