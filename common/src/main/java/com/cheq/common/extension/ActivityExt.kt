package com.cheq.common.extension

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * A value holder that automatically clears the reference if the fragment's view is destroyed
 * @param <T>
 *
 * Created by Akash Khatkale on 16th May, 2023.
 * akash.k@cheq.one
 */
fun <T> AppCompatActivity.viewBinding(): ReadWriteProperty<AppCompatActivity, T> =
    object : ReadWriteProperty<AppCompatActivity, T>, LifecycleObserver {

        private var binding: T? = null


        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            binding = null
        }

        override fun getValue(
            thisRef: AppCompatActivity,
            property: KProperty<*>
        ): T {
            return this.binding ?: error(
                "Binding value is null."
            )
        }

        override fun setValue(
            thisRef: AppCompatActivity,
            property: KProperty<*>,
            value: T
        ) {
            // Set the backing property
            this.binding = value
        }
    }