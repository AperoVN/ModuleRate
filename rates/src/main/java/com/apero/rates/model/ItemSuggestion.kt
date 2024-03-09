package com.apero.rates.model


internal data class ItemSuggestion(
    val label: UiText,
    val isSelected: Boolean = false,
    val note: String? = null,
)