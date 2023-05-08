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
package com.me.momopay.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.hover.sdk.actions.HoverAction
import com.me.momopay.MainNavigationDirections
import com.me.momopay.R
import com.me.momopay.addChannels.ChannelsViewModel
import com.me.momopay.databinding.FragmentHomeBinding
import com.me.momopay.domain.model.Account
import com.me.momopay.home.MainActivity
import com.me.momopay.hover.AbstractBalanceCheckerFragment
import com.me.momopay.utils.AnalyticsUtil
import com.me.momopay.utils.NavUtil
import com.me.momopay.utils.UIHelper
import com.me.momopay.utils.Utils
import com.me.momopay.utils.collectLifecycleFlow
import com.me.momopay.views.StaxDialog
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : AbstractBalanceCheckerFragment(), FinancialTipClickInterface, BalanceTapListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val channelsViewModel: ChannelsViewModel by sharedViewModel()
    private val balancesViewModel: BalancesViewModel by sharedViewModel()
    private val homeViewModel: HomeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        AnalyticsUtil.logAnalyticsEvent(getString(R.string.visit_screen, getString(R.string.visit_home)), requireContext())
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setComposeView()

        observeForBalances()
        observeForBonus()
    }

    private fun getHomeClickFunctions(): HomeClickFunctions {
        fun onSendMoneyClicked() = navigateTo(getTransferDirection(HoverAction.P2P))
        fun onBuyAirtimeClicked() = navigateTo(getTransferDirection(HoverAction.AIRTIME))
        fun onBuyGoodsClicked() = navigateTo(HomeFragmentDirections.actionNavigationHomeToMerchantFragment())
        fun onPayBillClicked() = navigateTo(HomeFragmentDirections.actionNavigationHomeToPaybillFragment())
        fun onRequestMoneyClicked() = navigateTo(HomeFragmentDirections.actionNavigationHomeToNavigationRequest())
        fun onClickedAddNewAccount() = (requireActivity() as MainActivity).checkPermissionsAndNavigate(MainNavigationDirections.actionGlobalAddChannelsFragment())
        fun onClickedTermsAndConditions() = Utils.openUrl(getString(R.string.terms_and_condition_url), requireContext())
        fun onClickedSettingsIcon() = navigateTo(HomeFragmentDirections.toSettingsFragment())
        fun onClickedRewards() = navigateTo(HomeFragmentDirections.actionGlobalRewardsFragment())

        return HomeClickFunctions(
            onSendMoneyClicked = { onSendMoneyClicked() },
            onBuyAirtimeClicked = { onBuyAirtimeClicked() },
            onBuyGoodsClicked = { onBuyGoodsClicked() },
            onPayBillClicked = { onPayBillClicked() },
            onRequestMoneyClicked = { onRequestMoneyClicked() },
            onClickedAddNewAccount = { onClickedAddNewAccount() },
            onClickedTC = { onClickedTermsAndConditions() },
            onClickedSettingsIcon = { onClickedSettingsIcon() },
            onClickedRewards = { onClickedRewards() }
        )
    }

    private fun setComposeView() {
        binding.root.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        binding.root.setContent {
            HomeScreen(
                channelsViewModel,
                homeClickFunctions = getHomeClickFunctions(),
                tipInterface = this@HomeFragment,
                balanceTapListener = this@HomeFragment,
                homeViewModel = homeViewModel,
                navTo = { dest -> navigateTo(dest) }
            )
        }
    }

    private fun observeForBonus() {
        collectLifecycleFlow(channelsViewModel.accountEventFlow) {
            if (homeViewModel.homeState.value?.bonuses?.isNotEmpty() == true)
                navigateTo(getTransferDirection(HoverAction.AIRTIME, homeViewModel.homeState.value?.bonuses?.first()?.from_institution_id.toString()))
        }
    }

    private fun observeForBalances() {
        collectLifecycleFlow(balancesViewModel.balanceAction) {
            attemptCallHover(balancesViewModel.userRequestedBalanceAccount.value, it)
        }

        collectLifecycleFlow(channelsViewModel.accountCallback) {
            askToCheckBalance(it)
        }

        collectLifecycleFlow(balancesViewModel.actionRunError) {
            UIHelper.flashAndReportError(requireActivity(), it)
        }
    }

    private fun getTransferDirection(type: String, institutionId: String? = null): NavDirections {
        return HomeFragmentDirections.actionNavigationHomeToNavigationTransfer(type).also {
            if (institutionId != null) it.institutionId = institutionId
        }
    }

    private fun attemptCallHover(account: Account?, action: HoverAction?) {
        action?.let { account?.let { callHover(checkBalance, generateSessionBuilder(account, action)) } }
    }

    private fun askToCheckBalance(account: Account) {
        val dialog = StaxDialog(requireActivity()).setDialogTitle(R.string.check_balance_title)
            .setDialogMessage(R.string.check_balance_desc).setNegButton(R.string.later, null)
            .setPosButton(R.string.check_balance_title) { onTapBalanceRefresh(account) }
        dialog.showIt()
    }

    private fun navigateTo(navDirections: NavDirections) = (requireActivity() as MainActivity).checkPermissionsAndNavigate(navDirections)

    private fun navigateTo(dest: Int) = findNavController().navigate(dest)

    override fun onTipClicked(tipId: String?) {
        val destination = HomeFragmentDirections.actionNavigationHomeToWellnessFragment().apply { setTipId(tipId) }
        NavUtil.navigate(findNavController(), destination)
    }

    override fun onTapBalanceRefresh(account: Account?) {
        balancesViewModel.requestBalance(account)
    }

    override fun onTapBalanceDetail(accountId: Int) {
        findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToAccountDetailsFragment(accountId))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}