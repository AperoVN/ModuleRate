package com.apero.rates.base

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.apero.rates.analytics.Analytics

/**
 * Created by KO Huyn on 05/06/2023.
 */
internal abstract class BaseDialogBinding<V : ViewBinding> : DialogFragment() {

    open fun isFullWidth(): Boolean = true
    open fun isCancelableDialog(): Boolean {
        return true
    }

    protected lateinit var binding: V
        private set

    abstract fun getLayoutBinding(inflater: LayoutInflater): V
    abstract fun updateUI(savedInstanceState: Bundle?)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val root = RelativeLayout(activity)
        root.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        val dialog = Dialog(activity as FragmentActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(root)
        dialog.setCanceledOnTouchOutside(isCancelableDialog())
        dialog.window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            if (isFullWidth()) {
                it.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            } else {
                it.setLayout(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        }

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getLayoutBinding(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = isCancelableDialog()
        updateUI(savedInstanceState)
    }

    fun show(fm: FragmentManager) = apply {
        show(fm, this::class.java.canonicalName)
    }

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