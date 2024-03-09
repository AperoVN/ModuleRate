package com.apero.rates.feedback.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isInvisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import com.apero.rates.R
import com.apero.rates.base.BaseViewHolder
import com.apero.rates.databinding.RateItemSelectMediaBinding
import com.apero.rates.model.ItemMedia
import com.bumptech.glide.Glide

internal class MediaAdapter : ListAdapter<ItemMedia, BaseViewHolder<RateItemSelectMediaBinding>>(DIFF) {
    companion object {
        val DIFF = object : ItemCallback<ItemMedia>() {
            override fun areItemsTheSame(oldItem: ItemMedia, newItem: ItemMedia): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ItemMedia, newItem: ItemMedia): Boolean {
                return oldItem == newItem
            }
        }
    }

    private var onRemoveMediaListener: (media: ItemMedia) -> Unit = {}
    private var onAddMediaListener: () -> Unit = {}

    fun setOnRemoveMediaListener(onRemoveMediaListener: (media: ItemMedia) -> Unit) {
        this.onRemoveMediaListener = onRemoveMediaListener
    }

    fun setOnAddMediaListener(onAddMediaListener: () -> Unit) {
        this.onAddMediaListener = onAddMediaListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<RateItemSelectMediaBinding> {
        return BaseViewHolder(
            RateItemSelectMediaBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder<RateItemSelectMediaBinding>, position: Int) {
        val item = getItem(position)
        with(holder) {
            val isAddItem = item is ItemMedia.Add
            binding.imgRemoveMedia.isInvisible = isAddItem
            binding.imgRemoveMedia.isEnabled = !isAddItem
            binding.imgMedia.updateLayoutParams<FrameLayout.LayoutParams> {
                val size = if (isAddItem) {
                    itemView.context.resources.getDimension(R.dimen.space_24).toInt()
                } else {
                    ViewGroup.LayoutParams.MATCH_PARENT
                }
                width = size
                height = size
                gravity = if (isAddItem) Gravity.START or Gravity.CENTER else Gravity.CENTER
            }
            Glide.with(itemView.context)
                .run {
                    when (item) {
                        is ItemMedia.Image -> {
                            load(item.uri).centerCrop()
                        }

                        is ItemMedia.Add -> {
                            load(R.drawable.icn_rate_add_media)
                        }
                    }
                }.into(binding.imgMedia)
            binding.imgRemoveMedia.setOnClickListener {
                onRemoveMediaListener(item)
            }
            binding.cvMedia.setOnClickListener {
                when (item) {
                    is ItemMedia.Add -> {
                        onAddMediaListener()
                    }

                    else -> Unit
                }
            }
        }
    }
}