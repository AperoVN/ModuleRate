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

    private var onConfirmShowRateListener: (isSatisfied: Boolean) -> Unit = {}

    fun setOnShowRateListener(onConfirmShowRateListener: (isSatisfied: Boolean) -> Unit) = apply {
        this.onConfirmShowRateListener = onConfirmShowRateListener
    }

    override fun updateUI() {
        track("bottom_sheet_rate_view")
        binding.btnSatisfied.setOnClickListener {
            track("bottom_sheet_rate_click_satisfied")
            onConfirmShowRateListener(true)
            dismiss()
        }
        binding.btnUnsatisfied.setOnClickListener {
            track("bottom_sheet_rate_click_dissatisfied")
            onConfirmShowRateListener(false)
            dismiss()
        }
        binding.ivClose.setOnClickListener {
            dismiss()
        }
    }
}