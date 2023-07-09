package com.cheq.retail.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.cheq.retail.R
import com.cheq.retail.databinding.FragmentMyVoucherBinding
import com.cheq.retail.ui.main.maininterface.RewardsInterface
import com.cheq.retail.ui.main.model.DenomsItem

class MyVoucherFragment : Fragment(), RewardsInterface {


    var mBinding: FragmentMyVoucherBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_voucher, container, false)
        setUpUi()
        return mBinding!!.root
    }

    private fun setUpUi() {

    }

    override fun onMyVoucherClicked(isClicked: Boolean, data: DenomsItem) {

    }

    override fun onMyVoucherClicked(isClicked: Boolean) {

    }

    override fun onMyC2CClicked(isClicked: Boolean, amount : Float, convertToCashRate : Double) {

    }

    override fun onCategoriesClicked(catId: String, categoryPosition : Int) {

    }
    override fun onCategoriesClicked(categoryPosition: Int) {


    }
}