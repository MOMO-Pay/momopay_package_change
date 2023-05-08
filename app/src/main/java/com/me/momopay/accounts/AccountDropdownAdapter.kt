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

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.me.momopay.R
import com.me.momopay.databinding.StaxSpinnerItemWithLogoBinding
import com.me.momopay.domain.model.Account
import com.me.momopay.utils.UIHelper.loadImage

class AccountDropdownAdapter(val accounts: List<Account>, context: Context) : ArrayAdapter<Account>(context, 0, accounts) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val account = accounts[position]
        val holder: ViewHolder

        if (view == null) {
            val binding = StaxSpinnerItemWithLogoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            view = binding.root
            holder = ViewHolder(binding)
            view.tag = holder
        } else
            holder = view.tag as ViewHolder

        holder.setAccount(account)

        return view
    }

    override fun getCount(): Int = accounts.size

    override fun getItem(position: Int): Account? {
        return if (accounts.isEmpty()) null else accounts[position]
    }

    inner class ViewHolder(val binding: StaxSpinnerItemWithLogoBinding) {

        fun setAccount(account: Account) {
            binding.serviceItemNameId.text = account.userAlias

            if (account.logoUrl.isEmpty())
                binding.serviceItemImageId.loadImage(binding.root.context, R.drawable.ic_add)
            else
                binding.serviceItemImageId.loadImage(binding.root.context, account.logoUrl)
        }
    }
}