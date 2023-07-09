package com.cheq.retail.inappratings.actions

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri


/**
 * Created by Sanket Mendon on 23,June,2023.
 * sanket@cheq.one
 */
class IntentAction(ctx: Context) : BaseAction {
    private val context = ctx
    override fun execute() {
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=com.cheq.retail")
                )
            )
        } catch (e: ActivityNotFoundException) {
            context.startActivity(
                Intent().apply {
                    Intent.ACTION_VIEW
                    Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
                }
            )
        }
    }
}