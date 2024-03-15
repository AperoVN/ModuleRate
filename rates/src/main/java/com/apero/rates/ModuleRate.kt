package com.apero.rates

import android.app.Application
import android.content.Context
import androidx.fragment.app.FragmentManager
import com.apero.rates.analytics.Analytics
import com.apero.rates.feedback.FeedbackActivity
import com.apero.rates.remote.RemoteRateConfiguration
import com.google.firebase.remoteconfig.FirebaseRemoteConfig

/**
 * Created by KO Huyn on 06/03/2024.
 */
object ModuleRate {
    private var hasInitialized = false
    internal var VERSION_NAME: String? = null
    internal var VERSION_CODE: Int? = null
    internal var APP_NAME: String? = null
    internal var onDismissAppOpenListener: () -> Unit = {}
    fun install(application: Application) {
        if (hasInitialized) return
        hasInitialized = true
        RemoteRateConfiguration.getInstance().init(application)
        Analytics.install(application)
    }

    fun install(application: Application, versionCode: Int, versionName: String, appName: String) {
        install(application)
        this.VERSION_CODE = versionCode
        this.VERSION_NAME = versionName
        this.APP_NAME = appName
    }

    fun setOnDismissAppOpenListener(onDismissAppOpenListener: () -> Unit) = apply {
        this.onDismissAppOpenListener = onDismissAppOpenListener
    }

    fun showRate(fm: FragmentManager, resultListener: (isRated: Boolean) -> Unit) {
        ChooseRateBottomSheet()
            .setOnCloseRateListener { resultListener(false) }
            .setOnShowRateListener { isSatisfied, chooseDialog ->
                chooseDialog.dismiss()
                RateAppDialog()
                    .setRateSatisfied(isSatisfied)
                    .setOnRateSuccessfully { isRated, dialog ->
                        resultListener(isRated)
                        dialog.dismiss()
                    }.show(fm)
            }.show(fm)
    }

    fun showFeedback(context: Context) {
        FeedbackActivity.start(context)
    }

    fun syncWithRemoteConfig(remoteConfig: FirebaseRemoteConfig) {
        RemoteRateConfiguration.getInstance().sync(remoteConfig)
    }

    fun getFeedbackClazz(): Class<*> {
        return FeedbackActivity::class.java
    }
}