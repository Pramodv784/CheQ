package com.cheq.profile.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cheq.cache.sharedprefs.SharedPrefs
import com.cheq.cache.sharedprefs.SharedPrefsCheQ
import com.cheq.cache.sharedprefs.SharedPrefsCheQUser
import com.cheq.cache.sharedprefs.SharedPrefsConstants
import com.cheq.common.config.remoteconfig.RemoteConfig
import com.cheq.common.extension.empty
import com.cheq.common.extension.orZero
import com.cheq.common.freshchat.FreshChat
import com.cheq.common.ui.base.BaseFragment
import com.cheq.common.utils.StatusBarUtils
import com.cheq.config.AppConfig
import com.cheq.navigation.IntentFactory
import com.cheq.profile.R
import com.cheq.profile.databinding.FragmentMyAccountBinding
import com.cheq.profile.domain.entities.PersonalDetails
import com.cheq.profile.presentation.adapters.MyAccountSettingsAdapter
import com.cheq.profile.presentation.bottomsheets.AlreadyLinkedEmailCheqSafeBottomsheet
import com.cheq.profile.presentation.bottomsheets.ConfirmationCheqSafeBottomsheet
import com.cheq.profile.presentation.bottomsheets.EnableCheqSafeBottomsheet
import com.cheq.profile.presentation.bottomsheets.LinkingFailedCheqSafeBottomsheet
import com.cheq.profile.presentation.bottomsheets.LinkingInitiatedCheqSafeBottomSheet
import com.cheq.profile.presentation.bottomsheets.LinkingProcessCheqSafeBottomSheet
import com.cheq.profile.presentation.bottomsheets.LogoutBottomsheet
import com.cheq.profile.presentation.listeners.OnAccountSettingsClicked
import com.cheq.profile.presentation.models.SettingsModel
import com.cheq.profile.presentation.models.SettingsModel.SettingsIdentifier
import com.cheq.profile.presentation.viewmodels.MyAccountViewModel
import com.cheq.profile.presentation.viewstate.CheqSafeState
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.cheq.common.R as commonR
import com.cheq.proxima.R as proxima

/**
 * Created by Akash Khatkale on 15th May, 2023.
 * akash.k@cheq.one
 */
@AndroidEntryPoint
class MyAccountFragment : BaseFragment<FragmentMyAccountBinding>(), OnAccountSettingsClicked {

    @Inject
    lateinit var appConfig: AppConfig

    @Inject
    lateinit var intentFactory: IntentFactory

    @Inject
    @SharedPrefsCheQ
    lateinit var sharedPrefs: SharedPrefs

    @Inject
    @SharedPrefsCheQUser
    lateinit var sharedPrefsCheQ: SharedPrefs

    @Inject
    lateinit var remoteConfig: RemoteConfig

    @Inject
    lateinit var freshChat: FreshChat

    private val myAccountViewModel: MyAccountViewModel by activityViewModels()
    private var accountSettingsData = listOf(
        SettingsModel(
            SettingsIdentifier.PERSONAL_DETAILS,
            commonR.string.personal_details, commonR.drawable.ic_personal_details,
        ),
        SettingsModel(
            SettingsIdentifier.TRANSACTION_HISTORY,
            commonR.string.transaction_history, commonR.drawable.ic_transaction_history,
        ),
        SettingsModel(
            SettingsIdentifier.MANAGE_CHEQ_SAFE,
            commonR.string.manage_cheq_safe,
            commonR.drawable.ic_cheq_safe,
        ),
        SettingsModel(
            SettingsIdentifier.REFER_EARN,
            commonR.string.refer_earn,
            commonR.drawable.ic_refer_and_earn,
        )
    )
    private var userId: String = String.empty
    private var bottomsheetTag = "cheqSafeBottomsheet"
    private lateinit var adapter: MyAccountSettingsAdapter

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMyAccountBinding {
        return FragmentMyAccountBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupData()
        setupUi()
        setupClickListeners()
        fetchUserSettings()
        setupObservers()
    }

    private fun setupData() {
        userId = sharedPrefsCheQ.getString(SharedPrefsConstants.CHEQ_USER_ID)
    }

    private fun setupClickListeners() {
        with(binding) {
            ivBack.setOnClickListener {
                requireActivity().finish()
            }
            termsPoliciesTv.setOnClickListener {
                // todo: mparticle log event
                navigate(R.id.action_myAccountFragment_to_termsPoliciesFragment)
            }
            logoutFl.setOnClickListener {
                onLogoutClicked()
            }
            needHelpTv.setOnClickListener {
                freshChat.showConversation()
            }
        }
    }

    private fun onLogoutClicked() = LogoutBottomsheet.newInstance(
        sharedPrefs, sharedPrefsCheQ, intentFactory
    ).show(parentFragmentManager, LogoutBottomsheet.TAG)


    override fun onResume() {
        super.onResume()
        StatusBarUtils.setStatusBarColor(requireActivity(), ContextCompat.getColor(requireContext(), proxima.color.primary_p0))
    }

