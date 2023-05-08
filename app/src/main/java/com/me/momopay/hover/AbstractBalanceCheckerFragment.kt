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
package com.me.momopay.hover

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hover.sdk.actions.HoverAction
import com.me.momopay.R
import com.me.momopay.domain.model.Account
import com.me.momopay.utils.AnalyticsUtil
import com.me.momopay.utils.NavUtil
import com.me.momopay.utils.UIHelper

abstract class AbstractBalanceCheckerFragment : Fragment() {

    protected val checkBalance = registerForActivityResult(TransactionContract()) { data: Intent? ->
        if (data != null && data.extras != null && data.extras!!.getString("uuid") != null) {
            NavUtil.showTransactionDetailsFragment(findNavController(), data.extras!!.getString("uuid")!!)
        }
    }

    protected fun generateSessionBuilder(account: Account, action: HoverAction): HoverSession.Builder {
        return HoverSession.Builder(action, account, requireActivity())
    }

    protected fun callHover(launcher: ActivityResultLauncher<HoverSession.Builder>, b: HoverSession.Builder) {
        try {
            launcher.launch(b)
        } catch (e: Exception) {
            requireActivity().runOnUiThread {
                UIHelper.flashAndReportMessage(
                    requireContext(),
                    getString(
                        R.string.error_running_action
                    )
                )
            }
            AnalyticsUtil.logErrorAndReportToFirebase(b.action.public_id, getString(R.string.error_running_action_log), e)
        }
    }
}