package com.apero.ratesample

import android.app.Application
import com.apero.rates.ModuleRate

/**
 * Created by KO Huyn on 14/03/2024.
 */
class App:Application() {
    override fun onCreate() {
        super.onCreate()
        ModuleRate.install(this)
    }
}