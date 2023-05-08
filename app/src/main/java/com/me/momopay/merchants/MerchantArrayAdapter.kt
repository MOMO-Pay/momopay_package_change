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
package com.me.momopay.merchants

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.me.momopay.databinding.StaxSpinner2lineBinding
import java.util.*
import kotlin.collections.ArrayList

class MerchantArrayAdapter(context: Context, val allMerchants: List<Merchant>) : ArrayAdapter<Merchant>(context, 0, allMerchants) {

    var filteredMerchants: MutableList<Merchant>? = ArrayList(allMerchants)

    override fun getView(position: Int, v: View?, parent: ViewGroup): View {
        var view = v
        val holder: ViewHolder
        if (view == null) {
            val binding: StaxSpinner2lineBinding =
                StaxSpinner2lineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            view = binding.root
            holder = ViewHolder(binding)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }
        val m = filteredMerchants!![position]
        holder.title.text = m.shortName()
        holder.subtitle.text = m.tillNo
        holder.subtitle.visibility = if (m.hasName()) View.VISIBLE else View.GONE
        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                val filtered: MutableList<Merchant> = ArrayList()
                if (constraint != null) {
                    for (merchant in allMerchants) {
                        if (merchant.toString().replace(" ".toRegex(), "").lowercase(Locale.getDefault())
                            .contains(constraint.toString().lowercase(Locale.getDefault()))
                        ) {
                            filtered.add(merchant)
                        }
                    }
                    filterResults.values = filtered
                    filterResults.count = filtered.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredMerchants!!.clear()
                if (results != null && results.count > 0) {
                    for (o in results.values as List<*>) {
                        if (o is Merchant) {
                            filteredMerchants!!.add(o)
                        }
                    }
                    notifyDataSetChanged()
                } else if (constraint.isNullOrEmpty()) {
                    // no filter, add entire original list back in
                    filteredMerchants!!.addAll(allMerchants)
                    notifyDataSetInvalidated()
                }
            }
        }
    }

    override fun getCount(): Int {
        return filteredMerchants!!.size
    }

    override fun getItem(position: Int): Merchant? {
        return if (filteredMerchants!!.isEmpty()) null else filteredMerchants!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class ViewHolder(binding: StaxSpinner2lineBinding) {
        var title: TextView
        var subtitle: TextView

        init {
            title = binding.title
            subtitle = binding.subtitle
        }
    }
}