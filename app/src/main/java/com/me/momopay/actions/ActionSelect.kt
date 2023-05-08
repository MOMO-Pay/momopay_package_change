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
package com.me.momopay.actions

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.hover.sdk.actions.HoverAction
import com.me.momopay.R
import com.me.momopay.databinding.ActionSelectBinding
import com.me.momopay.utils.UIHelper
import com.me.momopay.views.AbstractStatefulInput

class ActionSelect(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs), RadioGroup.OnCheckedChangeListener {

    private var allActions: List<HoverAction>? = null
    private var uniqueInstitutions: List<HoverAction>? = null
    private var highlightedAction: HoverAction? = null
    private var highlightListener: HighlightListener? = null

    private var _binding: ActionSelectBinding? = null
    private val binding get() = _binding!!

    init {
        _binding = ActionSelectBinding.inflate(LayoutInflater.from(context), this, true)
        createListeners()
        visibility = GONE
    }

    private fun createListeners() {
        binding.actionDropdown.autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            selectRecipientNetwork(parent.getItemAtPosition(position) as HoverAction)
        }
    }

    fun updateActions(filteredActions: List<HoverAction>) {
        visibility = if (filteredActions.isEmpty()) GONE else VISIBLE
        if (filteredActions.isEmpty()) return

        allActions = filteredActions
        if (!filteredActions.contains(highlightedAction))
            highlightedAction = null

        uniqueInstitutions = sort(filteredActions)
        val actionDropdownAdapter = ActionDropdownAdapter(uniqueInstitutions!!, context)

        binding.actionHeader.setText(if (uniqueInstitutions!!.firstOrNull()?.transaction_type == HoverAction.AIRTIME) R.string.airtime_who_header else R.string.send_who_header)
        binding.actionDropdown.visibility = if (showRecipientNetwork(uniqueInstitutions!!)) View.VISIBLE else View.GONE
        binding.actionDropdown.autoCompleteTextView.setAdapter(actionDropdownAdapter)
    }

    fun sort(actions: List<HoverAction>): List<HoverAction> = actions.distinctBy { it.to_institution_id }.toList()

    private fun showRecipientNetwork(actions: List<HoverAction>): Boolean {
        return actions.size > 1 || (actions.size == 1 && !actions.first().isOnNetwork)
    }

    fun selectRecipientNetwork(action: HoverAction) {
        if (action == highlightedAction) return

        setState(null, AbstractStatefulInput.SUCCESS)
        binding.actionDropdown.autoCompleteTextView.setText(action.toString(), false)

        val target = object : CustomTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                binding.actionDropdown.autoCompleteTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(resource, null, null, null)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                binding.actionDropdown.autoCompleteTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_grey_circle_small, 0, 0, 0)
            }
        }

        UIHelper.loadImage(context, context.getString(R.string.root_url).plus(action.to_institution_logo), target)

        val options = getWhoMeOptions(action.to_institution_id)
        if (options.size == 1) selectOnlyOption(options.first())
        else createOptions(options)
    }

    private fun getWhoMeOptions(recipientInstId: Int): List<HoverAction> {
        return allActions?.filter { it.to_institution_id == recipientInstId } ?: emptyList()
    }

    private fun selectOnlyOption(option: HoverAction) {
        binding.actionHeader.visibility = GONE
        binding.isSelfRadio.visibility = GONE
        if (!option.requiresRecipient())
            setState(context.getString(R.string.self_only_money_warning), AbstractStatefulInput.INFO)
        selectAction(option)
    }

    private fun createOptions(recipientInstitutionActions: List<HoverAction>) {
        val radioVisibility = if (recipientInstitutionActions.size > 1) VISIBLE else GONE
        binding.actionHeader.visibility = radioVisibility
        binding.isSelfRadio.removeAllViews()
        binding.isSelfRadio.clearCheck()
        binding.isSelfRadio.visibility = radioVisibility
        binding.isSelfRadio.setOnCheckedChangeListener(this@ActionSelect)

        recipientInstitutionActions.forEachIndexed { index, action -> addOption(index, action) }
    }

    private fun addOption(index: Int, action: HoverAction) {
        val radioButton = (LayoutInflater.from(context).inflate(R.layout.stax_radio_button, null) as RadioButton).apply {
            text = action.getPronoun(context)
            id = action.id
            isChecked = index == 0
        }
        binding.isSelfRadio.addView(radioButton)
    }

    private fun selectAction(action: HoverAction) {
        highlightedAction = action
        highlightListener?.highlightAction(action)
    }

    fun setState(message: String?, state: Int) = binding.actionDropdown.setState(message, state)

    fun setListener(listener: HighlightListener) {
        highlightListener = listener
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        if (checkedId == -1 || allActions.isNullOrEmpty() || allActions?.find { it.id == checkedId } == null) return
        val a = allActions!!.find { it.id == checkedId }!!
        selectAction(a)
    }

    interface HighlightListener {
        fun highlightAction(action: HoverAction?)
    }
}