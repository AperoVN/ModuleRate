package com.apero.rates.ext

import com.ads.control.admob.AppOpenManager
import com.ads.control.ads.AperoAd
import com.ads.control.applovin.AppOpenMax
import com.ads.control.config.AperoAdConfig

/**
 * Created by KO Huyn on 06/03/2024.
 */
internal fun disableAdResumeByClickAction() {
    if (AperoAd.getInstance().mediationProvider == AperoAdConfig.PROVIDER_ADMOB) {
        AppOpenManager.getInstance().disableAdResumeByClickAction()
    } else {
        AppOpenMax.getInstance().disableAdResumeByClickAction()
    }
}