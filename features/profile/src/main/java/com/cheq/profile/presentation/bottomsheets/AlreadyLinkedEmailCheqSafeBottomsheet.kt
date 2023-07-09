package com.cheq.profile.presentation.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cheq.cache.sharedprefs.SharedPrefs
import com.cheq.common.ui.bottomsheet.GenericBottomSheet
import com.cheq.common.config.remoteconfig.RemoteConfig
import com.cheq.common.extension.empty
import com.cheq.config.AppConfig
import com.cheq.navigation.IntentFactory
import com.cheq.profile.databinding.BottomsheetAlreadyLinkedEmailCheqSafeBinding
import com.cheq.profile.domain.entities.PersonalDetails
import com.cheq.profile.presentation.adapters.EmailCheqSafeAdapter
import com.cheq.profile.presentation.viewmodels.MyAccountViewModel

/**
 * Created by Akash Khatkale on 25th May, 2023.
 * akash.k@cheq.one
 */
class AlreadyLinkedEmailCheqSafeBottomsheet :
    GenericBottomSheet<BottomsheetAlreadyLinkedEmailCheqSafeBinding>() {

    private var viewModel: MyAccountViewModel? = null
    private var intentFactory: IntentFactory? = null
    private var remoteConfig: RemoteConfig? = null
    private var sharedPrefs: SharedPrefs? = null
    private var appConfig: AppConfig? = null
    private var userId: String = ""
    private var activeEmails: List<PersonalDetails.ActiveEmails> = emptyList()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BottomsheetAlreadyLinkedEmailCheqSafeBinding {
        return BottomsheetAlreadyLinkedEmailCheqSafeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MyAccountViewModel::class.java]

        setupRecyclerView()
        setupClickListeners()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString(USER_ID, String.empty)
        }
    }

    private fun setupClickListeners() {
        with(binding) {
            backIv.setOnClickListener {
                dismiss()
            }
            btnLinkEmail.setOnClickListener {
                dismiss()
                EnableCheqSafeBottomsheet.newInstance(
                    intentFactory,
                    sharedPrefs,
                    userId,
                    appConfig
                ).show(parentFragmentManager, EnableCheqSafeBottomsheet.TAG)
            }
        }
    }

    private fun setupRecyclerView() {
        with(binding.emailsRv) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = EmailCheqSafeAdapter(activeEmails)
        }
    }

    private fun setActiveEmails(activeEmails: List<PersonalDetails.ActiveEmails>) {
        this.activeEmails = activeEmails
    }

    private fun setIntentFactory(intentFactory: IntentFactory?) {
        this.intentFactory = intentFactory
    }

    private fun setSharedPrefs(sharedPrefs: SharedPrefs?) {
        this.sharedPrefs = sharedPrefs
    }

    private fun setRemoteConfig(config: RemoteConfig) {
        this.remoteConfig = config
    }

    private fun setRemoteConfig(config: AppConfig) {
        this.appConfig = config
    }

    companion object {
        const val TAG = "AlreadyLinkedEmailCheqSafeBottomsheet"
        private const val USER_ID = "USER_ID"

        fun newInstance(
            activeEmails: List<PersonalDetails.ActiveEmails>,
            intentFactory: IntentFactory?,
            sharedPrefs: SharedPrefs?,
            remoteConfig: RemoteConfig,
            userId: String,
            appConfig: AppConfig
        ): AlreadyLinkedEmailCheqSafeBottomsheet =
            AlreadyLinkedEmailCheqSafeBottomsheet().apply {
                arguments = Bundle().apply {
                    putString(USER_ID, userId)
                }
                setActiveEmails(activeEmails)
                setIntentFactory(intentFactory)
                setSharedPrefs(sharedPrefs)
                setRemoteConfig(remoteConfig)
            }
    }
}