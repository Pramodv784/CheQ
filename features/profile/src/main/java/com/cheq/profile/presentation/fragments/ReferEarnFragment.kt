package com.cheq.profile.presentation.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.appsflyer.share.LinkGenerator
import com.appsflyer.share.ShareInviteHelper
import com.cheq.cache.sharedprefs.SharedPrefs
import com.cheq.cache.sharedprefs.SharedPrefsCheQ
import com.cheq.cache.sharedprefs.SharedPrefsCheQUser
import com.cheq.cache.sharedprefs.SharedPrefsConstants.BASE_FAQS
import com.cheq.cache.sharedprefs.SharedPrefsConstants.CHEQ_USER_ID
import com.cheq.cache.sharedprefs.SharedPrefsConstants.REF_SHORT_URL
import com.cheq.common.extension.copyClipboard
import com.cheq.common.extension.empty
import com.cheq.common.extension.getInviteHelper
import com.cheq.common.models.takeIfError
import com.cheq.common.models.takeIfLoading
import com.cheq.common.models.takeIfSuccess
import com.cheq.common.ui.base.BaseFragment
import com.cheq.common.utils.Constants.AF_DEV_KEY
import com.cheq.common.utils.Constants.BASE_POLICIES
import com.cheq.common.utils.Constants.TEMPLATE_ID
import com.cheq.common.utils.Constants.T_C_REFERRAL
import com.cheq.common.utils.ShareUtils
import com.cheq.common.utils.StatusBarUtils
import com.cheq.navigation.IntentFactory
import com.cheq.navigation.IntentKey
import com.cheq.profile.R
import com.cheq.profile.databinding.FragmentReferEarnBinding
import com.cheq.profile.domain.entities.ReferralEarned
import com.cheq.profile.domain.entities.ReferralStatic
import com.cheq.profile.presentation.viewmodels.ReferEarnViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.cheq.common.R as commonR
import com.cheq.proxima.R as proxima

/**
 * Created by Akash Khatkale on 20th May, 2023.
 * akash.k@cheq.one
 */
@AndroidEntryPoint
class ReferEarnFragment : BaseFragment<FragmentReferEarnBinding>() {

    @Inject
    @SharedPrefsCheQUser
    lateinit var sharedPrefs: SharedPrefs

    @Inject
    @SharedPrefsCheQ
    lateinit var sharedPrefsCheQ: SharedPrefs

    @Inject
    lateinit var intentFactory: IntentFactory

