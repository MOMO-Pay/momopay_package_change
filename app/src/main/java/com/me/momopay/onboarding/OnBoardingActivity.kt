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
package com.me.momopay.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.hover.sdk.permissions.PermissionHelper
import com.me.momopay.FRAGMENT_DIRECT
import com.me.momopay.OnboardingNavigationDirections
import com.me.momopay.R
import com.me.momopay.databinding.OnboardingLayoutBinding
import com.me.momopay.home.MainActivity
import com.me.momopay.home.NAV_HOME
import com.me.momopay.home.NAV_LINK_ACCOUNT
import com.me.momopay.login.AbstractGoogleAuthActivity
import com.me.momopay.permissions.PermissionUtils
import com.me.momopay.utils.AnalyticsUtil
import com.me.momopay.utils.NavUtil
import com.me.momopay.utils.UIHelper
import com.me.momopay.utils.Utils

class OnBoardingActivity : AbstractGoogleAuthActivity() {

    private lateinit var binding: OnboardingLayoutBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        UIHelper.setFullscreenView(this)
        super.onCreate(savedInstanceState)

        logEvents()
        binding = OnboardingLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        navigateNextScreen()
        setGoogleLoginInterface(this)
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_onboarding) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun logEvents() = AnalyticsUtil.logAnalyticsEvent(getString(R.string.visit_screen, getString(R.string.visit_onboarding)), this)

    private fun navigateNextScreen() {
        if (hasPassedOnboarding()) checkPermissionsAndNavigate()
        else NavUtil.navigate(navController, OnboardingNavigationDirections.toWelcomeFragment())
    }

    fun checkPermissionsAndNavigate() {
        val permissionHelper = PermissionHelper(this)
        if (permissionHelper.hasBasicPerms()) navigateToMainActivity()
        else showBasicPermission()
    }

    private fun showBasicPermission() {
        PermissionUtils.showInformativeBasicPermissionDialog(
            0,
            { PermissionUtils.requestPerms(NAV_HOME, this) }, {
            AnalyticsUtil.logAnalyticsEvent(getString(R.string.perms_basic_cancelled), this)
        }, this
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtils.logPermissionsGranted(grantResults, this)
        checkPermissionsAndNavigate()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(FRAGMENT_DIRECT, NAV_LINK_ACCOUNT)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)

        setPassedOnboarding()
        finish()
    }

    private fun setPassedOnboarding() = Utils.saveBoolean(OnBoardingActivity::class.java.simpleName, true, this)

    private fun hasPassedOnboarding(): Boolean = Utils.getBoolean(OnBoardingActivity::class.java.simpleName, this)
}