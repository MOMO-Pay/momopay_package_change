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
package com.me.momopay.accounts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.me.momopay.R
import com.me.momopay.databinding.StaxSpinnerItemWithLogoBinding
import com.me.momopay.domain.model.Account
import com.me.momopay.utils.GlideApp

class AccountsAdapter(var accounts: List<Account>) : RecyclerView.Adapter<AccountsAdapter.ViewHolder>() {

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = StaxSpinnerItemWithLogoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val account = accounts[holder.adapterPosition]
        holder.setAccount(account)
    }

    override fun getItemCount(): Int = accounts.size

    inner class ViewHolder(val binding: StaxSpinnerItemWithLogoBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setAccount(account: Account) {
            binding.serviceItemNameId.text = account.userAlias

            GlideApp.with(binding.root.context)
                .load(account.logoUrl)
                .placeholder(R.color.buttonColor)
                .circleCrop()
                .override(binding.root.context.resources.getDimensionPixelSize(R.dimen.logoDiam))
                .into(binding.serviceItemImageId)
        }
    }
}