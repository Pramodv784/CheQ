package com.cheq.retail.ui.rewards.view

import android.os.Bundle
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.utils.Utils

class CreditProcessingActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit_processing)
        Utils.setLightStatusBar(this)
    }
}