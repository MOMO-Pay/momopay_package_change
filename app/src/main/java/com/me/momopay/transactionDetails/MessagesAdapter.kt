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
package com.me.momopay.transactionDetails

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.me.momopay.R
import com.me.momopay.databinding.TransactionMessagesItemsBinding
import com.me.momopay.transactionDetails.MessagesAdapter.TransactionMessageViewHolder
import com.me.momopay.utils.Utils.dial

class MessagesAdapter internal constructor(
    private val messagesList: List<UssdCallResponse>?,
    private val timeStarted: String,
    private val timeCompleted: String
) : RecyclerView.Adapter<TransactionMessageViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionMessageViewHolder {
        val binding = TransactionMessagesItemsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionMessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionMessageViewHolder, position: Int) {
        val model = messagesList!![position]

        setTimeStamp(holder, position)
        setEnteredValueView(model, holder)
        setResponseView(model, holder)
    }

    private fun setEnteredValueView(model: UssdCallResponse, holder: TransactionMessageViewHolder) {
        if (model.enteredValue.isNotEmpty()) {
            holder.binding.messageEnteredValue.text = model.enteredValue
            if (model.isShortCode) styleAsLink(
                holder.binding.messageEnteredValue,
                model.enteredValue
            )
        } else holder.binding.messageEnteredValue.visibility = View.GONE
    }

    private fun setResponseView(model: UssdCallResponse, holder: TransactionMessageViewHolder) {
        if (model.responseMessage.isNotEmpty()) holder.binding.messageContent.text =
            model.responseMessage
        else holder.binding.messageContent.visibility = View.GONE
    }

    private fun setTimeStamp(holder: TransactionMessageViewHolder, position: Int) {
        setFirstTimeStamp(holder, position)
        setLastTimeStamp(holder, position)
    }

    private fun setFirstTimeStamp(holder: TransactionMessageViewHolder, position: Int) {
        if (position == 0) {
            holder.binding.timeStampIntro.apply {
                text = timeStarted
                visibility = View.VISIBLE
            }
        }
    }

    private fun setLastTimeStamp(holder: TransactionMessageViewHolder, position: Int) {
        with(messagesList) {
            this?.let {
                if (it.size > 1 && position == it.size - 1) {
                    holder.binding.timeStampEnded.apply {
                        text = timeCompleted
                        visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun styleAsLink(tv: TextView, shortcode: String) {
        tv.setTextColor(ContextCompat.getColor(tv.context, R.color.brightBlue))
        tv.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        tv.setOnClickListener { v: View -> dial(shortcode, v.context) }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return messagesList?.size ?: 0
    }

    class TransactionMessageViewHolder(var binding: TransactionMessagesItemsBinding) :
        RecyclerView.ViewHolder(binding.root)
}