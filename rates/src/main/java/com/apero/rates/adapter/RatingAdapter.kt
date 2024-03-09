package com.apero.rates.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.apero.rates.R
import com.apero.rates.base.BaseViewHolder
import com.apero.rates.databinding.RateItemRatingBinding

/**
 * Created by KO Huyn on 18/10/2023.
 */
internal class RatingAdapter : RecyclerView.Adapter<BaseViewHolder<RateItemRatingBinding>>() {

    private var onRatingChange: (rating: Int) -> Unit = {}
    private var items = List(5) { false }
        set(value) {
            field = value
            onRatingChange(value.count { isRated -> isRated })
            notifyDataSetChanged()
        }

    private val isDefault get() = items.none { rated -> rated }
    fun setOnRatingChangeListener(onRatingChange: (rating: Int) -> Unit) {
        this.onRatingChange = onRatingChange
    }

    fun getRating(): Int {
        return items.count { isRated -> isRated }
    }

    fun setRate(rate: Int) {
        if (rate != getRating()) {
            items = List(items.size) { index -> index <= rate }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<RateItemRatingBinding> {
        return BaseViewHolder(
            RateItemRatingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<RateItemRatingBinding>, position: Int) {
        val isRated = items[position]
        with(holder) {
            binding.ivRating.setImageResource(
                if (isDefault && position == items.size - 1) {
                    R.drawable.icn_rate_highlight
                } else {
                    if (isRated) R.drawable.icn_rate_fill else R.drawable.icn_rate_unfill
                }
            )
            binding.ivRating.setOnClickListener {
                items = List(items.size) { index -> index <= position }
            }
        }
    }
}