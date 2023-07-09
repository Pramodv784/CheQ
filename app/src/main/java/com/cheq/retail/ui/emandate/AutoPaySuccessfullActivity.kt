package com.cheq.retail.ui.emandate

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.databinding.ActivityAutoPaySuccessfullBinding

class AutoPaySuccessfullActivity : BaseActivity() {
    lateinit var binding: ActivityAutoPaySuccessfullBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_auto_pay_successfull)
//        MparticleUtils.logCurrentScreen("","","","",
//            "",
//            "", SharePrefs.instance?.getBoolean(""),this)
    }
}