package com.cheq.retail.startup

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import androidx.startup.Initializer
import com.cheq.retail.R
import com.cheq.retail.ui.DevSettingsActivity
import com.github.takahirom.hyperion.plugin.simpleitem.SimpleItem
import com.github.takahirom.hyperion.plugin.simpleitem.SimpleItemHyperionPlugin

class HyperionInitializer: Initializer<Unit> {
    override fun create(context: Context) {
        SimpleItemHyperionPlugin.addItem(
            SimpleItem.Builder().apply {
                title("Dev Settings")
                text("Cheq Dev settings to change backend environment and other configurations")
                image(R.drawable.ic_setting)
                clickListener {
                    context.startActivity(
                        Intent(
                            context,
                            DevSettingsActivity::class.java
                        ).apply {
                            flags = FLAG_ACTIVITY_NEW_TASK
                        }
                    )
                }
            }.build()
        )
        SimpleItemHyperionPlugin.addItem(
            SimpleItem.Builder().apply {
                title("Simulate crash")
                text("Throws a fake crash for testing purpose")
                clickListener { error("Fake crash") }
            }.build()
        )

    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}