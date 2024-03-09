package com.apero.rates.feedback.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.apero.rates.R
import com.apero.rates.base.BaseViewHolder
import com.apero.rates.databinding.RateItemSuggestionBinding
import com.apero.rates.model.ItemSuggestion

/**
 * Created by KO Huyn on 18/10/2023.
 */
internal class SuggestionAdapter :
    RecyclerView.Adapter<BaseViewHolder<RateItemSuggestionBinding>>() {

    private var onItemClick: (item: ItemSuggestion, position: Int) -> Unit = { _, _ -> }

    var items = listOf<ItemSuggestion>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun setOnClickListener(onItemClick: (item: ItemSuggestion, position: Int) -> Unit) {
        this.onItemClick = onItemClick
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): BaseViewHolder<RateItemSuggestionBinding> {
        return BaseViewHolder(
            RateItemSuggestionBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(
        holder: BaseViewHolder<RateItemSuggestionBinding>, position: Int
    ) {
        val item = items[position]
        with(holder) {
            binding.tvSuggestion.text = item.label.getBy(holder.itemView.context)
            binding.tvSuggestion.setBackgroundResource(if (item.isSelected) R.drawable.rate_bg_selected_corner_8 else R.drawable.rate_bg_black_corner_8)
            binding.tvSuggestion.backgroundTintList =
                if (item.isSelected) null else ColorStateList.valueOf(
                    ContextCompat.getColor(
                        itemView.context, R.color.clr_rate_background
                    )
                )
            binding.tvSuggestion.setOnClickListener {
                onItemClick(items[position], position)
            }
            binding.tvSuggestion.setTextColor(
                ContextCompat.getColor(
                    itemView.context, if (item.isSelected) R.color.white else R.color.clr_text
                )
            )
        }
    }
}
