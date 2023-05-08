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
package com.me.momopay.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.me.momopay.R
import com.me.momopay.databinding.TransactionCardHistoryBinding
import com.me.momopay.presentation.home.components.TopBar
import com.me.momopay.ui.theme.StaxTheme
import com.me.momopay.utils.AnalyticsUtil
import com.me.momopay.utils.NavUtil
import com.me.momopay.utils.UIHelper
import org.koin.androidx.viewmodel.ext.android.viewModel

class TransactionHistoryFragment : Fragment(), TransactionHistoryAdapter.SelectListener {

    private var _binding: TransactionCardHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TransactionHistoryViewModel by viewModel()
    private var transactionsAdapter: TransactionHistoryAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        AnalyticsUtil.logAnalyticsEvent(getString(R.string.visit_screen, getString(R.string.visit_transaction_history)), requireActivity())
        _binding = TransactionCardHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar()

        initRecyclerView()
        observeTransactionActionPair()
    }

    private fun initToolbar() {
        binding.toolbar.setContent {
            StaxTheme { TopBar(title = R.string.nav_history) { dest -> navigateTo(dest) } }
        }
    }

    private fun navigateTo(dest: Int) = findNavController().navigate(dest)

    private fun initRecyclerView() {
        binding.transactionsRecycler.apply {
            layoutManager = UIHelper.setMainLinearManagers(context)
            transactionsAdapter = TransactionHistoryAdapter(this@TransactionHistoryFragment)
            adapter = transactionsAdapter
        }
    }

    private fun observeTransactionActionPair() {
        viewModel.transactionHistoryItem.observe(viewLifecycleOwner) {
            binding.noHistory.visibility = if (it.isNullOrEmpty()) View.VISIBLE else View.GONE
            transactionsAdapter!!.submitList(it)
        }
    }

    override fun viewTransactionDetail(uuid: String?) {
        uuid?.let { NavUtil.showTransactionDetailsFragment(findNavController(), it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}