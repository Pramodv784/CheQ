package com.cheq.proxima.ui.toast

/**
 * Created by Akash Khatkale on 31st May, 2023.
 * akash.k@cheq.one
 */
data class ToastButtonConfig(
    val text: String,
    val onClick: () -> Unit = {}
)