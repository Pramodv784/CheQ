package com.cheq.retail.ui.vouchers

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.databinding.ActivityVouchersBinding
import com.cheq.retail.ui.vouchers.adapter.VoucherImageAdapter

class VouchersActivity : BaseActivity() {
    lateinit var binding: ActivityVouchersBinding
    val viewModel by viewModels<VouchersViewModel>()
    lateinit var adapter: VoucherImageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_vouchers)
        setUpView()
    }

    private fun setUpView() {
        adapter = VoucherImageAdapter()
        binding.voucherImageRV.layoutManager = LinearLayoutManager(applicationContext)
        binding.voucherImageRV.adapter = adapter

        viewModel.voucherImageList.observe(this, Observer {
            adapter.voucherImageList = it
        })
    }
}