    private fun setupUi() {
        // todo: log mparticle screen event
        setupRecyclerView()
        with(binding) {
            tvAppVersion.text = "Build V${appConfig.getVersionName()}"
        }
    }

    private fun setupRecyclerView() {
        val showCheqSafe = sharedPrefs.getBoolean(SharedPrefsConstants.IS_CHEQ_SAFE_VISIBLE)
        adapter = MyAccountSettingsAdapter(
            accountSettingsData.filterNot { it.id == SettingsIdentifier.MANAGE_CHEQ_SAFE && showCheqSafe.not() },
            this@MyAccountFragment
        )
        binding.shimmerContainer.startShimmer()
        with(binding.myAccountRv) {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = this@MyAccountFragment.adapter
        }
    }

    private fun setupObservers() {
        myAccountViewModel.userInitialsStateLiveData.observe(viewLifecycleOwner) {
            if (accountSettingsData.size > 2) {
                accountSettingsData[2].emailCount = it?.activeEmails?.size.orZero
            }
            adapter.notifyDataSetChanged()
            setupNameUi(it)
        }
        myAccountViewModel.cheqSafeState.observe(viewLifecycleOwner) {
            if (childFragmentManager.findFragmentByTag(bottomsheetTag) == null)
                return@observe

            when (it) {
                is CheqSafeState.Intro -> {
                    removeAndOpenBottomsheet(
                        EnableCheqSafeBottomsheet.newInstance(intentFactory, sharedPrefs, userId, appConfig)
                    )
                }
                is CheqSafeState.LinkingFailed -> {
                    removeAndOpenBottomsheet(
                        LinkingFailedCheqSafeBottomsheet.newInstance()
                    )
                }
                is CheqSafeState.LinkingInProcess -> {
                    removeAndOpenBottomsheet(
                        LinkingProcessCheqSafeBottomSheet.newInstance()
                    )
                }
                is CheqSafeState.LinkingInitiated -> {
                    removeAndOpenBottomsheet(
                        LinkingInitiatedCheqSafeBottomSheet.newInstance(it)
                    )
                }
                is CheqSafeState.UserConfirmation -> {
                    removeAndOpenBottomsheet(
                        ConfirmationCheqSafeBottomsheet.newInstance(remoteConfig)
                    )
                }
                else -> Unit
            }
        }
    }

    private fun removeAndOpenBottomsheet(newBottomsheet: BottomSheetDialogFragment) {
        childFragmentManager
            .findFragmentByTag(bottomsheetTag)
            .takeIf { it?.javaClass != newBottomsheet.javaClass }
            .let { (it as? BottomSheetDialogFragment)?.dismiss() }

        openBottomsheet(
            newBottomsheet,
            bottomsheetTag
        )
    }

    private fun setupNameUi(personalDetails: PersonalDetails?) {
        with(binding) {
            nameInitialsTv.text = personalDetails?.initials.orEmpty()
            nameTv.text = personalDetails?.fullname.orEmpty()
            mobileTv.text = personalDetails?.mobile.orEmpty()
            shimmerContainer.stopShimmer()
            shimmerContainer.visibility = View.GONE
        }
    }

    private fun fetchUserSettings() = myAccountViewModel.fetchUserSettings()

    override fun onClick(selected: SettingsIdentifier) {
        when (selected) {
            SettingsIdentifier.PERSONAL_DETAILS -> {
                val action = MyAccountFragmentDirections.actionMyAccountFragmentToPersonalDetailsFragment().apply {
                        personalDetailsArgs = myAccountViewModel.getPersonalDetails()
                    }
                findNavController().navigate(action)
            }
            SettingsIdentifier.TRANSACTION_HISTORY -> {
                navigate(R.id.action_myAccountFragment_to_transactionHistoryFragment)
            }
            SettingsIdentifier.MANAGE_CHEQ_SAFE -> {
                openCheqSafeBottomsheet()
            }
            SettingsIdentifier.REFER_EARN -> {
                navigate(R.id.action_myAccountFragment_to_referEarnFragment)
            }
            else -> Unit
        }
    }

    private fun openCheqSafeBottomsheet() {
        myAccountViewModel.initialCheqSafeState.value?.let {
            when(it) {
                is CheqSafeState.AlreadyLinkedEmail -> {
                    openBottomsheet(
                        AlreadyLinkedEmailCheqSafeBottomsheet.newInstance(it.linkedEmails, intentFactory, sharedPrefs, remoteConfig, userId, appConfig),
                        bottomsheetTag
                    )
                }
                is CheqSafeState.Intro -> {
                    openBottomsheet(
                        EnableCheqSafeBottomsheet.newInstance(intentFactory, sharedPrefs, userId, appConfig),
                        bottomsheetTag
                    )
                }
                else -> Unit
            }
        }
    }

    private fun openBottomsheet(bottomsheet: BottomSheetDialogFragment, tag: String) {
        bottomsheet.show(childFragmentManager, tag)
    }

    private fun navigate(destination: Int) {
        findNavController().navigate(destination)
    }
}