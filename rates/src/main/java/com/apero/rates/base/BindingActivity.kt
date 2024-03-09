package com.apero.rates.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.viewbinding.ViewBinding
import com.apero.rates.analytics.Analytics

/**
 * Created by KO Huyn on 15/08/2023.
 */
internal abstract class BindingActivity<T : ViewBinding> : AppCompatActivity() {
    companion object {
        private const val TAG = "BaseActivityBinding"
    }

    protected lateinit var binding: T
        private set

    @ColorRes
    open fun getStatusBarColor(): Int {
        return android.R.color.white
    }

    open fun isLightStatusBar(): Boolean {
        return true
    }

    protected abstract fun inflateBinding(layoutInflater: LayoutInflater): T
    protected abstract fun updateUI(savedInstanceState: Bundle?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, getStatusBarColor())
        binding = inflateBinding(layoutInflater)
        setContentView(binding.root)
        WindowCompat.getInsetsController(window, binding.root).apply {
            isAppearanceLightStatusBars = isLightStatusBar()
        }
        supportActionBar?.hide()
        actionBar?.hide()
        updateUI(savedInstanceState)
    }

    fun track(eventName: String) {
        Analytics.track(eventName)
    }

    fun track(eventName: String, params: HashMap<String, Any?>) {
        Analytics.track(eventName,params)
    }

    fun track(eventName: String, vararg param: Pair<String, Any?>) {
        Analytics.track(eventName,*param)
    }

    fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
