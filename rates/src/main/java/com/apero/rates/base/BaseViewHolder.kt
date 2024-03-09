package com.apero.rates.base

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

internal open class BaseViewHolder<out T : ViewBinding>(val binding: T) :
    RecyclerView.ViewHolder(binding.root)