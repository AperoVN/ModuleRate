package com.apero.rates.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.apero.rates.R
import com.apero.rates.analytics.Analytics
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheet<T : ViewBinding> : BottomSheetDialogFragment() {
    lateinit var binding: T
        private set

    abstract fun inflateBinding(layoutInflater: LayoutInflater): T
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflateBinding(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setOnShowListener {
            dialog.findViewById<View?>(R.id.design_bottom_sheet)?.let { container ->
                container.post {
                    if (container is FrameLayout) {
                        BottomSheetBehavior.from(container).apply {
                            state = BottomSheetBehavior.STATE_EXPANDED
                        }
                    }
                }
            }
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()
    }

    fun show(fm: FragmentManager) {
        this.show(fm, this::class.java.canonicalName)
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            super.show(manager, tag)
        } catch (e: Exception) {
            manager.beginTransaction().add(this@BaseBottomSheet, tag).commitAllowingStateLoss()
        }
    }

    fun showMessage(message: String) {
        val activity = activity
        if (activity is BindingActivity<*>) {
            activity.showMessage(message)
        } else {
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        }
    }

    abstract fun updateUI()

    fun track(eventName: String) {
        Analytics.track(eventName)
    }

    fun track(eventName: String, params: HashMap<String, Any?>) {
        Analytics.track(eventName, params)
    }

    fun track(eventName: String, vararg param: Pair<String, Any?>) {
        Analytics.track(eventName, *param)
    }
}