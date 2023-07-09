package com.cheq.retail.ui.vouchers

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cheq.retail.application.MainApplication
import com.cheq.retail.extensions.waitlistBaseUrl
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.ui.vouchers.model.VoucherModel

class VouchersViewModel: ViewModel() {
    val voucherImageList = MutableLiveData<List<VoucherModel>>()

    init {
        getVouchersImages()
        voucherImageList.value = emptyList()
    }

    private fun getVouchersImages() {
        val imageList =  listOf(
            VoucherModel(imageUrl = "${SharePrefs.getInstance(MainApplication.getApplicationContext())?.waitlistBaseUrl}Slice1.png"),
            VoucherModel(imageUrl = "${SharePrefs.getInstance(MainApplication.getApplicationContext())?.waitlistBaseUrl}Slice2.png"),
            VoucherModel(imageUrl = "${SharePrefs.getInstance(MainApplication.getApplicationContext())?.waitlistBaseUrl}Slice3.png")
        )
        voucherImageList.postValue(imageList)
    }
}