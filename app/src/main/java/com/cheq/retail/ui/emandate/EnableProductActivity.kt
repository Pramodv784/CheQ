package com.cheq.retail.ui.emandate

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.databinding.ActivityEnableProductBinding
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.emandate.adapter.AutoPayProductsAdapter
import com.cheq.retail.ui.emandate.viewModel.EmandateViewModel
import com.cheq.retail.ui.login.modelv2.productv1.ProductV2
import com.cheq.retail.ui.main.MainActivity
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.Utils

class EnableProductActivity : BaseActivity(), AutoPayProductsAdapter.AutopayProductInterface {
    lateinit var binding: ActivityEnableProductBinding
    private var viewModel: EmandateViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUI()
        setupViewModel()
        setupObserver()
        MparticleUtils.logCurrentScreen("/emandate-registration/enable-autopay", "The customer proceeds ahead to enable autopay on individual products", "", "", "", "", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.EMandate_Enable_AutoPay), this@EnableProductActivity)
    }

    override fun onBackPressed() {
        finish()
        MparticleUtils.logEvent("Emandate_AutopayEnable_BackClicked", "User chooses to close the sscreen by clicking the cancel CTA", "Unique", "Back", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Emandate_AutopayEnable_BackClicked), this@EnableProductActivity)
    }

    private fun setUI() {
        Utils.setLightStatusBar(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_enable_product)
        binding.activity = this
    }

    fun closeScreen() {
        MparticleUtils.logEvent("Emandate_AutopayEnable_BackClicked", "User chooses to close the sscreen by clicking the cancel CTA", "Unique", "Back", SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Emandate_AutopayEnable_BackClicked), this@EnableProductActivity)

        startActivity(Intent(this, MainActivity::class.java).apply {
            putExtra("COMING_FROM", "Enable_Product")
        })
        finishAffinity()
    }


    override fun onResume() {
        super.onResume()
        viewModel?.getAddedProducts()
    }

    private fun setupObserver() {
        viewModel?.productObserver?.observe(this) {
            if (it.products != null) {

                val list: ArrayList<ProductV2> = ArrayList()

                for (i in it.products) {
                    if (i.product_status=="2") {
                        list.add(i)
                    }
                }

                binding.rvProductList.layoutManager = LinearLayoutManager(this)
                binding.rvProductList.adapter =
                    AutoPayProductsAdapter(this, this, list)
            }
        }

        viewModel?.statusObserver?.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        viewModel?.progressObserver?.observe(this) {
            if (it) {
                Utils.showProgressDialog(this)
            } else {
                Utils.hideProgressDialog()
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[EmandateViewModel::class.java]
    }

    override fun onActiveClick(
        productId: String,
        totalDueEnabled: Boolean,
        isAutoPayEnabled: Boolean
    ) {
        viewModel?.modifyAutoPay(
            productId,
            totalDueEnabled,
            isAutoPayEnabled
        )

        println("AUTOPAY +++++ " + productId + " " + totalDueEnabled + " " + isAutoPayEnabled)
    }


    override fun onFullDue(productId: Int) {

    }
}