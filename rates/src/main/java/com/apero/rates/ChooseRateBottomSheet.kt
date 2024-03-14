package com.apero.rates

import android.view.LayoutInflater
import com.apero.rates.base.BaseBottomSheet
import com.apero.rates.databinding.RateBottomSheetChooseRateBinding

/**
 * Created by KO Huyn on 07/03/2024.
 */
class ChooseRateBottomSheet : BaseBottomSheet<RateBottomSheetChooseRateBinding>() {
    override fun inflateBinding(layoutInflater: LayoutInflater): RateBottomSheetChooseRateBinding {
        return RateBottomSheetChooseRateBinding.inflate(layoutInflater)
    }

    private var onConfirmShowRateListener: (isSatisfied: Boolean, dialog: ChooseRateBottomSheet) -> Unit = { _, _ -> }
    private var onCloseRateListener: () -> Unit = {}

    fun setOnShowRateListener(onConfirmShowRateListener: (isSatisfied: Boolean, dialog: ChooseRateBottomSheet) -> Unit) = apply {
        this.onConfirmShowRateListener = onConfirmShowRateListener
    }

    fun setOnCloseRateListener(onCloseRateListener: () -> Unit) = apply {
        this.onCloseRateListener = onCloseRateListener
    }

    override fun updateUI() {
        track("bottom_sheet_rate_view")
        binding.btnSatisfied.setOnClickListener {
            track("bottom_sheet_rate_click_satisfied")
            onConfirmShowRateListener(true, this)
        }
        binding.btnUnsatisfied.setOnClickListener {
            track("bottom_sheet_rate_click_dissatisfied")
            onConfirmShowRateListener(false, this)
        }
        binding.ivClose.setOnClickListener {
            onCloseRateListener()
            dismiss()
        }
    }
}