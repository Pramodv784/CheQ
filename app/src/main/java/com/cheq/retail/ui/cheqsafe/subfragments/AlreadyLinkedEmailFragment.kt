package com.cheq.retail.ui.cheqsafe.subfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cheq.retail.R
import com.cheq.retail.databinding.BottomSheetManageCheqSafeBinding
import com.cheq.retail.ui.accountSettings.webView.adapter.EmailCheqSafeAdapter
import com.cheq.retail.ui.cheqsafe.CheqSafeViewModel
import com.cheq.retail.ui.cheqsafe.ViewVO
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AlreadyLinkedEmailFragment : BottomSheetDialogFragment() {

    private val viewModel by lazy {
        ViewModelProvider(parentFragment ?: this)[CheqSafeViewModel::class.java]
    }

    private lateinit var binding: BottomSheetManageCheqSafeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetManageCheqSafeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLinkedEmailList()
        setUpAction()
        observeView()
    }

    private fun setupLinkedEmailList() {
        binding.rvCheqSafeEmail.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCheqSafeEmail.adapter = EmailCheqSafeAdapter(requireContext(), emptyList())

    }

    private fun setUpAction() {
        binding.btnLinkEmail.setOnClickListener {
            viewModel.notifyIntro()
        }

        binding.ivBack.setOnClickListener {
            dialog?.dismiss()
        }
    }

    private fun observeView() {

        viewModel.view.observe(viewLifecycleOwner) {
            val linkedEmails = when (it) {
                is ViewVO.AlreadyLinkedEmail -> it.linkedEmails
                ViewVO.ExitConfirmation,
                ViewVO.Initial,
                ViewVO.Intro,
                ViewVO.LinkingFailed,
                ViewVO.LinkingInProcess,
                ViewVO.UserConfirmation,
                is ViewVO.LinkingInitiated -> emptyList()
            }
            binding.rvCheqSafeEmail.adapter = EmailCheqSafeAdapter(requireContext(), linkedEmails)
        }


    }
}