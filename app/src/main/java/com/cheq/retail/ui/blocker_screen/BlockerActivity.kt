package com.cheq.retail.ui.blocker_screen

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.cheq.retail.R
import com.cheq.retail.api.errormodel.BlockData
import com.cheq.retail.application.MainApplication
import com.cheq.retail.constants.AFConstants.DESC_PRIMARY_CTA_CLICKED
import com.cheq.retail.constants.AFConstants.DESC_SECONDARY_CTA_CLICKED
import com.cheq.retail.constants.AFConstants.EVENT_PRIMARY_CTA_CLICKED
import com.cheq.retail.constants.AFConstants.EVENT_SECONDARY_CTA_CLICKED
import com.cheq.retail.constants.AFConstants.KEY_SCREEN_NAME
import com.cheq.retail.constants.AFConstants.PROPERTY_DESCRIPTION
import com.cheq.retail.constants.AFConstants.PROPERTY_PRIMARY_CTA
import com.cheq.retail.constants.AFConstants.PROPERTY_SECONDARY_CTA
import com.cheq.retail.constants.AFConstants.PROPERTY_TITLE
import com.cheq.retail.databinding.ActivityBlockerBinding
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsReferral
import com.cheq.retail.ui.login.CommonWebViewActivity
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.Utils
import com.moengage.core.MoECoreHelper

class BlockerActivity : AppCompatActivity() {
    private var blockData: BlockData? = null
    private var screenName: String? = null
    private lateinit var binding: ActivityBlockerBinding

    companion object {
        val TAG = BlockerActivity::class.java.simpleName
        val EXTRA_BLOCKER = "$TAG.EXTRA_BLOCKER"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlockerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        checkIntent()
        setupData()
        setActionsOnPrimaryButton()
        setActionsOnSecondaryButton()
        screenName?.let { logScreen(it) }
    }
    private fun setupData() {
        binding.tvTitle.text = blockData?.title
        binding.tvSubtitle.text = blockData?.subtitle
        Glide.with(this).load(blockData?.image).placeholder(R.drawable.ic_blocker)
            .into(binding.ivBlockerImage)
        if (!blockData?.secondaryCta?.image.isNullOrEmpty()) {
            Glide.with(this).load(blockData?.secondaryCta?.image).placeholder(R.drawable.ic_help_new).into(binding.ivHelp)
        }else{
            binding.ivHelp.visibility= View.GONE
        }
    }

    private fun setActionsOnPrimaryButton() {
        binding.btClose.setOnClickListener {
            logEvent(EVENT_PRIMARY_CTA_CLICKED, DESC_PRIMARY_CTA_CLICKED)
            when (blockData?.primaryCta?.title) {
                "CLOSE THE APP" -> { closeTheApp() }
                "TRY AGAIN" -> { tryAgain() }
                "UPDATE THE APP" -> { updateTheApp() }
            }
        }
    }

    private fun setActionsOnSecondaryButton() {
        binding.ivHelp.setOnClickListener {
            when (blockData?.secondaryCta?.metadata?.actionType) {
                "WEBLINK" -> { openWeblink() }
                "EMAIL" -> { openEmailClient() }
            }
            logEvent(EVENT_SECONDARY_CTA_CLICKED, DESC_SECONDARY_CTA_CLICKED)
        }
    }

    private fun openEmailClient() {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:") // only email apps should handle this
        val receipientAddress: String? = blockData?.secondaryCta?.metadata?.emailMetadata?.receipientAddress
        receipientAddress?.let { address ->
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(address))
        }
        intent.putExtra(
            Intent.EXTRA_SUBJECT, blockData?.secondaryCta?.metadata?.emailMetadata?.subject
        )
        intent.putExtra(Intent.EXTRA_TEXT, blockData?.secondaryCta?.metadata?.emailMetadata?.body)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun openWeblink() {
        val urlString = Utils.decodeString(blockData?.secondaryCta?.metadata?.action ?: "")
        startActivity(Intent(this, CommonWebViewActivity::class.java).apply {
            putExtra("url", urlString)
        })
    }

    private fun checkIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            blockData = intent?.getParcelableExtra(EXTRA_BLOCKER, BlockData::class.java)
        } else {
            blockData = intent?.getParcelableExtra(EXTRA_BLOCKER)
        }
        screenName = intent?.getStringExtra(KEY_SCREEN_NAME)
    }

    private fun closeTheApp() {
        logout()
        this.finishAffinity()
        System.exit(0)
    }

    private fun logout() {
        MoECoreHelper.logoutUser(this)
        SharePrefs.getInstance(MainApplication.getApplicationContext())!!
            .putBoolean(SharePrefsKeys.IS_LOGGED_IN, false)
        SharePrefs.getInstance(MainApplication.getApplicationContext())!!
            .putBoolean(SharePrefsKeys.QUICK_LOGIN_AVAILABLE, true)

        SharePrefsReferral.getInstance(this)?.setCardShown(SharePrefsKeys.IS_CARD_SHOWN, false)

        SharePrefsReferral.getInstance(applicationContext)
            ?.putBoolean(SharePrefsKeys.SSL_SAVED, false)

        MoECoreHelper.logoutUser(MainApplication.getApplicationContext())

        MainApplication.finartLogout()
    }

    private fun tryAgain() {

    }

    private fun updateTheApp() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }
    }
    private fun logScreen(screenName: String?) {
        screenName?.let { screen ->
            val propertyMap = hashMapOf<String, String>()
            propertyMap[PROPERTY_TITLE] = blockData?.title.toString()
            propertyMap[PROPERTY_DESCRIPTION] = blockData?.subtitle.toString()
            propertyMap[PROPERTY_PRIMARY_CTA] = blockData?.primaryCta?.title.toString()
            propertyMap[PROPERTY_SECONDARY_CTA] = blockData?.secondaryCta?.metadata?.actionType.toString()
            MparticleUtils.logBlockerScreen(screen, propertyMap)
        }
    }
    private fun logEvent(eventName: String, description: String) {
        val propertyMap = hashMapOf(PROPERTY_DESCRIPTION to description)
        MparticleUtils.logBlockerEvent(eventName, propertyMap)
    }
}