    private val viewModel: ReferEarnViewModel by viewModels()
    private val userId: String by lazy {
        sharedPrefs.getString(CHEQ_USER_ID, String.empty)
    }
    private var referralStatic: ReferralStatic? = null
    private var linkListener: LinkGenerator.AFa1xSDK? = object : LinkGenerator.AFa1xSDK {
        override fun onResponse(s: String) {
            if (binding != null) {
                binding.linkTv.text = s
            }
            sharedPrefs.putString(REF_SHORT_URL, s)
            sendReferral(s)
        }
        override fun onResponseError(s: String) = Unit
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentReferEarnBinding {
        return FragmentReferEarnBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // todo : log mparticle current screen
        // todo : handle cases where this fragment is directly opened from home
        AppsFlyerLib.getInstance().setAppInviteOneLink(TEMPLATE_ID)

        viewModel.getReferralStaticData(userId)
        viewModel.getReferralEarnedData(userId)

        setupClickListeners()
        setupObservers()
        setupTermsConditions()
        setupReferralLinkUi()
        startAppsFlyer()
    }

    private fun setupReferralLinkUi() {
        val refUrl = sharedPrefs.getString(REF_SHORT_URL)
        if (refUrl.isEmpty())
            generateReferralLink()
        else {
            binding.linkTv.text = refUrl
        }
    }

    private fun setupTermsConditions() {
        val termsText = "<font color=#858989>Read</font> <font color=#00C197> Terms & Conditions</font>"
        binding.termsTv.text = HtmlCompat.fromHtml(termsText, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun setupObservers() {
        viewModel.referralStaticData.observe(viewLifecycleOwner) {
            it.takeIfLoading {
                setLoadingUi()
            }.takeIfSuccess {
                setReferralStaticUi(data)
            }.takeIfError {
                showWarningToast(
                    binding.root,
                    error.localizedMessage.orEmpty()
                )
            }
        }
        viewModel.referralEarnedData.observe(viewLifecycleOwner) {
            it?.let {
                setReferralEarnedUi(it)
            }
        }
    }

    private fun setLoadingUi() {
        binding.loadingLottie.isVisible = true
        binding.parentSv.isVisible = false
        binding.toolbarCl.backgroundTintList = ColorStateList.valueOf(requireContext().getColor(
            proxima.color.white))
    }

    private fun setReferralEarnedUi(referralEarned: ReferralEarned) {
        with(binding) {
            headerFriendsDivider.isVisible = referralEarned.show
            friendsReferredLl.isVisible = referralEarned.show
            rewardsEarnedTv.text = referralEarned.totalRewardsEarned.toString()
            friendInvitedTv.text = referralEarned.totalFriendsInvited.toString()
            friendLimitTv.isVisible = referralEarned.totalReferralLimit > 0
            friendLimitTv.text = "/${referralEarned.totalReferralLimit.toString()}"
        }
    }

    private fun setReferralStaticUi(static: ReferralStatic) {
        with(binding) {
            loadingLottie.isVisible = false
            parentSv.isVisible = true
            toolbarCl.backgroundTintList = ColorStateList.valueOf(requireContext().getColor(
                proxima.color.primary_p0))
            titleTv.text = if (static.offerValid) "${titleTv.context.getString(commonR.string.refer_n_earn)}\n${static.youGetValue} ${titleTv.context.getString(
                commonR.string.ref_cheq_chips)}" else titleTv.context.getString(commonR.string.refer_your_friends)
            daysLeftTv.text = static.daysLeft
            daysLeftLl.isVisible = static.daysLeft.isNotEmpty()
            defaultLl.isVisible = static.offerValid.not()
            defaultMessageTv.text = static.defaultMessage
            joiningVerticalView.isVisible = static.offerValid
            secondMessageRl.isVisible = static.offerValid
            youGetLl.isVisible = static.youGetMessage.isNotEmpty()
            youGetTv.text = static.youGetMessage
            youGetAmountTv.text = static.youGetValue.toString()
            cheqChipsLblTv.isVisible = static.type == ReferralStatic.ReferralStaticType.ONE_SIDED
            twoSidedBorderView.isVisible = static.type == ReferralStatic.ReferralStaticType.TWO_SIDED
            twoSidedLl.isVisible = static.type == ReferralStatic.ReferralStaticType.TWO_SIDED
            twoSidedLblTv.text = static.friendsGetMessage.ifEmpty { twoSidedLblTv.context.getString(commonR.string.friend_gets) }
            friendGetTv.text = if (static.friendsGetValue == 0) "100" else static.friendsGetValue.toString()
            secondPointTv.text = static.steps[0].ifEmpty { secondPointTv.context.getString(commonR.string.download_register) }
            thirdPointTv.text = static.steps[1].ifEmpty { secondPointTv.context.getString(commonR.string.frnd_complete_first_pay) }
        }
    }

    override fun onResume() {
        super.onResume()
        StatusBarUtils.setStatusBarColor(requireActivity(), ContextCompat.getColor(requireContext(), proxima.color.primary_p0))
    }

    private fun setupClickListeners() {
        with(binding) {
            backIv.setOnClickListener {
                if(findNavController().popBackStack().not()) {
                    requireActivity().onBackPressed()
                }
            }
            termsTv.setOnClickListener {
                val url = "${sharedPrefsCheQ.getString(BASE_POLICIES)}${T_C_REFERRAL}"
                startWebView(url)
            }
            helpTv.setOnClickListener {
                val url = "${sharedPrefsCheQ.getString(BASE_FAQS)}${getString(commonR.string.referral_faq)}"
                startWebView(url)
            }
            inviteButtonLl.setOnClickListener {
                onWhatsappClicked()
            }
            copyIv.setOnClickListener {
                requireContext().copyClipboard("label", linkTv.text.toString())
                showToast(it, getString(commonR.string.link_copied), com.cheq.common.R.drawable.ic_check_circle)
            }
            friendsReferredLl.setOnClickListener {
                findNavController().navigate(R.id.action_referEarnFragment_to_referralRewardsFragment)
            }
        }
    }

    private fun startWebView(url: String) {
        val intent = intentFactory.createIntent(requireContext(), IntentKey.GenericWebViewActivityKey(url))
        startActivity(intent)
    }

    private fun startAppsFlyer() {
        AppsFlyerLib.getInstance().start(
            requireContext(),
            AF_DEV_KEY, object : AppsFlyerRequestListener {
                override fun onSuccess() = Unit
                override fun onError(errorCode: Int, errorDesc: String) = Unit
            }
        )
    }

    private fun onWhatsappClicked() {
        // todo: log firebase, appsflyer and mparticle event
        val message = referralStatic?.whatsappMessage?.replace(
            "[URL]",
            binding.linkTv.text.toString()
        ) ?: (getString(commonR.string.hey_this_your_referral) + "\n\n" + binding.linkTv.text)
        ShareUtils.genericShare(
            requireContext(),
            message,
            ShareUtils.ShareType.SHARE_WHATSAPP,
            commonR.string.install_the_app
        )
    }

    private fun generateReferralLink() {
        val linkGenerator = ShareInviteHelper.generateInviteUrl(context)
        if (userId.isNotEmpty()) {
            linkGenerator.getInviteHelper(
                userId,
                requireContext(),
                getString(commonR.string.af_og_title),
                getString(commonR.string.af_og_description)
            )
        }
        linkGenerator.generateLink(context, linkListener)
    }

    private fun sendReferral(url: String) {
        viewModel.sendReferralUrl(userId, url)
    }

    override fun onDestroy() {
        super.onDestroy()
        linkListener = null
    }
}