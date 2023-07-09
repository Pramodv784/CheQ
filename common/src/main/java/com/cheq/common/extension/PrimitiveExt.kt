package com.cheq.common.extension

val Int?.orZero: Int get() = this ?: 0

val Boolean?.orFalse: Boolean get() = this ?: false

val String.Companion.empty: String get() = ""