package com.cheq.common.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.cheq.common.R
import com.cheq.common.extension.viewBinding
import com.cheq.proxima.ui.toast.CheqToast
import com.cheq.proxima.ui.toast.CheqToastConfig

/**
 * Created by Akash Khatkale on 30th May, 2023.
 * akash.k@cheq.one
 */
abstract class BaseFragment<T : ViewBinding> : Fragment() {

    private var _binding by viewBinding<T>()
    protected val binding: T
        get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflateBinding(inflater, container)
        return _binding.root
    }

    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): T

    fun showWarningToast(view: View, message: String) {
        CheqToast().showCheqSnackbar(
            view,
            requireContext(),
            CheqToastConfig
                .CheqToastBuilder(message)
                .setStartIcon(R.drawable.ic_warning)
                .build()
        )
    }

    fun showToast(view: View, message: String, icon: Int) {
        CheqToast().showCheqSnackbar(
            view,
            requireContext(),
            CheqToastConfig
                .CheqToastBuilder(message)
                .setStartIcon(icon)
                .build()
        )
    }

}