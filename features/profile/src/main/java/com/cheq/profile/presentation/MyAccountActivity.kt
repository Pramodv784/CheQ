package com.cheq.profile.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.cheq.common.extension.viewBinding
import com.cheq.common.ui.base.BaseActivity
import com.cheq.navigation.IntentFactory
import com.cheq.navigation.IntentKey
import com.cheq.navigation.IntentKey.MyAccountActivityKey.Companion.PROFILE_DESTINATION
import com.cheq.navigation.IntentKey.MyAccountActivityKey.Companion.REFER_EARN_DESTINATION
import com.cheq.navigation.IntentProvider
import com.cheq.profile.R
import com.cheq.profile.databinding.ActivityMyAccountBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Created by Akash Khatkale on 15th May, 2023.
 * akash.k@cheq.one
 */
@AndroidEntryPoint
class MyAccountActivity : BaseActivity() {

    private var binding: ActivityMyAccountBinding by viewBinding()

    @Inject
    lateinit var intentFactory: IntentFactory

    companion object {
        const val CHEQ_SAFE = "CHEQ_SAFE"
        const val START_DESTINATION = "START_DESTINATION"
        const val GO_HOME_WHEN_BACK = "GO_HOME_WHEN_BACK"
        val intentHelper = object:
            IntentProvider<IntentKey.MyAccountActivityKey> {
            override fun createIntent(context: Context, key: IntentKey.MyAccountActivityKey): Intent {
                return Intent(context, MyAccountActivity::class.java).apply {
                    putExtra(CHEQ_SAFE, key.cheqSafe)
                    putExtra(START_DESTINATION, key.startDestination)
                    putExtra(GO_HOME_WHEN_BACK, key.goToHomeOnBack)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        setStartDestination()
    }

    private fun setStartDestination() {
        val navHost = supportFragmentManager.findFragmentById(R.id.my_account_fragment) as NavHostFragment
        val navController = navHost.navController
        val navInflater = navController.navInflater
        val graph = navInflater.inflate(R.navigation.profile_nav_graph)

        graph.setStartDestination(
            when (intent.getStringExtra(START_DESTINATION)) {
                PROFILE_DESTINATION -> R.id.myAccountFragment
                REFER_EARN_DESTINATION -> R.id.referEarnFragment
                else -> R.id.myAccountFragment
            }
        )
        navController.graph = graph
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            handleOnBackPress()
        }
    }

    private fun handleOnBackPress() {
        if (intent.getBooleanExtra(GO_HOME_WHEN_BACK, false)) {
            startActivity(
                intentFactory.createIntent(
                    this,
                    IntentKey.MainActivityKey
                )
            )
        } else if(findNavController(R.id.my_account_fragment).popBackStack().not()) {
            finish()
        }
    }
}