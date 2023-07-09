package com.cheq.proxima.ui.toast

/**
 * Created by Akash Khatkale on 31st May, 2023.
 * akash.k@cheq.one
 */
class CheqToastConfig private constructor(
    builder: CheqToastBuilder
) {
    var message: String = ""
    var dismissButton: ToastButtonConfig? = null
    var acceptButton: ToastButtonConfig? = null
    var startIcon: Int? = null
    var endIcon: Int? = null

    init {
        message = builder.message
        dismissButton = builder.dismissButton
        acceptButton = builder.acceptButton
        startIcon = builder.startIcon
        endIcon = builder.endIcon
    }

    class CheqToastBuilder(
        val message: String
    ) {

        var dismissButton: ToastButtonConfig? = null
            private set
        var acceptButton: ToastButtonConfig? = null
            private set

        var startIcon: Int? = null
            private set
        var endIcon: Int? = null
            private set

        fun showDismissButton(): CheqToastBuilder = apply {
            this.dismissButton = ToastButtonConfig(
                "CANCEL",
            )
        }

        fun showDismissButton(config: ToastButtonConfig): CheqToastBuilder = apply {
            this.dismissButton = config
        }

        fun showAcceptButton(): CheqToastBuilder = apply {
            this.acceptButton = ToastButtonConfig(
                "ACCEPT",
            )
        }

        fun showAcceptButton(config: ToastButtonConfig): CheqToastBuilder = apply {
            this.acceptButton = config
        }

        fun setStartIcon(icon: Int): CheqToastBuilder = apply {
            this.startIcon = icon
        }

        fun setEndIcon(icon: Int): CheqToastBuilder = apply {
            this.endIcon = icon
        }

        fun build() = CheqToastConfig(
            this
        )
    }
}