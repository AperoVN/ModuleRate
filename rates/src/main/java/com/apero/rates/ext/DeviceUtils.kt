package com.apero.rates.ext

import android.app.ActivityManager
import android.content.Context
import java.util.Locale

internal object DeviceUtils {
    private fun floatForm(d: Double): String {
        return String.format(Locale.US, "%.2f", d)
    }

    private fun bytesToHuman(size: Long): String {
        val Kb: Long = 1024
        val Mb = Kb * 1024
        val Gb = Mb * 1024
        val Tb = Gb * 1024
        val Pb = Tb * 1024
        val Eb = Pb * 1024
        if (size < Kb) return floatForm(size.toDouble()) + " byte"
        if (size in Kb until Mb) return floatForm(size.toDouble() / Kb) + " KB"
        if (size in Mb until Gb) return floatForm(size.toDouble() / Mb) + " MB"
        if (size in Gb until Tb) return floatForm(size.toDouble() / Gb) + " GB"
        if (size in Tb until Pb) return floatForm(size.toDouble() / Tb) + " TB"
        if (size in Pb until Eb) return floatForm(size.toDouble() / Pb) + " PB"
        return if (size >= Eb) floatForm(size.toDouble() / Eb) + " EB" else "0"
    }

    @JvmStatic
    fun getRAMDeviceInfo(context: Context?): String? {
        val activityManager =
            context?.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager?.getMemoryInfo(memoryInfo)
        return bytesToHuman(memoryInfo.totalMem)
    }
}
