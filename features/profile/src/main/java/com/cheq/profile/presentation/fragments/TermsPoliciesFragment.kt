package com.cheq.profile.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cheq.cache.sharedprefs.SharedPrefs
import com.cheq.cache.sharedprefs.SharedPrefsCheQ
import com.cheq.common.ui.base.BaseFragment
import com.cheq.common.utils.Constants
import com.cheq.common.utils.StatusBarUtils
import com.cheq.navigation.IntentFactory
import com.cheq.navigation.IntentKey
import com.cheq.profile.databinding.FragmentTermsPoliciesBinding
import com.cheq.profile.presentation.adapters.MyAccountSettingsAdapter
import com.cheq.profile.presentation.listeners.OnAccountSettingsClicked
import com.cheq.profile.presentation.models.SettingsModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.cheq.common.R as commonR

/**
 * Created by Akash Khatkale on 24th May, 2023.
 * akash.k@cheq.one
 */
@AndroidEntryPoint
class TermsPoliciesFragment : BaseFragment<FragmentTermsPoliciesBinding>(), OnAccountSettingsClicked {

    @Inject
    lateinit var intentFactory: IntentFactory

    @Inject
    @SharedPrefsCheQ
    lateinit var sharedPrefsCheQ: SharedPrefs

    private var listData = listOf(
        SettingsModel(
            SettingsModel.SettingsIdentifier.TERMS_CONDITIONS,
            commonR.string.terms_and_conditions,
            commonR.drawable.ic_terms_cond)
        ,
        SettingsModel(
            SettingsModel.SettingsIdentifier.PRIVACY_POLICY,
            commonR.string.privacy_policy,
            commonR.drawable.ic_privacy_policy
        ),
    )
    private var termsConditionsUrl = ""
    private var privacyPolicyUrl = ""

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTermsPoliciesBinding {
        return FragmentTermsPoliciesBinding.inflate(inflater, container, false)
    }

    override fun onResume() {
        super.onResume()
        StatusBarUtils.setLightStatusBar(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()
        setData()
        setupClickListeners()
    }

    private fun setData() {
        termsConditionsUrl = "${sharedPrefsCheQ.getString(Constants.BASE_POLICIES)}${Constants.TERMS_CONDITION}"
        privacyPolicyUrl = "${sharedPrefsCheQ.getString(Constants.BASE_POLICIES)}${Constants.PRIVACY_POLICY}"
    }

    private fun setupUi() {
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        with(binding.recyclerview){
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter =  MyAccountSettingsAdapter(listData, this@TermsPoliciesFragment)
        }
    }

    private fun setupClickListeners() {
        with(binding) {
            backIv.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun startWebView(url: String) {
        val intent = intentFactory.createIntent(
            requireContext(),
            IntentKey.GenericWebViewActivityKey(
                url = url
            )
        )
        requireActivity().startActivity(intent)
    }

    override fun onClick(selected: SettingsModel.SettingsIdentifier) {
        when(selected) {
            SettingsModel.SettingsIdentifier.PRIVACY_POLICY -> {
                startWebView(privacyPolicyUrl)
            }
            SettingsModel.SettingsIdentifier.TERMS_CONDITIONS -> {
                startWebView(termsConditionsUrl)
            }
            else -> Unit
        }
    }
}