package com.apero.rates.analytics

import android.app.Application
import android.util.Log
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics

/**
 * Created by KO Huyn on 07/03/2024.
 */
object Analytics {
    private const val TAG = "ConsoleAnalytics"
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    fun install(application: Application) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(application)
    }

    @JvmStatic
    fun track(event: String) {
        firebaseAnalytics.logEvent(event, null)
        Log.v(TAG, "eventName:${event}")
    }

    @JvmStatic
    fun track(eventName: String, params: HashMap<String, Any?>) {
        val bundle = bundleOf(*params.map { (k, v) -> k to (v ?: k) }.toList().toTypedArray())
        firebaseAnalytics.logEvent(eventName, bundle)
        Log.v(TAG, "eventName:${eventName}, params:$bundle")
    }

    @JvmStatic
    fun track(eventName: String, vararg param: Pair<String, Any?>) {
        val bundle = bundleOf(*param.map { (k, v) -> k to (v ?: k) }.toList().toTypedArray())
        firebaseAnalytics.logEvent(eventName, bundle)
        Log.v(TAG, "eventName:${eventName}, params:$bundle")
    }
}