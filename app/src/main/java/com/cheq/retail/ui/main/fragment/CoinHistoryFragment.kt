package com.cheq.retail.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.cheq.retail.R
import com.cheq.retail.databinding.FragmentCoinHistoryBinding
import com.cheq.retail.ui.rewards.adapter.CoinHistoryAdapter

private var dummyList = arrayListOf(
    "+3000",
    "+3000",
    "+3000",
    "+3000",
    "+3000",
    "+3000",
    "+3000",
    "+3000",
    "+3000",
    "+3000",
    "+3000",
    "+3000"
)

class CoinHistoryFragment : Fragment() {
    var mBinding: FragmentCoinHistoryBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_coin_history, container, false)
        setUpUi()
        return mBinding!!.root

    }

    private fun setUpUi() {
        mBinding!!.rvMyCoinHistory.apply {
            adapter = CoinHistoryAdapter(requireContext(), dummyList)
            hasFixedSize()
        }
    }
}