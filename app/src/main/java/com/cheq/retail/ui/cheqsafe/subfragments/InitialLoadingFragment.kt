package com.cheq.retail.ui.cheqsafe.subfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cheq.retail.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class InitialLoadingFragment: BottomSheetDialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cheq_safe_initial_loading, container, false)
    }

}