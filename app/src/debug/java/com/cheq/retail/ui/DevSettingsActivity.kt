package com.cheq.retail.ui

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cheq.common.utils.StatusBarUtils
import com.cheq.retail.databinding.ActivityDevSettingsBinding
import com.cheq.retail.ui.adapter.DevSettingsRecyclerAdapter
import com.cheq.retail.ui.splash.SplashActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Akash Khatkale on 5th June, 2023.
 * akash.k@cheq.one
 */
@AndroidEntryPoint
class DevSettingsActivity : AppCompatActivity() {

    lateinit var binding: ActivityDevSettingsBinding
    private val viewModel: DevSettingsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDevSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUi()
    }

    private fun setupUi() {
        with(binding) {
            backIv.setOnClickListener {
                finish()
            }
            doneTv.setOnClickListener {
                val intent = Intent(this@DevSettingsActivity, SplashActivity::class.java)
                intent.flags = FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
                Runtime.getRuntime().exit(0)
            }
            devSettingsRv.apply {
                adapter = DevSettingsRecyclerAdapter(viewModel.getItems())
                layoutManager = LinearLayoutManager(this@DevSettingsActivity)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        StatusBarUtils.setLightStatusBar(this)
    }
}