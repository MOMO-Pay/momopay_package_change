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
package com.me.momopay.requests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.me.momopay.R
import com.me.momopay.contacts.StaxContact
import com.me.momopay.databinding.FragmentRequestDetailBinding
import com.me.momopay.utils.AnalyticsUtil.logAnalyticsEvent
import com.me.momopay.utils.DateUtils
import com.me.momopay.utils.UIHelper.flashAndReportMessage
import com.me.momopay.utils.Utils
import com.me.momopay.views.Stax2LineItem
import com.me.momopay.views.StaxDialog
import org.json.JSONException
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel

class RequestDetailFragment : Fragment(), RequestSenderInterface {

    private val viewModel: RequestDetailViewModel by viewModel()
    private val args: RequestDetailFragmentArgs by navArgs()
    private var _binding: FragmentRequestDetailBinding? = null
    private var dialog: StaxDialog? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val data = JSONObject()

        try {
            data.put("id", args.id)
        } catch (ignored: JSONException) {
        }

        logAnalyticsEvent(getString(R.string.visit_screen, getString(R.string.visit_request_detail)), data, requireContext())

        _binding = FragmentRequestDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.shareCard.requestLinkCardView.setTitle(getString(R.string.share_again_cardhead))

        viewModel.recipients.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                for (c in it) createRecipientEntry(c)
            }
        }

        viewModel.account.observe(viewLifecycleOwner) {
            binding.summaryCard.requesterAccountRow.visibility = if (it != null) View.VISIBLE else View.GONE
            it?.let { (view.findViewById(R.id.requesterValue) as Stax2LineItem).setTitle(it.userAlias) }
        }

        viewModel.request.observe(viewLifecycleOwner) {
            it?.let { setUpSummary(it) }
        }

        viewModel.setRequest(requireArguments().getInt("id"))
        initShareButtons()
    }

    private fun createRecipientEntry(c: StaxContact) {
        val ss2li = Stax2LineItem(requireActivity(), null)
        ss2li.setContact(c)
        binding.summaryCard.requesteeValueList.addView(ss2li)
    }

    private fun setUpSummary(request: Request) {
        binding.summaryCard.requestMoneyCard.setTitle(request.description)
        binding.summaryCard.dateValue.text = DateUtils.humanFriendlyDateTime(request.date_sent)

        if (!request.amount.isNullOrEmpty()) {
            binding.summaryCard.amountRow.visibility = View.VISIBLE
            binding.summaryCard.amountValue.text = Utils.formatAmount(request.amount!!)
        } else
            binding.summaryCard.amountRow.visibility = View.GONE

        if (!request.requester_number.isNullOrEmpty()) binding.summaryCard.requesterValue.setSubtitle(request.requester_number)

        binding.summaryCard.noteRow.visibility = if (request.note.isNullOrEmpty()) View.GONE else View.VISIBLE
        binding.summaryCard.noteValue.text = request.note
        binding.cancelBtn.setOnClickListener { showConfirmDialog() }
    }

    private fun showConfirmDialog() {
        if (activity != null) {
            dialog = StaxDialog(requireActivity())
                .setDialogTitle(R.string.cancelreq_head)
                .setDialogMessage(R.string.cancelreq_msg)
                .setNegButton(R.string.btn_back) {}
                .setPosButton(R.string.btn_cancelreq) {
                    viewModel.deleteRequest()
                    flashAndReportMessage(requireActivity(), getString(R.string.toast_confirm_cancelreq))
                    NavHostFragment.findNavController(this@RequestDetailFragment).popBackStack()
                }
                .isDestructive

            dialog!!.showIt()
        }
    }

    private fun initShareButtons() {
        if (activity != null) {
            binding.shareCard.smsShareSelection.setOnClickListener { sendSms(viewModel.request.value, viewModel.recipients.value, requireActivity()) }
            binding.shareCard.whatsappShareSelection.setOnClickListener { sendWhatsapp(viewModel.request.value, viewModel.recipients.value, viewModel.account.value, requireActivity()) }
            binding.shareCard.copylinkShareSelection.setOnClickListener { copyShareLink(viewModel.request.value, binding.shareCard.copylinkShareSelection, requireActivity()) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        if (dialog != null && dialog!!.isShowing) dialog!!.dismiss()

        _binding = null
    }
}